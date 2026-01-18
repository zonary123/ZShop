package dev.zonary123.zshop.commands;

import dev.zonary123.zshop.ZShop;
import dev.zonary123.zshop.commands.base.ShopCommand;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 9:18
 */
public class Commands {
  public static void register() {
    var plugin = ZShop.get();
    plugin.getCommandRegistry().registerCommand(new ShopCommand("shop", "Main command for ZShop"));
  }
}
