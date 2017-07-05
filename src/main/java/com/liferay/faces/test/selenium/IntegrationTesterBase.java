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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;
import com.liferay.faces.test.selenium.browser.internal.BrowserDriverImpl;
import com.liferay.faces.test.selenium.browser.internal.WaitingAsserterImpl;
import com.liferay.faces.test.selenium.webdriver.WebDriverFactory;


/**
 * @author  Kyle Stiemann
 */
public abstract class IntegrationTesterBase {

	// Private Static Data Members (Singletons)
	private static boolean setUp = false;
	private static BrowserDriver browserDriver;
	private static WaitingAsserter waitingAsserter;

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

		waitingAsserter = null;

		if (browserDriver != null) {

			browserDriver.quit();
			browserDriver = null;
		}

		setUp = false;
	}

	@Before
	public final void setUp() {

		if (!setUp) {

			doSetUp();
			setUp = true;
		}
	}

	/**
	 * This method is run once before any tests in order to prepare for testing. The default behavior of this method is
	 * to initialize the default {@link BrowserDriver} and sign in to the container where applicable.
	 */
	protected void doSetUp() {
		signIn(getBrowserDriver());
	}

	/**
	 * Returns an instance of {@link BrowserDriver}. The instance will be closed automatically. The instance may be a
	 * singleton, new instance, or from a pool of BrowserDrivers. To obtain a new instance of BrowserDriver, use {@link
	 * #newBrowserDriver(org.openqa.selenium.WebDriver, boolean, boolean)}.
	 */
	protected final BrowserDriver getBrowserDriver() {

		if (browserDriver == null) {

			String browserName = TestUtil.getSystemPropertyOrDefault("integration.browser.name", "chrome");

			String defaultBrowserHeadlessString = "true";

			// Default to non-headless when running with Firefox or Chrome without maven (in other words running tests
			// from an IDE like Eclipse).
			if ("firefox".equals(browserName) || ("chrome".equals(browserName) && !TestUtil.RUNNING_WITH_MAVEN)) {
				defaultBrowserHeadlessString = "false";
			}

			String browserHeadlessString = TestUtil.getSystemPropertyOrDefault("integration.browser.headless",
					defaultBrowserHeadlessString);
			boolean browserHeadless = Boolean.parseBoolean(browserHeadlessString);
			String browserSimulatingMobileString = TestUtil.getSystemPropertyOrDefault(
					"integration.browser.simulate.mobile", "false");
			boolean browserSimulatingMobile = Boolean.parseBoolean(browserSimulatingMobileString);
			WebDriver webDriver = WebDriverFactory.getWebDriver(browserName, browserHeadless, browserSimulatingMobile);
			browserDriver = newBrowserDriver(webDriver, browserHeadless, browserSimulatingMobile);
		}

		return browserDriver;
	}

	/**
	 * Returns an instance of {@link WaitingAsserter} for the {@link BrowserDriver} obtained from {@link
	 * #getBrowserDriver()}. The instance may be a singleton, new instance, or from a pool of BrowserStateAsserters. To
	 * obtain a new instance of WaitingAsserter, use {@link
	 * #newWaitingAsserter(com.liferay.faces.test.selenium.browser.BrowserDriver)}.
	 */
	protected final WaitingAsserter getWaitingAsserter() {

		if (waitingAsserter == null) {
			waitingAsserter = newWaitingAsserter(getBrowserDriver());
		}

		return waitingAsserter;
	}

	/**
	 * Returns a new instances of {@link BrowserDriver}. The BrowserDriver must be closed (via {@link
	 * BrowserDriver#quit()} or {@link BrowserDriver#closeCurrentWindow()}) by the caller.
	 *
	 * @param  webDriver                The {@link WebDriver} used by the BrowserDriver to drive the browser.
	 * @param  browserHeadless          If true, the browser will run in headless mode.
	 * @param  browserSimulatingMobile  If true, the browser will request pages as a mobile device via its User-Agent.
	 */
	protected final BrowserDriver newBrowserDriver(WebDriver webDriver, boolean browserHeadless,
		boolean browserSimulatingMobile) {
		return new BrowserDriverImpl(webDriver, browserHeadless, browserSimulatingMobile);
	}

	/**
	 * Returns a new instances of {@link WaitingAsserter}.
	 *
	 * @param  browserDriver  The {@link BrowserDriver} which should be used to assert the browser's state.
	 */
	protected final WaitingAsserter newWaitingAsserter(BrowserDriver browserDriver) {
		return new WaitingAsserterImpl(browserDriver);
	}

	protected final void signIn(BrowserDriver browserDriver) {

		String container = TestUtil.getContainer();
		signIn(browserDriver, container);
	}

	protected final void signIn(BrowserDriver browserDriver, String container) {

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

	protected final void signIn(BrowserDriver browserDriver, String signInURL, String loginXpath, String login,
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
