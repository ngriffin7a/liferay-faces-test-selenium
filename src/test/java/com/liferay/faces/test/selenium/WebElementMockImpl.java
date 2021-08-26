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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;


/**
 * @author  Kyle Stiemann
 */
public class WebElementMockImpl implements WebElement {

	// Private Final Data Members
	private final String tagName;
	private final boolean enabled;

	public WebElementMockImpl(String tagName, boolean enabled) {
		this.tagName = tagName;
		this.enabled = enabled;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void click() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WebElement findElement(By by) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<WebElement> findElements(By by) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCssValue(String propertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Point getLocation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Rectangle getRect() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Dimension getSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTagName() {
		return tagName;
	}

	@Override
	public String getText() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDisplayed() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isSelected() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void submit() {
		throw new UnsupportedOperationException();
	}
}
