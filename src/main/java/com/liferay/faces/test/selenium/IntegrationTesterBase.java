/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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

import org.junit.AfterClass;
import org.junit.Before;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * @author  Kyle Stiemann
 */
public abstract class IntegrationTesterBase {

	// Private Static Data Members
	private static boolean setUp = false;

	/**
	 * {@link IntegrationTestSuiteListener#testRunFinished(org.junit.runner.Result)} is used to shut down the
	 * browser/webDriver when the tests are run with the maven. However, {@link
	 * IntegrationTestSuiteListener#testRunFinished(org.junit.runner.Result)} is not called when the tests are not run
	 * with the maven (i.e. when the tests are run from an IDE). So when the tests are run from an IDE, it is necessary
	 * to shutdown the browser after each test class is run.
	 */
	@AfterClass
	public static void tearDown() {

		if (!TestUtil.RUNNING_WITH_MAVEN) {
			doTearDown();
		}
	}

	protected static void doTearDown() {

		// When the browser is phantomjs or chrome, WebDriver.close() does not quit the browser (like it is
		// supposed to
		// http://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebDriver.html#close--),
		// so use WebDriver.quit() instead.
		Browser.getInstance().quit();
	}

	@Before
	public final void setUp() {

		if (!setUp) {

			doSetUp();
			setUp = true;
		}
	}

	protected void doSetUp() {
		signIn(Browser.getInstance());
	}

	protected final void signIn(Browser browser) {

		String container = TestUtil.getContainer();
		signIn(browser, container);
	}

	protected final void signIn(Browser browser, String container) {

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
		signIn(browser, signInURL, loginXpath, login, passwordXpath, password, signInButtonXpath);
	}

	protected final void signIn(Browser browser, String signInURL, String loginXpath, String login,
		String passwordXpath, String password, String signInButtonXpath) {

		browser.get(signInURL);
		browser.waitForElementEnabled(loginXpath);
		browser.clear(loginXpath);
		browser.sendKeys(loginXpath, login);
		browser.clear(passwordXpath);
		browser.sendKeys(passwordXpath, password);

		WebElement loginElement = browser.findElementByXpath(loginXpath);
		browser.click(signInButtonXpath);
		browser.waitUntil(ExpectedConditions.stalenessOf(loginElement));
		browser.waitForElementDisplayed("//body");
	}
}
