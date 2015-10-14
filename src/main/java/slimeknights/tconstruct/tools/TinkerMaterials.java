package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.texture.ExtraUtilityTexture;
import slimeknights.tconstruct.library.client.texture.MetalColoredTexture;
import slimeknights.tconstruct.library.client.texture.MetalTextureTexture;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.modifiers.TraitAridiculous;
import slimeknights.tconstruct.tools.modifiers.TraitCheap;
import slimeknights.tconstruct.tools.modifiers.TraitCrude;
import slimeknights.tconstruct.tools.modifiers.TraitDuritos;
import slimeknights.tconstruct.tools.modifiers.TraitEcological;
import slimeknights.tconstruct.tools.modifiers.TraitInsatiable;
import slimeknights.tconstruct.tools.modifiers.TraitMomentum;
import slimeknights.tconstruct.tools.modifiers.TraitPetramor;
import slimeknights.tconstruct.tools.modifiers.TraitPrickly;
import slimeknights.tconstruct.tools.modifiers.TraitSlimey;
import slimeknights.tconstruct.tools.modifiers.TraitSplintering;
import slimeknights.tconstruct.tools.modifiers.TraitSqueaky;
import slimeknights.tconstruct.tools.modifiers.TraitStonebound;

import static slimeknights.tconstruct.library.utils.HarvestLevels.COBALT;
import static slimeknights.tconstruct.library.utils.HarvestLevels.IRON;
import static slimeknights.tconstruct.library.utils.HarvestLevels.OBSIDIAN;
import static slimeknights.tconstruct.library.utils.HarvestLevels.STONE;

@Pulse(id = TinkerMaterials.PulseId, forced = true)
public final class TinkerMaterials {

  static final String PulseId = "TinkerTools";
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
  public static final Material copper     = mat("copper", EnumChatFormatting.GOLD);

  // Nether Materials
  public static final Material ardite     = mat("ardite", EnumChatFormatting.RED);
  public static final Material cobalt     = mat("cobalt", EnumChatFormatting.DARK_BLUE);
  public static final Material manyullyn  = mat("manyullyn", EnumChatFormatting.DARK_PURPLE);

  // Alloys
  public static final Material pigiron    = mat("pigiron", EnumChatFormatting.RED);
  public static final Material bronze     = mat("bronze", EnumChatFormatting.GOLD);


  public static final Material xu;

  public static final AbstractTrait aridiculous = new TraitAridiculous();
  public static final AbstractTrait cheap = new TraitCheap();
  public static final AbstractTrait crude = new TraitCrude();
  public static final AbstractTrait duritos = new TraitDuritos(); // yes you read that correctly
  public static final AbstractTrait ecological = new TraitEcological();
  public static final AbstractTrait insatiable = new TraitInsatiable();
  public static final AbstractTrait momentum = new TraitMomentum();
  public static final AbstractTrait petramor = new TraitPetramor();
  public static final AbstractTrait prickly = new TraitPrickly();
  public static final AbstractTrait slimeyGreen = new TraitSlimey(EntitySlime.class);
  public static final AbstractTrait slimeyBlue = new TraitSlimey(EntitySlime.class); // todo: blue slime
  public static final AbstractTrait splintering = new TraitSplintering();
  public static final AbstractTrait squeaky = new TraitSqueaky();
  public static final AbstractTrait stonebound = new TraitStonebound();

  private static Material mat(String name, EnumChatFormatting color) {
    Material mat = new Material(name, color);
    materials.add(mat);
    return mat;
  }
  
  static {
    xu = new Material("unstable", EnumChatFormatting.WHITE);
  }

  @Subscribe
  public void registerMaterials(FMLPreInitializationEvent event) {
    for(Material material : materials) {
      TinkerRegistry.addMaterial(material);
    }
//TinkerRegistry.addMaterial(ardite);
    //TinkerRegistry.addMaterial(xu);
  }

  @Subscribe
  public void registerRendering(FMLPostInitializationEvent event) {
    if(event.getSide().isClient()) {
      TinkerMaterials.registerMaterialRendering();
    }
  }

