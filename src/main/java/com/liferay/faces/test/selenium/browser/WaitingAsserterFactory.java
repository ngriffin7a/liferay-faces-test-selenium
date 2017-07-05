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
package com.liferay.faces.test.selenium.browser;

import java.util.Iterator;
import java.util.ServiceLoader;


/**
 * @author  Kyle Stiemann
 */
public abstract class WaitingAsserterFactory {

	private static final WaitingAsserterFactory waitingAsserterFactory;

	static {

		ServiceLoader<WaitingAsserterFactory> serviceLoader = ServiceLoader.load(WaitingAsserterFactory.class);

		if (serviceLoader != null) {

			Iterator<WaitingAsserterFactory> iterator = serviceLoader.iterator();

			WaitingAsserterFactory waitingAsserterFactoryImpl = null;

			while ((waitingAsserterFactoryImpl == null) && iterator.hasNext()) {
				waitingAsserterFactoryImpl = iterator.next();
			}

			if (waitingAsserterFactoryImpl == null) {
				throw new NullPointerException("Unable locate service for " + WaitingAsserterFactory.class.getName());
			}

			waitingAsserterFactory = waitingAsserterFactoryImpl;
		}
		else {
			throw new NullPointerException("Unable to acquire ServiceLoader for " +
				WaitingAsserterFactory.class.getName());
		}
	}

	/**
	 * Returns a new instance of {@link WaitingAsserter}.
	 *
	 * @param  browserDriver  The {@link BrowserDriver} which should be used to assert the browser's state.
	 */
	public static final WaitingAsserter getWaitingAsserter(BrowserDriver browserDriver) {
		return waitingAsserterFactory.getWaitingAsserterImplementation(browserDriver);
	}

	/**
	 * Returns a new instance of {@link WaitingAsserter}.
	 *
	 * @param  browserDriver  The {@link BrowserDriver} which should be used to assert the browser's state.
	 */
	public abstract WaitingAsserter getWaitingAsserterImplementation(BrowserDriver browserDriver);
}
