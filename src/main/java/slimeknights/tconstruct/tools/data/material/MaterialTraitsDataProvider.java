package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.SkullStats;

import static slimeknights.tconstruct.library.materials.MaterialRegistry.ARMOR;
import static slimeknights.tconstruct.library.materials.MaterialRegistry.MELEE_HARVEST;
import static slimeknights.tconstruct.library.materials.MaterialRegistry.RANGED;

public class MaterialTraitsDataProvider extends AbstractMaterialTraitDataProvider {
  public MaterialTraitsDataProvider(PackOutput packOutput, AbstractMaterialDataProvider materials) {
    super(packOutput, materials);
  }

  @Override
  public String getName() {
    return "Tinker's Construct Material Traits";
  }

  @Override
  protected void addMaterialTraits() {
    // tier 1
    addDefaultTraits(MaterialIds.wood, ModifierIds.cultivated);
    addDefaultTraits(MaterialIds.rock, TinkerModifiers.stonebound);
    addDefaultTraits(MaterialIds.flint, TinkerModifiers.jagged);
    addDefaultTraits(MaterialIds.bone, ModifierIds.pierce);
    addDefaultTraits(MaterialIds.bamboo, ModifierIds.unburdened);
    addDefaultTraits(MaterialIds.ice, ModifierIds.frostshield);
    addDefaultTraits(MaterialIds.cactus, ModifierIds.thorns);
    // tier 1 - end
    addDefaultTraits(MaterialIds.chorus, TinkerModifiers.enderference);
    addTraits(MaterialIds.chorus, ARMOR, ModifierIds.enderclearance);
    // tier 1 - binding
    addDefaultTraits(MaterialIds.string, ModifierIds.stringy);
    addDefaultTraits(MaterialIds.leather, TinkerModifiers.tanned);
    addDefaultTraits(MaterialIds.vine, TinkerModifiers.solarPowered);
    addTraits(MaterialIds.gold, ARMOR, TinkerModifiers.golden.getId(), ModifierIds.magicProtection);
    addTraits(MaterialIds.gold, PlatingMaterialStats.SHIELD.getId(), ModifierIds.magicProtection);

    // tier 2
    addDefaultTraits(MaterialIds.iron, TinkerModifiers.magnetic);
    addTraits(MaterialIds.iron, ARMOR, ModifierIds.projectileProtection);
    addDefaultTraits(MaterialIds.copper, TinkerModifiers.dwarven);
    addTraits(MaterialIds.copper, ARMOR, ModifierIds.depthProtection);
    addDefaultTraits(MaterialIds.searedStone, ModifierIds.searing);
    addTraits(MaterialIds.searedStone, ARMOR, ModifierIds.fireProtection);
    addDefaultTraits(MaterialIds.slimewood, ModifierIds.overgrowth, TinkerModifiers.overslime.getId());
    addDefaultTraits(MaterialIds.slimeskin, ModifierIds.overgrowth, TinkerModifiers.overslime.getId());
    addDefaultTraits(MaterialIds.venombone, ModifierIds.antitoxin);
    addDefaultTraits(MaterialIds.aluminum, ModifierIds.featherweight);
    // tier 2 - nether
    addDefaultTraits(MaterialIds.necroticBone, TinkerModifiers.necrotic);
    addDefaultTraits(MaterialIds.scorchedStone, ModifierIds.scorching);
    addTraits(MaterialIds.scorchedStone, ARMOR, ModifierIds.scorchProtection);
    // tier 2 - end
    addDefaultTraits(MaterialIds.whitestone, ModifierIds.stoneshield);
    // tier 2 - binding
    addDefaultTraits(MaterialIds.skyslimeVine, ModifierIds.airborne);
    addTraits(MaterialIds.skyslimeVine, ARMOR, ModifierIds.skyfall);
    addDefaultTraits(MaterialIds.weepingVine, ModifierIds.flamestance);
    addDefaultTraits(MaterialIds.twistingVine, ModifierIds.entangled);

    // tier 3
    addDefaultTraits(MaterialIds.slimesteel, ModifierIds.overcast, TinkerModifiers.overslime.getId());
    addTraits(MaterialIds.amethystBronze, MELEE_HARVEST, ModifierIds.crumbling);
    addTraits(MaterialIds.amethystBronze, RANGED, ModifierIds.crystalbound);
    addTraits(MaterialIds.amethystBronze, ARMOR, ModifierIds.crystalstrike);
    addDefaultTraits(MaterialIds.nahuatl, TinkerModifiers.lacerating);
    addDefaultTraits(MaterialIds.roseGold, ModifierIds.enhanced);
    addDefaultTraits(MaterialIds.pigIron, TinkerModifiers.tasty);
    addTraits(MaterialIds.obsidian, ARMOR, ModifierIds.blastProtection);
    // tier 3 - nether
    addDefaultTraits(MaterialIds.cobalt, ModifierIds.lightweight);
    addTraits(MaterialIds.cobalt, ARMOR, ModifierIds.meleeProtection);
    // tier 3 - binding
    addDefaultTraits(MaterialIds.darkthread, ModifierIds.looting);
    addDefaultTraits(MaterialIds.ichorskin, ModifierIds.overshield, TinkerModifiers.overslime.getId());

    // tier 4
    addDefaultTraits(MaterialIds.cinderslime, ModifierIds.overburn, TinkerModifiers.overslime.getId());
    addDefaultTraits(MaterialIds.queensSlime, ModifierIds.overlord, TinkerModifiers.overslime.getId());
    addDefaultTraits(MaterialIds.hepatizon, TinkerModifiers.momentum);
    addTraits(MaterialIds.hepatizon, ARMOR, ModifierIds.recurrentProtection);
    addDefaultTraits(MaterialIds.manyullyn, TinkerModifiers.insatiable);
    addTraits(MaterialIds.manyullyn, ARMOR, ModifierIds.kinetic);
    addDefaultTraits(MaterialIds.blazingBone, TinkerModifiers.conducting);
    addDefaultTraits(MaterialIds.blazewood, ModifierIds.flameBarrier);
    addDefaultTraits(MaterialIds.ancient, ModifierIds.vintage, ModifierIds.worldbound);
    // tier 4 - binding
    addTraits(MaterialIds.ancientHide, MELEE_HARVEST, ModifierIds.fortune);
    addDefaultTraits(MaterialIds.ancientHide, ModifierIds.fortified);

    // tier 4 (end)
    addDefaultTraits(MaterialIds.knightmetal, ModifierIds.valiant);
    addTraits(MaterialIds.knightmetal, ARMOR, ModifierIds.stalwart);
    addDefaultTraits(MaterialIds.enderslimeVine, TinkerModifiers.enderporting);
    addTraits(MaterialIds.enderslimeVine, ARMOR, ModifierIds.enderclearance);

    // tier 2 - mod compat
    addDefaultTraits(MaterialIds.osmium, ModifierIds.dense);
    addDefaultTraits(MaterialIds.lead, ModifierIds.heavy);
    addTraits(MaterialIds.silver, MELEE_HARVEST, ModifierIds.smite);
    addTraits(MaterialIds.silver, RANGED, ModifierIds.holy);
    addTraits(MaterialIds.silver, ARMOR, ModifierIds.consecrated);
    addDefaultTraits(MaterialIds.treatedWood, ModifierIds.preserved);
    addDefaultTraits(MaterialIds.ironwood, ModifierIds.deciduous);
    // tier 3 - mod compat
    addDefaultTraits(MaterialIds.steel, ModifierIds.ductile);
    addDefaultTraits(MaterialIds.bronze, ModifierIds.maintained);
    addDefaultTraits(MaterialIds.constantan, ModifierIds.temperate);
    addDefaultTraits(MaterialIds.invar, ModifierIds.solid);
    addDefaultTraits(MaterialIds.pewter, ModifierIds.raging);
    addTraits(MaterialIds.pewter, ARMOR, ModifierIds.vitalProtection);
    addDefaultTraits(MaterialIds.necronium, TinkerModifiers.decay);
    addDefaultTraits(MaterialIds.electrum, ModifierIds.shock);
    addDefaultTraits(MaterialIds.platedSlimewood, TinkerModifiers.overworked, TinkerModifiers.overslime);
    addDefaultTraits(MaterialIds.steeleaf, ModifierIds.experienced);
    // tier 4 - mod compat
    addDefaultTraits(MaterialIds.fiery, TinkerModifiers.autosmelt);
    addTraits(MaterialIds.fiery, ARMOR, ModifierIds.temperedProtection);

    // slimeskull
    addTraits(MaterialIds.glass,        SkullStats.ID, TinkerModifiers.selfDestructive.getId(), ModifierIds.creeperDisguise);
    addTraits(MaterialIds.enderPearl,   SkullStats.ID, TinkerModifiers.enderdodging.getId(), ModifierIds.endermanDisguise);
    addTraits(MaterialIds.bone,         SkullStats.ID, TinkerModifiers.strongBones.getId(), ModifierIds.skeletonDisguise);
    addTraits(MaterialIds.venombone,    SkullStats.ID, TinkerModifiers.frosttouch.getId(), ModifierIds.strayDisguise);
    addTraits(MaterialIds.necroticBone, SkullStats.ID, TinkerModifiers.withered.getId(), ModifierIds.witherSkeletonDisguise);
    addTraits(MaterialIds.string,       SkullStats.ID, TinkerModifiers.boonOfSssss.getId(), ModifierIds.spiderDisguise);
    addTraits(MaterialIds.darkthread,   SkullStats.ID, ModifierIds.mithridatism, ModifierIds.caveSpiderDisguise);
    addTraits(MaterialIds.rottenFlesh,  SkullStats.ID, TinkerModifiers.wildfire.getId(), ModifierIds.zombieDisguise);
    addTraits(MaterialIds.iron,         SkullStats.ID, TinkerModifiers.plague.getId(), ModifierIds.huskDisguise);
    addTraits(MaterialIds.copper,       SkullStats.ID, TinkerModifiers.breathtaking.getId(), ModifierIds.drownedDisguise);
    addTraits(MaterialIds.blazingBone,  SkullStats.ID, TinkerModifiers.firebreath.getId(), ModifierIds.blazeDisguise);
    addTraits(MaterialIds.gold,         SkullStats.ID, TinkerModifiers.chrysophilite.getId(), ModifierIds.piglinDisguise, TinkerModifiers.golden.getId());
    addTraits(MaterialIds.roseGold,     SkullStats.ID, TinkerModifiers.goldGuard.getId(), ModifierIds.piglinBruteDisguise, TinkerModifiers.golden.getId());
    addTraits(MaterialIds.pigIron,      SkullStats.ID, TinkerModifiers.revenge.getId(), ModifierIds.zombifiedPiglinDisguise);
    // slimesuit
    noTraits(MaterialIds.earthslime);
    noTraits(MaterialIds.skyslime);
    noTraits(MaterialIds.blood);
    noTraits(MaterialIds.magma);
    noTraits(MaterialIds.ichor);
    noTraits(MaterialIds.enderslime);
    noTraits(MaterialIds.clay);
    noTraits(MaterialIds.honey);
    noTraits(MaterialIds.phantom);
  }
}
