package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.Version;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) {
    try {
      final Os os = detectOs();
      System.out.println("Detected OS: " + os);

      final Chrome chrome = new Chrome("/usr/bin/google-chrome", os);
      final Version chromeVersion = chrome.getVersion();

      System.out.println("Chrome Version: " + chromeVersion);
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  private static Os detectOs() {
    final String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      return Os.WINDOWS;
    } else if (osName.contains("nux")) {
      return Os.LINUX;
    } else if (osName.contains("mac")) {
      return Os.MACOS;
    } else {
      throw new UnsupportedOperationException("Unsupported OS: " + osName);
    }
  }
}
