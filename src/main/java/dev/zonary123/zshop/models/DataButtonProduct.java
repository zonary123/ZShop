package dev.zonary123.zshop.models;

import dev.zonary123.zutils.models.EconomySelector;
import lombok.*;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 11:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DataButtonProduct {
  private Product product;
  private EconomySelector economy;
}
