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

  private static final int STONE = 0;
  private static final int IRON = 1;
  private static final int DIAMOND = 2;
  private static final int OBSIDIAN = 3;
  private static final int COBALT = 4;

  static final Map<MaterialId, List<IMaterialStats>> allMaterialStats = new HashMap<>();

  static {
    addMaterialStats(MaterialIds.wood,
        new HeadMaterialStats(35, 2.00f, STONE, 2.00f),
        new HandleMaterialStats(1.00f, 25),
        new ExtraMaterialStats(15));

    addMaterialStats(MaterialIds.stone,
        new HeadMaterialStats(120, 4.00f, IRON, 3.00f),
        new HandleMaterialStats(0.50f, -50),
        new ExtraMaterialStats(20));
    addMaterialStats(MaterialIds.flint,
        new HeadMaterialStats(150, 5.00f, IRON, 2.90f),
        new HandleMaterialStats(0.60f, -60),
        new ExtraMaterialStats(40));
    addMaterialStats(MaterialIds.cactus,
        new HeadMaterialStats(210, 4.00f, IRON, 3.40f),
        new HandleMaterialStats(0.85f, 20),
        new ExtraMaterialStats(50));
    addMaterialStats(MaterialIds.bone,
        new HeadMaterialStats(200, 5.09f, IRON, 2.50f),
        new HandleMaterialStats(1.10f, 50),
        new ExtraMaterialStats(65));
    addMaterialStats(MaterialIds.obsidian,
        new HeadMaterialStats(139, 7.07f, COBALT, 4.20f),
        new HandleMaterialStats(0.90f, -100),
        new ExtraMaterialStats(90));
    addMaterialStats(MaterialIds.prismarine,
        new HeadMaterialStats(430, 5.50f, IRON, 6.20f),
        new HandleMaterialStats(0.60f, -150),
        new ExtraMaterialStats(100));
    addMaterialStats(MaterialIds.endstone,
        new HeadMaterialStats(420, 3.23f, OBSIDIAN, 3.23f),
        new HandleMaterialStats(0.85f, 0),
        new ExtraMaterialStats(42));
    addMaterialStats(MaterialIds.paper,
        new HeadMaterialStats(12, 0.51f, STONE, 0.05f),
        new HandleMaterialStats(0.10f, 5),
        new ExtraMaterialStats(15));
    addMaterialStats(MaterialIds.sponge,
        new HeadMaterialStats(1050, 3.02f, STONE, 0.00f),
        new HandleMaterialStats(1.20f, 250),
        new ExtraMaterialStats(250));

    // Slime
    addMaterialStats(MaterialIds.slime,
        new HeadMaterialStats(1000, 4.24f, STONE, 1.80f),
        new HandleMaterialStats(0.70f, 0),
        new ExtraMaterialStats(350));
    addMaterialStats(MaterialIds.blueslime,
        new HeadMaterialStats(780, 4.03f, STONE, 1.80f),
        new HandleMaterialStats(1.30f, -50),
        new ExtraMaterialStats(200));
    addMaterialStats(MaterialIds.knightslime,
        new HeadMaterialStats(850, 5.8f, OBSIDIAN, 5.10f),
        new HandleMaterialStats(0.50f, 500),
        new ExtraMaterialStats(125));
    addMaterialStats(MaterialIds.magmaslime,
        new HeadMaterialStats(600, 2.1f, STONE, 7.00f),
        new HandleMaterialStats(0.85f, -200),
        new ExtraMaterialStats(150));

    // Nether
    addMaterialStats(MaterialIds.netherrack,
        new HeadMaterialStats(270, 4.50f, IRON, 3.00f),
        new HandleMaterialStats(0.85f, -150),
        new ExtraMaterialStats(75));
    addMaterialStats(MaterialIds.cobalt,
        new HeadMaterialStats(780, 12.00f, COBALT, 4.10f),
        new HandleMaterialStats(0.90f, 100),
        new ExtraMaterialStats(300));
    addMaterialStats(MaterialIds.ardite,
        new HeadMaterialStats(990, 3.50f, COBALT, 3.60f),
        new HandleMaterialStats(1.40f, -200),
        new ExtraMaterialStats(450));
    addMaterialStats(MaterialIds.manyullyn,
        new HeadMaterialStats(820, 7.02f, COBALT, 8.72f),
        new HandleMaterialStats(0.50f, 250),
        new ExtraMaterialStats(50));
    addMaterialStats(MaterialIds.firewood,
        new HeadMaterialStats(550, 6.00f, STONE, 5.50f),
        new HandleMaterialStats(1.0f, -200),
        new ExtraMaterialStats(150));

    // Metals
    addMaterialStats(MaterialIds.iron,
        new HeadMaterialStats(204, 6.00f, DIAMOND, 4.00f),
        new HandleMaterialStats(0.85f, 60),
        new ExtraMaterialStats(50));
    addMaterialStats(MaterialIds.pigiron,
        new HeadMaterialStats(380, 6.20f, DIAMOND, 4.50f),
        new HandleMaterialStats(1.20f, 0),
        new ExtraMaterialStats(170));

    // Mod Integration
    addMaterialStats(MaterialIds.copper,
        new HeadMaterialStats(210, 5.30f, IRON, 3.00f),
        new HandleMaterialStats(1.05f, 30),
        new ExtraMaterialStats(100));

    addMaterialStats(MaterialIds.bronze,
        new HeadMaterialStats(430, 6.80f, DIAMOND, 3.50f),
        new HandleMaterialStats(1.10f, 70),
        new ExtraMaterialStats(80));

    addMaterialStats(MaterialIds.lead,
        new HeadMaterialStats(434, 5.25f, IRON, 3.50f),
        new HandleMaterialStats(0.70f, -50),
        new ExtraMaterialStats(100));

    addMaterialStats(MaterialIds.silver,
        new HeadMaterialStats(250, 5.00f, IRON, 5.00f),
        new HandleMaterialStats(0.95f, 50),
        new ExtraMaterialStats(150));

    addMaterialStats(MaterialIds.electrum,
        new HeadMaterialStats(50, 12.00f, IRON, 3.00f),
        new HandleMaterialStats(1.10f, -25),
        new ExtraMaterialStats(250));

    addMaterialStats(MaterialIds.steel,
        new HeadMaterialStats(540, 7.00f, OBSIDIAN, 6.00f),
        new HandleMaterialStats(0.9f, 150),
        new ExtraMaterialStats(25));
  }

  private static void addMaterialStats(MaterialId location, IMaterialStats... stats) {
    allMaterialStats.computeIfAbsent(location, materialId -> new ArrayList<>())
      .addAll(Arrays.asList(stats));
  }

  private MaterialStats() {
  }
}
