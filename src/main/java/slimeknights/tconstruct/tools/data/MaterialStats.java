package slimeknights.tconstruct.tools.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static slimeknights.tconstruct.library.utils.HarvestLevels.DIAMOND;
import static slimeknights.tconstruct.library.utils.HarvestLevels.IRON;
import static slimeknights.tconstruct.library.utils.HarvestLevels.NETHERITE;
import static slimeknights.tconstruct.library.utils.HarvestLevels.STONE;
import static slimeknights.tconstruct.library.utils.HarvestLevels.WOOD;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MaterialStats {
  static final Map<MaterialId, List<IMaterialStats>> allMaterialStats = new HashMap<>();

  static {
    // head order is durability, mining speed, mining level, damage

    // tier 1
    // vanilla wood: 59, 2f, WOOD, 0f
    addMaterialStats(MaterialIds.wood,
                     new HeadMaterialStats(60, 2f, WOOD, 0f),
                     HandleMaterialStats.DEFAULT, // 1.0 to all four stats for wood, its the baseline handle
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.flint,
                     new HeadMaterialStats(75, 3f, STONE, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(0.75f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    // vanilla stone: 131, 4f, STONE, 1f
    addMaterialStats(MaterialIds.stone,
                     new HeadMaterialStats(130, 4f, STONE, 1f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withMiningSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bone,
                     new HeadMaterialStats(100, 3f, STONE, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(0.85f).withAttackSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    // tier 2
    // vanilla iron: 250, 6f, IRON, 2f
    addMaterialStats(MaterialIds.iron,
                     new HeadMaterialStats(250, 6f, IRON, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(1.15f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.searedStone,
                     new HeadMaterialStats(200, 6f, IRON, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackDamage(1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.copper,
                     new HeadMaterialStats(210, 7f, IRON, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withMiningSpeed(1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.slimewood,
                     new HeadMaterialStats(560, 4f, IRON, 1f),
                     HandleMaterialStats.DEFAULT.withDurability(1.3f).withMiningSpeed(0.85f).withAttackDamage(0.85f),
                     ExtraMaterialStats.DEFAULT);
    // vanilla gold: 32, 12f, WOOD, 0f
    addMaterialStats(MaterialIds.roseGold,
                     new HeadMaterialStats(175, 12f, IRON, 1f),
                     HandleMaterialStats.DEFAULT.withDurability(0.6f).withAttackSpeed(1.25f).withMiningSpeed(1.3f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2 (mod integration)
    addMaterialStats(MaterialIds.silver,
                     new HeadMaterialStats(300, 6f, IRON, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withAttackSpeed(1.1f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.lead,
                     new HeadMaterialStats(350, 5.5f, IRON, 3f),
                     HandleMaterialStats.DEFAULT.withAttackSpeed(0.8f).withAttackDamage(1.3f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.electrum,
                     new HeadMaterialStats(225, 12f, IRON, 1.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackSpeed(1.15f).withMiningSpeed(1.2f),
                     ExtraMaterialStats.DEFAULT);

    // tier 3
    // vanilla diamond: 1561, 8f, DIAMOND, 3f
    addMaterialStats(MaterialIds.slimesteel,
                     new HeadMaterialStats(640, 6f, DIAMOND, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(1.25f).withAttackSpeed(0.95f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.nahuatl,
                     new HeadMaterialStats(350, 4f, DIAMOND, 4f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withAttackSpeed(0.9f).withAttackDamage(1.4f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.tinkersBronze,
                     new HeadMaterialStats(420, 8f, DIAMOND, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withMiningSpeed(1.15f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.pigIron,
                     new HeadMaterialStats(380, 7f, DIAMOND, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(1.15f).withAttackDamage(1.1f).withMiningSpeed(0.8f),
                     ExtraMaterialStats.DEFAULT);

    // tier 3 (mod integration)
    addMaterialStats(MaterialIds.steel,
                     new HeadMaterialStats(550, 7f, DIAMOND, 3.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.15f).withAttackSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bronze,
                     new HeadMaterialStats(450, 8f, DIAMOND, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(1.15f).withMiningSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.constantan,
                     new HeadMaterialStats(375, 8.5f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withMiningSpeed(1.3f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2 (nether)
//    addMaterialStats(MaterialIds.witherBone,
//                     new HeadMaterialStats(120, 5f, IRON, 2f),
//                     new HandleMaterialStats(0.85f, 1.1f, 0.9f),
//                     ExtraMaterialStats.DEFAULT);
    // tier 3 (nether)
    addMaterialStats(MaterialIds.cobalt,
                     new HeadMaterialStats(1060, 8.5f, DIAMOND, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withAttackSpeed(1.1f).withMiningSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    // tier 4
    // vanilla netherite: 2031, 9f, NETHERITE, 4f
    addMaterialStats(MaterialIds.queensSlime,
                     new HeadMaterialStats(1350, 9f, NETHERITE, 4f),
                     HandleMaterialStats.DEFAULT.withDurability(1.4f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.manyullyn,
                     new HeadMaterialStats(1500, 8f, NETHERITE, 5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.2f).withAttackDamage(1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.hepatizon,
                     new HeadMaterialStats(900, 10f, NETHERITE, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withAttackSpeed(0.9f).withMiningSpeed(1.4f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.soulsteel,
                     new HeadMaterialStats(1120, 7f, NETHERITE, 4f),
                     HandleMaterialStats.DEFAULT.withAttackSpeed(1.1f).withAttackDamage(1.3f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2 (end)
//    addMaterialStats(MaterialIds.endstone,
//                     new HeadMaterialStats(420, 6f, IRON, 3f),
//                     HandleMaterialStats.DEFAULT,
//                     ExtraMaterialStats.DEFAULT);
  }

  /**
   * Adds a set of material stats for the given material ID
   * @param location  Material ID
   * @param stats     Stats to add
   */
  private static void addMaterialStats(MaterialId location, IMaterialStats... stats) {
    allMaterialStats.computeIfAbsent(location, materialId -> new ArrayList<>())
      .addAll(Arrays.asList(stats));
  }
}
