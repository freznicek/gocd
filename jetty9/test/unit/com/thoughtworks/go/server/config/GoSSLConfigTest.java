/*************************GO-LICENSE-START*********************************
 * Copyright 2015 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.server.config;

import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.SystemEnvironment;
import org.junit.Test;

import javax.net.ssl.SSLSocketFactory;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoSSLConfigTest {
    @Test
    public void shouldUseWeakConfigWhenSslConfigFlagTurnedOff() {
        GoSSLConfig goSSLConfig = new GoSSLConfig(mock(SSLSocketFactory.class), mock(SystemEnvironment.class));
        SSLConfig config = (SSLConfig) ReflectionUtil.getField(goSSLConfig, "config");
        assertThat(config instanceof WeakSSLConfig, is(true));
    }

    @Test
    public void shouldUseNewImprovedConfigWhenSslConfigFlagTurnedOn() {
        SystemEnvironment systemEnvironment = mock(SystemEnvironment.class);
        when(systemEnvironment.getPropertyImpl("sslconfig")).thenReturn("Y");
        when(systemEnvironment.get(SystemEnvironment.GO_SSL_CONFIG_FILE_PATH)).thenReturn("/ssl.config");
        GoSSLConfig goSSLConfig = new GoSSLConfig(mock(SSLSocketFactory.class), systemEnvironment);
        SSLConfig config = (SSLConfig) ReflectionUtil.getField(goSSLConfig, "config");
        assertThat(config instanceof NewImprovedSSLConfig, is(true));
    }
}