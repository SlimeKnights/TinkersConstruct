package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.tconstruct.gadgets.Exploder;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

import javax.annotation.Nonnull;

public class EflnBallEntity extends ThrowableItemProjectile implements IEntityAdditionalSpawnData {

  public EflnBallEntity(EntityType<? extends EflnBallEntity> p_i50159_1_, Level p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public EflnBallEntity(Level worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.eflnEntity.get(), throwerIn, worldIn);
  }

  public EflnBallEntity(Level worldIn, double x, double y, double z) {
    super(TinkerGadgets.eflnEntity.get(), x, y, z, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.efln.get();
  }

  @Override
  protected void onHit(HitResult result) {
    if (!this.level.isClientSide) {
      EFLNExplosion explosion = new EFLNExplosion(this.level, this, null, null, this.getX(), this.getY(), this.getZ(), 6f, false, Explosion.BlockInteraction.NONE);
      if (!ForgeEventFactory.onExplosionStart(this.level, explosion)) {
        Exploder.startExplosion(this.level, explosion, this, new BlockPos(this.getX(), this.getY(), this.getZ()), 6f, 6f);
      }
    }

    if (!this.level.isClientSide) {
      this.level.broadcastEntityEvent(this, (byte) 3);
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
  public Packet<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
