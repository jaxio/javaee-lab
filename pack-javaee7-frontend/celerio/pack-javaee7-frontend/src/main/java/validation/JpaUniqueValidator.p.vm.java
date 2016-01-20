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
$output.java($WebValidation, "JpaUniqueValidator")##

$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_ERROR")##
$output.requireStatic("org.apache.commons.lang.StringUtils.isBlank")##
$output.require("javax.enterprise.context.RequestScoped")##
$output.require("javax.faces.application.FacesMessage")##
$output.require("javax.faces.component.UIComponent")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.validator.FacesValidator")##
$output.require("javax.faces.validator.Validator")##
$output.require("javax.faces.validator.ValidatorException")##
$output.require("javax.inject.Inject")##
$output.require("javax.inject.Named")##
$output.require("com.jaxio.jpa.querybyexample.JpaUniqueUtil")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($Util, "ResourcesUtil")##

@Named
@RequestScoped
@FacesValidator(value = "jpaUniqueValidator")
public class $output.currentClass implements Validator {

    @Inject
    private JpaUniqueUtil jpaUniqueUtil;
    @Inject
    private ResourcesUtil resourcesUtil;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (ConditionalValidatorUtil.doValidation(context)) {        
            if (entity == null && isBlank(property)) {
                return;
            }
    
            String errorCode = jpaUniqueUtil.validateSimpleUnique(entity, property, value);
            if (errorCode != null) {
                FacesMessage fm = new FacesMessage(resourcesUtil.getProperty(errorCode));
                fm.setSeverity(SEVERITY_ERROR);
                throw new ValidatorException(fm);
            }
        }
    }

    private Identifiable<?> entity;
    private String property;

    public void setEntity(Identifiable<?> entity) {
        this.entity = entity;
    }

    public Identifiable<?> getEntity() {
        return entity;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
