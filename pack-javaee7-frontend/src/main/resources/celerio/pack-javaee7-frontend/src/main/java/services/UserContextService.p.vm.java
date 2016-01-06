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
$output.java($WebService, "UserContextService")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.require("java.util.List")##
$output.require($Context, "UserContext")##

/**
 * Simple pass over to access static 'userContext' from EL.
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass {
    public String getUsername() {
        return UserContext.getUsername();
    }

    public boolean isAnonymousUser() {
        return UserContext.ANONYMOUS_USER.equalsIgnoreCase(getUsername());
    }

    public boolean isLoggedIn() {
        return !isAnonymousUser();
    }

    // TODO: improve... 
    public List<String> getRoles() {
        List<String> roles = newArrayList();

        if (hasRole("ROLE_ADMIN")) {
            roles.add("ROLE_ADMIN");
        }

        if (hasRole("ROLE_USER")) {
            roles.add("ROLE_USER");
        }

        if (hasRole("ROLE_MONITORING")) {
            roles.add("ROLE_MONITORING");
        }
        
        return roles;
    }

    public boolean hasRole(String role) {
        return UserContext.hasRole(role);
    }
}