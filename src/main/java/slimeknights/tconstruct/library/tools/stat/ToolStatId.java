package slimeknights.tconstruct.library.tools.stat;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.utils.IdParser;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class ToolStatId extends ResourceLocation {
  public static final IdParser<ToolStatId> PARSER = new IdParser<>(ToolStatId::new, "Tool Stat");

  public ToolStatId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public ToolStatId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  public ToolStatId(String value) {
    super(value);
  }

  /**
   * Tries to create a tool stat ID from the given string, for NBT parsing
   * @param string  String
   * @return  Tool stat ID, or null of invalid
   */
  @Nullable
  public static ToolStatId tryCreate(String string) {
    return PARSER.tryParse(string);
  }
}
