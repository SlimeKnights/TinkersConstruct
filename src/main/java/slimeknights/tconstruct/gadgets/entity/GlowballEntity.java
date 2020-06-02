package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.items.GadgetItems;

import javax.annotation.Nonnull;

public class GlowballEntity extends ProjectileItemEntity implements IEntityAdditionalSpawnData {

  public GlowballEntity(EntityType<? extends GlowballEntity> p_i50159_1_, World p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public GlowballEntity(World worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.throwable_glow_ball, throwerIn, worldIn);
  }

  public GlowballEntity(World worldIn, double x, double y, double z) {
    super(TinkerGadgets.throwable_glow_ball, x, y, z, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return GadgetItems.glow_ball.get();
  }

  @Override
  protected void onImpact(RayTraceResult result) {
    if (!this.world.isRemote) {
      BlockPos position = null;
      Direction direction = Direction.DOWN;

      if (result.getType() == RayTraceResult.Type.ENTITY) {
        position = ((EntityRayTraceResult) result).getEntity().getPosition();
      }

      if (result.getType() == RayTraceResult.Type.BLOCK) {
        BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) result;
        position = blockraytraceresult.getPos().offset(blockraytraceresult.getFace());
        direction = blockraytraceresult.getFace().getOpposite();
      }

      if (position != null) {
        CommonBlocks.glow.get().addGlow(this.world, position, direction);
      }
    }

    if (!this.world.isRemote) {
      this.world.setEntityState(this, (byte) 3);
      this.remove();
    }
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer) {
    buffer.writeItemStack(this.func_213882_k());
  }

  @Override
  public void readSpawnData(PacketBuffer additionalData) {
    this.setItem(additionalData.readItemStack());
  }

  @Nonnull
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
