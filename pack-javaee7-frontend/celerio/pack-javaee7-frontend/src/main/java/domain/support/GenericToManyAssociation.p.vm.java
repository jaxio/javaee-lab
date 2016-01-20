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
$output.java($WebModelSupport, "GenericToManyAssociation")##

$output.require("java.io.Serializable")##
$output.require("java.util.List")##
$output.require("org.omnifaces.util.Faces")##
$output.require("org.primefaces.event.SelectEvent")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require("com.jaxio.jpa.querybyexample.GenericRepository")##
$output.require($WebPermissionSupport, "GenericPermission")##
$output.require($WebUtil, "MessageUtil")##

/**
 * Controller that allows you to manage an entity's x-to-many association.
 */
public abstract class $output.currentClass<E extends Identifiable<PK>, PK extends Serializable> {
    protected  String labelKey;
    protected  MessageUtil messageUtil;
    protected  GenericController<E, PK> controller;
    protected  GenericPermission<E> permission;
    protected  GenericRepository<E, PK> repository;    
    protected  SelectableListDataModel<E> dataModel;

    public ${output.currentClass}(List<E> elements, String labelKey, GenericController<E, PK> controller) {
        this.dataModel = new SelectableListDataModel<E>(elements);
        this.labelKey = labelKey;
        this.controller = controller;
        this.messageUtil = controller.getMessageUtil();
        this.permission = controller.getPermission();
        this.repository = controller.getRepository();
    }

    /**
     * Return the dataModel used in the datatable component. 
     */
    public SelectableListDataModel<E> getModel() {
        return dataModel;
    }

    /**
     * Set the dataModel used in the datatable component. 
     */
    public void setModel(SelectableListDataModel<E> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * Remove the passed entity from the x-to-many association.
     */
    protected abstract void remove(E e);

    /**
     * Add the passed entity to the x-to-many association.
     */
    protected void add(E e) {        
    }

    /**
     * Action to edit the entity corresponding to the selected row.
     * @return the implicit jsf view.
     */
    public String edit() {
        return controller.edit(dataModel.getSelectedRow());
    }

    /**
     * Action to view the entity corresponding to the selected row.
     * @return the implicit jsf view.
     */
    public String view() {
        return controller.view(dataModel.getSelectedRow());
    }    

    /**
     * This datatable row selection listener invokes the {@link ${pound}view()} action and force the navigation to the returned implicit view.
     * Use it from a p:ajax event="rowSelect".
     */
    public void onRowSelectView(SelectEvent event) {
        Faces.navigate(view());
    }

    /**
     * This datatable row selection listener invokes the {@link ${pound}edit()} action and force the navigation to the returned implicit view.
     * Use it from a p:ajax event="rowSelect".
     */
    public void onRowSelectEdit(SelectEvent event) {
        Faces.navigate(edit());
    }

    /**
     * Remove the entity corresponding to the selected row from the x-to-many association.
     */
    public void remove() {
        checkPermission(permission.canDelete(dataModel.getSelectedRow()));
        remove(dataModel.getSelectedRow());
        messageUtil.infoEntity("status_removed_ok", dataModel.getSelectedRow());
    }

    /**
     * Action to create a new entity. 
     * @return the implicit jsf view.
     */
    public String add() {
        String extra = extraCreateIds();
        return extra != null ? controller.create() + extra : controller.create();
    }

    /**
     * Extra ids name/value pair param used to pre-fill the entity, in general this is the owner's id.
     * Example: <code>&ownerId=1234</code>.
     * Returns null if no extra id.
     */
    protected String extraCreateIds() {
        return null;
    }

    /**
     * For many to many, we use an auto-complete to select and add and element to the collection.
     */
    public void setAddElement(E selectedInAutoComplete) {
        if (selectedInAutoComplete != null) {
            remove(selectedInAutoComplete);
            add(selectedInAutoComplete);
            messageUtil.infoEntity("status_added_existing_ok", selectedInAutoComplete);
        }
    }
     
    /**
     * Always null as we use the setter only to add element.
     */
    public E getAddElement() {
        return null;
    }

    protected void checkPermission(boolean check) {
        if (!check) {
            throw new IllegalStateException();
        }
    }
}
