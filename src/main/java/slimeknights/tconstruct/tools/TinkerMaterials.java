package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.texture.ExtraUtilityTexture;
import slimeknights.tconstruct.library.client.texture.MetalColoredTexture;
import slimeknights.tconstruct.library.client.texture.MetalTextureTexture;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.tools.traits.TraitAlien;
import slimeknights.tconstruct.tools.traits.TraitAquadynamic;
import slimeknights.tconstruct.tools.traits.TraitAridiculous;
import slimeknights.tconstruct.tools.traits.TraitBaconlicious;
import slimeknights.tconstruct.tools.traits.TraitBonusDamage;
import slimeknights.tconstruct.tools.traits.TraitCheap;
import slimeknights.tconstruct.tools.traits.TraitCheapskate;
import slimeknights.tconstruct.tools.traits.TraitColdblooded;
import slimeknights.tconstruct.tools.traits.TraitCrude;
import slimeknights.tconstruct.tools.traits.TraitCrumbling;
import slimeknights.tconstruct.tools.traits.TraitDuritos;
import slimeknights.tconstruct.tools.traits.TraitEcological;
import slimeknights.tconstruct.tools.traits.TraitEnderference;
import slimeknights.tconstruct.tools.traits.TraitHellish;
import slimeknights.tconstruct.tools.traits.TraitInsatiable;
import slimeknights.tconstruct.tools.traits.TraitJagged;
import slimeknights.tconstruct.tools.traits.TraitLightweight;
import slimeknights.tconstruct.tools.traits.TraitMagnetic;
import slimeknights.tconstruct.tools.traits.TraitMomentum;
import slimeknights.tconstruct.tools.traits.TraitPetramor;
import slimeknights.tconstruct.tools.traits.TraitPrickly;
import slimeknights.tconstruct.tools.traits.TraitSlimey;
import slimeknights.tconstruct.tools.traits.TraitSpiky;
import slimeknights.tconstruct.tools.traits.TraitSplintering;
import slimeknights.tconstruct.tools.traits.TraitSplinters;
import slimeknights.tconstruct.tools.traits.TraitSqueaky;
import slimeknights.tconstruct.tools.traits.TraitStonebound;
import slimeknights.tconstruct.tools.traits.TraitTasty;
import slimeknights.tconstruct.tools.traits.TraitUnnatural;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;

import static slimeknights.tconstruct.library.utils.HarvestLevels.COBALT;
import static slimeknights.tconstruct.library.utils.HarvestLevels.DIAMOND;
import static slimeknights.tconstruct.library.utils.HarvestLevels.IRON;
import static slimeknights.tconstruct.library.utils.HarvestLevels.OBSIDIAN;
import static slimeknights.tconstruct.library.utils.HarvestLevels.STONE;

/**
 * All the tool materials tcon supports.
 */
@Pulse(id = TinkerMaterials.PulseId, description = "All the tool materials added by TConstruct", pulsesRequired = TinkerTools.PulseId, forced = true)
public final class TinkerMaterials {

  static final String PulseId = "TinkerMaterials";
  static final Logger log = Util.getLogger(PulseId);

  public static final List<Material> materials = Lists.newArrayList();

  // not all listed materials are available by default. They enable when the needed material is present

  // natural resources/blocks
  public static final Material wood       = mat("wood", 0x8e661b);
  public static final Material stone      = mat("stone", 0x999999);
  public static final Material flint      = mat("flint", 0x696969);
  public static final Material cactus     = mat("cactus", 0x00a10f);
  public static final Material bone       = mat("bone", 0xede6bf);
  public static final Material obsidian   = mat("obsidian", 0x601cc4);
  public static final Material prismarine = mat("prismarine", 0x7edebc);
  public static final Material endstone   = mat("endstone", 0xe0d890);
  public static final Material paper      = new Material("paper", 0xffffff);//mat("paper", 0xffffff);
  public static final Material sponge     = mat("sponge", 0xcacc4e);

