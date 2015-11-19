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
$output.java($Util, "ConfigPropertyProducer")##

$output.require("javax.enterprise.inject.Produces")##
$output.require("javax.enterprise.inject.spi.InjectionPoint")##
$output.require("javax.inject.Inject")##
$output.require("org.apache.deltaspike.core.util.BeanUtils")##

public class $output.currentClass {

    @Inject
    private ResourcesUtil resourceUtil;

    @Produces 
    @IntConfig(name = "notused")
    public int intPropertyProducer(InjectionPoint injectionPoint) {
        IntConfig intConfig = BeanUtils.extractAnnotation(injectionPoint.getAnnotated(), IntConfig.class);

        String value = resourceUtil.getProperty(intConfig.name());

        if (value != null && !value.isEmpty()) {
            return Integer.parseInt(value);
        } else if (intConfig.required()) {
            throw new IllegalStateException("Could not find any value for key " + intConfig.name());
        } else {
            return intConfig.defaultValue();
        }
    }
}