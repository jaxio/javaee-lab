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
$output.java($WebSecurity, "UserAuthorizingRealmService")##

$output.requireStatic("com.google.common.collect.Sets.newHashSet")##
$output.require("java.util.logging.Logger")
$output.require("java.util.Set")##
$output.require("javax.inject.Inject")##
$output.require("org.apache.shiro.authc.AuthenticationException")##
$output.require("org.apache.shiro.authc.AuthenticationInfo")##
$output.require("org.apache.shiro.authc.AuthenticationToken")##
$output.require("org.apache.shiro.authc.DisabledAccountException")##
$output.require("org.apache.shiro.authc.SimpleAuthenticationInfo")##
$output.require("org.apache.shiro.authc.UnknownAccountException")##
$output.require("org.apache.shiro.authc.UsernamePasswordToken")##
$output.require("org.apache.shiro.authz.AuthorizationException")##
$output.require("org.apache.shiro.authz.AuthorizationInfo")##
$output.require("org.apache.shiro.authz.SimpleAuthorizationInfo")##
$output.require("org.apache.shiro.subject.PrincipalCollection")##
$output.require($Context, "UserWithId")##

$output.dynamicAnnotationTakeOver("javax.ejb.Singleton")##
public class $output.currentClass {
    @Inject
    private Logger log;
#if ($project.isAccountEntityPresent())
$output.require($project.accountEntity.model)##
$output.require($project.accountEntity.repository)##    
    @Inject
    private $project.accountEntity.repository.type $project.accountEntity.repository.var;

    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        $project.accountEntity.model.type $project.accountEntity.model.var = ${project.accountEntity.repository.var}.${project.accountEntity.accountAttributes.username.uniqueGetter}(username);

        if ($project.accountEntity.model.var != null) {
#if ($project.accountEntity.accountAttributes.isEnabledSet())            
            if(!${project.accountEntity.model.var}.${project.accountEntity.accountAttributes.enabled.getter}()) {
                log.info("User " + username + " is disabled");
                throw new DisabledAccountException("$project.accountEntity.model.type [" + username + "] is disabled.");
            }

#end
            UserWithId userWithId = new UserWithId(username, true, ${project.accountEntity.model.var}.${identifiableProperty.getter}());
            return new SimpleAuthenticationInfo(userWithId, ${project.accountEntity.model.var}.${project.accountEntity.accountAttributes.password.getter}(), UserAuthorizingRealm.class.getCanonicalName());
        } else {
            log.info("User " + username + " not found");
            throw new UnknownAccountException("User " + username + " could not be found");
        }
    }

    public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals, Object availablePrincipal) {
        // null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        UserWithId userWithId = (UserWithId) availablePrincipal;

        String username = userWithId.getUsername();
        $project.accountEntity.model.type $project.accountEntity.model.var = ${project.accountEntity.repository.var}.${project.accountEntity.accountAttributes.username.uniqueGetter}(username);
        if ($project.accountEntity.model.var != null){
#if ($project.accountEntity.accountAttributes.isEnabledSet())            
            if(!${project.accountEntity.model.var}.${project.accountEntity.accountAttributes.enabled.getter}()) {
                log.info("User " + username + " is disabled");
                throw new DisabledAccountException("$project.accountEntity.model.type [" + userWithId.getUsername() + "] is disabled.");
             }

#end
            Set<String> roleNames = newHashSet(${project.accountEntity.model.var}.getRoleNames());
            return new SimpleAuthorizationInfo(roleNames);
        } else{
            log.info("User " + username + " not found");
            throw new UnknownAccountException("User " + username + " could not be found");
        }
    }
#else
    
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        log.info("User " + username + " is login... you should change this");
        UserWithId user = new UserWithId(username, true, username);
        return new SimpleAuthenticationInfo(user, username /* password */ , UserAuthorizingRealm.class.getCanonicalName());
    }

    public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals, Object availablePrincipal) {
        // null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        UserWithId user = (UserWithId) availablePrincipal;
        Set<String> roleNames = newHashSet("ROLE_ADMIN");
        log.info("granting user " + user.getUsername() + " ROLE_ADMIN, you should change this");        
        return new SimpleAuthorizationInfo(roleNames);
    }
#end    
}