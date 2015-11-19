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
$output.java($WebConverterSupport, "NumberToStringConverter")##

$output.require("javax.inject.Named")##
$output.require("org.apache.commons.lang.math.NumberUtils")##

@Named
public class $output.currentClass extends AbstractTypeConverter<String, Number> {

    @Override
    public Number convert(String source) {
        return NumberUtils.createNumber(source);
    }

    @Override
    Class<String> getSourceType() {
        return String.class;
    }

    @Override
    Class<Number> getTargetType() {
        return Number.class;
    }
}
