package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;
import slimeknights.tconstruct.smeltery.tileentity.controller.HeatingStructureTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockResult;

/**
 * Multiblock that displays the error from the tile entity on right click
 */
public abstract class HeatingControllerBlock extends ControllerBlock {
  protected HeatingControllerBlock(Properties builder) {
    super(builder);
  }

  @Override
  protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    super.openGui(player, world, pos);
    // only need to update if holding the proper items
    if (!world.isRemote) {
      TileEntityHelper.getTile(HeatingStructureTileEntity.class, world, pos).ifPresent(te -> {
        MultiblockResult result = te.getStructureResult();
        if (!result.isSuccess() && te.showDebugBlockBorder(player)) {
          TinkerNetwork.getInstance().sendTo(new StructureErrorPositionPacket(pos, result.getPos()), player);
        }
      });
    }
    return true;
  }

  @Override
  protected boolean displayStatus(PlayerEntity player, World world, BlockPos pos, BlockState state) {
    if (!world.isRemote) {
      TileEntityHelper.getTile(HeatingStructureTileEntity.class, world, pos).ifPresent(te -> {
        MultiblockResult result = te.getStructureResult();
        if (!result.isSuccess()) {
          player.sendStatusMessage(result.getMessage(), true);
          TinkerNetwork.getInstance().sendTo(new StructureErrorPositionPacket(pos, result.getPos()), player);
        }
      });
    }
    return true;
  }
}
