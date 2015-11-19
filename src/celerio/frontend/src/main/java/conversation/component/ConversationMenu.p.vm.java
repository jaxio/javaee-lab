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
$output.java($WebConversationComponent, "ConversationMenu")##

$output.require("java.io.Serializable")##
$output.require("java.util.Map")##
$output.require("javax.enterprise.context.Conversation")##
$output.require("javax.inject.Inject")##
$output.require("org.primefaces.component.menuitem.MenuItem")##
$output.require("org.primefaces.model.DefaultMenuModel")##
$output.require("org.primefaces.model.MenuModel")##
$output.require($WebConversation, "ConversationManager")##
$output.require($WebConversation, "ConversationBean")##

$output.dynamicAnnotationTakeOver("javax.enterprise.context.SessionScoped","javax.inject.Named")##
public class ConversationMenu implements Serializable {
$serialVersionUID    

    @Inject
    private ConversationManager conversationManager;

    public boolean getRender() {
        return !conversationManager.navigationInfoMaps().isEmpty();
    }

    public MenuModel getModel() {
        MenuModel model = new DefaultMenuModel();
        Conversation currentRawConversation = conversationManager.getCurrentRawConversation();
        ConversationBean conversation = conversationManager.getCurrentConversation();

        for (Map<String, String> navigationInfo : conversationManager.navigationInfoMaps().values()) {
            MenuItem htmlMenuItem = new MenuItem();
            htmlMenuItem.setValue(navigationInfo.get("LABEL"));
            htmlMenuItem.setUrl(navigationInfo.get("URL"));

            // TODO: do not use conversationManager.getCurrentRawConversation();
            if (currentRawConversation != null && !currentRawConversation.isTransient() && conversation.getId().equals(navigationInfo.get("ID"))) {
                htmlMenuItem.setDisabled(true);
            }

            model.addMenuItem(htmlMenuItem);
        }

        return model;
    }
}