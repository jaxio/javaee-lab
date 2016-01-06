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
$output.java($WebConverterSupport, "IsoConverter")##

$output.require("javax.inject.Named")##

/**
 * Converter for input that does not need conversion.
 * 
 * @param <T>
 */
@Named
public class ${output.currentClass}<T> implements TypeConverter<T, T> {

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        return source != null && targetType.isAssignableFrom(source.getClass());
    }

    @Override
    public T convert(T source) {
        return source;
    }
}