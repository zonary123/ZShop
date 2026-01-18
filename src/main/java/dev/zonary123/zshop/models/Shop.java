package dev.zonary123.zshop.models;

import dev.zonary123.zutils.models.EconomySelector;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:05
 */
@Data
public class Shop {
  private transient String id;
  private String icon = "Ingredient_Fibre";
  private String title = "Default Shop Title";
  private String description = "This is a default shop description.";
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
}
