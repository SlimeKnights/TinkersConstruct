package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import static slimeknights.tconstruct.library.utils.HarvestLevels.DIAMOND;
import static slimeknights.tconstruct.library.utils.HarvestLevels.IRON;
import static slimeknights.tconstruct.library.utils.HarvestLevels.NETHERITE;
import static slimeknights.tconstruct.library.utils.HarvestLevels.STONE;
import static slimeknights.tconstruct.library.utils.HarvestLevels.WOOD;

public class MaterialStatsDataProvider extends AbstractMaterialStatsDataProvider {
  public MaterialStatsDataProvider(DataGenerator gen, AbstractMaterialDataProvider materials) {
    super(gen, materials);
  }

  @Override
  public String getName() {
    return "Tinker's Construct Material Stats";
  }

  @Override
  protected void addMaterialStats() {
    // head order is durability, mining speed, mining level, damage

    // tier 1
    // vanilla wood: 59, 2f, WOOD, 0f
    addMaterialStats(MaterialIds.wood,
                     new HeadMaterialStats(60, 2f, WOOD, 0f),
                     HandleMaterialStats.DEFAULT, // 1.0 to all four stats for wood, its the baseline handle
                     ExtraMaterialStats.DEFAULT);
    // vanilla stone: 131, 4f, STONE, 1f
    addMaterialStats(MaterialIds.stone,
                     new HeadMaterialStats(130, 4f, STONE, 1f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withMiningSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.flint,
                     new HeadMaterialStats(85, 3.5f, STONE, 1.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bone,
                     new HeadMaterialStats(100, 2.5f, STONE, 1.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.75f).withAttackSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    // tier 1 - nether
    addMaterialStats(MaterialIds.necroticBone,
                     new HeadMaterialStats(125, 2f, STONE, 1.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.65f).withAttackSpeed(1.15f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2
    // vanilla iron: 250, 6f, IRON, 2f
    addMaterialStats(MaterialIds.iron,
                     new HeadMaterialStats(250, 6f, IRON, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(1.10f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.copper,
                     new HeadMaterialStats(210, 6.5f, IRON, 1.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.85f).withMiningSpeed(1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.searedStone,
                     new HeadMaterialStats(150, 5f, IRON, 2.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackDamage(1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.scorchedStone,
                     new HeadMaterialStats(120, 4.5f, IRON, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackSpeed(1.05f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.slimewood,
                     new HeadMaterialStats(375, 4f, IRON, 1f),
                     HandleMaterialStats.DEFAULT.withDurability(1.3f).withMiningSpeed(0.85f).withAttackDamage(0.85f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bloodbone,
                     new HeadMaterialStats(195, 5f, IRON, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withAttackSpeed(1.1f).withAttackDamage(1.05f),
                     ExtraMaterialStats.DEFAULT);

    // tier 2 (mod integration)
    addMaterialStats(MaterialIds.silver,
                     new HeadMaterialStats(300, 5.5f, IRON, 2.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withMiningSpeed(1.05f).withAttackSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.lead,
                     new HeadMaterialStats(200, 5f, IRON, 2.5f),
                     HandleMaterialStats.DEFAULT.withAttackSpeed(0.8f).withAttackDamage(1.2f),
                     ExtraMaterialStats.DEFAULT);

    // tier 3
    // vanilla diamond: 1561, 8f, DIAMOND, 3f
    addMaterialStats(MaterialIds.slimesteel,
                     new HeadMaterialStats(1040, 6.5f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.2f).withAttackSpeed(0.95f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.tinkersBronze,
                     new HeadMaterialStats(720, 7f, DIAMOND, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(1.05f).withMiningSpeed(1.10f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.nahuatl,
                     new HeadMaterialStats(350, 4.5f, DIAMOND, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withAttackDamage(1.25f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.pigIron,
                     new HeadMaterialStats(580, 6f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.10f).withMiningSpeed(0.85f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    // vanilla gold: 32, 12f, WOOD, 0f
    addMaterialStats(MaterialIds.roseGold,
                     new HeadMaterialStats(175, 12f, IRON, 1f), // tier 2 mining level and durability despite being tier 3
                     HandleMaterialStats.DEFAULT.withDurability(0.6f).withMiningSpeed(1.25f).withAttackSpeed(1.25f),
                     ExtraMaterialStats.DEFAULT);
    // tier 3 (nether)
    addMaterialStats(MaterialIds.cobalt,
                     new HeadMaterialStats(800, 8f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.05f).withMiningSpeed(1.05f).withAttackSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);

    // tier 3 (mod integration)
    addMaterialStats(MaterialIds.steel,
                     new HeadMaterialStats(775, 7f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.05f).withMiningSpeed(1.05f).withAttackSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bronze,
                     new HeadMaterialStats(760, 7f, DIAMOND, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(1.10f).withMiningSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.constantan,
                     new HeadMaterialStats(675, 6.5f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.95f).withMiningSpeed(1.15f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.electrum,
                     new HeadMaterialStats(225, 11f, IRON, 1.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackSpeed(1.15f).withMiningSpeed(1.15f),
                     ExtraMaterialStats.DEFAULT);
    // tier 4
    // vanilla netherite: 2031, 9f, NETHERITE, 4f
    addMaterialStats(MaterialIds.queensSlime,
                     new HeadMaterialStats(1650, 7f, NETHERITE, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(1.35f).withMiningSpeed(0.9f).withAttackSpeed(0.95f).withAttackDamage(0.95f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.hepatizon,
                     new HeadMaterialStats(975, 10f, NETHERITE, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withMiningSpeed(1.2f).withAttackDamage(0.9f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.manyullyn,
                     new HeadMaterialStats(1250, 8f, NETHERITE, 3.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withMiningSpeed(0.9f).withAttackSpeed(0.9f).withAttackDamage(1.3f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.blazingBone,
                     new HeadMaterialStats(530, 6f, IRON, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(0.85f).withMiningSpeed(1.05f).withAttackSpeed(1.2f),
                     ExtraMaterialStats.DEFAULT);
    //    addMaterialStats(MaterialIds.soulsteel,
    //                     new HeadMaterialStats(1120, 7.5f, NETHERITE, 3f),
    //                     HandleMaterialStats.DEFAULT.withAttackSpeed(1.1f).withAttackDamage(1.3f),
    //                     ExtraMaterialStats.DEFAULT);
  }
}
