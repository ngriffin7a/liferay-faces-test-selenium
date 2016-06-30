/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.test.selenium;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.selenium.expectedconditions.PageLoaded;


/**
 * @author  Kyle Stiemann
 */
public final class TestUtil {

	// Private Constants
	private static final String SIGN_IN_URL;
	private static final String LOGIN;
	private static final String PASSWORD;

	// Private Xpath
	private static final String loginXpath;
	private static final String passwordXpath;
	private static final String signInButtonXpath;

	// /* package-private */ Constants
	/* package-private */ static final boolean RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN = Boolean.valueOf(TestUtil.getSystemPropertyOrDefault(
				"RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN", "false"));

	// Public Constants
	public static final String BASE_URL;
	public static final String CONTAINER = TestUtil.getSystemPropertyOrDefault("integration.container", "tomcat");
	public static final String DEFAULT_PLUTO_CONTEXT = "/pluto/portal";

	static {

		String defaultSignInContext = "";
		String defaultLoginXpath = "";
		String defaultPasswordXpath = "";
		String defaultSignInButtonXpath = "";
		String defaultLogin = "";
		String defaultPassword = "";

		if (CONTAINER.contains("liferay")) {

			defaultSignInContext = "/c/portal/login";
			defaultLoginXpath = "//input[contains(@id, '_login') and @type='text']";
			defaultPasswordXpath = "//input[contains(@id, '_password') and @type='password']";
			defaultSignInButtonXpath = "//button[contains(., 'Sign In')]";
			defaultLogin = "test@liferay.com";
			defaultPassword = "test";
		}
		else if (CONTAINER.contains("pluto")) {

			defaultSignInContext = DEFAULT_PLUTO_CONTEXT;
			defaultLoginXpath = "//input[contains(@id, '_username')]";
			defaultPasswordXpath = "//input[contains(@id, '_password')]";
			defaultSignInButtonXpath = "//input[contains(@id, '_login')]";
			defaultLogin = "pluto";
			defaultPassword = "pluto";
		}

		String host = TestUtil.getSystemPropertyOrDefault("integration.host", "localhost");
		String port = TestUtil.getSystemPropertyOrDefault("integration.port", "8080");
		String signInContext = TestUtil.getSystemPropertyOrDefault("integration.sign.in.context", defaultSignInContext);

		BASE_URL = "http://" + host + ":" + port;
		SIGN_IN_URL = BASE_URL + signInContext;

		loginXpath = TestUtil.getSystemPropertyOrDefault("integration.login.xpath", defaultLoginXpath);
		passwordXpath = TestUtil.getSystemPropertyOrDefault("integration.password.xpath", defaultPasswordXpath);
		signInButtonXpath = TestUtil.getSystemPropertyOrDefault("integration.sign.in.button.xpath",
				defaultSignInButtonXpath);
		LOGIN = TestUtil.getSystemPropertyOrDefault("integration.login", defaultLogin);
		PASSWORD = TestUtil.getSystemPropertyOrDefault("integration.password", defaultPassword);
	}

	private TestUtil() {
		throw new AssertionError();
	}

	/**
	 * Returns the property value or the default if the property value has not been set or is an empty String. {@link
	 * System#getProperty(java.lang.String, java.lang.String)} only returns the default value when the property has not
	 * been set. Use this method when a default value is preferred for unset properties and properties which have been
	 * set to an empty String.
	 */
	public static String getSystemPropertyOrDefault(String propertyName, String defaultValue) {

		String propertyValue = System.getProperty(propertyName, "");

		if ("".equals(propertyValue)) {
			propertyValue = defaultValue;
		}

		return propertyValue;
	}

	/* package-private */ static void signIn() {

		Browser browser = Browser.getInstance();
		browser.get(SIGN_IN_URL);
		browser.waitForElementPresent(loginXpath);

		WebElement loginElement = browser.findElementByXpath(loginXpath);
		loginElement.clear();
		loginElement.sendKeys(LOGIN);

		WebElement passwordElement = browser.findElementByXpath(passwordXpath);
		passwordElement.clear();
		passwordElement.sendKeys(PASSWORD);
		browser.click(signInButtonXpath);
		browser.waitUntil(ExpectedConditions.stalenessOf(loginElement));
		browser.waitUntil(new PageLoaded());
	}
}
