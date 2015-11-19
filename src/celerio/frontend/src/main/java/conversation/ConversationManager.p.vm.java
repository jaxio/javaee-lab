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
$output.java($WebConversation, "ConversationManager")##

$output.requireStatic("com.google.common.collect.Iterables.addAll")##
$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.requireStatic("com.google.common.collect.Sets.newHashSet")##
$output.require("java.io.Serializable")##
$output.require("java.util.Collection")##
$output.require("java.util.logging.Logger")
$output.require("java.util.Map")##
$output.require("javax.enterprise.context.Conversation")##
$output.require("javax.enterprise.inject.Instance")##
$output.require("javax.inject.Inject")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require("org.omnifaces.util.Faces")##

/**
 * The conversation manager is responsible for creating conversations, managing their lifecycle and calling the conversation listeners.
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.SessionScoped","javax.inject.Named")##
public class $output.currentClass implements Serializable {
$serialVersionUID    
    protected static final String INFO_ID = "ID";
    protected static final String INFO_LABEL = "LABEL";
    protected static final String INFO_URL = "URL";
    
    @Inject
    private transient Logger log;
    @Inject
    private ConversationBean conversation;
    private Collection<ConversationListener> conversationListeners = newHashSet();
    private int maxConversations = 5;
    private Map<String,Map<String, String>> navigationInfoMaps = newHashMap();

    /**
     * The maximum number of conversations a given user can open simultaneously.
     */
    public int getMaxConversations() {
        return maxConversations;
    }
    
    /**
     * Return the number of conversations for the current user 
     */
    public int getNbConversations() {
        return navigationInfoMaps().size();
    }

    /**
     * Whether the max number of conversations per user is reached. Used in createConversation (which has no FacesContext yet).
     */
    public boolean isMaxConversationsReached() {
        return navigationInfoMaps.keySet().size() >= maxConversations;
    }

    /**
     * Returns the current conversation. Note that this method is mainly here so it can be used from the view.
     * Use directly ConversationHolder.getCurrentRawConversation() from Java code.
     */
    public Conversation getCurrentRawConversation() {
        return ConversationHolder.getCurrentRawConversation();
    }

    public ConversationBean getCurrentConversation() {
        return ConversationHolder.getCurrentConversation();
    }

    public Map<String,Map<String, String>> navigationInfoMaps() {
        return navigationInfoMaps;
    }
    
    // --------------------------------------
    // Manage conversation lifecycle
    // --------------------------------------

    public ConversationBean beginConversation(ConversationContext<?> ctx) {
        handleMaxConversationsReached();
        conversation.getRawConversation().begin();
        conversationCreated(conversation);
        conversation.setNextContext(ctx);
        conversation.pushNextContextIfNeeded();
        
        Map<String, String> info = newHashMap();
        navigationInfoMaps.put(conversation.getId(), info);
        info.put(INFO_ID, conversation.getId());
        info.put(INFO_LABEL, conversation.getLabel());
        info.put(INFO_URL, conversation.getUrl());

        return conversation;
    }

    private void handleMaxConversationsReached() {
        if (isMaxConversationsReached()) {
            // FIFO conversation eviction
            String keyToEvict = navigationInfoMaps.keySet().iterator().next();
            log.info("Max number of conversations (" + maxConversations + ") reached. Evicting conversation " + keyToEvict + " using fifo policy");
            
            // TODO FIXME - remove oldest conversation
        }
    }

    /**
     * Resume the {@link Conversation} having the passed id. Before resuming it, if a pending ConversationContext is present, 
     * it is pushed on the conversation contextes stack.
     * @param id the id of the conversation to resume 
     * @param ccid the id of the conversation context that should be on top of the stack. 
     * @param request
     * @throws UnexpectedConversationException
     */
    public void resumeConversation(String ccid) throws UnexpectedConversationException {
        if (conversation != null) {
            conversation.pushNextContextIfNeeded();

            // compare the context id
            if (ccid != null && !conversation.getCurrentContext().getId().equals(ccid)) {
                conversation.handleOutOfSynchContext(ccid);
            }

            conversationResuming(conversation);
            ConversationHolder.setCurrentRawConversation(conversation.getRawConversation());
            ConversationHolder.setCurrentConversation(conversation);
        }
    }

    /**
     * Pause the current conversation. Before pausing it, pops the current context as needed.
     * In case all contextes are popped, then conversation is ended.
     */
    public void pauseCurrentConversation() {

        // we check for not null because the conversation could have 
        // been ended during the current request.
        if (conversation != null && !conversation.getRawConversation().isTransient()) {
            // call order of 2 methods below is important as we want all the contextes (even the one we are about to be popped)
            // to be visible from the conversation listener.
            conversationPausing(conversation);
            conversation.popContextesIfNeeded();

            if (conversation.getConversationContextesCount() == 0) {
                // all was popped, we consider that this is the natural end of the conversation.
                endCurrentConversation();
            } else {
                // update label
                Map<String, String> info = navigationInfoMaps.get(conversation.getId());
                info.put(INFO_LABEL,  conversation.getLabel());
                info.put(INFO_URL,  conversation.getUrl());
            }
        }

        ConversationHolder.setCurrentRawConversation(null);
        ConversationHolder.setCurrentConversation(null);
    }

    /**
     * End the current Conversation. Requires a FacesContext to be present.
     */
    public void endCurrentConversation() {
        String id = conversation.getRawConversation().getId();
        log.info("Ending conversation "+ id);
        conversationEnding(conversation);
        conversation.getRawConversation().end();
        
        HttpServletResponse response = ((HttpServletResponse)Faces.getExternalContext().getResponse());
        System.out.println(response);

        navigationInfoMaps.remove(id);
    }

    // --------------------------------------------
    // Support for conversation listeners
    // --------------------------------------------    

    private Collection<ConversationListener> getConversationListeners() {
        return conversationListeners;
    }
    
   public void getConversationListeners(Instance<ConversationListener> listeners){
       addAll(conversationListeners, listeners);
   }

    private void conversationCreated(ConversationBean conversation) {
        for (ConversationListener cl : getConversationListeners()) {
            cl.conversationCreated(conversation);
        }
    }

    private void conversationPausing(ConversationBean conversation) {
        for (ConversationListener cl : getConversationListeners()) {
            cl.conversationPausing(conversation);
        }
    }

    private void conversationResuming(ConversationBean conversation) {
        for (ConversationListener cl : getConversationListeners()) {
            // FIXME - do we really need request ? always in faces context
            cl.conversationResuming(conversation, null);
        }
    }

    private void conversationEnding(ConversationBean conversation) {
        for (ConversationListener cl : getConversationListeners()) {
            cl.conversationEnding(conversation);
        }
    }
}