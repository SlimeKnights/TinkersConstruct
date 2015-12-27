package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import slimeknights.mantle.pulsar.pulse.Pulse;
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
import slimeknights.tconstruct.smeltery.TinkerFluids;
import slimeknights.tconstruct.tools.modifiers.traits.TraitAlien;
import slimeknights.tconstruct.tools.modifiers.traits.TraitAquadynamic;
import slimeknights.tconstruct.tools.modifiers.traits.TraitAridiculous;
import slimeknights.tconstruct.tools.modifiers.traits.TraitCheap;
import slimeknights.tconstruct.tools.modifiers.traits.TraitCrude;
import slimeknights.tconstruct.tools.modifiers.traits.TraitDuritos;
import slimeknights.tconstruct.tools.modifiers.traits.TraitEcological;
import slimeknights.tconstruct.tools.modifiers.traits.TraitInsatiable;
import slimeknights.tconstruct.tools.modifiers.traits.TraitMagnetic;
import slimeknights.tconstruct.tools.modifiers.traits.TraitMomentum;
import slimeknights.tconstruct.tools.modifiers.traits.TraitPetramor;
import slimeknights.tconstruct.tools.modifiers.traits.TraitPrickly;
import slimeknights.tconstruct.tools.modifiers.traits.TraitSlimey;
import slimeknights.tconstruct.tools.modifiers.traits.TraitSplintering;
import slimeknights.tconstruct.tools.modifiers.traits.TraitSqueaky;
import slimeknights.tconstruct.tools.modifiers.traits.TraitStonebound;
import slimeknights.tconstruct.tools.modifiers.traits.TraitTasty;
import slimeknights.tconstruct.tools.modifiers.traits.TraitUnnatural;

import static slimeknights.tconstruct.library.utils.HarvestLevels.COBALT;
import static slimeknights.tconstruct.library.utils.HarvestLevels.DIAMOND;
import static slimeknights.tconstruct.library.utils.HarvestLevels.IRON;
import static slimeknights.tconstruct.library.utils.HarvestLevels.OBSIDIAN;
import static slimeknights.tconstruct.library.utils.HarvestLevels.STONE;

/**
 * All the tool materials tcon supports.
 */
@Pulse(id = TinkerMaterials.PulseId, description = "All the tool materials added by TConstruct", pulsesRequired = TinkerTools.PulseId)
public final class TinkerMaterials {

  static final String PulseId = "TinkerMaterials";
  static final Logger log = Util.getLogger(PulseId);

  public static final List<Material> materials = Lists.newArrayList();
  private static final Map<Material, String> materialPrerequisite = Maps.newHashMap();

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
  public static final Material knightslime= mat("knightslime", 0xf18ff0, "ingotKnightslime");
  public static final Material slime      = mat("slime", 0x82c873);
  public static final Material blueslime  = mat("blueslime", 0x74c8c7, "slimeballBlue");

  // Metals
  public static final Material iron       = mat("iron", 0xcacaca);
  public static final Material pigiron    = mat("pigiron", 0xef9e9b, "ingotPigiron");
  public static final Material copper     = mat("copper", 0xed9f07, "ingotCopper");
  public static final Material bronze     = mat("bronze", 0xd2a869, "ingotBronze");

  // Nether Materials
  public static final Material netherrack = mat("netherrack", 0xb84f4f);
  public static final Material ardite     = mat("ardite", 0xd14210, "ingotArdite");
  public static final Material cobalt     = mat("cobalt", 0x2882d4, "ingotCobalt");
  public static final Material manyullyn  = mat("manyullyn", 0xa15cf8, "ingotManyullyn");

  // specul
  public static final Material xu;

