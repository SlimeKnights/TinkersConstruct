package slimeknights.tconstruct.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.mantle.tileentity.MantleTileEntity;

import java.util.Objects;

// TODO: move back to Mantle after smeltery is updated
public class MultiServantLogic extends MantleTileEntity implements IServantLogic {

  boolean hasMaster;
  BlockPos master;
  Block masterBlock;
  BlockState state;

  public MultiServantLogic(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  public boolean canUpdate() {
    return false;
  }

  public boolean getHasMaster() {
    return this.hasMaster;
  }

  public boolean hasValidMaster() {
    if (!this.hasMaster) {
      return false;
    }

    assert this.world != null;
    if (this.world.getBlockState(this.master).getBlock() == this.masterBlock && this.world.getBlockState(this.master) == this.state) {
      return true;
    }
    else {
      this.hasMaster = false;
      this.master = null;
      return false;
    }
  }

  @Override
  public BlockPos getMasterPosition() {
    return this.master;
  }

  public void overrideMaster(BlockPos pos) {
    assert this.world != null;
    this.hasMaster = true;
    this.master = pos;
    this.state = this.world.getBlockState(this.master);
    this.masterBlock = this.state.getBlock();
    this.markDirtyFast();
  }

  public void removeMaster() {
    this.hasMaster = false;
    this.master = null;
    this.masterBlock = null;
    this.state = null;
    this.markDirtyFast();
  }

  @Override
  public boolean setPotentialMaster(slimeknights.mantle.multiblock.IMasterLogic master, World w, BlockPos pos) {
    return !this.hasMaster;
  }

  @Deprecated
  public boolean verifyMaster(slimeknights.mantle.multiblock.IMasterLogic logic, BlockPos pos) {
    assert this.world != null;
    return this.master.equals(pos) && this.world.getBlockState(pos) == this.state
           && this.world.getBlockState(pos).getBlock() == this.masterBlock;
  }

  @Override
  public boolean verifyMaster(slimeknights.mantle.multiblock.IMasterLogic logic, World world, BlockPos pos) {
    if (this.hasMaster) {
      return this.hasValidMaster();
    }
    else {
      this.overrideMaster(pos);
      return true;
    }
  }

  @Override
  public void invalidateMaster(slimeknights.mantle.multiblock.IMasterLogic master, World w, BlockPos pos) {
    this.removeMaster();
  }

  @Override
  public void notifyMasterOfChange() {
    if (this.hasValidMaster()) {
      assert this.world != null;
      slimeknights.mantle.multiblock.IMasterLogic logic = (IMasterLogic) this.world.getTileEntity(this.master);
      logic.notifyChange(this, this.pos);
    }
  }

  public void readCustomNBT(CompoundNBT tags) {
    this.hasMaster = tags.getBoolean("hasMaster");
    if (this.hasMaster) {
      int xCenter = tags.getInt("xCenter");
      int yCenter = tags.getInt("yCenter");
      int zCenter = tags.getInt("zCenter");
      this.master = new BlockPos(xCenter, yCenter, zCenter);
      this.masterBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tags.getString("MasterBlockName")));
      this.state = Block.getStateById(tags.getInt("masterState"));
    }
  }

  public CompoundNBT writeCustomNBT(CompoundNBT tags) {
    tags.putBoolean("hasMaster", this.hasMaster);
    if (this.hasMaster) {
      tags.putInt("xCenter", this.master.getX());
      tags.putInt("yCenter", this.master.getY());
      tags.putInt("zCenter", this.master.getZ());
      tags.putString("MasterBlockName", Objects.requireNonNull(this.masterBlock.getRegistryName()).toString());
      tags.putInt("masterState", Block.getStateId(this.state));
    }
    return tags;
  }

  @Override
  public void read(BlockState blockState, CompoundNBT tags) {
    super.read(blockState, tags);
    this.readCustomNBT(tags);
  }

  @Override
  public CompoundNBT write(CompoundNBT tags) {
    tags = super.write(tags);
    return this.writeCustomNBT(tags);
  }

  /* Packets */
  @Override
  public CompoundNBT getUpdateTag() {
    CompoundNBT tag = new CompoundNBT();
    this.writeCustomNBT(tag);
    return tag;
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
    this.readCustomNBT(packet.getNbtCompound());
    //this.world.notifyLightSet(this.pos);
    assert world != null;
    BlockState state = world.getBlockState(this.pos);
    this.world.notifyBlockUpdate(this.pos, state, state, 3);
  }

  @Deprecated
  public boolean setMaster(BlockPos pos) {
    assert this.world != null;
    if (!this.hasMaster || this.world.getBlockState(this.master) != this.state || (this.world.getBlockState(this.master).getBlock() != this.masterBlock)) {
      this.overrideMaster(pos);
      return true;
    }
    else {
      return false;
    }
  }

}
