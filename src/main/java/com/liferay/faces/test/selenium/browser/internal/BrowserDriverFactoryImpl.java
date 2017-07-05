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
package com.liferay.faces.test.selenium.browser.internal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverFactory;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;


/**
 * @author  Kyle Stiemann
 */
public class BrowserDriverFactoryImpl extends BrowserDriverFactory {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BrowserDriverFactoryImpl.class);

	// Private Constants
	private static final String IPHONE_7_IOS_10_0_USER_AGENT =
		"Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
	private static final boolean RUNNING_WITH_MAVEN = Boolean.valueOf(TestUtil.getSystemPropertyOrDefault(
				"RUNNING_WITH_MAVEN", "false"));

	@Override
	public BrowserDriver getBrowserDriverImplementation() {

		String browserName = TestUtil.getSystemPropertyOrDefault("integration.browser.name", "chrome");

		String defaultBrowserHeadlessString = "true";

		// Default to non-headless when running with Firefox or Chrome without maven (in other words running tests
		// from an IDE like Eclipse).
		if ("firefox".equals(browserName) || ("chrome".equals(browserName) && !RUNNING_WITH_MAVEN)) {
			defaultBrowserHeadlessString = "false";
		}

		String browserHeadlessString = TestUtil.getSystemPropertyOrDefault("integration.browser.headless",
				defaultBrowserHeadlessString);
		boolean browserHeadless = Boolean.parseBoolean(browserHeadlessString);
		String browserSimulatingMobileString = TestUtil.getSystemPropertyOrDefault(
				"integration.browser.simulate.mobile", "false");
		boolean browserSimulatingMobile = Boolean.parseBoolean(browserSimulatingMobileString);

		return getBrowserDriverImplementation(browserName, browserHeadless, browserSimulatingMobile);
	}

	@Override
	public BrowserDriver getBrowserDriverImplementation(String browserName, boolean browserHeadless,
		boolean browserSimulateMobile) {

		WebDriver webDriver;

		if ("chrome".equals(browserName)) {

			ChromeOptions chromeOptions = new ChromeOptions();
			String chromeBinaryPath = TestUtil.getSystemPropertyOrDefault("webdriver.chrome.bin", null);

			if (chromeBinaryPath != null) {

				chromeOptions.setBinary(chromeBinaryPath);
				logger.info("Chrome Binary: {0}", chromeBinaryPath);
			}

			if (browserHeadless && browserSimulateMobile) {
				throw new UnsupportedOperationException(
					"Headless mode with mobile simulation is not yet supported for Chrome.");
			}

			if (browserHeadless) {

				// The start-maximized argument does not work correctly in headless mode, so set the window size to
				// 1920x1200 (resolution of a 15.4 inch screen).
				chromeOptions.addArguments("headless", "disable-gpu", "window-size=1920,1200");
			}
			else {
				chromeOptions.addArguments("start-maximized");
			}

			if (browserSimulateMobile) {
				chromeOptions.addArguments("user-agent=\"" + IPHONE_7_IOS_10_0_USER_AGENT + "\"");
			}

			webDriver = new ChromeDriver(chromeOptions);
		}
		else if ("firefox".equals(browserName)) {

			// The value of this property is obtained automatically by FirefoxDriver.
			String firefoxBinaryPath = TestUtil.getSystemPropertyOrDefault("webdriver.firefox.bin", null);

			if (firefoxBinaryPath != null) {
				logger.info("Firefox Binary: {0}", firefoxBinaryPath);
			}

			FirefoxProfile firefoxProfile = new FirefoxProfile();

			if (browserHeadless) {
				throw new UnsupportedOperationException("Headless mode is not yet supported for Firefox");
			}

			if (browserSimulateMobile) {
				firefoxProfile.setPreference("general.useragent.override", IPHONE_7_IOS_10_0_USER_AGENT);
			}

			webDriver = new FirefoxDriver(firefoxProfile);
		}
		else if ("phantomjs".equals(browserName)) {

			// The value of this property is obtained automatically by PhantomJSDriver.
			String phantomJSBinaryPath = TestUtil.getSystemPropertyOrDefault("phantomjs.binary.path", null);

			if (phantomJSBinaryPath != null) {
				logger.info("PhantomJS Binary: {0}", phantomJSBinaryPath);
			}

			DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

			if (!browserHeadless) {
				throw new UnsupportedOperationException("Non-headless mode is not yet supported for PhantomJS");
			}

			if (browserSimulateMobile) {

				desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent",
					IPHONE_7_IOS_10_0_USER_AGENT);
				desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX +
					"User-Agent", IPHONE_7_IOS_10_0_USER_AGENT);
			}

			// Set the Accept-Language header to "en-US,en;q=0.8" to ensure that it isn't set to "en-US," (the default).
			desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX +
				"Accept-Language", "en-US,en;q=0.8");

			String phantomJSLogLevel;

			if (logger.isDebugEnabled()) {
				phantomJSLogLevel = "DEBUG";
			}
			else if (logger.isInfoEnabled()) {
				phantomJSLogLevel = "INFO";
			}
			else if (logger.isWarnEnabled()) {
				phantomJSLogLevel = "WARN";
			}
			else if (logger.isErrorEnabled()) {
				phantomJSLogLevel = "ERROR";
			}
			else {
				phantomJSLogLevel = "NONE";
			}

			String[] phantomArgs = new String[1];
			phantomArgs[0] = "--webdriver-loglevel=" + phantomJSLogLevel;
			desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
			webDriver = new PhantomJSDriver(desiredCapabilities);
		}
		else if ("htmlunit".equals(browserName)) {

			if (!browserHeadless) {
				throw new UnsupportedOperationException("Non-headless mode is not yet supported for HtmlUnit");
			}

			if (browserSimulateMobile) {
				webDriver = new HtmlUnitDriverLiferayFacesImpl(IPHONE_7_IOS_10_0_USER_AGENT);
			}
			else {
				webDriver = new HtmlUnitDriverLiferayFacesImpl();
			}
		}
		else if ("jbrowser".equals(browserName)) {

			if (!browserHeadless) {
				throw new UnsupportedOperationException("Non-headless mode is not yet supported for JBrowser");
			}

			if (browserSimulateMobile) {
				throw new UnsupportedOperationException("Mobile simulation is not yet supported for JBrowser.");
			}

			webDriver = new JBrowserDriver();
		}
		else {
			throw new UnsupportedOperationException("Browser with not supported: " + browserName);
		}

		if (!"chrome".equals(browserName)) {
			webDriver.manage().window().maximize();
		}

		return getBrowserDriverImplementation(webDriver, browserHeadless, browserSimulateMobile);
	}

	@Override
	public BrowserDriver getBrowserDriverImplementation(WebDriver webDriver, boolean browserHeadless,
		boolean browserSimulateMobile) {
		return new BrowserDriverImpl(webDriver, browserHeadless, browserSimulateMobile);
	}
}
