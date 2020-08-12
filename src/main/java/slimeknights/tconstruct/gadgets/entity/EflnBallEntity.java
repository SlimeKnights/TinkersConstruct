package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.tconstruct.gadgets.Exploder;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

import javax.annotation.Nonnull;

public class EflnBallEntity extends ProjectileItemEntity implements IEntityAdditionalSpawnData {

  public EflnBallEntity(EntityType<? extends EflnBallEntity> p_i50159_1_, World p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public EflnBallEntity(World worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.eflnEntity.get(), throwerIn, worldIn);
  }

  public EflnBallEntity(World worldIn, double x, double y, double z) {
    super(TinkerGadgets.eflnEntity.get(), x, y, z, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.efln.get();
  }

  @Override
  protected void onImpact(RayTraceResult result) {
    if (!this.world.isRemote) {
      EFLNExplosion explosion = new EFLNExplosion(this.world, this, null, null, this.getPosX(), this.getPosY(), this.getPosZ(), 6f, false, Explosion.Mode.NONE);
      if (!ForgeEventFactory.onExplosionStart(this.world, explosion)) {
        Exploder.startExplosion(this.world, explosion, this, new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ()), 6f, 6f);
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
