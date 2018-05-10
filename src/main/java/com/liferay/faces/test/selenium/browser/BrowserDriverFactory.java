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
package com.liferay.faces.test.selenium.browser;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.openqa.selenium.WebDriver;


/**
 * @author  Kyle Stiemann
 */
public abstract class BrowserDriverFactory {

	private static final BrowserDriverFactory browserDriverFactory;

	static {

		ServiceLoader<BrowserDriverFactory> serviceLoader = ServiceLoader.load(BrowserDriverFactory.class);

		if (serviceLoader != null) {

			Iterator<BrowserDriverFactory> iterator = serviceLoader.iterator();

			BrowserDriverFactory browserDriverFactoryImpl = null;

			while ((browserDriverFactoryImpl == null) && iterator.hasNext()) {
				browserDriverFactoryImpl = iterator.next();
			}

			if (browserDriverFactoryImpl == null) {
				throw new NullPointerException("Unable locate service for " + BrowserDriverFactory.class.getName());
			}

			browserDriverFactory = browserDriverFactoryImpl;
		}
		else {
			throw new NullPointerException("Unable to acquire ServiceLoader for " +
				BrowserDriverFactory.class.getName());
		}
	}

	/**
	 * Returns a new instance of {@link BrowserDriver} with the default {@link WebDriver} settings. The BrowserDriver
	 * must be closed (via {@link BrowserDriver#quit()} or {@link BrowserDriver#closeCurrentWindow()}) by the caller.
	 *
	 * @see  #getBrowserDriver(java.lang.String, boolean, boolean)
	 * @see  #getBrowserDriver(org.openqa.selenium.WebDriver, boolean, boolean)
	 */
	public static final BrowserDriver getBrowserDriver() {
		return browserDriverFactory.getBrowserDriverImplementation();
	}

	/**
	 * Returns a new instance of {@link BrowserDriver}. The BrowserDriver must be closed (via {@link
	 * BrowserDriver#quit()} or {@link BrowserDriver#closeCurrentWindow()}) by the caller.
	 *
	 * @param  browserName            The name of the browser driven by BrowserDriver to drive the browser.
	 * @param  browserHeadless        If true, the browser will run in headless mode.
	 * @param  browserSimulateMobile  If true, the browser will request pages as a mobile device via its User-Agent.
	 */
	public static final BrowserDriver getBrowserDriver(String browserName, boolean browserHeadless,
		boolean browserSimulateMobile) {
		return browserDriverFactory.getBrowserDriverImplementation(browserName, browserHeadless, browserSimulateMobile);
	}

	/**
	 * Returns a new instance of {@link BrowserDriver}. The BrowserDriver must be closed (via {@link
	 * BrowserDriver#quit()} or {@link BrowserDriver#closeCurrentWindow()}) by the caller.
	 *
	 * @param  webDriver              The {@link WebDriver} used by the BrowserDriver to drive the browser.
	 * @param  browserHeadless        If true, the browser will run in headless mode.
	 * @param  browserSimulateMobile  If true, the browser will request pages as a mobile device via its User-Agent.
	 */
	public static final BrowserDriver getBrowserDriver(WebDriver webDriver, boolean browserHeadless,
		boolean browserSimulateMobile) {
		return browserDriverFactory.getBrowserDriverImplementation(webDriver, browserHeadless, browserSimulateMobile);
	}

	/**
	 * Returns a new instance of {@link BrowserDriver} with the default {@link WebDriver} settings. The BrowserDriver
	 * must be closed (via {@link BrowserDriver#quit()} or {@link BrowserDriver#closeCurrentWindow()}) by the caller.
	 *
	 * @see  #getBrowserDriverImplementation(java.lang.String, boolean, boolean)
	 * @see  #getBrowserDriverImplementation(org.openqa.selenium.WebDriver, boolean, boolean)
	 */
	public abstract BrowserDriver getBrowserDriverImplementation();

	/**
	 * Returns a new instance of {@link BrowserDriver}. The BrowserDriver must be closed (via {@link
	 * BrowserDriver#quit()} or {@link BrowserDriver#closeCurrentWindow()}) by the caller.
	 *
	 * @param  browserName            The name of the browser driven by BrowserDriver to drive the browser.
	 * @param  browserHeadless        If true, the browser will run in headless mode.
	 * @param  browserSimulateMobile  If true, the browser will request pages as a mobile device via its User-Agent.
	 */
	public abstract BrowserDriver getBrowserDriverImplementation(String browserName, boolean browserHeadless,
		boolean browserSimulateMobile);

	/**
	 * Returns a new instance of {@link BrowserDriver}. The BrowserDriver must be closed (via {@link
	 * BrowserDriver#quit()} or {@link BrowserDriver#closeCurrentWindow()}) by the caller.
	 *
	 * @param  webDriver              The {@link WebDriver} used by the BrowserDriver to drive the browser.
	 * @param  browserHeadless        If true, the browser will run in headless mode.
	 * @param  browserSimulateMobile  If true, the browser will request pages as a mobile device via its User-Agent.
	 */
	public abstract BrowserDriver getBrowserDriverImplementation(WebDriver webDriver, boolean browserHeadless,
		boolean browserSimulateMobile);
}
