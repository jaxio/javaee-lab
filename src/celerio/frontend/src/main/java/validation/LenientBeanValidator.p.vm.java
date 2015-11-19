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
$output.java($WebValidation, "LenientBeanValidator")##

$output.require("javax.faces.component.UIComponent")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.validator.BeanValidator")##

/**
 * Disables validation for certain actions in order to let the user navigate to sub view
 * without loosing (potentially invalid) data entered in input fields.
 *
 * _HACK_ This setting is tricky. It circumvents a JSF limitation.
 */
$output.dynamicAnnotationTakeOver("javax.inject.Named", "javax.enterprise.context.RequestScoped")##
public class $output.currentClass extends BeanValidator {
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {
        if (ConditionalValidatorUtil.doValidation(context)) {
            super.validate(context, component, value);
        }
    }
}
