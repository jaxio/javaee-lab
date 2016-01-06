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
$output.generateIf($CHAR_PADDING)##
$output.java($Validation, "Padding")##

$output.requireStatic("java.lang.annotation.ElementType.ANNOTATION_TYPE")##
$output.requireStatic("java.lang.annotation.ElementType.FIELD")##
$output.requireStatic("java.lang.annotation.ElementType.METHOD")##
$output.requireStatic("java.lang.annotation.RetentionPolicy.RUNTIME")##
$output.require("java.lang.annotation.Documented")##
$output.require("java.lang.annotation.Retention")##
$output.require("java.lang.annotation.Target")##

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
/**
 * Remove the padding on the following events
 * <ul>
 * <li>{@link javax.persistence.PrePersist}</li>
 * <li>{@link javax.persistence.PreRemove}</li>
 * <li>{@link javax.persistence.PreUpdate}</li>
 * </ul>
 * Add padding on the following events
 * <ul>
 * <li>{@link javax.persistence.PostPersist}</li>
 * <li>{@link javax.persistence.PostLoad}</li>
 * <li>{@link javax.persistence.PostUpdate}</li>
 * </ul>
 * @see ${RepositorySupport.packageName}.PaddingListener
 */
public @interface $output.currentClass {
    char padChar() default ' ';
}
