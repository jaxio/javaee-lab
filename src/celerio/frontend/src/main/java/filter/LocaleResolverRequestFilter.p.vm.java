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
$output.java($WebFilter, "LocaleResolverRequestFilter")##

$output.require("java.io.IOException")##
$output.require("javax.enterprise.context.ApplicationScoped")##
$output.require("javax.inject.Inject")##
$output.require("javax.inject.Named")##
$output.require("javax.servlet.Filter")##
$output.require("javax.servlet.FilterChain")##
$output.require("javax.servlet.FilterConfig")##
$output.require("javax.servlet.ServletException")##
$output.require("javax.servlet.ServletRequest")##
$output.require("javax.servlet.ServletResponse")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##

$output.require("org.apache.commons.logging.Log")##
$output.require("org.apache.commons.logging.LogFactory")##

$output.require($Context, "LocaleHolder")##

@Named
@ApplicationScoped
public class LocaleResolverRequestFilter implements Filter {

    @Inject
    private LocaleResolver localeResolver;

    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LocaleHolder.setLocale(localeResolver.getLocale((HttpServletRequest)request,(HttpServletResponse) response));
        try {
            chain.doFilter(request, response);       
        } finally{
            LocaleHolder.setLocale(null);
        }
    }

    @Override
    public void destroy() {
    }
}