package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;

import org.jetbrains.annotations.Nonnull;

public class GlowballEntity extends ThrownItemEntity implements IEntityAdditionalSpawnData {

  public GlowballEntity(EntityType<? extends GlowballEntity> p_i50159_1_, World p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public GlowballEntity(World worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.glowBallEntity.get(), throwerIn, worldIn);
  }

  public GlowballEntity(World worldIn, double x, double y, double z) {
    super(TinkerGadgets.glowBallEntity.get(), x, y, z, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.glowBall.get();
  }

  @Override
  protected void onCollision(HitResult result) {
    if (!this.world.isClient) {
      BlockPos position = null;
      Direction direction = Direction.DOWN;

      if (result.getType() == HitResult.Type.ENTITY) {
        position = ((EntityHitResult) result).getEntity().getBlockPos();
      }

      if (result.getType() == HitResult.Type.BLOCK) {
        BlockHitResult blockraytraceresult = (BlockHitResult) result;
        position = blockraytraceresult.getBlockPos().offset(blockraytraceresult.getSide());
        direction = blockraytraceresult.getSide().getOpposite();
      }

      if (position != null) {
        TinkerCommons.glow.get().addGlow(this.world, position, direction);
      }
    }

    if (!this.world.isClient) {
      this.world.sendEntityStatus(this, (byte) 3);
      this.remove();
    }
  }

  @Override
  public void writeSpawnData(PacketByteBuf buffer) {
    buffer.writeItemStack(this.getItem());
  }

  @Override
  public void readSpawnData(PacketByteBuf additionalData) {
    this.setItem(additionalData.readItemStack());
  }

  @NotNull
  @Override
  public Packet<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
