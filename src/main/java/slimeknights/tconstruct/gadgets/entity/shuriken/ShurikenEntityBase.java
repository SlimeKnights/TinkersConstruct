package slimeknights.tconstruct.gadgets.entity.shuriken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class ShurikenEntityBase extends ThrownItemEntity {

  public ShurikenEntityBase(EntityType<? extends ShurikenEntityBase> type, World worldIn) {
    super(type, worldIn);
  }

  public ShurikenEntityBase(EntityType<? extends ShurikenEntityBase> type, double x, double y, double z, World worldIn) {
    super(type, x, y, z, worldIn);
  }

  public ShurikenEntityBase(EntityType<? extends ShurikenEntityBase> type, LivingEntity livingEntityIn, World worldIn) {
    super(type, livingEntityIn, worldIn);
  }

    /**
   * Get damage dealt by Shuriken
   * Should be <= 20.0F
   * @return float damage
   */
  public abstract float getDamage();

  /**
   * Get knockback dealt by Shuriken
   * Should be <= 1.0F, Minecraft
   * typically uses values from 0.2F-0.6F
   * @return float knockback
   */
  public abstract float getKnockback();

  @Override
  protected void onCollision(HitResult result) {
    super.onCollision(result);

    if (!this.world.isClient) {
      this.world.sendEntityStatus(this, (byte) 3);
      this.remove();
    }
  }

  @Override
  protected void onBlockHit(BlockHitResult result) {
    super.onBlockHit(result);

    this.dropItem(getDefaultItem());
  }

  @Override
  protected void onEntityHit(EntityHitResult result) {
    Entity entity = result.getEntity();
    entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), this.getDamage());

    if (entity instanceof LivingEntity) {
      Vec3d motion = this.getVelocity().normalize();
      ((LivingEntity) entity).takeKnockback(this.getKnockback(), -motion.x, -motion.z);
    }
  }

//  @Override
//  public void writeSpawnData(PacketByteBuf buffer) {
//    buffer.writeItemStack(this.getItem());
//  }
//
//  @Override
//  public void readSpawnData(PacketByteBuf additionalData) {
//    this.setItem(additionalData.readItemStack());
//  }
//
//  @NotNull
//  @Override
//  public Packet<?> createSpawnPacket() {
//    return NetworkHooks.getEntitySpawningPacket(this);
//  }
}
