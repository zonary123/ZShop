package dev.zonary123.zshop.models;

import lombok.*;

import java.math.BigDecimal;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Product {
  @Builder.Default
  private String product = "Rock_Crystal_Yellow_Large";
  @Builder.Default
  private BigDecimal buyPrice = BigDecimal.valueOf(10);
  @Builder.Default
  private BigDecimal sellPrice = BigDecimal.valueOf(0.25);

  public void fix() {
  }

}
