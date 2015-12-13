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
$output.java($WebUtil, "DownloadUtil")##

$output.require("javax.faces.context.ExternalContext")##
$output.require("javax.faces.context.FacesContext")##

public class $output.currentClass {

    /**
     * Set the http response header in order to please IE when downloading file over https.
     * see http://stackoverflow.com/questions/1918840/downloading-an-excel-file-over-https-to-ie-from-a-j2ee-application
     */
    public static void forceResponseHeaderForDownload() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.setResponseCharacterEncoding("UTF-8");
        ec.setResponseHeader("Cache-Control", "no-store");
        ec.setResponseHeader("Pragma", "private");
        ec.setResponseHeader("Expires", "1");        
    }
}
