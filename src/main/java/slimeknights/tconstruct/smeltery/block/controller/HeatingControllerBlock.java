package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockResult;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;

/**
 * Multiblock that displays the error from the tile entity on right click
 */
public abstract class HeatingControllerBlock extends ControllerBlock {
  protected HeatingControllerBlock(Properties builder) {
    super(builder);
  }

  @Override
  protected boolean openGui(Player player, Level world, BlockPos pos) {
    super.openGui(player, world, pos);
    // only need to update if holding the proper items
    if (!world.isClientSide) {
      BlockEntityHelper.get(HeatingStructureBlockEntity.class, world, pos).ifPresent(te -> {
        MultiblockResult result = te.getStructureResult();
        if (!result.isSuccess() && te.showDebugBlockBorder(player)) {
          TinkerNetwork.getInstance().sendTo(new StructureErrorPositionPacket(pos, result.getPos()), player);
        }
      });
    }
    return true;
  }

  @Override
  protected boolean displayStatus(Player player, Level world, BlockPos pos, BlockState state) {
    if (!world.isClientSide) {
      BlockEntityHelper.get(HeatingStructureBlockEntity.class, world, pos).ifPresent(te -> {
        MultiblockResult result = te.getStructureResult();
        if (!result.isSuccess()) {
          player.displayClientMessage(result.getMessage(), true);
          TinkerNetwork.getInstance().sendTo(new StructureErrorPositionPacket(pos, result.getPos()), player);
        }
      });
    }
    return true;
  }
}
