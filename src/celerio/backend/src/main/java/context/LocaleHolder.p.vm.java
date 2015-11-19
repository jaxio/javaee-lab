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
$output.java($Context, "LocaleHolder")##

$output.require("java.util.Locale")##

public class $output.currentClass {
    private static final ThreadLocal<Locale> currentLocaleHolder = new ThreadLocal<Locale>();

    public static Locale getLocale() {
        Locale locale = currentLocaleHolder.get();
        
        return locale != null ? locale : Locale.getDefault();
    }

    public static void setLocale(Locale locale) {
        currentLocaleHolder.set(locale);
    }
}