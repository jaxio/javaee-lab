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
$output.java($WebFilter, "CookieLocaleResolver")##

$output.requireStatic("com.google.common.collect.Iterables.find")##
$output.requireStatic("java.util.Arrays.asList")##
$output.require("java.util.Locale")##
$output.require("javax.servlet.http.Cookie")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require("org.apache.commons.lang.LocaleUtils")##
$output.require("org.apache.deltaspike.core.api.exclude.Exclude")##
$output.require("com.google.common.base.Predicate")##

/**
 * inspired from org.springframework.web.servlet.i18n.CookieLocaleResolver
 */
@Exclude
public class $output.currentClass implements LocaleResolver {

    private static final int DEFAULT_COOKIE_MAX_AGE = -1;
    private static final String DEFAULT_COOKIE_NAME = "locale";
    
    private String cookieName = DEFAULT_COOKIE_NAME;
    private int cookieMaxAge = DEFAULT_COOKIE_MAX_AGE;
    private Locale defaultLocale;

    @Override
    public Locale getLocale(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = null;
        if (request != null) {
            Cookie cookie = getCookie(request, cookieName);
            if (cookie != null) {
                try {
                    locale = LocaleUtils.toLocale(cookie.getValue());
                } catch (IllegalArgumentException e) {
                    // invalid locale string retrieved from the cookie, reset cookie value
                    locale = defaultLocale;
                    setLocale(request, response, defaultLocale);
                }
            }
        }

        if (locale == null) {
            locale = defaultLocale;
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (locale != null) {
            Cookie cookie = new Cookie(cookieName, locale.toString());
            cookie.setMaxAge(cookieMaxAge);
            //cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }

        return find(asList(request.getCookies()), getCookieNamePredicate(name), null);
    }

    private Predicate<Cookie> getCookieNamePredicate(final String cookieName) {
        return new Predicate<Cookie>() {
            @Override
            public boolean apply(Cookie cookie) {
                return cookieName.equals(cookie.getName());
            }
        };
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}