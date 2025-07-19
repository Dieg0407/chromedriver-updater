package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.Version;
import java.io.File;

public class Chrome {
  private final File chromeLocation;
  private final Os os;

  /**
   * Constructs a Chrome instance with the specified location and operating system.
   *
   * @param location the file path to the Chrome executable
   * @param os the operating system on which Chrome is running
   * @throws IllegalArgumentException if the file does not exist at the specified location
   */
  public Chrome(final String location, final Os os) throws IllegalArgumentException {
    assert location != null && !location.isEmpty() : "Location cannot be null or empty";
    assert os != null : "Operating system cannot be null";

    final File file = new File(location);
    if (!file.exists()) {
      throw new IllegalArgumentException("File does not exist: " + location);
    }

    this.chromeLocation = file;
    this.os = os;
  }

  public Version getVersion() {
    switch (this.os) {
      case WINDOWS -> {
        return getWindowsVersion();
      }
      case LINUX -> {
        // TODO: Implement Linux version detection
        throw new UnsupportedOperationException("Linux version detection is not implemented yet.");
      }
      case MACOS -> {
        // TODO: Implement macOS version detection
        throw new UnsupportedOperationException("macOS version detection is not implemented yet.");
      }
      default -> {
        throw new UnsupportedOperationException("Unsupported OS: " + this.os);
      }
    }
  }

  // windows doesn't implement a proper cli to get the version
  // so we infer it from the folder version name
  private Version getWindowsVersion() {
    final String path = this.chromeLocation.getParent();

    final File folder = new File(path);
    if (!folder.exists() || !folder.isDirectory()) {
      throw new IllegalArgumentException("The specified path is not a valid directory: " + path);
    }

    final File[] elements = folder.listFiles();
    assert elements != null : "Folder should not be empty";
    for (File element : elements) {
      if (!element.isDirectory()) {
        continue;
      }

      final String name = element.getName();
      if (!name.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
        continue;
      }

      return Version.fromRawVersion(name);
    }

    throw new IllegalArgumentException("No valid version directory found in: " + path);
  }

}
