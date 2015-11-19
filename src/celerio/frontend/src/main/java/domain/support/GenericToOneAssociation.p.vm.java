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
$output.require($WebConversation, "ConversationCallBack")##
$output.require($WebPermissionSupport, "GenericPermission")##
$output.require($WebUtil, "MessageUtil")##
$output.requireStatic("com.google.common.base.CaseFormat.LOWER_CAMEL")##
$output.requireStatic("com.google.common.base.CaseFormat.UPPER_CAMEL")##
$output.require("java.util.Collection")##
$output.require("javax.persistence.CascadeType")##
$output.require("javax.persistence.metamodel.SingularAttribute")##
$output.require("com.jaxio.jpa.querybyexample.MetamodelUtil")##

/**
 * Controller that allows you to manage an entity's x-to-one association.
 */
public abstract class $output.currentClass<E extends Identifiable<PK>, PK extends Serializable> {
    protected  GenericController<E, PK> controller;
    protected  GenericPermission<E> permission;
    protected  MessageUtil messageUtil;
    protected  String labelKey;
    protected  Collection<CascadeType> cascades;

    public ${output.currentClass}(GenericController<E, PK> controller, SingularAttribute<?,E> attribute) {
        this.labelKey = buildLabelKey(attribute);
        this.controller = controller;
        this.messageUtil = controller.getMessageUtil();
        this.permission = controller.getPermission();
        this.cascades = MetamodelUtil.getInstance().getCascades(attribute);
    }

    protected abstract E get();

    protected abstract void set(E e);

    public String view() {
        return controller.editReadOnlyView(labelKey, get(), isSubView());
    }

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
        if (controller.shouldReplace(get(), selected)) {
            set(selected);
        }
    }

    public String search() {
        return select();
    }

    public String select() {
        return controller.selectView(labelKey, selectCallBack, isSubView());
    }

    protected ConversationCallBack<E> selectCallBack = new ConversationCallBack<E>() {
        private static final long serialVersionUID = 1L;

        @Override
        protected void onSelected(E e) {
            if (!permission.canSelect(e)) {
                messageUtil.error("error_action_denied");
                return;
            }

            set(e);
            messageUtil.infoEntity("status_selected_ok", get());
        }
    };

    public String add() {
        return controller.createView(labelKey, addCallBack, isSubView());
    }

    protected ConversationCallBack<E> addCallBack = new ConversationCallBack<E>() {
        private static final long serialVersionUID = 1L;

        @Override
        protected void onSaved(E e) {
            if (!permission.canCreate()) {
                messageUtil.error("error_action_denied");
                return;
            }

            set(e);
        }
        
        @Override
        protected void onOk(E e) {
            if (!permission.canCreate()) {
                messageUtil.error("error_action_denied");
                return;
            }

            set(e);
            messageUtil.infoEntity("status_selected_new_ok", e);
        }
    };

    public String edit() {
        return controller.editView(labelKey, get(), editCallBack, isSubView());
    }

    protected ConversationCallBack<E> editCallBack = new ConversationCallBack<E>() {
        private static final long serialVersionUID = 1L;

        // through this way if sub is false
        @Override
        protected void onSaved(E e) {
            set(e);
        }
        
        // callback through this way if sub is true
        @Override
        protected void onOk(E e) {
            set(e);
            messageUtil.infoEntity("status_edited_ok", e);
        }
    };
    
    /**
     * @return <code>true</code> if view is related to a parent and should not be performed any persistance. <code>false</code> otherwise.
     */
    protected boolean isSubView() {
        return cascades.contains(CascadeType.PERSIST) || cascades.contains(CascadeType.ALL);
    }
    
    private String buildLabelKey(SingularAttribute<?, E> attribute) {
        return UPPER_CAMEL.to(LOWER_CAMEL, attribute.getDeclaringType().getJavaType().getSimpleName())
          + "_" + attribute.getName();
    }
}
