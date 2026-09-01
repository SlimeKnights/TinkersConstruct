package slimeknights.tconstruct.library.modifiers.modules.display;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/** Module to show a tool stat in the tooltip. */
public record StatTooltipModule<T>(IToolStat<T> stat, ModifierCondition<IToolStackView> condition) implements ModifierModule, TooltipModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<StatTooltipModule<?>>defaultHooks(ModifierHooks.TOOLTIP);
  /** Priority to use to show stat tooltips above all other tooltips. */
  public static final int STAT_TOOLTIP_PRIORITY = 1000;
  public static final RecordLoadable<StatTooltipModule<?>> LOADER = RecordLoadable.create(
    ToolStats.LOADER.requiredField("stat", StatTooltipModule::stat),
    ModifierCondition.TOOL_FIELD,
    StatTooltipModule::new);

  public StatTooltipModule(IToolStat<T> stat) {
    this(stat, ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<StatTooltipModule<?>> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Integer getPriority() {
    return STAT_TOOLTIP_PRIORITY;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, modifier)) {
      tooltip.add(stat.formatValue(tool.getStats().get(stat)));
    }
  }
}
