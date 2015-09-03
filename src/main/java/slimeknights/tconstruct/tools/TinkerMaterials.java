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
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.texture.ExtraUtilityTexture;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.tools.modifiers.TraitCheap;
import slimeknights.tconstruct.tools.modifiers.TraitEcological;
import slimeknights.tconstruct.tools.modifiers.TraitStonebound;

import static slimeknights.tconstruct.library.tools.ToolPart.COST_Fragment;
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

  // Mod support Metals
  public static final Material copper     = mat("copper", EnumChatFormatting.GOLD);

  // Alloys
  public static final Material pigiron    = mat("pigiron", EnumChatFormatting.RED);
  public static final Material bronze     = mat("bronze", EnumChatFormatting.GOLD);


  public static final Material xu;

  public static final TraitEcological ecological = new TraitEcological();
  public static final TraitCheap cheap = new TraitCheap();
  public static final TraitStonebound stonebound = new TraitStonebound();

  private static Material mat(String name, EnumChatFormatting color) {
    Material mat = new Material(name, color);
    materials.add(mat);
    return mat;
  }

  private TinkerMaterials() {
  }

  static {
    xu = new Material("unstable", EnumChatFormatting.WHITE);
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

    bone.setRenderInfo(0xede6bf);
    paper.setRenderInfo(0xffffff); // paper has custom textures
    sponge.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/sponge"));
    slime.setRenderInfo(0x82c873);
    blueslime.setRenderInfo(0x74c8c7);

    // Metals
    iron.setRenderInfo(0xffffff);

    xu.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() {
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new ExtraUtilityTexture(baseTexture, location);
      }
    });
  }

  public static void setupMaterials() {
    // natural resources/blocks
    wood.setCraftable(true);
    wood.addItem("stickWood", 1, COST_Shard);
    wood.addItem("plankWood", 1, COST_Ingot);
    wood.addItem("logWood", 1, COST_Ingot * 4);
    wood.setRepresentativeItem(new ItemStack(Items.stick));
    wood.addTrait(ecological);

    stone.setCraftable(true);
    stone.addItem("cobblestone", 1, COST_Ingot);
    stone.addItem("stone", 1, COST_Ingot);
    stone.setRepresentativeItem(new ItemStack(Blocks.cobblestone));
    stone.addTrait(cheap);

    flint.setCraftable(true);
    flint.addItem(Items.flint, 1, COST_Ingot);
    flint.setRepresentativeItem(new ItemStack(Items.flint));

    cactus.setCraftable(true);
    cactus.addItem(Blocks.cactus, COST_Ingot);
    cactus.setRepresentativeItem(new ItemStack(Blocks.cactus));

    obsidian.setCraftable(true);
    obsidian.setFluid(FluidRegistry.WATER).setCastable(true); // todo
    obsidian.addItem(Blocks.obsidian, COST_Ingot);
    obsidian.setRepresentativeItem(new ItemStack(Blocks.obsidian));

    prismarine.setCraftable(true);
    prismarine.addItem(Items.prismarine_shard, 1, COST_Fragment);
    prismarine.addItem(Blocks.prismarine, COST_Ingot);
    prismarine.setRepresentativeItem(Items.prismarine_shard);

    netherrack.setCraftable(true);
    netherrack.addItem(Blocks.netherrack, COST_Ingot);
    netherrack.setRepresentativeItem(Blocks.netherrack);

    endstone.setCraftable(true);
    endstone.addItem(Blocks.end_stone, COST_Ingot);
    endstone.setRepresentativeItem(Blocks.end_stone);

    // item/special resources
    bone.setCraftable(true);
    bone.addItem(Items.bone, 1, COST_Ingot);
    bone.setRepresentativeItem(Items.bone);

    paper.setCraftable(true);
    paper.addItem(Items.paper, 1, COST_Fragment);
    paper.setRepresentativeItem(Items.paper);

    sponge.setCraftable(true);
    sponge.addItem(Blocks.sponge, COST_Ingot);
    sponge.setRepresentativeItem(Blocks.sponge);

    slime.setCraftable(true);
    // todo: slime

    blueslime.setCraftable(true);
    // todo: blueslime

    // Metals
    iron.addItem(Items.iron_ingot);
    iron.setRepresentativeItem(Items.iron_ingot);
    // todo: remaining metals


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
    // Stats:                                                   Durability, speed, attack, handle, extra, harvestlevel
    // natural resources/blocks
    TinkerRegistry.addMaterialStats(wood,       new ToolMaterialStats( 137, 3.00f, 0.20f, 0.80f, 0.60f, STONE));
    TinkerRegistry.addMaterialStats(stone,      new ToolMaterialStats( 195, 4.00f, 0.50f, 0.05f, 0.18f, STONE));
    TinkerRegistry.addMaterialStats(flint,      new ToolMaterialStats( 285, 5.70f, 1.10f, 0.20f, 0.19f, IRON));
    TinkerRegistry.addMaterialStats(cactus,     new ToolMaterialStats( 229, 4.50f, 2.00f, 0.25f, 0.43f, IRON));
    TinkerRegistry.addMaterialStats(obsidian,   new ToolMaterialStats(  99, 7.07f, 2.80f, 0.02f, 0.24f, COBALT));
    TinkerRegistry.addMaterialStats(prismarine, new ToolMaterialStats( 512, 5.50f, 5.00f, 0.18f, 0.84f, IRON));
    TinkerRegistry.addMaterialStats(netherrack, new ToolMaterialStats( 222, 4.89f, 1.00f, 0.10f, 0.27f, IRON));
    TinkerRegistry.addMaterialStats(endstone,   new ToolMaterialStats( 333, 3.33f, 1.23f, 0.33f, 0.33f, OBSIDIAN));

    // item/special resources
    TinkerRegistry.addMaterialStats(bone,      new ToolMaterialStats( 235, 5.09f, 1.00f, 0.70f, 0.56f, IRON));
    TinkerRegistry.addMaterialStats(paper,     new ToolMaterialStats(  42, 0.50f, 0.05f, 0.01f, 0.70f, STONE));
    TinkerRegistry.addMaterialStats(sponge,    new ToolMaterialStats( 350, 3.00f, 0.00f, 0.01f, 0.01f, STONE));
    TinkerRegistry.addMaterialStats(slime,     new ToolMaterialStats(1000, 4.00f, 1.80f, 0.50f, 0.10f, STONE));
    TinkerRegistry.addMaterialStats(blueslime, new ToolMaterialStats( 250, 4.00f, 1.80f, 0.30f, 1.00f, STONE));

    TinkerRegistry.addMaterialStats(iron,       new ToolMaterialStats( 353, 6.00f, 1.80f, 0.50f, 0.60f, IRON));

    //TinkerRegistry.addMaterialStats(xu,         new ToolMaterialStats(97, 1.00f, 1.00f, 0.10f, 0.20f, DIAMOND));
  }

  public static void registerBowMaterials() {

  }

  public static void registerProjectileMaterials() {

  }
}
