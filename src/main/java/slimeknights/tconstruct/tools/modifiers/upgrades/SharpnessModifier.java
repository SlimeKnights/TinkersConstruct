package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

public class SharpnessModifier extends IncrementalModifier {
  public SharpnessModifier() {
    super(0xEAE5DE);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    // displays special names for levels of sharpness
    if (level <= 5) {
      return new TranslationTextComponent(getTranslationKey() + "." + level)
        .modifyStyle(style -> style.setColor(Color.fromInt(getColor())));
    }
    return super.getDisplayName(level);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    // vanilla give +1, 1.5, 2, 2.5, 3, but that is stupidly low
    // we instead do +1, 2,  3, 4,   5
    builder.addAttackDamage(getScaledLevel(persistentData, level));
  }
}