  @SideOnly(Side.CLIENT)
  private static void registerMaterialRendering() {
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
    //iron.setRenderInfo(new MaterialRenderInfo.Metal(0xcccccc, 0.0f, 0f, 0f));
    iron.setRenderInfo(new MaterialRenderInfo.Metal(0xcacaca, 0f, 0.3f, 0f));
    cobalt.setRenderInfo(new MaterialRenderInfo.Metal(0x173b75, 0.25f, 0.5f, -0.1f));
    //ardite.setRenderInfo(new MaterialRenderInfo.Metal(0xa53000, 0.4f, 0.4f, 0.1f));
    ardite.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() {
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new MetalTextureTexture(Util.resource("items/materials/ardite_rust"), baseTexture, location, 0xf97217, 0.6f, 0.4f, 0.1f);
      }
    });
    //ardite.setRenderInfo(new MaterialRenderInfo.MultiColor(0x4e0000, 0xbc2a00, 0xff9e00).setTextureSuffix("metal"));
    //ardite.setRenderInfo(new MaterialRenderInfo.MultiColor(0x0000FF, 0x00FF00, 0xff9e00).setTextureSuffix("metal"));
    manyullyn.setRenderInfo(new MaterialRenderInfo.Metal(0x623588, 0.15f, 0.3f, 0.05f));

    xu.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() {
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new ExtraUtilityTexture(baseTexture, location);
      }
    });
  }

  @Subscribe
  public void setupMaterials(FMLInitializationEvent event) {
    // natural resources/blocks
    wood.setCraftable(true);
    wood.addItem("stickWood", 1, Material.VALUE_Shard);
    wood.addItem("plankWood", 1, Material.VALUE_Ingot);
    wood.addItem("logWood", 1, Material.VALUE_Ingot * 4);
    wood.setRepresentativeItem(new ItemStack(Items.stick));
    wood.addTrait(ecological);

    stone.setCraftable(true);
    stone.addItem("cobblestone", 1, Material.VALUE_Ingot);
    stone.addItem("stone", 1, Material.VALUE_Ingot);
    stone.setRepresentativeItem(new ItemStack(Blocks.cobblestone));
    stone.addTrait(cheap);

    flint.setCraftable(true);
    flint.addItem("flint", 1, Material.VALUE_Ingot);
    flint.setRepresentativeItem(new ItemStack(Items.flint));
    flint.addTrait(crude);

    cactus.setCraftable(true);
    cactus.addItem("blockCactus", 1, Material.VALUE_Ingot);
    cactus.setRepresentativeItem(new ItemStack(Blocks.cactus));
    cactus.addTrait(prickly);

    obsidian.setCraftable(true);
    obsidian.setFluid(FluidRegistry.WATER).setCastable(true); // todo
    obsidian.addItem("blockObsidian", 1, Material.VALUE_Ingot);
    obsidian.setRepresentativeItem(new ItemStack(Blocks.obsidian));
    obsidian.addTrait(duritos);

    prismarine.setCraftable(true);
    prismarine.addItem(Items.prismarine_shard, 1, Material.VALUE_Fragment);
    prismarine.addItem(Blocks.prismarine, Material.VALUE_Ingot);
    prismarine.setRepresentativeItem(Items.prismarine_shard);

    netherrack.setCraftable(true);
    netherrack.addItem(Blocks.netherrack, Material.VALUE_Ingot);
    netherrack.setRepresentativeItem(Blocks.netherrack);
    netherrack.addTrait(aridiculous);

    endstone.setCraftable(true);
    endstone.addItem(Blocks.end_stone, Material.VALUE_Ingot);
    endstone.setRepresentativeItem(Blocks.end_stone);

    // item/special resources
    bone.setCraftable(true);
    bone.addItem(Items.bone, 1, Material.VALUE_Ingot);
    bone.setRepresentativeItem(Items.bone);
    bone.addTrait(splintering);

    paper.setCraftable(true);
    paper.addItem(Items.paper, 1, Material.VALUE_Fragment);
    paper.setRepresentativeItem(Items.paper);

    sponge.setCraftable(true);
    sponge.addItem(Blocks.sponge, Material.VALUE_Ingot);
    sponge.setRepresentativeItem(Blocks.sponge);
    sponge.addTrait(squeaky);

    slime.setCraftable(true);
    safeAdd(slime, TinkerCommons.matSlimeCrystal, Material.VALUE_Ingot, true);
    slime.addTrait(slimeyGreen);

    blueslime.setCraftable(true);
    safeAdd(slime, TinkerCommons.matSlimeCrystalBlue, Material.VALUE_Ingot, true);
    blueslime.addTrait(slimeyBlue);

    // Metals
    iron.addItem(Items.iron_ingot);
    iron.setRepresentativeItem(Items.iron_ingot);
    // todo: remaining metals

    safeAdd(cobalt, TinkerCommons.ingotCobalt, Material.VALUE_Ingot, true);
    cobalt.addTrait(momentum);

    safeAdd(ardite, TinkerCommons.ingotArdite, Material.VALUE_Ingot, true);
    ardite.addTrait(petramor);

    safeAdd(manyullyn, TinkerCommons.ingotManyullyn, Material.VALUE_Ingot, true);
    manyullyn.addTrait(insatiable);

    registerToolMaterials();
  }

  private void safeAdd(Material material, ItemStack item, int value) {
    this.safeAdd(material, item, value, false);
  }

  private void safeAdd(Material material, ItemStack item, int value, boolean representative) {
    if(item != null) {
      material.addItem(item, 1, value);
      if(representative) {
        material.setRepresentativeItem(item);
      }
    }
  }

  public void registerToolMaterials() {
    // Stats:                                                   Durability, speed, attack, handle, extra, harvestlevel
    // natural resources/blocks
    TinkerRegistry.addMaterialStats(wood,       new ToolMaterialStats( 137, 3.00f, 0.20f, 0.80f, 0.50f, STONE));
    TinkerRegistry.addMaterialStats(stone,      new ToolMaterialStats( 195, 4.00f, 0.50f, 0.05f, 0.18f, STONE));
    TinkerRegistry.addMaterialStats(flint,      new ToolMaterialStats( 285, 5.70f, 0.90f, 0.20f, 0.19f, IRON));
    TinkerRegistry.addMaterialStats(cactus,     new ToolMaterialStats( 329, 4.50f, 2.00f, 0.25f, 0.43f, IRON));
    TinkerRegistry.addMaterialStats(obsidian,   new ToolMaterialStats( 111, 7.07f, 2.80f, 0.02f, 0.24f, COBALT));
    TinkerRegistry.addMaterialStats(prismarine, new ToolMaterialStats( 612, 5.50f, 5.00f, 0.18f, 0.84f, IRON));
    TinkerRegistry.addMaterialStats(netherrack, new ToolMaterialStats( 322, 4.89f, 1.00f, 0.10f, 0.27f, IRON));
    TinkerRegistry.addMaterialStats(endstone,   new ToolMaterialStats( 533, 3.33f, 3.23f, 0.33f, 0.33f, OBSIDIAN));

    // item/special resources
    TinkerRegistry.addMaterialStats(bone,       new ToolMaterialStats( 335, 5.09f, 1.50f, 0.70f, 0.56f, IRON));
    TinkerRegistry.addMaterialStats(paper,      new ToolMaterialStats(  42, 0.51f, 0.05f, 0.01f, 0.70f, STONE));
    TinkerRegistry.addMaterialStats(sponge,     new ToolMaterialStats( 850, 3.02f, 0.00f, 0.01f, 0.01f, STONE));
    TinkerRegistry.addMaterialStats(slime,      new ToolMaterialStats(1200, 4.03f, 1.80f, 0.66f, 0.15f, STONE));
    TinkerRegistry.addMaterialStats(blueslime,  new ToolMaterialStats( 550, 4.24f, 1.80f, 0.30f, 1.00f, STONE));

    TinkerRegistry.addMaterialStats(iron,       new ToolMaterialStats( 853, 6.00f, 1.80f, 0.50f, 0.60f, IRON));
    TinkerRegistry.addMaterialStats(cobalt,     new ToolMaterialStats( 720,11.11f, 3.20f, 0.40f, 0.60f, COBALT));
    TinkerRegistry.addMaterialStats(ardite,     new ToolMaterialStats(1234, 2.42f, 1.80f, 0.64f, 0.78f, COBALT));
    TinkerRegistry.addMaterialStats(manyullyn,  new ToolMaterialStats( 513, 7.80f, 8.72f, 0.30f, 0.70f, COBALT));

    //TinkerRegistry.addMaterialStats(xu,         new ToolMaterialStats(97, 1.00f, 1.00f, 0.10f, 0.20f, DIAMOND));
  }

  public void registerBowMaterials() {

  }

  public void registerProjectileMaterials() {

  }

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    // each material without a shard set gets the default one set
    for(Material material : TinkerRegistry.getAllMaterials()) {
      ItemStack shard = TinkerTools.shard.getItemstackWithMaterial(material);

      material.addRecipeMatch(new RecipeMatch.ItemCombination(Material.VALUE_Shard, shard));
      if(material.getShard() != null) {
        material.setShard(shard);
      }
    }
  }
}
