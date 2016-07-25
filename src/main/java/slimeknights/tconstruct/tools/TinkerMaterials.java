package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import com.sun.org.apache.bcel.internal.generic.IREM;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
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
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.tools.traits.TraitAlien;
import slimeknights.tconstruct.tools.traits.TraitAquadynamic;
import slimeknights.tconstruct.tools.traits.TraitAridiculous;
import slimeknights.tconstruct.tools.traits.TraitAutosmelt;
import slimeknights.tconstruct.tools.traits.TraitBaconlicious;
import slimeknights.tconstruct.tools.traits.TraitBonusDamage;
import slimeknights.tconstruct.tools.traits.TraitCheap;
import slimeknights.tconstruct.tools.traits.TraitCheapskate;
import slimeknights.tconstruct.tools.traits.TraitColdblooded;
import slimeknights.tconstruct.tools.traits.TraitCrude;
import slimeknights.tconstruct.tools.traits.TraitCrumbling;
import slimeknights.tconstruct.tools.traits.TraitDense;
import slimeknights.tconstruct.tools.traits.TraitDepthdigger;
import slimeknights.tconstruct.tools.traits.TraitDuritos;
import slimeknights.tconstruct.tools.traits.TraitEcological;
import slimeknights.tconstruct.tools.traits.TraitEnderference;
import slimeknights.tconstruct.tools.traits.TraitEstablished;
import slimeknights.tconstruct.tools.traits.TraitFlammable;
import slimeknights.tconstruct.tools.traits.TraitHellish;
import slimeknights.tconstruct.tools.traits.TraitHoly;
import slimeknights.tconstruct.tools.traits.TraitInsatiable;
import slimeknights.tconstruct.tools.traits.TraitJagged;
import slimeknights.tconstruct.tools.traits.TraitLightweight;
import slimeknights.tconstruct.tools.traits.TraitMagnetic;
import slimeknights.tconstruct.tools.traits.TraitMomentum;
import slimeknights.tconstruct.tools.traits.TraitPetramor;
import slimeknights.tconstruct.tools.traits.TraitPoisonous;
import slimeknights.tconstruct.tools.traits.TraitPrickly;
import slimeknights.tconstruct.tools.traits.TraitSharp;
import slimeknights.tconstruct.tools.traits.TraitShocking;
import slimeknights.tconstruct.tools.traits.TraitSlimey;
import slimeknights.tconstruct.tools.traits.TraitSpiky;
import slimeknights.tconstruct.tools.traits.TraitSplintering;
import slimeknights.tconstruct.tools.traits.TraitSplinters;
import slimeknights.tconstruct.tools.traits.TraitSqueaky;
import slimeknights.tconstruct.tools.traits.TraitStiff;
import slimeknights.tconstruct.tools.traits.TraitStonebound;
import slimeknights.tconstruct.tools.traits.TraitSuperheat;
import slimeknights.tconstruct.tools.traits.TraitTasty;
import slimeknights.tconstruct.tools.traits.TraitUnnatural;
import slimeknights.tconstruct.tools.traits.TraitWritable;
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
  public static final Material paper      = mat("paper", 0xffffff);
  public static final Material sponge     = mat("sponge", 0xcacc4e);
  public static final Material firewood   = mat("firewood", 0xcc5300);

  // Slime
  public static final Material knightslime= mat("knightslime", 0xf18ff0);
  public static final Material slime      = mat("slime", 0x82c873);
  public static final Material blueslime  = mat("blueslime", 0x74c8c7);
  public static final Material magmaslime = mat("magmaslime", 0xff960d);

  // Metals
  public static final Material iron       = mat("iron", 0xcacaca);
  public static final Material pigiron    = mat("pigiron", 0xef9e9b);

  // Nether Materials
  public static final Material netherrack = mat("netherrack", 0xb84f4f);
  public static final Material ardite     = mat("ardite", 0xd14210);
  public static final Material cobalt     = mat("cobalt", 0x2882d4);
  public static final Material manyullyn  = mat("manyullyn", 0xa15cf8);

  // mod integration
  public static final Material copper     = mat("copper", 0xed9f07);
  public static final Material bronze     = mat("bronze", 0xe3bd68);
  public static final Material lead       = mat("lead", 0x4d4968);
  public static final Material silver     = mat("silver", 0xd1ecf6);
  public static final Material electrum   = mat("electrum", 0xe8db49);
  public static final Material steel      = mat("steel", 0xa7a7a7);

  // specul
  public static final Material xu;

  public static final AbstractTrait alien = new TraitAlien();
  public static final AbstractTrait aquadynamic = new TraitAquadynamic();
  public static final AbstractTrait aridiculous = new TraitAridiculous();
  public static final AbstractTrait autosmelt = new TraitAutosmelt();
  public static final AbstractTrait baconlicious = new TraitBaconlicious();
  public static final AbstractTrait cheap = new TraitCheap();
  public static final AbstractTrait cheapskate = new TraitCheapskate();
  public static final AbstractTrait coldblooded = new TraitColdblooded();
  public static final AbstractTrait crude = new TraitCrude(1);
  public static final AbstractTrait crude2 = new TraitCrude(2);
  public static final AbstractTrait crumbling = new TraitCrumbling();
  public static final AbstractTrait dense = new TraitDense();
  public static final AbstractTrait depthdigger = new TraitDepthdigger();
  public static final AbstractTrait duritos = new TraitDuritos(); // yes you read that correctly
  public static final AbstractTrait ecological = new TraitEcological();
  public static final AbstractTrait enderference = new TraitEnderference();
  public static final AbstractTrait established = new TraitEstablished();
  public static final AbstractTrait flammable = new TraitFlammable();
  public static final AbstractTrait fractured = new TraitBonusDamage("fractured", 1.5f);
  public static final AbstractTrait hellish = new TraitHellish();
  public static final AbstractTrait holy = new TraitHoly();
  public static final AbstractTrait insatiable = new TraitInsatiable();
  public static final AbstractTrait jagged = new TraitJagged();
  public static final AbstractTrait lightweight = new TraitLightweight();
  public static final AbstractTrait magnetic = new TraitMagnetic(1);
  public static final AbstractTrait magnetic2 = new TraitMagnetic(2);
  public static final AbstractTrait momentum = new TraitMomentum();
  public static final AbstractTrait petramor = new TraitPetramor();
  public static final AbstractTrait poisonous = new TraitPoisonous();
  public static final AbstractTrait prickly = new TraitPrickly();
  public static final AbstractTrait sharp = new TraitSharp();
  public static final AbstractTrait shocking = new TraitShocking();
  public static final AbstractTrait slimeyGreen = new TraitSlimey("green", EntitySlime.class);
  public static final AbstractTrait slimeyBlue = new TraitSlimey("blue", EntityBlueSlime.class);
  public static final AbstractTrait spiky = new TraitSpiky();
  public static final AbstractTrait splintering = new TraitSplintering();
  public static final AbstractTrait splinters = new TraitSplinters();
  public static final AbstractTrait squeaky = new TraitSqueaky();
  public static final AbstractTrait superheat = new TraitSuperheat();
  public static final AbstractTrait stiff = new TraitStiff();
  public static final AbstractTrait stonebound = new TraitStonebound();
  public static final AbstractTrait tasty = new TraitTasty();
  public static final AbstractTrait unnatural = new TraitUnnatural();
  public static final AbstractTrait writable = new TraitWritable(1);
  public static final AbstractTrait writable2 = new TraitWritable(2);

  private static Material mat(String name, int color) {
    Material mat = new Material(name, color);
    materials.add(mat);
    return mat;
  }

  static {
    xu = new Material("unstable", TextFormatting.WHITE);
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
    endstone.setRenderInfo(new MaterialRenderInfo.InverseMultiColor(0x5c6296, 0x3c4276, 0x212a76));
    firewood.setRenderInfo(new MaterialRenderInfo.BlockTexture("tconstruct:blocks/firewood"));

    bone.setRenderInfo(0xede6bf).setTextureSuffix("bone_base");
    paper.setRenderInfo(0xffffff); // paper has custom textures
    sponge.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/sponge"));
    slime.setRenderInfo(0x82c873);
    blueslime.setRenderInfo(0x74c8c7);
    magmaslime.setRenderInfo(new MaterialRenderInfo.MultiColor(0xa8673b, 0xff8c49, 0xff9d3d));
    //magmaslime.setRenderInfo(new MaterialRenderInfo.BlockTexture("minecraft:blocks/lava_flow"));
    /*
    magmaslime.setRenderInfo(new MaterialRenderInfo.AbstractMaterialRenderInfo() { // not technically a metal
      @Override
      public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
        return new MetalTextureTexture(Util.resource("items/materials/magmaslime_pattern"), baseTexture, location, 0xff8c0d, 0.00f, 0.05f, 0.0f);
      }
    });*/


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

    // mod integration
    copper.setRenderInfo(new MaterialRenderInfo.Metal(0xefa055, 0.25f, 0.25f, -0.05f));
    bronze.setRenderInfo(new MaterialRenderInfo.Metal(0xe3bd68, 0.25f, 0.15f, -0.05f));
    lead.setRenderInfo(new MaterialRenderInfo.Metal(0x4d4968, 0.0f, 0.15f, 0.2f));
    silver.setRenderInfo(new MaterialRenderInfo.Metal(0xd1ecf6, 1f, 0.5f, 0.1f));
    electrum.setRenderInfo(new MaterialRenderInfo.Metal(0xeddd51, 0.15f, 0.25f, -0.05f));
    steel.setRenderInfo(new MaterialRenderInfo.Metal(0x888888, 0.1f, 0.3f, 0.1f));

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
    wood.setRepresentativeItem(new ItemStack(Items.STICK));
    wood.addTrait(ecological, HEAD);
    wood.addTrait(splinters);
    wood.addTrait(ecological);

    stone.setCraftable(true);
    stone.addItemIngot("cobblestone");
    stone.addItemIngot("stone");
    stone.setRepresentativeItem(new ItemStack(Blocks.COBBLESTONE));
    stone.addTrait(cheapskate, HEAD);
    stone.addTrait(cheap);

    flint.setCraftable(true);
    flint.addItem(Items.FLINT, 1, Material.VALUE_Ingot);
    flint.setRepresentativeItem(new ItemStack(Items.FLINT));
    flint.addTrait(crude2, HEAD);
    flint.addTrait(crude);

    cactus.setCraftable(true);
    cactus.addItemIngot("blockCactus");
    cactus.setRepresentativeItem(new ItemStack(Blocks.CACTUS));
    cactus.addTrait(prickly, HEAD);
    cactus.addTrait(spiky);

    obsidian.setFluid(TinkerFluids.obsidian);
    obsidian.setCraftable(true);
    obsidian.setCastable(true);
    obsidian.addItemIngot("obsidian");
    obsidian.setRepresentativeItem(new ItemStack(Blocks.OBSIDIAN));
    obsidian.addTrait(duritos);

    prismarine.setCraftable(true);
    prismarine.addItem("gemPrismarine", 1, Material.VALUE_Fragment);
    prismarine.addItem("blockPrismarine", 1, Material.VALUE_Ingot);
    prismarine.addItem("blockPrismarineBrick", 1, Material.VALUE_Fragment * 9);
    prismarine.addItem("blockPrismarineDark", 1, Material.VALUE_Ingot * 2);
    prismarine.setRepresentativeItem(Blocks.PRISMARINE);
    prismarine.addTrait(jagged, HEAD);
    prismarine.addTrait(aquadynamic);

    netherrack.setCraftable(true);
    netherrack.addItemIngot("netherrack");
    netherrack.setRepresentativeItem(Blocks.NETHERRACK);
    netherrack.addTrait(aridiculous, HEAD);
    netherrack.addTrait(hellish);

    endstone.setCraftable(true);
    endstone.addItemIngot("endstone");
    endstone.setRepresentativeItem(Blocks.END_STONE);
    endstone.addTrait(alien, HEAD);
    endstone.addTrait(enderference);

    // item/special resources
    bone.setCraftable(true);
    bone.addItemIngot("bone");
    bone.addItem(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), 1, Material.VALUE_Fragment); // bonemeal
    bone.setRepresentativeItem(Items.BONE);
    bone.addTrait(splintering, HEAD);
    bone.addTrait(fractured);

    paper.setCraftable(true);
    paper.addItem("paper", 1, Material.VALUE_Fragment);
    paper.setRepresentativeItem(Items.PAPER);
    paper.addTrait(writable2, HEAD);
    paper.addTrait(writable);

    sponge.setCraftable(true);
    sponge.addItem(Blocks.SPONGE, Material.VALUE_Ingot);
    sponge.setRepresentativeItem(Blocks.SPONGE);
    sponge.addTrait(squeaky);

    firewood.setCraftable(true);
    firewood.addItem(TinkerCommons.firewood, 1, Material.VALUE_Ingot);
    firewood.setRepresentativeItem(TinkerCommons.firewood);
    firewood.addTrait(autosmelt);

    slime.setCraftable(true);
    slime.addItemIngot("slimecrystalGreen");
    slime.addTrait(slimeyGreen);

    blueslime.setCraftable(true);
    blueslime.addItemIngot("slimecrystalBlue");
    blueslime.addTrait(slimeyBlue);

    knightslime.setCraftable(true);
    knightslime.addItemIngot("ingotKnightslime");
    knightslime.addTrait(crumbling, HEAD);
    knightslime.addTrait(unnatural);

    magmaslime.setCraftable(true);
    magmaslime.addItemIngot("slimecrystalMagma");
    magmaslime.setRepresentativeItem(TinkerCommons.matSlimeCrystalMagma);
    magmaslime.addTrait(superheat, HEAD);
    magmaslime.addTrait(flammable);

    // Metals
    iron.addItemIngot("ingotIron");
    iron.setRepresentativeItem(Items.IRON_INGOT);
    iron.addTrait(magnetic2, HEAD);
    iron.addTrait(magnetic);

    pigiron.addItemIngot("ingotPigiron");
    pigiron.addTrait(baconlicious, HEAD);
    pigiron.addTrait(tasty, HEAD);
    pigiron.addTrait(tasty);

    cobalt.addItemIngot("ingotCobalt");
    cobalt.addTrait(momentum, HEAD);
    cobalt.addTrait(lightweight);

    ardite.addItemIngot("ingotArdite");
    ardite.addTrait(stonebound, HEAD);
    ardite.addTrait(petramor);

    manyullyn.addItemIngot("ingotManyullyn");
    manyullyn.addTrait(insatiable, HEAD);
    manyullyn.addTrait(coldblooded);

    // mod integration
    copper.addItemIngot("ingotCopper");
    copper.addTrait(established);

    bronze.addItemIngot("ingotBronze");
    bronze.addTrait(dense);

    lead.addItemIngot("ingotLead");
    lead.addTrait(poisonous);

    silver.addItemIngot("ingotSilver");
    silver.addTrait(holy);

    electrum.addItemIngot("ingotElectrum");
    electrum.addTrait(shocking);

    steel.addItemIngot("ingotSteel");
    steel.addTrait(sharp, HEAD);
    steel.addTrait(stiff);

    registerToolMaterials();
  }

  private void safeAdd(Material material, ItemStack item, int value) {
    this.safeAdd(material, item, value, false);
  }

  private void safeAddOredicted(Material material, String oredict, ItemStack representative) {
    material.addItem(oredict, 1, Material.VALUE_Ingot);
    material.setRepresentativeItem(representative);
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
                                    new HandleMaterialStats(1.10f, 50),
                                    new ExtraMaterialStats(65));
    TinkerRegistry.addMaterialStats(obsidian,
                                    new HeadMaterialStats(139, 7.07f, 4.20f, COBALT),
                                    new HandleMaterialStats(0.90f, -100),
                                    new ExtraMaterialStats(90));
    TinkerRegistry.addMaterialStats(prismarine,
                                    new HeadMaterialStats(430, 5.50f, 6.00f, IRON),
                                    new HandleMaterialStats(0.60f, -150),
                                    new ExtraMaterialStats(100));
    TinkerRegistry.addMaterialStats(endstone,
                                    new HeadMaterialStats(420, 3.23f, 3.23f, OBSIDIAN),
                                    new HandleMaterialStats(0.85f, 0),
                                    new ExtraMaterialStats(42));
    TinkerRegistry.addMaterialStats(paper,
                                    new HeadMaterialStats(12, 0.51f, 0.05f, STONE),
                                    new HandleMaterialStats(0.10f, 5),
                                    new ExtraMaterialStats(15));
    TinkerRegistry.addMaterialStats(sponge,
                                    new HeadMaterialStats(550, 3.02f, 0.00f, STONE),
                                    new HandleMaterialStats(1.20f, 250),
                                    new ExtraMaterialStats(250));
    TinkerRegistry.addMaterialStats(firewood,
                                    new HeadMaterialStats(550, 6.00f, 5.50f, STONE),
                                    new HandleMaterialStats(1.0f, -200),
                                    new ExtraMaterialStats(150));

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
                                    new HeadMaterialStats(850, 5.8f, 5.10f, OBSIDIAN),
                                    new HandleMaterialStats(0.50f, 500),
                                    new ExtraMaterialStats(125));
    TinkerRegistry.addMaterialStats(magmaslime,
                                    new HeadMaterialStats(600, 2.1f, 7.00f, STONE),
                                    new HandleMaterialStats(0.85f, -200),
                                    new ExtraMaterialStats(150));

    // Nether
    TinkerRegistry.addMaterialStats(netherrack,
                                    new HeadMaterialStats(270, 4.50f, 3.00f, IRON),
                                    new HandleMaterialStats(0.85f, -150),
                                    new ExtraMaterialStats(75));
    TinkerRegistry.addMaterialStats(cobalt,
                                    new HeadMaterialStats(780, 12.00f, 4.10f, COBALT),
                                    new HandleMaterialStats(0.90f, 100),
                                    new ExtraMaterialStats(300));
    TinkerRegistry.addMaterialStats(ardite,
                                    new HeadMaterialStats(990, 3.50f, 3.60f, COBALT),
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

    // Mod Integration
    TinkerRegistry.addMaterialStats(copper,
                                    new HeadMaterialStats(210, 5.30f, 3.00f, IRON),
                                    new HandleMaterialStats(1.05f, 30),
                                    new ExtraMaterialStats(100));

    TinkerRegistry.addMaterialStats(bronze,
                                    new HeadMaterialStats(430, 6.80f, 3.50f, DIAMOND),
                                    new HandleMaterialStats(1.10f, 70),
                                    new ExtraMaterialStats(80));

    TinkerRegistry.addMaterialStats(lead,
                                    new HeadMaterialStats(334, 5.25f, 3.50f, IRON),
                                    new HandleMaterialStats(0.70f, -50),
                                    new ExtraMaterialStats(100));

    TinkerRegistry.addMaterialStats(silver,
                                    new HeadMaterialStats(250, 5.00f, 5.00f, IRON),
                                    new HandleMaterialStats(0.95f, 50),
                                    new ExtraMaterialStats(150));

    TinkerRegistry.addMaterialStats(electrum,
                                    new HeadMaterialStats(50, 12.00f, 3.00f, IRON),
                                    new HandleMaterialStats(1.10f, -25),
                                    new ExtraMaterialStats(250));

    TinkerRegistry.addMaterialStats(steel,
                                    new HeadMaterialStats(540, 7.00f, 6.00f, OBSIDIAN),
                                    new HandleMaterialStats(0.9f, 150),
                                    new ExtraMaterialStats(25));

    //TinkerRegistry.addMaterialStats(xu,         new ToolMaterialStats(97, 1.00f, 1.00f, 0.10f, 0.20f, DIAMOND));
  }

  public void registerBowMaterials() {

  }

  public void registerProjectileMaterials() {

  }

  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    if(TinkerTools.shard == null) {
      return;
    }

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
