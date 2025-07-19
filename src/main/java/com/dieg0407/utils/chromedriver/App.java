package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.Os;
import com.dieg0407.utils.chromedriver.model.ProcessHandler;
import com.dieg0407.utils.chromedriver.model.Version;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


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
      final ProcessHandler processHandler = new ProcessHandler.ProcessHandlerImpl();

      System.out.println("Detected OS: " + os);
      final Chrome chrome = new Chrome(chromeLocation, os, processHandler);
      final Chromedriver chromedriver = new Chromedriver(chromedriverLocation, processHandler);

      final Version chromeVersion = chrome.getVersion();
      final Version chromedriverVersion = chromedriver.getVersion();

      System.out.println("Chrome Version: " + chromeVersion);
      System.out.println("ChromeDriver Version: " + chromedriverVersion);

      if (!chromedriverVersion.isLessThan(chromeVersion)) {
        System.out.println("ChromeDriver is up to date!");
        return 0; // No update needed
      }

      chromedriver.updateTo(chromeVersion);

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
