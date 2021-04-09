package slimeknights.tconstruct.library.tinkering;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;
import slimeknights.tconstruct.tools.TinkerTools;

public class IndestructibleEntityItem extends ItemEntity {

  public IndestructibleEntityItem(EntityType<? extends IndestructibleEntityItem> entityType, World world) {
    super(entityType, world);
  }

  public IndestructibleEntityItem(World world) {
    super(TinkerTools.indestructibleItem, world);
  }

  public IndestructibleEntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
    super(TinkerTools.indestructibleItem, worldIn);
    this.updatePosition(x, y, z);
    this.yaw = this.random.nextFloat() * 360.0F;
    this.setVelocity(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
    this.setStack(stack);
    this.setCovetedItem();
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }

  public void setPickupDelayFrom(Entity reference) {
    if (reference instanceof ItemEntity) {
      short pickupDelay = this.getPickupDelay((ItemEntity) reference);
      this.setPickupDelay(pickupDelay);
    }
    setVelocity(reference.getVelocity());
  }

  /**
   * workaround for private access on pickup delay. We simply read it from the items NBT representation ;)
   */
  private short getPickupDelay(ItemEntity reference) {
    CompoundTag tag = new CompoundTag();
    reference.writeCustomDataToTag(tag);
    return tag.getShort("PickupDelay");
  }

  @Override
  public boolean isFireImmune() {
    return true;
  }

  @Override
  public boolean damage(DamageSource source, float amount) {
    // prevent any damage besides out of world
    return source.getName().equals(DamageSource.OUT_OF_WORLD.name);
  }
/*
  @SubscribeEvent
  public void onExpire(ItemExpireEvent event) {
    if (event.getEntityItem() instanceof IndestructibleEntityItem) {
      event.setCanceled(true);
    }
  }*/
}
