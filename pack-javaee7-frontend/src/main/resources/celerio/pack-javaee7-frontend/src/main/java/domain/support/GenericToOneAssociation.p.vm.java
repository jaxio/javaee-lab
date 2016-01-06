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
$output.java($WebModelSupport, "GenericToOneAssociation")##

$output.require("java.io.Serializable")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($WebPermissionSupport, "GenericPermission")##
$output.require($WebUtil, "MessageUtil")##

/**
 * Controller that allows you to manage an entity's x-to-one association.
 */
public abstract class $output.currentClass<E extends Identifiable<PK>, PK extends Serializable> {
    protected  GenericController<E, PK> controller;
    protected  GenericPermission<E> permission;
    protected  MessageUtil messageUtil;
    protected  String labelKey;

    public ${output.currentClass}(String labelKey, GenericController<E, PK> controller) {
        this.labelKey = labelKey;
        this.controller = controller;
        this.messageUtil = controller.getMessageUtil();
        this.permission = controller.getPermission();
    }

    protected abstract E get();

    protected abstract void set(E e);

    /**
     * Helper for the autoComplete component.
     */
    public E getSelected() {
        return get();
    }

    /**
     * Handles ajax autoComplete event and regular page postback.
     */
    public void setSelected(E selected) {
        set(selected);
    }

    public String add() {
        return controller.create();
    }

    public String edit() {
        return controller.edit(get());
    }

    public String view() {
        return controller.view(get());
    }
}