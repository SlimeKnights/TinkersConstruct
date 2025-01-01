package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;

/** Item entity that will never die */
public class IndestructibleItemEntity extends ItemEntity {
  public IndestructibleItemEntity(EntityType<? extends IndestructibleItemEntity> entityType, Level world) {
    super(entityType, world);
    // using setUnlimitedLifetime() makes the item no longer spin, dumb design
    // since age is a short, this value should never be reachable so the item will never despawn
    this.lifespan = Integer.MAX_VALUE;
  }

  public IndestructibleItemEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
    this(TinkerTools.indestructibleItem.get(), worldIn);
    this.setPos(x, y, z);
    this.setYRot(this.random.nextFloat() * 360.0F);
    this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
    this.setItem(stack);
  }

  @Override
  public Packet<ClientGamePacketListener> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  /** Copies the pickup delay from another entity */
  public void setPickupDelayFrom(Entity reference) {
    if (reference instanceof ItemEntity) {
      short pickupDelay = this.getPickupDelay((ItemEntity) reference);
      this.setPickUpDelay(pickupDelay);
    }
    setDeltaMovement(reference.getDeltaMovement());
  }

  /**
   * workaround for private access on pickup delay. We simply read it from the items NBT representation ;)
   */
  private short getPickupDelay(ItemEntity reference) {
    CompoundTag tag = new CompoundTag();
    reference.addAdditionalSaveData(tag);
    return tag.getShort("PickupDelay");
  }

  @Override
  public boolean fireImmune() {
    return true;
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    // prevent any damage besides out of world
    return source.is(DamageTypes.FELL_OUT_OF_WORLD);
  }

  /** Checks if the given stack has a custom entity */
  public static boolean hasCustomEntity(ItemStack stack) {
    return ModifierUtil.checkVolatileFlag(stack, IModifiable.INDESTRUCTIBLE_ENTITY);
  }

  /**
   * Creates an indestructible item entity from the given item stack (if needed). Intended to be called in {@link net.minecraftforge.common.extensions.IForgeItem#createEntity(Level, Entity, ItemStack)}
   * @param world     World instance
   * @param original  Original entity
   * @param stack     Stack to drop
   * @return  indestructible entity, or null if the stack is not marked indestructible
   */
  @Nullable
  public static Entity createFrom(Level world, Entity original, ItemStack stack) {
    if (ModifierUtil.checkVolatileFlag(stack, IModifiable.INDESTRUCTIBLE_ENTITY)) {
      IndestructibleItemEntity entity = new IndestructibleItemEntity(world, original.getX(), original.getY(), original.getZ(), stack);
      entity.setPickupDelayFrom(original);
      return entity;
    }
    return null;
  }
}
