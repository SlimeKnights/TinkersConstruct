package slimeknights.tconstruct.library.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.tconstruct.tools.TinkerTools;

/** Item entity that will never die */
public class IndestructibleItemEntity extends ItemEntity {
  public IndestructibleItemEntity(EntityType<? extends IndestructibleItemEntity> entityType, World world) {
    super(entityType, world);
    this.setNoDespawn();
  }

  public IndestructibleItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
    this(TinkerTools.indestructibleItem.get(), worldIn);
    this.setPosition(x, y, z);
    this.rotationYaw = this.rand.nextFloat() * 360.0F;
    this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
    this.setItem(stack);
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  /** Copies the pickup delay from another entity */
  public void setPickupDelayFrom(Entity reference) {
    if (reference instanceof ItemEntity) {
      short pickupDelay = this.getPickupDelay((ItemEntity) reference);
      this.setPickupDelay(pickupDelay);
    }
    setMotion(reference.getMotion());
  }

  /**
   * workaround for private access on pickup delay. We simply read it from the items NBT representation ;)
   */
  private short getPickupDelay(ItemEntity reference) {
    CompoundNBT tag = new CompoundNBT();
    reference.writeAdditional(tag);
    return tag.getShort("PickupDelay");
  }

  @Override
  public boolean isImmuneToFire() {
    return true;
  }

  @Override
  public boolean attackEntityFrom(DamageSource source, float amount) {
    // prevent any damage besides out of world
    return source.getDamageType().equals(DamageSource.OUT_OF_WORLD.damageType);
  }
}
