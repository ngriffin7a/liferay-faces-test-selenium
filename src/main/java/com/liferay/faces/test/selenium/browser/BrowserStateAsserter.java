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
package com.liferay.faces.test.selenium.browser;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.selenium.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public interface BrowserStateAsserter {

	/**
	 * Asserts that an element is displayed (see {@link
	 * ExpectedConditions#visibilityOfElementLocated(org.openqa.selenium.By)} for more details).
	 *
	 * @param  elementXpath  The xpath of the element.
	 */
	public void assertElementDisplayed(String elementXpath);

	/**
	 * Asserts that an element is enabled (see {@link WebElement#isEnabled()} for more details) and displayed. This
	 * method will wait for the amount of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link
	 * BrowserDriver#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertElementEnabled(java.lang.String, boolean)
	 */
	public void assertElementEnabled(String elementXpath);

	/**
	 * Asserts that an element is enabled (see {@link WebElement#isEnabled()} for more details) and potentially
	 * displayed. This method will wait for the amount of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or
	 * {@link BrowserDriver#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public void assertElementEnabled(String elementXpath, boolean elementMustBeDisplayed);

	/**
	 * Asserts that an element is not displayed (see {@link
	 * ExpectedConditions#invisibilityOfElementLocated(org.openqa.selenium.By)} for more details). This method will wait
	 * for the amount of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link
	 * BrowserDriver#setWaitTimeOut()}) before failing the assertion.
	 *
	 * @param  elementXpath  The xpath of the element.
	 */
	public void assertElementNotDisplayed(String elementXpath);

	/**
	 * Asserts that an element is not present on the page. This method will wait for the amount of time specified by
	 * {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before failing the
	 * assertion.
	 *
	 * @param  elementXpath  The xpath of the element.
	 */
	public void assertElementNotPresent(String elementXpath);

	/**
	 * Asserts that an element is present on the page. This method will wait for the amount of time specified by {@link
	 * TestUtil#getBrowserWaitTimeOut()} before failing the assertion.
	 *
	 * @param  elementXpath  The xpath of the element.
	 */
	public void assertElementPresent(String elementXpath);

	/**
	 * Assert that some expected condition is not satisfied within a browser. This method will wait for the amount of
	 * time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before
	 * failing the assertion.
	 *
	 * @param  expectedCondition  The expected condition of the browser.
	 */
	public void assertFalse(ExpectedCondition expectedCondition);

	/**
	 * Asserts that an element does not contain text and is displayed. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before failing
	 * the assertion.
	 *
	 * @param  text          The text contents.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertTextNotPresentInElement(java.lang.String, java.lang.String, boolean)
	 */
	public void assertTextNotPresentInElement(String text, String elementXpath);

	/**
	 * Asserts that an element does not contain text and potentially is displayed. This method will wait for the amount
	 * of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before
	 * failing the assertion.
	 *
	 * @param  text                    The text contents.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public void assertTextNotPresentInElement(String text, String elementXpath, boolean elementMustBeDisplayed);

	/**
	 * Asserts that an element contains text and is displayed. This method will wait for the amount of time specified by
	 * {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before failing the
	 * assertion.
	 *
	 * @param  text          The text contents.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertTextPresentInElement(java.lang.String, java.lang.String, boolean)
	 */
	public void assertTextPresentInElement(String text, String elementXpath);

	/**
	 * Asserts that an element contains text and potentially is displayed. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before failing
	 * the assertion.
	 *
	 * @param  text                    The text contents.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public void assertTextPresentInElement(String text, String elementXpath, boolean elementMustBeDisplayed);

	/**
	 * Asserts that an element's value contains text and is displayed. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before failing
	 * the assertion.
	 *
	 * @param  text          The text contents.
	 * @param  elementXpath  The xpath of the element.
	 *
	 * @see    #assertTextPresentInElementValue(java.lang.String, java.lang.String, boolean)
	 */
	public void assertTextPresentInElementValue(String text, String elementXpath);

	/**
	 * Asserts that an element's value contains text and potentially is displayed. This method will wait for the amount
	 * of time specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before
	 * failing the assertion.
	 *
	 * @param  text                    The text contents.
	 * @param  elementXpath            The xpath of the element.
	 * @param  elementMustBeDisplayed  If true, assert that this element must also be displayed.
	 */
	public void assertTextPresentInElementValue(String text, String elementXpath, boolean elementMustBeDisplayed);

	/**
	 * Assert that some expected condition is satisfied within a browser. This method will wait for the amount of time
	 * specified by {@link TestUtil#getBrowserWaitTimeOut()} (or {@link BrowserDriver#setWaitTimeOut()}) before failing
	 * the assertion.
	 *
	 * @param  expectedCondition  The expected condition of the browser.
	 */
	public void assertTrue(ExpectedCondition expectedCondition);
}
