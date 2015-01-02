package tconstruct.tools;

import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.tools.materials.IMaterialStats;
import tconstruct.library.tools.materials.ToolMaterialStats;

public final class TinkerMaterials {
  private TinkerMaterials() {}

  static {
    wood = new ToolMaterial("Wood", 0xabcdef, EnumChatFormatting.YELLOW);
    stone = new ToolMaterial("Stone");
  }

  public static void registerToolMaterials()
  {
    ToolMaterial material;
    IMaterialStats toolStats;

    // Wood
    material = wood;
    toolStats = new ToolMaterialStats(1, 97, 1.0f, 3.5f, 1.0f);

    TinkerRegistry.addToolMaterial(material, toolStats);

    // Stone
    material = stone;
    toolStats = new ToolMaterialStats(1, 97, 1.0f, 3.5f, 1.0f);

    TinkerRegistry.addToolMaterial(material, toolStats);
  }

  public static void registerBowMaterials()
  {

  }

  public static void registerProjectileMaterials()
  {

  }

  public static final ToolMaterial wood;
  public static final ToolMaterial stone;
}
