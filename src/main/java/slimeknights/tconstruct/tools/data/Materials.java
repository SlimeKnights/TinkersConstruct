package slimeknights.tconstruct.tools.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Materials {
  static final List<IMaterial> allMaterials = new ArrayList<>();
  //TODO: How do we define composite materials?

  //Tier 1
  public static final IMaterial wood = mat(MaterialIds.wood, true, "8e661b");
  public static final IMaterial bone = mat(MaterialIds.bone, true, "ede6bf");
  public static final IMaterial stone = mat(MaterialIds.stone, true, "999999");
  public static final IMaterial coral = mat(MaterialIds.coral, true, "ffef38");
  public static final IMaterial flint = mat(MaterialIds.flint, true, "696969");
  //public static final IMaterial paper = mat(MaterialIds.paper, true, "ffffff");

  //Tier 2
  public static final IMaterial copper = mat(MaterialIds.copper, TinkerFluids.moltenCopper, true, "ed9f07");
  public static final IMaterial searedstone = mat(MaterialIds.searedstone, TinkerFluids.searedStone, false, "ed9f07");
  public static final IMaterial iron = mat(MaterialIds.iron, TinkerFluids.moltenIron, false, "cacaca");
  public static final IMaterial slimewood = mat(MaterialIds.slimewood, true, "82c873"); //Compsite: Green Slime -> Wood
  public static final IMaterial slimestone = mat(MaterialIds.slimestone, true, "82c873"); //Compsite: Green Slime -> Stone

  //Tier 2 alternate
  public static final IMaterial mushwood = mat(MaterialIds.mushwood, true, "24bad5");
  public static final IMaterial blackstone = mat(MaterialIds.blackstone, true, "333333");
  public static final IMaterial basalt = mat(MaterialIds.basalt, true, "cfcfcf");
  public static final IMaterial bloodwood = mat(MaterialIds.bloodwood, true, "d53024");
  public static final IMaterial witherbone = mat(MaterialIds.witherbone, true, "16202e");

  //Tier 3
  public static final IMaterial slimesteel = mat(MaterialIds.slimesteel, TinkerFluids.moltenSlimeSteel,false, "74c8c7");
  public static final IMaterial nahuatl = mat(MaterialIds.nahuatl, false, "601cc4"); //Compsite: Obsidian -> Wood
  public static final IMaterial ravagersteel = mat(MaterialIds.ravagersteel, TinkerFluids.moltenRavagerSteel, false, "ff5a89");
  public static final IMaterial bronze = mat(MaterialIds.bronze, TinkerFluids.moltenBronze, false, "e3bd68");
  public static final IMaterial pigiron = mat(MaterialIds.pigiron, TinkerFluids.moltenPigIron, false, "ef9e9b");
  public static final IMaterial rosegold = mat(MaterialIds.rosegold, TinkerFluids.moltenRoseGold, false, "ff5a89");

  public static final IMaterial cobalt = mat(MaterialIds.cobalt, TinkerFluids.moltenCobalt, false, "2882d4");
  public static final IMaterial endstone = mat(MaterialIds.endstone, true, "e0d890");
  public static final IMaterial chorus = mat(MaterialIds.chorus, true, "937596");

  //Tier 4
  public static final IMaterial soulsteel = mat(MaterialIds.soulsteel, TinkerFluids.moltenSoulSteel, false, "6a3723");
  public static final IMaterial alexandrite = mat(MaterialIds.alexandrite, true, "52c492");
  public static final IMaterial heptazion = mat(MaterialIds.heptazion, TinkerFluids.moltenHeptazion, false, "601cc4");
  public static final IMaterial magmastone = mat(MaterialIds.magmastone, TinkerFluids.magmastone, true, "b0330c");
  public static final IMaterial knightmetal = mat(MaterialIds.knightmetal, TinkerFluids.moltenKnightMetal, false, "cbc9b3");
  public static final IMaterial slimebronze = mat(MaterialIds.slimebronze, TinkerFluids.moltenSlimeBronze, false, "e3bd68");
  public static final IMaterial blazewood = mat(MaterialIds.blazewood, true, "e0d890"); //Compsite: Liquid Blaze -> Wood

  //Tier 5
  public static final IMaterial manyullyn = mat(MaterialIds.manyullyn, TinkerFluids.moltenManyullyn, false, "a15cf8");
  public static final IMaterial knightslime = mat(MaterialIds.knightslime, TinkerFluids.moltenKnightslime, false, "f18ff0");
  public static final IMaterial rainbowslime = mat(MaterialIds.rainbowslime, TinkerFluids.rainbowSlime, true, "f580ff");
  public static final IMaterial dragonstone = mat(MaterialIds.dragonstone, TinkerFluids.dragonstone, true, "a2007a");
  public static final IMaterial gardite = mat(MaterialIds.gardite, false, "49b83d"); //Compsite: Rainbow Slime -> Diorite

  /**
   * Creates a material with a fluid
   */
  private static IMaterial mat(MaterialId location, Supplier<? extends Fluid> fluid, boolean craftable, String materialColor) {
    IMaterial material = new DataMaterial(location, fluid, craftable, materialColor);
    allMaterials.add(material);
    return material;
  }

  /** Creates a material with no fluid */
  private static IMaterial mat(MaterialId location, boolean craftable, String materialColor) {
    return mat(location, () -> Fluids.EMPTY, craftable, materialColor);
  }
}
