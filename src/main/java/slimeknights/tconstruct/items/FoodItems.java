package slimeknights.tconstruct.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.item.SpaghettiItem;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.ItemDeferredRegister;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.shared.block.SlimeBlock;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FoodItems {

  private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ITEMS.register(modEventBus);
  }
  /* Drool stimulant */
  public static final ItemObject<EdibleItem> bacon = ITEMS.register("bacon", () -> new EdibleItem(TinkerFood.BACON, TinkerRegistry.tabGeneral));

  /* Jerkies */
  public static final ItemObject<EdibleItem> monster_jerky = ITEMS.register("monster_jerky", () -> new EdibleItem(TinkerFood.MONSTER_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> beef_jerky = ITEMS.register("beef_jerky", () -> new EdibleItem(TinkerFood.BEEF_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> chicken_jerky = ITEMS.register("chicken_jerky", () -> new EdibleItem(TinkerFood.CHICKEN_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> pork_jerky = ITEMS.register("pork_jerky", () -> new EdibleItem(TinkerFood.PORK_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> mutton_jerky = ITEMS.register("mutton_jerky", () -> new EdibleItem(TinkerFood.MUTTON_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> rabbit_jerky = ITEMS.register("rabbit_jerky", () -> new EdibleItem(TinkerFood.RABBIT_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> fish_jerky = ITEMS.register("fish_jerky", () -> new EdibleItem(TinkerFood.FISH_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> salmon_jerky = ITEMS.register("salmon_jerky", () -> new EdibleItem(TinkerFood.SALMON_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> clownfish_jerky = ITEMS.register("clownfish_jerky", () -> new EdibleItem(TinkerFood.CLOWNFISH_JERKY, TinkerRegistry.tabGeneral));
  public static final ItemObject<EdibleItem> pufferfish_jerky = ITEMS.register("pufferfish_jerky", () -> new EdibleItem(TinkerFood.PUFFERFISH_JERKY, TinkerRegistry.tabGeneral));

  /* Slime Balls are edible, believe it or not */
  public static final EnumObject<SlimeBlock.SlimeType, Item> slime_ball;
  static {
    EnumObject<SlimeBlock.SlimeType,EdibleItem> tinkerSlimeballs = ITEMS.registerEnum(SlimeBlock.SlimeType.TINKER, "slime_ball", (type) -> new EdibleItem(type.getSlimeFood(type), TinkerRegistry.tabGeneral));
    Map<SlimeBlock.SlimeType, Supplier<? extends Item>> map = new EnumMap(SlimeBlock.SlimeType.class);
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.TINKER) {
      map.put(slime, tinkerSlimeballs.getSupplier(slime));
    }
    map.put(SlimeBlock.SlimeType.GREEN, Items.SLIME_BALL.delegate);
    slime_ball = new EnumObject<>(map);
  }

  /* Slime Drops */
  public static final EnumObject<SlimeBlock.SlimeType,EdibleItem> slime_drop = ITEMS.registerEnum(SlimeBlock.SlimeType.VISIBLE_COLORS,"slime_drop", (type) -> new EdibleItem(type.getSlimeDropFood(type), TinkerRegistry.tabGeneral));

  /* Spicy Memes */
  public static final ItemObject<SpaghettiItem> hard_spaghetti = ITEMS.register("hard_spaghetti", SpaghettiItem::new);
  public static final ItemObject<SpaghettiItem> soggy_spaghetti = ITEMS.register("soggy_spaghetti", SpaghettiItem::new);
  public static final ItemObject<SpaghettiItem> cold_spaghetti = ITEMS.register("cold_spaghetti", SpaghettiItem::new);
}
