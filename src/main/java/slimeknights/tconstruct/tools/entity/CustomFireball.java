package slimeknights.tconstruct.tools.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Custom implementation of {@link net.minecraft.world.entity.projectile.SmallFireball} for the sake of modifiers. */
public class CustomFireball extends Fireball implements ProjectileWithPower {
  /** Damage type to deal from this projectile */
  private ResourceKey<DamageType> damageType = DamageTypes.FIREBALL;
  /** Damage type to deal when enderference is targeting a teleporting mob */
  private ResourceKey<DamageType> enderferenceType = DamageTypes.ON_FIRE;
  /** Amount of damage to deal */
  @Getter @Setter
  private float power = 2.5f;
  /** Damage multiplier on power. Separate from power for the sake of conditional power modules. */
  @Setter
  private float damageMultiplier = 2f;

  public CustomFireball(EntityType<? extends CustomFireball> type, Level level) {
    super(type, level);
  }

  public CustomFireball(Level level, LivingEntity shooter, double xOffset, double yOffset, double zOffset) {
    super(TinkerModifiers.fireball.get(), shooter, xOffset, yOffset, zOffset, level);
  }

  public CustomFireball(Level pLevel, double x, double y, double z, double xOffset, double yOffset, double zOffset) {
    super(TinkerModifiers.fireball.get(), x, y, z, xOffset, yOffset, zOffset, pLevel);
  }


  /* Behavior */

  @Override
  protected boolean shouldBurn() {
    return false;
  }

  @Override
  public boolean isPickable() {
    return false;
  }

  @Override
  public boolean hurt(DamageSource pSource, float pAmount) {
    return false;
  }

  @Override
  protected Component getTypeName() {
    ItemStack stack = getItemRaw();
    if (!stack.isEmpty()) {
      return stack.getHoverName();
    }
    return super.getTypeName();
  }


  /* Hitting */

  /** Sets the damage type on this projectile */
  public void setDamageType(ResourceKey<DamageType> damageType, ResourceKey<DamageType> enderferenceType) {
    this.damageType = damageType;
    this.enderferenceType = enderferenceType;
  }

  @Override
  public float getDamage() {
    return ProjectileWithPower.velocityScale(this, power * damageMultiplier);
  }

  @Override
  protected void onHitEntity(EntityHitResult hit) {
    super.onHitEntity(hit);

    // based on SmallFireball, uses custom damage type and power though
    if (!this.level().isClientSide) {
      Entity target = hit.getEntity();
      Entity owner = this.getOwner();
      if (target.hurt(CombatHelper.damageSource(TinkerEffects.needsEnderferenceOverride(target) ? enderferenceType : damageType, this, owner), getDamage()) && owner instanceof LivingEntity livingOwner) {
        this.doEnchantDamageEffects(livingOwner, target);
      }
    }
  }

  @Override
  protected void onHit(HitResult pResult) {
    super.onHit(pResult);
    if (!this.level().isClientSide) {
      this.discard();
    }
  }


  /* Despawn */

  @Override
  public void tick() {
    super.tick();
    // should not exist for longer than 2 minutes, deals with the fact the projectile is not guaranteed to hit a block
    if (tickCount > 2400) {
      this.discard();
    }
  }

  @Override
  public void checkBelowWorld() {
    // despawn if going too high or low, otherwise projectile may live forever going into the sky
    double y = getY();
    Level level = level();
    if (y < (level.getMinBuildHeight() - 64) || y > level.getMaxBuildHeight() + 64) {
      onBelowWorld();
    }
  }


  /* NBT */
  private static final String TAG_POWER = "damage_power"; // "power" is taken by the velocity
  private static final String TAG_MULTIPLIER = "damage_multiplier";
  private static final String TAG_DAMAGE_TYPE = "damage_type";
  private static final String TAG_ENDERFERENCE_TYPE = "enderference_type";

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    tag.putFloat(TAG_POWER, power);
    tag.putFloat(TAG_MULTIPLIER, damageMultiplier);
    tag.putString(TAG_DAMAGE_TYPE, damageType.location().toString());
    tag.putString(TAG_ENDERFERENCE_TYPE, enderferenceType.location().toString());
  }

  /** Parses the given damage type */
  private static ResourceKey<DamageType> parseDamageType(String damageStr, ResourceKey<DamageType> fallback) {
    if (!damageStr.isEmpty()) {
      ResourceLocation damageLoc = ResourceLocation.tryParse(damageStr);
      if (damageLoc != null) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, damageLoc);
      }
    }
    return fallback;
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    power = tag.getFloat(TAG_POWER);
    damageMultiplier = tag.getFloat(TAG_MULTIPLIER);
    damageType = parseDamageType(tag.getString(TAG_DAMAGE_TYPE), DamageTypes.FIREBALL);
    enderferenceType = parseDamageType(tag.getString(TAG_ENDERFERENCE_TYPE), DamageTypes.ON_FIRE);
  }
}
