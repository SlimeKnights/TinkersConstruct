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

  //Tier 1
  public static final IMaterial wood = mat(MaterialIds.wood, true, "8e661b");
  public static final IMaterial bone = mat(MaterialIds.bone, true, "ede6bf");
  public static final IMaterial stone = mat(MaterialIds.stone, true, "999999");
  public static final IMaterial paper = mat(MaterialIds.paper, true, "ffffff");
  public static final IMaterial flint = mat(MaterialIds.flint, true, "696969");
  public static final IMaterial coral = mat(MaterialIds.coral, true, "ffef38");

  //Tier 2
  public static final IMaterial copper = mat(MaterialIds.copper, false, "ed9f07");
  public static final IMaterial searedstone = mat(MaterialIds.searedstone, false, "ed9f07");
  public static final IMaterial iron = mat(MaterialIds.iron, false, "cacaca");
  public static final IMaterial slimewood = mat(MaterialIds.slimewood, false, "82c873");
  public static final IMaterial slimestone = mat(MaterialIds.slimestone, false, "82c873");

  //Tier 2 alternate
  public static final IMaterial mushwood = mat(MaterialIds.mushwood, true, "24bad5");
  public static final IMaterial bloodwood = mat(MaterialIds.bloodwood, true, "d53024");
  public static final IMaterial netherrack = mat(MaterialIds.netherrack, true, "b84f4f");
  public static final IMaterial blackstone = mat(MaterialIds.blackstone, true, "333333");
  public static final IMaterial basalt = mat(MaterialIds.basalt, true, "cfcfcf");
  public static final IMaterial witherbone = mat(MaterialIds.witherbone, true, "16202e");

  //Tier 3
  public static final IMaterial slimesteel = mat(MaterialIds.slimesteel, false, "74c8c7");
  public static final IMaterial nahuatl = mat(MaterialIds.nahuatl, false, "601cc4");
  public static final IMaterial bronze = mat(MaterialIds.bronze, false, "e3bd68");
  public static final IMaterial pigiron = mat(MaterialIds.pigiron, false, "ef9e9b");
  public static final IMaterial rosegold = mat(MaterialIds.rosegold, false, "ff5a89");
  public static final IMaterial ravagersteel = mat(MaterialIds.ravagersteel, false, "ff5a89");

  public static final IMaterial cobalt = mat(MaterialIds.cobalt, false, "2882d4");
  public static final IMaterial endstone = mat(MaterialIds.endstone, true, "e0d890");
  public static final IMaterial chorus = mat(MaterialIds.chorus, true, "937596");

  //Tier 4
  public static final IMaterial soulsteel = mat(MaterialIds.soulsteel, false, "6a3723");
  public static final IMaterial heptazion = mat(MaterialIds.heptazion, false, "601cc4");
  public static final IMaterial slimebronze = mat(MaterialIds.slimebronze, false, "e3bd68");
  public static final IMaterial blazewood = mat(MaterialIds.blazewood, true, "e0d890");

  //Tier 5
  public static final IMaterial manyullyn = mat(MaterialIds.manyullyn, false, "a15cf8");
  public static final IMaterial knightslime = mat(MaterialIds.knightslime, false, "f18ff0");
  public static final IMaterial knightmetal = mat(MaterialIds.knightmetal, false, "cbc9b3");
  public static final IMaterial rainbowslime = mat(MaterialIds.rainbowslime, true, "f580ff");
  public static final IMaterial alexandrite = mat(MaterialIds.alexandrite, false, "52c492");
  public static final IMaterial gardite = mat(MaterialIds.gardite, false, "49b83d");

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
