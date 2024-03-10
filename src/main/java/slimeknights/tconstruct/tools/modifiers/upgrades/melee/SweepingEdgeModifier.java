package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class SweepingEdgeModifier extends IncrementalModifier implements TooltipModifierHook {
  private static final Component SWEEPING_BONUS = TConstruct.makeTranslation("modifier", "sweeping_edge.attack_damage");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.TOOLTIP);
  }

  /** Gets the damage dealt by this tool, boosted properly by sweeping */
  public float getSweepingDamage(IToolStackView toolStack, float baseDamage) {
    int level = toolStack.getModifierLevel(this);
    float sweepingDamage = 1;
    if (level > 4) {
      sweepingDamage = baseDamage;
    } else if (level > 0) {
      // gives 25% per level, cap at base damage
      sweepingDamage = Math.min(baseDamage, getEffectiveLevel(toolStack, level) * 0.25f * baseDamage + 1);
    }
    return sweepingDamage;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float amount = modifier.getEffectiveLevel(tool) * 0.25f;
    tooltip.add(applyStyle(new TextComponent(Util.PERCENT_FORMAT.format(amount)).append(" ").append(SWEEPING_BONUS)));
  }
}
