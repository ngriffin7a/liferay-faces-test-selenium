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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
public final class BrowserDriverManagingTestSuiteListener extends RunListener {

	@Override
	public void testRunFinished(Result result) throws Exception {

		Files.walkFileTree(FileUploadTesterBase.OnDemandTemporaryFolder.INSTANCE.toPath(), new DeleteFileVisitor());
		BrowserDriverManagingTesterBase.doTearDown();
		super.testRunFinished(result);
	}

	private static final class DeleteFileVisitor extends SimpleFileVisitor<Path> {

		// Logger
		private static final Logger logger = LoggerFactory.getLogger(DeleteFileVisitor.class);

		private static FileVisitResult visitToDelete(Path path) {

			try {
				Files.delete(path);
			}
			catch (IOException e) {
				logger.error("Failed to delete temporary test file: " + path.toString(), e);
			}

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path directory, IOException ioException) throws IOException {
			return visitToDelete(directory);
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			return visitToDelete(file);
		}
	}
}
