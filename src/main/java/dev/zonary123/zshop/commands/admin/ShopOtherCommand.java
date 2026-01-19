package dev.zonary123.zshop.commands.admin;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.zonary123.zshop.ui.ShopsIndexGui;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 9:18
 */
public class ShopOtherCommand extends AbstractAsyncCommand {
  private final RequiredArg<PlayerRef> playerArg = this.withRequiredArg(
    "player", "The player to open the shop for", ArgTypes.PLAYER_REF
  );

  public ShopOtherCommand(@NonNull String name, @NonNull String description) {
    super(name, description);
    this.requirePermission("zshop.admin.other");
  }

  @NonNullDecl
  @Override
  protected CompletableFuture<Void> executeAsync(CommandContext context) {
    PlayerRef playerRef = context.get(this.playerArg);
    UUID worldUuid = playerRef.getWorldUuid();
    if (worldUuid == null) {
      context.sendMessage(
        Message.raw(
          "§cCould not open the shop for the player because they are not in a world."
        )
      );
      return CompletableFuture.completedFuture(null);
    }
    World world = Universe.get().getWorld(worldUuid);
    if (world == null) {
      context.sendMessage(
        Message.raw(
          "§cCould not open the shop for the player because they are not in a world."
        )
      );
      return CompletableFuture.completedFuture(null);
    }
    return CompletableFuture.runAsync(() -> {
      var ref = playerRef.getReference();
      if (ref == null || !ref.isValid()) {
        context.sendMessage(
          Message.raw(
            "§cCould not open the shop for the player because they are not in a world."
          )
        );
        return;
      }
      var store = ref.getStore();
      var player = store.getComponent(ref, Player.getComponentType());
      if (player != null) {
        player.getPageManager().openCustomPage(
          ref,
          store,
          new ShopsIndexGui(
            playerRef,
            CustomPageLifetime.CanDismiss
          )
        );
      } else {
        context.sendMessage(
          Message.raw(
            "§cCould not open the shop for the player because they are not in a world."
          )
        );
      }
    }, world);
  }


}
