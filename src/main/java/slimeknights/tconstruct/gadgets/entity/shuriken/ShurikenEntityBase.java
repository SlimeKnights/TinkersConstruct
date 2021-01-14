package slimeknights.tconstruct.gadgets.entity.shuriken;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public abstract class ShurikenEntityBase extends ProjectileItemEntity implements IEntityAdditionalSpawnData {

  public ShurikenEntityBase(EntityType<? extends ShurikenEntityBase> type, World worldIn) {
    super(type, worldIn);
  }

  public ShurikenEntityBase(EntityType<? extends ShurikenEntityBase> type, double x, double y, double z, World worldIn) {
    super(type, x, y, z, worldIn);
  }

  public ShurikenEntityBase(EntityType<? extends ShurikenEntityBase> type, LivingEntity livingEntityIn, World worldIn) {
    super(type, livingEntityIn, worldIn);
  }

  public abstract float getDamage();

  public abstract float getKnockback();

  @Override
  protected void onEntityHit(EntityRayTraceResult result) {
    Entity entity = result.getEntity();
    entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), this.getDamage());

    if (entity instanceof LivingEntity) {
      Vector3d motion = this.getMotion().normalize();
      ((LivingEntity) entity).applyKnockback(this.getKnockback(), -motion.x, -motion.z);
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
