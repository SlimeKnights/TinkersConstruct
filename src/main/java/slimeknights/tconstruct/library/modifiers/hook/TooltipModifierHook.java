package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Hook for modifiers to add tooltip information
 */
public interface TooltipModifierHook {
  /**
   * Adds additional information from the modifier to the tooltip. Shown when holding shift on a tool, or in the stats area of the tinker station
   * @param tool         Tool instance
   * @param modifier        Tool level
   * @param player       Player holding this tool
   * @param tooltip      Tooltip
   * @param tooltipKey   Shows if the player is holding shift, control, or neither
   * @param tooltipFlag  Flag determining tooltip type
   */
  void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag);

  /** Merger that runs all hooks */
  record AllMerger(Collection<TooltipModifierHook> modules) implements TooltipModifierHook {
    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
      for (TooltipModifierHook module : modules) {
        module.addTooltip(tool, modifier, player, tooltip, tooltipKey, tooltipFlag);
      }
    }
  }
}
