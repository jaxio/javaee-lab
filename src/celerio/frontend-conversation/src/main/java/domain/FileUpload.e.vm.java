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
$output.java($entity.fileUpload)##

$output.require($entity.model)##
$output.require("org.primefaces.event.FileUploadEvent")##
$output.require("org.primefaces.model.UploadedFile")##

public class $output.currentClass {
    private $entity.model.type $entity.model.var;

    public ${output.currentClass}($entity.model.type $entity.model.var) {
        this.$entity.model.var = $entity.model.var;
    }
#foreach ($attribute in $entity.fileAttributes.list)

    /**
     * Primefaces support for ${attribute.var} file upload
     */
    public void on${attribute.varUp}FileUpload(FileUploadEvent fileUploadEvent) {
        UploadedFile uploadedFile = fileUploadEvent.getFile(); //application code
        ${entity.model.var}.${attribute.setter}(uploadedFile.getContents());
#if ($attribute.fileSize)
#if($attribute.fileSize.isInteger())    
        ${entity.model.var}.${attribute.fileSize.setter}(${entity.model.var}.${attribute.getter}().length);
#else
$output.require($attribute.fileSize)##
        ${entity.model.var}.${attribute.fileSize.setter}(${attribute.fileSize.type}.valueOf(${entity.model.var}.${attribute.getter}().length));
#end
#end
#if ($attribute.fileContentType)
        ${entity.model.var}.${attribute.fileContentType.setter}(uploadedFile.getContentType());
#end
#if ($attribute.filename)
$output.require("org.apache.commons.io.FilenameUtils")##
        ${entity.model.var}.${attribute.filename.setter}(FilenameUtils.getName(uploadedFile.getFileName()));
#end
    }
#end
}