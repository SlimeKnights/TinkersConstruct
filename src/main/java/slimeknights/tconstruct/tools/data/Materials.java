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
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Materials {
  /** General purpose materials */
  private static final int ORDER_GENERAL = 0;
  /** Materials primarily used for harvest */
  private static final int ORDER_HARVEST = 1;
  /** Materials primarily used for weapons */
  private static final int ORDER_WEAPON = 2;
  /** General purpose materials */
  private static final int ORDER_SPECIAL = 3;
  /** Order for mod integration materials */
  private static final int ORDER_COMPAT = 5;
  /** Order for nether materials in tiers 1-3 */
  private static final int ORDER_NETHER = 7;
  /** Order for end materials in tiers 1-4 */
  private static final int ORDER_END = 10;
  static final List<IMaterial> allMaterials = new ArrayList<>();

  // tier 1
  public static final IMaterial wood  = mat(MaterialIds.wood,  1, ORDER_GENERAL, true, 0x8e661b, TinkerModifiers.cultivated);
  public static final IMaterial stone = mat(MaterialIds.stone, 1, ORDER_HARVEST, true, 0x999999, TinkerModifiers.stonebound);
  public static final IMaterial flint = mat(MaterialIds.flint, 1, ORDER_WEAPON,  true, 0x696969, TinkerModifiers.jagged);
  public static final IMaterial bone  = mat(MaterialIds.bone,  1, ORDER_SPECIAL, true, 0xede6bf, TinkerModifiers.fractured);
  // tier 2
  public static final IMaterial iron        = mat(MaterialIds.iron,        2, ORDER_GENERAL, TinkerFluids.moltenIron, false, 0xcacaca, TinkerModifiers.reinforced);
  public static final IMaterial copper      = mat(MaterialIds.copper,      2, ORDER_HARVEST, TinkerFluids.moltenCopper, true, 0xfba165, TinkerModifiers.dwarfish);
  public static final IMaterial searedStone = mat(MaterialIds.searedStone, 2, ORDER_WEAPON,  TinkerFluids.searedStone, false, 0x3f3f3f, TinkerModifiers.searing);
  public static final IMaterial slimewood   = mat(MaterialIds.slimewood,   2, ORDER_SPECIAL, 0x82c873, TinkerModifiers.overgrowth, TinkerModifiers.overslime);
  // tier 3
  public static final IMaterial slimesteel    = mat(MaterialIds.slimesteel,    3, ORDER_GENERAL, TinkerFluids.moltenSlimesteel, false, 0x74c8c7, TinkerModifiers.overcast);
  public static final IMaterial tinkersBronze = mat(MaterialIds.tinkersBronze, 3, ORDER_HARVEST, TinkerFluids.moltenTinkersBronze, false, 0xf9cf72, TinkerModifiers.wellMaintained);
  public static final IMaterial nahuatl       = mat(MaterialIds.nahuatl,       3, ORDER_WEAPON,  false, 0x601cc4, TinkerModifiers.lacerating);
  public static final IMaterial roseGold      = mat(MaterialIds.roseGold,      3, ORDER_SPECIAL, TinkerFluids.moltenRoseGold, false, 0xf7cdbb, TinkerModifiers.enhanced);
  public static final IMaterial pigIron       = mat(MaterialIds.pigIron,       3, ORDER_SPECIAL, TinkerFluids.moltenPigIron, false, 0xf0a8a4, TinkerModifiers.tasty);

  // tier 2 (nether)
  //public static final IMaterial witherBone = mat(MaterialIds.witherBone, true, 0xede6bf);
  // tier 3 (nether)
  public static final IMaterial cobalt = mat(MaterialIds.cobalt, 3, ORDER_NETHER, TinkerFluids.moltenCobalt, false, 0x2376dd, TinkerModifiers.lightweight);
  // tier 4
  public static final IMaterial queensSlime = mat(MaterialIds.queensSlime, 4, ORDER_GENERAL, TinkerFluids.moltenQueensSlime, false, 0x236c45, TinkerModifiers.overlord);
  public static final IMaterial hepatizon   = mat(MaterialIds.hepatizon,   4, ORDER_HARVEST, TinkerFluids.moltenHepatizon,   false, 0x60496b);
  public static final IMaterial manyullyn   = mat(MaterialIds.manyullyn,   4, ORDER_WEAPON,  TinkerFluids.moltenManyullyn,   false, 0x9261cc);
  //public static final IMaterial soulsteel   = mat(MaterialIds.soulsteel, 4, ORDER_SPECIAL, TinkerFluids.moltenSoulsteel, false, 0x6a5244);

  // tier 2 (end)
  //public static final IMaterial endstone = mat(MaterialIds.endstone, true, 0xe0d890);

  // tier 2 (mod integration)
  public static final IMaterial lead     = mat(MaterialIds.lead,     2, ORDER_COMPAT, TinkerFluids.moltenLead,     false, 0x4d4968, TinkerModifiers.heavy);
  public static final IMaterial silver   = mat(MaterialIds.silver,   2, ORDER_COMPAT, TinkerFluids.moltenSilver,   false, 0xd1ecf6, TinkerModifiers.smite);
  // tier 3 (mod integration)
  public static final IMaterial electrum   = mat(MaterialIds.electrum,   3, ORDER_COMPAT,  TinkerFluids.moltenElectrum,   false, 0xe8db49, TinkerModifiers.experienced);
  public static final IMaterial bronze     = mat(MaterialIds.bronze,     3, ORDER_HARVEST, TinkerFluids.moltenBronze,     false, 0xcea179, TinkerModifiers.wellMaintained2);
  public static final IMaterial steel      = mat(MaterialIds.steel,      3, ORDER_GENERAL, TinkerFluids.moltenSteel,      false, 0xa7a7a7, TinkerModifiers.sturdy);
  public static final IMaterial constantan = mat(MaterialIds.constantan, 3, ORDER_COMPAT,  TinkerFluids.moltenConstantan, false, 0xff9e7f);

  // bowstring IMaterials
//  public static final IMaterial string = mat(MaterialIds.string, true, 0xeeeeee);
//  public static final IMaterial vine = mat(MaterialIds.vine, true, 0x40a10f);
//  public static final IMaterial slimevine_blue = mat(MaterialIds.slimevine_blue, true, 0x74c8c7);
//  public static final IMaterial slimevine_purple = mat(MaterialIds.slimevine_purple, true, 0xc873c8);


  /** Creates a material with no fluid and two traits */
  private static IMaterial mat(MaterialId location, int tier, int order, int color, Supplier<? extends Modifier> trait1, Supplier<? extends Modifier> trait2) {
    // all our materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    Supplier<List<ModifierEntry>> traitSupplier = () -> Arrays.asList(new ModifierEntry(trait1.get(), 1), new ModifierEntry(trait2.get(), 1));
    IMaterial material = new DataMaterial(location, tier, order, () -> Fluids.EMPTY, 0, true, Color.fromInt(color), traitSupplier);
    allMaterials.add(material);
    return material;
  }

  /** Creates a material with a fluid and a trait */
  private static IMaterial mat(MaterialId location, int tier, int order, Supplier<? extends Fluid> fluid, boolean craftable, int color, Supplier<? extends Modifier> trait) {
    // all our materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    Supplier<List<ModifierEntry>> traitSupplier = () -> Collections.singletonList(new ModifierEntry(trait.get(), 1));
    IMaterial material = new DataMaterial(location, tier, order, fluid, MaterialValues.INGOT, craftable, Color.fromInt(color), traitSupplier);
    allMaterials.add(material);
    return material;
  }

  /** Creates a material with no fluid */
  private static IMaterial mat(MaterialId location, int tier, int order, boolean craftable, int color, Supplier<? extends Modifier> trait) {
    return mat(location, tier, order, Fluids.EMPTY.delegate, craftable, color, trait);
  }

  /** Creates a material with a fluid and no trait */
  private static IMaterial mat(MaterialId location, int tier, int order, Supplier<? extends Fluid> fluid, boolean craftable, int color) {
    IMaterial material = new DataMaterial(location, tier, order, fluid, MaterialValues.INGOT, craftable, Color.fromInt(color), Collections::emptyList);
    allMaterials.add(material);
    return material;
  }

  /** Creates a material with no fluid and no trait */
  private static IMaterial mat(MaterialId location, int tier, int order, boolean craftable, int color) {
    return mat(location, tier, order, Fluids.EMPTY.delegate, craftable, color);
  }
}
