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
package com.liferay.faces.test.selenium;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * @author  Kyle Stiemann
 */
public class WebDriverMockImpl implements WebDriver {

	// Private Final Data Members
	private final Map<String, WebElement> webElements;

	public WebDriverMockImpl(WebElement... webElementsArray) {

		Map<String, WebElement> webElements = new HashMap<String, WebElement>();

		for (WebElement webElement : webElementsArray) {

			String tagName = webElement.getTagName();
			webElements.put(tagName, webElement);
		}

		this.webElements = Collections.unmodifiableMap(webElements);
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WebElement findElement(By by) {

		if (!(by instanceof By.ByXPath)) {
			throw new UnsupportedOperationException();
		}

		String tagName = by.toString().replaceAll("[^/]+//([a-zA-Z0-9_\\-:.]+)[\\s\\S]*", "$1");
		WebElement webElement = webElements.get(tagName);

		if (webElement == null) {
			throw new NoSuchElementException("");
		}

		return webElement;
	}

	@Override
	public List<WebElement> findElements(By by) {

		List<WebElement> webElements = Collections.emptyList();

		try {
			webElements = Collections.unmodifiableList(Arrays.asList(findElement(by)));
		}
		catch (NoSuchElementException e) {
			// Do nothing.
		}

		return webElements;
	}

	@Override
	public void get(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCurrentUrl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPageSource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTitle() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWindowHandle() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getWindowHandles() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Options manage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Navigation navigate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void quit() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TargetLocator switchTo() {
		throw new UnsupportedOperationException();
	}
}
