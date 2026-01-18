package dev.zonary123.zshop.configs;

import dev.zonary123.zshop.ZShop;
import dev.zonary123.zshop.models.Shop;
import dev.zonary123.zutils.utils.UtilsFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 9:21
 */
public class Shops {
  public static final List<Shop> SHOPS = new ArrayList<>();

  public static void init() {
    SHOPS.clear();
    Path path = ZShop.getPath().resolve("shops");
    var files = path.toFile().listFiles();
    if (files != null) {
      for (var file : files) {
        if (file.isFile() && file.getName().endsWith(".json")) {
          try {
            Shop shop = UtilsFile.read(file.toPath(), Shop.class);
            if (shop != null) {
              shop.setId(file.getName().replace(".json", ""));
              SHOPS.add(shop);
            }
          } catch (Exception e) {
            ZShop.getLog().atInfo().withCause(e).log("Failed to load shop file: " + file.getName());
            e.printStackTrace();
          }
        }
      }
    } else {
      Shop shop = new Shop();
      shop.setId("default");
      SHOPS.add(shop);
      ZShop.getLog().atInfo().log("No shop files found in " + path.toString());
    }
    for (Shop shop : SHOPS) {
      try {
        shop.fix();
        UtilsFile.write(path.resolve(shop.getId() + ".json"), shop);
      } catch (Exception e) {
        ZShop.getLog().atInfo().withCause(e).log("Failed to write shop file: " + shop.getId() + ".json");
        e.printStackTrace();
      }
    }
  }

  public static Shop getShopById(String button) {
    for (Shop shop : SHOPS) {
      if (shop.getId().equals(button)) {
        return shop;
      }
    }
    return null;
  }
}
