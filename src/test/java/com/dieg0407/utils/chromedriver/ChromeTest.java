package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.ProcessHandler;
import com.dieg0407.utils.chromedriver.model.Version;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ChromeTest {

  File temporalFile;
  ProcessHandler processHandler;

  File tmpDir;
  File versionDir;

  @BeforeEach
  void setUp() throws IOException {
    // Create a temporary directory for testing
    tmpDir = new File("tmp");
    if (!tmpDir.exists()) {
      tmpDir.mkdir();
    }

    temporalFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp", tmpDir);
    processHandler = Mockito.mock(ProcessHandler.class);
  }

  @AfterEach
  void tearDown() {
    if (temporalFile.exists()) {
      temporalFile.delete();
    }

    if (versionDir != null && versionDir.exists()) {
      versionDir.delete();
    }

    if (tmpDir.exists()) {
      tmpDir.delete();
    }
  }

  @Test
  void checkIfPathToChromeExists() {
    Assertions.assertDoesNotThrow(
        () -> new Chrome(temporalFile.getAbsolutePath(), Os.LINUX, processHandler));
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> new Chrome(UUID.randomUUID().toString(), Os.LINUX, processHandler));
  }

  @Test
  void checkIfOsIsNotNull() {
    Assertions.assertThrows(AssertionError.class,
        () -> new Chrome(temporalFile.getAbsolutePath(), null, processHandler));
  }

  @Test
  void checkIfProcessHandlerIsNotNull() {
    Assertions.assertThrows(AssertionError.class,
        () -> new Chrome(temporalFile.getAbsolutePath(), Os.LINUX, null));
  }

  @Test
  void checkIfVersionFollowsExpectedFormatInLinux() throws Exception {
    final String mockedVersion = "somerandomoutput";
    Mockito.when(processHandler.getOutput(Mockito.any(), Mockito.anyLong()))
        .thenReturn(mockedVersion);

    final Chrome chrome = new Chrome(temporalFile.getAbsolutePath(), Os.LINUX, processHandler);
    final RuntimeException ex = Assertions.assertThrows(RuntimeException.class, chrome::getVersion);

    Assertions.assertTrue(ex.getMessage().contains("Unexpected output format"));
  }

  @Test
  void checkIfVersionIsExtractedInLinux() throws Exception {
    final String mockedVersion = "Google Chrome 123.456.7890.12";
    Mockito.when(processHandler.getOutput(Mockito.any(), Mockito.anyLong()))
        .thenReturn(mockedVersion);

    final Chrome chrome = new Chrome(temporalFile.getAbsolutePath(), Os.LINUX, processHandler);
    final Version expectedVersion = new Version((short) 123, (short) 456, (short) 7890, (short) 12);
    Assertions.assertEquals(expectedVersion, chrome.getVersion());
  }

  @Test
  void checkIfVersionFolderIsNotFoundInWindows() {
    final Chrome chrome = new Chrome(temporalFile.getAbsolutePath(), Os.WINDOWS, processHandler);
    final RuntimeException ex = Assertions.assertThrows(IllegalArgumentException.class,
        chrome::getVersion);

    Assertions.assertTrue(ex.getMessage().contains("No valid version directory found in:"));
  }

  @Test
  void checkIfVersionIsExtractedInWindows() throws Exception {
    versionDir = new File(tmpDir, "123.456.7890.12");
    versionDir.mkdir();

    final Chrome chrome = new Chrome(temporalFile.getAbsolutePath(), Os.WINDOWS, processHandler);
    final Version expectedVersion = new Version((short) 123, (short) 456, (short) 7890, (short) 12);
    Assertions.assertEquals(expectedVersion, chrome.getVersion());
  }

}
