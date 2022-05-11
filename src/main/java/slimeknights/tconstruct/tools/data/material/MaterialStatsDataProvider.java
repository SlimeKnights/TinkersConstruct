package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.RepairKitStats;
import slimeknights.tconstruct.tools.stats.SkullStats;

import static net.minecraft.world.item.Tiers.DIAMOND;
import static net.minecraft.world.item.Tiers.GOLD;
import static net.minecraft.world.item.Tiers.IRON;
import static net.minecraft.world.item.Tiers.NETHERITE;
import static net.minecraft.world.item.Tiers.STONE;
import static net.minecraft.world.item.Tiers.WOOD;

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
    addMaterialStats(MaterialIds.rock,
                     new HeadMaterialStats(130, 4f, STONE, 1f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withMiningSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.flint,
                     new HeadMaterialStats(85, 3.5f, STONE, 1.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.85f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.copper,
                     new HeadMaterialStats(210, 5.0f, IRON, 0.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.80f).withMiningSpeed(1.1f).withAttackDamage(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bone,
                     new HeadMaterialStats(100, 2.5f, STONE, 1.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.75f).withAttackSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    // tier 1 - binding
    addMaterialStats(MaterialIds.string, ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.leather, ExtraMaterialStats.DEFAULT, new RepairKitStats(150));
    addMaterialStats(MaterialIds.vine, ExtraMaterialStats.DEFAULT);

    // tier 2
    // vanilla iron: 250, 6f, IRON, 2f
    addMaterialStats(MaterialIds.iron,
                     new HeadMaterialStats(250, 6f, IRON, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(1.10f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.searedStone,
                     new HeadMaterialStats(225, 6.5f, IRON, 1.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.85f).withMiningSpeed(1.10f).withAttackDamage(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bloodbone,
                     new HeadMaterialStats(175, 4.5f, IRON, 2.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withAttackSpeed(1.1f).withAttackDamage(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.slimewood,
                     new HeadMaterialStats(375, 4f, IRON, 1f),
                     HandleMaterialStats.DEFAULT.withDurability(1.3f).withMiningSpeed(0.85f).withAttackDamage(0.85f),
                     ExtraMaterialStats.DEFAULT);
    // tier 2 - nether
    addMaterialStats(MaterialIds.scorchedStone,
                     new HeadMaterialStats(120, 4.5f, IRON, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackSpeed(1.05f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.necroticBone,
                     new HeadMaterialStats(125, 4f, IRON, 2.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.7f).withAttackSpeed(1.15f).withAttackDamage(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.chain, ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.skyslimeVine, ExtraMaterialStats.DEFAULT);

    // tier 2 (mod integration)
    addMaterialStats(MaterialIds.osmium,
                     new HeadMaterialStats(500, 4.5f, IRON, 2.0f),
                     HandleMaterialStats.DEFAULT.withDurability(1.2f).withAttackSpeed(0.9f).withMiningSpeed(0.9f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.tungsten,
                     new HeadMaterialStats(350, 6.5f, IRON, 1.75f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withMiningSpeed(1.1f).withAttackSpeed(0.9f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.platinum,
                     new HeadMaterialStats(400, 7.0f, IRON, 1.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.05f).withMiningSpeed(1.05f).withAttackSpeed(0.95f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.silver,
                     new HeadMaterialStats(300, 5.5f, IRON, 2.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withMiningSpeed(1.05f).withAttackSpeed(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.lead,
                     new HeadMaterialStats(200, 5f, IRON, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withAttackSpeed(0.9f).withAttackDamage(1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.whitestone,
                     new HeadMaterialStats(275, 6.0f, IRON, 1.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.95f).withMiningSpeed(1.1f).withAttackSpeed(0.95f),
                     ExtraMaterialStats.DEFAULT);

    // tier 3
    // vanilla diamond: 1561, 8f, DIAMOND, 3f
    addMaterialStats(MaterialIds.slimesteel,
                     new HeadMaterialStats(1040, 6f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.2f).withAttackSpeed(0.95f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.amethystBronze,
                     new HeadMaterialStats(720, 7f, DIAMOND, 1.5f),
                     HandleMaterialStats.DEFAULT.withMiningSpeed(1.10f).withAttackSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.nahuatl,
                     new HeadMaterialStats(350, 4.5f, DIAMOND, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(0.9f).withAttackSpeed(0.9f).withAttackDamage(1.30f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.pigIron,
                     new HeadMaterialStats(580, 6f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.10f).withMiningSpeed(0.85f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    // vanilla gold: 32, 12f, WOOD, 0f
    addMaterialStats(MaterialIds.roseGold,
                     new HeadMaterialStats(175, 9f, GOLD, 1f), // gold mining level technically puts it in tier 0, but lets see if some mod does something weird
                     HandleMaterialStats.DEFAULT.withDurability(0.6f).withMiningSpeed(1.20f).withAttackSpeed(1.20f),
                     ExtraMaterialStats.DEFAULT);
    // tier 3 (nether)
    addMaterialStats(MaterialIds.cobalt,
                     new HeadMaterialStats(800, 6.5f, DIAMOND, 2.25f),
                     HandleMaterialStats.DEFAULT.withDurability(1.05f).withMiningSpeed(1.05f).withAttackSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.darkthread, ExtraMaterialStats.DEFAULT);

    // tier 3 (mod integration)
    addMaterialStats(MaterialIds.steel,
                     new HeadMaterialStats(775, 6f, DIAMOND, 2.75f),
                     HandleMaterialStats.DEFAULT.withDurability(1.05f).withMiningSpeed(1.05f).withAttackSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.bronze,
                     new HeadMaterialStats(760, 6.5f, DIAMOND, 2.25f),
                     HandleMaterialStats.DEFAULT.withDurability(1.10f).withMiningSpeed(1.05f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.constantan,
                     new HeadMaterialStats(675, 7.5f, DIAMOND, 1.75f),
                     HandleMaterialStats.DEFAULT.withDurability(0.95f).withMiningSpeed(1.15f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.invar,
                     new HeadMaterialStats(630, 5.5f, DIAMOND, 2.5f),
                     HandleMaterialStats.DEFAULT.withMiningSpeed(0.9f).withAttackDamage(1.2f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.necronium,
                     new HeadMaterialStats(357, 4.0f, DIAMOND, 2.75f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackSpeed(1.15f).withAttackDamage(1.1f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.electrum,
                     new HeadMaterialStats(225, 8.5f, IRON, 1.5f),
                     HandleMaterialStats.DEFAULT.withDurability(0.8f).withAttackSpeed(1.15f).withMiningSpeed(1.15f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.platedSlimewood,
                     new HeadMaterialStats(595, 5.0f, DIAMOND, 2.0f),
                     HandleMaterialStats.DEFAULT.withDurability(1.25f).withMiningSpeed(0.9f).withAttackSpeed(0.9f).withAttackDamage(1.05f),
                     ExtraMaterialStats.DEFAULT);

    // tier 4
    // vanilla netherite: 2031, 9f, NETHERITE, 4f
    addMaterialStats(MaterialIds.queensSlime,
                     new HeadMaterialStats(1650, 6f, NETHERITE, 2f),
                     HandleMaterialStats.DEFAULT.withDurability(1.35f).withMiningSpeed(0.9f).withAttackSpeed(0.95f).withAttackDamage(0.95f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.hepatizon,
                     new HeadMaterialStats(975, 8f, NETHERITE, 2.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withMiningSpeed(1.2f).withAttackDamage(0.9f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.manyullyn,
                     new HeadMaterialStats(1250, 6.5f, NETHERITE, 3.5f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withMiningSpeed(0.9f).withAttackSpeed(0.95f).withAttackDamage(1.25f),
                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.blazingBone,
                     new HeadMaterialStats(530, 6f, IRON, 3f),
                     HandleMaterialStats.DEFAULT.withDurability(0.85f).withAttackDamage(1.05f).withAttackSpeed(1.2f),
                     ExtraMaterialStats.DEFAULT);
    //    addMaterialStats(MaterialIds.soulsteel,
    //                     new HeadMaterialStats(1120, 7.5f, NETHERITE, 3f),
    //                     HandleMaterialStats.DEFAULT.withAttackSpeed(1.1f).withAttackDamage(1.3f),
    //                     ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.ancientHide, ExtraMaterialStats.DEFAULT);

    // tier 5
    addMaterialStats(MaterialIds.enderslimeVine, ExtraMaterialStats.DEFAULT);

    // slimeskull
    addMaterialStats(MaterialIds.glass,        new SkullStats( 90, 0));
    addMaterialStats(MaterialIds.enderPearl,   new SkullStats(180, 0));
    addMaterialStats(MaterialIds.bone,         new SkullStats(100, 0));
    addMaterialStats(MaterialIds.bloodbone,    new SkullStats(175, 1));
    addMaterialStats(MaterialIds.necroticBone, new SkullStats(125, 0));
    addMaterialStats(MaterialIds.string,       new SkullStats(140, 0));
    addMaterialStats(MaterialIds.darkthread,   new SkullStats(200, 1));
    addMaterialStats(MaterialIds.rottenFlesh,  new SkullStats( 45, 2));
    addMaterialStats(MaterialIds.iron,         new SkullStats(165, 2));
    addMaterialStats(MaterialIds.copper,       new SkullStats(145, 2));
    addMaterialStats(MaterialIds.blazingBone,  new SkullStats(205, 1));
    addMaterialStats(MaterialIds.gold,         new SkullStats(125, 0));
    addMaterialStats(MaterialIds.roseGold,     new SkullStats(175, 1));
    addMaterialStats(MaterialIds.pigIron,      new SkullStats(150, 2));
    // slimesuit
    addMaterialStats(MaterialIds.enderslime, new RepairKitStats( 30));
    addMaterialStats(MaterialIds.phantom,    new RepairKitStats(324));
    addMaterialStats(MaterialIds.chorus,     new RepairKitStats( 65));
    // crafting
    addMaterialStats(MaterialIds.obsidian);
    addMaterialStats(MaterialIds.debris);
    addMaterialStats(MaterialIds.netherite);
    addMaterialStats(MaterialIds.earthslime);
    addMaterialStats(MaterialIds.skyslime);
    addMaterialStats(MaterialIds.blood);
    addMaterialStats(MaterialIds.ichor);
    addMaterialStats(MaterialIds.clay);
    addMaterialStats(MaterialIds.honey);
    //addMaterialStats(MaterialIds.venom);
    // compat
    addMaterialStats(MaterialIds.aluminum);
    addMaterialStats(MaterialIds.nickel);
    addMaterialStats(MaterialIds.tin);
    addMaterialStats(MaterialIds.zinc);
    addMaterialStats(MaterialIds.brass);
    addMaterialStats(MaterialIds.uranium);
  }
}
