package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.ItemRegistryAdapter;
import slimeknights.tconstruct.gadgets.item.EflnBallItem;
import slimeknights.tconstruct.gadgets.item.FancyItemFrameItem;
import slimeknights.tconstruct.gadgets.item.GlowBallItem;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.SlimeSlingItem;
import slimeknights.tconstruct.gadgets.item.SpaghettiItem;
import slimeknights.tconstruct.library.TinkerRegistry;

@ObjectHolder(TConstruct.modID)
public class GadgetItems {
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

  public static final SpaghettiItem hard_spaghetti = TinkerPulse.injected();
  public static final SpaghettiItem soggy_spaghetti = TinkerPulse.injected();
  public static final SpaghettiItem cold_spaghetti = TinkerPulse.injected();

  static void registerSpaghetti(final RegistryEvent.Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabWorld);
  }

  private GadgetItems() {}
}
