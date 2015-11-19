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
$output.java($WebConversation, "ConversationListener")##

$output.require("javax.faces.lifecycle.Lifecycle")##
$output.require("javax.servlet.http.HttpServletRequest")##

/**
 * Interface to be implemented if you wish to listen to the {@link Lifecycle} of {@link ConversationBean conversations}.
 */
public interface $output.currentClass {

    /**
     * Called after a {@link ConversationBean} has been created but before it starts.
     * @param conversationBean the conversationBean that was created.
     */
    void conversationCreated(ConversationBean conversationBean);

    /**
     * Called to indicate the passed conversationBean is being resumed.
     */
    void conversationResuming(ConversationBean conversationBean, HttpServletRequest request);

    /**
     * Called to indicate the passed conversationBean is being paused.
     */
    void conversationPausing(ConversationBean conversationBean);

    /**
     * Called to indicate the conversation is ending.
     */
    void conversationEnding(ConversationBean conversationBean);
}