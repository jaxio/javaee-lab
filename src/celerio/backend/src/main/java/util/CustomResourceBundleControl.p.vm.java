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
$output.java($Util, "CustomResourceBundleControl")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.require("java.io.BufferedInputStream")##
$output.require("java.io.IOException")##
$output.require("java.io.InputStream")##
$output.require("java.io.InputStreamReader")##
$output.require("java.net.URL")##
$output.require("java.net.URLConnection")##
$output.require("java.util.Collections")##
$output.require("java.util.Enumeration")##
$output.require("java.util.List")##
$output.require("java.util.Locale")##
$output.require("java.util.Properties")##
$output.require("java.util.PropertyResourceBundle")##
$output.require("java.util.ResourceBundle")##
$output.require("org.apache.commons.io.IOUtils")##

/**
 * <ul>
 * <li>handles properties files encoded in UTF-8</li>
 * <li>handles properties in xml files</li>
 * <li>set fallback locale to ROOT instead of default locale</li>
 * </ul>
 */
public class $output.currentClass extends ResourceBundle.Control {

	private static final String ENCODING = "UTF-8";
	protected static final String BUNDLE_EXTENSION = "xml";

	@Override
	public List<String> getFormats(String baseName) {
		List<String> formats = newArrayList(super.getFormats(baseName));
		formats.add(BUNDLE_EXTENSION);
		return formats;
	}

	@Override
	public Locale getFallbackLocale(String baseName, Locale locale) {
		return !Locale.ROOT.equals(locale) ? Locale.ROOT : null;
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException,
			InstantiationException, IOException {
		if ("java.properties".equals(format)) {
			return newBundleInProperties(baseName, locale, loader, reload);
		} else if (BUNDLE_EXTENSION.equals(format)) {
			return newBundleInXml(baseName, locale, loader, reload);
		} else {
			return super.newBundle(baseName, locale, format, loader, reload);
		}
	}

	public ResourceBundle newBundleInXml(String baseName, Locale locale, ClassLoader loader, boolean reload) throws IllegalAccessException,
			InstantiationException, IOException {
		String bundleName = toBundleName(baseName, locale);
		String resourceName = toResourceName(bundleName, BUNDLE_EXTENSION);
		InputStream stream = loadResourceAsStream(loader, reload, resourceName);
		if (stream == null) {
			return null;
		}
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(stream);
			return new XmlResourceBundle(bis);
		} finally {
			IOUtils.closeQuietly(bis);
		}
	}

	private InputStream loadResourceAsStream(ClassLoader loader, boolean reload, String resourceName) throws IOException {
		if (!reload) {
			return loader.getResourceAsStream(resourceName);
		} else {
			URL url = loader.getResource(resourceName);
			if (url == null) {
				return null;
			}
			URLConnection connection = url.openConnection();
			if (connection == null) {
				throw new IllegalArgumentException(resourceName + " could not be opened");
			}
			connection.setUseCaches(false);
			return connection.getInputStream();
		}
	}

	public ResourceBundle newBundleInProperties(String baseName, Locale locale, ClassLoader loader, boolean reload) throws IllegalAccessException,
			InstantiationException, IOException {
		String bundleName = toBundleName(baseName, locale);
		final String resourceName = toResourceName(bundleName, "properties");
		final ClassLoader classLoader = loader;
		final boolean reloadFlag = reload;
		InputStream stream = loadResourceAsStream(classLoader, reloadFlag, resourceName);
		if (stream == null) {
			return null;
		}
		try {
			return new PropertyResourceBundle(new InputStreamReader(stream, ENCODING));
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public class XmlResourceBundle extends ResourceBundle {
		private Properties props;

		XmlResourceBundle(InputStream stream) throws IOException {
			props = new Properties();
			props.loadFromXML(stream);
		}

		@Override
		protected Object handleGetObject(String key) {
			return props.getProperty(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.enumeration(props.stringPropertyNames());
		}
	}
}
