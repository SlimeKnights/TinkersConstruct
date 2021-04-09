package slimeknights.tconstruct.common.multiblock;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.library.utils.TagUtil;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;

// TODO: move back to Mantle after smeltery is updated
public class ServantTileEntity extends MantleTileEntity implements IServantLogic {
  private static final String TAG_MASTER_POS = "masterPos";
  private static final String TAG_MASTER_BLOCK = "masterBlock";

  @Getter
  @Nullable
  private BlockPos masterPos;
  @Nullable
  private Block masterBlock;
  public ServantTileEntity(BlockEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  /** Checks if this servant has a master */
  public boolean hasMaster() {
    return masterPos != null;
  }

  /**
   * Called to change the master
   * @param master  New master
   * @param block   New master block
   */
  protected void setMaster(@Nullable BlockPos master, @Nullable Block block) {
    masterPos = master;
    masterBlock = block;
    this.markDirtyFast();
  }

  /**
   * Checks that this servant has a valid master. Clears the master if invalid
   * @return  True if this servant has a valid master
   */
  protected boolean validateMaster() {
    if (masterPos == null) {
      return false;
    }

    // ensure the master block is correct
    assert world != null;
    if (world.getBlockState(masterPos).getBlock() == masterBlock) {
      return true;
    }
    // master invalid, so clear
    setMaster(null, null);
    return false;
  }

  @Override
  public boolean isValidMaster(IMasterLogic master) {
    // if we have a valid master, the passed master is only valid if its our current master
    if (validateMaster()) {
      return master.getTileEntity().getPos().equals(this.masterPos);
    }
    // otherwise, we are happy with any master
    return true;
  }

  @Override
  public void notifyMasterOfChange(BlockPos pos, BlockState state) {
    if (validateMaster()) {
      assert masterPos != null;
      TileEntityHelper.getTile(IMasterLogic.class, world, masterPos).ifPresent(te -> te.notifyChange(this, pos, state));
    }
  }

  @Override
  public void setPotentialMaster(IMasterLogic master) {
    BlockEntity masterTE = master.getTileEntity();
    BlockPos newMaster = masterTE.getPos();
    // if this is our current master, simply update the master block
    if (newMaster.equals(this.masterPos)) {
      masterBlock = masterTE.getCachedState().getBlock();
      this.markDirtyFast();
    // otherwise, only set if we don't have a master
    } else if (!validateMaster()) {
      setMaster(newMaster, masterTE.getCachedState().getBlock());
    }
  }

  @Override
  public void removeMaster(IMasterLogic master) {
    if (masterPos != null && masterPos.equals(master.getTileEntity().getPos())) {
      setMaster(null, null);
    }
  }


  /* NBT */

  /**
   * Reads the master from NBT
   * @param tags  NBT to read
   */
  protected void readMaster(CompoundTag tags) {
    BlockPos masterPos = TagUtil.readPos(tags, TAG_MASTER_POS);
    Block masterBlock = null;
    // if the master position is valid, get the master block
    if (masterPos != null && tags.contains(TAG_MASTER_BLOCK, 8)) {
      Identifier masterBlockName = Identifier.tryParse(tags.getString(TAG_MASTER_BLOCK));
      if (masterBlockName != null && Registry.BLOCK.containsId(masterBlockName) ) {
        masterBlock = Registry.BLOCK.get(masterBlockName);
      }
    }
    // if both valid, set
    if (masterBlock != null) {
      this.masterPos = masterPos;
      this.masterBlock = masterBlock;
    }
  }

  @Override
  public void fromTag(BlockState blockState, CompoundTag tags) {
    super.fromTag(blockState, tags);
    readMaster(tags);
  }

  /**
   * Writes the master position and master block to the given compound
   * @param tags  Tags
   */
  protected CompoundTag writeMaster(CompoundTag tags) {
    if (masterPos != null && masterBlock != null) {
      tags.put(TAG_MASTER_POS, TagUtil.writePos(masterPos));
      tags.putString(TAG_MASTER_BLOCK, Objects.requireNonNull(Registry.BLOCK.getId(masterBlock).toString()));
    }
    return tags;
  }

  @Override
  public CompoundTag toTag(CompoundTag tags) {
    tags = super.toTag(tags);
    writeMaster(tags);
    return tags;
  }
}
