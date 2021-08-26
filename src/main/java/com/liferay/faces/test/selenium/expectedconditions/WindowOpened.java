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

import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;


/**
 * @author  Kyle Stiemann
 */
public class WindowOpened implements ExpectedCondition<Boolean> {

	// Private Data Members
	private int initialNumberOfWindows;

	public WindowOpened(int initialNumberOfWindows) {
		this.initialNumberOfWindows = initialNumberOfWindows;
	}

	@Override
	public Boolean apply(WebDriver webDriver) {

		Set<String> windowHandles = webDriver.getWindowHandles();
		int numberOfWindows = windowHandles.size();

		return (initialNumberOfWindows + 1) == numberOfWindows;
	}
}
