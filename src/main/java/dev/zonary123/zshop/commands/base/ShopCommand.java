package dev.zonary123.zshop.commands.base;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zshop.commands.admin.ReloadCommand;
import dev.zonary123.zshop.ui.ShopsIndexGui;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 9:18
 */
public class ShopCommand extends AbstractAsyncCommand {
  public ShopCommand(@NonNull String name, @NonNull String description) {
    super(name, description);
    this.addSubCommand(
      new ReloadCommand("reload", "Reload ZShop configuration")
    );
  }

  @NonNullDecl
  @Override
  protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
    CommandSender sender = commandContext.sender();
    if (sender instanceof Player player) {
      player.getWorldMapTracker().tick(0);
      Ref<EntityStore> ref = player.getReference();
      if (ref != null && ref.isValid()) {
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        return CompletableFuture.runAsync(() -> {
          PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
          if (playerRefComponent != null) {
            player.getPageManager().openCustomPage(ref, store, new ShopsIndexGui(playerRefComponent,
              CustomPageLifetime.CanDismiss));
          }
        }, world);
      } else {
        commandContext.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
        return CompletableFuture.completedFuture(null);
      }
    } else {
      return CompletableFuture.completedFuture(null);
    }
  }


}
