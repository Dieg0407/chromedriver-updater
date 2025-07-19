package com.dieg0407.utils.chromedriver.model;


import org.junit.jupiter.api.Test;

class VersionTest {

  @Test
  void shouldCheckIfVersionIsLessThan() {
    final Version version1 = new Version((short) 100, (short) 0, (short) 0, (short) 0);
    final Version version2 = new Version((short) 101, (short) 0, (short) 0, (short) 0);
    final Version version3 = new Version((short) 100, (short) 1, (short) 0, (short) 0);

    assert version1.isLessThan(version2);
    assert version1.isLessThan(version3);

    assert !version2.isLessThan(version1);
    assert !version3.isLessThan(version1);
    assert !version2.isLessThan(version3);
  }
}
