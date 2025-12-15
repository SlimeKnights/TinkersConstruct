package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class InsatiableModifier extends Modifier implements ProjectileHitModifierHook, ConditionalStatModifierHook, MeleeDamageModifierHook, MonsterMeleeHitModifierHook.RedirectAfter, MeleeHitModifierHook, TooltipModifierHook {
  public static final ToolType[] TYPES = {ToolType.MELEE, ToolType.RANGED};

  /** Gets the current bonus for the entity */
  private static float getEffect(LivingEntity attacker, ToolType type) {
    return TinkerEffect.getLevel(attacker, TinkerModifiers.insatiableEffect.get(type));
  }

  /** Applies the effect to the target */
  public static void applyEffect(LivingEntity living, ToolType type, int duration, int add, int maxLevel) {
    TinkerEffect effect = TinkerModifiers.insatiableEffect.get(type);
    effect.apply(living, duration, Math.min(maxLevel, TinkerEffect.getAmplifier(living, effect) + add), true);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.TOOLTIP);
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    // gives +0.5 per effect level, for +2.5 per modifier level at max
    return damage + (getEffect(context.getAttacker(), ToolType.MELEE) * modifier.getEffectiveLevel() / 2f * tool.getMultiplier(ToolStats.ATTACK_DAMAGE));
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    // 8 hits gets you to max, levels faster at higher levels
    if (!context.isExtraAttack() && context.isFullyCharged()) {
      applyEffect(context.getAttacker(), ToolType.MELEE, 5*20, 1, 4);
    }
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.PROJECTILE_DAMAGE) {
      // gives +0.25 power per effect level, up to +1.25 at 5 levels
      baseValue += (getEffect(living, ToolType.RANGED) * modifier.getEffectiveLevel() / 4f * multiplier);
    }
    return baseValue;
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
    if (attacker != null) {
      applyEffect(attacker, ToolType.RANGED, 10*20, 1, 4);
    }
    return false;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    ToolType type = ToolType.from(tool.getItem(), TYPES);
    if (type != null) {
      float level = modifier.getEffectiveLevel();
      float bonus = level * 2.5f;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getEffect(player, type) * level;
      }
      if (bonus > 0) {
        TooltipModifierHook.addFlatBoost(this, TooltipModifierHook.statName(this, ToolStats.ATTACK_DAMAGE), bonus, tooltip);
      }
    }
  }
}
