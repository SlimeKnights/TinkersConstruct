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
      MaterialIds.leather, MaterialIds.vine, MaterialIds.string,
      // tier 2
      MaterialIds.gold,
      MaterialIds.scorchedStone, MaterialIds.slimewood, MaterialIds.necroticBone,
      // tier 3
      MaterialIds.nahuatl, MaterialIds.obsidian, MaterialIds.darkthread,
      MaterialIds.cobalt, MaterialIds.steel,
      // tier 4
      MaterialIds.manyullyn, MaterialIds.cinderslime,
      MaterialIds.queensSlime, MaterialIds.blazingBone, MaterialIds.blazewood,
      MaterialIds.ancientHide, MaterialIds.ancient
    );
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
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Tag Provider";
  }
}
