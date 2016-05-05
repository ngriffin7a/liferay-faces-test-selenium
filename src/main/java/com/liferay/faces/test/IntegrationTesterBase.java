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
package com.liferay.faces.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.expectedconditions.PageLoaded;


/**
 * @author  Kyle Stiemann
 */
public abstract class IntegrationTesterBase {

	// Private Constants
	private static final String SIGN_IN_URL;
	private static final String loginXpath;
	private static final String passwordXpath;
	private static final String signInButtonXpath;
	private static final String login;
	private static final String password;

	// /* package-private */ Constants
	/* package-private */ static final boolean RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN = Boolean.valueOf(TestUtil.getSystemPropertyOrDefault(
				"RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN", "false"));

	// Protected Constants
	protected static final String BASE_URL;
	protected static final String CONTAINER = TestUtil.getSystemPropertyOrDefault("integration.container", "tomcat");
	protected static final String DEFAULT_PLUTO_CONTEXT = "/pluto/portal";

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
		login = TestUtil.getSystemPropertyOrDefault("integration.login", defaultLogin);
		password = TestUtil.getSystemPropertyOrDefault("integration.password", defaultPassword);
	}

	/**
	 * {@link TestSuiteListener#testRunFinished()} is used to shut down the browser/webDriver when the tests are run
	 * with the maven-surefire-plugin. However, {@link TestSuiteListener#testRunFinished()} is not called when the tests
	 * are not run with the maven-surefire-plugin (i.e. when the tests are run from an IDE). So when the tests are run
	 * from an IDE, it is necessary to shutdown the browser after each test class is run.
	 */
	@AfterClass
	public static void tearDown() {

		if (!RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN) {

			// When the browser is phantomjs or chrome, WebDriver.close() does not quit the browser (like it is
			// supposed to
			// https://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/WebDriver.html#quit%28%29),
			// so we use WebDriver.quit() instead.
			Browser.getInstance().quit();
		}
	}

	/* package-private */ static void signIn() {

		Browser browser = Browser.getInstance();
		browser.navigateToURL(SIGN_IN_URL);
		browser.waitForElementPresent(loginXpath);

		WebElement loginElement = browser.getElement(loginXpath);
		loginElement.clear();
		loginElement.sendKeys(login);

		WebElement passwordElement = browser.getElement(passwordXpath);
		passwordElement.clear();
		passwordElement.sendKeys(password);
		browser.click(signInButtonXpath);
		browser.waitUntil(ExpectedConditions.and(ExpectedConditions.stalenessOf(loginElement), new PageLoaded()));
	}

	/**
	 * {@link TestSuiteListener#testRunStarted()} is used to sign in to the container when the tests are run with the
	 * maven-surefire-plugin. However, {@link TestSuiteListener#testRunStarted()} is not called when the tests are not
	 * run with the maven-surefire-plugin (i.e. when the tests are run from an IDE). So when the tests are run from an
	 * IDE, it is necessary to sign in to the container before each test class is run.
	 */
	@BeforeClass
	public static void setUp() {

		if (!RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN && !"tomcat".equals(CONTAINER)) {
			signIn();
		}
	}
}
