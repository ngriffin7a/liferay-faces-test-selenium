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
package com.liferay.faces.test.selenium.browser.internal;

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
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.expectedconditions.ElementEnabled;
import com.liferay.faces.test.selenium.util.ClosableUtil;


/**
 * @author  Kyle Stiemann
 */
public class BrowserDriverImpl implements BrowserDriver {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BrowserDriverImpl.class);

	// Private Constants
	private static final String JAVA_IO_TMPDIR;

	static {

		String javaIOTmpdir = System.getProperty("java.io.tmpdir");

		if (!javaIOTmpdir.endsWith(File.separator)) {
			javaIOTmpdir += File.separator;
		}

		JAVA_IO_TMPDIR = javaIOTmpdir;
	}

	// Private Member Variables
	private boolean browserHeadless;
	private boolean browserSimulatingMobile;
	private WebDriver webDriver;
	private WebDriverWait webDriverWait;
	private Integer windowHeight;

	public BrowserDriverImpl(WebDriver webDriver, boolean browserHeadless, boolean browserSimulatingMobile) {

		this.webDriver = webDriver;
		this.browserHeadless = browserHeadless;
		this.browserSimulatingMobile = browserSimulatingMobile;

		int browserWaitTimeOut = TestUtil.getBrowserDriverWaitTimeOut();
		this.webDriverWait = new WebDriverWait(webDriver, browserWaitTimeOut);
	}

	@Override
	public void acceptAlert() {

		WebDriver webDriver = getWebDriver();
		webDriver.switchTo().alert().accept();
	}

	@Override
	public void captureCurrentBrowserState() {

		String outputDirectoryPath = TestUtil.getSystemPropertyOrDefault(
				"integration.captured.browser.state.output.directory", JAVA_IO_TMPDIR + "captured-browser-state");
		captureCurrentBrowserState(outputDirectoryPath, null);
	}

	@Override
	public void captureCurrentBrowserState(String outputDirectoryPath, String fileNamePrefix) {

		File file = new File(outputDirectoryPath);
		file.mkdirs();

		StringBuilder buf = new StringBuilder();
		buf.append(outputDirectoryPath);
		buf.append("/");

		if (fileNamePrefix != null) {

			buf.append(fileNamePrefix);
			buf.append("_");
		}

		buf.append(getBrowserName());
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

			String currentPageState = getCurrentDocumentMarkup();
			printWriter.write(currentPageState);
		}
		catch (Exception e) {

			logger.error("Unable to write page source to {} due to the following exception:\n", htmlFileName);
			logger.error("", e);
		}
		finally {
			ClosableUtil.close(printWriter);
		}

		String currentUrl = getCurrentWindowUrl();
		logger.info("The html of url=\"{}\" has been written to {}", currentUrl, htmlFileName);

		WebDriver webDriver = getWebDriver();

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

				logger.error("Unable to write page source to {} due to the following exception:\n", screenshotFileName);
				logger.error("", e);
			}
			finally {
				ClosableUtil.close(fileOutputStream);
			}

			logger.info("A screenshot of url=\"{}\" has been saved to {}", currentUrl, screenshotFileName);
		}
	}

	@Override
	public void centerElementInCurrentWindow(String elementXpath) {

		// http://stackoverflow.com/questions/8922107/javascript-scrollintoview-middle-alignment#36499256
		executeScriptInCurrentWindow(
			"window.scrollTo(0, (arguments[0].getBoundingClientRect().top + window.pageYOffset) - (window.innerHeight / 2));",
			findElementByXpath(elementXpath));
	}

	@Override
	public void clearBrowserCookies() {

		WebDriver webDriver = getWebDriver();
		webDriver.manage().deleteAllCookies();
	}

	@Override
	public void clearElement(String elementXpath) {

		centerElementInCurrentWindow(elementXpath);

		WebElement element = findElementByXpath(elementXpath);
		String value = element.getAttribute("value");

		if ((value != null) && !value.equals("")) {

			CharSequence[] clearKeys = new CharSequence[value.length()];

			for (int i = 0; i < value.length(); i++) {
				clearKeys[i] = Keys.BACK_SPACE;
			}

			sendKeysToElement(elementXpath, Keys.END);
			sendKeysToElement(elementXpath, clearKeys);
		}
	}

	@Override
	public void clickElement(String elementXpath) {

		centerElementInCurrentWindow(elementXpath);
		findElementByXpath(elementXpath).click();
	}

	@Override
	public void clickElementAndWaitForRerender(String elementXpath) {
		performAndWaitForRerender(createClickElementAction(elementXpath), elementXpath);
	}

	@Override
	public void closeCurrentWindow() {

		WebDriver webDriver = getWebDriver();
		webDriver.close();
	}

	@Override
	public Actions createActions() {

		WebDriver webDriver = getWebDriver();

		return new Actions(webDriver);
	}

	@Override
	public Actions createActions(String elementXpath) {

		if (windowHeight == null) {
			Long windowHeightLong = (Long) executeScriptInCurrentWindow(
					"return document.documentElement.clientHeight || window.innerHeight;");
			windowHeight = windowHeightLong.intValue();
		}

		WebElement webElement = findElementByXpath(elementXpath);
		Dimension elementSize = webElement.getSize();
		int elementHeight = elementSize.getHeight();
		Actions actions = createActions();

		return actions.moveToElement(webElement, 0, -(windowHeight / 2) + (elementHeight / 2));
	}

	@Override
	public Action createClickElementAction(String elementXpath) {

		Actions actions = createActions(elementXpath);
		WebElement webElement = findElementByXpath(elementXpath);
		actions = actions.click(webElement);

		return actions.build();
	}

	@Override
	public void dismissAlert() {

		WebDriver webDriver = getWebDriver();
		webDriver.switchTo().alert().dismiss();
	}

	@Override
	public Object executeScriptInCurrentWindow(String script, Object... args) {

		WebDriver webDriver = getWebDriver();

		if (webDriver instanceof JavascriptExecutor) {

			JavascriptExecutor javaScriptExecutor = (JavascriptExecutor) webDriver;

			return javaScriptExecutor.executeScript(script, args);
		}
		else {
			throw new UnsupportedOperationException("Executing JavaScript is not supported by: " + getBrowserName());
		}
	}

	@Override
	public WebElement findElementByXpath(String elementXpath) {

		WebDriver webDriver = getWebDriver();

		return webDriver.findElement(By.xpath(elementXpath));
	}

	@Override
	public List<WebElement> findElementsByXpath(String elementXpath) {

		WebDriver webDriver = getWebDriver();

		return webDriver.findElements(By.xpath(elementXpath));
	}

	@Override
	public Set<Cookie> getBrowserCookies() {

		WebDriver webDriver = getWebDriver();

		return webDriver.manage().getCookies();
	}

	@Override
	public String getBrowserName() {

		WebDriver webDriver = getWebDriver();

		if (webDriver instanceof RemoteWebDriver) {

			RemoteWebDriver remoteWebDriver = (RemoteWebDriver) webDriver;

			return remoteWebDriver.getCapabilities().getBrowserName();
		}
		else {
			throw new UnsupportedOperationException("getBrowserName() not supported by this browser.");
		}
	}

	@Override
	public String getCurrentDocumentMarkup() {

		WebElement documentElement = findElementByXpath("/html");
		String outerHTMLAttrName = "outerHTML";
		String browserName = getBrowserName();

		// https://github.com/SeleniumHQ/htmlunit-driver/issues/45
		if ("htmlunit".equals(browserName)) {
			outerHTMLAttrName = "innerHTML";
		}

		return documentElement.getAttribute(outerHTMLAttrName);
	}

	@Override
	public String getCurrentWindowId() {

		WebDriver webDriver = getWebDriver();

		return webDriver.getWindowHandle();
	}

	@Override
	public String getCurrentWindowUrl() {

		WebDriver webDriver = getWebDriver();

		return webDriver.getCurrentUrl();
	}

	public WebDriver getWebDriver() {
		return webDriver;
	}

	@Override
	public Set<String> getWindowIds() {

		WebDriver webDriver = getWebDriver();

		return webDriver.getWindowHandles();
	}

	@Override
	public boolean isBrowserHeadless() {
		return browserHeadless;
	}

	@Override
	public boolean isBrowserSimulatingMobile() {
		return browserSimulatingMobile;
	}

	@Override
	public void loadCurrentWindowImages() {

		String browserName = getBrowserName();

		if ("htmlunit".equals(browserName)) {

			WebDriver webDriver = getWebDriver();
			HtmlUnitDriverLiferayFacesImpl htmlUnitDriverLiferayFacesImpl = (HtmlUnitDriverLiferayFacesImpl) webDriver;
			htmlUnitDriverLiferayFacesImpl.loadCurrentWindowImages();
		}
		else {
			logger.warn("Images are automatically loaded by {}", browserName);
		}
	}

	@Override
	public void navigateWindowTo(String url) {

		WebDriver webDriver = getWebDriver();
		webDriver.get(url);
	}

	@Override
	public void performAndWaitForRerender(Action action, String rerenderXpath) {

		WebElement rerenderElement = findElementByXpath(rerenderXpath);
		action.perform();
		logger.info("Waiting for element {} to be stale.", rerenderXpath);
		waitFor(ExpectedConditions.stalenessOf(rerenderElement));
		logger.info("Element {} is stale.", rerenderXpath);
		waitForElementDisplayed(rerenderXpath);
	}

	@Override
	public void quit() {

		WebDriver webDriver = getWebDriver();
		webDriver.quit();
	}

	@Override
	public void sendKeysToElement(String elementXpath, CharSequence... keys) {

		centerElementInCurrentWindow(elementXpath);
		findElementByXpath(elementXpath).sendKeys(keys);
	}

	@Override
	public void setPageLoadTimeout(int waitTimeOutInSeconds) {

		WebDriver webDriver = getWebDriver();
		webDriver.manage().timeouts().pageLoadTimeout(waitTimeOutInSeconds, TimeUnit.SECONDS);
	}

	@Override
	public void setWaitTimeOut(int waitTimeOutInSeconds) {

		WebDriver webDriver = getWebDriver();
		webDriverWait = new WebDriverWait(webDriver, waitTimeOutInSeconds);
	}

	@Override
	public void switchToFrame(String iframeXpath) {

		WebElement webElement = findElementByXpath(iframeXpath);
		WebDriver webDriver = getWebDriver();
		webDriver.switchTo().frame(webElement);
	}

	@Override
	public void switchToWindow(String windowId) {

		WebDriver webDriver = getWebDriver();
		webDriver.switchTo().window(windowId);
	}

	@Override
	public void waitFor(ExpectedCondition expectedCondition) {
		webDriverWait.until(expectedCondition);
	}

	@Override
	public void waitForElementDisplayed(String elementXpath) {

		logger.info("Waiting for element {} to be displayed.", elementXpath);
		waitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
		logger.info("Element {} is displayed.", elementXpath);
	}

	@Override
	public void waitForElementEnabled(String elementXpath) {
		waitForElementEnabled(elementXpath, true);
	}

	@Override
	public void waitForElementEnabled(String elementXpath, boolean elementMustBeDisplayed) {

		logger.info("Waiting for element {} to be enabled.", elementXpath);

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(
				new ElementEnabled(elementXpath), elementMustBeDisplayed, byXpath);
		waitFor(expectedCondition);
		logger.info("Element {} is enabled.", elementXpath);
	}

	@Override
	public void waitForElementNotDisplayed(String elementXpath) {

		logger.info("Waiting for element {} not to be displayed.", elementXpath);
		waitFor(ExpectedConditions.invisibilityOfElementLocated(By.xpath(elementXpath)));
		logger.info("Element {} is not displayed.", elementXpath);
	}

	@Override
	public void waitForTextPresentInElement(String text, String elementXpath) {
		waitForTextPresentInElement(text, elementXpath, true);
	}

	@Override
	public void waitForTextPresentInElement(String text, String elementXpath, boolean elementMustBeDisplayed) {

		logger.info("Waiting for text \"{}\" to be present in element {}.", text, elementXpath);

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditions.textToBePresentInElementLocated(byXpath, text);
		expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(expectedCondition,
				elementMustBeDisplayed, byXpath);
		waitFor(expectedCondition);
		logger.info("Text \"{}\" is present in Element {}.", text, elementXpath);
	}
}
