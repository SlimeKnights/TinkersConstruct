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
    // tier 1
    addMaterialStats(MaterialIds.wood,
                     new HeadMaterialStats(60, 2f, WOOD, 0f),
                     new HandleMaterialStats(1.0f, 1.0f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.flint,
                     new HeadMaterialStats(75, 4f, STONE, 2f),
                     new HandleMaterialStats(0.75f, 1.1f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.stone,
                     new HeadMaterialStats(130, 6f, STONE, 1f),
                     new HandleMaterialStats(0.75f, 1.0f, 1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bone,
                     new HeadMaterialStats(90, 5f, STONE, 2f),
                     new HandleMaterialStats(0.9f, 1.05f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    // tier 2
    addMaterialStats(MaterialIds.iron,
                     new HeadMaterialStats(250, 6f, IRON, 2f),
                     new HandleMaterialStats(1.2f, 1.0f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.searedStone,
                     new HeadMaterialStats(200, 6f, IRON, 3f),
                     new HandleMaterialStats(0.8f, 1.15f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.copper,
                     new HeadMaterialStats(210, 7f, IRON, 2f),
                     new HandleMaterialStats(0.8f, 1.0f, 1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.slimewood,
                     new HeadMaterialStats(560, 4f, IRON, 1f),
                     new HandleMaterialStats(1.4f, 0.8f, 0.8f),
                     ExtraMaterialStats.DEFAULT);

    // tier 3
    addMaterialStats(MaterialIds.slimesteel,
                     new HeadMaterialStats(640, 6f, DIAMOND, 3f),
                     new HandleMaterialStats(1.3f, 1.0f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.nahuatl,
                     new HeadMaterialStats(350, 4f, DIAMOND, 4f),
                     new HandleMaterialStats(0.9f, 1.2f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.tinkersBronze,
                     new HeadMaterialStats(420, 8f, DIAMOND, 3f),
                     new HandleMaterialStats(1.1f, 0.9f, 1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.roseGold,
                     new HeadMaterialStats(175, 12f, IRON, 1f),
                     new HandleMaterialStats(0.7f, 1.2f, 1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.pigIron,
                     new HeadMaterialStats(380, 7f, DIAMOND, 3f),
                     new HandleMaterialStats(1.15f, 1.1f, 0.85f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2 (nether)
//    addMaterialStats(MaterialIds.witherBone,
//                     new HeadMaterialStats(120, 5f, IRON, 2f),
//                     new HandleMaterialStats(0.85f, 1.1f, 0.9f),
//                     ExtraMaterialStats.DEFAULT);
    // tier 3 (nether)
    addMaterialStats(MaterialIds.cobalt,
                     new HeadMaterialStats(1060, 8.5f, DIAMOND, 3f),
                     new HandleMaterialStats(1.1f, 1.1f, 1.2f),
                     ExtraMaterialStats.DEFAULT);
    // tier 4
    addMaterialStats(MaterialIds.manyullyn,
                     new HeadMaterialStats(1500, 9f, NETHERITE, 4f),
                     new HandleMaterialStats(1.2f, 1.1f, 1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.hepatizon,
                     new HeadMaterialStats(920, 6f, NETHERITE, 5f),
                     new HandleMaterialStats(0.7f, 1.3f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.slimeBronze,
                     new HeadMaterialStats(1200, 10f, NETHERITE, 3f),
                     new HandleMaterialStats(1.1f, 0.9f, 1.3f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.soulsteel,
                     new HeadMaterialStats(1120, 7f, NETHERITE, 4f),
                     new HandleMaterialStats(1.0f, 1.1f, 1.1f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2 (end)
    addMaterialStats(MaterialIds.endstone,
                     new HeadMaterialStats(420, 6f, IRON, 3f),
                     new HandleMaterialStats(0.8f, 0.9f, 0.9f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2 (mod integration)
    addMaterialStats(MaterialIds.silver,
                     new HeadMaterialStats(300, 6f, IRON, 2.5f),
                     new HandleMaterialStats(1.1f, 1.0f, 1.0f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.lead,
                     new HeadMaterialStats(350, 5.5f, IRON, 3f),
                     new HandleMaterialStats(1.1f, 0.8f, 0.8f),
                     ExtraMaterialStats.DEFAULT);

    // tier 3 (mod integration)
    addMaterialStats(MaterialIds.electrum,
                     new HeadMaterialStats(225, 12f, IRON, 1.5f),
                     new HandleMaterialStats(0.8f, 1.1f, 1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bronze,
                     new HeadMaterialStats(450, 8f, DIAMOND, 3f),
                     new HandleMaterialStats(1.2f, 0.95f, 1.15f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.steel,
                     new HeadMaterialStats(550, 7f, DIAMOND, 3.5f),
                     new HandleMaterialStats(1.2f, 1.0f, 1.1f),
                     ExtraMaterialStats.DEFAULT);
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
