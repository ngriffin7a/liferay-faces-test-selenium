/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * @author  Kyle Stiemann
 */
public final class TestUtil {

	// Public Constants
	public static final String DEFAULT_BASE_URL = "http://" + TestUtil.getHost() + ":" + TestUtil.getPort();
	public static final String DEFAULT_PLUTO_CONTEXT = "/pluto/portal";

	private TestUtil() {
		throw new AssertionError();
	}

	public static int getBrowserDriverWaitTimeOut() {
		return TestUtil.getBrowserDriverWaitTimeOut(5);
	}

	public static int getBrowserDriverWaitTimeOut(Integer defaultTimeOutInSeconds) {

		String defaultTimeOutInSecondsString = defaultTimeOutInSeconds.toString();
		String timeOutInSecondsString = getSystemPropertyOrDefault("integration.browser.driver.wait.time.out",
				defaultTimeOutInSecondsString);

		return Integer.parseInt(timeOutInSecondsString);
	}

	public static String getContainer() {
		return getContainer("liferay");
	}

	public static String getContainer(String defaultContainer) {
		return getSystemPropertyOrDefault("integration.container", defaultContainer);
	}

	public static String getHost() {
		return getHost("localhost");
	}

	public static String getHost(String defaultHost) {
		return getSystemPropertyOrDefault("integration.host", defaultHost);
	}

	public static String getPort() {
		return getPort("8080");
	}

	public static String getPort(String defaultPort) {
		return getSystemPropertyOrDefault("integration.port", defaultPort);
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

	public static void signIn(BrowserDriver browserDriver) {

		String container = TestUtil.getContainer();
		signIn(browserDriver, container);
	}

	public static void signIn(BrowserDriver browserDriver, String container) {

		// Set up sign-in constants.
		String defaultSignInContext = "";
		String defaultLoginXpath = "";
		String defaultPasswordXpath = "";
		String defaultSignInButtonXpath = "";
		String defaultLogin = "";
		String defaultPassword = "";

		if (container.contains("liferay")) {

			defaultSignInContext = "/c/portal/login";
			defaultLoginXpath = "//input[contains(@id, '_login') and @type='text']";
			defaultPasswordXpath = "//input[contains(@id, '_password') and @type='password']";
			defaultSignInButtonXpath = "//button[contains(., 'Sign In')]";
			defaultLogin = "test@liferay.com";
			defaultPassword = "test";
		}
		else if (container.contains("pluto")) {

			defaultSignInContext = TestUtil.DEFAULT_PLUTO_CONTEXT;
			defaultLoginXpath = "//input[contains(@id, '_username')]";
			defaultPasswordXpath = "//input[contains(@id, '_password')]";
			defaultSignInButtonXpath = "//input[contains(@id, '_login')]";
			defaultLogin = "pluto";
			defaultPassword = "pluto";
		}

		String signInContext = TestUtil.getSystemPropertyOrDefault("integration.sign.in.context", defaultSignInContext);
		String signInURL = TestUtil.DEFAULT_BASE_URL + signInContext;
		String loginXpath = TestUtil.getSystemPropertyOrDefault("integration.login.xpath", defaultLoginXpath);
		String passwordXpath = TestUtil.getSystemPropertyOrDefault("integration.password.xpath", defaultPasswordXpath);
		String signInButtonXpath = TestUtil.getSystemPropertyOrDefault("integration.sign.in.button.xpath",
				defaultSignInButtonXpath);
		String login = TestUtil.getSystemPropertyOrDefault("integration.login", defaultLogin);
		String password = TestUtil.getSystemPropertyOrDefault("integration.password", defaultPassword);
		signIn(browserDriver, signInURL, loginXpath, login, passwordXpath, password, signInButtonXpath);
	}

	public static void signIn(BrowserDriver browserDriver, String signInURL, String loginXpath, String login,
		String passwordXpath, String password, String signInButtonXpath) {

		browserDriver.navigateWindowTo(signInURL);
		browserDriver.waitForElementEnabled(loginXpath);
		browserDriver.clearElement(loginXpath);
		browserDriver.sendKeysToElement(loginXpath, login);
		browserDriver.clearElement(passwordXpath);
		browserDriver.sendKeysToElement(passwordXpath, password);

		WebElement loginElement = browserDriver.findElementByXpath(loginXpath);
		browserDriver.clickElement(signInButtonXpath);
		browserDriver.waitFor(ExpectedConditions.stalenessOf(loginElement));
		browserDriver.waitForElementDisplayed("//body");
	}
}
