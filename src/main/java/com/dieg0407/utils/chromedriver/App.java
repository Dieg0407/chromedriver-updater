package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.Version;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(
    name = "chromedriver-updater",
    mixinStandardHelpOptions = true,
    version = "chromedriver 1.0",
    description = "A utility to update chromedriver"
)
public class App implements Callable<Integer> {

  public static void main(String... args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }

  @Option(names = { "--chrome-location", "-c" }, description = "Path to the Chrome executable", required = true)
  private String chromeLocation;

  @Option(names = { "--chromedriver-location", "-d" }, description = "Path to the ChromeDriver executable", required = true)
  private String chromedriverLocation;

  @Override
  public Integer call() {
    try {
      final Os os = detectOs();
      System.out.println("Detected OS: " + os);

      final Chrome chrome = new Chrome(chromeLocation, os);
      final Version chromeVersion = chrome.getVersion();
      System.out.println("Chrome Version: " + chromeVersion);
      return 0;
    } catch (Exception e) {
      e.printStackTrace(System.err);
      return 1;
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
