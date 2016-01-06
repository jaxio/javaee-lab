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
$output.java($WebFilter, "SessionExpirationFilter")##

$output.require("java.io.IOException")##
$output.require("javax.inject.Named")##
$output.require("javax.servlet.Filter")##
$output.require("javax.servlet.FilterChain")##
$output.require("javax.servlet.FilterConfig")##
$output.require("javax.servlet.ServletException")##
$output.require("javax.servlet.ServletRequest")##
$output.require("javax.servlet.ServletResponse")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require($WebUtil, "PrimeFacesUtil")##

/**
 * This filter handles session expiration during ajax request.
 * IMPORTANT: The spring security filter MUST be placed after this one.
 *
 * Note: if you do not use Spring Security filter then you do not need this filter since you can 
 * handle ViewExpiredException as any other exception (see {@link ConversationAwareExceptionHandler}).
 */
@Named
public class $output.currentClass implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (PrimeFacesUtil.isAjax(request) && !request.isRequestedSessionIdValid()) {
            response.getWriter().print(xmlPartialRedirectToPage(request, "/login.faces?session_expired=1"));
            response.flushBuffer();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String xmlPartialRedirectToPage(HttpServletRequest request, String page) {
        return "<?xml version='1.0' encoding='UTF-8'?>" //
                + "<partial-response>" //
                + "<redirect url=\"" + request.getContextPath() + page + "\"/>" //
                + "</partial-response>";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}