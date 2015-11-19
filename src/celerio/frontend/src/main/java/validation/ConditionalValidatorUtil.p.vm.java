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
$output.java($WebValidation, "ConditionalValidatorUtil")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.require("java.util.List")##
$output.require("java.util.Map")##
$output.require("javax.faces.context.FacesContext")##

/**
 * Central place for conditional validation policy.
 */
public class $output.currentClass {
    private static final List<String> actionsRequiringValidation = newArrayList( //
            "form:saveAndClose", // close button present in main form (see saveButton.xml)
            "form:save", // button present in main form (see saveButton.xml)
            "form:askForSaveDialogYes", // button present in ask for save dialog
            "form:ok" // button present in sub-edit (see saveButton.xml)

    );

    /**
     * Depending on which action was triggered, decides if validation should take place or not.
     * 
     * @return true if validation should be performed, false otherwise.
     */
    public static boolean doValidation(FacesContext context) {
        Map<String, String[]> requestParameterValuesMap = context.getExternalContext().getRequestParameterValuesMap();

        for (String action : actionsRequiringValidation) {
            if (requestParameterValuesMap.containsKey(action)) {
                return true;
            }
        }
        return false;
    }
}
