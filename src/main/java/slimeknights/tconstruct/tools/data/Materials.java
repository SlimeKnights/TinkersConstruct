package slimeknights.tconstruct.tools.data;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;

import java.util.ArrayList;
import java.util.List;

final class Materials {

  static final List<IMaterial> allMaterials = new ArrayList<>();

  // natural resources/blocks
  public static final IMaterial wood = mat(MaterialIds.wood, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial stone = mat(MaterialIds.stone, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial flint = mat(MaterialIds.flint, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial cactus = mat(MaterialIds.cactus, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial bone = mat(MaterialIds.bone, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial obsidian = mat(MaterialIds.obsidian, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial prismarine = mat(MaterialIds.prismarine, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial endstone = mat(MaterialIds.endstone, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial paper = mat(MaterialIds.paper, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial sponge = mat(MaterialIds.sponge, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial firewood = mat(MaterialIds.firewood, Fluids.EMPTY, true, ItemStack.EMPTY);

  // Slime
  public static final IMaterial knightslime = mat(MaterialIds.knightslime, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial slime = mat(MaterialIds.slime, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial blueslime = mat(MaterialIds.blueslime, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial magmaslime = mat(MaterialIds.magmaslime, Fluids.EMPTY, true, ItemStack.EMPTY);

  // Metals
  public static final IMaterial iron = mat(MaterialIds.iron, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial pigiron = mat(MaterialIds.pigiron, Fluids.EMPTY, false, ItemStack.EMPTY);

  // Nether Materials
  public static final IMaterial netherrack = mat(MaterialIds.netherrack, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial ardite = mat(MaterialIds.ardite, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial cobalt = mat(MaterialIds.cobalt, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial manyullyn = mat(MaterialIds.manyullyn, Fluids.EMPTY, false, ItemStack.EMPTY);

  // mod integration
  public static final IMaterial copper = mat(MaterialIds.copper, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial bronze = mat(MaterialIds.bronze, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial lead = mat(MaterialIds.lead, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial silver = mat(MaterialIds.silver, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial electrum = mat(MaterialIds.electrum, Fluids.EMPTY, false, ItemStack.EMPTY);
  public static final IMaterial steel = mat(MaterialIds.steel, Fluids.EMPTY, false, ItemStack.EMPTY);

  // bowstring IMaterials
  public static final IMaterial string = mat(MaterialIds.string, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial vine = mat(MaterialIds.vine, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial slimevine_blue = mat(MaterialIds.slimevine_blue, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial slimevine_purple = mat(MaterialIds.slimevine_purple, Fluids.EMPTY, true, ItemStack.EMPTY);

  // additional arrow shaft
  public static final IMaterial blaze = mat(MaterialIds.blaze, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial reed = mat(MaterialIds.reed, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial ice = mat(MaterialIds.ice, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial endrod = mat(MaterialIds.endrod, Fluids.EMPTY, true, ItemStack.EMPTY);

  // fletching
  public static final IMaterial feather = mat(MaterialIds.feather, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial leaf = mat(MaterialIds.leaf, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial slimeleaf_blue = mat(MaterialIds.slimeleaf_blue, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial slimeleaf_orange = mat(MaterialIds.slimeleaf_orange, Fluids.EMPTY, true, ItemStack.EMPTY);
  public static final IMaterial slimeleaf_purple = mat(MaterialIds.slimeleaf_purple, Fluids.EMPTY, true, ItemStack.EMPTY);

  private static IMaterial mat(MaterialId location, Fluid fluid, boolean craftable, ItemStack shard) {
    Material material = new Material(location, fluid, craftable, shard);
    allMaterials.add(material);
    return material;
  }

  private Materials() {
  }
}
