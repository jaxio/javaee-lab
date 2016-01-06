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
$output.java($Service, "VersionService")##

$output.requireStatic("org.apache.commons.lang.StringUtils.endsWithIgnoreCase")##
$output.requireStatic("org.apache.commons.lang.StringUtils.equalsIgnoreCase")##

$output.dynamicAnnotationTakeOver("javax.inject.Named")##
public class $output.currentClass {

    private static final String NO_SVN_REVISION = "noSVNRevision";

    //@Value("${dollar}{build.version:}")
    private String version = "1.0.0-SNAPSHOT"; // TODO

    //@Value("${dollar}{build.svnrevision:}")
    private String svnrevision = "0"; // TODO

    public String format() {
        if (endsWithIgnoreCase(version, "SNAPSHOT") && !equalsIgnoreCase(svnrevision, NO_SVN_REVISION)) {
            return version + " (rev" + svnrevision + ")";
        }
        return version;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getSvnrevision() {
        return svnrevision;
    }
}
