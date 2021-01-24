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

  // tier 1
  public static final IMaterial wood  = mat(MaterialIds.wood, true, "8e661b");
  public static final IMaterial flint = mat(MaterialIds.flint, true, "696969");
  public static final IMaterial stone = mat(MaterialIds.stone, true, "999999");
  public static final IMaterial bone  = mat(MaterialIds.bone, true, "ede6bf");
  // tier 2
  public static final IMaterial iron        = mat(MaterialIds.iron, TinkerFluids.moltenIron, false, "cacaca");
  public static final IMaterial searedStone = mat(MaterialIds.searedStone, TinkerFluids.searedStone, false, "3f3f3f");
  public static final IMaterial copper      = mat(MaterialIds.copper, TinkerFluids.moltenCopper, true, "ed9f07");
  public static final IMaterial slimewood   = mat(MaterialIds.slimewood, false, "82c873");
  // tier 3
  public static final IMaterial slimesteel    = mat(MaterialIds.slimesteel, TinkerFluids.moltenSlimesteel, false, "74c8c7");
  public static final IMaterial nahuatl       = mat(MaterialIds.nahuatl, false, "601cc4");
  public static final IMaterial tinkersBronze = mat(MaterialIds.tinkersBronze, TinkerFluids.moltenTinkersBronze, false, "f9cf72");
  public static final IMaterial roseGold      = mat(MaterialIds.roseGold, TinkerFluids.moltenRoseGold, false, "ffdbcc");
  public static final IMaterial pigIron       = mat(MaterialIds.pigIron, TinkerFluids.moltenPigIron, false, "ef9e9b");

  // tier 2 (nether)
  //public static final IMaterial witherBone = mat(MaterialIds.witherBone, true, "ede6bf");
  // tier 3 (nether)
  public static final IMaterial cobalt = mat(MaterialIds.cobalt, TinkerFluids.moltenCobalt, false, "2882d4");
  // tier 4
  public static final IMaterial manyullyn   = mat(MaterialIds.manyullyn, TinkerFluids.moltenManyullyn, false, "a15cf8");
  public static final IMaterial hepatizon   = mat(MaterialIds.hepatizon, TinkerFluids.moltenHepatizon, false, "60496b");
  public static final IMaterial slimeBronze = mat(MaterialIds.slimeBronze, TinkerFluids.moltenSlimeBronze, false, "ff960d");
  public static final IMaterial soulsteel   = mat(MaterialIds.soulsteel, TinkerFluids.moltenSoulsteel, false, "6a5244");

  // tier 2 (end)
  public static final IMaterial endstone = mat(MaterialIds.endstone, true, "e0d890");

  // tier 2 (mod integration)
  public static final IMaterial lead = mat(MaterialIds.lead, false, "4d4968");
  public static final IMaterial silver = mat(MaterialIds.silver, false, "d1ecf6");
  // tier 3 (mod integration)
  public static final IMaterial electrum = mat(MaterialIds.electrum, false, "e8db49");
  public static final IMaterial bronze = mat(MaterialIds.bronze, false, "e3bd68");
  public static final IMaterial steel = mat(MaterialIds.steel, false, "a7a7a7");

  // bowstring IMaterials
//  public static final IMaterial string = mat(MaterialIds.string, true, "eeeeee");
//  public static final IMaterial vine = mat(MaterialIds.vine, true, "40a10f");
//  public static final IMaterial slimevine_blue = mat(MaterialIds.slimevine_blue, true, "74c8c7");
//  public static final IMaterial slimevine_purple = mat(MaterialIds.slimevine_purple, true, "c873c8");

  /** Creates a material with a fluid */
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
