package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
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
    addDefaultTraits(MaterialIds.wood, TinkerModifiers.cultivated);
    addDefaultTraits(MaterialIds.rock, TinkerModifiers.stonebound);
    addDefaultTraits(MaterialIds.flint, TinkerModifiers.jagged);
    addDefaultTraits(MaterialIds.bone, TinkerModifiers.piercing);
    // tier 1 - nether
    addDefaultTraits(MaterialIds.necroticBone, TinkerModifiers.necrotic);
    // tier 1 - binding
    addDefaultTraits(MaterialIds.string, ModifierIds.stringy);
    addDefaultTraits(MaterialIds.leather, TinkerModifiers.tanned);
    addDefaultTraits(MaterialIds.vine, TinkerModifiers.solarPowered);

    // tier 2
    addDefaultTraits(MaterialIds.iron, ModifierIds.sturdy);
    addDefaultTraits(MaterialIds.copper, TinkerModifiers.dwarven);
    addDefaultTraits(MaterialIds.searedStone, TinkerModifiers.searing);
    addDefaultTraits(MaterialIds.slimewood, TinkerModifiers.overgrowth, TinkerModifiers.overslime);
    addDefaultTraits(MaterialIds.bloodbone, TinkerModifiers.raging);
    // tier 2 - nether
    addDefaultTraits(MaterialIds.scorchedStone, ModifierIds.scorching);
    // tier 2 - binding
    addDefaultTraits(MaterialIds.chain, TinkerModifiers.reinforced);
    addDefaultTraits(MaterialIds.skyslimeVine, TinkerModifiers.airborne);

    // tier 3
    addDefaultTraits(MaterialIds.slimesteel, TinkerModifiers.overcast, TinkerModifiers.overslime);
    addDefaultTraits(MaterialIds.amethystBronze, ModifierIds.crumbling);
    addDefaultTraits(MaterialIds.nahuatl, TinkerModifiers.lacerating);
    addDefaultTraits(MaterialIds.roseGold, ModifierIds.enhanced);
    addDefaultTraits(MaterialIds.pigIron, TinkerModifiers.tasty);
    // tier 3 - nether
    addDefaultTraits(MaterialIds.cobalt, ModifierIds.lightweight);
    // tier 3 - binding
    addDefaultTraits(MaterialIds.darkthread, ModifierIds.looting);

    // tier 4
    addDefaultTraits(MaterialIds.queensSlime, TinkerModifiers.overlord, TinkerModifiers.overslime);
    addDefaultTraits(MaterialIds.hepatizon, TinkerModifiers.momentum);
    addDefaultTraits(MaterialIds.manyullyn, TinkerModifiers.insatiable);
    addDefaultTraits(MaterialIds.blazingBone, TinkerModifiers.conducting);
    // tier 4 - binding
    addDefaultTraits(MaterialIds.ancientHide, ModifierIds.fortune);

    // tier 5
    addDefaultTraits(MaterialIds.enderslimeVine, TinkerModifiers.enderporting);

    // tier 2 - mod compat
    addDefaultTraits(MaterialIds.osmium, TinkerModifiers.dense);
    addDefaultTraits(MaterialIds.tungsten, ModifierIds.sharpweight);
    addDefaultTraits(MaterialIds.platinum, ModifierIds.lustrous);
    addDefaultTraits(MaterialIds.lead, ModifierIds.heavy);
    addDefaultTraits(MaterialIds.silver, ModifierIds.smite);
    addDefaultTraits(MaterialIds.whitestone, TinkerModifiers.stoneshield);
    // tier 3 - mod compat
    addDefaultTraits(MaterialIds.steel, ModifierIds.ductile);
    addDefaultTraits(MaterialIds.bronze, TinkerModifiers.maintained);
    addDefaultTraits(MaterialIds.constantan, TinkerModifiers.temperate);
    addDefaultTraits(MaterialIds.invar, TinkerModifiers.invariant);
    addDefaultTraits(MaterialIds.necronium, TinkerModifiers.decay);
    addDefaultTraits(MaterialIds.electrum, TinkerModifiers.experienced);
    addDefaultTraits(MaterialIds.platedSlimewood, TinkerModifiers.overworked, TinkerModifiers.overslime);

    // slimeskull
    addTraits(MaterialIds.glass,        SkullStats.ID, TinkerModifiers.selfDestructive.getId(), ModifierIds.creeperDisguise);
    addTraits(MaterialIds.enderPearl,   SkullStats.ID, TinkerModifiers.enderdodging.getId(), ModifierIds.endermanDisguise);
    addTraits(MaterialIds.bone,         SkullStats.ID, TinkerModifiers.strongBones.getId(), ModifierIds.skeletonDisguise);
    addTraits(MaterialIds.bloodbone,    SkullStats.ID, TinkerModifiers.frosttouch.getId(), ModifierIds.strayDisguise);
    addTraits(MaterialIds.necroticBone, SkullStats.ID, TinkerModifiers.withered.getId(), ModifierIds.witherSkeletonDisguise);
    addTraits(MaterialIds.string,       SkullStats.ID, TinkerModifiers.boonOfSssss.getId(), ModifierIds.spiderDisguise);
    addTraits(MaterialIds.darkthread,   SkullStats.ID, TinkerModifiers.mithridatism.getId(), ModifierIds.caveSpiderDisguise);
    addTraits(MaterialIds.rottenFlesh,  SkullStats.ID, TinkerModifiers.wildfire.getId(), ModifierIds.zombieDisguise);
    addTraits(MaterialIds.iron,         SkullStats.ID, TinkerModifiers.plague.getId(), ModifierIds.huskDisguise);
    addTraits(MaterialIds.copper,       SkullStats.ID, TinkerModifiers.breathtaking.getId(), ModifierIds.drownedDisguise);
    addTraits(MaterialIds.blazingBone,  SkullStats.ID, TinkerModifiers.firebreath.getId(), ModifierIds.blazeDisguise);
    addTraits(MaterialIds.gold,         SkullStats.ID, TinkerModifiers.chrysophilite.getId(), ModifierIds.piglinDisguise);
    addTraits(MaterialIds.roseGold,     SkullStats.ID, TinkerModifiers.goldGuard.getId(), ModifierIds.piglinBruteDisguise);
    addTraits(MaterialIds.pigIron,      SkullStats.ID, TinkerModifiers.revenge.getId(), ModifierIds.zombifiedPiglinDisguise);
    // plate
    noTraits(MaterialIds.obsidian);
    noTraits(MaterialIds.debris);
    noTraits(MaterialIds.netherite);
    // slimesuit
    noTraits(MaterialIds.earthslime);
    noTraits(MaterialIds.skyslime);
    noTraits(MaterialIds.blood);
    noTraits(MaterialIds.ichor);
    noTraits(MaterialIds.enderslime);
    noTraits(MaterialIds.clay);
    noTraits(MaterialIds.honey);
    //noTraits(MaterialIds.venom);
    noTraits(MaterialIds.phantom);
    noTraits(MaterialIds.chorus);
    // compat plate
    noTraits(MaterialIds.aluminum);
    noTraits(MaterialIds.nickel);
    noTraits(MaterialIds.tin);
    noTraits(MaterialIds.zinc);
    noTraits(MaterialIds.brass);
    noTraits(MaterialIds.uranium);
  }
}