  // Slime
  public static final Material knightslime= mat("knightslime", 0xf18ff0);
  public static final Material slime      = mat("slime", 0x82c873);
  public static final Material blueslime  = mat("blueslime", 0x74c8c7);

  // Metals
  public static final Material iron       = mat("iron", 0xcacaca);
  public static final Material pigiron    = mat("pigiron", 0xef9e9b);
  public static final Material copper     = mat("copper", 0xed9f07);
  public static final Material bronze     = mat("bronze", 0xd2a869);

  // Nether Materials
  public static final Material netherrack = mat("netherrack", 0xb84f4f);
  public static final Material ardite     = mat("ardite", 0xd14210);
  public static final Material cobalt     = mat("cobalt", 0x2882d4);
  public static final Material manyullyn  = mat("manyullyn", 0xa15cf8);

  // specul
  public static final Material xu;

  public static final AbstractTrait alien = new TraitAlien();
  public static final AbstractTrait aquadynamic = new TraitAquadynamic();
  public static final AbstractTrait aridiculous = new TraitAridiculous();
  public static final AbstractTrait baconlicious = new TraitBaconlicious();
  public static final AbstractTrait cheap = new TraitCheap();
  public static final AbstractTrait cheapskate = new TraitCheapskate();
  public static final AbstractTrait coldblooded = new TraitColdblooded();
  public static final AbstractTrait crude = new TraitCrude(1);
  public static final AbstractTrait crude2 = new TraitCrude(2);
  public static final AbstractTrait crumbling = new TraitCrumbling();
  public static final AbstractTrait duritos = new TraitDuritos(); // yes you read that correctly
  public static final AbstractTrait ecological = new TraitEcological();
  public static final AbstractTrait enderference = new TraitEnderference();
  public static final AbstractTrait fractured = new TraitBonusDamage("fractured", 2);
  public static final AbstractTrait hellish = new TraitHellish();
  public static final AbstractTrait insatiable = new TraitInsatiable();
  public static final AbstractTrait jagged = new TraitJagged();
  public static final AbstractTrait lightweight = new TraitLightweight();
  public static final AbstractTrait magnetic = new TraitMagnetic(1);
  public static final AbstractTrait magnetic2 = new TraitMagnetic(2);
  public static final AbstractTrait momentum = new TraitMomentum();
  public static final AbstractTrait petramor = new TraitPetramor();
  public static final AbstractTrait prickly = new TraitPrickly();
  public static final AbstractTrait slimeyGreen = new TraitSlimey("green", EntitySlime.class);
  public static final AbstractTrait slimeyBlue = new TraitSlimey("blue", EntityBlueSlime.class);
  public static final AbstractTrait spiky = new TraitSpiky();
  public static final AbstractTrait splintering = new TraitSplintering();
  public static final AbstractTrait splinters = new TraitSplinters();
  public static final AbstractTrait squeaky = new TraitSqueaky();
  public static final AbstractTrait stonebound = new TraitStonebound();
  public static final AbstractTrait tasty = new TraitTasty();
  public static final AbstractTrait unnatural = new TraitUnnatural();

  private static Material mat(String name, int color) {
    Material mat = new Material(name, color);
    materials.add(mat);
    return mat;
  }

  static {
    xu = new Material("unstable", EnumChatFormatting.WHITE);
  }

  private static final String HEAD = HeadMaterialStats.TYPE;

  @Subscribe
  public void registerRendering(FMLPostInitializationEvent event) {
    if(event.getSide().isClient()) {
      TinkerMaterials.registerMaterialRendering();
    }
  }

