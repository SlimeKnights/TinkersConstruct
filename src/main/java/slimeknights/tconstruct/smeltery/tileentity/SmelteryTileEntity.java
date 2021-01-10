package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.common.multiblock.IMasterLogic;
import slimeknights.tconstruct.common.multiblock.IServantLogic;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.MultiblockSmeltery.StructureData;

import javax.annotation.Nullable;

public class SmelteryTileEntity extends MantleTileEntity implements ITickableTileEntity, IMasterLogic {
  private static final String TAG_STRUCTURE = "structure";

  /** Sub module to detect the multiblock for this structure */
  private final MultiblockSmeltery multiblock = new MultiblockSmeltery(this);

  /* Instance data, this data is not written to NBT */
  /** Timer to allow delaying actions based on number of ticks alive */
  private int tick = 0;

  /** If true, structure will check for an update next tick */
  private boolean updateQueued = false;

  /* Saved data, written to NBT */
  /** Current structure contents */
  @Nullable
  private MultiblockSmeltery.StructureData structure;

  public SmelteryTileEntity() {
    super(TinkerSmeltery.smeltery.get());
  }

  @Override
  public void tick() {
    if (world == null || world.isRemote) {
      return;
    }

    // run structure update if requested
    if (updateQueued) {
      checkStructure();
      updateQueued = false;
    }

    // if we have a structure, run smeltery logic
    if (structure != null) {
      // check the next inside position to see if its a valid inner block
      if (!multiblock.isInnerBlock(world, structure.getNextInsideCheck())) {
        queueUpdate();
      }

    } else if (tick == 0) {
      queueUpdate();
    }

    // update tick timer
    tick = (tick + 1) % 20;
  }

  /**
   * Marks the smeltery for a structure check
   */
  public void queueUpdate() {
    updateQueued = true;
  }

  /**
   * Attempts to locate a valid smeltery structure
   */
  protected void checkStructure() {
    assert world != null;

    // TODO: validate the block is correct?
    boolean wasActive = getBlockState().get(ControllerBlock.ACTIVE);
    StructureData oldStructure = structure;
    StructureData newStructure = multiblock.detectMultiblock(world, pos, getBlockState().get(BlockStateProperties.HORIZONTAL_FACING));

    // update block state
    boolean active = newStructure != null;
    if (active != wasActive) {
      world.setBlockState(pos, getBlockState().with(ControllerBlock.ACTIVE, active));
    }

    // structure info updates
    if (active) {
      newStructure.assignMaster(this, oldStructure);
      // TODO: inventory size
      // TODO: tank size
    } else {
      if (oldStructure != null) {
        oldStructure.clearMaster(this);
      }
    }
    structure = newStructure;
  }

  /**
   * Called when the controller is broken to invalidate the master in all servants
   */
  public void invalidateStructure() {
    if (structure != null) {
      structure.clearMaster(this);
      structure = null;
    }
  }

  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos, BlockState state) {
    // structure invalid? can ignore this, will automatically check later
    if (structure == null) {
      return;
    }

    assert world != null;
    if (multiblock.shouldUpdate(world, structure, pos, state)) {
      queueUpdate();
    }
  }


  /* NBT */

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    if (nbt.contains(TAG_STRUCTURE, NBT.TAG_COMPOUND)) {
      structure = multiblock.readFromNBT(nbt.getCompound(TAG_STRUCTURE));
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    compound = super.write(compound);
    if (structure != null) {
      compound.put(TAG_STRUCTURE, structure.writeToNBT());
    }
    return compound;
  }
}
