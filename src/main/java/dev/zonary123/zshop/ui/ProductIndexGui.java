package dev.zonary123.zshop.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.command.system.MatchResult;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zshop.ZShop;
import dev.zonary123.zshop.models.DataButtonProduct;
import dev.zonary123.zshop.models.Product;
import dev.zonary123.zshop.models.Shop;
import dev.zonary123.zutils.api.EconomyApi;
import dev.zonary123.zutils.models.EconomySelector;
import dev.zonary123.zutils.utils.FormatMessage;
import dev.zonary123.zutils.utils.UtilsFile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;

public class ProductIndexGui extends InteractiveCustomUIPage<ProductIndexGui.BindingData> {
  private final Shop shop;
  private String searchQuery = "";
  private int amount;

  private final Map<String, Product> visibleProductsMap = new LinkedHashMap<>();

  public ProductIndexGui(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime, Shop shop) {
    super(playerRef, lifetime, BindingData.CODEC);
    this.shop = shop;
  }

  @Getter
  @Setter
  public static class BindingData {
    static final String KEY_DATA = "Data";
    static final String KEY_INPUT_FIELD = "@InputField";
    static final String KEY_SEARCH_QUERY = "@SearchQuery";

    public static final BuilderCodec<BindingData> CODEC = BuilderCodec.builder(BindingData.class, BindingData::new)
      .addField(new KeyedCodec<>(KEY_DATA, Codec.STRING), (d, s) -> d.data = s, d -> d.data)
      .addField(new KeyedCodec<>(KEY_INPUT_FIELD, Codec.INTEGER), (d, i) -> d.amount = i, d -> d.amount)
      .addField(new KeyedCodec<>(KEY_SEARCH_QUERY, Codec.STRING), (d, s) -> d.searchQuery = s, d -> d.searchQuery)
      .build();

    private String data;
    private Integer amount;
    private String searchQuery;

    public DataButtonProduct getData() {
      return UtilsFile.getGson().fromJson(this.data, DataButtonProduct.class);
    }
  }

  @Override
  public void build(@Nonnull Ref<EntityStore> ref,
                    @Nonnull UICommandBuilder uiCommandBuilder,
                    @Nonnull UIEventBuilder uiEventBuilder,
                    @Nonnull Store<EntityStore> store) {

    uiCommandBuilder.append("Pages/ProductsIndex.ui");
    uiCommandBuilder.set("#SearchInput.Value", this.searchQuery);
    uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SearchInput",
      EventData.of("@SearchQuery", "#SearchInput.Value"), false);

