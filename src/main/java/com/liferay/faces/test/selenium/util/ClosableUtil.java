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
package com.liferay.faces.test.selenium.util;

import java.io.Closeable;
import java.io.IOException;


/**
 * @author  Kyle Stiemann
 */
public final class ClosableUtil {

	private ClosableUtil() {
		throw new AssertionError();
	}

	public static void close(Closeable closeable) {

		if (closeable != null) {

			try {
				closeable.close();
			}
			catch (IOException e) {
				// do nothing.
			}
		}
	}
}
