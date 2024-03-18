package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.gson.ResourceLocationSerializer;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

/**
 * This is a copy of resource location with a couple extra helpers
 */
public class Pattern extends ResourceLocation {
  public static final IdParser<Pattern> PARSER = new IdParser<>(Pattern::new, "Pattern");
  public static final ResourceLocationSerializer<Pattern> SERIALIZER = new ResourceLocationSerializer<>(Pattern::new, "minecraft");

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
   * Gets the display name for this pattern
   * @return  Display name
   */
  public Component getDisplayName() {
    return Component.translatable(Util.makeTranslationKey("pattern", this));
  }

  /**
   * Gets the texture for this pattern for rendering
   * @return  Pattern texture
   */
  public ResourceLocation getTexture() {
    return new ResourceLocation(getNamespace(), "gui/tinker_pattern/" + getPath());
  }

  /**
   * Tries to create a pattern from the given string, for NBT parsing
   * @param string  String
   * @return  Tool stat ID, or null of invalid
   */
  @Nullable
  public static Pattern tryParse(String string) {
    return PARSER.tryParse(string);
  }
}
