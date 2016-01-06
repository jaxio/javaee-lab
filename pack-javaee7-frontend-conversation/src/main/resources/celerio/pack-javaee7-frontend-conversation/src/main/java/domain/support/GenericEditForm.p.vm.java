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
$output.java($WebModelSupport, "GenericEditForm")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require("java.io.Serializable")##
$output.require("java.util.List")##
$output.require("java.util.Set")##
$output.require("javax.inject.Inject")##
$output.require("javax.persistence.OptimisticLockException")##
$output.require("org.apache.commons.lang.WordUtils")##
$output.require("com.jaxio.jpa.querybyexample.JpaUniqueUtil")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($PrinterSupport, "TypeAwarePrinter")##
$output.require("com.jaxio.jpa.querybyexample.EntityGraphLoader")##
$output.require("com.jaxio.jpa.querybyexample.GenericRepository")##
$output.require($RepositorySupport, "RepositoryLocator")##
$output.require($Util, "ResourcesUtil")##
$output.require($WebUtil, "MessageUtil")##
$output.require($WebUtil, "TabBean")##
$output.require($WebConversation, "ConversationCallBack")##
$output.require($WebConversation, "ConversationContext")##

/**
 * Base Edit Form for JPA entities.
 */
public abstract class ${output.currentClass}<E extends Identifiable<PK>, PK extends Serializable> extends CommonAction<E> {
    private static final long serialVersionUID = 1L;
    private E entity;

    @Inject
    protected transient JpaUniqueUtil jpaUniqueUtil;
    @Inject
    protected transient MessageUtil messageUtil;
    @Inject
    protected transient TypeAwarePrinter printer;
    @Inject
    protected transient ResourcesUtil resourcesUtil;
    @Inject
    protected transient RepositoryLocator repositoryLocator;

    protected transient GenericRepository<E, PK> repository;
    protected transient EntityGraphLoader<E, PK> entityGraphLoader;

    public ${output.currentClass}() {
        // mandatory no-args constructor to make this bean proxyable
    }

    public ${output.currentClass}(GenericRepository<E, PK> repository) {
        this.repository = repository;
    }

    public ${output.currentClass}(GenericRepository<E, PK> repository, EntityGraphLoader<E, PK> entityGraphLoader) {
        this.repository = repository;
        this.entityGraphLoader = entityGraphLoader;
    }

    /**
     * Retrieves the entity var from the current ConversationContext and
     * depending on the case merge the entity and load its graph. 
     */
    $output.dynamicAnnotation("javax.annotation.PostConstruct")    
    public void init() {
        if (context().getEntity() == null) {
            throw new IllegalStateException("Could not find any entity. Please fix me");
        }

        if (context().isNewEntity()) {
            // no need to merge anything since it is a brand new entity, not yet persisted.
            entity = context().getEntity();
        } else if (context().isSub()) {
            // entity is persistent and we are in sub mode (not the root edit page of the graph)
            if (entityGraphLoader != null) {
                try {
                    // we load the associations to avoid lazylily access exception.
                    // note: this is a readonly merge (nothing is flushed)
                    entity = entityGraphLoader.merge(context().getEntity());
                } catch (OptimisticLockException e){
                    getCurrentConversation().setNextContext(newConcurrentModificationContext());
                    throw e; // Please see ExceptionInRenderPhaseListener
                }
            } else {
                // we can use the entity as is, it does not have any association
                // or they are eagerly loaded.
                // TODO: probably a repository.readonlyMerge(context().getEntity()); to be consistent
                // ==> AGAIN this demonstration IMO that we do not need a separated service...                    
                entity = context().getEntity();
            }
        } else {
            // entity is persistent and we are in the root edit page.
            if (entityGraphLoader != null) {
                entity = entityGraphLoader.getBy${identifiableProperty.varUp}(context().getEntity().${identifiableProperty.getter}());
            } else {
                // we can use the entity as is, it does not have any association
                // or they are eargly loaded.
                // TODO: probably a repository.readonlyMerge(context().getEntity()); to be consistent
                // ==> AGAIN this demonstration IMO that we do not need a separated service...                  
                entity = context().getEntity();
            }
        }

        if (entity == null) {
            throw new IllegalStateException("Could not find any entity, after init! Was it deleted?");
        }
    }

