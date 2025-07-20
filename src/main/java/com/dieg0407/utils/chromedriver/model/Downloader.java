package com.dieg0407.utils.chromedriver.model;

import static java.lang.String.format;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;

public interface Downloader {

  /**
   * Downloads a file from the specified URL to the given destination. If the file already exists at
   * the destination, then it will be overwritten.
   *
   * @param uri         the URI of the file to download
   * @param destination the destination file where the downloaded content will be saved
   * @return the downloaded file
   */
  File download(final URI uri, final File destination);

  class DownloaderImpl implements Downloader {

    @Override
    public File download(final URI uri, final File destination) {
      assert uri != null : "URL cannot be null";
      assert destination != null : "Destination cannot be null";

      if (destination.exists()) {
        if (!destination.delete()) {
          throw new RuntimeException(format(
              "Failed to delete existing file at '%s'. Please ensure you have the necessary permissions.",
              destination.getAbsolutePath()
          ));
        }
      }

      try (final HttpClient client = HttpClient.newHttpClient()) {
        final var request = java.net.http.HttpRequest.newBuilder()
            .uri(uri)
            .build();

        // Send the request and receive the response
        final var response = client.send(request, BodyHandlers.ofFile(destination.toPath()));

        if (response.statusCode() != 200) {
          throw new RuntimeException(format(
              "Failed to download file from '%s'. HTTP status code: %d",
              uri, response.statusCode()
          ));
        }
      } catch (Exception e) {
        throw new RuntimeException("Error downloading file: " + e.getMessage(), e);
      }

      return destination;
    }
  }
}
