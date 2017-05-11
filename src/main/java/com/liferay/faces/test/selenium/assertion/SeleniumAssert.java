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
package com.liferay.faces.test.selenium.assertion;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.expectedconditions.ElementEnabled;
import com.liferay.faces.test.selenium.expectedconditions.internal.ExpectedConditionsUtil;


/**
 * @author  Kyle Stiemann
 */
public final class SeleniumAssert {

	/**
	 * Asserts that an element is displayed (see {@link
	 * ExpectedConditions#visibilityOfElementLocated(org.openqa.selenium.By)} for more details).
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  elementXpath  The xpath of the element.
	 */
	public static void assertElementDisplayed(Browser browser, String elementXpath) {
		assertTrue(browser, ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
	}

	/**
	 * Asserts that an element is not displayed (see {@link
	 * ExpectedConditions#invisibilityOfElementLocated(org.openqa.selenium.By)} for more details). This method will wait
	 * for the amount of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link
	 * Browser#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  elementXpath  The xpath of the element.
	 */
	public static void assertElementNotDisplayed(Browser browser, String elementXpath) {
		assertTrue(browser, ExpectedConditions.invisibilityOfElementLocated(By.xpath(elementXpath)));
	}

	/**
	 * Asserts that an element is not present on the page. This method will wait for the amount of time specified by
	 * {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  elementXpath  The xpath of the element.
	 */
	public static void assertElementNotPresent(Browser browser, String elementXpath) {
		assertFalse(browser, ExpectedConditions.presenceOfElementLocated(By.xpath(elementXpath)));
	}

	/**
	 * Asserts that an element is present on the page. This method will wait for the amount of time specified by {@link
	 * TestUtil#getBrowserWaitTimeOut()} before failing the assertion.
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  elementXpath  The xpath of the element.
	 */
	public static void assertElementPresent(Browser browser, String elementXpath) {
		assertTrue(browser, ExpectedConditions.presenceOfElementLocated(By.xpath(elementXpath)));
	}

	/**
	 * Assert that some expected condition is not satisfied within a browser. This method will wait for the amount of
	 * time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before failing
	 * the assertion.
	 *
	 * @param  browser            The currently running browser in which the assertion will be checked.
	 * @param  expectedCondition  The expected condition of the browser.
	 */
	public static void assertFalse(Browser browser, ExpectedCondition expectedCondition) {

		try {
			browser.waitUntil(ExpectedConditions.not(expectedCondition));
		}
		catch (TimeoutException e) {
			throw createAssertionErrorWithoutExtraInfo(e);
		}
	}

	/**
	 * Asserts that an element does not contain text and is displayed. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before failing the
	 * assertion.
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  text          The text contents.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertTextNotPresentInElement(com.liferay.faces.test.selenium.Browser, java.lang.String,
	 *         java.lang.String, boolean)
	 */
	public static void assertTextNotPresentInElement(Browser browser, String text, String elementXpath) {
		assertTextNotPresentInElement(browser, text, elementXpath, true);
	}

	/**
	 * Asserts that an element does not contain text and potentially is displayed. This method will wait for the amount
	 * of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before
	 * failing the assertion.
	 *
	 * @param  browser                 The currently running browser in which the assertion will be checked.
	 * @param  text                    The text contents.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public static void assertTextNotPresentInElement(Browser browser, String text, String elementXpath,
		boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditions.not(ExpectedConditions
				.textToBePresentInElementLocated(byXpath, text));
		expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(expectedCondition,
				elementMustBeDisplayed, byXpath);
		assertTrue(browser, expectedCondition);
	}

	/**
	 * Asserts that an element contains text and is displayed. This method will wait for the amount of time specified by
	 * {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  text          The text contents.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertTextPresentInElement(com.liferay.faces.test.selenium.Browser, java.lang.String, java.lang.String,
	 *         boolean)
	 */
	public static void assertTextPresentInElement(Browser browser, String text, String elementXpath) {
		assertTextPresentInElement(browser, text, elementXpath, true);
	}

	/**
	 * Asserts that an element contains text and potentially is displayed. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before failing the
	 * assertion.
	 *
	 * @param  browser                 The currently running browser in which the assertion will be checked.
	 * @param  text                    The text contents.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public static void assertTextPresentInElement(Browser browser, String text, String elementXpath,
		boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditions.textToBePresentInElementLocated(byXpath, text);
		expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(expectedCondition,
				elementMustBeDisplayed, byXpath);
		assertTrue(browser, expectedCondition);
	}

	/**
	 * Asserts that an element's value contains text and is displayed. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before failing the
	 * assertion.
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  text          The text contents.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertTextPresentInElementValue(com.liferay.faces.test.selenium.Browser, java.lang.String,
	 *         java.lang.String, boolean)
	 */
	public static void assertTextPresentInElementValue(Browser browser, String text, String elementXpath) {
		assertTextPresentInElementValue(browser, text, elementXpath, true);
	}

	/**
	 * Asserts that an element's value contains text and potentially is displayed. This method will wait for the amount
	 * of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before
	 * failing the assertion.
	 *
	 * @param  browser                 The currently running browser in which the assertion will be checked.
	 * @param  text                    The text contents.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public static void assertTextPresentInElementValue(Browser browser, String text, String elementXpath,
		boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditions.textToBePresentInElementValue(byXpath, text);
		expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(expectedCondition,
				elementMustBeDisplayed, byXpath);
		assertTrue(browser, expectedCondition);
	}

	/**
	 * Assert that some expected condition is satisfied within a browser. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link Browser#setWaitTimeOut()}) before failing the
	 * assertion.
	 *
	 * @param  browser            The currently running browser in which the assertion will be checked.
	 * @param  expectedCondition  The expected condition of the browser.
	 */
	public static void assertTrue(Browser browser, ExpectedCondition expectedCondition) {

		try {
			browser.waitUntil(expectedCondition);
		}
		catch (final TimeoutException e) {
			throw createAssertionErrorWithoutExtraInfo(e);
		}
	}

	private static AssertionError createAssertionErrorWithoutExtraInfo(final TimeoutException timeoutException) {

		return new AssertionError(timeoutException) {

				@Override
				public String getMessage() {

					String message = super.getMessage();
					String additionalInformation = timeoutException.getAdditionalInformation();

					if (additionalInformation != null) {
						message = message.replace(additionalInformation, "");
					}

					BuildInfo buildInformation = timeoutException.getBuildInformation();

					if (buildInformation != null) {
						message = message.replace(buildInformation.toString(), "");
					}

					String systemInformation = timeoutException.getSystemInformation();

					if (systemInformation != null) {
						message = message.replace(systemInformation, "");
					}

					message = message.replaceFirst("\n+$", "").replaceAll("\n+", "\n");

					return message;
				}
			};
	}

	/**
	 * Asserts that an element is enabled (see {@link WebElement#isEnabled()} for more details) and displayed. This
	 * method will wait for the amount of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link
	 * Browser#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  browser       The currently running browser in which the assertion will be checked.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertElementEnabled(com.liferay.faces.test.selenium.Browser, java.lang.String, boolean)
	 */
	public void assertElementEnabled(Browser browser, String elementXpath) {
		assertElementEnabled(browser, elementXpath, true);
	}

	/**
	 * Asserts that an element is enabled (see {@link WebElement#isEnabled()} for more details) and potentially
	 * displayed. This method will wait for the amount of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or
	 * {@link Browser#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  browser                 The currently running browser in which the assertion will be checked.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public void assertElementEnabled(Browser browser, String elementXpath, boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(
				new ElementEnabled(elementXpath), elementMustBeDisplayed, byXpath);
		assertTrue(browser, expectedCondition);
	}
}
