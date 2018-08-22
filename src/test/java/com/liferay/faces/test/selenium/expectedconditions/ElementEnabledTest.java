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
package com.liferay.faces.test.selenium.expectedconditions;

import org.junit.Assert;
import org.junit.Test;

import org.openqa.selenium.WebDriver;

import com.liferay.faces.test.selenium.WebDriverMockImpl;
import com.liferay.faces.test.selenium.WebElementMockImpl;


/**
 * @author  Kyle Stiemann
 */
public class ElementEnabledTest {

	@Test
	public void testElementEnabled() {

		try {

			WebDriver webDriver = new WebDriverMockImpl(new WebElementMockImpl("html", true));
			Assert.assertNotNull(new ElementEnabled("//html").apply(webDriver));

			webDriver = new WebDriverMockImpl(new WebElementMockImpl("html", false));
			Assert.assertNull(new ElementEnabled("//html").apply(webDriver));

			webDriver = new WebDriverMockImpl();
			Assert.assertNull(new ElementEnabled("//html").apply(webDriver));
		}
		catch (Throwable t) {

			if (t instanceof AssertionError) {
				throw (AssertionError) t;
			}
			else {
				throw new AssertionError(t);
			}

		}
	}
}