    buildProductList(uiCommandBuilder, uiEventBuilder, store.getComponent(ref, PlayerRef.getComponentType()));
  }

  private void buildProductList(@Nonnull UICommandBuilder commandBuilder,
                                @Nonnull UIEventBuilder eventBuilder, PlayerRef playerRef) {

    ObjectArrayList<SearchResult> results = new ObjectArrayList<>();

    // Crear SearchResult para cada producto
    for (Product product : shop.getProducts()) {
      results.add(new SearchResult(product.getProduct(), product, MatchResult.EXACT));
    }

    if (searchQuery != null && !searchQuery.isBlank()) {
      String[] terms = searchQuery.split(" ");

      for (String s : terms) {
        String term = s.toLowerCase(Locale.ENGLISH);

        for (int i = results.size() - 1; i >= 0; i--) {
          SearchResult r = results.get(i);

          ItemStack stack = new ItemStack(r.product.getProduct(), 1);
          var translatedName = I18nModule.get().getMessage(this.playerRef.getLanguage(), stack.getItem().getTranslationKey());
          translatedName = translatedName != null ? translatedName.toLowerCase(Locale.ENGLISH) : "";

          MatchResult match = translatedName.contains(term) || r.name.toLowerCase(Locale.ENGLISH).contains(term)
            ? MatchResult.EXACT
            : MatchResult.NONE;

          if (match == MatchResult.NONE) {
            results.remove(i);
          } else {
            r.match = r.match.min(match);
          }
        }
      }
    }

    results.sort(SearchResult.COMPARATOR);

    visibleProductsMap.clear();
    for (SearchResult r : results) {
      visibleProductsMap.put(r.name, r.product);
    }

    // Renderizar la UI con los productos visibles
    renderUI(commandBuilder, eventBuilder);
  }

  private void renderUI(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder) {
    commandBuilder.clear("#IndexCards");
    int rowIndex = 0;
    int cardsInRow = 0;

    for (Product product : visibleProductsMap.values()) {
      if (cardsInRow == 0)
        commandBuilder.appendInline("#IndexCards", "Group { LayoutMode: Left; Anchor: (Bottom: 0); }");

      String indexCardSelector = "#IndexCards[" + rowIndex + "]";
      commandBuilder.append(indexCardSelector, "Pages/ProductEntry.ui");
      String cardSelector = indexCardSelector + "[" + cardsInRow + "]";

      ItemStack stack = new ItemStack(product.getProduct(), 1);

      commandBuilder.set(cardSelector + " #IndexIcon.ItemId", stack.getItem().getId());
      commandBuilder.set(cardSelector + " #IndexBuyPrice.Text", ZShop.get().getLang().getBuyPrice().replace("%price%", product.getBuyPrice().toString()));
      commandBuilder.set(cardSelector + " #IndexSellPrice.Text", ZShop.get().getLang().getSellPrice().replace("%price%", product.getSellPrice().toString()));

      eventBuilder.addEventBinding(CustomUIEventBindingType.Activating,
        cardSelector + " #BuyButton",
        EventData.of(BindingData.KEY_DATA, UtilsFile.getGson().toJson(DataButtonProduct.builder()
          .product(product)
          .economy(shop.getEconomy())
          .action(ProductAction.BUY)
          .build())));

      eventBuilder.addEventBinding(CustomUIEventBindingType.Activating,
        cardSelector + " #SellButton",
        EventData.of(BindingData.KEY_DATA, UtilsFile.getGson().toJson(DataButtonProduct.builder()
          .product(product)
          .economy(shop.getEconomy())
          .action(ProductAction.SELL)
          .build())));

      eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged,
        cardSelector + " #AmountField",
        EventData.of(BindingData.KEY_INPUT_FIELD, cardSelector + " #AmountField.Value"), false);

      cardsInRow++;
      if (cardsInRow >= 5) {
        cardsInRow = 0;
        rowIndex++;
      }
    }
  }

  @Override
  public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                              @Nonnull Store<EntityStore> store,
                              @Nonnull BindingData data) {

    super.handleDataEvent(ref, store, data);

    if (data.amount != null) {
      this.amount = data.amount;
      var commandBuilder = new UICommandBuilder();
      var eventBuilder = new UIEventBuilder();
      ZShop.getLog().atInfo().log(
        "Amount set to " + this.amount + " for playerRef: " + this.playerRef.getUuid()
      );
      sendUpdate(commandBuilder, eventBuilder, false);
    }

    if (data.searchQuery != null) {
      this.searchQuery = data.searchQuery.trim().toLowerCase();
      var commandBuilder = new UICommandBuilder();
      var eventBuilder = new UIEventBuilder();
      buildProductList(commandBuilder, eventBuilder, store.getComponent(ref, PlayerRef.getComponentType()));
      sendUpdate(commandBuilder, eventBuilder, false);
    }

    if (data.getData() != null) {
      var commandBuilder = new UICommandBuilder();
      var eventBuilder = new UIEventBuilder();
      DataButtonProduct buttonData = data.getData();
      if (amount <= 0) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null) {
          playerRef.sendMessage(
            FormatMessage.formatMessage(
              "Amount must be greater than zero to perform this action."
            )
          );
        }
        this.sendUpdate(commandBuilder, eventBuilder, false);
        return;
      }


      Player player = store.getComponent(ref, Player.getComponentType());
      if (player == null) {
        ZShop.getLog().atInfo().log(
          "Player component not found for playerRef: " + this.playerRef.getUuid()
        );
        return;
      }

      
      switch (buttonData.getAction()) {
        case BUY -> buy(buttonData, this.playerRef, player, amount);
        case SELL -> sell(buttonData, this.playerRef, player, amount);
      }


      this.amount = 0;
      sendUpdate(commandBuilder, eventBuilder, false);
    }
  }

  private void buy(DataButtonProduct data, PlayerRef playerRef, Player player, int amount) {
    Product product = data.getProduct();
    EconomySelector economy = data.getEconomy();
    UUID playerUuid = playerRef.getUuid();
    BigDecimal price = product.getBuyPrice().multiply(BigDecimal.valueOf(amount));

    if (!EconomyApi.hasBalance(playerUuid, economy, price)) {
      playerRef.sendMessage(FormatMessage.formatMessage(ZShop.get().getLang().getPurchaseFailFunds()
        .replace("%amount%", String.valueOf(amount))
        .replace("%product%", product.getProduct())
        .replace("%total_price%", price.toString())));
      return;
    }

    ItemStack stack = new ItemStack(product.getProduct(), amount);
    ItemStackTransaction transaction = player.getInventory().getCombinedHotbarFirst().addItemStack(stack);
    ItemStack remainder = transaction.getRemainder();

    if (remainder != null && !remainder.isEmpty()) {
      playerRef.sendMessage(FormatMessage.formatMessage(ZShop.get().getLang().getPurchaseFailSpace()
        .replace("%amount%", String.valueOf(amount))
        .replace("%product%", stack.getItem().getId())));
      return;
    }

    String reason = ZShop.get().getLang().getPurchaseReason()
      .replace("%amount%", String.valueOf(amount))
      .replace("%product%", stack.getItem().getId());

    if (EconomyApi.withdraw(playerUuid, economy, price, reason)) {
      playerRef.sendMessage(FormatMessage.formatMessage(ZShop.get().getLang().getPurchaseSuccess()
        .replace("%amount%", String.valueOf(amount))
        .replace("%product%", stack.getItem().getId())
        .replace("%total_price%", price.toString())));
    }
  }

  private void sell(DataButtonProduct data, PlayerRef playerRef, Player player, int amount) {
    Product product = data.getProduct();
    if (product.getSellPrice().compareTo(BigDecimal.ZERO) <= 0) {
      playerRef.sendMessage(FormatMessage.formatMessage(ZShop.get().getLang().getSellNotHaveSellPrice()));
      return;
    }
    EconomySelector economy = data.getEconomy();
    UUID playerUuid = playerRef.getUuid();

    ItemStack stack = new ItemStack(product.getProduct(), amount);
    ItemStackTransaction transaction = player.getInventory().getCombinedEverything().removeItemStack(stack);
    int removedAmount;

    if (transaction.getRemainder() == null || transaction.getRemainder().isEmpty()) removedAmount = amount;
    else removedAmount = amount - transaction.getRemainder().getQuantity();
    if (removedAmount <= 0) {
      playerRef.sendMessage(FormatMessage.formatMessage(ZShop.get().getLang().getSellFailProduct()
        .replace("%amount%", String.valueOf(amount))
        .replace("%product%", stack.getItem().getId())));
      return;
    }

    String reason = ZShop.get().getLang().getSellReason()
      .replace("%amount%", String.valueOf(removedAmount))
      .replace("%product%", stack.getItem().getId());

    BigDecimal price = product.getSellPrice().multiply(BigDecimal.valueOf(removedAmount));
    if (EconomyApi.deposit(playerUuid, economy, price, reason)) {
      playerRef.sendMessage(
        FormatMessage.formatMessage(
          ZShop.get().getLang().getSellSuccess()
            .replace("%amount%", String.valueOf(removedAmount))
            .replace("%product%", stack.getItem().getId())
            .replace("%total_price%", price.toString())
        )
      );
    } else {
      player.getInventory().getCombinedHotbarFirst().addItemStack(stack);
      playerRef.sendMessage(
        FormatMessage.formatMessage(
          ZShop.get().getLang().getSellFailRollback()
            .replace("%amount%", String.valueOf(removedAmount))
            .replace("%product%", product.getProduct())
        )
      );
    }
  }

  private static class SearchResult {
    public static final Comparator<SearchResult> COMPARATOR = Comparator.comparing(r -> r.match);
    private final String name;
    private final Product product;
    private MatchResult match;

    public SearchResult(String name, Product product, MatchResult match) {
      this.name = name;
      this.product = product;
      this.match = match;
    }
  }
}
