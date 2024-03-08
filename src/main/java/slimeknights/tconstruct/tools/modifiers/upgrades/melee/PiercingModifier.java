package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

public class PiercingModifier extends IncrementalModifier implements ToolStatsModifierHook, VolatileDataModifierHook, MeleeHitModifierHook, TooltipModifierHook {
  private static final ResourceLocation PIERCING_DEBUFF = TConstruct.getResource("piercing_debuff");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.TOOL_STATS, TinkerHooks.VOLATILE_DATA, TinkerHooks.MELEE_HIT, TinkerHooks.TOOLTIP);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    float toRemove = 0.5f * modifier.getEffectiveLevel(context);
    float baseDamage = context.getBaseStats().get(ToolStats.ATTACK_DAMAGE);
    if (baseDamage < toRemove) {
      volatileData.putFloat(PIERCING_DEBUFF, toRemove - baseDamage);
    }
  }

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    float toRemove = 0.5f * modifier.getEffectiveLevel(context) - context.getVolatileData().getFloat(PIERCING_DEBUFF);
    ToolStats.ATTACK_DAMAGE.add(builder, -toRemove);
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    // deals 0.5 pierce damage per level, scaled, half of sharpness
    DamageSource source;
    Player player = context.getPlayerAttacker();
    if (player != null) {
      source = DamageSource.playerAttack(player);
    } else {
      source = DamageSource.mobAttack(context.getAttacker());
    }
    source.bypassArmor();
    float secondaryDamage = (modifier.getEffectiveLevel(tool) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE) - tool.getVolatileData().getFloat(PIERCING_DEBUFF)) * context.getCooldown();
    if (context.isCritical()) {
      secondaryDamage *= 1.5f;
    }
    ToolAttackUtil.attackEntitySecondary(source, secondaryDamage, context.getTarget(), context.getLivingTarget(), true);
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    TooltipModifierHook.addDamageBoost(tool, this, modifier.getEffectiveLevel(tool) - tool.getVolatileData().getFloat(PIERCING_DEBUFF), tooltip);
  }
}
