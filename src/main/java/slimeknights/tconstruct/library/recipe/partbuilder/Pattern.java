package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.ResourceLocationSerializer;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.Util;

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
    return new TranslatableComponent(Util.makeTranslationKey("pattern", this));
  }

  /**
   * Gets the texture for this pattern for rendering
   * @return  Pattern texture
   */
  public ResourceLocation getTexture() {
    return new ResourceLocation(getNamespace(), "gui/tinker_pattern/" + getPath());
  }
}
