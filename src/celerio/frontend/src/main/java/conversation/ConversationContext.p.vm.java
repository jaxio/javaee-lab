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
$output.java($WebConversation, "ConversationContext")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.requireStatic("com.google.common.collect.Sets.newHashSet")##
$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.requireStatic("java.util.Collections.unmodifiableSet")##
$output.require($ModelSupport, "Identifiable")##
$output.require("java.io.Serializable")##
$output.require("java.util.Collection")##
$output.require("java.util.Set")##
$output.require("java.util.Map")##
$output.require("javax.enterprise.context.spi.Contextual")##
$output.require($Util, "ResourcesUtil")##

/**
 * Context holding variables and 'conversationContext' scoped beans so they can be accessed from the view.
 * Note that you can change the view of the context. This allows you to navigate from page to page
 * using the same context.
 */
public class ${output.currentClass}<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  Stores 'conversationContext' scope beans.
     */
    private Map<Contextual<?>, Object> beans = newHashMap();

    /**
     *  Stores variables such as <code>readonly</code>, <code>sub</code>, etc.
     */
    private Map<String, Object> vars = newHashMap();
    private String id; // context id
    private String conversationId;
    private ConversationCallBack<T> callBack = new ConversationCallBack<T>();
    private String labelKey = null;
    private Object[] labelKeyArgs = null;
    private String viewUri;
    private boolean isNewEntity;

    public ${output.currentClass}() {
    }

    public ${output.currentClass}(String viewUri) {
        this.viewUri = viewUri;
    }

    /**
     * Set this conversation context id.
     */
    protected void setId(String id) {
        this.id = id;
    }

    public ${output.currentRootClass}<T> id(String id) {
        setId(id);
        return ${output.getCurrentRootCast("T")}this;
    }

    public String getId() {
        return id;
    }

    protected void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public ${output.currentRootClass}<T> conversationId(String conversationId) {
        setConversationId(conversationId);
        return ${output.getCurrentRootCast("T")}this;
   }

    /**
     * Set the entity that will be used by the XxxForm.
     */
    public void setEntity(T entity) {
        setVar("_entity", entity);
    }

    public ${output.currentRootClass}<T> entity(T entity) {
        setEntity(entity);
        return ${output.getCurrentRootCast("T")}this;
    }

    /**
     * Returns the original entity that was passed to this context.
     */
    @SuppressWarnings("unchecked")
    public T getEntity() {
        return (T) vars.get("_entity");
    }

    public void setIsNewEntity(boolean isNewEntity) {
        this.isNewEntity = isNewEntity;
    }

    public ${output.currentRootClass}<T> isNewEntity(boolean isNewEntity) {
        setIsNewEntity(isNewEntity);
        return ${output.getCurrentRootCast("T")}this;
    }

    public ${output.currentRootClass}<T> newEntity() {
        return isNewEntity(true);
    }

    public boolean isNewEntity() {
        return isNewEntity;
    }

    /**
     * Use this method if you want the entity to be loaded afterward.
     */
    public void setEntityId(Serializable entityId) {
        setVar("entityId", entityId);
    }

    public ${output.currentRootClass}<T> entityId(Serializable entityId) {
        setEntityId(entityId);
        return ${output.getCurrentRootCast("T")}this;
    }

    @SuppressWarnings("unchecked")
    public <PK> PK getEntityIdAndRemove() {
        return (PK) vars.remove("entityId");
    }

    /**
     * Sets the label displayed in the conversation menu.
     * @param labelKey the resource property key.
     * @param labelKeyArgs the optional args
     */
    public void setLabelKey(String labelKey, Object... labelKeyArgs) {
        this.labelKey = labelKey;
        this.labelKeyArgs = labelKeyArgs;
    }

    public ${output.currentRootClass}<T> labelKey(String labelKey, Object... labelKeyArgs) {
        setLabelKey(labelKey, labelKeyArgs);
        return ${output.getCurrentRootCast("T")}this;
    }

    /**
     * Resolve the label at runtime as it depends on the current language.
     */
    public String getLabel() {
        return ResourcesUtil.getInstance().getPropertyWithArrayArg(labelKey, labelKeyArgs);
    }

    /**
     * Sets the viewUri attached to this context. 
     */
    public void setViewUri(String viewUri) {
        this.viewUri = viewUri;
    }

    public ${output.currentRootClass}<T> viewUri(String viewUri) {
        setViewUri(viewUri);
        return ${output.getCurrentRootCast("T")}this;
    }

    /**
     * The viewUri attached to this context.
     */
    public String getViewUri() {
        return viewUri;
    }

    /**
     * Sets the <code>sub</code> variable. This variable is used in the xhtml view to render certain menus/buttons.
     */
    public void setSub(boolean sub) {
        setVar("sub", sub);
    }

    public ${output.currentRootClass}<T> sub(boolean sub) {
        setSub(sub);
        return ${output.getCurrentRootCast("T")}this;
    }

    public ${output.currentRootClass}<T> sub() {
        return sub(true);
    }

    public boolean isSub() {
        return getVar("sub", Boolean.class) != null ? getVar("sub", Boolean.class) : false;
    }

    /**
     * Sets the <code>readonly</code> variable.
     */
    public void setReadonly(boolean readonly) {
        setVar("readonly", readonly);
    }

    public ${output.currentRootClass}<T> readonly(boolean readonly) {
        setReadonly(readonly);
        return ${output.getCurrentRootCast("T")}this;
    }

    public ${output.currentRootClass}<T> readonly() {
        return readonly(true);
    }

    public boolean isReadOnly() {
        return getVar("readonly", Boolean.class) != null ? getVar("readonly", Boolean.class) : false;
    }

    /**
     * Sets the <code>print</code> variable. It activates a printer friendly mode.
     */
    public void setPrint(boolean print) {
        setVar("print", print);
    }

    public ${output.currentRootClass}<T> print(boolean print) {
        setPrint(print);
        return ${output.getCurrentRootCast("T")}this;
    }

    public ${output.currentRootClass}<T> print() {
        return print(true);
    }

    public boolean isPrint() {
        return getVar("print", Boolean.class) != null ? getVar("print", Boolean.class) : false;
    }
    
    /**
     * Sets the <code>dependentEntities</code> variables. It keeps track of entities to be saved/merged just as the primary <code>entity</code>.
     */
    public ${output.currentRootClass}<T> dependentEntity(Identifiable<? extends Serializable>... dependents) {
        addDependentEntities(newArrayList(dependents));
        return ${output.getCurrentRootCast("T")}this;
    }
    
    public void addDependentEntity(Identifiable<? extends Serializable> dependent) {
        Set<Identifiable<? extends Serializable>> dependents = getDependents();
        dependents.add(dependent);
        
        setVar("dependent", dependents);
    }
    
    public void addDependentEntities(Collection<? extends Identifiable<? extends Serializable>> dependentsCollection) {
        Set<Identifiable<? extends Serializable>> dependents = getDependents();
        dependents.addAll(dependentsCollection);
        
        setVar("dependent", dependents);
    }
    
    public Set<? extends Identifiable<? extends Serializable>> getDependentEntities(){
        Set<? extends Identifiable<? extends Serializable>> dependents = getDependents();
        return unmodifiableSet(dependents);
    }
    
    public void clearDependentEntities(){
        setVar("dependent", newHashSet());
    }

    /**
     * @return dependents entities set if exists, empty set otherwise.
     */
    @SuppressWarnings("unchecked")
    private Set<Identifiable<? extends Serializable>> getDependents() {
        Set<Identifiable<? extends Serializable>> dependents = (Set<Identifiable<? extends Serializable>>) getVar("dependent", Set.class);
        if( dependents == null){
            dependents = newHashSet();
        }
        return dependents;
    }
    
    /**
     * The callback to use just after this context is flagged for pop from the conversation's context stack. 
     */
    public void setCallBack(ConversationCallBack<T> callBack) {
        this.callBack = callBack;
    }
    
    public ${output.currentRootClass}<T> callBack(ConversationCallBack<T> callBack) {
        setCallBack(callBack);
        return ${output.getCurrentRootCast("T")}this;
    }

    public ConversationCallBack<T> getCallBack() {
        return callBack;
    }

    /**
     * Returns the URL associated to this context. It is used for direct access, from the conversation menu or the filter.
     */
    public String getUrl() {
        checkViewUriAndConversationId();
        return viewUri + "?cid=" + conversationId + "&ccid=" + id;
    }

    /**
     * Return the view to display for this context. It is used by actions when returning a view.
     */
    public String view() {
        checkViewUriAndConversationId();
        return viewUri + "?faces-redirect=true&ccid=" + id;
    }

    private void checkViewUriAndConversationId() {
        if (viewUri == null) {
            throw new IllegalStateException("Developer! viewUri is null, it must be set before calling view() or getUrl() methods");
        }

        if (conversationId == null) {
            throw new IllegalStateException("Developer! conversationId is null, it must be set before calling view() or getUrl() methods");
        }
    }

    // ----------------------------------
    // Support for conversation scope
    // ----------------------------------

    /**
     * Store a ConversationContextScoped bean.
     */
    public <E> void addBean(Contextual<E> contextual, E bean) {
        beans.put(contextual, bean);
    }

    @SuppressWarnings("unchecked")
    public <E> E getBean(Contextual<E> contextual) {
        return (E) beans.get(contextual);
    }

    public void setVar(String name, Object var) {
        vars.put(name, var);
    }

    public Object getVar(String name) {
        return vars.get(name);
    }

    @SuppressWarnings("unchecked")
    public <E> E getVar(String name, Class<E> expectedType) {
        return (E) vars.get(name);
    }
}