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
$output.java($WebModelSupport, "GenericEnumController")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.requireStatic("org.apache.commons.lang.StringUtils.containsIgnoreCase")##
$output.require("java.util.List")##
$output.require($EnumModelSupport, "LabelizedEnum")##

public class ${output.currentClass}<T extends Enum<? extends Enum<?>> & LabelizedEnum> {

    private T[] values;

    public void init(T[] values) {
        this.values = values;
    }

    public List<T> complete(String text) {
        List<T> ret = newArrayList();
        for (T value : values) {
            if (containsIgnoreCase(value.name(), text) || containsIgnoreCase(value.getLabel(), text)) {
                ret.add(value);
            }
        }
        return ret;
    }
}