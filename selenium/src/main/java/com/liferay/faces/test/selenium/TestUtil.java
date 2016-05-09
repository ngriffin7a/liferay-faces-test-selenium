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
package com.liferay.faces.test.selenium;

/**
 * @author  Kyle Stiemann
 */
public final class TestUtil {

	private TestUtil() {
		throw new AssertionError();
	}

	/**
	 * Returns the property value or the default if the property value has not been set or is an empty String. {@link
	 * System#getProperty(java.lang.String, java.lang.String)} only returns the default value when the property has not
	 * been set. Use this method when a default value is preferred for unset properties and properties which have been
	 * set to an empty String.
	 */
	public static String getSystemPropertyOrDefault(String propertyName, String defaultValue) {

		String propertyValue = System.getProperty(propertyName, "");

		if ("".equals(propertyValue)) {
			propertyValue = defaultValue;
		}

		return propertyValue;
	}
}
