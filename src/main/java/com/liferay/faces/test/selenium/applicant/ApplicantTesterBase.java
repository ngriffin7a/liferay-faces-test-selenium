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
package com.liferay.faces.test.selenium.applicant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

import org.junit.runners.MethodSorters;

import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class ApplicantTesterBase extends IntegrationTesterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicantTesterBase.class);

	// Private Constants
	protected static final String LIFERAY_JSF_JERSEY_PNG_FILE_PATH = TestUtil.JAVA_IO_TMPDIR + "liferay-jsf-jersey.png";

	@BeforeClass
	public static void setUpApplicantTester() {
		Browser.getInstance().setWaitTimeOut(TestUtil.getBrowserWaitTimeOut(10));
	}

	@AfterClass
	public static void tearDownApplicantTester() {
		Browser.getInstance().setWaitTimeOut(TestUtil.getBrowserWaitTimeOut());
	}

	@Test
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		Browser browser = Browser.getInstance();
		browser.get(TestUtil.DEFAULT_BASE_URL + getContext());

		SeleniumAssert.assertElementDisplayed(browser, getFirstNameFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getLastNameFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getEmailAddressFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getPhoneNumberFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getDateOfBirthFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getCityFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getProvinceIdFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getPostalCodeFieldXpath());
		SeleniumAssert.assertElementDisplayed(browser, getShowHideCommentsLinkXpath());
		assertFileUploadChooserDisplayed(browser);
		assertLibraryElementDisplayed(browser, "Mojarra");
		assertLibraryElementDisplayed(browser, "Liferay Faces Alloy");
		assertLibraryElementDisplayed(browser, "Liferay Faces Bridge Impl");

		if (TestUtil.getContainer().contains("liferay")) {
			assertLibraryElementDisplayed(browser, "Liferay Faces Bridge Ext");
		}

		String extraLibraryName = getExtraLibraryName();

		if (extraLibraryName != null) {
			assertLibraryElementDisplayed(browser, extraLibraryName);
		}
	}

	@Test
	public void runApplicantPortletTest_B_EditMode() {

		// Test that changing the date pattern via preferences changes the Birthday value in the portlet.
		Browser browser = Browser.getInstance();
		browser.click(getEditModeXpath());

		String datePatternPreferencesXpath = getDatePatternPreferencesXpath();

		try {
			browser.waitForElementEnabled(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		browser.clear(datePatternPreferencesXpath);

		String newDatePattern = "MM/dd/yy";
		browser.sendKeys(datePatternPreferencesXpath, newDatePattern);

		String preferencesSubmitButtonXpath = getPreferencesSubmitButtonXpath();
		browser.click(preferencesSubmitButtonXpath);

		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();

		try {
			browser.waitForElementEnabled(dateOfBirthFieldXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		Date today = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(newDatePattern);
		TimeZone gmtTimeZone = TimeZone.getTimeZone("Greenwich");
		simpleDateFormat.setTimeZone(gmtTimeZone);

		String todayString = simpleDateFormat.format(today);
		SeleniumAssert.assertTextPresentInElementValue(browser, todayString, dateOfBirthFieldXpath);

		// Test that resetting the date pattern via preferences changes the Birthday year back to the long version.
		browser.click(getEditModeXpath());

		try {
			browser.waitForElementEnabled(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String preferencesResetButtonXpath = getPreferencesResetButtonXpath();
		browser.click(preferencesResetButtonXpath);

		try {
			browser.waitForElementEnabled(dateOfBirthFieldXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String oldDatePattern = "MM/dd/yyyy";
		simpleDateFormat.applyPattern(oldDatePattern);
		todayString = simpleDateFormat.format(today);
		SeleniumAssert.assertTextPresentInElementValue(browser, todayString, dateOfBirthFieldXpath);
	}

	@Test
	public void runApplicantPortletTest_C_FirstNameField() {

		Browser browser = Browser.getInstance();
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browser.createActions().sendKeys(Keys.TAB).perform();
		browser.sendKeys(firstNameFieldXpath, "asdf");

		String lastNameFieldXpath = getLastNameFieldXpath();
		Action lastNameFieldClick = browser.createClickAction(lastNameFieldXpath);
		browser.performAndWaitForRerender(lastNameFieldClick, firstNameFieldXpath);

		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		SeleniumAssert.assertElementNotDisplayed(browser, firstNameFieldErrorXpath);
		browser.clear(firstNameFieldXpath);
		browser.performAndWaitForRerender(lastNameFieldClick, firstNameFieldXpath);
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required", firstNameFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_D_EmailValidation() {

		Browser browser = Browser.getInstance();
		String emailAddressFieldXpath = getEmailAddressFieldXpath();
		browser.centerElementInView(emailAddressFieldXpath);
		sendKeysTabAndWaitForRerender(browser, emailAddressFieldXpath, "test");

		String emailAddressFieldErrorXpath = getFieldErrorXpath(emailAddressFieldXpath);
		SeleniumAssert.assertTextPresentInElement(browser, "Invalid e-mail address", emailAddressFieldErrorXpath);
		sendKeysTabAndWaitForRerender(browser, emailAddressFieldXpath, "@liferay.com");
		SeleniumAssert.assertElementNotDisplayed(browser, emailAddressFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		Browser browser = Browser.getInstance();
		clearAllFields(browser);
		browser.clickAndWaitForRerender(getSubmitButtonXpath());
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getFirstNameFieldXpath()));
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getLastNameFieldXpath()));
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getEmailAddressFieldXpath()));
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getPhoneNumberFieldXpath()));
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getDateOfBirthFieldXpath()));
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getCityFieldXpath()));
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getProvinceIdFieldXpath()));
		SeleniumAssert.assertTextPresentInElement(browser, "Value is required",
			getFieldErrorXpath(getPostalCodeFieldXpath()));
	}

	@Test
	public void runApplicantPortletTest_F_AutoPopulateCityState() {

		Browser browser = Browser.getInstance();
		browser.centerElementInView(getPostalCodeFieldXpath());
		sendKeysTabAndWaitForRerender(browser, getPostalCodeFieldXpath(), "32801");
		SeleniumAssert.assertTextPresentInElementValue(browser, "Orlando", getCityFieldXpath());
		SeleniumAssert.assertTextPresentInElementValue(browser, "3", getProvinceIdFieldXpath());
	}

	@Test
	public void runApplicantPortletTest_G_Comments() {

		Browser browser = Browser.getInstance();
		String showHideCommentsLinkXpath = getShowHideCommentsLinkXpath();
		browser.clickAndWaitForRerender(showHideCommentsLinkXpath);

		String commentsXpath = getCommentsXpath();
		browser.sendKeys(commentsXpath, "testing 1, 2, 3");
		browser.clickAndWaitForRerender(showHideCommentsLinkXpath);
		browser.clickAndWaitForRerender(showHideCommentsLinkXpath);
		SeleniumAssert.assertTextPresentInElement(browser, "testing 1, 2, 3", commentsXpath);
	}

	@Test
	public void runApplicantPortletTest_H_DateValidation() {

		Browser browser = Browser.getInstance();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browser.centerElementInView(dateOfBirthFieldXpath);
		browser.clear(dateOfBirthFieldXpath);
		sendKeysTabAndWaitForRerender(browser, dateOfBirthFieldXpath, "12/34/5678");

		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		SeleniumAssert.assertTextPresentInElement(browser, "Invalid date format", dateOfBirthFieldErrorXpath);
		browser.clear(dateOfBirthFieldXpath);
		sendKeysTabAndWaitForRerender(browser, dateOfBirthFieldXpath, "01/02/3456");
		SeleniumAssert.assertElementNotDisplayed(browser, dateOfBirthFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_I_FileUpload() {

		Browser browser = Browser.getInstance();
		String fileUploadChooserXpath = getFileUploadChooserXpath();
		WebElement fileUploadChooser = browser.findElementByXpath(fileUploadChooserXpath);

		// Set PrimeFaces p:fileUpload transform style to "none" since it causes the element to not be displayed
		// according to Selenium (although the element is visible to users).
		browser.executeScript("arguments[0].style.transform = 'none';", fileUploadChooser);

		// Workaround https://github.com/ariya/phantomjs/issues/10993 by removing the multiple attribute from <input
		// type="file" />
		if (browser.getName().equals("phantomjs")) {

			browser.executeScript(
				"var multipleFileUploadElements = document.querySelectorAll('input[type=\"file\"][multiple]');" +
				"for (var i = 0; i < multipleFileUploadElements.length; i++) {" +
				"multipleFileUploadElements[i].removeAttribute('multiple'); }");
		}

		fileUploadChooser.sendKeys(LIFERAY_JSF_JERSEY_PNG_FILE_PATH);
		submitFile(browser);
		SeleniumAssert.assertTextPresentInElement(browser, "jersey", getUploadedFileXpath());
	}

	@Test
	public void runApplicantPortletTest_J_Submit() {

		Browser browser = Browser.getInstance();
		clearAllFields(browser);
		browser.clear(getCommentsXpath());

		String firstNameFieldXpath = getFirstNameFieldXpath();
		browser.waitForElementEnabled(firstNameFieldXpath);
		browser.sendKeys(firstNameFieldXpath, "David");
		browser.sendKeys(getLastNameFieldXpath(), "Samuel");
		browser.sendKeys(getEmailAddressFieldXpath(), "no_need@just.pray");
		browser.sendKeys(getPhoneNumberFieldXpath(), "(way) too-good");
		selectDate(browser);
		browser.sendKeys(getCityFieldXpath(), "North Orlando");
		selectProvince(browser);
		browser.sendKeys(getPostalCodeFieldXpath(), "32802");

		String genesis11 =
			"Indeed the people are one and they all have one language, and this is what they begin to do ...";
		browser.sendKeys(getCommentsXpath(), genesis11);
		browser.waitForElementNotDisplayed(getFieldErrorXpath("//*"));
		browser.click(getSubmitButtonXpath());
		SeleniumAssert.assertTextPresentInElement(browser, "Dear David,", getConfimationFormXpath());
	}

	protected abstract String getContext();

	protected void assertFileUploadChooserDisplayed(Browser browser) {
		SeleniumAssert.assertElementDisplayed(browser, getFileUploadChooserXpath());
	}

	protected void assertLibraryElementDisplayed(Browser browser, String libraryName) {

		String libraryVersionXpath = "//li[contains(.,'" + libraryName + "')]";
		SeleniumAssert.assertElementDisplayed(browser, libraryVersionXpath);

		if (logger.isInfoEnabled()) {

			WebElement libraryVersionElement = browser.findElementByXpath(libraryVersionXpath);
			logger.info(libraryVersionElement.getText());
		}
	}

	protected void clearAllFields(Browser browser) {

		browser.clear(getFirstNameFieldXpath());
		browser.clear(getLastNameFieldXpath());
		browser.clear(getEmailAddressFieldXpath());
		browser.clear(getPhoneNumberFieldXpath());
		browser.clear(getDateOfBirthFieldXpath());
		browser.clear(getCityFieldXpath());
		clearProvince(browser);
		browser.clear(getPostalCodeFieldXpath());
	}

	protected void clearProvince(Browser browser) {
		createSelect(browser, getProvinceIdFieldXpath()).selectByVisibleText("Select");
	}

	protected final Select createSelect(Browser browser, String selectXpath) {

		WebElement selectField = browser.findElementByXpath(selectXpath);

		return new Select(selectField);
	}

	protected String getCityFieldXpath() {
		return "//input[contains(@id,':city')]";
	}

	protected String getCommentsXpath() {
		return "//textarea[contains(@id,':comments')]";
	}

	protected String getConfimationFormXpath() {
		return "//form[@method='post']";
	}

	protected String getDateOfBirthFieldXpath() {
		return "//input[contains(@id,':dateOfBirth')]";
	}

	protected String getDatePatternPreferencesXpath() {
		return "//input[contains(@id,':datePattern')]";
	}

	protected String getEditModeXpath() {
		return "//a[contains(@id,'editLink')]";
	}

	protected String getEmailAddressFieldXpath() {
		return "//input[contains(@id,':emailAddress')]";
	}

	protected String getExtraLibraryName() {
		return null;
	}

	protected String getFieldErrorXpath(String fieldXpath) {
		return fieldXpath + "/../span[@class='portlet-msg-error']";
	}

	protected String getFileUploadChooserXpath() {
		return "//input[@type='file']";
	}

	protected String getFirstNameFieldXpath() {
		return "//input[contains(@id,':firstName')]";
	}

	protected String getLastNameFieldXpath() {
		return "//input[contains(@id,':lastName')]";
	}

	protected String getLogoXpath() {
		return "//img[contains(@src, 'liferay-logo.png')]";
	}

	protected String getPhoneNumberFieldXpath() {
		return "//input[contains(@id,':phoneNumber')]";
	}

	protected String getPostalCodeFieldXpath() {
		return "//input[contains(@id,':postalCode')]";
	}

	protected String getPostalCodeToolTipXpath() {
		return "//img[contains(@title, 'Type any of these ZIP codes')]";
	}

	protected String getPreferencesResetButtonXpath() {
		return "//input[@type='submit'][@value='Reset']";
	}

	protected String getPreferencesSubmitButtonXpath() {
		return "//input[@type='submit'][@value='Submit']";
	}

	protected String getProvinceIdFieldXpath() {
		return "//select[contains(@id,':provinceId')]";
	}

	protected String getShowHideCommentsLinkXpath() {
		return "//a[contains(text(), 'Show Comments') or contains(text(), 'Hide Comments')]";
	}

	protected String getSubmitAnotherApplicationButton() {
		return "//input[@type='submit'][contains(@value, 'Submit Another Application')]";
	}

	protected String getSubmitButtonXpath() {
		return "//input[@type='submit'][@value='Submit']";
	}

	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post'][@enctype='multipart/form-data']/input[@type='submit'][@value='Submit']";
	}

	protected String getUploadedFileXpath() {
		return "//tr[@class='portlet-section-body results-row']/td[2]";
	}

	protected void resetBrowser() {

		// Reset everything in case there was an error.
		Browser browser = Browser.getInstance();
		browser.manage().deleteAllCookies();
		signIn(browser);
		browser.get(TestUtil.DEFAULT_BASE_URL + getContext());
	}

	protected void selectDate(Browser browser) {
		browser.sendKeys(getDateOfBirthFieldXpath(), "01/02/3456");
	}

	protected void selectProvince(Browser browser) {
		createSelect(browser, getProvinceIdFieldXpath()).selectByVisibleText("FL");
	}

	protected final void sendKeysTabAndWaitForRerender(Browser browser, String xpath, CharSequence... keys) {

		Actions actions = browser.createActions();
		WebElement element = browser.findElementByXpath(xpath);
		actions.sendKeys(element, keys);
		actions.sendKeys(Keys.TAB);
		browser.performAndWaitForRerender(actions.build(), xpath);
	}

	protected void submitFile(Browser browser) {

		browser.click(getSubmitFileButtonXpath());
		browser.waitForElementDisplayed(getUploadedFileXpath());
	}
}
