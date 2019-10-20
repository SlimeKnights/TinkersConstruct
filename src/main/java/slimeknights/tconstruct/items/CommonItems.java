package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.item.GeneratedItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
public final class CommonItems {

  public static final TinkerBookItem book = injected();

  /* Bricks */
  public static final GeneratedItem seared_brick = injected();
  public static final GeneratedItem mud_brick = injected();
  public static final GeneratedItem dried_brick = injected();

  /* Slime Balls */
  public static final EdibleItem blue_slime_ball = injected();
  public static final EdibleItem purple_slime_ball = injected();
  public static final EdibleItem blood_slime_ball = injected();
  public static final EdibleItem magma_slime_ball = injected();
  public static final EdibleItem pink_slime_ball = injected();

  /* Metals */
  public static final GeneratedItem cobalt_nugget = injected();
  public static final GeneratedItem cobalt_ingot = injected();
  public static final GeneratedItem ardite_nugget = injected();
  public static final GeneratedItem ardite_ingot = injected();
  public static final GeneratedItem manyullyn_nugget = injected();
  public static final GeneratedItem manyullyn_ingot = injected();
  public static final GeneratedItem pigiron_nugget = injected();
  public static final GeneratedItem pigiron_ingot = injected();
  public static final GeneratedItem alubrass_nugget = injected();
  public static final GeneratedItem alubrass_ingot = injected();
  public static final GeneratedItem knightslime_nugget = injected();
  public static final GeneratedItem knightslime_ingot = injected();

  @SubscribeEvent
  static void registerItems(final RegistryEvent.Register<Item> event) {
    BaseRegistryAdapter<Item> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new TinkerBookItem(), "book");

    // extra bricks
    registerBrick(registry, "seared");
    registerBrick(registry, "mud");
    registerBrick(registry, "dried");

    // Ingots and nuggets
    registerIngotNugget(registry, "cobalt");
    registerIngotNugget(registry, "ardite");
    registerIngotNugget(registry, "manyullyn");
    registerIngotNugget(registry, "pigiron");
    registerIngotNugget(registry, "alubrass");
    registerIngotNugget(registry, "knightslime");
  }

  private static void registerBrick(BaseRegistryAdapter<Item> registry, String name) {
    registry.register(new GeneratedItem(TinkerRegistry.tabGeneral), name + "_brick");
  }

  private static void registerIngotNugget(BaseRegistryAdapter<Item> registry, String name) {
    registry.register(new GeneratedItem(TinkerRegistry.tabGeneral), name + "_nugget");
    registry.register(new GeneratedItem(TinkerRegistry.tabGeneral), name + "_ingot");
  }

  private CommonItems() {}
}
