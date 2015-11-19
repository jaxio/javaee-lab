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
$output.java($Service, "EnvironmentService")##

$output.requireStatic($Service, "EnvironmentService.Environment.Development")##
$output.requireStatic($Service, "EnvironmentService.Environment.Integration")##
$output.requireStatic($Service, "EnvironmentService.Environment.Production")##
$output.requireStatic($Service, "EnvironmentService.Environment.toEnvironment")##
$output.requireStatic("org.apache.commons.lang.StringUtils.trimToEmpty")##
$output.require("javax.inject.Inject")##
$output.require("org.apache.deltaspike.core.api.config.ConfigProperty")##

$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class EnvironmentService {

    @Inject
    @ConfigProperty(name="env_name", defaultValue="development" )
    private String environmentName;
    
    public enum Environment {
        Development, Integration, Production;
        boolean is(String value) {
            return name().equalsIgnoreCase(trimToEmpty(value));
        }

        public static Environment toEnvironment(String value) {
            for (Environment environment : values()) {
                if (environment.is(value)) {
                    return environment;
                }
            }
            return Development;
        }
    }

    public boolean isDevelopment() {
        return Development.is(environmentName);
    }

    public boolean isIntegration() {
        return Integration.is(environmentName);
    }

    public boolean isProduction() {
        return Production.is(environmentName);
    }

    public Environment getEnvironment() {
        return toEnvironment(environmentName);
    }
}