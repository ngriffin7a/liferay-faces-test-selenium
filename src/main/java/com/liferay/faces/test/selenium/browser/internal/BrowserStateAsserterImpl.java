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

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;
import com.liferay.faces.test.selenium.expectedconditions.ElementEnabled;


/**
 * @author  Kyle Stiemann
 */
public class BrowserStateAsserterImpl implements BrowserStateAsserter {

	// Private Data Members
	private BrowserDriver browserDriver;

	public BrowserStateAsserterImpl(BrowserDriver browserDriver) {
		this.browserDriver = browserDriver;
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

	@Override
	public void assertElementDisplayed(String elementXpath) {
		assertTrue(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
	}

	@Override
	public void assertElementEnabled(String elementXpath) {
		assertElementEnabled(elementXpath, true);
	}

	@Override
	public void assertElementEnabled(String elementXpath, boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(
				new ElementEnabled(elementXpath), elementMustBeDisplayed, byXpath);
		assertTrue(expectedCondition);
	}

	@Override
	public void assertElementNotDisplayed(String elementXpath) {
		assertTrue(ExpectedConditions.invisibilityOfElementLocated(By.xpath(elementXpath)));
	}

	@Override
	public void assertElementNotPresent(String elementXpath) {
		assertFalse(ExpectedConditions.presenceOfElementLocated(By.xpath(elementXpath)));
	}

	@Override
	public void assertElementPresent(String elementXpath) {
		assertTrue(ExpectedConditions.presenceOfElementLocated(By.xpath(elementXpath)));
	}

	@Override
	public void assertFalse(ExpectedCondition expectedCondition) {

		try {
			browserDriver.waitFor(ExpectedConditions.not(expectedCondition));
		}
		catch (TimeoutException e) {
			throw createAssertionErrorWithoutExtraInfo(e);
		}
	}

	@Override
	public void assertTextNotPresentInElement(String text, String elementXpath) {
		assertTextNotPresentInElement(text, elementXpath, true);
	}

	@Override
	public void assertTextNotPresentInElement(String text, String elementXpath, boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditions.not(ExpectedConditions
				.textToBePresentInElementLocated(byXpath, text));
		expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(expectedCondition,
				elementMustBeDisplayed, byXpath);
		assertTrue(expectedCondition);
	}

	@Override
	public void assertTextPresentInElement(String text, String elementXpath) {
		assertTextPresentInElement(text, elementXpath, true);
	}

	@Override
	public void assertTextPresentInElement(String text, String elementXpath, boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditions.textToBePresentInElementLocated(byXpath, text);
		expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(expectedCondition,
				elementMustBeDisplayed, byXpath);
		assertTrue(expectedCondition);
	}

	@Override
	public void assertTextPresentInElementValue(String text, String elementXpath) {
		assertTextPresentInElementValue(text, elementXpath, true);
	}

	@Override
	public void assertTextPresentInElementValue(String text, String elementXpath, boolean elementMustBeDisplayed) {

		By byXpath = By.xpath(elementXpath);
		ExpectedCondition<?> expectedCondition = ExpectedConditions.textToBePresentInElementValue(byXpath, text);
		expectedCondition = ExpectedConditionsUtil.ifNecessaryExpectElementDisplayed(expectedCondition,
				elementMustBeDisplayed, byXpath);
		assertTrue(expectedCondition);
	}

	@Override
	public void assertTrue(ExpectedCondition expectedCondition) {

		try {
			browserDriver.waitFor(expectedCondition);
		}
		catch (final TimeoutException e) {
			throw createAssertionErrorWithoutExtraInfo(e);
		}
	}
}
