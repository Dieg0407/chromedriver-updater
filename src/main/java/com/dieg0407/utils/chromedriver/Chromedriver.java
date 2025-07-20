package com.dieg0407.utils.chromedriver;

import static java.lang.String.format;

import com.dieg0407.utils.chromedriver.model.Downloader;
import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.ProcessHandler;
import com.dieg0407.utils.chromedriver.model.Version;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Chromedriver {

  public static final String VERSIONS_FOLDER = "versions";
  public static final String DOWNLOAD_URL_TEMPLATE = "https://storage.googleapis.com/chrome-for-testing-public/%s/%s/chromedriver-%s.zip";

  private final File chromedriverLocation;
  private final Os os;
  private final ProcessHandler processHandler;
  private final Downloader downloader;

  /**
   * Constructs a Chromedriver instance with the specified location and process handler.
   *
   * @param location       the file path to the ChromeDriver executable
   * @param processHandler the handler for executing processes
   * @param os             the operating system on which ChromeDriver is running
   * @param downloader     the downloader for fetching ChromeDriver updates
   * @throws IllegalArgumentException if the file does not exist at the specified location or if any
   *                                  parameter is null
   */
  public Chromedriver(final String location, final Os os, final ProcessHandler processHandler,
      final Downloader downloader) throws IllegalArgumentException {
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
      final ProcessBuilder builder = new ProcessBuilder(chromedriverLocation.getAbsolutePath(),
          "--version")
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

    // check if version folders exists in location
    final File versionsFolder = new File(chromedriverLocation.getParentFile(), VERSIONS_FOLDER);
    if (!versionsFolder.exists() && !versionsFolder.mkdirs()) {
      throw new RuntimeException(
          "Failed to create versions folder: " + versionsFolder.getAbsolutePath());
    }

    final String type = getOsType(os);
    try {
      final String downloadFileName = format("chromedriver-%s.%s.zip", chromeVersion.identifier(),
          type);
      final URI downloadUrl = new URI(
          format(DOWNLOAD_URL_TEMPLATE, chromeVersion.identifier(), type, type));

      System.out.printf(
          "Downloading ChromeDriver version %s for %s into %s\n",
          chromeVersion.identifier(), type, downloadFileName
      );

      final File zipFile = downloader.download(downloadUrl,
          new File(versionsFolder, downloadFileName));
      if (!zipFile.exists()) {
        throw new RuntimeException("Failed to download ChromeDriver: " + zipFile.getAbsolutePath());
      }

      System.out.printf("Downloaded ChromeDriver version %s for %s into %s\n",
          chromeVersion.identifier(), type, downloadFileName);

      File extractedFile = null;
      try (InputStream inputStream = new FileInputStream(zipFile);
          ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
          System.out.printf("Processing entry: %s\n", zipEntry.getName());
          if (!zipEntry.isDirectory() && zipEntry.getName().endsWith("/chromedriver") ||
              zipEntry.getName().endsWith("/chromedriver.exe")) {

            extractedFile = new File(versionsFolder, "chromedriver");
            if (extractedFile.exists() && !extractedFile.delete()) {
              throw new RuntimeException(
                  "Failed to delete existing chromedriver: " + extractedFile.getAbsolutePath());
            }
            try (var outputStream = new FileOutputStream(extractedFile)) {
              byte[] buffer = new byte[1024];
              int len;
              while ((len = zipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
              }
            }
            System.out.printf("Extracted ChromeDriver to %s\n", extractedFile.getAbsolutePath());
            break;
          }
          zipEntry = zipInputStream.getNextEntry();
        }
      }

      if (extractedFile == null || !extractedFile.exists()) {
        throw new RuntimeException(
            "Failed to extract ChromeDriver from zip file: " + zipFile.getAbsolutePath());
      }

      // replace the existing chromedriver with the new one
      if (chromedriverLocation.exists() && !chromedriverLocation.delete()) {
        throw new RuntimeException(
            "Failed to delete existing chromedriver: " + chromedriverLocation.getAbsolutePath());
      }

      if (!extractedFile.renameTo(chromedriverLocation)) {
        throw new RuntimeException("Failed to rename extracted chromedriver to: "
            + chromedriverLocation.getAbsolutePath());
      }

      System.out.printf("Updated ChromeDriver to version %s at %s\n",
          chromeVersion.identifier(), chromedriverLocation.getAbsolutePath());

      // if linux execute chmod +x
      if (os == Os.LINUX) {
        ProcessBuilder chmodBuilder = new ProcessBuilder("chmod", "+x",
            chromedriverLocation.getAbsolutePath());
        processHandler.getOutput(chmodBuilder, 5000);
        System.out.println("Set executable permissions for ChromeDriver on Linux.");
      }
    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Update interrupted", e);
    }
  }

  private static String getOsType(final Os os) {
    return switch (os) {
      case WINDOWS -> "win64";
      case LINUX -> "linux64";
      case MACOS -> "mac-arm64"; // Assuming macOS is ARM64 for simplicity
    };
  }
}