  public static final AbstractTrait alien = new TraitAlien();
  public static final AbstractTrait aquadynamic = new TraitAquadynamic();
  public static final AbstractTrait aridiculous = new TraitAridiculous();
  public static final AbstractTrait cheap = new TraitCheap();
  public static final AbstractTrait crude = new TraitCrude();
  public static final AbstractTrait duritos = new TraitDuritos(); // yes you read that correctly
  public static final AbstractTrait ecological = new TraitEcological();
  public static final AbstractTrait insatiable = new TraitInsatiable();
  public static final AbstractTrait magnetic = new TraitMagnetic();
  public static final AbstractTrait momentum = new TraitMomentum();
  public static final AbstractTrait petramor = new TraitPetramor();
  public static final AbstractTrait prickly = new TraitPrickly();
  public static final AbstractTrait slimeyGreen = new TraitSlimey(EntitySlime.class);
  public static final AbstractTrait slimeyBlue = new TraitSlimey(EntitySlime.class); // todo: blue slime
  public static final AbstractTrait splintering = new TraitSplintering();
  public static final AbstractTrait squeaky = new TraitSqueaky();
  public static final AbstractTrait stonebound = new TraitStonebound();
  public static final AbstractTrait tasty = new TraitTasty();
  public static final AbstractTrait unnatural = new TraitUnnatural();

  private static Material mat(String name, int color) {
    return mat(name, color, null);
  }

  private static Material mat(String name, int color, String oredict) {
    Material mat = new Material(name, color);
    materials.add(mat);
    if(oredict != null)
      materialPrerequisite.put(mat, oredict);
    return mat;
  }
  
  static {
    xu = new Material("unstable", EnumChatFormatting.WHITE);
  }

