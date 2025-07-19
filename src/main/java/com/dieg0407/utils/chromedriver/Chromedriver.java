package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.ProcessHandler;
import com.dieg0407.utils.chromedriver.model.Version;
import java.io.File;

public class Chromedriver {
  private final File chromedriverLocation;
  private final ProcessHandler processHandler;

  /**
   * Constructs a Chromedriver instance with the specified location and process handler.
   *
   * @param location the file path to the ChromeDriver executable
   * @param processHandler the handler for executing processes
   * @throws IllegalArgumentException if the file does not exist at the specified location or if any parameter is null
   */
  public Chromedriver(final String location, final ProcessHandler processHandler) throws IllegalArgumentException {
    assert location != null && !location.isEmpty() : "Location cannot be null or empty";
    assert processHandler != null : "ProcessHandler cannot be null";

    final File file = new File(location);
    if (!file.exists()) {
      throw new IllegalArgumentException("File does not exist: " + location);
    }

    this.chromedriverLocation = file;
    this.processHandler = processHandler;
  }

  public Version getVersion() {
    try {
      final ProcessBuilder builder = new ProcessBuilder(chromedriverLocation.getAbsolutePath(), "--version")
          .redirectErrorStream(true);
      final String output = processHandler.getOutput(builder, 5000);

      final String[] parts = output.split(" ");
      if (parts.length < 2) {
        throw new RuntimeException("Unexpected output format: " + output +
            ". Expected format: 'ChromeDriver x.x.x.x'");
      }

      final String versionString = parts[1];
      return Version.fromRawVersion(versionString);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get ChromeDriver version", e);
    }
  }
}
