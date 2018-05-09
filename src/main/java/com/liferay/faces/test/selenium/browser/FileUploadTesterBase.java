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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.ClassRule;

import org.junit.rules.TemporaryFolder;

import com.liferay.faces.test.selenium.util.ClosableUtil;


/**
 * @author  Kyle Stiemann
 */
public class FileUploadTesterBase extends BrowserDriverManagingTesterBase {

	// Public Constants
	@ClassRule
	public static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

	/**
	 * Obtains the path to a test resource on the file system. If the file does not exist, this method copies the test
	 * resource data to a file with the same name in the specified temp sub directory.
	 *
	 * @param   resourceFileName  The name of file both on the file system and on the classpath.
	 *
	 * @return  The path to the test resource file on the file system.
	 *
	 * @throws  IOException
	 */
	protected final String getFileSystemPathForResource(String resourceFileName) throws IOException {

		File tempDir = TEMPORARY_FOLDER.getRoot();
		File resourceFile = new File(tempDir, resourceFileName);

		if (!resourceFile.exists()) {

			InputStream inputStream = null;

			try {

				resourceFile = TEMPORARY_FOLDER.newFile(resourceFileName);
				inputStream = getClass().getClassLoader().getResourceAsStream(resourceFileName);
				Files.copy(inputStream, resourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			finally {
				ClosableUtil.close(inputStream);
			}
		}

		return resourceFile.getPath();
	}
}
