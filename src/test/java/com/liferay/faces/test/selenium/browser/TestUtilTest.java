/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
 */
package com.liferay.faces.test.selenium.browser;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author  Kyle Stiemann
 */
public class TestUtilTest {

	private static String getDefaultBaseURL(String protocol, String host, String port) throws MalformedURLException,
		ReflectiveOperationException {

		setSystemPropertyIfValueNotNull("integration.protocol", protocol);
		setSystemPropertyIfValueNotNull("integration.host", host);
		setSystemPropertyIfValueNotNull("integration.port", port);

		// Get a new ClassLoader to ensure that TestUtil is re-initialized using the passed system property values.
		ClassLoader classLoader = newTestUtilClassLoader();
		Class<?> clazz = classLoader.loadClass(TestUtil.class.getName());
		Field field = clazz.getDeclaredField("DEFAULT_BASE_URL");

		return (String) field.get(null);
	}

	private static ClassLoader newTestUtilClassLoader() throws MalformedURLException {

		URL testUtilClassPathURL = TestUtil.class.getResource(TestUtil.class.getSimpleName() + ".class");
		String path = testUtilClassPathURL.getPath();
		String protocol = testUtilClassPathURL.getProtocol();
		String host = testUtilClassPathURL.getHost();
		int port = testUtilClassPathURL.getPort();
		String testUtilClassPath = path.replaceAll(TestUtil.class.getName() + ".class", "");

		if (port > -1) {
			testUtilClassPathURL = new URL(protocol, host, port, testUtilClassPath);
		}
		else {
			testUtilClassPathURL = new URL(protocol, host, testUtilClassPath);
		}

		// Use a parent classLoader that doesn't know how to load TestUtil so that the URLClassLoader loads TestUtil
		// instead.
		ClassLoader parentClassLoader = TestUtil.class.getClassLoader();
		parentClassLoader = parentClassLoader.getParent();

		return new URLClassLoader(new URL[] { testUtilClassPathURL }, parentClassLoader);
	}

	private static void setSystemPropertyIfValueNotNull(String systemPropertyName, String systemPropertyValue) {

		if (systemPropertyValue != null) {
			System.setProperty(systemPropertyName, systemPropertyValue);
		}
	}

	@Test
	public void testDefaultBaseURL() throws MalformedURLException, ReflectiveOperationException {

		Assert.assertEquals("http://localhost:8080", getDefaultBaseURL(null, null, null));
		Assert.assertEquals("http://localhost:8080", getDefaultBaseURL("", "", ""));
		Assert.assertEquals("http://localhost:8181", getDefaultBaseURL("", "", "8181"));
		Assert.assertEquals("http://localhost:1", getDefaultBaseURL("", "", "1"));
		Assert.assertEquals("http://localhost:0", getDefaultBaseURL("", "", "0"));
		Assert.assertEquals("http://localhost", getDefaultBaseURL("", "", "-1"));
		Assert.assertEquals("https://liferayfaces.org", getDefaultBaseURL("https", "liferayfaces.org", "-1"));
		Assert.assertEquals("file:///home", getDefaultBaseURL("file", "/home", "-1"));
	}
}
