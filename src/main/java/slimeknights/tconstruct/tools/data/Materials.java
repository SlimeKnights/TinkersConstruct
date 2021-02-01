package slimeknights.tconstruct.tools.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.text.Color;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Materials {
  static final List<IMaterial> allMaterials = new ArrayList<>();

  // tier 1
  public static final IMaterial wood  = mat(MaterialIds.wood, true, 0x8e661b);
  public static final IMaterial flint = mat(MaterialIds.flint, true, 0x696969);
  public static final IMaterial stone = mat(MaterialIds.stone, true, 0x999999);
  public static final IMaterial bone  = mat(MaterialIds.bone, true, 0xede6bf, TinkerModifiers.fractured);
  // tier 2
  public static final IMaterial iron        = mat(MaterialIds.iron, TinkerFluids.moltenIron, false, 0xcacaca, TinkerModifiers.reinforced);
  public static final IMaterial searedStone = mat(MaterialIds.searedStone, TinkerFluids.searedStone, false, 0x3f3f3f);
  public static final IMaterial copper      = mat(MaterialIds.copper, TinkerFluids.moltenCopper, true, 0xed9f07);
  public static final IMaterial slimewood   = mat(MaterialIds.slimewood, false, 0x82c873);
  // tier 3
  public static final IMaterial slimesteel    = mat(MaterialIds.slimesteel, TinkerFluids.moltenSlimesteel, false, 0x74c8c7);
  public static final IMaterial nahuatl       = mat(MaterialIds.nahuatl, false, 0x601cc4);
  public static final IMaterial tinkersBronze = mat(MaterialIds.tinkersBronze, TinkerFluids.moltenTinkersBronze, false, 0xf9cf72);
  public static final IMaterial roseGold      = mat(MaterialIds.roseGold, TinkerFluids.moltenRoseGold, false, 0xffdbcc, TinkerModifiers.enhanced);
  public static final IMaterial pigIron       = mat(MaterialIds.pigIron, TinkerFluids.moltenPigIron, false, 0xef9e9b);

  // tier 2 (nether)
  //public static final IMaterial witherBone = mat(MaterialIds.witherBone, true, 0xede6bf);
  // tier 3 (nether)
  public static final IMaterial cobalt = mat(MaterialIds.cobalt, TinkerFluids.moltenCobalt, false, 0x2882d4, TinkerModifiers.lightweight);
  // tier 4
  public static final IMaterial manyullyn   = mat(MaterialIds.manyullyn, TinkerFluids.moltenManyullyn, false, 0xa15cf8);
  public static final IMaterial hepatizon   = mat(MaterialIds.hepatizon, TinkerFluids.moltenHepatizon, false, 0x60496b);
  public static final IMaterial queensSlime = mat(MaterialIds.queensSlime, TinkerFluids.moltenQueensSlime, false, 0x236c45, TinkerModifiers.overlord);
  public static final IMaterial soulsteel   = mat(MaterialIds.soulsteel, TinkerFluids.moltenSoulsteel, false, 0x6a5244);

  // tier 2 (end)
  //public static final IMaterial endstone = mat(MaterialIds.endstone, true, 0xe0d890);

  // tier 2 (mod integration)
  public static final IMaterial lead = mat(MaterialIds.lead, false, 0x4d4968, TinkerModifiers.heavy);
  public static final IMaterial silver = mat(MaterialIds.silver, false, 0xd1ecf6, TinkerModifiers.smite);
  // tier 3 (mod integration)
  public static final IMaterial electrum = mat(MaterialIds.electrum, false, 0xe8db49);
  public static final IMaterial bronze = mat(MaterialIds.bronze, false, 0xe3bd68);
  public static final IMaterial steel = mat(MaterialIds.steel, false, 0xa7a7a7, TinkerModifiers.sturdy);

  // bowstring IMaterials
//  public static final IMaterial string = mat(MaterialIds.string, true, 0xeeeeee);
//  public static final IMaterial vine = mat(MaterialIds.vine, true, 0x40a10f);
//  public static final IMaterial slimevine_blue = mat(MaterialIds.slimevine_blue, true, 0x74c8c7);
//  public static final IMaterial slimevine_purple = mat(MaterialIds.slimevine_purple, true, 0xc873c8);


  /** Creates a material with a fluid */
  private static IMaterial mat(MaterialId location, Supplier<? extends Fluid> fluid, boolean craftable, int color, @Nullable DataModifierEntry trait) {
    // all our materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    IMaterial material = new DataMaterial(location, fluid, MaterialValues.INGOT, craftable, Color.fromInt(color), trait);
    allMaterials.add(material);
    return material;
  }

  /** Creates a material with a fluid and a single trait */
  private static IMaterial mat(MaterialId location, Supplier<? extends Fluid> fluid, boolean craftable, int color, Supplier<? extends Modifier> trait) {
    return mat(location, fluid, craftable, color, new DataModifierEntry(trait, 1));
  }

  /** Creates a material with a fluid and no trait */
  private static IMaterial mat(MaterialId location, Supplier<? extends Fluid> fluid, boolean craftable, int color) {
    return mat(location, fluid, craftable, color, (DataModifierEntry)null);
  }

  /** Creates a material with no fluid */
  private static IMaterial mat(MaterialId location, boolean craftable, int color, DataModifierEntry traits) {
    return mat(location, () -> Fluids.EMPTY, craftable, color, traits);
  }

  /** Creates a material with no fluid and a single trait */
  private static IMaterial mat(MaterialId location, boolean craftable, int color, Supplier<? extends Modifier> trait) {
    return mat(location, craftable, color, new DataModifierEntry(trait, 1));
  }

  /** Creates a material with no fluid and no trait */
  private static IMaterial mat(MaterialId location, boolean craftable, int color) {
    return mat(location, () -> Fluids.EMPTY, craftable, color, (DataModifierEntry)null);
  }
}
