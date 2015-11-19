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
$output.java($WebFaces, "ConversationContextScopedContext")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require("javax.enterprise.context.spi.Context")##
$output.require("javax.enterprise.context.spi.Contextual")##
$output.require("javax.enterprise.context.spi.CreationalContext")##
$output.require("javax.enterprise.inject.spi.Bean")##
$output.require($WebConversation, "ConversationBean")##

public class $output.currentClass implements Context {

    @Override
    public Class<ConversationContextScoped> getScope() {
        return ConversationContextScoped.class;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {

        T bean = get(contextual);

        if (bean == null) {
            bean = contextual.create(creationalContext);

            ConversationBean currentConversation = getCurrentConversation();
            if (currentConversation != null && currentConversation.getConversationContextesCount() > 0 && currentConversation.getCurrentContext() != null) {
                currentConversation.getCurrentContext().addBean(contextual, bean);
            } else {
                throw new RuntimeException("No conversation context to store the bean!");
            }
        }

        return bean;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T get(Contextual<T> contextual) {
        ConversationBean currentConversation = getCurrentConversation();
        if (currentConversation == null) {
            return null;
        }

        T bean = null;
        
        if (currentConversation.getConversationContextesCount() > 0 && currentConversation.getCurrentContext() != null) {
            bean = currentConversation.getCurrentContext().getBean(contextual);
        } else {
            throw new RuntimeException("No conversation context to get the bean from!");
        }

        return bean;
    }

    @Override
    public boolean isActive() {
        return true; // TODO ? 
    }
}
