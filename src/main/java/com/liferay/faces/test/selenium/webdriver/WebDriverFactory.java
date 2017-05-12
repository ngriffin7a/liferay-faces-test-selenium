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
package com.liferay.faces.test.selenium.webdriver;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.openqa.selenium.WebDriver;


/**
 * @author  Kyle Stiemann
 */
public abstract class WebDriverFactory {

	private static final WebDriverFactory webDriverFactory;

	static {

		ServiceLoader<WebDriverFactory> serviceLoader = ServiceLoader.load(WebDriverFactory.class);

		if (serviceLoader != null) {

			Iterator<WebDriverFactory> iterator = serviceLoader.iterator();

			WebDriverFactory webDriverFactoryImpl = null;

			while ((webDriverFactoryImpl == null) && iterator.hasNext()) {
				webDriverFactoryImpl = iterator.next();
			}

			if (webDriverFactoryImpl == null) {
				throw new NullPointerException("Unable locate service for " + WebDriverFactory.class.getName());
			}

			webDriverFactory = webDriverFactoryImpl;
		}
		else {
			throw new NullPointerException("Unable to acquire ServiceLoader for " + WebDriverFactory.class.getName());
		}
	}

	public static final WebDriver getWebDriver(String browserName, boolean browserHeadless,
		boolean browserSimulateMobile) {
		return webDriverFactory.getWebDriverImplementation(browserName, browserHeadless, browserSimulateMobile);
	}

	public abstract WebDriver getWebDriverImplementation(String browserName, boolean browserHeadless,
		boolean browserSimulateMobile);
}
