package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

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
  public void addToolStats(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ToolStatsModifierBuilder builder) {
    // vanilla give +1, 1.5, 2, 2.5, 3
    // to make up for that 0.5 on the first level, we give a small bonus after the first level
    float scaledLevel = getScaledLevel(persistentData, level);
    if (scaledLevel >= 1) {
      scaledLevel += 1;
    }
    builder.addAttackDamage(scaledLevel * 0.5f);
  }
}
