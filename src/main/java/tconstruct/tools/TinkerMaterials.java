package tconstruct.tools;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.client.MaterialRenderInfo;
import tconstruct.library.client.texture.ExtraUtilityTexture;
import tconstruct.library.materials.Material;
import tconstruct.library.materials.ToolMaterialStats;
import tconstruct.library.traits.ITrait;
import tconstruct.library.traits.StoneboundTrait;

public final class TinkerMaterials {

  public static final Material wood;
  public static final Material stone;
  public static final Material flint;
  public static final Material netherrack;

  public static final Material xu;

  public static final ITrait stonebound;

  private TinkerMaterials() {
  }

  static {
    wood = new Material("wood", EnumChatFormatting.YELLOW);
    stone = new Material("stone", EnumChatFormatting.GRAY);
    flint = new Material("flint", EnumChatFormatting.DARK_GRAY);
    netherrack = new Material("netherrack", EnumChatFormatting.DARK_RED);

    xu = new Material("unstable", EnumChatFormatting.WHITE);

    stonebound = new StoneboundTrait();
  }

  @SideOnly(Side.CLIENT)
  public static void registerMaterialRendering() {
    //wood.setRenderInfo(new MaterialRenderInfo.Default(0xffaa00, 0xffaa00, 0xffcc22), EnumChatFormatting.YELLOW);
    wood.setRenderInfo(new MaterialRenderInfo.MultiColor(0xff0000, 0x00ff00, 0x0000ff).setTextureSuffix("contrast"));
    //stone.setRenderInfo(new MaterialRenderInfo.Default(0x555555, 0x555555, 0xffffff), EnumChatFormatting.YELLOW);
    stone.setRenderInfo(0x555555);
    flint.setRenderInfo(new MaterialRenderInfo.Default(0xffffff)
                            .setTextureSuffix("contrast"));
    netherrack.setRenderInfo(new MaterialRenderInfo.BlockTexture(Blocks.netherrack));

    xu.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() {
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new ExtraUtilityTexture(baseTexture, location);
      }
    });
  }

  public static void registerMaterials() {
    TinkerRegistry.addMaterial(wood);
    TinkerRegistry.addMaterial(stone, stonebound);
    TinkerRegistry.addMaterial(flint);
    TinkerRegistry.addMaterial(netherrack, stonebound);

    TinkerRegistry.addMaterial(xu);
  }

  public static void registerToolMaterials() {
    TinkerRegistry.addMaterialStats(wood, new ToolMaterialStats(1, 97, 1.0f, 3.5f, 0.9f));
    TinkerRegistry.addMaterialStats(stone, new ToolMaterialStats(1, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(flint, new ToolMaterialStats(1, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(netherrack, new ToolMaterialStats(2, 200, 0.5f, 5.0f, 1.4f));
    TinkerRegistry.addMaterialStats(xu, new ToolMaterialStats(2, 200, 0.5f, 5.0f, 1.4f));
  }

  public static void registerBowMaterials() {

  }

  public static void registerProjectileMaterials() {

  }
}
