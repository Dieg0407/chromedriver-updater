package com.dieg0407.utils.chromedriver.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public interface ProcessHandler {

  /**
   * Executes the given ProcessBuilder and returns its output as a String.
   *
   * @param processBuilder the ProcessBuilder to execute
   * @param timeoutInMillis the maximum time to wait for the process to complete, in milliseconds
   * @return the output of the process as a String
   * @throws IOException if an I/O error occurs
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  String getOutput(final ProcessBuilder processBuilder, final long timeoutInMillis) throws IOException, InterruptedException;

  class ProcessHandlerImpl implements ProcessHandler {
    @Override
    public String getOutput(final ProcessBuilder processBuilder, final long timeoutInMillis) throws IOException, InterruptedException {
      assert processBuilder != null : "ProcessBuilder cannot be null";
      assert timeoutInMillis > 0 : "Timeout must be greater than 0 milliseconds";

      final Process process = processBuilder.start();
      process.waitFor(timeoutInMillis, TimeUnit.MILLISECONDS);
      try(final InputStream inputStream = process.getInputStream()) {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      }
    }
  }
}
