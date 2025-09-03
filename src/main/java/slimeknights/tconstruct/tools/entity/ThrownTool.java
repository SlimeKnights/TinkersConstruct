package slimeknights.tconstruct.tools.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.ModifierIds;

import javax.annotation.Nullable;

/** Based on {@link net.minecraft.world.entity.projectile.ThrownTrident} for throwing a modifiable weapon. */
public class ThrownTool extends ThrownTrident {
  /** Key to sync the stack to the client */
  protected static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(ThrownTool.class, EntityDataSerializers.ITEM_STACK);

  @Nullable
  private IToolStackView tool = null;
  private float charge = 1;

  public ThrownTool(EntityType<? extends ThrownTrident> type, Level level) {
    super(type, level);
  }

  public ThrownTool(Level level, LivingEntity shooter, ItemStack stack, IToolStackView tool, float charge) {
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
    this.entityData.set(STACK, tridentItem);
    this.entityData.set(ID_LOYALTY, (byte) tool.getModifiers().getLevel(ModifierIds.loyalty));
    // TODO: find loyalty on the tool somewhere, maybe just the modifier ID?
    this.entityData.set(ID_FOIL, tool.getVolatileData().getBoolean(ModifiableItem.SHINY));
    this.charge = charge;
  }

  @Override
  public boolean isChanneling() {
    // TODO: hardcode to channeling modifier perhaps?
    return false;
  }

  @Override
  public void tickDespawn() {
    // if no pickup, despawn in 1 minute
    if (pickup != Pickup.ALLOWED || tridentItem.isEmpty()) {
      life += 1;
      if (life >= 1200) {
        this.discard();
      }
      // if its worldbound or loyalty, don't despawn
    } else if (this.entityData.get(ID_LOYALTY) == 0 && getTool().getVolatileData().getBoolean(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY)) {
      // otherwise despawn in 5 minutes like a normal item. Like seriously mojang, why does your rare enchanted trident despawn in 1 minute?
      this.life += 1;
      if (this.life >= 6000) {
        this.discard();
      }
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
      owner.setItemInHand(InteractionHand.OFF_HAND, tridentItem);

      // OFFHAND slot is a bit of a hack, ensures the damage is fetched from the tool instead of the attribute, and any hooks detect the tool properly
      IToolStackView tool = getTool();
      // isExtraAttack bypasses a lot of main hit behaviors, like critical and sounds
      if (ToolAttackUtil.attackEntity(tool, owner, InteractionHand.OFF_HAND, target, () -> charge, false, EquipmentSlot.OFFHAND, this)) {
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

      // back off from the target
      this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
      // play sound
      // TODO: change this sound for channeling?
      this.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
    }
  }

  /* Client */

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(STACK, ItemStack.EMPTY);
  }

  /** Gets the tool for display. Use {@link #getPickupItem()} for pickup. */
  public ItemStack getToolItem() {
    return this.entityData.get(STACK);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    // update the tool to sync to client, if its set
    if (tag.contains("Trident", CompoundTag.TAG_COMPOUND)) {
      this.entityData.set(STACK, tridentItem);
      this.entityData.set(ID_LOYALTY, (byte) ModifierUtil.getModifierLevel(tridentItem, ModifierIds.loyalty));
    }
  }
}
