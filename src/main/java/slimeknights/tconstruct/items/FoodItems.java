package slimeknights.tconstruct.items;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.gadgets.item.SpaghettiItem;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerFood;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FoodItems {

  /* Drool stimulant */
  public static final EdibleItem bacon = injected();

  /* Jerkies */
  public static final EdibleItem monster_jerky = injected();
  public static final EdibleItem beef_jerky = injected();
  public static final EdibleItem chicken_jerky = injected();
  public static final EdibleItem pork_jerky = injected();
  public static final EdibleItem mutton_jerky = injected();
  public static final EdibleItem rabbit_jerky = injected();
  public static final EdibleItem fish_jerky = injected();
  public static final EdibleItem salmon_jerky = injected();
  public static final EdibleItem clownfish_jerky = injected();
  public static final EdibleItem pufferfish_jerky = injected();

  /* Slime Balls */
  public static final EdibleItem blue_slime_ball = injected();
  public static final EdibleItem purple_slime_ball = injected();
  public static final EdibleItem blood_slime_ball = injected();
  public static final EdibleItem magma_slime_ball = injected();
  public static final EdibleItem pink_slime_ball = injected();

  /* Slime Drops */
  public static final EdibleItem green_slime_drop = injected();
  public static final EdibleItem blue_slime_drop = injected();
  public static final EdibleItem purple_slime_drop = injected();
  public static final EdibleItem blood_slime_drop = injected();
  public static final EdibleItem magma_slime_drop = injected();

  /* Spicy Memes */
  public static final SpaghettiItem hard_spaghetti = TinkerPulse.injected();
  public static final SpaghettiItem soggy_spaghetti = TinkerPulse.injected();
  public static final SpaghettiItem cold_spaghetti = TinkerPulse.injected();


  @SubscribeEvent
  static void registerFoodItems(final RegistryEvent.Register<Item> event) {
    BaseRegistryAdapter<Item> registry = new BaseRegistryAdapter<>(event.getRegistry());

    // slimeballs are edible, believe it or not
    registry.register(createEdible(TinkerFood.BLUE_SLIME_BALL), "blue_slime_ball");
    registry.register(createEdible(TinkerFood.PURPLE_SLIME_BALL), "purple_slime_ball");
    registry.register(createEdible(TinkerFood.BLOOD_SLIME_BALL), "blood_slime_ball");
    registry.register(createEdible(TinkerFood.MAGMA_SLIME_BALL), "magma_slime_ball");
    registry.register(createEdible(TinkerFood.PINK_SLIME_BALL), "pink_slime_ball");

    registry.register(createEdible(TinkerFood.BACON), "bacon");

    registerJerkies(registry);

    registry.register(new SpaghettiItem(), "hard_spaghetti");
    registry.register(new SpaghettiItem(), "soggy_spaghetti");
    registry.register(new SpaghettiItem(), "cold_spaghetti");
  }

  private static void registerJerkies(BaseRegistryAdapter<Item> registry) {
    registry.register(createEdible(TinkerFood.MONSTER_JERKY), "monster_jerky");
    registry.register(createEdible(TinkerFood.BEEF_JERKY), "beef_jerky");
    registry.register(createEdible(TinkerFood.CHICKEN_JERKY), "chicken_jerky");
    registry.register(createEdible(TinkerFood.PORK_JERKY), "pork_jerky");
    registry.register(createEdible(TinkerFood.MUTTON_JERKY), "mutton_jerky");
    registry.register(createEdible(TinkerFood.RABBIT_JERKY), "rabbit_jerky");
    registry.register(createEdible(TinkerFood.FISH_JERKY), "fish_jerky");
    registry.register(createEdible(TinkerFood.SALMON_JERKY), "salmon_jerky");
    registry.register(createEdible(TinkerFood.CLOWNFISH_JERKY), "clownfish_jerky");
    registry.register(createEdible(TinkerFood.PUFFERFISH_JERKY), "pufferfish_jerky");

    registry.register(createEdible(TinkerFood.GREEN_SLIME_DROP), "green_slime_drop");
    registry.register(createEdible(TinkerFood.BLUE_SLIME_DROP), "blue_slime_drop");
    registry.register(createEdible(TinkerFood.PURPLE_SLIME_DROP), "purple_slime_drop");
    registry.register(createEdible(TinkerFood.BLOOD_SLIME_DROP), "blood_slime_drop");
    registry.register(createEdible(TinkerFood.MAGMA_SLIME_DROP), "magma_slime_drop");
  }

  private static Item createEdible(Food food) {
    return new EdibleItem(food, TinkerRegistry.tabGeneral);
  }

  private FoodItems() {}
}
