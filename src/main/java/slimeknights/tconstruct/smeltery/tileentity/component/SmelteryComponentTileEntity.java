package slimeknights.tconstruct.smeltery.tileentity.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.common.multiblock.ServantTileEntity;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

import javax.annotation.Nullable;

/** Mostly extended to make type validaton easier, and the servant base class is not registered */
public class SmelteryComponentTileEntity extends ServantTileEntity {

  public SmelteryComponentTileEntity() {
    this(TinkerSmeltery.smelteryComponent.get());
  }

  protected SmelteryComponentTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  @Override
  protected void setMaster(@Nullable BlockPos master, @Nullable Block block) {
    // update the master
    super.setMaster(master, block);

    // update the active state
    if (world != null) {
      BlockState currentState = getBlockState();
      boolean hasMaster = getMasterPos() != null;
      if (currentState.hasProperty(SearedBlock.IN_STRUCTURE) && currentState.get(SearedBlock.IN_STRUCTURE) != hasMaster) {
        world.setBlockState(pos, getBlockState().with(SearedBlock.IN_STRUCTURE, hasMaster), BlockFlags.BLOCK_UPDATE);
      }
    }
  }

  /**
   * Block method to update neighbors of a smeltery component when a new one is placed
   * @param world  World instance
   * @param pos    Location of new smeltery component
   */
  public static void updateNeighbors(World world, BlockPos pos, BlockState state) {
    for (Direction direction : Direction.values()) {
      // if the neighbor is a master, notify it we exist
      TileEntity tileEntity = world.getTileEntity(pos.offset(direction));
      if (tileEntity instanceof IMasterLogic) {
        TileEntity servant = world.getTileEntity(pos);
        if (servant instanceof IServantLogic) {
          ((IMasterLogic) tileEntity).notifyChange((IServantLogic) servant, pos, state);
          break;
        }
        // if the neighbor is a servant, notify its master we exist
      } else if (tileEntity instanceof SmelteryComponentTileEntity) {
        SmelteryComponentTileEntity componentTileEntity = (SmelteryComponentTileEntity) tileEntity;
        if (componentTileEntity.hasMaster()) {
          componentTileEntity.notifyMasterOfChange(pos, state);
          break;
        }
      }
    }
  }
}
