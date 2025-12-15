package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class NecroticModifier extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, OnAttackedModifierHook, TooltipModifierHook {
  private static final Component LIFE_STEAL = TConstruct.makeTranslation("modifier", "necrotic.lifesteal");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.ON_ATTACKED, ModifierHooks.TOOLTIP);
  }

  @Override
  public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
    if (damage > 0 && !context.getTarget().getType().is(TinkerTags.EntityTypes.NECROTIC_BLACKLIST)) {
      // heals a percentage of damage dealt, using same rate as reinforced
      float percent = 0.05f * modifier.getEffectiveLevel();
      if (percent > 0) {
        LivingEntity attacker = context.getAttacker();
        attacker.heal(percent * damage);
        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);
        // take a bit of extra damage to heal
        ToolDamageUtil.damageAnimated(tool, modifier.getLevel(), attacker, context.getSlotType());
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
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null && attacker != null && !target.getType().is(TinkerTags.EntityTypes.NECROTIC_BLACKLIST)) {
      float percent = 0.05f * modifier.getEffectiveLevel();
      if (percent > 0) {
        // we don't actually know how much damage will be dealt, so just grab the projectile power, scaled by velocity if needed
        // to prevent healing too much, limit by the target's health. Will let you life steal ignoring armor, but eh, only so much we can do efficiently
        float power = ProjectileWithPower.getDamage(projectile);
        if (power > 0) {
          attacker.heal(percent * Math.min(target.getHealth(), power));
          attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
      }
    }
    return false;
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // works like vanilla, if multiple pieces have it we get the highest effect
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      // 15% chance of working, no shield doubling as shields prevent the damage, better than any healing
      LivingEntity defender = context.getEntity();
      if (RANDOM.nextFloat() < 0.15f) {
        // heals 25% of damage taken, but slowly over time
        int heal = (int)(0.25f * modifier.getEffectiveLevel() * amount);
        if (heal > 0) {
          // regen restores 1 health every 50 ticks
          defender.addEffect(new MobEffectInstance(MobEffects.REGENERATION, heal * 50));
          defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);

          // extra damage for running based on level
          ToolDamageUtil.damageAnimated(tool, modifier.getLevel(), defender, slotType);
        }
      }
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float lifesteal = 0.05f * modifier.getLevel();
    if (lifesteal > 0) {
      tooltip.add(applyStyle(Component.literal(Util.PERCENT_FORMAT.format(lifesteal) + " ").append(LIFE_STEAL)));
    }
  }
}
