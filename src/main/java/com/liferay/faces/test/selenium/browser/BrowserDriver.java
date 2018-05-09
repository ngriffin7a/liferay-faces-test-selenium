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

import java.util.List;
import java.util.Set;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * @author  Kyle Stiemann
 */
public interface BrowserDriver {

	public void acceptAlert();

	/**
	 * Calls {@link #captureCurrentBrowserState(java.lang.String, java.lang.String)}. The default output directory is
	 * <code>System.getProperty("java.io.tmpdir") + "captured-browser-state"</code>. This can be changed with the <code>
	 * "integration.captured.browser.state.output.directory"</code> system property. No file name prefix is used.
	 *
	 * @see  #captureCurrentBrowserState(java.lang.String, java.lang.String)
	 */
	public void captureCurrentBrowserState();

	/**
	 * Captures the current page markup (using {@link #getCurrentDocumentMarkup()}) to a file, and if the browser
	 * supports it, captures a screenshot to a file as well. This method logs the file locations for the benefit of the
	 * tester.
	 *
	 * @param  outputDirectoryPath  Path where the captured page state should be generated.
	 * @param  fileNamePrefix       String to prepend to each html (and potentially screenshot) file name.
	 */
	public void captureCurrentBrowserState(String outputDirectoryPath, String fileNamePrefix);

	public void centerElementInCurrentWindow(String elementXpath);

	public void clearBrowserCookies();

	public void clearElement(String elementXpath);

	public void clickElement(String elementXpath);

	/**
	 * Clicks on the element specified via xpath and waits for the clicked element to be rerendered (for example via
	 * Ajax or full page reload). This method will only work if the element clicked is also rerendered. If the clicked
	 * element will not be rerendered, then use {@link
	 * BrowserDriver#performAndWaitForRerender(org.openqa.selenium.interactions.Action, java.lang.String)} with {@link
	 * BrowserDriver#createClickElementAction(java.lang.String)} and the xpath of an element which will be rerendered
	 * instead.
	 *
	 * @param  elementXpath  The xpath of the element to be clicked and rerendered.
	 */
	public void clickElementAndWaitForRerender(String elementXpath);

	/**
	 * Closes the current window. Note: this method quits the browser if the current window is the last window, however,
	 * the driver must still be quit by calling {@link BrowserDriver#quit()}.
	 */
	public void closeCurrentWindow();

	public Actions createActions();

	/**
	 * Creates {@link Actions}. The first action attempts to center the element in the window in order to ensure that it
	 * will not be obscured when other actions are performed.
	 *
	 * @param  elementXpath  The xpath of the element which should be centered in the window.
	 */
	public Actions createActions(String elementXpath);

	public Action createClickElementAction(String elementXpath);

	public void dismissAlert();

	public Object executeScriptInCurrentWindow(String script, Object... args);

	public WebElement findElementByXpath(String elementXpath);

	public List<WebElement> findElementsByXpath(String elementXpath);

	public Set<Cookie> getBrowserCookies();

	public String getBrowserName();

	/**
	 * Returns the current HTML of the entire page.
	 */
	public String getCurrentDocumentMarkup();

	public String getCurrentWindowId();

	public String getCurrentWindowUrl();

	public WebDriver getWebDriver();

	public Set<String> getWindowIds();

	public boolean isBrowserHeadless();

	public boolean isBrowserSimulatingMobile();

	/**
	 * Load all images on the page. If the browser loads images automatically, this method does nothing.
	 */
	public void loadCurrentWindowImages();

	public void navigateWindowTo(String url);

	/**
	 * Performs an {@link Action} and waits for an element to be rerendered (for example via Ajax or a full page
	 * reload).
	 *
	 * @param  action         The action which will cause the rerender.
	 * @param  rerenderXpath  The xpath of the element which will be rerendered.
	 */
	public void performAndWaitForRerender(Action action, String rerenderXpath);

	/**
	 * Closes all windows, quits the browser, and quits the driver.
	 */
	public void quit();

	public void sendKeysToElement(String elementXpath, CharSequence... keys);

	public void setPageLoadTimeout(int waitTimeOutInSeconds);

	public void setWaitTimeOut(int waitTimeOutInSeconds);

	public void switchToFrame(String iframeXpath);

	public void switchToWindow(String windowId);

	public void waitFor(ExpectedCondition expectedCondition);

	/**
	 * Waits for an element to be displayed (see {@link
	 * ExpectedConditions#visibilityOfElementLocated(org.openqa.selenium.By)} for more details).
	 *
	 * @param  elementXpath  The xpath of the element.
	 */
	public void waitForElementDisplayed(String elementXpath);

	/**
	 * Waits for an element to be enabled (see {@link WebElement#isEnabled()}) and displayed.
	 *
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #waitForElementEnabled(java.lang.String, boolean)
	 */
	public void waitForElementEnabled(String elementXpath);

	/**
	 * Waits for an element to be enabled (see {@link WebElement#isEnabled()}) and potentially displayed.
	 *
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, also wait for the element to be displayed.
	 */
	public void waitForElementEnabled(String elementXpath, boolean elementMustBeDisplayed);

	/**
	 * Waits for an element to not be displayed (either not present or not displayed, see {@link
	 * ExpectedConditions#invisibilityOfElementLocated(org.openqa.selenium.By)} for more details).
	 *
	 * @param  elementXpath  The xpath of the element.
	 */
	public void waitForElementNotDisplayed(String elementXpath);

	/**
	 * Waits for an element to contain text and be displayed.
	 *
	 * @param  text          The text.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #waitForElementEnabled(java.lang.String, boolean)
	 */
	public void waitForTextPresentInElement(String text, String elementXpath);

	/**
	 * Waits for an element to contain text and potentially be displayed.
	 *
	 * @param  text                    The text.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, also wait for the element to be displayed.
	 */
	public void waitForTextPresentInElement(String text, String elementXpath, boolean elementMustBeDisplayed);
}
