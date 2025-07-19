package com.dieg0407.utils.chromedriver.model;

public record Version(
    short major,
    short minor,
    short patch,
    short build
) {
  public static Version fromRawVersion(String version) {
    if (!version.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
      throw new IllegalArgumentException("Version string must be in the format 'major.minor.patch.build'");
    }
    final String[] parts = version.split("\\.");
    assert parts.length == 4 : "Directory name should have exactly 4 parts";

    return new Version(
        Short.parseShort(parts[0]),
        Short.parseShort(parts[1]),
        Short.parseShort(parts[2]),
        Short.parseShort(parts[3])
    );
  }

  /**
   * Checks if this version is less than the other version.
   * It doesn't check the build number or the patch
   * @param other the other version to compare with
   * @return true if this version is less than the other version, false otherwise
   */
  public boolean isLessThan(final Version other) {
    if (this.major < other.major) {
      return true;
    } else if (this.major > other.major) {
      return false;
    }
    return this.minor < other.minor;
  }

  /**
   * Returns a string representation of the version in the format "major.minor.patch.build".
   * @return a string representation of the version
   */
  public String identifier() {
    return String.format("%d.%d.%d.%d", major, minor, patch, build);
  }
}
