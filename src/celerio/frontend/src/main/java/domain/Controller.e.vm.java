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
$output.java($entity.controller)##

$output.require("javax.inject.Inject")##
$output.require("javax.annotation.PostConstruct")##
$output.require($WebModelSupport, "GenericController")##
$output.require($entity.webPermission)##
$output.require($entity.model)##
$output.require($entity.root.primaryKey)##
$output.require($entity.repository)##
$output.require($entity.printer)##

/**
 * Stateless controller for {@link $entity.model.type} conversation start. 
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass extends GenericController<$entity.model.type, $entity.root.primaryKey.type> {
    public static final String ${entity.model.type.toUpperCase()}_EDIT_URI = "/${entity.model.subPackagePath}/${entity.model.var}Edit.faces";
    public static final String ${entity.model.type.toUpperCase()}_SELECT_URI = "/${entity.model.subPackagePath}/${entity.model.var}Select.faces";

    @Inject
    protected $entity.repository.type $entity.repository.var;
    @Inject
    protected $entity.webPermission.type $entity.webPermission.var;
    @Inject
    protected $entity.printer.type $entity.printer.var;
    
    @PostConstruct    
    public void init() {
        init($entity.repository.var, $entity.webPermission.var, $entity.printer.var, ${entity.model.type.toUpperCase()}_SELECT_URI, ${entity.model.type.toUpperCase()}_EDIT_URI);
    }    
#if($entity.defaultSortAttributes.flatUp.isNotEmpty())
$output.require("com.jaxio.jpa.querybyexample.SearchParameters")##
$output.requireMetamodel($entity.model)##

    @Override
    protected SearchParameters defaultOrder(SearchParameters searchParameters) {
        return searchParameters
#foreach($attribute in $entity.defaultSortAttributes.flatUp.list)
#if($attribute.isDate())
            .desc(${entity.model.type}_.$attribute.var)$project.print($velocityHasNext, "//", ";")
#else
            .asc(${entity.model.type}_.$attribute.var)$project.print($velocityHasNext, "//", ";")
#end
#end
    }
#end
#if($entity.fileAttributes.isNotEmpty())

    public ${entity.fileUpload.type} ${entity.fileUpload.getter}($entity.model.type $entity.model.var) {
        return new ${entity.fileUpload.type}($entity.model.var);
    }
#end
}