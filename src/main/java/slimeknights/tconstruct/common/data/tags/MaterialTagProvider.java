package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

public class MaterialTagProvider extends AbstractMaterialTagProvider {
  public MaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    // fiery is obtained through specific progression in TF, better to not add a progression bypass
    tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT).addOptional(MaterialIds.fiery);
    tag(TinkerTags.Materials.NETHER).add(
      // tier 1
      MaterialIds.wood, MaterialIds.flint, MaterialIds.rock, MaterialIds.bone,
      MaterialIds.leather, MaterialIds.string,
      // tier 2
      MaterialIds.gold, MaterialIds.slimewood,
      // tier 3
      MaterialIds.nahuatl, MaterialIds.obsidian, MaterialIds.darkthread, MaterialIds.steel,
      // tier 4
      MaterialIds.ancient
    ).addTag(TinkerTags.Materials.NETHER_GATED);

    // things that *require* nether access to craft
    tag(TinkerTags.Materials.NETHER_GATED).add(
      // tier 1
      MaterialIds.twistingVine, MaterialIds.weepingVine,
      // tier 2
      MaterialIds.scorchedStone, MaterialIds.necroticBone,
      // tier 3
      MaterialIds.cobalt,
      // tier 4
      MaterialIds.manyullyn, MaterialIds.hepatizon, MaterialIds.cinderslime,
      MaterialIds.queensSlime, MaterialIds.blazingBone, MaterialIds.blazewood,
      MaterialIds.ancientHide,
      // ammo
      MaterialIds.glowstone, MaterialIds.ichor, MaterialIds.quartz, MaterialIds.blaze, MaterialIds.magma
    ).addOptional(MaterialIds.necronium);

    // all materials to show in materials and you for ammo, kept to 9 options
    tag(TinkerTags.Materials.BASIC_AMMO).add(
      // head
      MaterialIds.flint, MaterialIds.wool, MaterialIds.glass,
      // shaft
      MaterialIds.wood, MaterialIds.bamboo, MaterialIds.bone,
      // fletching
      MaterialIds.feather, MaterialIds.paper, MaterialIds.leaves
    );

    // tier 4 is split into several parts in different books
    tag(TinkerTags.Materials.BLAZING_BLOOD).add(MaterialIds.manyullyn, MaterialIds.hepatizon, MaterialIds.queensSlime, MaterialIds.cinderslime, MaterialIds.blazingBone, MaterialIds.blazewood, MaterialIds.ancientHide);
    tag(TinkerTags.Materials.DISTANT).add(
      // tiers 1-2
      MaterialIds.chorus, MaterialIds.whitestone,
      // tier 4
      MaterialIds.knightmetal, MaterialIds.knightly, MaterialIds.enderslimeVine, MaterialIds.ancient,
      // ammo and maille
      MaterialIds.shulker, MaterialIds.dragonScale, MaterialIds.enderslime, MaterialIds.endRod
    ).addOptional(MaterialIds.ironwood, MaterialIds.steeleaf, MaterialIds.fiery);

    // materials bartered by piglins
    tag(TinkerTags.Materials.BARTERED).add(
      // tier 3
      MaterialIds.nahuatl, MaterialIds.obsidian, MaterialIds.darkthread,
      MaterialIds.cobalt, MaterialIds.steel,
      // tier 4
      MaterialIds.manyullyn, MaterialIds.hepatizon,
      MaterialIds.cinderslime, MaterialIds.queensSlime,
      MaterialIds.blazingBone, MaterialIds.blazewood,
      MaterialIds.ancientHide, MaterialIds.ancient
    ).addOptional(MaterialIds.necronium);

    // tag all compat materials
    tag(TinkerTags.Materials.COMPATABILITY_METALS).addOptional(
      // tier 2
      MaterialIds.silver, MaterialIds.lead, MaterialIds.aluminum,
      MaterialIds.osmium, MaterialIds.ironwood,
      // tier 3
      MaterialIds.steeleaf,
      // tier 4
      MaterialIds.fiery
    ).addTag(TinkerTags.Materials.COMPATABILITY_ALLOYS);
    tag(TinkerTags.Materials.COMPATABILITY_ALLOYS).addOptional(MaterialIds.bronze, MaterialIds.constantan, MaterialIds.invar, MaterialIds.electrum, MaterialIds.pewter);

    // material categories
    // melee harvest
    tag(TinkerTags.Materials.GENERAL).add(
      // tier 1
      MaterialIds.wood, MaterialIds.string, MaterialIds.vine, MaterialIds.leather,
      // tier 2
      MaterialIds.iron, MaterialIds.slimewood,
      // tier 3
      MaterialIds.slimesteel, MaterialIds.pigIron, MaterialIds.roseGold, MaterialIds.cobalt,
      // tier 4
      MaterialIds.cinderslime, MaterialIds.queensSlime, MaterialIds.enderslimeVine
    ).addOptional(
      // tier 1
      MaterialIds.treatedWood,
      // tier 2
      MaterialIds.osmium, MaterialIds.ironwood,
      // tier 3
      MaterialIds.platedSlimewood, MaterialIds.electrum, MaterialIds.steeleaf,
      // tier 4
      MaterialIds.fiery
    );
    tag(TinkerTags.Materials.HARVEST).add(
      // tier 1
      MaterialIds.rock, MaterialIds.copper,
      // tier 2
      MaterialIds.searedStone, MaterialIds.whitestone, MaterialIds.skyslimeVine, MaterialIds.twistingVine,
      // tier 3
      MaterialIds.amethystBronze,
      // tier 4
      MaterialIds.hepatizon, MaterialIds.ancientHide
    ).addOptional(
      // tier 2
      MaterialIds.lead,
      // tier 3
      MaterialIds.bronze, MaterialIds.constantan
    );
    tag(TinkerTags.Materials.MELEE).add(
      // tier 1
      MaterialIds.flint, MaterialIds.bone, MaterialIds.chorus,
      // tier 2
      MaterialIds.scorchedStone, MaterialIds.necroticBone, MaterialIds.venombone, MaterialIds.weepingVine,
      // tier 3
      MaterialIds.nahuatl, MaterialIds.steel, MaterialIds.darkthread,
      // tier 4
      MaterialIds.manyullyn, MaterialIds.blazingBone, MaterialIds.knightmetal
    ).addOptional(
      // tier 2
      MaterialIds.silver,
      // tier 3
      MaterialIds.invar, MaterialIds.pewter, MaterialIds.necronium
    );

    // ranged
    tag(TinkerTags.Materials.BALANCED).add(
      // tier 1
      MaterialIds.wood, MaterialIds.chorus,
      MaterialIds.string, MaterialIds.vine, MaterialIds.leather,
      // tier 2
      MaterialIds.slimewood, MaterialIds.necroticBone, MaterialIds.skyslimeVine,
      // tier 3
      MaterialIds.slimesteel, MaterialIds.roseGold, MaterialIds.darkthread, MaterialIds.cobalt,
      // tier 4
      MaterialIds.blazingBone, MaterialIds.ancientHide, MaterialIds.enderslimeVine
    ).addOptional(
      // tier 1
      MaterialIds.treatedWood,
      // tier 2
      MaterialIds.silver, MaterialIds.ironwood,
      // tier 3
      MaterialIds.invar, MaterialIds.pewter, MaterialIds.steeleaf
    );
    tag(TinkerTags.Materials.LIGHT).add(
      // tier 1
      MaterialIds.bamboo, MaterialIds.bone,
      // tier 2
      MaterialIds.venombone, MaterialIds.twistingVine,
      // tier 3
      MaterialIds.nahuatl,
      // tier 4
      MaterialIds.hepatizon, MaterialIds.queensSlime, MaterialIds.knightmetal
    ).addOptional(
      // tier 2
      MaterialIds.aluminum,
      // tier 3
      MaterialIds.necronium, MaterialIds.constantan, MaterialIds.platedSlimewood
    );
    tag(TinkerTags.Materials.HEAVY).add(
      // tier 1
      MaterialIds.copper,
      // tier 2
      MaterialIds.iron, MaterialIds.weepingVine,
      // tier 3
      MaterialIds.amethystBronze, MaterialIds.steel,
      // tier 4
      MaterialIds.manyullyn, MaterialIds.cinderslime
    ).addOptional(
      // tier 2
      MaterialIds.lead,
      // tier 3
      MaterialIds.bronze, MaterialIds.electrum,
      // tier 4
      MaterialIds.fiery
    );

    // slimeskull sort order
    tag(TinkerTags.Materials.SLIMESKULL).add(
      // creeper
      MaterialIds.glass,
      // zombie
      MaterialIds.leather, MaterialIds.iron, MaterialIds.copper,
      // spider
      MaterialIds.string, MaterialIds.darkthread,
      // skeleton
      MaterialIds.bone, MaterialIds.venombone, MaterialIds.necroticBone,
      // piglins
      MaterialIds.gold, MaterialIds.roseGold, MaterialIds.pigIron,
      // misc
      MaterialIds.blazingBone, MaterialIds.enderPearl
    );
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Tag Provider";
  }
}
