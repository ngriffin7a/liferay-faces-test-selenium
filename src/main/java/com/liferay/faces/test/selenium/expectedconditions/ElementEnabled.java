/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.test.selenium.expectedconditions;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;


/**
 * @author  Kyle Stiemann
 */
public class ElementEnabled implements ExpectedCondition<WebElement> {

	// Private Data Members
	private String elementXpath;

	public ElementEnabled(String elementXpath) {
		this.elementXpath = elementXpath;
	}

	@Override
	public WebElement apply(WebDriver webDriver) {

		WebElement webElement = null;

		try {

			List<WebElement> webElements = webDriver.findElements(By.xpath(elementXpath));

			if (!webElements.isEmpty() && webElements.get(0).isEnabled()) {
				webElement = webElements.get(0);
			}
		}
		catch (StaleElementReferenceException e) {
			// Do nothing.
		}

		return webElement;
	}
}
