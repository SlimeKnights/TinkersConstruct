package slimeknights.tconstruct.library.tinkering;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.tconstruct.entity.ToolEntities;

import javax.annotation.Nonnull;

public class IndestructibleEntityItem extends ItemEntity {

  public IndestructibleEntityItem(EntityType<? extends IndestructibleEntityItem> entityType, World world) {
    super(entityType, world);
  }

  public IndestructibleEntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
    super(ToolEntities.indestructible_item, worldIn);
    this.setPosition(x, y, z);
    this.rotationYaw = this.rand.nextFloat() * 360.0F;
    this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
    this.setItem(stack);
    this.setNoDespawn();
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  public void setPickupDelayFrom(Entity reference) {
    if (reference instanceof ItemEntity) {
      short pickupDelay = getPickupDelay((ItemEntity) reference);
      setPickupDelay(pickupDelay);
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
  protected int getFireImmuneTicks() {
    return Integer.MAX_VALUE;
  }

  @Override
  protected void dealFireDamage(int p_70081_1_) {
    // no fire damage for you
  }

  @Override
  public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
    // prevent any damage besides out of world
    return source.getDamageType().equals(DamageSource.OUT_OF_WORLD.damageType);
  }
/*
  @SubscribeEvent
  public void onExpire(ItemExpireEvent event) {
    if (event.getEntityItem() instanceof IndestructibleEntityItem) {
      event.setCanceled(true);
    }
  }*/
}
