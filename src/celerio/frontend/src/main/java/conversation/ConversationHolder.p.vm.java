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
$output.java($WebConversation, "ConversationHolder")##

$output.require("javax.enterprise.context.Conversation")##

/**
 * Holds the current {@link Conversation} in the current thread of execution.
 */
public class $output.currentClass {
    private static final ThreadLocal<Conversation> currentConversationHolder = new ThreadLocal<Conversation>();
    private static final ThreadLocal<ConversationBean> currentConversationBeanHolder = new ThreadLocal<ConversationBean>();

    /**
     * Returns the {@link Conversation} that is bound to the current thread of execution.
     */
    public static Conversation getCurrentRawConversation() {
        return currentConversationHolder.get();
    }

    /**
     * Bind the passed {@link Conversation} to the current thread of execution.
     */
    public static void setCurrentRawConversation(Conversation conversation) {
        currentConversationHolder.set(conversation);
    }
    
    public static ConversationBean getCurrentConversation() {
        return currentConversationBeanHolder.get();
    }

    public static void setCurrentConversation(ConversationBean conversationBean) {
        currentConversationBeanHolder.set(conversationBean);
    }
}