  @SideOnly(Side.CLIENT)
  private static void registerMaterialRendering() {
    wood.setRenderInfo(new MaterialRenderInfo.MultiColor(0x6e572a, 0x745f38, 0x8e671d));
    stone.setRenderInfo(0x696969);
    flint.setRenderInfo(0xffffff).setTextureSuffix("contrast");
    cactus.setRenderInfo(0x006d0a); // cactus has custom textures
    obsidian.setRenderInfo(new MaterialRenderInfo.MultiColor(0x71589c, 0x8f60d4, 0x8c53df).setTextureSuffix("contrast")); // increase each color by 20 to get thaumium
    prismarine.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/prismarine_bricks"));
    netherrack.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/netherrack"));
    //endstone.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/end_stone"));
    endstone.setRenderInfo(new MaterialRenderInfo.InverseMultiColor(0x5c6296, 0x3c4276, 0x212a76));

    bone.setRenderInfo(0xede6bf).setTextureSuffix("bone_base");
    paper.setRenderInfo(0xffffff); // paper has custom textures
    sponge.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/sponge"));
    slime.setRenderInfo(0x82c873);
    blueslime.setRenderInfo(0x74c8c7);


    // Metals
    //iron.setRenderInfo(new MaterialRenderInfo.Metal(0xcccccc, 0.0f, 0f, 0f));
    iron.setRenderInfo(new MaterialRenderInfo.Metal(0xcacaca, 0f, 0.3f, 0f));
    cobalt.setRenderInfo(new MaterialRenderInfo.Metal(0x173b75, 0.25f, 0.5f, -0.1f));
    //ardite.setRenderInfo(new MaterialRenderInfo.Metal(0xa53000, 0.4f, 0.4f, 0.1f));
    ardite.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() { // not technically a metal
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new MetalTextureTexture(Util.resource("items/materials/ardite_rust"), baseTexture, location, 0xf97217, 0.6f, 0.4f, 0.1f);
      }
    });
    //ardite.setRenderInfo(new MaterialRenderInfo.MultiColor(0x4e0000, 0xbc2a00, 0xff9e00).setTextureSuffix("metal"));
    //ardite.setRenderInfo(new MaterialRenderInfo.MultiColor(0x0000FF, 0x00FF00, 0xff9e00).setTextureSuffix("metal"));
    manyullyn.setRenderInfo(new MaterialRenderInfo.Metal(0xa93df5, 0.4f, 0.2f, -0.1f));

    pigiron.setRenderInfo(new MaterialRenderInfo.Metal(0xd37c78, 0.1f, 0.1f, 0f));

    // alloys
    //knightslime.setRenderInfo(new MaterialRenderInfo.MultiColor(0x9c9c9c, 0xb79acc, 0xbc61f8).setTextureSuffix("contrast")); // looks awesome as obsidian
    TinkerMaterials.knightslime.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() { // not technically a metal
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        //return new MetalTextureTexture(Util.resource("blocks/slime/slimeblock_purple"), baseTexture, location, 0xdf86fa, 0.4f, 0.2f, 0.0f);
        return new MetalColoredTexture(baseTexture, location, 0x685bd0, 0.0f, 0.5f, 0.3f);
      }
    });

    // specul
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
    wood.addTrait(ecological, HEAD);
    wood.addTrait(splinters);

    stone.setCraftable(true);
    stone.addItem("cobblestone", 1, Material.VALUE_Ingot);
    stone.addItem("stone", 1, Material.VALUE_Ingot);
    stone.setRepresentativeItem(new ItemStack(Blocks.cobblestone));
    stone.addTrait(cheapskate, HEAD);
    stone.addTrait(cheap);

    flint.setCraftable(true);
    flint.addItem(Items.flint, 1, Material.VALUE_Ingot);
    flint.setRepresentativeItem(new ItemStack(Items.flint));
    flint.addTrait(crude2, HEAD);
    flint.addTrait(crude);

    cactus.setCraftable(true);
    cactus.addItem("blockCactus", 1, Material.VALUE_Ingot);
    cactus.setRepresentativeItem(new ItemStack(Blocks.cactus));
    cactus.addTrait(prickly, HEAD);
    cactus.addTrait(spiky);

    obsidian.setFluid(TinkerFluids.obsidian);
    obsidian.setCraftable(true);
    obsidian.setCastable(true);
    obsidian.addItem(Blocks.obsidian, Material.VALUE_Ingot);
    obsidian.setRepresentativeItem(new ItemStack(Blocks.obsidian));
    obsidian.addTrait(duritos, HEAD);
    obsidian.addTrait(fractured);

    prismarine.setCraftable(true);
    prismarine.addItem(Items.prismarine_shard, 1, Material.VALUE_Fragment);
    prismarine.addItem(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.ROUGH_META), 1, Material.VALUE_Ingot);
    prismarine.addItem(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.BRICKS_META), 1, Material.VALUE_Fragment*9);
    prismarine.addItem(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.DARK_META), 1, Material.VALUE_Ingot*2);
    prismarine.setRepresentativeItem(Blocks.prismarine);
    prismarine.addTrait(jagged, HEAD);
    prismarine.addTrait(aquadynamic);

    netherrack.setCraftable(true);
    netherrack.addItem(Blocks.netherrack, Material.VALUE_Ingot);
    netherrack.setRepresentativeItem(Blocks.netherrack);
    netherrack.addTrait(aridiculous, HEAD);
    netherrack.addTrait(hellish);

    endstone.setCraftable(true);
    endstone.addItem(Blocks.end_stone, Material.VALUE_Ingot);
    endstone.setRepresentativeItem(Blocks.end_stone);
    endstone.addTrait(alien, HEAD);
    endstone.addTrait(enderference);

    // item/special resources
    bone.setCraftable(true);
    bone.addItem(Items.bone, 1, Material.VALUE_Ingot);
    bone.addItem(new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()), 1, Material.VALUE_Fragment); // bonemeal
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
    safeAdd(blueslime, TinkerCommons.matSlimeCrystalBlue, Material.VALUE_Ingot, true);
    blueslime.addTrait(slimeyBlue);

    knightslime.setCraftable(true);
    safeAdd(knightslime, TinkerCommons.ingotKnightSlime, Material.VALUE_Ingot, true);
    knightslime.addTrait(crumbling, HEAD);
    knightslime.addTrait(unnatural);

    // Metals
    iron.addItem("ingotIron", 1, Material.VALUE_Ingot);
    iron.setRepresentativeItem(Items.iron_ingot);
    iron.addTrait(magnetic2, HEAD);
    iron.addTrait(magnetic);
    // todo: remaining metals

    safeAdd(pigiron, TinkerCommons.ingotPigIron, Material.VALUE_Ingot, true);
    pigiron.addTrait(baconlicious, HEAD);
    pigiron.addTrait(tasty, HEAD);
    pigiron.addTrait(tasty);

    safeAdd(cobalt, TinkerCommons.ingotCobalt, Material.VALUE_Ingot, true);
    cobalt.addTrait(momentum, HEAD);
    cobalt.addTrait(lightweight);

    safeAdd(ardite, TinkerCommons.ingotArdite, Material.VALUE_Ingot, true);
    ardite.addTrait(stonebound, HEAD);
    ardite.addTrait(petramor);

    safeAdd(manyullyn, TinkerCommons.ingotManyullyn, Material.VALUE_Ingot, true);
    manyullyn.addTrait(insatiable, HEAD);
    manyullyn.addTrait(coldblooded);

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
    TinkerRegistry.addMaterialStats(wood,
                                    new HeadMaterialStats(35, 2.00f, 2.00f, STONE),
                                    new HandleMaterialStats(1.00f, 25),
                                    new ExtraMaterialStats(15));

    TinkerRegistry.addMaterialStats(stone,
                                    new HeadMaterialStats(120, 4.00f, 2.90f, IRON),
                                    new HandleMaterialStats(0.50f, -50),
                                    new ExtraMaterialStats(20));
    TinkerRegistry.addMaterialStats(flint,
                                    new HeadMaterialStats(150, 5.00f, 2.80f, IRON),
                                    new HandleMaterialStats(0.60f, -60),
                                    new ExtraMaterialStats(40));
    TinkerRegistry.addMaterialStats(cactus,
                                    new HeadMaterialStats(210, 4.00f, 3.40f, IRON),
                                    new HandleMaterialStats(0.85f, 20),
                                    new ExtraMaterialStats(50));
    TinkerRegistry.addMaterialStats(bone,
                                    new HeadMaterialStats(200, 5.09f, 2.50f, IRON),
                                    new HandleMaterialStats(1.10f, 60),
                                    new ExtraMaterialStats(65));
    TinkerRegistry.addMaterialStats(obsidian,
                                    new HeadMaterialStats(89, 7.07f, 4.20f, COBALT),
                                    new HandleMaterialStats(0.90f, -150),
                                    new ExtraMaterialStats(90));
    TinkerRegistry.addMaterialStats(prismarine,
                                    new HeadMaterialStats(430, 5.50f, 6.00f, IRON),
                                    new HandleMaterialStats(0.60f, -200),
                                    new ExtraMaterialStats(100));
    TinkerRegistry.addMaterialStats(endstone,
                                    new HeadMaterialStats(420, 3.23f, 3.23f, OBSIDIAN),
                                    new HandleMaterialStats(0.85f, 0),
                                    new ExtraMaterialStats(42));
    TinkerRegistry.addMaterialStats(paper,
                                    new HeadMaterialStats(12, 0.51f, 0.05f, STONE),
                                    new HandleMaterialStats(0.10f, 5),
                                    new ExtraMaterialStats(5));
    TinkerRegistry.addMaterialStats(sponge,
                                    new HeadMaterialStats(550, 3.02f, 0.00f, STONE),
                                    new HandleMaterialStats(1.20f, 250),
                                    new ExtraMaterialStats(250));

    // Slime
    TinkerRegistry.addMaterialStats(slime,
                                    new HeadMaterialStats(1000, 4.24f, 1.80f, STONE),
                                    new HandleMaterialStats(0.70f, -100),
                                    new ExtraMaterialStats(350));
    TinkerRegistry.addMaterialStats(blueslime,
                                    new HeadMaterialStats(780, 4.03f, 1.80f, STONE),
                                    new HandleMaterialStats(1.30f, -100),
                                    new ExtraMaterialStats(200));
    TinkerRegistry.addMaterialStats(knightslime,
                                    new HeadMaterialStats(800, 3.81f, 5.10f, OBSIDIAN),
                                    new HandleMaterialStats(0.50f, 500),
                                    new ExtraMaterialStats(125));

    // Nether
    TinkerRegistry.addMaterialStats(netherrack,
                                    new HeadMaterialStats(270, 4.50f, 3.00f, IRON),
                                    new HandleMaterialStats(0.85f, -150),
                                    new ExtraMaterialStats(75));
    TinkerRegistry.addMaterialStats(cobalt,
                                    new HeadMaterialStats(780, 10.00f, 4.10f, COBALT),
                                    new HandleMaterialStats(0.90f, 100),
                                    new ExtraMaterialStats(300));
    TinkerRegistry.addMaterialStats(ardite,
                                    new HeadMaterialStats(990, 2.42f, 3.60f, COBALT),
                                    new HandleMaterialStats(1.40f, -200),
                                    new ExtraMaterialStats(450));
    TinkerRegistry.addMaterialStats(manyullyn,
                                    new HeadMaterialStats(820, 7.02f, 8.72f, COBALT),
                                    new HandleMaterialStats(0.50f, 250),
                                    new ExtraMaterialStats(50));

    // Metals
    TinkerRegistry.addMaterialStats(iron,
                                    new HeadMaterialStats(204, 6.00f, 4.00f, DIAMOND),
                                    new HandleMaterialStats(0.85f, 60),
                                    new ExtraMaterialStats(50));
    TinkerRegistry.addMaterialStats(pigiron,
                                    new HeadMaterialStats(380, 6.20f, 4.50f, OBSIDIAN),
                                    new HandleMaterialStats(1.20f, -100),
                                    new ExtraMaterialStats(170));

    //TinkerRegistry.addMaterialStats(xu,         new ToolMaterialStats(97, 1.00f, 1.00f, 0.10f, 0.20f, DIAMOND));
  }

  public void registerBowMaterials() {

  }

  public void registerProjectileMaterials() {

  }

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    if(TinkerTools.shard == null) return;

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
