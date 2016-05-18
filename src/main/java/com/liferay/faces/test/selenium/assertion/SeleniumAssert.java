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
package com.liferay.faces.test.selenium.assertion;

import org.junit.Assert;

import org.openqa.selenium.WebElement;

import com.liferay.faces.test.selenium.Browser;


/**
 * @author  Kyle Stiemann
 */
public final class SeleniumAssert {

	public static void assertElementPresent(Browser browser, String xpath) {

		WebElement element = browser.findElementByXpath(xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);
	}

	public static void assertElementTextVisible(Browser browser, String xpath, String text) {

		WebElement element = browser.findElementByXpath(xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);

		boolean elementDisplayed = element.isDisplayed();
		Assert.assertTrue("Element " + xpath + " is not displayed.", elementDisplayed);

		String elementText = element.getText();
		Assert.assertTrue("Element " + xpath + " does not contain text \"" + text + "\". Instead it contains text \"" +
			elementText + "\".", elementText.contains(text));
	}

	public static void assertElementValue(Browser browser, String xpath, String value) {

		WebElement element = browser.findElementByXpath(xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);

		boolean elementDisplayed = element.isDisplayed();
		Assert.assertTrue("Element " + xpath + " is not displayed.", elementDisplayed);

		String elementValue = element.getAttribute("value");
		Assert.assertEquals("Element " + xpath + " does contain the value \"" + value +
			"\". Instead it contains the value \"" + elementValue + "\".", value, elementValue);
	}

	public static void assertElementVisible(Browser browser, String xpath) {

		WebElement element = browser.findElementByXpath(xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);

		boolean elementDisplayed = element.isDisplayed();
		Assert.assertTrue("Element " + xpath + " is not displayed.", elementDisplayed);
	}
}
