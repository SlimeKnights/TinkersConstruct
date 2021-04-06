package slimeknights.tconstruct.tools.data;

import com.mojang.datafixers.util.Pair;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
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
  static final List<Pair<IMaterial,ICondition>> allMaterials = new ArrayList<>();

  // tier 1
  public static final IMaterial wood  = mat(MaterialIds.wood,  1, ORDER_GENERAL, true, 0x8e661b, TinkerModifiers.cultivated);
  public static final IMaterial stone = mat(MaterialIds.stone, 1, ORDER_HARVEST, true, 0x999999, TinkerModifiers.stonebound);
  public static final IMaterial flint = mat(MaterialIds.flint, 1, ORDER_WEAPON,  true, 0x696969, TinkerModifiers.jagged);
  public static final IMaterial bone  = mat(MaterialIds.bone,  1, ORDER_SPECIAL, true, 0xede6bf, TinkerModifiers.fractured);
  // tier 2
  public static final IMaterial iron        = mat(MaterialIds.iron,        2, ORDER_GENERAL, TinkerFluids.moltenIron, false, 0xcacaca, TinkerModifiers.reinforced);
  public static final IMaterial copper      = mat(MaterialIds.copper,      2, ORDER_HARVEST, TinkerFluids.moltenCopper, true, 0xfba165, TinkerModifiers.dwarven);
  public static final IMaterial searedStone = mat(MaterialIds.searedStone, 2, ORDER_WEAPON,  TinkerFluids.searedStone, false, 0x3f3f3f, TinkerModifiers.searing);
  public static final IMaterial slimewood   = mat(new DataMaterial(MaterialIds.slimewood, 2, ORDER_SPECIAL, false, 0x82c873, getOverslimeTrait(TinkerModifiers.overgrowth)));
  // tier 3
  public static final IMaterial slimesteel = overslimeMat(MaterialIds.slimesteel, 3, ORDER_GENERAL, TinkerFluids.moltenSlimesteel, false, 0x74c8c7, TinkerModifiers.overcast);
  public static final IMaterial tinkersBronze = mat(MaterialIds.tinkersBronze, 3, ORDER_HARVEST, TinkerFluids.moltenTinkersBronze, false, 0xf9cf72, TinkerModifiers.wellMaintained);
  public static final IMaterial nahuatl       = mat(MaterialIds.nahuatl,       3, ORDER_WEAPON,  false, 0x601cc4, TinkerModifiers.lacerating);
  public static final IMaterial roseGold      = mat(MaterialIds.roseGold,      3, ORDER_SPECIAL, TinkerFluids.moltenRoseGold, false, 0xf7cdbb, TinkerModifiers.enhanced);
  public static final IMaterial pigIron       = mat(MaterialIds.pigIron,       3, ORDER_SPECIAL, TinkerFluids.moltenPigIron, false, 0xf0a8a4, TinkerModifiers.tasty);

  // tier 2 (nether)
  //public static final IMaterial witherBone = mat(MaterialIds.witherBone, true, 0xede6bf);
  // tier 3 (nether)
  public static final IMaterial cobalt = mat(MaterialIds.cobalt, 3, ORDER_NETHER, TinkerFluids.moltenCobalt, false, 0x2376dd, TinkerModifiers.lightweight);
  // tier 4
  public static final IMaterial queensSlime = overslimeMat(MaterialIds.queensSlime, 4, ORDER_GENERAL, TinkerFluids.moltenQueensSlime, false, 0x236c45, TinkerModifiers.overlord);
  public static final IMaterial hepatizon   = mat(MaterialIds.hepatizon,   4, ORDER_HARVEST, TinkerFluids.moltenHepatizon,   false, 0x60496b, TinkerModifiers.momentum);
  public static final IMaterial manyullyn   = mat(MaterialIds.manyullyn,   4, ORDER_WEAPON,  TinkerFluids.moltenManyullyn,   false, 0x9261cc, TinkerModifiers.insatiable);
  //public static final IMaterial soulsteel   = mat(MaterialIds.soulsteel, 4, ORDER_SPECIAL, TinkerFluids.moltenSoulsteel, false, 0x6a5244);

  // tier 2 (end)
  //public static final IMaterial endstone = mat(MaterialIds.endstone, true, 0xe0d890);

  // tier 2 (mod integration)
  public static final IMaterial lead     = compatMat(MaterialIds.lead,   2, ORDER_COMPAT, TinkerFluids.moltenLead,   TinkerModifiers.heavy);
  public static final IMaterial silver   = compatMat(MaterialIds.silver, 2, ORDER_COMPAT, TinkerFluids.moltenSilver, TinkerModifiers.holy);
  // tier 3 (mod integration)
  public static final IMaterial electrum   = compatMat(MaterialIds.electrum,   3, ORDER_COMPAT,  TinkerFluids.moltenElectrum,   TinkerModifiers.experienced);
  public static final IMaterial bronze     = compatMat(MaterialIds.bronze,     3, ORDER_HARVEST, TinkerFluids.moltenBronze,     TinkerModifiers.wellMaintained2);
  public static final IMaterial steel      = compatMat(MaterialIds.steel,      3, ORDER_GENERAL, TinkerFluids.moltenSteel,      TinkerModifiers.sturdy);
  public static final IMaterial constantan = compatMat(MaterialIds.constantan, 3, ORDER_COMPAT,  TinkerFluids.moltenConstantan, TinkerModifiers.temperate);

  // bowstring IMaterials
