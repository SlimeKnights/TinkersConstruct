package slimeknights.tconstruct.library.modifiers.modules.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;

import javax.annotation.Nullable;
import java.util.List;

/** Common tooltip logic for conditional stat modules */
public interface ConditionalStatTooltip extends TooltipModifierHook, ConditionalModule<IToolStackView> {
  /** Gets the holder condition for this module */
  IJsonPredicate<LivingEntity> holder();
  /** Gets the stat for this tooltip */
  INumericToolStat<?> stat();
  /** If true, display as percent. If false, display as boost */
  boolean percent();

  /** Computes the value to display in the tooltip */
  float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player);

  /** Checks if the tool condition matches */
  default boolean matchesTool(IToolStackView tool, ModifierEntry entry) {
    return condition().matches(tool, entry);
  }

  @Override
  default void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // if holding shift, or we have no attacker condition, then we don't need the player to show the tooltip
    INumericToolStat<?> stat = stat();
    IJsonPredicate<LivingEntity> holder = holder();
    if (stat.supports(tool.getItem()) && matchesTool(tool, entry) && TinkerPredicate.matchesInTooltip(holder, player, tooltipKey)) {
      // it's hard to display a good tooltip value without knowing the details of the formula, best we can do is guess based on the boolean
      // if this is inaccurate, just add this module without the tooltip hook to ignore
      Modifier modifier = entry.getModifier();
      Component statName = TooltipModifierHook.statName(modifier, stat);
      // subtracting 1 will cancel out the base value or the 100%, based on the type
      // null player when not pressing shift, so we can see the max value in the table instead of the current value. Shift lets you see max
      float value = computeTooltipValue(tool, entry, tooltipKey == TooltipKey.SHIFT ? player : null) - 1;
      if (value != 0) {
        if (percent()) {
          TooltipModifierHook.addPercentBoost(modifier, statName, value, tooltip);
        } else {
          TooltipModifierHook.addFlatBoost(modifier, statName, value, tooltip);
        }
      }
    }
  }
}
