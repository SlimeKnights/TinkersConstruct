package slimeknights.tconstruct.library.tools.stat;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.ResourceId;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety in tool stat JSON.
 */
public class ToolStatId extends ResourceId {
  public static final IdParser<ToolStatId> PARSER = new IdParser<>(ToolStatId::new, "Tool Stat");

  public ToolStatId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public ToolStatId(ResourceLocation location) {
    super(location);
  }

  public ToolStatId(String value) {
    super(value);
  }

  private ToolStatId(String namespace, String path, @Nullable Dummy pDummy) {
    super(namespace, path, pDummy);
  }

  /** {@return Tool stat ID, or null if invalid} */
  @Nullable
  public static ToolStatId tryParse(String string) {
    return tryParse(string, (namespace, path) -> new ToolStatId(namespace, path, null));
  }

  /** {@return Tool stat ID, or null if invalid} */
  @Nullable
  public static ToolStatId tryBuild(String namespace, String path) {
    return tryBuild(namespace, path, (n, p) -> new ToolStatId(namespace, path, null));
  }
}
