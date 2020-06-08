package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

import javax.annotation.Nullable;

public class SearedBlock extends Block {

  public SearedBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new SmelteryComponentTileEntity();
  }

  @Override
  @Deprecated
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    TileEntity tileEntity = worldIn.getTileEntity(pos);

    if (tileEntity instanceof SmelteryComponentTileEntity) {
      ((SmelteryComponentTileEntity) tileEntity).notifyMasterOfChange();
    }

    super.onReplaced(state, worldIn, pos, newState, isMoving);
    worldIn.removeTileEntity(pos);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    // look for a smeltery (controller directly or through another smeltery block) and notify it that we exist
    for (Direction direction : Direction.values()) {
      TileEntity tileEntity = worldIn.getTileEntity(pos.offset(direction));

      if (tileEntity instanceof IMasterLogic) {
        TileEntity servant = worldIn.getTileEntity(pos);

        if (servant instanceof IServantLogic) {
          ((IMasterLogic) tileEntity).notifyChange((IServantLogic) servant, pos);
          break;
        }
      } else if (tileEntity instanceof SmelteryComponentTileEntity) {
        SmelteryComponentTileEntity componentTileEntity = (SmelteryComponentTileEntity) tileEntity;

        if (componentTileEntity.hasValidMaster()) {
          componentTileEntity.notifyMasterOfChange();
          break;
        }
      }
    }
  }

  @Override
  @Deprecated
  public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
    super.eventReceived(state, worldIn, pos, id, param);

    TileEntity tileentity = worldIn.getTileEntity(pos);

    return tileentity != null && tileentity.receiveClientEvent(id, param);
  }
}
