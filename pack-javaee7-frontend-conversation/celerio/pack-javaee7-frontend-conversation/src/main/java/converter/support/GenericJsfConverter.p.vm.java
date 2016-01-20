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
$output.java($WebConverterSupport, "GenericJsfConverter")##

$output.require("javax.faces.component.UIComponent")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.convert.Converter")##
$output.require("javax.faces.convert.ConverterException")##
$output.require("org.apache.commons.lang.RandomStringUtils")##

/**
 * Base JSF converter to store objects in the jsf tree. 
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class ${output.currentClass} implements Converter {
	private static final String COMPONENT_UNIQUE_PREFIX = "object:";
	private static final String COMPONENT_NULL_VALUE = COMPONENT_UNIQUE_PREFIX + "null";

	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		if (value == null || COMPONENT_NULL_VALUE.equals(value)) {
			return null;
		}
		return component.getAttributes().get(value);
	}

	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object == null) {
			return COMPONENT_NULL_VALUE;
		}
		String unique = COMPONENT_UNIQUE_PREFIX + RandomStringUtils.randomAlphanumeric(36);
		component.getAttributes().put(unique, object);
		return unique;
	}
}