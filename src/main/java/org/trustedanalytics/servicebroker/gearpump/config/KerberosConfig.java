/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trustedanalytics.servicebroker.gearpump.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.trustedanalytics.hadoop.config.ConfigurationHelper;
import org.trustedanalytics.hadoop.config.ConfigurationHelperImpl;
import org.trustedanalytics.hadoop.config.PropertyLocator;
import org.trustedanalytics.servicebroker.gearpump.kerberos.KerberosProperties;
import org.trustedanalytics.servicebroker.gearpump.kerberos.KerberosService;

import javax.security.auth.login.LoginException;
import java.io.IOException;

@Configuration
public class KerberosConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KerberosConfig.class);
    private static final String DEFAULT_VALUE = "";
    ConfigurationHelper confHelper = ConfigurationHelperImpl.getInstance();

    @Bean
    public KerberosProperties getKerberosProperties() {
        KerberosProperties krbProps = new KerberosProperties();
        krbProps.setKdc(get(PropertyLocator.KRB_KDC));
        krbProps.setRealm(get(PropertyLocator.KRB_REALM));
        krbProps.setUser(get(PropertyLocator.USER));
        krbProps.setPassword(get(PropertyLocator.PASSWORD));

        return krbProps;
    }

    @Bean
    public KerberosService kerberosService() throws IOException, LoginException {
        return new KerberosService(getKerberosProperties());
    }

    String get(PropertyLocator property) {
        try {
            return confHelper.getPropertyFromEnv(property).orElseGet(() -> {
                LOGGER.warn(getErrorMsg(property));
                return DEFAULT_VALUE;
            });
        } catch (Exception e) {
            LOGGER.warn(getErrorMsg(property), e);
            return DEFAULT_VALUE;
        }
    }

    private String getErrorMsg(PropertyLocator property) {
        return property.name() + " not found in VCAP_SERVICES";
    }
}
