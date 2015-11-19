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
$output.java($WebService, "ConcurrentModificationResolver")##

$output.require("javax.inject.Inject")##
$output.require($WebConversation, "ConversationManager")##

/**
 * Simple service used from the concurrentModificationResolution.xhtml view.
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass {

    @Inject
    private ConversationManager conversationManager;

    public String refresh() {
        return conversationManager.getCurrentConversation().getCurrentContext().getCallBack().ok(null);
    }

    public String discard() {
        conversationManager.endCurrentConversation();
        return "/home.faces?faces-redirect=true";
    }
}