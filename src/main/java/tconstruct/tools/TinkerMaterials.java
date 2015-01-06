package tconstruct.tools;

import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tools.Material;
import tconstruct.library.tools.materials.IMaterialStats;
import tconstruct.library.tools.materials.ToolMaterialStats;

public final class TinkerMaterials {

  private TinkerMaterials() {
  }

  static {
    wood = new Material("Wood", 0, 0xabcdef, EnumChatFormatting.YELLOW);
    stone = new Material("Stone", 1);
  }

  public static void registerMaterials() {
    TinkerRegistry.addMaterial(wood);
    TinkerRegistry.addMaterial(stone);
  }

  public static void registerToolMaterials() {
    TinkerRegistry.addMaterialStats(wood, new ToolMaterialStats(1, 97, 1.0f, 3.5f, 0.9f));
    TinkerRegistry.addMaterialStats(stone, new ToolMaterialStats(1, 120, 0.2f, 4.0f, 1.0f));
  }

  public static void registerBowMaterials() {

  }

  public static void registerProjectileMaterials() {

  }

  public static final Material wood;
  public static final Material stone;
}
