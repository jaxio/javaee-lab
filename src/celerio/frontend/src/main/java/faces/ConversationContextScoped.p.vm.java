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
$output.java($WebFaces, "ConversationContextScoped")##

$output.requireStatic("java.lang.annotation.ElementType.TYPE")##
$output.requireStatic("java.lang.annotation.ElementType.FIELD")##
$output.requireStatic("java.lang.annotation.ElementType.METHOD")##
$output.requireStatic("java.lang.annotation.ElementType.FIELD")##
$output.requireStatic("java.lang.annotation.RetentionPolicy.RUNTIME")##
$output.require("java.lang.annotation.Documented")##
$output.require("java.lang.annotation.Inherited")##
$output.require("java.lang.annotation.Retention")##
$output.require("java.lang.annotation.Target")##
$output.require("javax.enterprise.context.NormalScope")##
$output.require($WebConversation, "ConversationBean")##
$output.require($WebConversation, "ConversationContext")##

/**
 * Beans in the 'conversationContext' scope reside in a {@link ConversationBean conversation}'s {@link ConversationContext}.
 * They are 'visible' only when the navigation is bound to the current Thread of execution and their 
 * hosting ConversationContext is on top of the navigation's contextes stack.
 * <p>
 * Such a design decision allows a conversation to have 2 <code>ConversationContextScoped</code> beans with 
 * the same name (they just have to reside in 2 different ConversationContext).
 * This prevents bean name clash in complex navigation scenario within the same conversation.
 */
@Target( { TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
@NormalScope(passivating = true)
@Inherited
public @interface $output.currentClass {
}
