package slimeknights.tconstruct.tools.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ScheduledProjectileTaskModifierHook;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Schedule;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;

/** Arrow with material variants */
public class ModifiableArrow extends AbstractArrow implements ToolProjectile {
  /** Key to sync the stack to the client */
  protected static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(ModifiableArrow.class, EntityDataSerializers.ITEM_STACK);
  /** Movement speed in water */
  protected static final EntityDataAccessor<Float> WATER_INERTIA = SynchedEntityData.defineId(ModifiableArrow.class, EntityDataSerializers.FLOAT);

  private ItemStack stack = ItemStack.EMPTY;
  private IToolStackView tool = null;
  private boolean reclaim = false;
  private boolean dealtDamage = false;
  /** Tasks queued by modifiers */
  private Schedule tasks = Schedule.EMPTY;

  public ModifiableArrow(EntityType<? extends AbstractArrow> type, Level level) {
    super(type, level);
  }

  public ModifiableArrow(Level level, double pX, double pY, double pZ) {
    super(TinkerTools.materialArrow.get(), pX, pY, pZ, level);
  }

  public ModifiableArrow(Level level, LivingEntity shooter) {
    super(TinkerTools.materialArrow.get(), shooter, level);
  }


  /* Stack */

  @Override
  public ItemStack getPickupItem() {
    return stack.copy();
  }

  /** Updates the stack on the arrow */
  private void setStack(ItemStack stack) {
    this.stack = stack;
    this.entityData.set(STACK, stack);
    this.reclaim = ModifierUtil.checkVolatileFlag(stack, IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY);
  }

  /** Gets the tool instance, ensuring its created */
  private IToolStackView getTool() {
    if (tool == null) {
      tool = ToolStack.from(stack);
    }
    return tool;
  }

  /**
   * Called when the arrow is created to set initial properties.
   * @see ThrownShuriken#onCreate(ItemStack, LivingEntity)
   */
  public IToolStackView onCreate(ItemStack stack, @Nullable LivingEntity shooter) {
    stack = stack.copyWithCount(1);
    setStack(stack);
    // initialize arrow stats
    IToolStackView tool = getTool();
    EntityModifierCapability.getCapability(this).addModifiers(tool.getModifiers());
    setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, shooter, ToolStats.PROJECTILE_DAMAGE));
    this.entityData.set(WATER_INERTIA, ConditionalStatModifierHook.getModifiedStat(tool, shooter, ToolStats.WATER_INERTIA));
    return tool;
  }

  /** @see ThrownShuriken#shoot(double, double, double, float, float)  */
  @Override
  public void shoot(double pX, double pY, double pZ, float velocity, float inaccuracy) {
    if (!stack.isEmpty()) {
      IToolStackView tool = getTool();
      // apply accuracy, no need to compute this earlier nor store it
      LivingEntity shooter = ModifierUtil.asLiving(getOwner());
      inaccuracy *= ModifierUtil.getInaccuracy(tool, shooter);

      // shoot with new information
      super.shoot(pX, pY, pZ, velocity, inaccuracy);

      // run modifier hooks from the arrow's perspective
      ModDataNBT arrowData = PersistentDataCapability.getOrWarn(this);
      for (ModifierEntry entry : tool.getModifiers()) {
        entry.getHook(ModifierHooks.PROJECTILE_SHOT).onProjectileShoot(tool, entry, shooter, stack, this, this, arrowData, true);
      }

      // schedule tasks
      this.tasks = ScheduledProjectileTaskModifierHook.createSchedule(tool, stack, this, this, arrowData);
    } else {
      super.shoot(pX, pY, pZ, velocity, inaccuracy);
    }
  }

  @Override
  public void tick() {
    super.tick();
    // check if any tasks are ready
    if (!tasks.isEmpty() && !stack.isEmpty()) {
      ScheduledProjectileTaskModifierHook.checkSchedule(getTool(), stack, this, this, tasks);
    }
  }

  /* Stats */

  @Override
  protected float getWaterInertia() {
    return entityData.get(WATER_INERTIA);
  }

  // need to replace some setters with adders so vanilla bows work with our logic

  @Override
  public void setKnockback(int knockback) {
    super.setKnockback(getKnockback() + knockback);
  }

  @Override
  public void setPierceLevel(byte pierceLevel) {
    super.setPierceLevel((byte) (getPierceLevel() + pierceLevel));
  }


  /* Despawn */

  @Override
  public void tickDespawn() {
    // if we can pick up the arrows, don't despawn with worldbound
    if (pickup != Pickup.ALLOWED || !reclaim) {
      super.tickDespawn();
    }
  }

  private enum CaptureDiscard { NOT_CAPTURING,  CAPTURING,  DISCARDED }
  private CaptureDiscard captureDiscard = CaptureDiscard.NOT_CAPTURING;

  @Override
  protected void onHitEntity(EntityHitResult result) {
    if (reclaim) {
      // prevent the entity from being discarded for a bit
      captureDiscard = CaptureDiscard.CAPTURING;
    }

    super.onHitEntity(result);

    // if we tried to discard it, back off the movement and mark it to prevent further damage
    if (captureDiscard == CaptureDiscard.DISCARDED) {
      dealtDamage = true;
      setDeltaMovement(getDeltaMovement().multiply(-0.01, -0.1, -0.01));
    }
    captureDiscard = CaptureDiscard.NOT_CAPTURING;
  }

  @Override
  public void remove(RemovalReason reason) {
    // capturing is used for worldbound to keep the ammo around after hit
    // however, there is a single case where we don't want to stick around, and that is when we failed to hit a target and the movement is now too small
    if (reason == RemovalReason.DISCARDED && captureDiscard != CaptureDiscard.NOT_CAPTURING && getDeltaMovement().lengthSqr() >= 1.0E-7D) {
      captureDiscard = CaptureDiscard.DISCARDED;
    } else {
      super.remove(reason);
    }
  }

  @Override
  @Nullable
  protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
    return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
  }


  /* Client */

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(STACK, ItemStack.EMPTY);
    this.entityData.define(WATER_INERTIA, 0.6f);
  }

  @Override
  public ItemStack getDisplayTool() {
    return this.entityData.get(STACK);
  }

  @Override
  public Component getDisplayName() {
    return getDisplayTool().getDisplayName();
  }


  /* NBT */
  private static final String KEY_STACK = "stack";
  private static final String KEY_WATER_INERTIA = "water_inertia";
  private static final String KEY_DEALT_DAMAGE = "dealt_damage";
  private static final String KEY_TASKS = "tasks";

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.put(KEY_STACK, this.stack.save(new CompoundTag()));
    tag.putFloat(KEY_WATER_INERTIA, this.entityData.get(WATER_INERTIA));
    tag.putBoolean(KEY_DEALT_DAMAGE, dealtDamage);
    if (!this.tasks.isEmpty()) {
      tag.put(KEY_TASKS, this.tasks.serialize());
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    if (tag.contains(KEY_STACK, CompoundTag.TAG_COMPOUND)) {
      setStack(ItemStack.of(tag.getCompound(KEY_STACK)));
    }
    this.entityData.set(WATER_INERTIA, tag.getFloat(KEY_WATER_INERTIA));
    this.dealtDamage = tag.getBoolean(KEY_DEALT_DAMAGE);
    if (tag.contains(KEY_TASKS, CompoundTag.TAG_LIST)) {
      this.tasks = Schedule.deserialize(tag.getList(KEY_TASKS, CompoundTag.TAG_COMPOUND));
    }
  }
}
