package slimeknights.tconstruct.library.materials.stats;

import lombok.EqualsAndHashCode;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;

/**
 * A simple material class without stats.
 * This class is meant to be extended with custom stats added to it for your use.
 */
@EqualsAndHashCode
public abstract class BaseMaterialStats implements IMaterialStats {
  /**
   * Helper to make a translation key for the given name
   * @param name  name
   * @return  Text component
   */
  protected static String makeTooltipKey(ResourceLocation name) {
    return Util.makeTranslationKey("tool_stat", name);
  }

  /**
   * Helper to make a text component for the given name
   * @param name  name
   * @return  Text component
   */
  protected static ITextComponent makeTooltip(ResourceLocation name) {
    return new TranslationTextComponent(makeTooltipKey(name));
  }
}
