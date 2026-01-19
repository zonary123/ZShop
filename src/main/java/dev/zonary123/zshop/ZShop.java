package dev.zonary123.zshop;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zonary123.zshop.commands.Commands;
import dev.zonary123.zshop.configs.Config;
import dev.zonary123.zshop.configs.Lang;
import dev.zonary123.zshop.configs.Shops;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ZShop extends JavaPlugin {
  public static Map<String, Item> ITEMS = new HashMap<>();
  private static ZShop INSTANCE;
  private Config config = new Config();
  private Lang lang = new Lang();

  public ZShop(@Nonnull JavaPluginInit init) {
    super(init);
    INSTANCE = this;
  }

  @Override
  protected void setup() {
    super.setup();
    this.getEventRegistry().register(LoadedAssetsEvent.class, Item.class, ZShop::onItemAssetLoad);
    getLogger().atInfo().log(
      "Starting ZShop v%s", getPath().toAbsolutePath().toString()
    );
    Commands.register();
    reload();
  }


  public void reload() {
    files();

  }

  private static void onItemAssetLoad(LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>> event) {
    ITEMS = event.getAssetMap().getAssetMap();
  }

  private void files() {
    config.init();
    lang.init();
    Shops.init();
  }


  public static ZShop get() {
    return INSTANCE;
  }

  public static Path getPath() {
    return get().getDataDirectory();
  }

  public static HytaleLogger getLog() {
    return get().getLogger();
  }

}

