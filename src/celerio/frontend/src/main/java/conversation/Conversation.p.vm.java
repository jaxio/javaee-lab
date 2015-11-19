## Copyright 2015 JAXIO http://www.jaxio.com
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##
$output.java($WebConversation, "ConversationBean")##
## keep the filename as Conversation => easier for synch between packs
$output.requireStatic("com.google.common.collect.Sets.newHashSet")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require("java.io.Serializable")##
$output.require("java.util.logging.Logger")
$output.require("java.util.Set")##
$output.require("java.util.Stack")##
$output.require("javax.enterprise.context.Conversation")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##

/**
 * A conversation has a smaller scope than an http session. It has well defined entry and exits boundaries.
 * Within a conversation the user can perform actions and navigate from page to page.
 * 
 * <p>
 * Each page is bound to a {@link ConversationContext} which is stored in a stack maintained by the conversation. 
 * All {@link ConversationContextScoped} beans are stored in the page's {@link ConversationContext}.
 * 
 * <p>
 * There are 4 types of navigation inside a conversation:
 * <ol>
 * <li><b>forward navigation</b> using a new {@link ConversationContext}. In that case, a new {@link ConversationContext} is created and pushed on top of the
 * Conversation's {@link ConversationContext} stack.</li>
 * 
 * <li><b>backward navigation</b>. It consists in popping the current {@link ConversationContext} from the stack and returning the viewUri of the
 * {@link ConversationContext} promoted on top of the stack.</li>
 * 
 * <li><b>navigation in the same context</b>. You just replace the {@link ConversationContext}'s viewUri. It can be useful if you want to have access to the
 * beans/vars from the previous page. But beware, we do not keep track of viewUri changes inside a {@link ConversationContext}.</li>
 * 
 * <li><b>navigation outside of the conversation</b>. You simply return a URL that does not carry the conversation _cid parameter.</li>
 * </ol>
 *
 * <p>
 * The main strength of this API is the possibility to pass information back, using {@link ConversationCallBack} to the previous page when we do backward
 * navigation. It allows page/navigation/action reusability as the current page does not need to know which page led to it.
 * 
 * <p>
 * The most important methods are:
 * <ol>
 * <li>{@link ${pound}nextView()} which returns the implicit JSF view to display (the conversation's page). Use it in your JSF action returning implicit view.</li>
 * <li>{@link ${pound}setNextContext()} to push (push is not done immediately for technical reason) a new {@link ConversationContext}.</li>
 * <li>{@link ${pound}incrementPopContextOnNextPauseCounter()} if you want to pop a {@link ConversationContext} from the stack.</li>
 * </ol>
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ConversationScoped","javax.inject.Named")##
public class $output.currentClass implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String CONVERSATION_COUNTER_KEY = "convCounter";

    @Inject
    private transient Logger log;
    @Inject
    private Conversation rawConversation;

    /**
     * Each time a {@link ConversationContext} is created we assign it an id. 
     */
    private int conversationContextIdCounter = 0;
    private Stack<ConversationContext<?>> contextes = new Stack<ConversationContext<?>>();
    private int popContextOnNextPauseCounter = 0;
    private ConversationContext<?> nextContext;

    /**
     * Returns the underlying raw {@link Conversation}.
     */
    protected Conversation getRawConversation() {
        return rawConversation;
    }

    /**
     * Returns the raw conversation id (i.e. cid).
     */
    public String getId() {
        return rawConversation.getId();
    }

    public boolean isTransient() {
        return rawConversation.isTransient();
    }

    /**
     * Here to handle browser's back button
     * @param ccid
     * @param request
     */
    protected void handleOutOfSynchContext(String ccid) throws UnexpectedConversationException {
        ConversationContext<?> requestedContext = null;

        for (ConversationContext<?> ctx : contextes) {
            if (requestedContext == null) {
                if (ctx.getId().equals(ccid)) {
                    requestedContext = ctx;
                }
            } else {
                // we pop all contextes that are placed after the contexId requested
                incrementPopContextOnNextPauseCounter();
            }
        }

        if (requestedContext != null) {
            popContextesIfNeeded();
        } else {
            // FIXME - always in a faces context ?
            String requestURI = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
            throw new UnexpectedConversationException("Uri not in sync with conversation expecting _ccid_=" + getCurrentContext().getId() + " but got " + ccid,
                    requestURI, getUrl());
        }
    }

    /**
     * Increment the number of conversation context that must be popped from the stack by the conversation manager 
     * when it pauses the current conversation.
     * <p>
     * <i>Why don't we pop directly the context here?</i>
     * <p>
     * When an action is triggered from within a dataTable, the JSF runtime executes some EL after our action. 
     * By doing so it requests a conversation scoped bean that belongs to the current context. 
     * If this context is popped too soon, the bean is recreated!
     */
    public void incrementPopContextOnNextPauseCounter() {
        popContextOnNextPauseCounter++;
    }

    /**
     * The number of conversation contextes that must be popped on next conversation pause.
     */
    public int getPopContextOnNextPauseCounter() {
        return popContextOnNextPauseCounter;
    }

    /**
     * Set the next context. Note that this context is not pushed on the stack immediately.
     * Therefore you cannot set multiple contextes in the hope that they will stacked up.
     */
    public void setNextContext(ConversationContext<?> newContext) {
        newContext.setId(String.valueOf(++conversationContextIdCounter));
        newContext.setConversationId(getId());
        // we delay the context push because apparently some EL is invoked after bean action is performed
        // which it leads in some cases to re-creation of 'conversation scoped' bean.
        nextContext = newContext; // will be pushed at next request during resuming...
    }

    public ConversationContext<?> nextContext(ConversationContext<?> newContext) {
        setNextContext(newContext);
        return newContext;
    }

    protected void pushNextContextIfNeeded() {
        if (nextContext != null) {
            contextes.push(nextContext);
            log.fine("pushed 1 context on stack: " + nextContext.getLabel());
            nextContext = null;
        }
    }

    /**
     * Set the <code>sub</code> var to true in the passed context and invoke {@link ${pound}setNextContext(ConversationContext)}
     * @param newContext
     * @see ${pound}setNextContext(ConversationContext)
     */
    public void setNextContextSub(ConversationContext<?> newContext) {
        setNextContext(newContext.sub());
    }

    /**
     * Set the <code>sub</code> var to true and the <code>readonly</code> var to true in the passed context and invoke {@link ${pound}setNextContext(ConversationContext)}.
     * @param newContext
     * @see ${pound}setNextContext(ConversationContext)
     */
    public void setNextContextSubReadOnly(ConversationContext<?> newContext) {
        setNextContext(newContext.sub().readonly());
    }

    /**
     * Set the <code>readonly</code> var to true in the passed context and invoke {@link ${pound}setNextContext(ConversationContext)}.
     * @param newContext
     * @see ${pound}setNextContext(ConversationContext)
     */
    public void setNextContextReadOnly(ConversationContext<?> newContext) {
        setNextContext(newContext.readonly());
    }

    public int getConversationContextesCount() {
        return contextes.size();
    }

    /**
     * Returns the context on top on stack.
     * BEWARE, the context could have been scheduled for popping.
     */
    @SuppressWarnings("unchecked")
    public <T extends ConversationContext<?>> T getCurrentContext() {
        return (T) contextes.peek();
    }

    public Stack<ConversationContext<?>> getConversationContextes() {
        return contextes;
    }

    protected void popContextesIfNeeded() {
        if (popContextOnNextPauseCounter > 1) {
            log.fine("There are " + popContextOnNextPauseCounter + " to pop from the stack");
        }

        Set<Identifiable<? extends Serializable>> dependentEntities = newHashSet();
        for (int i = 0; i < popContextOnNextPauseCounter; i++) {
            if (!contextes.isEmpty()) {
                ConversationContext<?> ccPopped = contextes.pop();
                dependentEntities.addAll(ccPopped.getDependentEntities());
                log.fine("popped 1 context from stack: " + ccPopped.getLabel());
            } else {
                log.warning("Attention, too many pop requested! Could be source of potential bug");
            }
        }

        // adds all popped entities to current context
        getCurrentContext().addDependentEntities(dependentEntities);

        popContextOnNextPauseCounter = 0;

        if (contextes.isEmpty()) {
            log.info("All contextes have been popped. Natural conversation ending will be performed");
        }
    }

    /**
     * Returns the view url for the next page. Used by action when returning the view.
     */
    public String nextView() {
        ConversationContext<?> context = nextContext();

        if (context != null) {
            return context.view();
        } else {
            // FIXME TODO: improve this => determine in phase listener if conversation is about to end... 
            // 
            // Currently, we cannot just return "/home.faces?faces-redirect=true as we currently end the conversation
            // after the render phase. As a result the cid would be propagated and we would end up with a
            // NonexistentConversationException: WELD-000321 No conversation found to restore for id x...
            ConversationUtil.redirect("/home.faces");
            return null; // not really used.. 
        }
    }

    /**
     * Returns the url for the next page. Used by filter.
     */
    public String nextUrl() {
        ConversationContext<?> context = nextContext();
        return context != null ? context.getUrl() : "/home.faces";
    }

    @SuppressWarnings("unchecked")
    public <T extends ConversationContext<?>> T nextContext() {
        if (nextContext != null) {
            return (T) nextContext;
        }

        if (popContextOnNextPauseCounter > 0) {
            int nextActiveContextIndex = contextes.size() - 1 - popContextOnNextPauseCounter;
            if (nextActiveContextIndex >= 0) {
                ConversationContext<?> contextOnTopOfStackOnNextResume = contextes.elementAt(nextActiveContextIndex);
                return (T) contextOnTopOfStackOnNextResume;
            } else {
                return null;
            }
        }

        return (T) contextes.peek();
    }

    
    // ------------------------------------------
    // Methods below use the last pushed context
    // ------------------------------------------

    /**
     * @return the label of the last pushed context. 
     */
    public String getLabel() {
        return contextes.peek().getLabel();
    }

    /**
     * @return the menu url of the last pushed context. 
     */
    public String getUrl() {
        return contextes.peek().getUrl();
    }

    /**
     * @return the uri of the last pushed context. 
     */
    public String getViewUri() {
        return contextes.peek().getViewUri();
    }
}
