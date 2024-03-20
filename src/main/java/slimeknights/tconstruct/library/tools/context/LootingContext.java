package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * Context for looting modifier hooks such as {@link slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook} and {@link slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook}.
 */
@Getter
public class LootingContext {
  /** Entity using the tool */
  private final LivingEntity holder;
  /** Entity being targeted */
  private final Entity target;
  /** Living entity being targeted */
  @Nullable
  private final LivingEntity livingTarget;
  /** Damage source triggering the looting, may be null for non-damage based looting (e.g. shearing) */
  @Nullable
  private final DamageSource damageSource;
  /** Slot used to perform the looting, may be null for projectiles causing looting */
  @Nullable
  private final EquipmentSlot lootingSlot;

  public LootingContext(LivingEntity holder, Entity target, @Nullable DamageSource damageSource, @Nullable EquipmentSlot lootingSlot) {
    this.holder = holder;
    this.target = target;
    this.livingTarget = target instanceof LivingEntity living ? living : null;
    this.damageSource = damageSource;
    this.lootingSlot = lootingSlot;
  }

  public LootingContext(LivingEntity holder, LivingEntity target, @Nullable DamageSource damageSource, @Nullable EquipmentSlot lootingSlot) {
    this.holder = holder;
    this.target = target;
    this.livingTarget = target;
    this.damageSource = damageSource;
    this.lootingSlot = lootingSlot;
  }

  /** If true, this looting is caused by a projectile */
  public boolean isIndirect() {
    return lootingSlot == null;
  }

  /** If true, this looting is caused by an attack */
  public boolean isAttack() {
    return damageSource != null;
  }
}
