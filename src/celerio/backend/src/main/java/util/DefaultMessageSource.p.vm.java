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
$output.java($Util, "DefaultMessageSource")##

$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.require("java.text.MessageFormat")##
$output.require("java.util.Arrays")##
$output.require("java.util.HashMap")##
$output.require("java.util.List")##
$output.require("java.util.Locale")##
$output.require("java.util.Map")##
$output.require("java.util.MissingResourceException")##
$output.require("java.util.logging.Level")##
$output.require("java.util.logging.Logger")##
$output.require("java.util.ResourceBundle")##
$output.require("com.google.common.collect.Lists")##

$output.dynamicAnnotationTakeOver("org.apache.deltaspike.core.api.exclude.Exclude")##
public class $output.currentClass implements MessageSource {

    protected Logger logger = Logger.getLogger(DefaultMessageSource.class.getName());

    protected Map<String, Map<Locale, ResourceBundle>> resourceBundles = newHashMap();

    protected List<String> basenames = Lists.newArrayList();

    @Override
    public void setBasenames(String... basenames) {
        if (basenames != null && basenames.length > 0) {
            this.basenames.clear();
            this.basenames.addAll(Arrays.asList(basenames));
        }
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return getMessage(key, null, locale);
    }
    
    @Override
    public String getMessage(String key, Object[] args, Locale locale) {
        if (key == null) {
            return "";
        }
        
        // NOTE: we do not use varargs, as passing var args from method to method does not work...        
        String messageFormat = getMessageFormatFromKey(key, locale);

        if (messageFormat != null) {
            if (args == null || args.length ==0) {
                return messageFormat;
            }

            switch (args.length) {
            case 1: return MessageFormat.format(messageFormat, args[0]);
            case 2: return MessageFormat.format(messageFormat, args[0], args[1]);
            case 3: return MessageFormat.format(messageFormat, args[0], args[1], args[2]);
            case 4: return MessageFormat.format(messageFormat, args[0], args[1], args[2], args[3]);
            case 5: return MessageFormat.format(messageFormat, args[0], args[1], args[2], args[3], args[4]);
            case 6: return MessageFormat.format(messageFormat, args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7: return MessageFormat.format(messageFormat, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8: return MessageFormat.format(messageFormat, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9: return MessageFormat.format(messageFormat, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10: return MessageFormat.format(messageFormat, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default: throw new IllegalStateException("need more case!");
            }
        } else {
            return "";
        }
    }

    protected String getMessageFormatFromKey(String key, Locale locale) {
        String messageFormat = null;
        for (String basename : basenames) {
            ResourceBundle resourceBundle = getResourceBundle(basename, locale);
            try {
                messageFormat = resourceBundle.getString(key);
            } catch (MissingResourceException e) {
            }
            if (messageFormat != null) {
                break;
            }
        }
        return messageFormat;
    }

    protected ResourceBundle getResourceBundle(String basename, Locale locale) {
        ResourceBundle resourceBundle = null;
        Map<Locale, ResourceBundle> basenameResourceBundle = resourceBundles.get(basename);
        if (basenameResourceBundle != null) {
            resourceBundle = basenameResourceBundle.get(locale);
        } else {
            resourceBundles.put(basename, new HashMap<Locale, ResourceBundle>());
        }

        if (resourceBundle == null) {
            try {
                resourceBundle = ResourceBundle.getBundle(basename, locale, this.getClass().getClassLoader(), new CustomResourceBundleControl());
            } catch (MissingResourceException e) {
                logger.log(Level.SEVERE, "No resource bundle file for " + basename, e);
                throw e;
            }
            resourceBundles.get(basename).put(locale, resourceBundle);

        }

        return resourceBundle;
    }
}
