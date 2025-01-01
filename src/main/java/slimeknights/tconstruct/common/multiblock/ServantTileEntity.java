package slimeknights.tconstruct.common.multiblock;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nullable;

public class ServantTileEntity extends MantleBlockEntity implements IServantLogic {
  private static final String TAG_MASTER_POS = "masterOffset";
  private static final String TAG_MASTER_BLOCK = "masterBlock";

  @Getter
  @Nullable
  private BlockPos masterPos;
  @Nullable
  private Block masterBlock;

  public ServantTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
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
    this.setChangedFast();
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
    assert level != null;
    if (level.getBlockState(masterPos).getBlock() == masterBlock) {
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
      return master.getMasterPos().equals(this.masterPos);
    }
    // otherwise, we are happy with any master
    return true;
  }

  @Override
  public void notifyMasterOfChange(BlockPos pos, BlockState state) {
    if (validateMaster()) {
      assert masterPos != null;
      BlockEntityHelper.get(IMasterLogic.class, level, masterPos).ifPresent(te -> te.notifyChange(pos, state));
    }
  }

  @Override
  public void setPotentialMaster(IMasterLogic master) {
    BlockPos newMaster = master.getMasterPos();
    // if this is our current master, simply update the master block
    if (newMaster.equals(this.masterPos)) {
      masterBlock = master.getMasterBlock().getBlock();
      this.setChangedFast();
    // otherwise, only set if we don't have a master
    } else if (!validateMaster()) {
      setMaster(newMaster, master.getMasterBlock().getBlock());
    }
  }

  @Override
  public void removeMaster(IMasterLogic master) {
    if (masterPos != null && masterPos.equals(master.getMasterPos())) {
      setMaster(null, null);
    }
  }


  /* NBT */

  /**
   * Reads the master from NBT
   * @param tags  NBT to read
   */
  protected void readMaster(CompoundTag tags) {
    BlockPos masterPos = TagUtil.readOptionalPos(tags, TAG_MASTER_POS, this.worldPosition);
    Block masterBlock = null;
    // if the master position is valid, get the master block
    if (masterPos != null && tags.contains(TAG_MASTER_BLOCK, Tag.TAG_STRING)) {
      ResourceLocation masterBlockName = ResourceLocation.tryParse(tags.getString(TAG_MASTER_BLOCK));
      if (masterBlockName != null && ForgeRegistries.BLOCKS.containsKey(masterBlockName)) {
        masterBlock = ForgeRegistries.BLOCKS.getValue(masterBlockName);
      }
    }
    // if both valid, set
    if (masterBlock != null) {
      this.masterPos = masterPos;
      this.masterBlock = masterBlock;
    }
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    readMaster(tags);
  }

  /**
   * Writes the master position and master block to the given compound
   * @param tags  Tags
   */
  protected CompoundTag writeMaster(CompoundTag tags) {
    if (masterPos != null && masterBlock != null) {
      tags.put(TAG_MASTER_POS, NbtUtils.writeBlockPos(masterPos.subtract(this.worldPosition)));
      tags.putString(TAG_MASTER_BLOCK, BuiltInRegistries.BLOCK.getKey(masterBlock).toString());
    }
    return tags;
  }

  @Override
  public void saveAdditional(CompoundTag tags) {
    super.saveAdditional(tags);
    writeMaster(tags);
  }
}
