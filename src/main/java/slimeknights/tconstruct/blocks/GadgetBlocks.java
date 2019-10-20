package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.BlockItemRegistryAdapter;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.block.StoneLadderBlock;
import slimeknights.tconstruct.gadgets.block.StoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WallStoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WoodenDropperRailBlock;
import slimeknights.tconstruct.gadgets.block.WoodenRailBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

@ObjectHolder(TConstruct.modID)
public final class GadgetBlocks {

  public static final StoneLadderBlock stone_ladder = TinkerPulse.injected();
  public static final StoneTorchBlock stone_torch = TinkerPulse.injected();
  public static final WallStoneTorchBlock wall_stone_torch = TinkerPulse.injected();
  public static final PunjiBlock punji = TinkerPulse.injected();
  public static final WoodenRailBlock wooden_rail = TinkerPulse.injected();
  public static final WoodenDropperRailBlock wooden_dropper_rail = TinkerPulse.injected();

  @SubscribeEvent
  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new StoneLadderBlock(), "stone_ladder");

    registry.register(new StoneTorchBlock(), "stone_torch");
    registry.register(new WallStoneTorchBlock(), "wall_stone_torch");

    registry.register(new PunjiBlock(), "punji");

    registry.register(new WoodenRailBlock(), "wooden_rail");
    registry.register(new WoodenDropperRailBlock(), "wooden_dropper_rail");
  }

  @SubscribeEvent
  static void registerBlockItems(final RegistryEvent.Register<Item> event) {
    BlockItemRegistryAdapter registry = new BlockItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabGadgets);

    registry.registerBlockItem(GadgetBlocks.stone_ladder);

    registry.registerBlockItem(new WallOrFloorItem(GadgetBlocks.stone_torch, GadgetBlocks.wall_stone_torch, (new Item.Properties()).group(TinkerRegistry.tabGadgets)));

    registry.registerBlockItem(GadgetBlocks.punji);

    registry.registerBlockItem(GadgetBlocks.wooden_rail);
    registry.registerBlockItem(GadgetBlocks.wooden_dropper_rail);
  }

  private GadgetBlocks() {}
}
