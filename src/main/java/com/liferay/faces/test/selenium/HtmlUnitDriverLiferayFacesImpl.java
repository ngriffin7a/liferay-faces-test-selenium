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
package com.liferay.faces.test.selenium;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.LogFactory;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ class HtmlUnitDriverLiferayFacesImpl extends HtmlUnitDriver {

	private static final Level logLevel = TestUtil.getLogLevel();

	static {

		// Disable HtmlUnit's verbose logging unless the developer specifies a log level below (or equal to)
		// Level.FINER.
		if (logLevel.intValue() > Level.FINER.intValue()) {

			LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
			Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
			Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		}
	}

	/* package-private */ HtmlUnitDriverLiferayFacesImpl(BrowserVersion browserVersion, boolean enableJavascript) {
		super(browserVersion, enableJavascript);
	}

	@Override
	protected WebClient modifyWebClient(WebClient initialWebClient) {

		WebClient webClient = super.modifyWebClient(initialWebClient);
		webClient.getOptions().setThrowExceptionOnScriptError(false);

		if (logLevel.intValue() > Level.FINEST.intValue()) {
			webClient.setCssErrorHandler(new SilentCssErrorHandler());
		}

		return webClient;
	}

	/* package-private */ void loadImages() {

		HtmlPage htmlPage = (HtmlPage) lastPage();
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
