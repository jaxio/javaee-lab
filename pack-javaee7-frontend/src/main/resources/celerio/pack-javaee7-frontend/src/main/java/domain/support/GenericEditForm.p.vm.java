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

$output.require("java.io.Serializable")##
$output.require("java.util.List")##

$output.require("javax.annotation.PostConstruct")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##

$output.require("org.apache.commons.lang.WordUtils")##
$output.require("org.omnifaces.util.Faces")##
$output.require("com.jaxio.jpa.querybyexample.JpaUniqueUtil")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($PrinterSupport, "TypeAwarePrinter")##
$output.require("com.jaxio.jpa.querybyexample.EntityGraphLoader")##
$output.require("com.jaxio.jpa.querybyexample.GenericRepository")##
$output.require($RepositorySupport, "RepositoryLocator")##
$output.require($Util, "ResourcesUtil")##
$output.require($WebUtil, "MessageUtil")##
$output.require($WebUtil, "TabBean")##

/**
 * Base Edit Form for JPA entities.
 */
public abstract class ${output.currentClass}<E extends Identifiable<PK>, PK extends Serializable> extends CommonAction<E> {
    private static final long serialVersionUID = 1L;
    private E entity;
    private boolean persistent;

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
    
    protected EntityGraphLoader<E, PK> getEntityGraphLoader() {
        return entityGraphLoader;
    }
    
    @PostConstruct
    public void init() {
        PK id = null;
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

        if (idParam != null)  {
            id = repository.convertToPrimaryKey(idParam);
        }
        
        if (id == null) {
            // no need to merge anything since it is a brand new entity, not yet persisted.
            entity = repository.getNew();
            initNewEntity(entity);
        } else if (entityGraphLoader != null) {
            entity = entityGraphLoader.getBy${identifiableProperty.varUp}(id);
            persistent = entity != null;                
        } else {
            entity = repository.getBy${identifiableProperty.varUp}(id);
            persistent = entity != null;
        }

        onInit();
    }
    
    /**
     * When init was called, was the entity persistent?
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Override this method to pre-filled the passed new entity, for example to set the owner side in case the passed entity
     * destiny is to be part of an one-to-many association.
     */
    protected void initNewEntity(E entity) {        
    }

    /**
     * Override this method to complete the current entity initialization.
     */
    protected void onInit() {        
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
     * Close the current edit page, without saving anything.
     */
    public String forceClose() {
        return quit();        
    }

    /**
     * deleteAndClose action is used form modal dialogs in the main edit page. 
     */
    public String deleteAndClose() {
        repository.delete(getEntity());
        messageUtil.infoEntity("status_deleted_ok", getEntity());
        return quit();
    }

    /**
     * Save action. Used from main edit page. Expected to be an ajax request.
     */
    public String saveAndClose() {
        if (!validate(entity)) {
            return null;
        }

        // Note: merge work also on new entity (actually it works better with many to many association)
        entity = repository.merge(entity);
        messageUtil.infoEntity("status_saved_ok", entity);
        return quit();
    }

    /**
     * Save action. Used from main edit page. Expected to be an ajax request.
     */
    public void save() {
        if (!validate(entity)) {
            return;
        }

        // Note: merge work also on new entity (actually it works better with many to many association)
        entity = repository.merge(entity);
        onInit(); // init data models...
        messageUtil.infoEntity("status_saved_ok", entity);
        if (!isPersistent()) {
            // force redirect in order to have the id in the URL
            Faces.navigate(FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true&id=" + entity.${identifiableProperty.getter}().toString());                
        }
    }

    public boolean validate(E entity) {
        List<String> errors = jpaUniqueUtil.validateUniques(entity);
        for(String error : errors) {
            messageUtil.error(error);
        }
        return errors.isEmpty();
    }

    protected void checkPermission(boolean check) {
        if (!check) {
            throw new IllegalStateException();
        }
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
        String key = isPersistent() ? getEditKey() : getCreateKey();
        return resourcesUtil.getProperty(key, printer.print(entity));
    }    
}