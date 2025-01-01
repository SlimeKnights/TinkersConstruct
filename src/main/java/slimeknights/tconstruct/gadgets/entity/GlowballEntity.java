package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nonnull;

public class GlowballEntity extends ThrowableItemProjectile implements IEntityAdditionalSpawnData {

  public GlowballEntity(EntityType<? extends GlowballEntity> p_i50159_1_, Level p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public GlowballEntity(Level worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.glowBallEntity.get(), throwerIn, worldIn);
  }

  public GlowballEntity(Level worldIn, double x, double y, double z) {
    super(TinkerGadgets.glowBallEntity.get(), x, y, z, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.glowBall.get();
  }

  @SuppressWarnings("ConstantConditions")  // getType() enforces the class type
  @Override
  protected void onHit(HitResult result) {
    Level level = level();
    if (!level.isClientSide) {
      BlockPos position = null;
      Direction direction = Direction.DOWN;

      if (result.getType() == HitResult.Type.ENTITY) {
        position = ((EntityHitResult) result).getEntity().blockPosition();
      }

      if (result.getType() == HitResult.Type.BLOCK) {
        BlockHitResult blockHit = (BlockHitResult) result;
        position = blockHit.getBlockPos().relative(blockHit.getDirection());
        direction = blockHit.getDirection().getOpposite();
      }

      if (position != null) {
        TinkerCommons.glow.get().addGlow(level, position, direction);
      }
    }

    if (!level.isClientSide) {
      level.broadcastEntityEvent(this, (byte) 3);
      this.discard();
    }
  }

  @Override
  public void writeSpawnData(FriendlyByteBuf buffer) {
    buffer.writeItem(this.getItemRaw());
  }

  @Override
  public void readSpawnData(FriendlyByteBuf additionalData) {
    this.setItem(additionalData.readItem());
  }

  @Nonnull
  @Override
  public Packet<ClientGamePacketListener> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
