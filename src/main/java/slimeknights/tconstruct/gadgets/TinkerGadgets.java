package slimeknights.tconstruct.gadgets;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.block.StoneLadderBlock;
import slimeknights.tconstruct.gadgets.block.StoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WallStoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WoodenDropperRailBlock;
import slimeknights.tconstruct.gadgets.block.WoodenRailBlock;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;

@Pulse(id = TinkerPulseIds.TINKER_GADGETS_PULSE_ID, description = "All the fun toys")
@ObjectHolder(TConstruct.modID)
public class TinkerGadgets extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_GADGETS_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> GadgetClientProxy::new, () -> ServerProxy::new);

  public static final StoneLadderBlock stone_ladder = null;

  public static final StoneTorchBlock stone_torch = null;
  public static final WallStoneTorchBlock wall_stone_torch = null;

  public static final PunjiBlock punji = null;

  public static final WoodenRailBlock wooden_rail = null;
  public static final WoodenDropperRailBlock wooden_dropper_rail = null;

  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    register(registry, new StoneLadderBlock(), "stone_ladder");

    register(registry, new StoneTorchBlock(), "stone_torch");
    register(registry, new WallStoneTorchBlock(), "wall_stone_torch");

    register(registry, new PunjiBlock(), "punji");

    register(registry, new WoodenRailBlock(), "wooden_rail");
    register(registry, new WoodenDropperRailBlock(), "wooden_dropper_rail");
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    registerBlockItem(registry, stone_ladder, TinkerRegistry.tabGadgets);

    registerBlockItem(registry, new WallOrFloorItem(stone_torch, wall_stone_torch, (new Item.Properties()).group(TinkerRegistry.tabGadgets)));

    registerBlockItem(registry, punji, TinkerRegistry.tabGadgets);

    registerBlockItem(registry, wooden_rail, TinkerRegistry.tabGadgets);
    registerBlockItem(registry, wooden_dropper_rail, TinkerRegistry.tabGadgets);
  }

}
