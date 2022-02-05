package slimeknights.tconstruct.library.materials.stats;

import lombok.EqualsAndHashCode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tconstruct.library.utils.Util;

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
  protected static Component makeTooltip(ResourceLocation name) {
    return new TranslatableComponent(makeTooltipKey(name));
  }
}
