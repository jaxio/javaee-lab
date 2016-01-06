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
$output.java($Context, "UserWithId")##

$output.require("java.io.Serializable")##
## -- id type
#if ($project.isAccountEntityPresent())
$output.require($project.accountEntity.root.primaryKey)##
#set ($idType=$project.accountEntity.root.primaryKey.type)##
#else
#set ($idType="Serializable")##
#end

/**
 * Simple User that also keep track of the primary key.
 */
public class $output.currentClass implements Serializable {
    private static final long serialVersionUID = 1L;
    private $idType id;
    private final String username;
    private final boolean enabled;

    public UserWithId(String username, boolean enabled, $idType id) {
        this.username = username;
        this.enabled = enabled;
        this.id = id;
    }

    public $idType getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }
}