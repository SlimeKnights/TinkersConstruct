package slimeknights.tconstruct.tools.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.display.ToolNameHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.ModifierIds;

import javax.annotation.Nullable;

/** Based on {@link net.minecraft.world.entity.projectile.ThrownTrident} for throwing a modifiable weapon. */
public class ThrownTool extends ThrownTrident implements ToolProjectile {
  /** Key to sync the stack to the client */
  protected static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(ThrownTool.class, EntityDataSerializers.ITEM_STACK);
  /** Movement speed in water */
  protected static final EntityDataAccessor<Float> WATER_INERTIA = SynchedEntityData.defineId(ThrownTool.class, EntityDataSerializers.FLOAT);
  /** Volatile integer key for the loyalty level */
  public static final ResourceLocation LOYALTY = TConstruct.getResource("loyalty");

  @Nullable
  private IToolStackView tool = null;
  private float charge = 1;
  private float multiplier = 1;
  private boolean noDespawn = false;

  public ThrownTool(EntityType<? extends ThrownTrident> type, Level level) {
    super(type, level);
  }

  public ThrownTool(Level level, LivingEntity shooter, ItemStack stack, float charge, float multiplier, float waterInertia) {
    this(TinkerTools.thrownTool.get(), level);
    // AbstractArrow - positional constructor
    this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    // AbstractArrow - shooter constructor
    this.setOwner(shooter);
    if (shooter instanceof Player) {
      this.pickup = AbstractArrow.Pickup.ALLOWED;
    }
    // trident - stack constructor
    this.tridentItem = stack.copyWithCount(1);
    this.charge = charge;
    this.multiplier = multiplier;
    this.entityData.set(WATER_INERTIA, waterInertia);
    updateFromStack();
  }

  /** Sets any relevant properties from the stack */
  private void updateFromStack() {
    this.entityData.set(STACK, tridentItem);
    this.entityData.set(ID_LOYALTY, (byte) ModifierUtil.getVolatileInt(tridentItem, LOYALTY));
    this.entityData.set(ID_FOIL, ModifierUtil.checkVolatileFlag(tridentItem, ModifiableItem.SHINY));
    this.noDespawn = ModifierUtil.checkVolatileFlag(tridentItem, IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY);
  }

  @Override
  protected float getWaterInertia() {
    return entityData.get(WATER_INERTIA);
  }

  @Override
  public boolean isChanneling() {
    return !tridentItem.isEmpty() && getTool().getModifiers().getLevel(ModifierIds.channeling) > 0;
  }

  @Override
  public Component getDisplayName() {
    if (tridentItem.isEmpty()) {
      return super.getDisplayName();
    }
    IToolStackView tool = getTool();
    return ToolNameHook.getName(tool.getDefinition(), tridentItem, tool);
  }


  /* Despawn */

  @Override
  public void tickDespawn() {
    // if no pickup, despawn in 1 minute
    if (pickup != Pickup.ALLOWED || tridentItem.isEmpty()) {
      life += 1;
      if (life >= 1200) {
        this.discard();
      }
      // if its worldbound or loyalty, don't despawn
    } else if (!noDespawn && this.entityData.get(ID_LOYALTY) == 0) {
      // otherwise despawn in 5 minutes like a normal item. Like seriously mojang, why does your rare enchanted trident despawn in 1 minute?
      this.life += 1;
      if (this.life >= 6000) {
        this.discard();
      }
    }
  }

  @Override
  protected void onBelowWorld() {
    // don't discard tools below world if they have loyalty
    if (pickup == Pickup.ALLOWED && this.entityData.get(ID_LOYALTY) != 0) {
      // ensure it returns
      dealtDamage = true;
      // we don't damage the tool on throw, so instead damage it when it hits a block or an entity
      if (!tridentItem.isEmpty()) {
        ToolDamageUtil.damage(getTool(), 1, getOwner() instanceof LivingEntity l ? l : null, tridentItem);
      }
    } else {
      super.onBelowWorld();
    }
  }


  /* Combat */

  /** Gets the tool instance, ensuring its created */
  private IToolStackView getTool() {
    if (tool == null) {
      tool = ToolStack.from(tridentItem);
    }
    return tool;
  }

  @Override
  public void tick() {
    // TODO: consider expiry time for loyalty
    if (!dealtDamage && inGroundTime > 4) {
      // we don't damage the tool on throw, so instead damage it when it hits a block or an entity
      if (!tridentItem.isEmpty()) {
        ToolDamageUtil.damage(getTool(), 1, getOwner() instanceof LivingEntity l ? l : null, tridentItem);
      }
      dealtDamage = true;
    }
    super.tick();
  }

  @Override
  protected void onHitEntity(EntityHitResult pResult) {
    this.dealtDamage = true;

    // need a living entity to run our attack hooks, just do nothing if we lack an owner
    if (!tridentItem.isEmpty() && this.getOwner() instanceof LivingEntity owner) {
      Entity target = pResult.getEntity();
      // hack: swap the offhand for the tool so any relevant modifier hooks (notably looting) see the right thing
      ItemStack offhand = owner.getOffhandItem();

      IToolStackView tool = getTool();
      if (ToolAttackUtil.canPerformAttack(tool) && ToolAttackUtil.isAttackable(owner, target)) {
        // does not actually matter which slot we use, just need the tool there to ensure hooks are properly run
        owner.setItemInHand(InteractionHand.OFF_HAND, tridentItem);
        // TODO: consider whether redundant sound is fine
        if (ToolAttackUtil.performAttack(tool, ToolAttackContext.attacker(owner).target(target).hand(InteractionHand.OFF_HAND).baseDamage(tool.getStats().get(ToolStats.ATTACK_DAMAGE) * multiplier).cooldown(charge).projectile(this).build())) {
          if (target.getType() == EntityType.ENDERMAN && tool.getModifiers().getLevel(TinkerModifiers.enderference.getId()) == 0) {
            // restore held item
            owner.setItemInHand(InteractionHand.OFF_HAND, offhand);
            return;
          }
          if (target instanceof LivingEntity living) {
            this.doPostHurtEffects(living);
          }
        }

        // restore held item
        owner.setItemInHand(InteractionHand.OFF_HAND, offhand);
      }

      // back off from the target
      this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
      // play sound
      if (!level().isClientSide && tool.getModifiers().getLevel(ModifierIds.channeling) == 0) {
        this.playSound(tool.isBroken() ? SoundEvents.ITEM_BREAK : SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
      }
    }
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


  /* NBT */
  private static final String KEY_CHARGE = "charge";
  private static final String KEY_MULTIPLIER = "multiplier";
  private static final String KEY_WATER_INERTIA = "water_inertia";

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putFloat(KEY_CHARGE, this.charge);
    tag.putFloat(KEY_MULTIPLIER, this.multiplier);
    tag.putFloat(KEY_WATER_INERTIA, this.entityData.get(WATER_INERTIA));
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    // update the tool to sync to client, if its set
    if (tag.contains("Trident", CompoundTag.TAG_COMPOUND)) {
      updateFromStack();
    }
    this.charge = tag.getFloat(KEY_CHARGE);
    this.multiplier = tag.getFloat(KEY_MULTIPLIER);
    this.entityData.set(WATER_INERTIA, tag.getFloat(KEY_WATER_INERTIA));
  }
}
