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
$output.java($PrinterSupport, "TypeAwarePrinter")##

$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.requireStatic("org.hibernate.proxy.HibernateProxyHelper.getClassWithoutInitializingProxy")##
$output.require("java.util.Locale")##
$output.require("java.util.Map")##
$output.require("javax.enterprise.inject.Instance")##
$output.require("javax.inject.Inject")##
$output.require("com.jaxio.jpa.querybyexample.LocaleHolder")##

/**
 * Given the type of the object use the corresponding {@link GenericPrinter}, or use {@link ${pound}toString()} method.
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped",'javax.inject.Named("printer")')##
public class $output.currentClass {
	private Map<Class<?>, GenericPrinter<?>> printers = newHashMap();

	@Inject
	void buildCache(Instance<GenericPrinter<?>> registredPrinters) {
		for (GenericPrinter<?> printer : registredPrinters) {
			printers.put(printer.getTarget(), printer);
		}
	}

	public String print(Object object) {
		return print(object, LocaleHolder.getLocale());
	}

    @SuppressWarnings("unchecked")
	public String print(Object object, Locale locale) {
		if (object == null) {
			return "";
		}
 
		// note: getClassWithoutInitializingProxy expects a non null object
		// _HACK_ as we depend on hibernate here.
        @SuppressWarnings("rawtypes")
		GenericPrinter printer = printers.get(getClassWithoutInitializingProxy(object));
		return printer == null ? object.toString() : printer.print(object, locale);
	}
}