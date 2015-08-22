package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.texture.AnimatedColoredTexture;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.texture.ExtraUtilityTexture;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.traits.StoneboundTrait;
import static slimeknights.tconstruct.library.tools.ToolPart.COST_Ingot;
import static slimeknights.tconstruct.library.tools.ToolPart.COST_Shard;
import static slimeknights.tconstruct.library.utils.HarvestLevels.*;

public final class TinkerMaterials {

  public static final List<Material> materials = Lists.newArrayList();

  // natural resources/blocks
  public static final Material wood       = mat("wood", EnumChatFormatting.YELLOW);
  public static final Material stone      = mat("stone", EnumChatFormatting.GRAY);
  public static final Material flint      = mat("flint", EnumChatFormatting.DARK_GRAY);
  public static final Material cactus     = mat("cactus", EnumChatFormatting.DARK_GREEN);
  public static final Material obsidian   = mat("obsidian", EnumChatFormatting.LIGHT_PURPLE);
  public static final Material prismarine = mat("prismarine", EnumChatFormatting.DARK_AQUA);
  public static final Material netherrack = mat("netherrack", EnumChatFormatting.DARK_RED);
  public static final Material endstone   = mat("endstone", EnumChatFormatting.GOLD);

  // item/special resources
  public static final Material bone       = mat("bone", EnumChatFormatting.WHITE);
  public static final Material paper      = mat("paper", EnumChatFormatting.WHITE);
  public static final Material sponge     = mat("sponge", EnumChatFormatting.YELLOW);
  public static final Material slime      = mat("slime", EnumChatFormatting.GREEN);
  public static final Material blueslime  = mat("blueslime", EnumChatFormatting.BLUE);

  // Metals
  public static final Material iron       = mat("iron", EnumChatFormatting.GRAY);
  public static final Material ardite     = mat("ardite", EnumChatFormatting.RED);
  public static final Material cobalt     = mat("cobalt", EnumChatFormatting.DARK_BLUE);
  public static final Material manyullyn  = mat("manyullyn", EnumChatFormatting.DARK_PURPLE);



  public static final Material xu;

  public static final ITrait stonebound;

  private static Material mat(String name, EnumChatFormatting color) {
    Material mat = new Material(name, color);
    materials.add(mat);
    return mat;
  }

  private TinkerMaterials() {
  }

  static {
    xu = new Material("unstable", EnumChatFormatting.WHITE);

    stonebound = new StoneboundTrait();
  }

  @SideOnly(Side.CLIENT)
  public static void registerMaterialRendering() {
    wood.setRenderInfo(new MaterialRenderInfo.MultiColor(0x6e572a, 0x745f38, 0x8e671d));
    stone.setRenderInfo(0x898989);
    flint.setRenderInfo(0xffffff).setTextureSuffix("contrast");
    cactus.setRenderInfo(0x006d0a); // cactus has custom textures
    obsidian.setRenderInfo(new MaterialRenderInfo.MultiColor(0x71589c, 0x8f60d4, 0x8c53df).setTextureSuffix("contrast")); // increase each color by 20 to get thaumium
    prismarine.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/prismarine_bricks"));
    netherrack.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/netherrack"));
    //endstone.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/end_stone"));
    endstone.setRenderInfo(new MaterialRenderInfo.InverseMultiColor(0x5c6296, 0x3c4276, 0x3c4276));

    xu.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() {
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new ExtraUtilityTexture(baseTexture, location);
      }
    });
  }

  public static void setupMaterials() {
    wood.setCraftable(true);
    wood.addItem("stickWood", 1, COST_Shard);
    wood.addItem("plankWood", 1, COST_Ingot);
    wood.addItem("logWood", 1, COST_Ingot * 4);
    wood.setRepresentativeItem(new ItemStack(Items.stick));

    stone.setCraftable(true);
    stone.addItem("cobblestone", 1, COST_Ingot);
    stone.addItem("stone", 1, COST_Ingot);
    stone.setRepresentativeItem(new ItemStack(Blocks.cobblestone));

    flint.setCraftable(true);
    flint.addItem(Items.flint, 1, COST_Ingot);
    flint.setRepresentativeItem(new ItemStack(Items.flint));

    obsidian.setCraftable(true);
    obsidian.setFluid(FluidRegistry.WATER).setCastable(true);
    obsidian.addItem(Blocks.obsidian, COST_Ingot);
    obsidian.setRepresentativeItem(new ItemStack(Blocks.obsidian));

    prismarine.setCraftable(true);
    prismarine.addItem(Items.prismarine_shard, 1, COST_Shard);
    prismarine.setRepresentativeItem(new ItemStack(Items.prismarine_shard));

    netherrack.setCraftable(true);
    netherrack.addItem(Blocks.netherrack, COST_Ingot);
    netherrack.setRepresentativeItem(new ItemStack(Blocks.netherrack));

    endstone.setCraftable(true);
    endstone.addItem(Blocks.end_stone, COST_Ingot);
    endstone.setRepresentativeItem(new ItemStack(Blocks.end_stone));

    registerToolMaterials();
    registerBowMaterials();
    registerBowMaterials();
  }

  public static void registerMaterials() {
    for(Material material : materials) {
      TinkerRegistry.addMaterial(material);
    }

    TinkerRegistry.addMaterial(xu);


  }

  public static void registerToolMaterials() {
    TinkerRegistry.addMaterialStats(wood, new ToolMaterialStats(STONE, 97, 1.0f, 3.5f, 0.9f));
    TinkerRegistry.addMaterialStats(stone, new ToolMaterialStats(STONE, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(flint, new ToolMaterialStats(IRON, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(cactus, new ToolMaterialStats(IRON, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(obsidian, new ToolMaterialStats(COBALT, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(prismarine, new ToolMaterialStats(IRON, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(netherrack, new ToolMaterialStats(IRON, 200, 0.5f, 5.0f, 1.4f));
    TinkerRegistry.addMaterialStats(endstone, new ToolMaterialStats(OBSIDIAN, 120, 0.2f, 4.0f, 1.0f));
    TinkerRegistry.addMaterialStats(xu, new ToolMaterialStats(DIAMOND, 200, 0.5f, 5.0f, 1.4f));
  }

  public static void registerBowMaterials() {

  }

  public static void registerProjectileMaterials() {

  }
}
