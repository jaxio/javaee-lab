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
$output.java($WebSecurity, "UserAuthorizingRealm")##

$output.require("org.apache.deltaspike.core.api.provider.BeanProvider")##
$output.require("org.apache.shiro.authc.AuthenticationException")##
$output.require("org.apache.shiro.authc.AuthenticationInfo")##
$output.require("org.apache.shiro.authc.AuthenticationToken")##
$output.require("org.apache.shiro.authz.AuthorizationInfo")##
$output.require("org.apache.shiro.realm.AuthorizingRealm")##
$output.require("org.apache.shiro.subject.PrincipalCollection")##

public class $output.currentClass extends AuthorizingRealm {
    // FIXME - replace with shiro CdiIniWebEnvironment in next shiro release
    private UserAuthorizingRealmService userAuthorizingRealmService = BeanProvider.getContextualReference(UserAuthorizingRealmService.class, false);


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return userAuthorizingRealmService.getAuthenticationInfo(token);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return userAuthorizingRealmService.getAuthorizationInfo(principals, getAvailablePrincipal(principals));
    }
}
