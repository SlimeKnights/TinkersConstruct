package slimeknights.tconstruct.library.modifiers;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.ResourceId;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety in modifier JSON.
 */
public class ModifierId extends ResourceId {
  public static final IdParser<ModifierId> PARSER = new IdParser<>(ModifierId::new, "Modifier");
  /** ID of the default modifier. Used in a few contexts to indicate "no modifier" instead of using null. */
  public static final ModifierId EMPTY = new ModifierId(TConstruct.MOD_ID, "empty");
  /**
   * Context key used in {@link slimeknights.tconstruct.library.client.modifiers.ModifierModelMapManager}.
   * Note when the modifier itself is the JSON file (such as modifier JSON), {@link ContextKey#ID} will be used instead.
   */
  public static final ContextKey<ModifierId> CONTEXT_KEY = new ContextKey<>("modifier");

  public ModifierId(String resourceName) {
    super(resourceName);
  }

  public ModifierId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public ModifierId(ResourceLocation location) {
    super(location);
  }

  private ModifierId(String namespace, String path, @Nullable Dummy pDummy) {
    super(namespace, path, pDummy);
  }

  /** {@return Modifier ID, or null if invalid} */
  @Nullable
  public static ModifierId tryParse(String string) {
    return tryParse(string, (namespace, path) -> new ModifierId(namespace, path, null));
  }

  /** {@return Modifier ID, or null if invalid} */
  @Nullable
  public static ModifierId tryBuild(String namespace, String path) {
    return tryBuild(namespace, path, (n, p) -> new ModifierId(namespace, path, null));
  }
}
