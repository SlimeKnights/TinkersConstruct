package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class SwiftstrikeModifier extends IncrementalModifier {
  @Override
  public Component getDisplayName(int level) {
    // displays special names for levels of haste
    if (level <= 5) {
      return applyStyle(new TranslatableComponent(getTranslationKey() + "." + level));
    }
    return super.getDisplayName(level);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    float scaledLevel = getScaledLevel(context, level);
    // maxes at 125%, number chosen to be comparable DPS to quartz
    ToolStats.ATTACK_SPEED.multiply(builder, 1 + scaledLevel * 0.05f);
  }
}
