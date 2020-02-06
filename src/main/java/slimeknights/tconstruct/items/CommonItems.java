package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonItems {

  public static final TinkerBookItem book = injected();

  /* Bricks */
  public static final Item seared_brick = injected();
  public static final Item mud_brick = injected();
  public static final Item dried_brick = injected();

  /* Slime Balls */
  public static final EdibleItem blue_slime_ball = injected();
  public static final EdibleItem purple_slime_ball = injected();
  public static final EdibleItem blood_slime_ball = injected();
  public static final EdibleItem magma_slime_ball = injected();
  public static final EdibleItem pink_slime_ball = injected();

  /* Metals */
  public static final Item cobalt_nugget = injected();
  public static final Item cobalt_ingot = injected();
  public static final Item ardite_nugget = injected();
  public static final Item ardite_ingot = injected();
  public static final Item manyullyn_nugget = injected();
  public static final Item manyullyn_ingot = injected();
  public static final Item pigiron_nugget = injected();
  public static final Item pigiron_ingot = injected();
  public static final Item alubrass_nugget = injected();
  public static final Item alubrass_ingot = injected();
  public static final Item knightslime_nugget = injected();
  public static final Item knightslime_ingot = injected();

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
    registry.register(new Item(new Item.Properties().group(TinkerRegistry.tabGeneral)), name + "_brick");
  }

  private static void registerIngotNugget(BaseRegistryAdapter<Item> registry, String name) {
    registry.register(new Item(new Item.Properties().group(TinkerRegistry.tabGeneral)), name + "_nugget");
    registry.register(new Item(new Item.Properties().group(TinkerRegistry.tabGeneral)), name + "_ingot");
  }

  private CommonItems() {}
}