  private void registerMaterials() {
    for(Material material : materials) {
      // has a prerequisite?
      if(materialPrerequisite.containsKey(material)) {
        String oredict = materialPrerequisite.get(material);
        boolean found = false;
        // we use this method because it doesn't add empty entries to the oredict, even though it is less performant
        for(String ore : OreDictionary.getOreNames()) {
          if(ore.equals(oredict)) {
            found = true;
            break;
          }
        }

        // prerequisite not fulfilled
        if(!found) {
          log.debug("Material %s was not registered due to missing oredict entry: ", material.getIdentifier(), oredict);
          continue;
        }
      }
      TinkerRegistry.addMaterial(material);
    }
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
    registerMaterials();

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
    flint.addItem(Items.flint, 1, Material.VALUE_Ingot);
    flint.setRepresentativeItem(new ItemStack(Items.flint));
    flint.addTrait(crude);

    cactus.setCraftable(true);
    cactus.addItem("blockCactus", 1, Material.VALUE_Ingot);
    cactus.setRepresentativeItem(new ItemStack(Blocks.cactus));
    cactus.addTrait(prickly);

    obsidian.setFluid(TinkerFluids.obsidian);
    obsidian.setCraftable(true);
    obsidian.setCastable(true);
    obsidian.addItem(Blocks.obsidian, Material.VALUE_Ingot);
    obsidian.setRepresentativeItem(new ItemStack(Blocks.obsidian));
    obsidian.addTrait(duritos);

    prismarine.setCraftable(true);
    prismarine.addItem(Items.prismarine_shard, 1, Material.VALUE_Fragment);
    prismarine.addItem(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.ROUGH_META), 1, Material.VALUE_Ingot);
    prismarine.addItem(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.BRICKS_META), 1, Material.VALUE_Fragment*9);
    prismarine.addItem(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.DARK_META), 1, Material.VALUE_Ingot*2);
    prismarine.setRepresentativeItem(Blocks.prismarine);
    prismarine.addTrait(aquadynamic);

    netherrack.setCraftable(true);
    netherrack.addItem(Blocks.netherrack, Material.VALUE_Ingot);
    netherrack.setRepresentativeItem(Blocks.netherrack);
    netherrack.addTrait(aridiculous);

    endstone.setCraftable(true);
    endstone.addItem(Blocks.end_stone, Material.VALUE_Ingot);
    endstone.setRepresentativeItem(Blocks.end_stone);
    endstone.addTrait(alien);

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
    safeAdd(blueslime, TinkerCommons.matSlimeCrystalBlue, Material.VALUE_Ingot, true);
    blueslime.addTrait(slimeyBlue);

    knightslime.setCraftable(true);
    safeAdd(knightslime, TinkerCommons.ingotKnightSlime, Material.VALUE_Ingot, true);
    knightslime.addTrait(unnatural);

    // Metals
    iron.setFluid(TinkerFluids.iron);
    iron.setCastable(true);
    iron.addItem("ingotIron", 1, Material.VALUE_Ingot);
    iron.setRepresentativeItem(Items.iron_ingot);
    iron.addTrait(magnetic);
    // todo: remaining metals

    pigiron.setFluid(TinkerFluids.pigIron);
    pigiron.setCastable(true);
    pigiron.addTrait(tasty);

    cobalt.setFluid(TinkerFluids.cobalt);
    cobalt.setCastable(true);
    safeAdd(cobalt, TinkerCommons.ingotCobalt, Material.VALUE_Ingot, true);
    cobalt.addTrait(momentum);

    ardite.setFluid(TinkerFluids.ardite);
    ardite.setCastable(true);
    safeAdd(ardite, TinkerCommons.ingotArdite, Material.VALUE_Ingot, true);
    ardite.addTrait(petramor);

    manyullyn.setFluid(TinkerFluids.manyullyn);
    manyullyn.setCastable(true);
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
    TinkerRegistry.addMaterialStats(wood,       new ToolMaterialStats(  73, 3.40f, 2.00f, 0.75f, 0.50f, STONE));
    TinkerRegistry.addMaterialStats(stone,      new ToolMaterialStats( 131, 3.80f, 2.10f, 0.05f, 0.18f, IRON));
    TinkerRegistry.addMaterialStats(flint,      new ToolMaterialStats( 235, 5.00f, 2.80f, 0.20f, 0.19f, IRON));
    TinkerRegistry.addMaterialStats(cactus,     new ToolMaterialStats( 329, 4.50f, 3.40f, 0.25f, 0.43f, IRON));
    TinkerRegistry.addMaterialStats(bone,       new ToolMaterialStats( 373, 5.09f, 2.50f, 0.81f, 0.56f, IRON));
    TinkerRegistry.addMaterialStats(obsidian,   new ToolMaterialStats(  89, 7.07f, 4.20f, 0.07f, 0.24f, COBALT));
    TinkerRegistry.addMaterialStats(prismarine, new ToolMaterialStats( 530, 5.50f, 5.50f, 0.18f, 0.84f, IRON));
    TinkerRegistry.addMaterialStats(endstone,   new ToolMaterialStats( 412, 3.23f, 3.23f, 0.33f, 0.33f, OBSIDIAN));
    TinkerRegistry.addMaterialStats(paper,      new ToolMaterialStats(  42, 0.51f, 0.05f, 0.01f, 0.70f, STONE));
    TinkerRegistry.addMaterialStats(sponge,     new ToolMaterialStats( 650, 3.02f, 0.00f, 0.05f, 0.01f, STONE));

    // Slime
    TinkerRegistry.addMaterialStats(slime,      new ToolMaterialStats( 600, 4.24f, 1.80f, 0.30f, 1.00f, STONE));
    TinkerRegistry.addMaterialStats(blueslime,  new ToolMaterialStats( 780, 4.03f, 1.80f, 1.00f, 0.15f, STONE));
    TinkerRegistry.addMaterialStats(knightslime,new ToolMaterialStats( 902, 3.81f, 5.10f, 0.76f, 0.46f, OBSIDIAN));

    // Nether
    TinkerRegistry.addMaterialStats(netherrack, new ToolMaterialStats( 322, 4.89f, 3.00f, 0.10f, 0.27f, IRON));
    TinkerRegistry.addMaterialStats(cobalt,     new ToolMaterialStats( 680,10.00f, 4.10f, 0.40f, 0.60f, COBALT));
    TinkerRegistry.addMaterialStats(ardite,     new ToolMaterialStats( 989, 2.42f, 3.60f, 0.64f, 0.78f, COBALT));
    TinkerRegistry.addMaterialStats(manyullyn,  new ToolMaterialStats( 513, 7.02f, 8.72f, 0.30f, 0.70f, COBALT));

    // Metals
    TinkerRegistry.addMaterialStats(iron,       new ToolMaterialStats( 275, 5.70f, 6.00f, 0.50f, 0.60f, DIAMOND));
    TinkerRegistry.addMaterialStats(pigiron,    new ToolMaterialStats( 380, 6.20f, 4.50f, 0.66f, 0.73f, OBSIDIAN));

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
