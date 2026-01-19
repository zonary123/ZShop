package dev.zonary123.zshop.models;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import dev.zonary123.zutils.models.EconomySelector;
import dev.zonary123.zutils.utils.UtilsFile;
import lombok.Data;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:05
 */
@Data
public class Shop {
  private transient File file;
  private transient String id;
  private String icon = "Ingredient_Fibre";
  private String name = "Weapons";
  private String title = "Weapons Shop";
  private String description = "Find the best weapons here!";
  private EconomySelector economy = new EconomySelector();
  private List<Product> products = new ArrayList<>(
    List.of(
      new Product()
    )
  );

  public Shop() {
  }

  public void fix() {
    for (Product product : products) {
      product.fix();
    }
  }

  public void addProduct(Product product) {
    for (Product p : products) {
      if (p.getProduct().equals(product.getProduct())) {
        return;
      }
    }
    products.add(product);
    save();
  }

  public void save() {
    Path path = file.toPath();
    try {
      UtilsFile.write(path, this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean containItem(Item item) {
    for (Product product : products) {
      if (product.getProduct().equals(item.getId())) {
        return true;
      }
    }
    return false;
  }
}
