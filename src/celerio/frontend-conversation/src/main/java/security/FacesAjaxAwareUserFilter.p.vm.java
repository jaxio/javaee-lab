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
$output.java($WebSecurity, "FacesAjaxAwareUserFilter")##

$output.require("java.io.IOException")##
$output.require("javax.servlet.ServletRequest")##
$output.require("javax.servlet.ServletResponse")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("org.apache.shiro.web.filter.authc.UserFilter")##

/**
 * Please see: http://balusc.blogspot.fr/2013/01/apache-shiro-is-it-ready-for-java-ee-6.html
 */
public class $output.currentClass extends UserFilter {

    private static final String FACES_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

    @Override
    protected void redirectToLogin(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
            res.setContentType("text/xml");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().printf(FACES_REDIRECT_XML, request.getContextPath() + getLoginUrl());
        } else {
            super.redirectToLogin(req, res);
        }
    }
}