//  public static final IMaterial string = mat(MaterialIds.string, true, 0xeeeeee);
//  public static final IMaterial vine = mat(MaterialIds.vine, true, 0x40a10f);
//  public static final IMaterial slimevine_blue = mat(MaterialIds.slimevine_blue, true, 0x74c8c7);
//  public static final IMaterial slimevine_purple = mat(MaterialIds.slimevine_purple, true, 0xc873c8);

  /** Creates a supplier for the given trait */
  private static Supplier<List<ModifierEntry>> traitSupplier(@Nullable Supplier<? extends Modifier> trait) {
    if (trait == null) {
      return Collections::emptyList;
    }
    return () -> Collections.singletonList(new ModifierEntry(trait.get(), 1));
  }

  /** Creates a new material with a tag requirement */
  private static IMaterial mat(IMaterial material, @Nullable ICondition condition) {
    allMaterials.add(Pair.of(material, condition));
    return material;
  }

  /** Creates a new material */
  private static IMaterial mat(IMaterial material) {
    return mat(material, null);
  }

  /** Creates a material with a fluid */
  private static IMaterial mat(MaterialId location, int tier, int order, Supplier<? extends Fluid> fluid, boolean craftable, int color, @Nullable Supplier<? extends Modifier> trait) {
    // all our materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    return mat(new DataMaterial(location, tier, order, fluid, MaterialValues.INGOT, craftable, color, traitSupplier(trait)));
  }

  /** Creates a material with a fluid */
  private static IMaterial overslimeMat(MaterialId location, int tier, int order, Supplier<? extends Fluid> fluid, boolean craftable, int color, Supplier<? extends Modifier> trait) {
    // all our materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    return mat(new DataMaterial(location, tier, order, fluid, MaterialValues.INGOT, craftable, color, getOverslimeTrait(trait)));
  }

  /** Creates a material with no fluid */
  private static IMaterial mat(MaterialId location, int tier, int order, boolean craftable, int color, @Nullable Supplier<? extends Modifier> trait) {
    return mat(new DataMaterial(location, tier, order, craftable, color, traitSupplier(trait)));
  }

  /** Creates a new compat material */
  private static IMaterial compatMat(MaterialId location, int tier, int order, Supplier<? extends Fluid> fluid, @Nullable Supplier<? extends Modifier> trait) {
    // all our addon materials use ingot value right now, so not much need to make a constructor parameter - option is mainly for addons
    ICondition condition = new NotCondition(new TagEmptyCondition("forge", "ingots/" + location.getPath()));
    return mat(new DataMaterial(location, tier, order, fluid, MaterialValues.INGOT, false, fluid.get().getAttributes().getColor() & 0xFFFFFF, traitSupplier(trait)), condition);
  }

  /** Gets an overslime based trait for the given tool */
  private static Supplier<List<ModifierEntry>> getOverslimeTrait(Supplier<? extends Modifier> trait) {
    return () -> Arrays.asList(new ModifierEntry(trait.get(), 1), new ModifierEntry(TinkerModifiers.overslime.get(), 1));
  }
}
