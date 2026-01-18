package dev.zonary123.zshop.configs;

import dev.zonary123.zshop.ZShop;
import dev.zonary123.zutils.utils.UtilsFile;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:09
 */
@Data
public class Config {
  private boolean debug = false;
  private String lang = "en_us";

  public void init() {
    Path path = ZShop.getPath().resolve("config.json");
    try {

      Config config = UtilsFile.read(path, Config.class);
      if (config == null) config = this;
      config.fix();
      ZShop.get().setConfig(config);
      UtilsFile.write(path, config);
    } catch (IOException e) {
      ZShop.getLog().atInfo().withCause(e).log("Failed to load config.json");
      e.printStackTrace();
    }
  }

  private void fix() {
  }
}
