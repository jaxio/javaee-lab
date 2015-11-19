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
$output.java($WebFaces, "ConversationAwareElResolver")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require("java.beans.FeatureDescriptor")##
$output.require("java.util.Iterator")##
$output.require("javax.el.ELContext")##
$output.require("javax.el.ELException")##
$output.require("javax.el.ELResolver")##
$output.require($WebConversation, "ConversationBean")##

/**
 * ConversationAwareElResolver is declared in faces-config.xml.
 * It tries to find values in the current {@link ConversationContext}. 
 */
public class $output.currentClass extends ELResolver {

    @Override
    public Object getValue(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null && property != null) {
            ConversationBean currentConversation = getCurrentConversation();
            if (currentConversation != null && currentConversation.getConversationContextesCount() > 0) {
                Object result = currentConversation.getCurrentContext().getVar(property.toString());
                if (result != null) {
                    elContext.setPropertyResolved(true);
                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public Class<?> getType(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null && property != null) {
            ConversationBean currentConversation = getCurrentConversation();
            if (currentConversation != null && currentConversation.getConversationContextesCount() > 0) {
                Object value = currentConversation.getCurrentContext().getVar(property.toString());
                if (value != null) {
                    elContext.setPropertyResolved(true);
                    return value.getClass();
                }
            }
        }

        return null;     
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override    
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return Object.class;
    }
}