    protected ConversationCallBack<E> onOptimisticLockCallBack = new ConversationCallBack<E>() {
        private static final long serialVersionUID = 1L;
        @Override
        protected void onOk(E entity) {
            ConversationContext<E> context = getCurrentConversation().nextContext();
            Identifiable<PK> previousEntity = context.getEntity();
            E refreshedEntity = repository.getBy${identifiableProperty.varUp}(previousEntity.getId());
            context.setEntity(refreshedEntity);
            init();
        }
    };
    
    protected ConversationContext<E> newConcurrentModificationContext() {
        ConversationContext<E> ctx = new ConversationContext<E>();
        ctx.setEntity(context().getEntity());
        ctx.setViewUri("/concurrentModificationResolution.faces");
        ctx.setCallBack(onOptimisticLockCallBack);
        return ctx;
    }

    /**
     * Return the entity that this edit form backs.
     */
    public E getEntity() {
        return entity;
    }
    
    /**
     * Return the TabBean used by this form or <code>null</code> if this form
     * does not use any tab.
     */
    public TabBean getTabBean() {
        return null; // leave it null by default as the view checks for null.
    }
    

    // ------------------------------------
    // Actions
    // ------------------------------------
    
    /**
     * Ok action is used from sub page (non-readonly) to send the data without saving it. 
     * It is expected to be ajax since we want to display the errors, if any,
     * without refreshing the page.
     */
    public String ok() {
        return context().getCallBack().ok(getEntity());
    }

    /**
     * Close the current edit page, without any dirty data checking.
     */
    public String forceClose() {
        getCurrentConversation().getCurrentContext().clearDependentEntities();
        return context().getCallBack().notSaved(getEntity());
    }

    /**
     * deleteAndClose action is used form modal dialogs in the main edit page.
     */
    public String deleteAndClose() {
        repository.delete(getEntity());
        messageUtil.infoEntity("status_deleted_ok", getEntity());
        return context().getCallBack().deleted(getEntity());
    }

    /**
     * Save action. Used from main edit page. Expected to be an ajax request.
     */
    public String saveAndClose() {
        try {
            if (saveAndCloseInternal(getEntity())) {
                return context().getCallBack().saved(getEntity());
            }

            return null; // stay on the same page, errors will be displayed.
        } catch (OptimisticLockException e){
            getCurrentConversation().nextContext(newConcurrentModificationContext());
            return getCurrentConversation().nextView();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected boolean saveAndCloseInternal(E entity) {
        if (!validate(entity)) {
            return false;
        }

        this.entity = saveEntity(entity);
        Set<? extends Identifiable<? extends Serializable>> dependentEntities = getCurrentConversation().getCurrentContext().getDependentEntities();
        for(Identifiable<? extends Serializable> dependentEntity : dependentEntities){
            repositoryLocator.getRepository(dependentEntity).merge((Identifiable)dependentEntity);
        }

        if (context().isNewEntity()) {
            // if for some reason, save is invoked again, no need to persist anymore.
            context().setIsNewEntity(false);
        }

        messageUtil.infoEntity("status_saved_ok", this.entity);
        return true;
    }

    /**
     * Note: merge work also on new entity (actually it works better with many to many association)
     * we replace current entity with the merged one so the callback receive the merged one.
     */
    protected E saveEntity(E entity) {
        return repository.merge(entity);
    }

    public boolean validate(E entity) {
        List<String> errors = jpaUniqueUtil.validateUniques(entity);
        for(String error : errors) {
            messageUtil.error(error);
        }
        return errors.isEmpty();
    }

    protected String getLabelName() {
        return WordUtils.uncapitalize(repository.getType().getSimpleName());
    }

    public String getEditKey() {
        return getLabelName() + "_edit";
    }

    public String getViewKey() {
        return getLabelName() + "_view";
    }

    public String getCreateKey() {
        return getLabelName() + "_create";
    }

    public String getTitle() {
        String key = getEditKey();
        if(!getEntity().isIdSet()) {
            key = getCreateKey();
        } else if (getCurrentConversation().getCurrentContext().isReadOnly()) {
            key = getViewKey();
        }
        return resourcesUtil.getProperty(key, printer.print(entity));
    }
}