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
$output.java($Context, "UserContext")##

$output.require("java.util.Locale")##
$output.require("org.apache.shiro.SecurityUtils")##
$output.require("org.apache.shiro.subject.Subject")##
#if ($project.isAccountEntityPresent())
$output.require($project.accountEntity.root.primaryKey)##
#set ($idType=$project.accountEntity.root.primaryKey.type)##
#else
$output.require("java.io.Serializable")##
#set ($idType="Serializable")##
#end
$output.require("com.jaxio.jpa.querybyexample.LocaleHolder")##


/**
 * Get Spring security context to access user data security infos
 */
public class $output.currentClass {
    public static final String ANONYMOUS_USER = "anonymousUser";
    /**
     * Get the current username. Note that it may not correspond to a username that
     * currently exists in your account repository; it could be a spring security
     * 'anonymous user'.
     * 
     * @return the current user's username, or 'anonymousUser'.
     */
    public static String getUsername() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Object principal = subject.getPrincipal();

            if (principal != null && principal instanceof UserWithId) {
                return ((UserWithId) principal).getUsername();
            } else if (principal != null) {
                return principal.toString();
            }
        }
        return ANONYMOUS_USER;
    }

    public static $idType getId() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Object principal = subject.getPrincipal();

            if (principal != null && principal instanceof UserWithId) {
                return ((UserWithId) principal).getId();
            }
        }

        return null;
    }

    /**
     * return the current locale
     */
    public static Locale getLocale() {
        return LocaleHolder.getLocale();
    }

    /**
     * Tell whether the passed role is set?
     * 
     * @return true if the passed role is present, false otherwise.
     */
    public static boolean hasRole(String roleName) {
        return SecurityUtils.getSubject().hasRole(roleName);
    }
}