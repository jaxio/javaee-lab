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
$output.java($PrinterSupport, "GenericPrinter")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.requireStatic("org.apache.commons.lang.StringUtils.isNotBlank")##
$output.require("java.util.List")##
$output.require("java.util.Locale")##
$output.require("javax.persistence.metamodel.Attribute")##
$output.require($Context, "LocaleHolder")##
$output.require("com.jaxio.jpa.querybyexample.JpaUtil")##

public abstract class ${output.currentClass}<T> {
    private Class<T> clazz;
    private List<String> displayedAttributes;

    public void init(Class<T> clazz, Attribute<?, ?>... displayedAttributes) {
        this.clazz = clazz;
        this.displayedAttributes = newArrayList(JpaUtil.getInstance().toNames(displayedAttributes));
    }

    public Class<T> getTarget() {
        return clazz;
    }

    public String print(T document) {
        return print(document, LocaleHolder.getLocale());
    }
    
    public abstract String print(T object, Locale locale);
    
    public List<String> getDisplayedAttributes() {
        return displayedAttributes;
    }

    protected void appendIfNotEmpty(StringBuilder builder, String value) {
        if (isNotBlank(value)) {
            if (builder.length() != 0) {
                builder.append('/');
            }
            builder.append(value.trim());
        }
    }

    protected void appendIfNotEmpty(StringBuilder builder, Object value) {
        if (value != null) {
            if (builder.length() != 0) {
                builder.append('/');
            }
            builder.append(value);
        }
    }
}