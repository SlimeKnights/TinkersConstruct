package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class MomentumModifier extends Modifier implements ProjectileLaunchModifierHook, ConditionalStatModifierHook, BlockBreakModifierHook, BreakSpeedModifierHook, SlingLaunchModifierHook, TooltipModifierHook {
  private static final Component SPEED = TConstruct.makeTranslation("modifier", "momentum.speed");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_THROWN, ModifierHooks.BLOCK_BREAK, ModifierHooks.BREAK_SPEED, ModifierHooks.SLING_LAUNCH, ModifierHooks.TOOLTIP);
  }

  @Override
  public int getPriority() {
    // run this last as we boost original speed, adds to existing boosts
    return 75;
  }

  /** Gets the bonus for the modifier */
  private static float getBonus(LivingEntity living, ToolType type, ModifierEntry modifier) {
    return modifier.getEffectiveLevel() * (TinkerEffect.getLevel(living, TinkerModifiers.momentumEffect.get(type)));
  }

  /** Applies the effect to the target */
  private static void applyEffect(LivingEntity living, ToolType type, int duration, int maxLevel) {
    TinkerEffect effect = TinkerModifiers.momentumEffect.get(type);
    effect.apply(living, duration, Math.min(maxLevel, TinkerEffect.getAmplifier(living, effect) + 1), true);
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      // 25% boost per level at max
      event.setNewSpeed(event.getNewSpeed() * (1 + getBonus(event.getEntity(), ToolType.HARVEST, modifier) / 40f));
    }
  }

  @Override
  public float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
    if (context.isEffective()) {
      // 25% boost per level at max
      speed *= 1 + getBonus(context.player(), ToolType.HARVEST, modifier) / 40f;
    }
    return speed;
  }

  @Override
  public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (context.canHarvest() && context.isEffective() && !context.isAOE()) {
      // grant the effect for 5 seconds, though grant a longer effect if the blocks hardness is particularly high compared to our mining speed
      int duration = Math.max(5*20, (int) (2.5f * 20f * context.getState().getDestroySpeed(context.getWorld(), context.getPos()) / tool.getStats().get(ToolStats.MINING_SPEED)));
      // 10 blocks gets you to max, effect is stronger at higher levels
      applyEffect(context.getLiving(), ToolType.HARVEST, duration, 9);
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    if (primary && (arrow == null || arrow.isCritArrow())) {
      // 10 arrows gets you to max
      applyEffect(shooter, ToolType.RANGED, 10*20, 9);
    }
  }

  @Override
  public void afterSlingLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
    applyEffect(holder, ToolType.RANGED, 10*20, 9);
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.DRAW_SPEED) {
      // +25% at max level
      return baseValue * (1 + getBonus(living, ToolType.RANGED, modifier) / 40f);
    }
    return baseValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    ToolType type = ToolType.from(tool.getItem(), ToolType.HARVEST, ToolType.RANGED);
    if (type != null) {
      float bonus;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getBonus(player, type, modifier) / 40f;
      } else {
        // 25% per level for both of them
        bonus = modifier.getEffectiveLevel() * 0.25f;
      }
      if (bonus > 0) {
        TooltipModifierHook.addPercentBoost(this, SPEED, bonus, tooltip);
      }
    }
  }
}
