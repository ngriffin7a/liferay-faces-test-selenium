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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public final class SeleniumAssert {

	// Logger
	private static final Logger logger = Logger.getLogger(SeleniumAssert.class.getName());

	static {
		logger.setLevel(TestUtil.getLogLevel());
	}

	public static void assertElementNotPresent(Browser browser, String xpath) {

		WebElement element = findFirstElementByXpath(browser, xpath);
		Assert.assertNull("Element " + xpath + " is present in the DOM.", element);
	}

	public static void assertElementPresent(Browser browser, String xpath) {

		WebElement element = findFirstElementByXpath(browser, xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);
	}

	public static void assertElementTextInvisible(Browser browser, String xpath, String text) {

		WebElement element = findFirstElementByXpath(browser, xpath);

		boolean elementDisplayed = false;
		String elementText = "";

		if (element != null) {

			elementDisplayed = element.isDisplayed();
			elementText = element.getText();
		}

		Assert.assertTrue("Element " + xpath + " text \"" + elementText +
			"\" is visible. Instead it should be invisible.",
			((element == null) || !elementDisplayed || !elementText.contains(text)));
	}

	public static void assertElementTextVisible(Browser browser, String xpath, String text) {

		WebElement element = findFirstElementByXpath(browser, xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);

		boolean elementDisplayed = element.isDisplayed();
		Assert.assertTrue("Element " + xpath + " is not displayed.", elementDisplayed);

		String elementText = element.getText();
		Assert.assertTrue("Element " + xpath + " does not contain text \"" + text + "\". Instead it contains text \"" +
			elementText + "\".", elementText.contains(text));
	}

	public static void assertElementValue(Browser browser, String xpath, String value) {
		assertElementValue(browser, xpath, value, true);
	}

	public static void assertElementValue(Browser browser, String xpath, String value, boolean elementMustBeVisible) {

		WebElement element = findFirstElementByXpath(browser, xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);

		if (elementMustBeVisible) {

			boolean elementDisplayed = element.isDisplayed();
			Assert.assertTrue("Element " + xpath + " is not displayed.", elementDisplayed);
		}

		String elementValue = element.getAttribute("value");
		Assert.assertEquals("Element " + xpath + " does not contain the value \"" + value +
			"\". Instead it contains the value \"" + elementValue + "\".", value, elementValue);
	}

	public static void assertElementVisible(Browser browser, String xpath) {

		WebElement element = findFirstElementByXpath(browser, xpath);
		Assert.assertNotNull("Element " + xpath + " is not present in the DOM.", element);

		boolean elementDisplayed = element.isDisplayed();
		Assert.assertTrue("Element " + xpath + " is not displayed.", elementDisplayed);
	}

	public static void assertLibraryVisible(Browser browser, String libraryName) {

		String libraryVersionXpath = "//li[contains(.,'" + libraryName + "')]";
		WebElement libraryVersionElement = browser.findElementByXpath(libraryVersionXpath);
		logger.log(Level.INFO, libraryVersionElement.getText());
		SeleniumAssert.assertElementVisible(browser, libraryVersionXpath);
	}

	/**
	 * Returns the first element matching the xpath or null if no element is found. This method may be used in place of
	 * {@link Browser#findElementByXpath(java.lang.String)} which throws an error when an element is not found instead
	 * of returning null.
	 */
	private static WebElement findFirstElementByXpath(Browser browser, String xpath) {

		WebElement element = null;
		List<WebElement> elements = browser.findElements(By.xpath(xpath));

		if (!elements.isEmpty()) {
			element = elements.get(0);
		}

		return element;
	}
}
