package slimeknights.tconstruct.tools.data;

import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.tools.stats.CommonMaterialStats;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HarvestMaterialStats;

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
      new CommonMaterialStats(35, 2.00f),
      new HarvestMaterialStats(2.00f, STONE),
      new HandleMaterialStats(1.00f, 25),
      new ExtraMaterialStats(15));

    addMaterialStats(MaterialIds.stone,
      new CommonMaterialStats(120, 3.00f),
      new HarvestMaterialStats(4.00f, IRON),
      new HandleMaterialStats(0.50f, -50),
      new ExtraMaterialStats(20));
    addMaterialStats(MaterialIds.flint,
      new CommonMaterialStats(150, 2.90f),
      new HarvestMaterialStats(5.00f, IRON),
      new HandleMaterialStats(0.60f, -60),
      new ExtraMaterialStats(40));
    addMaterialStats(MaterialIds.cactus,
      new CommonMaterialStats(210, 3.40f),
      new HarvestMaterialStats(4.00f, IRON),
      new HandleMaterialStats(0.85f, 20),
      new ExtraMaterialStats(50));
    addMaterialStats(MaterialIds.bone,
      new CommonMaterialStats(200, 2.50f),
      new HarvestMaterialStats(5.09f, IRON),
      new HandleMaterialStats(1.10f, 50),
      new ExtraMaterialStats(65));
    addMaterialStats(MaterialIds.obsidian,
      new CommonMaterialStats(139, 4.20f),
      new HarvestMaterialStats(7.07f, COBALT),
      new HandleMaterialStats(0.90f, -100),
      new ExtraMaterialStats(90));
    addMaterialStats(MaterialIds.prismarine,
      new CommonMaterialStats(430, 6.20f),
      new HarvestMaterialStats(5.50f, IRON),
      new HandleMaterialStats(0.60f, -150),
      new ExtraMaterialStats(100));
    addMaterialStats(MaterialIds.endstone,
      new CommonMaterialStats(420, 3.23f),
      new HarvestMaterialStats(3.23f, OBSIDIAN),
      new HandleMaterialStats(0.85f, 0),
      new ExtraMaterialStats(42));
    addMaterialStats(MaterialIds.paper,
      new CommonMaterialStats(12, 0.05f),
      new HarvestMaterialStats(0.51f, STONE),
      new HandleMaterialStats(0.10f, 5),
      new ExtraMaterialStats(15));
    addMaterialStats(MaterialIds.sponge,
      new CommonMaterialStats(1050, 0.00f),
      new HarvestMaterialStats(3.02f, STONE),
      new HandleMaterialStats(1.20f, 250),
      new ExtraMaterialStats(250));

    // Slime
    addMaterialStats(MaterialIds.slime,
      new CommonMaterialStats(1000, 1.80f),
      new HarvestMaterialStats(4.24f, STONE),
      new HandleMaterialStats(0.70f, 0),
      new ExtraMaterialStats(350));
    addMaterialStats(MaterialIds.blueslime,
      new CommonMaterialStats(780, 1.80f),
      new HarvestMaterialStats(4.03f, STONE),
      new HandleMaterialStats(1.30f, -50),
      new ExtraMaterialStats(200));
    addMaterialStats(MaterialIds.knightslime,
      new CommonMaterialStats(850, 5.10f),
      new HarvestMaterialStats(5.8f, OBSIDIAN),
      new HandleMaterialStats(0.50f, 500),
      new ExtraMaterialStats(125));
    addMaterialStats(MaterialIds.magmaslime,
      new CommonMaterialStats(600, 7.00f),
      new HarvestMaterialStats(2.1f, STONE),
      new HandleMaterialStats(0.85f, -200),
      new ExtraMaterialStats(150));

    // Nether
    addMaterialStats(MaterialIds.netherrack,
      new CommonMaterialStats(270, 3.00f),
      new HarvestMaterialStats(4.50f, IRON),
      new HandleMaterialStats(0.85f, -150),
      new ExtraMaterialStats(75));
    addMaterialStats(MaterialIds.cobalt,
      new CommonMaterialStats(780, 4.10f),
      new HarvestMaterialStats(12.00f, COBALT),
      new HandleMaterialStats(0.90f, 100),
      new ExtraMaterialStats(300));
    addMaterialStats(MaterialIds.ardite,
      new CommonMaterialStats(990, 3.60f),
      new HarvestMaterialStats(3.50f, COBALT),
      new HandleMaterialStats(1.40f, -200),
      new ExtraMaterialStats(450));
    addMaterialStats(MaterialIds.manyullyn,
      new CommonMaterialStats(820, 8.72f),
      new HarvestMaterialStats(7.02f, COBALT),
      new HandleMaterialStats(0.50f, 250),
      new ExtraMaterialStats(50));
    addMaterialStats(MaterialIds.firewood,
      new CommonMaterialStats(550, 5.50f),
      new HarvestMaterialStats(6.00f, STONE),
      new HandleMaterialStats(1.0f, -200),
      new ExtraMaterialStats(150));

    // Metals
    addMaterialStats(MaterialIds.iron,
      new CommonMaterialStats(204, 4.00f),
      new HarvestMaterialStats(6.00f, DIAMOND),
      new HandleMaterialStats(0.85f, 60),
      new ExtraMaterialStats(50));
    addMaterialStats(MaterialIds.pigiron,
      new CommonMaterialStats(380, 4.50f),
      new HarvestMaterialStats(6.20f, DIAMOND),
      new HandleMaterialStats(1.20f, 0),
      new ExtraMaterialStats(170));

    // Mod Integration
    addMaterialStats(MaterialIds.copper,
      new CommonMaterialStats(210, 3.00f),
      new HarvestMaterialStats(5.30f, IRON),
      new HandleMaterialStats(1.05f, 30),
      new ExtraMaterialStats(100));

    addMaterialStats(MaterialIds.bronze,
      new CommonMaterialStats(430, 3.50f),
      new HarvestMaterialStats(6.80f, DIAMOND),
      new HandleMaterialStats(1.10f, 70),
      new ExtraMaterialStats(80));

    addMaterialStats(MaterialIds.lead,
      new CommonMaterialStats(434, 3.50f),
      new HarvestMaterialStats(5.25f, IRON),
      new HandleMaterialStats(0.70f, -50),
      new ExtraMaterialStats(100));

    addMaterialStats(MaterialIds.silver,
      new CommonMaterialStats(250, 5.00f),
      new HarvestMaterialStats(5.00f, IRON),
      new HandleMaterialStats(0.95f, 50),
      new ExtraMaterialStats(150));

    addMaterialStats(MaterialIds.electrum,
      new CommonMaterialStats(50, 3.00f),
      new HarvestMaterialStats(12.00f, IRON),
      new HandleMaterialStats(1.10f, -25),
      new ExtraMaterialStats(250));

    addMaterialStats(MaterialIds.steel,
      new CommonMaterialStats(540, 6.00f),
      new HarvestMaterialStats(7.00f, OBSIDIAN),
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
