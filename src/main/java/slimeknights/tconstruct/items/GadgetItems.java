package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.item.EflnBallItem;
import slimeknights.tconstruct.gadgets.item.FancyItemFrameItem;
import slimeknights.tconstruct.gadgets.item.GlowBallItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.SlimeSlingItem;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.SlimeBlock;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GadgetItems {

  public static final SlimeSlingItem slime_sling_blue = TinkerPulse.injected();
  public static final SlimeSlingItem slime_sling_purple = TinkerPulse.injected();
  public static final SlimeSlingItem slime_sling_magma = TinkerPulse.injected();
  public static final SlimeSlingItem slime_sling_green = TinkerPulse.injected();
  public static final SlimeSlingItem slime_sling_blood = TinkerPulse.injected();

  public static final SlimeBootsItem slime_boots_blue = TinkerPulse.injected();
  public static final SlimeBootsItem slime_boots_purple = TinkerPulse.injected();
  public static final SlimeBootsItem slime_boots_magma = TinkerPulse.injected();
  public static final SlimeBootsItem slime_boots_green = TinkerPulse.injected();
  public static final SlimeBootsItem slime_boots_blood = TinkerPulse.injected();

  public static final PiggyBackPackItem piggy_backpack = TinkerPulse.injected();

  public static final FancyItemFrameItem jewel_item_frame = TinkerPulse.injected();
  public static final FancyItemFrameItem aluminum_brass_item_frame = TinkerPulse.injected();
  public static final FancyItemFrameItem cobalt_item_frame = TinkerPulse.injected();
  public static final FancyItemFrameItem ardite_item_frame = TinkerPulse.injected();
  public static final FancyItemFrameItem manyullyn_item_frame = TinkerPulse.injected();
  public static final FancyItemFrameItem gold_item_frame = TinkerPulse.injected();
  public static final FancyItemFrameItem clear_item_frame = TinkerPulse.injected();

  public static final GlowBallItem glow_ball = TinkerPulse.injected();
  public static final EflnBallItem efln_ball = TinkerPulse.injected();

  @SubscribeEvent
  static void registerItems(final RegistryEvent.Register<Item> event) {
    BaseRegistryAdapter<Item> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new Item((new Item.Properties()).group(TinkerRegistry.tabGadgets)), "stone_stick");

    for (SlimeBlock.SlimeType type : SlimeBlock.SlimeType.VISIBLE_COLORS) {
      registry.register(new SlimeSlingItem(), "slime_sling_" + type.getName());
      registry.register(new SlimeBootsItem(type), "slime_boots_" + type.getName());
    }

    registry.register(new PiggyBackPackItem(), "piggy_backpack");

    for (FrameType frameType : FrameType.values()) {
      registry.register(new FancyItemFrameItem((world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, frameType.getId())), frameType.getName() + "_item_frame");
    }

    registry.register(new GlowBallItem(), "glow_ball");
    registry.register(new EflnBallItem(), "efln_ball");
  }

  private GadgetItems() {}
}
