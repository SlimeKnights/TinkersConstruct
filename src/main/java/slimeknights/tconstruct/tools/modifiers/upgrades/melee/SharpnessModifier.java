package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class SharpnessModifier extends IncrementalModifier {
  @Override
  public Component getDisplayName(int level) {
    // displays special names for levels of sharpness
    if (level <= 5) {
      return new TranslatableComponent(getTranslationKey() + "." + level)
        .withStyle(style -> style.withColor(getTextColor()));
    }
    return super.getDisplayName(level);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    // vanilla give +1, 1.5, 2, 2.5, 3, but that is stupidly low
    // we instead do +1, 2,  3, 4,   5
    ToolStats.ATTACK_DAMAGE.add(builder, getScaledLevel(context, level));
  }
}
