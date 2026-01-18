package dev.zonary123.zshop.commands.admin;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.zonary123.zshop.ZShop;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 10:24
 */
public class ReloadCommand extends CommandBase {
  public ReloadCommand(@NonNull String name, @NonNull String description) {
    super(name, description);
    this.requirePermission("zshop.admin.reload");
  }

  @Override protected void executeSync(@NonNull CommandContext context) {
    ZShop.get().reload();

  }
}
