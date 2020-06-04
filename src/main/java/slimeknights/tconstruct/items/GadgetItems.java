package slimeknights.tconstruct.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.item.EflnBallItem;
import slimeknights.tconstruct.gadgets.item.FancyItemFrameItem;
import slimeknights.tconstruct.gadgets.item.GlowBallItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.SlimeSlingItem;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.ItemDeferredRegister;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.shared.block.SlimeBlock;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GadgetItems {

  private static final Item.Properties gadgetProps = new Item.Properties().group(TinkerRegistry.tabGadgets);
  private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ITEMS.register(modEventBus);
  }

  public static final ItemObject<Item> stone_stick = ITEMS.register("stone_stick", gadgetProps);

  public static final EnumObject<SlimeBlock.SlimeType,SlimeSlingItem> slime_sling = ITEMS.registerEnum(SlimeBlock.SlimeType.values(), "slime_sling", (type) -> new SlimeSlingItem());
  public static final EnumObject<SlimeBlock.SlimeType,SlimeBootsItem> slime_boots = ITEMS.registerEnum(SlimeBlock.SlimeType.values(), "slime_boots", SlimeBootsItem::new);

  public static final ItemObject<PiggyBackPackItem> piggy_backpack = ITEMS.register("piggy_backpack", PiggyBackPackItem::new);

  public static final EnumObject<FrameType,FancyItemFrameItem> item_frame = ITEMS.registerEnum(FrameType.values(), "jewel_item_frame", (type) -> new FancyItemFrameItem(((world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, type.getId()))));

  public static final ItemObject<GlowBallItem> glow_ball = ITEMS.register("glow_ball", GlowBallItem::new);
  public static final ItemObject<EflnBallItem> efln_ball = ITEMS.register("efln_ball", EflnBallItem::new);
}
