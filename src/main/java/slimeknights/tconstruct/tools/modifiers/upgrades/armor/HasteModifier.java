package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.modules.unserializable.ArmorStatModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class HasteModifier extends IncrementalModifier implements ToolStatsModifierHook, TooltipModifierHook {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "fake_attribute.mining_speed");
  /** Player modifier data key for haste, represents an additive percentage boost on mining speed. */
  public static final TinkerDataKey<Float> HASTE = TConstruct.createKey("haste");

  private static final ModifierLevelDisplay NAME = new UniqueForLevels(5);

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new ArmorStatModule(HASTE, 0.1f, false));
    hookBuilder.addHook(this, TinkerHooks.TOOL_STATS, TinkerHooks.TOOLTIP);
  }

  @Override
  public Component getDisplayName(int level) {
    return NAME.nameForLevel(this, level);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    ToolStats.MINING_SPEED.add(builder, 4 * modifier.getEffectiveLevel(context));
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      double boost = 0.1 * modifier.getEffectiveLevel(tool);
      if (boost != 0) {
        tooltip.add(applyStyle(Component.literal(Util.PERCENT_BOOST_FORMAT.format(boost)).append(" ").append(MINING_SPEED)));
      }
    }
  }
}
