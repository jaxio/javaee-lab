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
$output.java($Util, "ResourcesUtil")##

$output.require("javax.ejb.Startup")##
$output.require("javax.enterprise.context.ApplicationScoped")##
$output.require("javax.inject.Inject")##
$output.require("javax.inject.Named")##
$output.require("com.jaxio.jpa.querybyexample.LocaleHolder")##

/**
 * ResourcesUtil allows you to retrieve localized resources for the locale present in the current thread of execution.
 * You can either inject it or use the static method ResourcesUtil.getInstance().
 */
@ApplicationScoped
@Named
@Startup
public class $output.currentClass {

    private static ${output.currentClass} instance;
    private MessageSource messageSource;
    
    public ${output.currentClass}() {
        instance = this;
        messageSource = createMessageSource();
    }

    /**
     * Call it from code that cannot get a reference to the ${output.currentClass}
     * singleton instance through dependency injection.
     */
    public static ${output.currentClass} getInstance() {
        return instance;
    }

    /**
     * Return the {@link MessageSource} that backs this ${output.currentClass}.
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * Return the property value for the contextual locale.
     * If no value is found, the passed key is returned.
     */
    public String getProperty(String key) {
        return messageSource.getMessage(key, LocaleHolder.getLocale());
    }

    public String getPropertyWithArrayArg(String key, Object[] arrayArg) {
        return messageSource.getMessage(key, arrayArg, LocaleHolder.getLocale());
    }
    
    public String getProperty(String key, Object arg1) {
        return messageSource.getMessage(key, new Object[]{arg1}, LocaleHolder.getLocale());
    }

    // we do not use var args to please EL + passing var args from method to method does not work well
    // with array of Object as an Object[] is also an Object.
    
    public String getProperty(String key, Object arg1, Object arg2) {
        return messageSource.getMessage(key, new Object[]{arg1, arg2}, LocaleHolder.getLocale());
    }

    public String getProperty(String key, Object arg1, Object arg2, Object arg3) {
        return messageSource.getMessage(key, new Object[]{arg1, arg2, arg3}, LocaleHolder.getLocale());
    }

    public String getProperty(String key, Object arg1, Object arg2, Object arg3, Object arg4) {
        return messageSource.getMessage(key, new Object[]{arg1, arg2, arg3, arg4}, LocaleHolder.getLocale());
    }

    public String getProperty(String key, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return messageSource.getMessage(key, new Object[]{arg1, arg2, arg3, arg4, arg5}, LocaleHolder.getLocale());
    }

    public String getProperty(String key, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return messageSource.getMessage(key, new Object[]{arg1, arg2, arg3, arg4, arg5, arg6}, LocaleHolder.getLocale());
    }
    
    /**
     * Same as getProperty() but use the count to choose the proper key.
     * Used when the message varies depending on the context. For example: 'there is no result' vs 'there is one result' vs 'there are n results'
     * @param key the base key
     */
    public String getPluralableProperty(String key, int count) {
        if (key == null) {
            return "";
        }

        switch (count) {
        case 0:
            return getProperty(key + "_0");
        case 1:
            return getProperty(key + "_1");
        default:
            return getProperty(key + "_n", count);
        }
    }

    private MessageSource createMessageSource() {
        MessageSource messageSource = new DefaultMessageSource();
        messageSource.setBasenames( //
        // global
        "localization/messages", //
        "localization/application", //
#foreach($entity in $project.withoutManyToManyJoinEntities.list)
#if ($velocityCount == 1)
        // entities
#end
        "localization/domain/$entity.model.type", //
#end
#foreach($enumType in $project.enumTypes)
#if ($velocityCount == 1)
        // enums
#end
        "localization/domain/$enumType.model.type", //
#end
        // pages
        "localization/pages/concurrentModificationResolution", //
        "localization/pages/home", //
        "localization/pages/login", //
        // validation
        "ValidationMessages", //
        "javax/faces/Messages", //
        "org/hibernate/validator/ValidationMessages");
        return messageSource;
    }
}