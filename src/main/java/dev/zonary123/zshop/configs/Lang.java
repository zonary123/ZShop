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
public class Lang {
  private String prefix = "&6[&eZShop&6]&r ";
  private String reloadSuccess = "&aZShop configuration reloaded successfully.";
  private String noPermission = "&cYou do not have permission to perform this action.";
  private String shopNotFound = "&cShop not found.";
  private String productNotFound = "&cProduct not found.";
  private String insufficientFunds = "&cYou do not have enough funds to complete this purchase.";
  private String insufficientItems = "&cYou do not have enough items to complete this sale.";
  private String purchaseSuccess = "&aYou have successfully purchased %amount% x %product% for %price%.";
  private String saleSuccess = "&aYou have successfully sold %amount% x %product% for %price%.";
  private String shopCreated = "&aShop '%shop%' has been created successfully.";
  private String shopDeleted = "&aShop '%shop%' has been deleted successfully.";
  private String productAdded = "&aProduct '%product%' has been added to shop '%shop%'.";
  private String productRemoved = "&aProduct '%product%' has been removed from shop '%shop%'.";
  
  public void init() {
    Config config = ZShop.get().getConfig();
    Path path = ZShop.getPath().resolve("lang").resolve(config.getLang() + ".json");
    try {

      Lang lang = UtilsFile.read(path, Lang.class);
      if (lang == null) lang = this;
      lang.fix();
      ZShop.get().setLang(lang);
      UtilsFile.write(path, lang);
    } catch (IOException e) {
      ZShop.getLog().atInfo().withCause(e).log("Failed to load config.json");
      e.printStackTrace();
    }
  }

  private void fix() {
  }
}
