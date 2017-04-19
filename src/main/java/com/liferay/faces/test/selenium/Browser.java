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

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;


/**
 * @author  Kyle Stiemann
 */
public class Browser implements WebDriver, JavascriptExecutor {

	// Logger
	private static final Logger logger = Logger.getLogger(Browser.class.getName());

	// Private Static Variables
	private static boolean headless;
	private static Browser instance = null;
	private static WebDriver webDriver = null;
	private static WebDriverWait wait = null;

	static {
		logger.setLevel(TestUtil.getLogLevel());
	}

	// Private Constants
	private final String NAME;

	private Browser() {

		String name = TestUtil.getSystemPropertyOrDefault("integration.browser", "chrome");
		NAME = name.toLowerCase(Locale.ENGLISH);

		if ("chrome".equals(NAME)) {

			ChromeOptions chromeOptions = new ChromeOptions();
			String chromeBinaryPath = TestUtil.getSystemPropertyOrDefault("webdriver.chrome.bin", null);

			if (chromeBinaryPath != null) {

				chromeOptions.setBinary(chromeBinaryPath);
				logger.log(Level.INFO, "Chrome Binary: {0}", chromeBinaryPath);
			}

			chromeOptions.addArguments("start-maximized");

			if (runHeadless(TestUtil.RUNNING_WITH_MAVEN)) {

				headless = true;
				chromeOptions.addArguments("headless");
				chromeOptions.addArguments("disable-gpu");
			}

			webDriver = new ChromeDriver(chromeOptions);
		}
		else if ("firefox".equals(NAME)) {

			String firefoxBinaryPath = TestUtil.getSystemPropertyOrDefault("webdriver.firefox.bin", null);

			if (firefoxBinaryPath != null) {
				logger.log(Level.INFO, "Firefox Binary: {0}", firefoxBinaryPath);
			}

			if (runHeadless(false)) {
				logger.log(Level.WARNING, "Firefox cannot run in headless mode.");
			}

			headless = false;
			webDriver = new FirefoxDriver();
		}
		else if ("phantomjs".equals(NAME)) {

			String phantomJSBinaryPath = TestUtil.getSystemPropertyOrDefault("phantomjs.binary.path", null);

			if (phantomJSBinaryPath != null) {
				logger.log(Level.INFO, "PhantomJS Binary: {0}", phantomJSBinaryPath);
			}

			if (!runHeadless(true)) {
				logger.log(Level.WARNING, "PhantomJS only runs in headless mode.");
			}

			headless = true;

			DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

			// Set the Accept-Language header to "en-US,en;q=0.5" to ensure that it isn't set to "en-US," (the default).
			desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX +
				"Accept-Language", "en-US,en;q=0.8");

			String phantomJSLogLevel;

			Level logLevel = TestUtil.getLogLevel();

			if (logLevel.intValue() == Level.OFF.intValue()) {
				phantomJSLogLevel = "NONE";
			}
			else if (logLevel.intValue() > Level.WARNING.intValue()) {
				phantomJSLogLevel = "ERROR";
			}
			else if (logLevel.intValue() > Level.INFO.intValue()) {
				phantomJSLogLevel = "WARN";
			}
			else if (logLevel.intValue() > Level.CONFIG.intValue()) {
				phantomJSLogLevel = "INFO";
			}
			else { // if (logLevel.intValue() <= Level.CONFIG.intValue())
				phantomJSLogLevel = "DEBUG";
			}

			String[] phantomArgs = new String[1];
			phantomArgs[0] = "--webdriver-loglevel=" + phantomJSLogLevel;
			desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
			webDriver = new PhantomJSDriver(desiredCapabilities);
		}
		else if ("htmlunit".equals(NAME)) {

			if (!runHeadless(true)) {
				logger.log(Level.WARNING, "HtmlUnit only runs in headless mode.");
			}

			headless = true;
			webDriver = new HtmlUnitDriverLiferayFacesImpl(BrowserVersion.FIREFOX_45, true);
		}
		else if ("jbrowser".equals(NAME)) {

			if (!runHeadless(true)) {
				logger.log(Level.WARNING, "JBrowser only runs in headless mode.");
			}

			headless = true;
			webDriver = new JBrowserDriver();
		}

