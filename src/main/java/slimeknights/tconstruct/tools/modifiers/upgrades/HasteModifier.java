package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class HasteModifier extends IncrementalModifier {
  public HasteModifier() {
    super(0xAA0F01);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    // displays special names for levels of haste
    if (level <= 5) {
      return new TranslationTextComponent(getTranslationKey() + "." + level)
        .modifyStyle(style -> style.setColor(Color.fromInt(getColor())));
    }
    return super.getDisplayName(level);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    float scaledLevel = getScaledLevel(persistentData, level);
    // currently gives +5 speed per level
    // for comparison, vanilla gives +2, 5, 10, 17, 26 for efficiency I to V
    // 5 per level gives us          +5, 10, 15, 20, 25 for 5 levels
    builder.addMiningSpeed(scaledLevel * 5f);
    // means 10 levels is 1 second off attack time, number just from 1.12
    builder.addAttackSpeed(scaledLevel * 0.1f);
  }
}
