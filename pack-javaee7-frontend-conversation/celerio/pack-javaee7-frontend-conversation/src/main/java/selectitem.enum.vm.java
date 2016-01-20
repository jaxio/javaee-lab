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
$output.java($enum.items)##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.require("java.util.List")##
$output.require("javax.faces.model.SelectItem")##
$output.require($enum.model)##

/**
 * Helper to support the <code>h:selectOneRadio</code> and <code>h:selectOneMenu</code> for {@link ${enum.model.type}}
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass {

    /**
     * Returns a list of {@link SelectItem}/{@link ${enum.model.type}} ready to use in a <code>h:selectOneRadio</code>, <code>h:selectOneMenu</code> or <code>p:selectManyCheckbox</code> tags
     */
    public List<SelectItem> getList() {
        List<SelectItem> result = newArrayList();
        for ($enum.model.type $enum.model.var : ${enum.model.type}.values()) {
            result.add(new SelectItem($enum.model.var, ${enum.model.var}.getLabel()));
        }

        return result;
    }
}
