package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;

/**
 * This is a copy of resource location with a couple extra helpers
 */
public class Pattern extends ResourceLocation {
  public Pattern(String resourceName) {
    super(resourceName);
  }

  public Pattern(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public Pattern(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  /**
   * Creates a new modifier ID from the given string
   * @param string  String
   * @return  Material ID, or null if invalid
   */
  @Nullable
  public static ModifierId tryCreate(String string) {
    try {
      return new ModifierId(string);
    } catch (ResourceLocationException resourcelocationexception) {
      return null;
    }
  }

  /**
   * Gets the display name for this pattern
   * @return  Display name
   */
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(Util.makeTranslationKey("pattern", this));
  }

  /**
   * Gets the texture for this pattern for rendering
   * @return  Pattern texture
   */
  public ResourceLocation getTexture() {
    return new ResourceLocation(getNamespace(), "gui/tinker_pattern/" + getPath());
  }
}
