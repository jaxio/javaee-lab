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
$output.javaTest($primaryKey.packageName, $primaryKey.testType)##

import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;
$output.require("$primaryKey.fullType")##

/**
 * Basic tests for ${primaryKey.type}
 */
public class $output.currentClass {
#foreach ($pkAttribute in $primaryKey.attributes)
$output.require($pkAttribute)##
    @Test
    public void compositePrimaryKey${pkAttribute.var}_test1() {
        $primaryKey.type cpk = new ${primaryKey.type}();
        assertThat(cpk.${pkAttribute.has}()).isFalse();
        assertThat(cpk.${pkAttribute.getter}()).isNull();
        assertThat(cpk.isEmpty()).isTrue();
    }

    @Test
    public void compositePrimaryKey${pkAttribute.var}_test2() {
        $primaryKey.type cpk = new ${primaryKey.type}().${pkAttribute.with}(${pkAttribute.values.dummy});

        assertThat(cpk.${pkAttribute.has}()).isTrue();
        assertThat(cpk.${pkAttribute.getter}()).isNotNull();
        assertThat(cpk.isEmpty()).isFalse();
    }
#end

    @Test
    public void isEmptyTrue() {
        $primaryKey.type cpk = new ${primaryKey.type}();
        assertThat(cpk.isEmpty()).isTrue();
     }

    @Test
    public void isEmptyFalse() {
        $primaryKey.type cpk = new ${primaryKey.type}();
#foreach ($pkAttribute in $primaryKey.attributes)
        cpk.${pkAttribute.setter}($pkAttribute.values.dummy);
#end
        assertThat(cpk.isEmpty()).isFalse();
     }

    @Test
    public void toStringNotNullWhenNew() {
        $primaryKey.type cpk = new ${primaryKey.type}();
        assertThat(cpk.toString()).isNotNull();
    }

    @Test
    public void toStringHasLength() {
        $primaryKey.type cpk = new ${primaryKey.type}();
#foreach ($pkAttribute in $primaryKey.attributes)
        cpk.${pkAttribute.setter}(${pkAttribute.values.dummy});
#end
        assertThat(cpk.toString()).isNotNull();
        assertThat(cpk.toString().isEmpty()).isFalse();
    }

    @Test
    public void equality_test1() {
        $primaryKey.type cpk = new ${primaryKey.type}();
        assertThat(cpk).isEqualTo(cpk);
        assertThat(cpk.hashCode()).isEqualTo(cpk.hashCode());
        assertThat(cpk.compareTo(cpk)).isZero();
    }

    @Test
    public void equality_test2() {
        $primaryKey.type cpk = new ${primaryKey.type}();
        assertThat(cpk.equals(null)).isFalse();
        assertThat(cpk.compareTo(null)).isEqualTo(-1);
    }

    @Test
    public void equality_test3() {
        $primaryKey.type cpk1 = new ${primaryKey.type}();
        $primaryKey.type cpk2 = new ${primaryKey.type}();
#foreach ($pkAttribute in $primaryKey.attributes)

        ${pkAttribute.type} ${pkAttribute.var} = ${pkAttribute.values.dummy};
        cpk1.${pkAttribute.setter}(${pkAttribute.var});
        cpk2.${pkAttribute.setter}(${pkAttribute.var});
#end

        assertThat(cpk1.hashCode()).isEqualTo(cpk2.hashCode());
        assertThat(cpk1.equals(cpk2)).isTrue();
        assertThat(cpk2.equals(cpk1)).isTrue();
        assertThat(cpk1.compareTo(cpk2)).isZero();
        assertThat(cpk2.compareTo(cpk1)).isZero();
    }
}