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

  // natural resources/blocks
  public static final IMaterial wood = mat(MaterialIds.wood, true, "8e661b");
  public static final IMaterial stone = mat(MaterialIds.stone, true, "999999");
  public static final IMaterial flint = mat(MaterialIds.flint, true, "696969");
  public static final IMaterial cactus = mat(MaterialIds.cactus, true, "00a10f");
  public static final IMaterial bone = mat(MaterialIds.bone, true, "ede6bf");
  public static final IMaterial obsidian = mat(MaterialIds.obsidian, TinkerFluids.moltenObsidian, true, "601cc4");
  public static final IMaterial prismarine = mat(MaterialIds.prismarine, true, "7edebc");
  public static final IMaterial endstone = mat(MaterialIds.endstone, true, "e0d890");
  public static final IMaterial paper = mat(MaterialIds.paper, true, "ffffff");
  public static final IMaterial sponge = mat(MaterialIds.sponge, true, "cacc4e");
  public static final IMaterial firewood = mat(MaterialIds.firewood, true, "cc5300");

  // Slime
  public static final IMaterial knightslime = mat(MaterialIds.knightslime, TinkerFluids.moltenKnightslime, false, "f18ff0");
  public static final IMaterial slime = mat(MaterialIds.slime, true, "82c873");
  public static final IMaterial blueslime = mat(MaterialIds.blueslime, true, "74c8c7");
  public static final IMaterial magmaslime = mat(MaterialIds.magmaslime, true, "ff960d");

  // Metals
  public static final IMaterial iron = mat(MaterialIds.iron, TinkerFluids.moltenIron, false, "cacaca");
  public static final IMaterial copper = mat(MaterialIds.copper, TinkerFluids.moltenCopper, false, "ed9f07");
  public static final IMaterial pigiron = mat(MaterialIds.pigiron, TinkerFluids.moltenPigIron, false, "ef9e9b");

  // Nether Materials
  public static final IMaterial netherrack = mat(MaterialIds.netherrack, true, "b84f4f");
  public static final IMaterial ardite = mat(MaterialIds.ardite, TinkerFluids.moltenArdite, false, "d14210");
  public static final IMaterial cobalt = mat(MaterialIds.cobalt, TinkerFluids.moltenCobalt, false, "2882d4");
  public static final IMaterial manyullyn = mat(MaterialIds.manyullyn, TinkerFluids.moltenManyullyn, false, "a15cf8");

  // mod integration
  public static final IMaterial bronze = mat(MaterialIds.bronze, false, "e3bd68");
  public static final IMaterial lead = mat(MaterialIds.lead, false, "4d4968");
  public static final IMaterial silver = mat(MaterialIds.silver, false, "d1ecf6");
  public static final IMaterial electrum = mat(MaterialIds.electrum, false, "e8db49");
  public static final IMaterial steel = mat(MaterialIds.steel, false, "a7a7a7");

  // bowstring IMaterials
  public static final IMaterial string = mat(MaterialIds.string, true, "eeeeee");
  public static final IMaterial vine = mat(MaterialIds.vine, true, "40a10f");
  public static final IMaterial slimevine_blue = mat(MaterialIds.slimevine_blue, true, "74c8c7");
  public static final IMaterial slimevine_purple = mat(MaterialIds.slimevine_purple, true, "c873c8");

  // additional arrow shaft
  public static final IMaterial blaze = mat(MaterialIds.blaze, true, "ffc100");
  public static final IMaterial reed = mat(MaterialIds.reed, true, "aadb74");
  public static final IMaterial ice = mat(MaterialIds.ice, true, "97d7e0");
  public static final IMaterial endrod = mat(MaterialIds.endrod, true, "e8ffd6");

  // fletching
  public static final IMaterial feather = mat(MaterialIds.feather, true, "eeeeee");
  public static final IMaterial leaf = mat(MaterialIds.leaf, true, "1d730c");
  public static final IMaterial slimeleaf_blue = mat(MaterialIds.slimeleaf_blue, true, "74c8c7");
  public static final IMaterial slimeleaf_orange = mat(MaterialIds.slimeleaf_orange, true, "ff960d");
  public static final IMaterial slimeleaf_purple = mat(MaterialIds.slimeleaf_purple, true, "c873c8");

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
