package com.dieg0407.utils.chromedriver;

import com.dieg0407.utils.chromedriver.model.ProcessHandler;
import com.dieg0407.utils.chromedriver.model.Version;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ChromedriverTest {
  File temporalFile;
  ProcessHandler processHandler;

  @BeforeEach
  void setUp() throws IOException {
    temporalFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
    processHandler = Mockito.mock(ProcessHandler.class);
  }

  @Test
  void checkIfPathToChromeDriverExists() {
    Assertions.assertDoesNotThrow(() -> new Chromedriver(temporalFile.getAbsolutePath(), processHandler));
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Chromedriver(UUID.randomUUID().toString(), processHandler));
  }

  @Test
  void checkIfProcessHandlerIsNotNull() {
    Assertions.assertThrows(AssertionError.class, () -> new Chromedriver(temporalFile.getAbsolutePath(), null));
  }

  @Test
  void checkIfVersionFollowsExpectedFormat() throws Exception {
    final String mockedVersion = "somerandomoutput";
    Mockito.when(processHandler.getOutput(Mockito.any(), Mockito.anyLong()))
        .thenReturn(mockedVersion);

    final Chromedriver chromedriver = new Chromedriver(temporalFile.getAbsolutePath(), processHandler);
    final RuntimeException ex = Assertions.assertThrows(RuntimeException.class, chromedriver::getVersion);

    Assertions.assertTrue(ex.getMessage().contains("Unexpected output format"));
  }
  @Test
  void checkIfItExtractsTheVersion() throws Exception {
    final String mockedVersion = "ChromeDriver 123.456.7890.12 (randomsha)";
    Mockito.when(processHandler.getOutput(Mockito.any(), Mockito.anyLong()))
        .thenReturn(mockedVersion);

    final Chromedriver chromedriver = new Chromedriver(temporalFile.getAbsolutePath(), processHandler);
    final Version expectedVersion = new Version((short) 123, (short) 456, (short) 7890, (short) 12);
    Assertions.assertEquals(expectedVersion, chromedriver.getVersion());

  }
}
