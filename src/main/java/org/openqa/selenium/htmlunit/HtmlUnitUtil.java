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
package org.openqa.selenium.htmlunit;

import java.io.IOException;

import org.openqa.selenium.WebDriver;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.liferay.faces.test.selenium.Browser;


/**
 * This class takes advantage of Java's <a
 * href="http://stackoverflow.com/questions/708744/can-classes-of-same-package-spread-across-multiple-jar-files">
 * split-package feature/design-flaw</a> in order to gain access to protected and package-private methods in the <code>
 * org.openqa.selenium.htmlunit</code> package.
 *
 * @author  Kyle Stiemann
 */
public class HtmlUnitUtil {

	private HtmlUnitUtil() {
		throw new AssertionError();
	}

	public static void loadImages(Browser browser) {

		WebDriver webDriver = browser.getWebDriver();
		HtmlUnitDriver htmlUnitDriver = (HtmlUnitDriver) webDriver;

		// Access HtmlUnitDriver's protected lastPage() method (see JavaDoc for details).
		HtmlPage htmlPage = (HtmlPage) htmlUnitDriver.lastPage();
		DomNodeList<DomElement> imageElements = htmlPage.getElementsByTagName("img");

		for (DomElement imageElement : imageElements) {

			HtmlImage htmlImage = (HtmlImage) imageElement;

			try {

				// Download the image.
				htmlImage.getImageReader();
			}
			catch (IOException e) {
				// do nothing.
			}
		}
	}
}
