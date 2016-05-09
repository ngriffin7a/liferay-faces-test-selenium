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
package com.liferay.faces.test.applicant;

import org.junit.Test;

import com.liferay.faces.test.Browser;
import com.liferay.faces.test.IntegrationTesterBase;


/**
 * @author  Kyle Stiemann
 */
public abstract class ApplicantTesterBase extends IntegrationTesterBase {

	@Test
	public void runApplicantTest() throws Exception {

		Browser browser = Browser.getInstance();
		browser.navigateToURL(BASE_URL + getContext());

		// Wait to begin the test until the logo is rendered.
		browser.waitForElementVisible(getLogoXpath());

		// Test that an empty value submits successfully, but with validation errors.
		browser.clickAndWaitForAjaxRerender(getSubmitButtonXpath());
		browser.assertElementVisible(getFirstNameFieldErrorXpath());

		// Test that a text value submits successfully.
		String text = "Hello World!";
		browser.sendKeys(getFirstNameFieldXpath(), text);
		browser.clickAndWaitForAjaxRerender(getSubmitButtonXpath());
		browser.assertElementValue(getFirstNameFieldXpath(), text);
		browser.assertElementVisible(getLogoXpath());
	}

	protected abstract String getContext();

	protected String getFirstNameFieldErrorXpath() {
		return "//input[contains(@id,':firstName')]/../span[@class='portlet-msg-error']";
	}

	protected String getFirstNameFieldXpath() {
		return "//input[contains(@id,':firstName')]";
	}

	protected String getLogoXpath() {
		return "//img[contains(@src,'liferay-logo.png')]";
	}

	protected String getSubmitButtonXpath() {
		return "//input[@type='submit' and @value='Submit']";
	}
}
