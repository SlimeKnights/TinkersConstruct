package slimeknights.tconstruct.library.modifiers;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.ResourceId;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety in modifier JSON.
 */
public class ModifierId extends ResourceId {
  public static final IdParser<ModifierId> PARSER = new IdParser<>(ModifierId::new, "Modifier");

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
