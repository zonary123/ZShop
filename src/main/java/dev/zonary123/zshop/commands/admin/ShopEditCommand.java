package dev.zonary123.zshop.commands.admin;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.zonary123.zshop.configs.Shops;
import dev.zonary123.zshop.models.Shop;
import dev.zonary123.zshop.ui.ShopEditIndexGui;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 9:18
 */
public class ShopEditCommand extends AbstractAsyncCommand {
  private final RequiredArg<String> shopArg = this.withRequiredArg(
    "shop", "The shop to edit", ArgTypes.STRING
  );

  public ShopEditCommand(@NonNull String name, @NonNull String description) {
    super(name, description);
    this.requirePermission("zshop.admin.other");
  }

  @NonNullDecl
  @Override
  protected CompletableFuture<Void> executeAsync(CommandContext context) {
    Player player = context.senderAs(Player.class);
    String shopId = context.get(this.shopArg);


    World world = player.getWorld();
    if (world == null) {
      context.sendMessage(
        Message.raw(
          "§cCould not open the shop for the player because they are not in a world."
        )
      );
      return CompletableFuture.completedFuture(null);
    }
    return CompletableFuture.runAsync(() -> {
      Shop shop = Shops.getShopById(shopId);
      if (shop == null) {
        context.sendMessage(
          Message.raw(
            "§cCould not find a shop with the ID: " + shopId
          )
        );
        return;
      }
      var ref = player.getReference();
      if (ref == null || !ref.isValid()) {
        context.sendMessage(
          Message.raw(
            "§cCould not open the shop for the player because they are not in a world."
          )
        );
        return;
      }
      var store = ref.getStore();
      var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
      if (playerRef == null) {
        context.sendMessage(
          Message.raw(
            "§cCould not open the shop for the player because their PlayerRef component is missing."
          )
        );
        return;
      }
      player.getPageManager().openCustomPage(
        ref,
        store,
        new ShopEditIndexGui(
          playerRef,
          CustomPageLifetime.CanDismiss,
          shop
        )
      );
    }, world);
  }


}
