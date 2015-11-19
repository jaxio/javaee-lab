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
$output.java($entity.printer)##

$output.require("java.util.Locale")##
$output.require($entity.model)##
$output.require($PrinterSupport, "GenericPrinter")##

/**
 * {@link GenericPrinter} for {@link $entity.model.type} 
 *
 * @see GenericPrinter
 * @see TypeAwarePrinter
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass extends GenericPrinter<${entity.model.type}> {
#if($entity.printerAttributes.flatUp.size > 0)
$output.requireMetamodel($entity.model)##
#end
    $output.dynamicAnnotation("javax.annotation.PostConstruct")
    public void init() {
        init(${entity.model.type}.class#foreach($attr in $entity.printerAttributes.flatUp.list) , ${entity.model.type}_.${attr.var}#end);
    }

    @Override
    public String print(${entity.model.type} ${entity.model.var}, Locale locale) {
        if ($entity.model.var == null) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
#if($entity.printerAttributes.flatUp.size > 0)
#foreach($attr in $entity.printerAttributes.flatUp.list)
#if($attr.isEnum())
        if (${entity.model.var}.${attr.getter}() != null) {
            appendIfNotEmpty(ret, ${entity.model.var}.${attr.getter}().getLabel());
        }
#else
        appendIfNotEmpty(ret, ${entity.model.var}.${attr.getter}());
#end
#end
#else
        appendIfNotEmpty(ret, ${entity.model.var}.${identifiableProperty.getter}());
#end
        return ret.toString();
    }
}