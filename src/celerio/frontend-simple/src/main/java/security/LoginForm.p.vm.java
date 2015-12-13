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
$output.java($WebSecurity, "LoginForm")##

$output.require("java.io.IOException")##
$output.require("java.io.Serializable")##
$output.require("java.util.logging.Level")##
$output.require("java.util.logging.Logger")##
$output.require("javax.faces.context.ExternalContext")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("javax.inject.Named")##
$output.require("org.apache.shiro.SecurityUtils")##
$output.require("org.apache.shiro.authc.AuthenticationException")##
$output.require("org.apache.shiro.authc.UsernamePasswordToken")##
$output.require("org.apache.shiro.web.util.SavedRequest")##
$output.require("org.apache.shiro.web.util.WebUtils")##
$output.require("org.omnifaces.util.Faces")##
$output.require($WebUtil, "MessageUtil")##

/**
 * More info on login: http://balusc.blogspot.fr/2013/01/apache-shiro-is-it-ready-for-java-ee-6.html
 */
$output.dynamicAnnotationTakeOver("javax.inject.Named","javax.faces.view.ViewScoped")
public class $output.currentClass implements Serializable {
    public static final String HOME_URL = "/home.faces";

    @Inject
    private transient Logger log;
    @Inject
    private transient MessageUtil messageUtil;

    private String username;
    private String password;
    private boolean rememberMe;

    public void submit() throws IOException {
        try {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password, rememberMe));
            SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(Faces.getRequest());
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            String redirectUrl = externalContext.getRequestContextPath() + HOME_URL;
            Faces.redirect(savedRequest != null ? savedRequest.getRequestUrl() : redirectUrl);
        } catch (AuthenticationException e) {
            messageUtil.error("login_error");
            log.log(Level.SEVERE, "AuthenticationException for user " + username, e);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}