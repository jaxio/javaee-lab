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
$output.java($WebUtil, "MessageBundle")##

$output.require("java.util.Enumeration")##
$output.require("java.util.ResourceBundle")##
$output.require($Util, "ResourcesUtil")##

/**
 * This {@link ResourceBundle} is set in faces-config.xml under <code>msg</code> var.
 * <p>
 * Implementation uses Spring {@link MessageSource}.
 * <p>
 * From your JSF2 pages, you may use <code>#{msg.property_key}</code>.
 * <p>
 * _HACK_ as it is a tricky JSF/Spring integration point.
 */
public class $output.currentClass extends ResourceBundle {

    private ResourcesUtil resourcesUtil;
    
    @Override
    public Enumeration<String> getKeys() {
        return null;
    }

    @Override
    protected Object handleGetObject(String key) {
        if (resourcesUtil == null) {
            resourcesUtil = ResourcesUtil.getInstance();
        }
        return resourcesUtil.getProperty(key);
    }
}