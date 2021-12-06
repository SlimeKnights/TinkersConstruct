package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.stats.SkullStats;

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
    addDefaultTraits(MaterialIds.bone, TinkerModifiers.piercing.get());
    // tier 1 - nether
    addDefaultTraits(MaterialIds.necroticBone, TinkerModifiers.necrotic.get());
    // tier 1 - binding
    addDefaultTraits(MaterialIds.string, TinkerModifiers.stringy.get());
    addDefaultTraits(MaterialIds.leather, TinkerModifiers.tanned.get());
    addDefaultTraits(MaterialIds.vine, TinkerModifiers.solarPowered.get());

    // tier 2
    addDefaultTraits(MaterialIds.iron, TinkerModifiers.sturdy.get());
    addDefaultTraits(MaterialIds.copper, TinkerModifiers.dwarven.get());
    addDefaultTraits(MaterialIds.searedStone, TinkerModifiers.searing.get());
    addDefaultTraits(MaterialIds.slimewood, TinkerModifiers.overgrowth.get(), TinkerModifiers.overslime.get());
    addDefaultTraits(MaterialIds.bloodbone, TinkerModifiers.raging.get());
    // tier 2 - nether
    addDefaultTraits(MaterialIds.scorchedStone, TinkerModifiers.scorching.get());
    // tier 2 - binding
    addDefaultTraits(MaterialIds.chain, TinkerModifiers.reinforced.get());
    addDefaultTraits(MaterialIds.skyslimeVine, TinkerModifiers.airborne.get());

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

    // tier 5
    addDefaultTraits(MaterialIds.enderslimeVine, TinkerModifiers.enderporting.get());

    // tier 2 - mod compat
    addDefaultTraits(MaterialIds.osmium, TinkerModifiers.dense.get());
    addDefaultTraits(MaterialIds.tungsten, TinkerModifiers.sharpweight.get());
    addDefaultTraits(MaterialIds.platinum, TinkerModifiers.lustrous.get());
    addDefaultTraits(MaterialIds.lead, TinkerModifiers.heavy.get());
    addDefaultTraits(MaterialIds.silver, TinkerModifiers.smite.get());
    addDefaultTraits(MaterialIds.whitestone, TinkerModifiers.stoneshield.get());
    // tier 3 - mod compat
    addDefaultTraits(MaterialIds.steel, TinkerModifiers.ductile.get());
    addDefaultTraits(MaterialIds.bronze, TinkerModifiers.wellMaintained2.get());
    addDefaultTraits(MaterialIds.constantan, TinkerModifiers.temperate.get());
    addDefaultTraits(MaterialIds.invar, TinkerModifiers.invariant.get());
    addDefaultTraits(MaterialIds.necronium, TinkerModifiers.decay.get());
    addDefaultTraits(MaterialIds.electrum, TinkerModifiers.experienced.get());
    addDefaultTraits(MaterialIds.platedSlimewood, TinkerModifiers.overworked.get(), TinkerModifiers.overslime.get());

    // slimeskull
    addTraits(MaterialIds.gunpowder,    SkullStats.ID, TinkerModifiers.selfDestructive.get(), TinkerModifiers.creeperDisguise.get());
    addTraits(MaterialIds.enderPearl,   SkullStats.ID, TinkerModifiers.enderdodging.get(), TinkerModifiers.endermanDisguise.get());
    addTraits(MaterialIds.bone,         SkullStats.ID, TinkerModifiers.strongBones.get(), TinkerModifiers.skeletonDisguise.get());
    addTraits(MaterialIds.bloodbone,    SkullStats.ID, TinkerModifiers.frosttouch.get(), TinkerModifiers.strayDisguise.get());
    addTraits(MaterialIds.necroticBone, SkullStats.ID, TinkerModifiers.withered.get(), TinkerModifiers.witherSkeletonDisguise.get());
    addTraits(MaterialIds.spider,       SkullStats.ID, TinkerModifiers.boonOfSssss.get(), TinkerModifiers.spiderDisguise.get());
    addTraits(MaterialIds.venom,        SkullStats.ID, TinkerModifiers.mithridatism.get(), TinkerModifiers.caveSpiderDisguise.get());
    addTraits(MaterialIds.rottenFlesh,  SkullStats.ID, TinkerModifiers.wildfire.get(), TinkerModifiers.zombieDisguise.get());
    addTraits(MaterialIds.potato,       SkullStats.ID, TinkerModifiers.plague.get(), TinkerModifiers.huskDisguise.get());
    addTraits(MaterialIds.fish,         SkullStats.ID, TinkerModifiers.breathtaking.get(), TinkerModifiers.drownedDisguise.get());
    addTraits(MaterialIds.blazingBone,  SkullStats.ID, TinkerModifiers.firebreath.get(), TinkerModifiers.blazeDisguise.get());
    // slimesuit
    noTraits(MaterialIds.phantom);
    noTraits(MaterialIds.chorus);
    noTraits(MaterialIds.rabbit);
  }
}
