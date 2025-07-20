package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.Downloader;
import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.ProcessHandler;
import com.dieg0407.utils.chromedriver.model.Version;
import java.io.File;
import java.io.IOException;

public class Chromedriver {
  public static final String VERSIONS_FOLDER = "versions";

  private final File chromedriverLocation;
  private final Os os;
  private final ProcessHandler processHandler;
  private final Downloader downloader;

  /**
   * Constructs a Chromedriver instance with the specified location and process handler.
   *
   * @param location the file path to the ChromeDriver executable
   * @param processHandler the handler for executing processes
   * @param os the operating system on which ChromeDriver is running
   * @param downloader the downloader for fetching ChromeDriver updates
   * @throws IllegalArgumentException if the file does not exist at the specified location or if any parameter is null
   */
  public Chromedriver(final String location, final Os os, final ProcessHandler processHandler, final Downloader downloader) throws IllegalArgumentException {
    assert location != null && !location.isEmpty() : "Location cannot be null or empty";
    assert processHandler != null : "ProcessHandler cannot be null";
    assert os != null : "Operating System cannot be null";
    assert downloader != null : "Downloader cannot be null";

    final File file = new File(location);
    if (!file.exists()) {
      throw new IllegalArgumentException("File does not exist: " + location);
    }

    this.chromedriverLocation = file;
    this.os = os;
    this.processHandler = processHandler;
    this.downloader = downloader;
  }

  public Version getVersion() {
    try {
      final ProcessBuilder builder = new ProcessBuilder(chromedriverLocation.getAbsolutePath(), "--version")
          .redirectErrorStream(true);
      final String output = processHandler.getOutput(builder, 5000);

      final String[] parts = output.split(" ");
      if (parts.length < 3) {
        throw new RuntimeException("Unexpected output format: " + output +
            ". Expected format: 'ChromeDriver x.x.x.x (code)'");
      }

      final String versionString = parts[1];
      return Version.fromRawVersion(versionString);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void updateTo(final Version chromeVersion) {
    assert chromeVersion != null : "Chrome version cannot be null";

  }
}
