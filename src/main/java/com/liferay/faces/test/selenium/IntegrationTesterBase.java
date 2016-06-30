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

import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 * @author  Kyle Stiemann
 */
public abstract class IntegrationTesterBase {

	/**
	 * {@link TestSuiteListener#testRunStarted()} is used to sign in to the container when the tests are run with the
	 * maven-surefire-plugin. However, {@link TestSuiteListener#testRunStarted()} is not called when the tests are not
	 * run with the maven-surefire-plugin (i.e. when the tests are run from an IDE). So when the tests are run from an
	 * IDE, it is necessary to sign in to the container before each test class is run.
	 */
	@BeforeClass
	public static void setUp() {

		if (!TestUtil.RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN && !TestUtil.CONTAINER.equals("tomcat")) {
			TestUtil.signIn();
		}
	}

	/**
	 * {@link TestSuiteListener#testRunFinished()} is used to shut down the browser/webDriver when the tests are run
	 * with the maven-surefire-plugin. However, {@link TestSuiteListener#testRunFinished()} is not called when the tests
	 * are not run with the maven-surefire-plugin (i.e. when the tests are run from an IDE). So when the tests are run
	 * from an IDE, it is necessary to shutdown the browser after each test class is run.
	 */
	@AfterClass
	public static void tearDown() {

		if (!TestUtil.RUNNING_WITH_MAVEN_SUREFIRE_PLUGIN) {

			// When the browser is phantomjs or chrome, WebDriver.close() does not quit the browser (like it is
			// supposed to
			// https://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/WebDriver.html#quit%28%29),
			// so we use WebDriver.quit() instead.
			Browser.getInstance().quit();
		}
	}
}
