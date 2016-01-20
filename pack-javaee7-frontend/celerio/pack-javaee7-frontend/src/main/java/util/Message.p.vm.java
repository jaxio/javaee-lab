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
$output.java($WebUtil, "Message")##

$output.require("javax.faces.application.FacesMessage")##

/**
 * Integration _HACK_ : would be nice if jsf was providing the id...
 * Wrap {@link FacesMessage} along with the id of the associated component.
 */
public class $output.currentClass {

    private String sourceId;
    private FacesMessage facesMessage;

    public ${output.currentClass}(String sourceId, FacesMessage facesMessage) {
        this.sourceId = sourceId;
        this.facesMessage = facesMessage;
    }

    public String getSourceId() {
        return sourceId;
    }

    public FacesMessage getFacesMessage() {
        return facesMessage;
    }

    public String getSeverity() {
        return MessageUtil.toCssFriendly(facesMessage.getSeverity());
    }
}
