package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.Downloader;
import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.ProcessHandler;
import com.dieg0407.utils.chromedriver.model.Version;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ChromedriverTest {

  File chromedriverExecutable;
  ProcessHandler processHandler;
  Downloader downloader;

  File downloadedZipFile;

  File tmpDir;

  @BeforeEach
  void setUp() throws IOException, URISyntaxException {
    chromedriverExecutable = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
    Files.write(chromedriverExecutable.toPath(), "mocked content".getBytes());

    URL testZipFile = getClass().getClassLoader().getResource("test-chromedriver.zip");
    if (testZipFile == null) {
      throw new IllegalStateException("Test zip file not found in resources");
    }
    downloadedZipFile = new File(testZipFile.toURI());

    processHandler = Mockito.mock(ProcessHandler.class);
    downloader = Mockito.mock(Downloader.class);

    tmpDir = new File("tmp");
    if (!tmpDir.exists()) {
      tmpDir.mkdir();
    }
  }

  @AfterEach
  void tearDown() throws  IOException {
    if (chromedriverExecutable.exists()) {
      chromedriverExecutable.delete();
    }

    if (tmpDir.exists()) {
      Files.walk(tmpDir.toPath())
          .forEach(path -> {
            try {
              Files.delete(path);
            } catch (IOException e) {
              throw new RuntimeException("Failed to delete file: " + path, e);
            }
          });
      tmpDir.delete();
    }
  }

  @Test
  void checkIfPathToChromeDriverExists() {
    Assertions.assertDoesNotThrow(
        () -> new Chromedriver(chromedriverExecutable.getAbsolutePath(), Os.LINUX, processHandler,
            downloader));
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> new Chromedriver(UUID.randomUUID().toString(), Os.LINUX, processHandler, downloader));
  }

  @Test
  void checkIfProcessHandlerIsNotNull() {
    Assertions.assertThrows(AssertionError.class,
        () -> new Chromedriver(chromedriverExecutable.getAbsolutePath(), Os.LINUX, null, downloader));
    Assertions.assertThrows(AssertionError.class,
        () -> new Chromedriver(chromedriverExecutable.getAbsolutePath(), Os.LINUX, null, downloader));
    Assertions.assertThrows(AssertionError.class,
        () -> new Chromedriver(chromedriverExecutable.getAbsolutePath(), Os.LINUX, processHandler, null));
  }

  @Test
  void checkIfVersionFollowsExpectedFormat() throws Exception {
    final String mockedVersion = "somerandomoutput";
    Mockito.when(processHandler.getOutput(Mockito.any(), Mockito.anyLong()))
        .thenReturn(mockedVersion);

    final Chromedriver chromedriver = new Chromedriver(this.chromedriverExecutable.getAbsolutePath(), Os.LINUX,
        processHandler, downloader);
    final RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
        chromedriver::getVersion);

    Assertions.assertTrue(ex.getMessage().contains("Unexpected output format"));
  }

  @Test
  void checkIfItExtractsTheVersion() throws Exception {
    final String mockedVersion = "ChromeDriver 123.456.7890.12 (randomsha)";
    Mockito.when(processHandler.getOutput(Mockito.any(), Mockito.anyLong()))
        .thenReturn(mockedVersion);

    final Chromedriver chromedriver = new Chromedriver(this.chromedriverExecutable.getAbsolutePath(), Os.LINUX,
        processHandler, downloader);
    final Version expectedVersion = new Version((short) 123, (short) 456, (short) 7890, (short) 12);
    Assertions.assertEquals(expectedVersion, chromedriver.getVersion());

  }

  @Test
  void checkUpdateToShouldReplaceOriginal() throws Exception {
    Mockito.when(downloader.download(Mockito.any(), Mockito.any()))
        .thenReturn(downloadedZipFile);

    String content = Files.readString(this.chromedriverExecutable.toPath()).trim();
    final Chromedriver chromedriver = new Chromedriver(this.chromedriverExecutable.getAbsolutePath(), Os.LINUX, processHandler, downloader);
    chromedriver.updateTo(new Version((short) 123, (short) 456, (short) 7890, (short) 12));

    String updatedContent = Files.readString(this.chromedriverExecutable.toPath()).trim();

    Assertions.assertNotEquals(content, updatedContent, "The content of the chromedriver should be updated");
  }
}
