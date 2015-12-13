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
$output.generateIf($entity.fileAttributes.isNotEmpty())##
$output.java($entity.fileDownload)##

$output.require("javax.inject.Inject")##
$output.require($entity.model)##
$output.require($entity.repository)##

/**
 * Stateless controller to download {@link $entity.model.type} binaries 
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass {
    @Inject
    private $entity.repository.type $entity.repository.var;
#foreach ($attribute in $entity.fileAttributes.list)
$output.require($WebModelSupport, "ByteArrayStreamedContent")##
$output.require("org.primefaces.model.StreamedContent")##
$output.require("${entity.model.fullType}_")##

    /**
     * Primefaces support for ${attribute.var} file download.
     */    
    public StreamedContent get${attribute.varUp}Stream(final $entity.model.type $entity.model.var) {
        // check whether the binary is null WITHOUT downloading it.
        if (${entity.model.var}.${identifiableProperty.iser}()) {
            if (${entity.repository.var}.isPropertyNull(${entity.model.var}.${identifiableProperty.getter}(), ${entity.model.type}_.$attribute.var)) {
                return null;
            }
        } else if (${entity.model.var}.${attribute.getter}() == null) {
            return null;
        }
        
        ByteArrayStreamedContent basc = new ByteArrayStreamedContent() {            
            @Override 
            public byte[] getByteArray() { 
                return ${entity.repository.var}.${attribute.getter}($entity.model.var); 
        }
        };
#if ($attribute.fileContentType) 
        basc.setContentType(${entity.model.var}.${attribute.fileContentType.getter}());
#end    
#if ($attribute.filename)
        basc.setName(${entity.model.var}.${attribute.filename.getter}());
#else
        basc.setName("$attribute.name");
#end
        return basc;
    }
#end
}