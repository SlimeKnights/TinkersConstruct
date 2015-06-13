package tconstruct.tools;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.client.MaterialRenderInfo;
import tconstruct.library.tinkering.Material;
import tconstruct.library.materials.ToolMaterialStats;
import tconstruct.library.traits.ITrait;
import tconstruct.library.traits.StoneboundTrait;

public final class TinkerMaterials {

  public static final Material wood;
  public static final Material stone;
  public static final Material netherrack;

  public static final ITrait stonebound;

  private TinkerMaterials() {
  }

  static {
    wood = new Material("Wood", 0xffaa00, 0xffaa00, 0xffcc22, EnumChatFormatting.YELLOW);
    stone = new Material("Stone", 0x555555, EnumChatFormatting.DARK_GRAY);
    netherrack =
        new Material("Netherrack", new MaterialRenderInfo.BlockTexture(Blocks.netherrack),
                     EnumChatFormatting.DARK_RED);

    stonebound = new StoneboundTrait();
  }

  public static void registerMaterials() {
    TinkerRegistry.addMaterial(wood);
    TinkerRegistry.addMaterial(stone, stonebound);
    TinkerRegistry.addMaterial(netherrack, stonebound);
  }

  public static void registerToolMaterials() {
    TinkerRegistry.addMaterialStats(wood, new ToolMaterialStats(1, 97, 1.0f, 3.5f, 0.9f));
    TinkerRegistry.addMaterialStats(stone, new ToolMaterialStats(1, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(netherrack, new ToolMaterialStats(2, 200, 0.5f, 5.0f, 1.4f));
  }

  public static void registerBowMaterials() {

  }

  public static void registerProjectileMaterials() {

  }
}
