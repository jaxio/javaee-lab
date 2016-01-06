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
$output.java($enum.controller.packageName, "$enum.controller.type")##

$output.require("javax.annotation.PostConstruct")##
$output.require($enum.model)##
$output.require($WebModelSupport, "GenericEnumController")##

$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class ${output.currentClass} extends GenericEnumController<${enum.model.type}> {
    
    @PostConstruct
    public void init() {
        init(${enum.model.type}.values());
    }
}