		if (!"chrome".equals(NAME)) {
			webDriver.manage().window().maximize();
		}

		wait = new WebDriverWait(webDriver, TestUtil.getBrowserWaitTimeOut());
	}

	public static Browser getInstance() {

		if (instance == null) {
			instance = new Browser();
		}

		return instance;
	}

	/**
	 * Calls {@link #captureCurrentPageState(java.lang.String, java.lang.String)}. The default output directory is
	 * <code> {@link TestUtil#JAVA_IO_TMPDIR} + "selenium"</code>. This can be changed with the <code>
	 * "integration.selenium.output.directory"</code> system property. No file name prefix is used.
	 *
	 * @see  {@link #captureCurrentPageState(java.lang.String, java.lang.String)}
	 */
	public void captureCurrentPageState() {

		String outputDirectoryPath = TestUtil.getSystemPropertyOrDefault("integration.selenium.output.directory",
				TestUtil.JAVA_IO_TMPDIR + "selenium");
		captureCurrentPageState(outputDirectoryPath, null);
	}

	/**
	 * Captures the current page markup (using {@link #getCurrentPageMarkup()}) to a file, and if the browser supports
	 * it, captures a screenshot to a file as well. This method logs the file locations for the benefit of the tester.
	 *
	 * @param  outputDirectoryPath  Path where the captured page state should be generated.
	 * @param  fileNamePrefix       String to prepend to each html (and potentially screenshot) file name.
	 */
	public void captureCurrentPageState(String outputDirectoryPath, String fileNamePrefix) {

		File file = new File(outputDirectoryPath);
		file.mkdirs();

		StringBuilder buf = new StringBuilder();
		buf.append(outputDirectoryPath);
		buf.append("/");

		if (fileNamePrefix != null) {

			buf.append(fileNamePrefix);
			buf.append("_");
		}

		buf.append(getName());
		buf.append("_");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.ENGLISH);
		simpleDateFormat.setTimeZone(TimeZone.getDefault());

		String dateString = simpleDateFormat.format(new Date());
		buf.append(dateString);

		String fileName = buf.toString();
		String htmlFileName = fileName + ".html";
		PrintWriter printWriter = null;

		try {

			printWriter = new PrintWriter(htmlFileName, "UTF-8");

			String currentPageState = getCurrentPageMarkup();
			printWriter.write(currentPageState);
		}
		catch (Exception e) {

			logger.log(Level.SEVERE, "Unable to write page source to {0} due to the following exception:\n",
				new Object[] { htmlFileName });
			logger.log(Level.SEVERE, "", e);
		}
		finally {
			close(printWriter);
		}

		String currentUrl = getCurrentUrl();
		logger.log(Level.INFO, "The html of url=\"{0}\" has been written to {1}",
			new Object[] { currentUrl, htmlFileName });

		if (webDriver instanceof TakesScreenshot) {

			TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
			byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
			String screenshotFileName = fileName + ".png";
			FileOutputStream fileOutputStream = null;

			try {

				fileOutputStream = new FileOutputStream(screenshotFileName);
				fileOutputStream.write(screenshotBytes);
			}
			catch (Exception e) {

				logger.log(Level.SEVERE, "Unable to write page source to {0} due to the following exception:\n",
					new Object[] { screenshotFileName });
				logger.log(Level.SEVERE, "", e);
			}
			finally {
				close(fileOutputStream);
			}

			logger.log(Level.INFO, "A screenshot of url=\"{0}\" has been saved to {1}",
				new Object[] { currentUrl, screenshotFileName });
		}
	}

	public void centerElementInView(String xpath) {

		// http://stackoverflow.com/questions/8922107/javascript-scrollintoview-middle-alignment#36499256
		executeScript(
			"window.scrollTo(0, (arguments[0].getBoundingClientRect().top + window.pageYOffset) - (window.innerHeight / 2))",
			findElementByXpath(xpath));
	}

	public void clear(String xpath) {

		WebElement element = findElementByXpath(xpath);
		String value = element.getAttribute("value");

		if ((value != null) && !value.equals("")) {

			CharSequence[] clearKeys = new CharSequence[value.length()];

			for (int i = 0; i < value.length(); i++) {
				clearKeys[i] = Keys.BACK_SPACE;
			}

			sendKeys(xpath, Keys.END);
			sendKeys(xpath, clearKeys);
		}
	}

	public void click(String xpath) {

		centerElementInView(xpath);
		findElementByXpath(xpath).click();
	}

	/**
	 * Clicks on the element specified via xpath and waits for the clicked element to be rerendered via Ajax. This
	 * method will only work if the element clicked is also rerendered via Ajax. If the clicked element will not be
	 * rerendered via Ajax, then use {@link
	 * Browser#performAndWaitForAjaxRerender(org.openqa.selenium.interactions.Action, java.lang.String)} with {@link
	 * Browser#createClickAction(java.lang.String)} and the xpath of an element which will be rerendered instead.
	 *
	 * @param  xpath  The xpath of the element to be clicked and rerendered.
	 */
	public void clickAndWaitForAjaxRerender(String xpath) {

		centerElementInView(xpath);
		performAndWaitForAjaxRerender(createClickAction(xpath), xpath);
	}

	@Override
	public void close() {
		webDriver.close();
	}

	// Currently unused:
	public Actions createActions() {
		return new Actions(webDriver);
	}

	public Action createClickAction(String xpath) {

		Actions actions = createActions();
		WebElement element = findElementByXpath(xpath);
		actions.moveToElement(element);
		actions.click(element);

		return actions.build();
	}

	@Override
	public Object executeAsyncScript(String script, Object... args) {

		JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;

		return javascriptExecutor.executeAsyncScript(script, args);
	}

	@Override
	public Object executeScript(String script, Object... args) {

		JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;

		return javascriptExecutor.executeScript(script, args);
	}

	@Override
	public WebElement findElement(By by) {
		return webDriver.findElement(by);
	}

	public WebElement findElementByXpath(String xpath) {
		return findElement(By.xpath(xpath));
	}

	@Override
	public List<WebElement> findElements(By by) {
		return webDriver.findElements(by);
	}

	@Override
	public void get(String url) {

		logger.log(Level.INFO, "Navigating to: {0}", url);
		webDriver.get(url);
	}

	public Capabilities getCapabilities() {

		RemoteWebDriver remoteWebDriver = (RemoteWebDriver) webDriver;

		return remoteWebDriver.getCapabilities();
	}

	/**
	 * @return  The current HTML of the entire page.
	 */
	public String getCurrentPageMarkup() {

		WebElement documentElement = findElementByXpath("/html");
		String outerHTMLAttrName = "outerHTML";

		// https://github.com/SeleniumHQ/htmlunit-driver/issues/45
		if ("htmlunit".equals(getName())) {
			outerHTMLAttrName = "innerHTML";
		}

		return documentElement.getAttribute(outerHTMLAttrName);
	}

	@Override
	public String getCurrentUrl() {
		return webDriver.getCurrentUrl();
	}

	public String getName() {
		return NAME;
	}

	/**
	 * @return  The HTML of the entire page. The returned HTML may not reflect JavaScript modifications. Consider using
	 *          {@link #getCurrentPageMarkup()} instead if you want the HTML markup after JavaScript has modified it.
	 */
	@Override
	public String getPageSource() {
		return webDriver.getPageSource();
	}

	@Override
	public String getTitle() {
		return webDriver.getTitle();
	}

	@Override
	public String getWindowHandle() {
		return webDriver.getWindowHandle();
	}

	@Override
	public Set<String> getWindowHandles() {
		return webDriver.getWindowHandles();
	}

	public boolean isHeadless() {
		return headless;
	}

	/**
	 * Load all images on the page. If the browser loads images automatically, this method does nothing.
	 */
	public void loadImages() {

		if (NAME.equals("htmlunit")) {

			HtmlUnitDriverLiferayFacesImpl htmlUnitDriverLiferayFacesImpl = (HtmlUnitDriverLiferayFacesImpl) webDriver;
			htmlUnitDriverLiferayFacesImpl.loadImages();
		}
		else {
			logger.log(Level.WARNING, "Images are loaded automatically for this browser.");
		}
	}

	@Override
	public Options manage() {
		return webDriver.manage();
	}

	@Override
	public Navigation navigate() {
		return webDriver.navigate();
	}

	/**
	 * Performs an {@link Action} and waits for an element to be rerendered via Ajax.
	 *
	 * @param  action         The action which will cause the Ajax rerender.
	 * @param  rerenderXpath  The xpath of the element which will be rerendered.
	 */
	public void performAndWaitForAjaxRerender(Action action, String rerenderXpath) {

		WebElement rerenderElement = findElementByXpath(rerenderXpath);
		action.perform();
		logger.log(Level.INFO, "Waiting for element {0} to be stale.", rerenderXpath);
		waitUntil(ExpectedConditions.stalenessOf(rerenderElement));
		logger.log(Level.INFO, "Element {0} is stale.", rerenderXpath);
		waitForElementVisible(rerenderXpath);
	}

	@Override
	public void quit() {
		webDriver.quit();
	}

	public void sendKeys(String xpath, CharSequence... keys) {
		findElementByXpath(xpath).sendKeys(keys);
	}

	public void setWaitTimeOut(Integer waitTimeOutInSeconds) {
		wait = new WebDriverWait(webDriver, waitTimeOutInSeconds);
	}

	@Override
	public TargetLocator switchTo() {
		return webDriver.switchTo();
	}

	// Currently unused:
	public void waitForElementNotPresent(String xpath) {

		logger.log(Level.INFO, "Waiting for element {0} to not be present in the DOM.", xpath);
		waitUntil(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.ByXPath.xpath(xpath))));
		logger.log(Level.INFO, "Element {0} is not present in the DOM.", xpath);
	}

	public void waitForElementPresent(String xpath) {

		logger.log(Level.INFO, "Waiting for element {0} to be present in the DOM.", xpath);
		waitUntil(ExpectedConditions.presenceOfElementLocated(By.ByXPath.xpath(xpath)));
		logger.log(Level.INFO, "Element {0} is present in the DOM.", xpath);
	}

	// Currently unused:
	public void waitForElementTextVisible(String xpath, String text) {

		String[] loggerArgs = new String[] { xpath, text };
		waitForElementVisible(xpath);
		logger.log(Level.INFO, "Waiting for element {0} to contain text \"{1}\".", loggerArgs);
		waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.ByXPath.xpath(xpath), text));
		logger.log(Level.INFO, "Element {0} is visible and contains text \"{1}\".", loggerArgs);
	}

	// Currently unused:
	public void waitForElementValue(String xpath, String value) {

		String[] loggerArgs = new String[] { xpath, value };
		waitForElementVisible(xpath);
		logger.log(Level.INFO, "Waiting for element {0} to contain value \"{1}\".", loggerArgs);
		waitUntil(ExpectedConditions.textToBePresentInElementValue(By.ByXPath.xpath(xpath), value));
		logger.log(Level.INFO, "Element {0} is visible and contains value \"{1}\".", loggerArgs);
	}

	public void waitForElementVisible(String xpath) {

		logger.log(Level.INFO, "Waiting for element {0} to be visible.", xpath);
		waitUntil(ExpectedConditions.visibilityOfElementLocated(By.ByXPath.xpath(xpath)));
		logger.log(Level.INFO, "Element {0} is visible.", xpath);
	}

	public void waitUntil(ExpectedCondition expectedCondition) {
		wait.until(expectedCondition);
	}

	private void close(Closeable closeable) {

		if (closeable != null) {

			try {
				closeable.close();
			}
			catch (IOException e) {
				// do nothing.
			}
		}
	}

	private boolean runHeadless(boolean runHeadlessDefault) {

		String headlessString = TestUtil.getSystemPropertyOrDefault("integration.browser.headless",
				Boolean.toString(runHeadlessDefault));

		return Boolean.parseBoolean(headlessString);
	}
}
