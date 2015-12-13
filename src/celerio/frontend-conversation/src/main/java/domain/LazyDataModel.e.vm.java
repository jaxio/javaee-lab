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
$output.java($entity.lazyDataModel)##

$output.require("javax.inject.Inject")##
$output.require("org.primefaces.model.LazyDataModel")##
$output.require($entity.model)##
$output.require($entity.root.primaryKey)##
$output.require($entity.repository)##
$output.require($entity.excelExporter)##
$output.require($WebModelSupport, "GenericLazyDataModel")##

/**
 * Provide PrimeFaces {@link LazyDataModel} for {@link $entity.model.type}
 */
$output.dynamicAnnotationTakeOver("${WebFaces.packageName}.ConversationContextScoped","javax.inject.Named")##
public class $output.currentClass extends GenericLazyDataModel<$entity.model.type, $entity.root.primaryKey.type, $entity.searchForm.type> {
    private static final long serialVersionUID = 1L;

    public ${output.currentClass}() {
        // mandatory no-args constructor to make this bean proxyable
    }
    
    @Inject
    public ${output.currentClass}($entity.repository.type $entity.repository.var, $entity.controller.type $entity.controller.var, $entity.searchForm.type $entity.searchForm.var, $entity.excelExporter.type $entity.excelExporter.var) {
        super($entity.repository.var, $entity.controller.var, $entity.searchForm.var, $entity.excelExporter.var);
    }
}