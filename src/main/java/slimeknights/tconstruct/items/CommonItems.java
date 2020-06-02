package slimeknights.tconstruct.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.ItemDeferredRegister;
import slimeknights.tconstruct.library.registration.object.ItemObject;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonItems {

  private static final Item.Properties commonProps = new Item.Properties().group(TinkerRegistry.tabGeneral);
  private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ITEMS.register(modEventBus);
  }

  public static final ItemObject<TinkerBookItem> book = ITEMS.register("book", TinkerBookItem::new);

  /* Bricks */
  public static final ItemObject<Item> seared_brick = ITEMS.register("seared_brick", commonProps);
  public static final ItemObject<Item> mud_brick = ITEMS.register("mud_brick", commonProps);
  public static final ItemObject<Item> dried_brick = ITEMS.register("dried_brick", commonProps);

  /* Metals */
  public static final ItemObject<Item> cobalt_nugget = ITEMS.register("cobalt_nugget", commonProps);
  public static final ItemObject<Item> cobalt_ingot = ITEMS.register("cobalt_ingot", commonProps);
  public static final ItemObject<Item> ardite_nugget = ITEMS.register("ardite_nugget", commonProps);
  public static final ItemObject<Item> ardite_ingot = ITEMS.register("ardite_ingot", commonProps);
  public static final ItemObject<Item> manyullyn_nugget = ITEMS.register("manyullyn_nugget", commonProps);
  public static final ItemObject<Item> manyullyn_ingot = ITEMS.register("manyullyn_ingot", commonProps);
  public static final ItemObject<Item> pigiron_nugget = ITEMS.register("pigiron_nugget", commonProps);
  public static final ItemObject<Item> pigiron_ingot = ITEMS.register("pigiron_ingot", commonProps);
  public static final ItemObject<Item> alubrass_nugget = ITEMS.register("alubrass_nugget", commonProps);
  public static final ItemObject<Item> alubrass_ingot = ITEMS.register("alubrass_ingot", commonProps);
  public static final ItemObject<Item> knightslime_nugget = ITEMS.register("knightslime_nugget", commonProps);
  public static final ItemObject<Item> knightslime_ingot = ITEMS.register("knightslime_ingot", commonProps);
}
