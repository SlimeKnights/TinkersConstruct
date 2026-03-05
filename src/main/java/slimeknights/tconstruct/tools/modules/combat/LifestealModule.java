package slimeknights.tconstruct.tools.modules.combat;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for stealing health after an attack.
 * @param percent          Percentage of health to steal.
 * @param durabilityUsage  Amount of durability to use when stealing health.
 * @param attacker         Condition on the attacker who will heal.
 * @param target           Condition on the target that was damaged.
 * @param modifierLevel    Condition on the modifier level for this module to apply.
 */
public record LifestealModule(LevelingValue percent, LevelingInt durabilityUsage, IJsonPredicate<LivingEntity> attacker, IJsonPredicate<LivingEntity> target, IntRange modifierLevel) implements ModifierModule, MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileLaunchModifierHook, ProjectileHitModifierHook, TooltipModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<LifestealModule>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<LifestealModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("percentage", LifestealModule::percent),
    LevelingInt.LOADABLE.requiredField("durability_usage", LifestealModule::durabilityUsage),
    LivingEntityPredicate.LOADER.defaultField("attacker", LifestealModule::attacker),
    LivingEntityPredicate.LOADER.defaultField("target", LifestealModule::target),
    ModifierEntry.VALID_LEVEL.defaultField("modifier_level", LifestealModule::modifierLevel),
    LifestealModule::new);

  /** @apiNote use {@link #builder()} */
  @Internal
  public LifestealModule {}

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
    LivingEntity attacker = context.getAttacker();
    Entity target = context.getTarget();
    if (damage > 0 && this.modifierLevel.test(modifier.getLevel()) && !context.getTarget().getType().is(TinkerTags.EntityTypes.NECROTIC_BLACKLIST) && this.attacker.matches(attacker) && TinkerPredicate.matches(this.target, target)) {
      // heals a percentage of damage dealt
      float level = modifier.getEffectiveLevel();
      float percent = this.percent.compute(modifier.getEffectiveLevel());
      if (percent > 0) {
        attacker.heal(percent * damage);
        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);
        // take a bit of extra damage to heal
        int durability = durabilityUsage.compute(level);
        if (durability > 0) {
          ToolDamageUtil.damageAnimated(tool, durability, attacker, context.getSlotType(), modifier.getId());
        }
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged() && context.isCritical()) {
      onMonsterMeleeHit(tool, modifier, context, damageDealt);
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    if (primary && this.attacker.matches(shooter)) {
      int durabilityUsage = this.durabilityUsage.compute(modifier.getEffectiveLevel());
      if (durabilityUsage > 0) {
        ToolDamageUtil.damageLauncher(tool, durabilityUsage, shooter, projectile, modifier.getId());
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
    if (notBlocked && target != null && attacker != null && !target.getType().is(TinkerTags.EntityTypes.NECROTIC_BLACKLIST) && this.attacker.matches(attacker) && this.target.matches(target)) {
      float level = modifier.getEffectiveLevel();
      float percent = this.percent.compute(modifier.getEffectiveLevel());
      if (percent > 0) {
        // we don't actually know how much damage will be dealt, so just grab the projectile power, scaled by velocity if needed
        // to prevent healing too much, limit by the target's health. Will let you life steal ignoring armor, but eh, only so much we can do efficiently
        float power = ProjectileWithPower.getDamage(projectile);
        if (power > 0) {
          attacker.heal(percent * Math.min(target.getHealth(), power));
          attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);

          // damage fishing rod
          int durabilityUsage = this.durabilityUsage.compute(level);
          if (durabilityUsage > 0) {
            ModifierUtil.updateFishingRod(projectile, durabilityUsage, false, modifier.getId());
          }
        }
      }
    }
    return false;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (TinkerPredicate.matchesInTooltip(this.attacker, player, tooltipKey)) {
      float percent = this.percent.compute(entry.getEffectiveLevel());
      if (percent > 0) {
        Modifier modifier = entry.getModifier();
        tooltip.add(modifier.applyStyle(Component.literal(Util.PERCENT_FORMAT.format(percent) + " ").append(Component.translatable(modifier.getTranslationKey() + ".lifesteal"))));
      }
    }
  }


  /* Builder */

  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(fluent = true)
  public static class Builder implements LevelingValue.Builder<LifestealModule> {
    private LevelingInt durabilityUsage = LevelingInt.eachLevel(1);
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private IntRange modifierLevel = ModifierEntry.VALID_LEVEL;

    private Builder() {}

    @Override
    public LifestealModule amount(float flat, float eachLevel) {
      return new LifestealModule(new LevelingValue(flat, eachLevel), durabilityUsage, attacker, target, modifierLevel);
    }
  }
}
