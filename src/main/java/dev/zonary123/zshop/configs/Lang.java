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
  private String titleShops = "ZShop - Shops";
  private String titleProducts = "ZShop - Products in %shop%";
  private String buyPrice = "Buy: %price% each";
  private String sellPrice = "Sell: %price% each";
  private String purchaseReason = "Purchase of %amount%x %product%";
  private String purchaseSuccess = "&aYou have purchased %amount%x %product% for %total_price%";
  private String purchaseFailFunds = "&cYou do not have enough funds to purchase %amount%x %product% for %total_price%";
  private String purchaseFailSpace = "&cYou do not have enough inventory space to purchase %amount%x %product%";
  private String sellReason = "Sale of %amount%x %product%";
  private String sellSuccess = "&aYou have sold %amount%x %product% for %total_price%";
  private String sellFailProduct = "&cYou do not have %amount%x %product% to sell";
  private String sellFailRollback = "&cThe sale of %amount%x %product% could not be completed due to a rollback";

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
