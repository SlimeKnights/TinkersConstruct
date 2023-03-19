package slimeknights.tconstruct.tables.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.table.RetexturedTableBlockEntity;

public class FallingAnvilEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {

  private String textureName;

  public FallingAnvilEntity(EntityType<FallingAnvilEntity> entityType, Level level) {
    super(TinkerTables.fallingAnvil.get(), level);
  }

  public FallingAnvilEntity(Level level, double x, double y, double z, BlockState state, BlockPos pos) {
    super(TinkerTables.fallingAnvil.get(), level);
    this.blockState = state;
    this.blocksBuilding = true;
    this.setPos(x, y, z);
    this.setDeltaMovement(Vec3.ZERO);
    this.xo = x;
    this.yo = y;
    this.zo = z;
    this.setStartPos(this.blockPosition());
    if (level.getBlockEntity(pos) instanceof RetexturedTableBlockEntity entity) {
      this.textureName = entity.getTextureName();
      this.blockData = entity.saveWithFullMetadata();
    }
  }


  public static FallingAnvilEntity fall(Level level, BlockPos pos, BlockState state) {
    FallingAnvilEntity anvil = new FallingAnvilEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, false) : state, pos);
    level.setBlock(pos, state.getFluidState().createLegacyBlock(), 67);
    level.addFreshEntity(anvil);
    return anvil;
  }

  @Override
  public BlockState getBlockState() {
    return blockState;
  }

  public IModelData getModelData() {
    return getRetexturedModelData();
  }

  public IModelData getRetexturedModelData() {
    // texture not loaded
    Block block = RetexturedHelper.getBlock(this.textureName);
    // cannot support air, saves a conditional on usage
    if (block == Blocks.AIR) {
      block = null;
    }
    return new SinglePropertyData<>(RetexturedHelper.BLOCK_PROPERTY, block);
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag pCompound) {
    pCompound.putString("textureName", textureName);
    super.addAdditionalSaveData(pCompound);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag pCompound) {
    this.textureName = pCompound.getString("textureName");
    super.load(pCompound);
  }

  public Packet<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
    super.recreateFromPacket(pPacket);
    this.blocksBuilding = true;
    double d0 = pPacket.getX();
    double d1 = pPacket.getY();
    double d2 = pPacket.getZ();
    this.setPos(d0, d1, d2);
    this.setStartPos(this.blockPosition());
  }

  @Override
  public void writeSpawnData(FriendlyByteBuf buffer) {
    buffer.writeInt(Block.getId(this.blockState));
    buffer.writeUtf(this.textureName);
    //buffer.writeNbt(blockData);
  }

  @Override
  public void readSpawnData(FriendlyByteBuf additionalData) {
    this.blockState = Block.stateById(additionalData.readInt());
    this.textureName = additionalData.readUtf();
    //this.blockData  = additionalData.readNbt();
  }
}
