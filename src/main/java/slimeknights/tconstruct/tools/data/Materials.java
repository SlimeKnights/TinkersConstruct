package slimeknights.tconstruct.tools.data;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;

import java.util.ArrayList;
import java.util.List;

final class Materials {

  static final List<IMaterial> allMaterials = new ArrayList<>();

  // natural resources/blocks
  public static final IMaterial wood = mat(MaterialIds.wood, Fluids.EMPTY, true, "8e661b");
  public static final IMaterial stone = mat(MaterialIds.stone, Fluids.EMPTY, true, "999999");
  public static final IMaterial flint = mat(MaterialIds.flint, Fluids.EMPTY, true, "696969");
  public static final IMaterial cactus = mat(MaterialIds.cactus, Fluids.EMPTY, true, "00a10f");
  public static final IMaterial bone = mat(MaterialIds.bone, Fluids.EMPTY, true, "ede6bf");
  public static final IMaterial obsidian = mat(MaterialIds.obsidian, Fluids.EMPTY, true, "601cc4");
  public static final IMaterial prismarine = mat(MaterialIds.prismarine, Fluids.EMPTY, true, "7edebc");
  public static final IMaterial endstone = mat(MaterialIds.endstone, Fluids.EMPTY, true, "e0d890");
  public static final IMaterial paper = mat(MaterialIds.paper, Fluids.EMPTY, true, "ffffff");
  public static final IMaterial sponge = mat(MaterialIds.sponge, Fluids.EMPTY, true, "cacc4e");
  public static final IMaterial firewood = mat(MaterialIds.firewood, Fluids.EMPTY, true, "cc5300");

  // Slime
  public static final IMaterial knightslime = mat(MaterialIds.knightslime, Fluids.EMPTY, false, "f18ff0");
  public static final IMaterial slime = mat(MaterialIds.slime, Fluids.EMPTY, true, "82c873");
  public static final IMaterial blueslime = mat(MaterialIds.blueslime, Fluids.EMPTY, true, "74c8c7");
  public static final IMaterial magmaslime = mat(MaterialIds.magmaslime, Fluids.EMPTY, true, "ff960d");

  // Metals
  public static final IMaterial iron = mat(MaterialIds.iron, Fluids.EMPTY, false, "cacaca");
  public static final IMaterial pigiron = mat(MaterialIds.pigiron, Fluids.EMPTY, false, "ef9e9b");

  // Nether Materials
  public static final IMaterial netherrack = mat(MaterialIds.netherrack, Fluids.EMPTY, true, "b84f4f");
  public static final IMaterial ardite = mat(MaterialIds.ardite, Fluids.EMPTY, false, "d14210");
  public static final IMaterial cobalt = mat(MaterialIds.cobalt, Fluids.EMPTY, false, "2882d4");
  public static final IMaterial manyullyn = mat(MaterialIds.manyullyn, Fluids.EMPTY, false, "a15cf8");

  // mod integration
  public static final IMaterial copper = mat(MaterialIds.copper, Fluids.EMPTY, false, "ed9f07");
  public static final IMaterial bronze = mat(MaterialIds.bronze, Fluids.EMPTY, false, "e3bd68");
  public static final IMaterial lead = mat(MaterialIds.lead, Fluids.EMPTY, false, "4d4968");
  public static final IMaterial silver = mat(MaterialIds.silver, Fluids.EMPTY, false, "d1ecf6");
  public static final IMaterial electrum = mat(MaterialIds.electrum, Fluids.EMPTY, false, "e8db49");
  public static final IMaterial steel = mat(MaterialIds.steel, Fluids.EMPTY, false, "a7a7a7");

  // bowstring IMaterials
  public static final IMaterial string = mat(MaterialIds.string, Fluids.EMPTY, true, "eeeeee");
  public static final IMaterial vine = mat(MaterialIds.vine, Fluids.EMPTY, true, "40a10f");
  public static final IMaterial slimevine_blue = mat(MaterialIds.slimevine_blue, Fluids.EMPTY, true, "74c8c7");
  public static final IMaterial slimevine_purple = mat(MaterialIds.slimevine_purple, Fluids.EMPTY, true, "c873c8");

  // additional arrow shaft
  public static final IMaterial blaze = mat(MaterialIds.blaze, Fluids.EMPTY, true, "ffc100");
  public static final IMaterial reed = mat(MaterialIds.reed, Fluids.EMPTY, true, "aadb74");
  public static final IMaterial ice = mat(MaterialIds.ice, Fluids.EMPTY, true, "97d7e0");
  public static final IMaterial endrod = mat(MaterialIds.endrod, Fluids.EMPTY, true, "e8ffd6");

  // fletching
  public static final IMaterial feather = mat(MaterialIds.feather, Fluids.EMPTY, true, "eeeeee");
  public static final IMaterial leaf = mat(MaterialIds.leaf, Fluids.EMPTY, true, "1d730c");
  public static final IMaterial slimeleaf_blue = mat(MaterialIds.slimeleaf_blue, Fluids.EMPTY, true, "74c8c7");
  public static final IMaterial slimeleaf_orange = mat(MaterialIds.slimeleaf_orange, Fluids.EMPTY, true, "ff960d");
  public static final IMaterial slimeleaf_purple = mat(MaterialIds.slimeleaf_purple, Fluids.EMPTY, true, "c873c8");

  private static IMaterial mat(MaterialId location, Fluid fluid, boolean craftable, String materialColor) {
    Material material = new Material(location, fluid, craftable, materialColor);
    allMaterials.add(material);
    return material;
  }

  private Materials() {
  }
}
