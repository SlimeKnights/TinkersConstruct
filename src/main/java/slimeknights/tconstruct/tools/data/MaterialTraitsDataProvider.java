package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class MaterialTraitsDataProvider extends AbstractMaterialTraitDataProvider {
  public MaterialTraitsDataProvider(DataGenerator gen, AbstractMaterialDataProvider materials) {
    super(gen, materials);
  }

  @Override
  public String getName() {
    return "Tinker's Construct Material Traits";
  }

  @Override
  protected void addMaterialTraits() {
    // tier 1
    addDefaultTraits(MaterialIds.wood, TinkerModifiers.cultivated.get());
    addDefaultTraits(MaterialIds.stone, TinkerModifiers.stonebound.get());
    addDefaultTraits(MaterialIds.flint, TinkerModifiers.jagged.get());
    addDefaultTraits(MaterialIds.bone, TinkerModifiers.fractured.get());
    // tier 1 - nether
    addDefaultTraits(MaterialIds.necroticBone, TinkerModifiers.necrotic.get());

    // tier 2
    addDefaultTraits(MaterialIds.iron, TinkerModifiers.sturdy.get());
    addDefaultTraits(MaterialIds.copper, TinkerModifiers.dwarven.get());
    addDefaultTraits(MaterialIds.searedStone, TinkerModifiers.searing.get());
    addDefaultTraits(MaterialIds.slimewood, TinkerModifiers.overgrowth.get(), TinkerModifiers.overslime.get());
    addDefaultTraits(MaterialIds.bloodbone, TinkerModifiers.raging.get());
    // tier 2 - nether
    addDefaultTraits(MaterialIds.scorchedStone, TinkerModifiers.scorching.get());

    // tier 3
    addDefaultTraits(MaterialIds.slimesteel, TinkerModifiers.overcast.get(), TinkerModifiers.overslime.get());
    addDefaultTraits(MaterialIds.tinkersBronze, TinkerModifiers.wellMaintained.get());
    addDefaultTraits(MaterialIds.nahuatl, TinkerModifiers.lacerating.get());
    addDefaultTraits(MaterialIds.roseGold, TinkerModifiers.enhanced.get());
    addDefaultTraits(MaterialIds.pigIron, TinkerModifiers.tasty.get());
    // tier 3 - nether
    addDefaultTraits(MaterialIds.cobalt, TinkerModifiers.lightweight.get());

    // tier 4
    addDefaultTraits(MaterialIds.queensSlime, TinkerModifiers.overlord.get(), TinkerModifiers.overslime.get());
    addDefaultTraits(MaterialIds.hepatizon, TinkerModifiers.momentum.get());
    addDefaultTraits(MaterialIds.manyullyn, TinkerModifiers.insatiable.get());
    addDefaultTraits(MaterialIds.blazingBone, TinkerModifiers.conducting.get());

    // tier 2 - mod compat
    addDefaultTraits(MaterialIds.lead, TinkerModifiers.heavy.get());
    addDefaultTraits(MaterialIds.silver, TinkerModifiers.smite.get());
    // tier 3 - mod compat
    addDefaultTraits(MaterialIds.electrum, TinkerModifiers.experienced.get());
    addDefaultTraits(MaterialIds.bronze, TinkerModifiers.wellMaintained2.get());
    addDefaultTraits(MaterialIds.steel, TinkerModifiers.ductile.get());
    addDefaultTraits(MaterialIds.constantan, TinkerModifiers.temperate.get());
  }
}
