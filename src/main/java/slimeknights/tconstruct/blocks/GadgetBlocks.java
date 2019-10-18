package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.gadgets.block.StoneLadderBlock;
import slimeknights.tconstruct.gadgets.block.StoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WallStoneTorchBlock;
import slimeknights.tconstruct.gadgets.block.WoodenDropperRailBlock;
import slimeknights.tconstruct.gadgets.block.WoodenRailBlock;

@ObjectHolder(TConstruct.modID)
public class GadgetBlocks {

  public static final StoneLadderBlock stone_ladder = TinkerPulse.injected();
  public static final StoneTorchBlock stone_torch = TinkerPulse.injected();
  public static final WallStoneTorchBlock wall_stone_torch = TinkerPulse.injected();
  public static final PunjiBlock punji = TinkerPulse.injected();
  public static final WoodenRailBlock wooden_rail = TinkerPulse.injected();
  public static final WoodenDropperRailBlock wooden_dropper_rail = TinkerPulse.injected();

  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new StoneLadderBlock(), "stone_ladder");

    registry.register(new StoneTorchBlock(), "stone_torch");
    registry.register(new WallStoneTorchBlock(), "wall_stone_torch");

    registry.register(new PunjiBlock(), "punji");

    registry.register(new WoodenRailBlock(), "wooden_rail");
    registry.register(new WoodenDropperRailBlock(), "wooden_dropper_rail");
  }

  private GadgetBlocks() {}
}
