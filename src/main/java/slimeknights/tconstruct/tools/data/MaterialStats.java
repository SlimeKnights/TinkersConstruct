package slimeknights.tconstruct.tools.data;

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

final class MaterialStats {

  private static final int WOOD = 0;
  private static final int STONE = 1;
  private static final int IRON = 2;
  private static final int DIAMOND = 3;
  private static final int NETHERITE = 4;
  private static final int MANYULLYN = 5;

  static final Map<MaterialId, List<IMaterialStats>> allMaterialStats = new HashMap<>();

  static {
    //Tier 1
    addMaterialStats(MaterialIds.wood,
      new HeadMaterialStats(60, 2f, WOOD, 0f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.bone,
      new HeadMaterialStats(90, 4f, STONE, 2f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.stone,
      new HeadMaterialStats(130, 4f, STONE, 1f),
      new HandleMaterialStats(0, 1.0f, 1.1f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.paper,
      new HeadMaterialStats(40, 2f, WOOD, -1f),
      new HandleMaterialStats(0, 0.5f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.flint,
      new HeadMaterialStats(60, 4f, STONE, 2f),
      new HandleMaterialStats(0, 0.5f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.coral,
      new HeadMaterialStats(100, 4f, STONE, 0f),
      new HandleMaterialStats(0, 1f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));

    //Tier 2
    addMaterialStats(MaterialIds.copper,
      new HeadMaterialStats(210, 8f, STONE, 2f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.searedstone,
      new HeadMaterialStats(200, 6f, IRON, 3f),
      new HandleMaterialStats(0, 0.8f, 1.0f, 1.1f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.iron,
      new HeadMaterialStats(250, 6f, IRON, 2f),
      new HandleMaterialStats(0, 1.2f, 1.2f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.slimewood,
      new HeadMaterialStats(560, 4f, IRON, 1f),
      new HandleMaterialStats(0, 1.5f, 1.0f, 1.3f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.slimestone,
      new HeadMaterialStats(630, 4f, IRON, 1f),
      new HandleMaterialStats(0, 1.5f, 1.3f, 1.0f),
      new ExtraMaterialStats(0));

    //Tier 2 alternate
    addMaterialStats(MaterialIds.mushwood,
      new HeadMaterialStats(130, 6f, STONE, 0f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.netherrack,
      new HeadMaterialStats(200, 8f, STONE, 3f),
      new HandleMaterialStats(0, 0.8f, 1.0f, 1.1f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.blackstone,
      new HeadMaterialStats(250, 6f, STONE, 1f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.1f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.basalt,
      new HeadMaterialStats(400, 8f, STONE, 1f),
      new HandleMaterialStats(0, 1.0f, 1.2f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.bloodwood,
      new HeadMaterialStats(90, 4f, STONE, 1f),
      new HandleMaterialStats(0, 0.5f, 0.5f, 0.5f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.witherbone,
      new HeadMaterialStats(100, 4f, STONE, 2f),
      new HandleMaterialStats(0, 1f, 1f, 1f),
      new ExtraMaterialStats(0));

    //Tier 3
    addMaterialStats(MaterialIds.slimesteel,
      new HeadMaterialStats(640, 6f, DIAMOND, 3f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.nahuatl,
      new HeadMaterialStats(350, 6f, DIAMOND, 4f),
      new HandleMaterialStats(0, 0.75f, 1.0f, 1.2f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.bronze,
      new HeadMaterialStats(420, 10f, DIAMOND, 3f),
      new HandleMaterialStats(0, 1.4f, 1.3f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.pigiron,
      new HeadMaterialStats(300, 8f, DIAMOND, 3f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.rosegold,
      new HeadMaterialStats(250, 12f, DIAMOND, 1f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 0.5f),
      new ExtraMaterialStats(0));

    addMaterialStats(MaterialIds.cobalt,
      new HeadMaterialStats(1060, 10f, DIAMOND, 3f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.endstone,
      new HeadMaterialStats(420, 6f, DIAMOND, 3f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.chorus,
      new HeadMaterialStats(300, 4f, DIAMOND, 3f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));

    //Tier 4
    addMaterialStats(MaterialIds.soulsteel,
      new HeadMaterialStats(1120, 8f, NETHERITE, 4f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.heptazion,
      new HeadMaterialStats(920, 6f, NETHERITE, 6f),
      new HandleMaterialStats(0, 0.7f, 1.0f, 1.3f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.slimebronze,
      new HeadMaterialStats(1200, 14f, DIAMOND, 4f),
      new HandleMaterialStats(0, 1.4f, 1.6f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.blazewood,
      new HeadMaterialStats(10, 1f, NETHERITE, 1f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));

    //Tier 5
    addMaterialStats(MaterialIds.manyullyn,
      new HeadMaterialStats(1420, 14f, MANYULLYN, 6f),
      new HandleMaterialStats(0, 1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.knightslime,
      new HeadMaterialStats(2100, 18f, MANYULLYN, 5f),
      new HandleMaterialStats(0, 1.8f, 1.5f, 1.0f),
      new ExtraMaterialStats(0));
    addMaterialStats(MaterialIds.knightmetal,
      new HeadMaterialStats(1500, 8f, MANYULLYN, 8f),
      new HandleMaterialStats(0, 0.6f, 1.0f, 1.4f),
      new ExtraMaterialStats(0));
  }

  private static void addMaterialStats(MaterialId location, IMaterialStats... stats) {
    allMaterialStats.computeIfAbsent(location, materialId -> new ArrayList<>())
      .addAll(Arrays.asList(stats));
  }

  private MaterialStats() {
  }
}
