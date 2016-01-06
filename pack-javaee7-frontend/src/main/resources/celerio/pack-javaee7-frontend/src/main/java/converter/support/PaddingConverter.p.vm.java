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
$output.generateIf($CHAR_PADDING)##
$output.java($WebConverterSupport, "PaddingConverter")##

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;

import com.sun.faces.util.MessageFactory;

/**
 * Helper to support the padding
 */
@Named
@Singleton
public class ${output.currentClass} implements Converter {

    private static final String PADDING_PARAMETER = "padding";

    private static final String PADDING_ID = "${WebModelSupport.packageName}.PaddingConverter.PADDING";

    private static final String NOT_A_STRING_ID = "${WebModelSupport.packageName}.PaddingConverter.NOT_A_STRING";

    @Inject
    private MessageSource messageSource;

    /**
     * Convert the user input as an Object, it returns a String that matches the input with a right padding. The padding attribute is given as a converter
     * parameter, the default padding character is ' '.
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        int padding = getPadding(context, component);
        if (StringUtils.isBlank(value)) {
            return StringUtils.rightPad("", padding);
        }
        return StringUtils.rightPad(value, padding);
    }

    private int getPadding(FacesContext context, UIComponent component) {
        if (component.getAttributes() != null && component.getAttributes().containsKey(PADDING_PARAMETER)) {
            return Integer.valueOf((String) component.getAttributes().get(PADDING_PARAMETER));
        } else {
            String message = messageSource.getMessage(PADDING_ID, null, context.getViewRoot().getLocale());
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
        }
    }

    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        if (object == null) {
            return "";
        }
        if (object instanceof String) {
            return ((String) object).trim();
        } else {
            throw new ConverterException(MessageFactory.getMessage(context, NOT_A_STRING_ID, object, MessageFactory.getLabel(context, component)));
        }
    }
}
