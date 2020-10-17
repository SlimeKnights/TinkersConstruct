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
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.bone,
      new HeadMaterialStats(90, 4f, STONE, 2f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
    new ExtraMaterialStats());
    addMaterialStats(MaterialIds.stone,
      new HeadMaterialStats(130, 4f, STONE, 1f),
      new HandleMaterialStats(1.0f, 1.1f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.coral,
      new HeadMaterialStats(100, 4f, STONE, 0f),
      new HandleMaterialStats(1f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.flint,
      new HeadMaterialStats(60, 4f, STONE, 2f),
      new HandleMaterialStats(0.5f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    /*addMaterialStats(MaterialIds.paper,
      new HeadMaterialStats(40, 2f, WOOD, -1f),
      new HandleMaterialStats(0.5f, 1.0f, 1.0f));*/

    //Tier 2
    addMaterialStats(MaterialIds.copper,
      new HeadMaterialStats(210, 8f, STONE, 2f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.searedstone,
      new HeadMaterialStats(200, 6f, IRON, 3f),
      new HandleMaterialStats(0.8f, 1.0f, 1.1f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.iron,
      new HeadMaterialStats(250, 6f, IRON, 2f),
      new HandleMaterialStats(1.2f, 1.2f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.slimewood,
      new HeadMaterialStats(560, 4f, IRON, 1f),
      new HandleMaterialStats(1.5f, 1.0f, 1.3f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.slimestone,
      new HeadMaterialStats(630, 4f, IRON, 1f),
      new HandleMaterialStats(1.5f, 1.3f, 1.0f),
      new ExtraMaterialStats());

    //Tier 2 alternate
    addMaterialStats(MaterialIds.mushwood,
      new HeadMaterialStats(130, 6f, STONE, 0f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.blackstone,
      new HeadMaterialStats(250, 6f, STONE, 1f),
      new HandleMaterialStats(1.0f, 1.0f, 1.1f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.basalt,
      new HeadMaterialStats(400, 8f, STONE, 1f),
      new HandleMaterialStats(1.0f, 1.2f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.bloodwood,
      new HeadMaterialStats(90, 4f, STONE, 1f),
      new HandleMaterialStats(0.5f, 0.5f, 0.5f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.witherbone,
      new HeadMaterialStats(100, 4f, STONE, 2f),
      new HandleMaterialStats(1f, 1f, 1f),
      new ExtraMaterialStats());

    //Tier 3
    addMaterialStats(MaterialIds.slimesteel,
      new HeadMaterialStats(640, 6f, DIAMOND, 3f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.nahuatl,
      new HeadMaterialStats(350, 6f, DIAMOND, 4f),
      new HandleMaterialStats(0.75f, 1.0f, 1.2f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.ravagersteel,
      new HeadMaterialStats(920, 6f, DIAMOND, 6f),
      new HandleMaterialStats(1.2f, 1.0f, 1.2f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.bronze,
      new HeadMaterialStats(420, 10f, DIAMOND, 3f),
      new HandleMaterialStats(1.4f, 1.3f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.pigiron,
      new HeadMaterialStats(300, 8f, DIAMOND, 3f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.rosegold,
      new HeadMaterialStats(250, 12f, DIAMOND, 1f),
      new HandleMaterialStats(1.0f, 1.0f, 0.5f),
      new ExtraMaterialStats());

    addMaterialStats(MaterialIds.cobalt,
      new HeadMaterialStats(1060, 10f, DIAMOND, 3f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.endstone,
      new HeadMaterialStats(420, 6f, DIAMOND, 3f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.chorus,
      new HeadMaterialStats(300, 4f, DIAMOND, 3f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());

    //Tier 4
    addMaterialStats(MaterialIds.soulsteel,
      new HeadMaterialStats(1120, 8f, NETHERITE, 4f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.alexandrite,
      new HeadMaterialStats(1140, 8f, NETHERITE, 4f),
      new HandleMaterialStats(1.0f, 1.2f, 1.2f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.heptazion,
      new HeadMaterialStats(920, 6f, NETHERITE, 6f),
      new HandleMaterialStats(0.7f, 1.0f, 1.3f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.magmastone,
      new HeadMaterialStats(1000, 8f, NETHERITE, 6f),
      new HandleMaterialStats(1.0f, 1.1f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.knightmetal,
      new HeadMaterialStats(1350, 10f, MANYULLYN, 4f),
      new HandleMaterialStats(1.3f, 1.1f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.slimebronze,
      new HeadMaterialStats(1200, 14f, DIAMOND, 4f),
      new HandleMaterialStats(1.4f, 1.6f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.blazewood,
      new HeadMaterialStats(10, 1f, NETHERITE, 1f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());

    //Tier 5
    addMaterialStats(MaterialIds.manyullyn,
      new HeadMaterialStats(1420, 14f, MANYULLYN, 6f),
      new HandleMaterialStats(1.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.knightslime,
      new HeadMaterialStats(2100, 18f, MANYULLYN, 5f),
      new HandleMaterialStats(1.8f, 1.0f, 1.6f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.rainbowslime,
      new HeadMaterialStats(2800, 4f, MANYULLYN, 0f),
      new HandleMaterialStats(2.0f, 1.0f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.dragonstone,
      new HeadMaterialStats(2100, 18f, MANYULLYN, 5f),
      new HandleMaterialStats(0.6f, 1.4f, 1.0f),
      new ExtraMaterialStats());
    addMaterialStats(MaterialIds.gardite,
      new HeadMaterialStats(3500, 4f, MANYULLYN, 4f),
      new HandleMaterialStats(1.5f, 1.0f, 1.0f),
      new ExtraMaterialStats());
  }

  private static void addMaterialStats(MaterialId location, IMaterialStats... stats) {
    allMaterialStats.computeIfAbsent(location, materialId -> new ArrayList<>())
      .addAll(Arrays.asList(stats));
  }

  private MaterialStats() {
  }
}
