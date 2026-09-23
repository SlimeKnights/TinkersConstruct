package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.ResourceId;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

/**
 * Resource location variant for pattern buttons in the part builder. Also used in a few spots for convenient icon loading.
 */
public class Pattern extends ResourceId {
  public static final IdParser<Pattern> PARSER = new IdParser<>(Pattern::new, "Pattern");

  public Pattern(String resourceName) {
    super(resourceName);
  }

  public Pattern(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public Pattern(ResourceLocation location) {
    super(location);
  }

  private Pattern(String namespace, String path, @Nullable Dummy pDummy) {
    super(namespace, path, pDummy);
  }

  /**
   * Gets the translation key for this pattern
   * @return  Translation key
   */
  public String getTranslationKey() {
    return Util.makeTranslationKey("pattern", this);
  }

  /**
   * Gets the display name for this pattern
   * @return  Display name
   */
  public Component getDisplayName() {
    return Component.translatable(getTranslationKey());
  }

  /**
   * Gets the texture for this pattern for rendering
   * @return  Pattern texture
   */
  public ResourceLocation getTexture() {
    return withPrefix("gui/tinker_pattern/");
  }

  /** Checks if this pattern matches the give item, typically a tool part. */
  public boolean matches(ItemLike item) {
    return this.equals(Loadables.ITEM.getKey(item.asItem()));
  }


  /** {@return Pattern ID, or null if invalid} */
  @Nullable
  public static Pattern tryParse(String string) {
    return tryParse(string, (namespace, path) -> new Pattern(namespace, path, null));
  }

  /** {@return Pattern ID, or null if invalid} */
  @Nullable
  public static Pattern tryBuild(String namespace, String path) {
    return tryBuild(namespace, path, (n, p) -> new Pattern(namespace, path, null));
  }

  /** Gets the pattern for the given item ID */
  public static Pattern fromItem(ItemLike item) {
    return new Pattern(Loadables.ITEM.getKey(item.asItem()));
  }
}
