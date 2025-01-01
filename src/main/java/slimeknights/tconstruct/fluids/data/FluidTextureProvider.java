package slimeknights.tconstruct.fluids.data;

import net.minecraft.data.PackOutput;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;
import slimeknights.mantle.fluid.texture.FluidTexture;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;

import static slimeknights.tconstruct.TConstruct.getResource;
import static slimeknights.tconstruct.fluids.TinkerFluids.withoutMolten;

@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
public class FluidTextureProvider extends AbstractFluidTextureProvider {
  public FluidTextureProvider(PackOutput packOutput) {
    super(packOutput, TConstruct.MOD_ID);
  }

  @Override
  public void addTextures() {
    // basic
    root(TinkerFluids.powderedSnow);
    root(TinkerFluids.potion).color(0xfff800f8);
    // slime
    slime(TinkerFluids.earthSlime, "earth");
    slime(TinkerFluids.skySlime, "sky");
    slime(TinkerFluids.enderSlime, "ender");
    slime(TinkerFluids.magma);
    slime(TinkerFluids.venom);
    slime(TinkerFluids.liquidSoul, "soul");
    // food
    folder(TinkerFluids.honey, "food");
    tintedStew(TinkerFluids.beetrootSoup).color(0xFF84160D);
    tintedStew(TinkerFluids.mushroomStew).color(0xFFCD8C6F);
    tintedStew(TinkerFluids.rabbitStew).color(0xFF984A2C);
    tintedStew(TinkerFluids.meatSoup).color(0xFFE03E35);

    // molten
    molten(TinkerFluids.moltenGlass);
    named(TinkerFluids.blazingBlood, "molten/blaze");
    // stone
    tintedStone(TinkerFluids.searedStone).color(0xFF4F4A47);
    tintedStone(TinkerFluids.scorchedStone).color(0xFF3E3029);
    tintedStone(TinkerFluids.moltenClay).color(0xFF9B6045);
    stone(TinkerFluids.moltenPorcelain);
    stone(TinkerFluids.moltenObsidian);
    tintedStone(TinkerFluids.moltenEnder).color(0xFF105E51);

    // ore - non-metal
    ore(TinkerFluids.moltenDiamond);
    ore(TinkerFluids.moltenEmerald);
    ore(TinkerFluids.moltenAmethyst);
    ore(TinkerFluids.moltenQuartz);
    tintedStone(TinkerFluids.moltenDebris).color(0xFF411E15);
    // ore - tinkers
    ore(TinkerFluids.moltenCopper);
    ore(TinkerFluids.moltenIron);
    ore(TinkerFluids.moltenGold);
    ore(TinkerFluids.moltenCobalt);

    // alloy - overworld
    alloy(TinkerFluids.moltenSlimesteel);
    alloy(TinkerFluids.moltenAmethystBronze);
    alloy(TinkerFluids.moltenPigIron);
    alloy(TinkerFluids.moltenRoseGold);
    // alloy - nether
    alloy(TinkerFluids.moltenManyullyn);
    alloy(TinkerFluids.moltenHepatizon);
    alloy(TinkerFluids.moltenQueensSlime);
    alloy(TinkerFluids.moltenNetherite);
    // alloy - end
    alloy(TinkerFluids.moltenSoulsteel);
    alloy(TinkerFluids.moltenKnightslime);

    // compat - ore
    compatOre(TinkerFluids.moltenAluminum);
    compatOre(TinkerFluids.moltenLead);
    compatOre(TinkerFluids.moltenNickel);
    compatOre(TinkerFluids.moltenOsmium);
    compatOre(TinkerFluids.moltenPlatinum);
    compatOre(TinkerFluids.moltenSilver);
    compatOre(TinkerFluids.moltenTin);
    compatOre(TinkerFluids.moltenTungsten);
    compatOre(TinkerFluids.moltenUranium);
    compatOre(TinkerFluids.moltenZinc);
    // compat - alloy
    compatAlloy(TinkerFluids.moltenBrass);
    compatAlloy(TinkerFluids.moltenBronze);
    compatAlloy(TinkerFluids.moltenConstantan);
    compatAlloy(TinkerFluids.moltenElectrum);
    compatAlloy(TinkerFluids.moltenInvar);
    compatAlloy(TinkerFluids.moltenPewter);
    compatAlloy(TinkerFluids.moltenSteel);
    // thermal
    compatAlloy(TinkerFluids.moltenEnderium);
    compatAlloy(TinkerFluids.moltenLumium);
    compatAlloy(TinkerFluids.moltenSignalum);
    // mekanism
    compatAlloy(TinkerFluids.moltenRefinedObsidian);
    compatAlloy(TinkerFluids.moltenRefinedGlowstone);
  }


  /* Helpers */

  /** Creates a texture in the root folder */
  private FluidTexture.Builder root(FluidObject<?> fluid) {
    return texture(fluid).wrapId("fluid/", "/", false, false);
  }

  /** Creates a texture using the fluid's ID in the given folder */
  private FluidTexture.Builder folder(FluidObject<?> fluid, String folder) {
    return texture(fluid).wrapId("fluid/"+folder+"/", "/", false, false);
  }

  /** Creates a texture using the given fixed name in the fluid folder */
  private FluidTexture.Builder named(FluidObject<?> fluid, String name) {
    return texture(fluid).textures(getResource("fluid/"+name+"/"), false, false);
  }

  /** Creates a texture in the slime folder using the ID */
  private FluidTexture.Builder slime(FluidObject<?> fluid) {
    return folder(fluid, "slime");
  }

  /** Creates a texture with the given name in the slime folder */
  private FluidTexture.Builder slime(FluidObject<?> fluid, String name) {
    return named(fluid, "slime/"+name);
  }


  /* Molten */

  /** Creates a texture in the molten using the fluid ID (stripping molten) */
  private FluidTexture.Builder molten(FluidObject<?> fluid) {
    return named(fluid, "molten/" + withoutMolten(fluid));
  }

  /** Creates a texture in given subfolder of molten, stripping molten from the name */
  private FluidTexture.Builder moltenFolder(FluidObject<?> fluid, String folder) {
    return named(fluid, "molten/" + folder + "/" + withoutMolten(fluid));
  }

  /** Creates a texture in the molten stone folder using the given name */
  private FluidTexture.Builder stone(FluidObject<?> fluid) {
    return moltenFolder(fluid, "stone");
  }

  /** Creates a texture in the ore folder using the given name */
  private FluidTexture.Builder ore(FluidObject<?> fluid) {
    return moltenFolder(fluid, "ore");
  }

  /** Creates a texture in the alloy folder using the given name */
  private FluidTexture.Builder alloy(FluidObject<?> fluid) {
    return moltenFolder(fluid, "alloy");
  }

  /** Creates a texture in the compat ore folder using the given name */
  private FluidTexture.Builder compatOre(FluidObject<?> fluid) {
    return moltenFolder(fluid, "compat_ore");
  }

  /** Creates a texture in the compat alloy folder using the given name */
  private FluidTexture.Builder compatAlloy(FluidObject<?> fluid) {
    return moltenFolder(fluid, "compat_alloy");
  }


  /* Tinted textures */

  /** Builder with the stew texture */
  private FluidTexture.Builder tintedStew(FluidObject<?> fluid) {
    return named(fluid, "food/stew");
  }

  /** Builder with the stone texture */
  private FluidTexture.Builder tintedStone(FluidObject<?> fluid) {
    return named(fluid, "molten/stone");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Fluid Texture Providers";
  }
}
