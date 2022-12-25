package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.BowstringMaterialStats;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
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
    addMeleeHarvest();
    addRanged();
    addMisc();
  }

  private void addMeleeHarvest() {
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
    addMaterialStats(MaterialIds.chorus,
                     new HeadMaterialStats(180, 3.0f, STONE, 1.0f),
                     HandleMaterialStats.DEFAULT.withDurability(1.1f).withMiningSpeed(0.95f).withAttackSpeed(0.9f),
                     ExtraMaterialStats.DEFAULT);
    // tier 1 - binding
    addMaterialStats(MaterialIds.string, ExtraMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.leather, ExtraMaterialStats.DEFAULT);
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
    // tier 2 - end
    addMaterialStats(MaterialIds.whitestone,
                     new HeadMaterialStats(275, 6.0f, IRON, 1.25f),
                     HandleMaterialStats.DEFAULT.withDurability(0.95f).withMiningSpeed(1.1f).withAttackSpeed(0.95f),
                     ExtraMaterialStats.DEFAULT);
    // tier 2 - bindings
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
    // tier 3 - binding
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
    addMaterialStats(MaterialIds.ancientHide, ExtraMaterialStats.DEFAULT);

    // tier 4 (end)
    addMaterialStats(MaterialIds.enderslimeVine, ExtraMaterialStats.DEFAULT);
  }

  private void addRanged() {
    // limb order is durability, drawspeed, velocity, accuracy
    // grip order is durability, accuracy, melee

    // tier 1 - wood is basically the only one from vanilla so it has (mostly) vanilla stats
    addMaterialStats(MaterialIds.wood,
                     new LimbMaterialStats(60, 0, 0, 0),
                     new GripMaterialStats(1.0f, 0, 0));
    addMaterialStats(MaterialIds.bamboo,
                     new LimbMaterialStats(70, 0.1f, -0.05f, -0.05f),
                     new GripMaterialStats(0.95f, 0.05f, 0.75f));
    addMaterialStats(MaterialIds.bone,
                     new LimbMaterialStats(100, 0.05f, -0.05f, 0.05f),
                     new GripMaterialStats(0.75f, 0.05f, 1.25f));
    addMaterialStats(MaterialIds.copper,
                     new LimbMaterialStats(210, -0.10f, 0.05f, 0f),
                     new GripMaterialStats(0.8f, 0f, 0.5f));
    addMaterialStats(MaterialIds.chorus,
                     new LimbMaterialStats(180, 0.1f, 0f, 0.1f),
                     new GripMaterialStats(1.1f, -0.1f, 1.0f));
    // tier 1 - bowstring
    addMaterialStats(MaterialIds.string, BowstringMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.vine, BowstringMaterialStats.DEFAULT);

    // tier 2
    addMaterialStats(MaterialIds.slimewood,
                     new LimbMaterialStats(375, 0, -0.05f, 0.1f),
                     new GripMaterialStats(1.4f, -0.2f, 1f));
    addMaterialStats(MaterialIds.bloodbone,
                     new LimbMaterialStats(175, 0.1f, -0.1f, 0.05f),
                     new GripMaterialStats(0.9f, -0.1f, 2.25f));
    addMaterialStats(MaterialIds.iron,
                     new LimbMaterialStats(250, -0.2f, 0.1f, 0),
                     new GripMaterialStats(1.1f, 0f, 2f));
    addMaterialStats(MaterialIds.necroticBone,
                     new LimbMaterialStats(125, 0.05f, 0.05f, -0.15f),
                     new GripMaterialStats(0.7f, 0.1f, 2.25f));
    // tier 2 - bowstring
    addMaterialStats(MaterialIds.chain, BowstringMaterialStats.DEFAULT);
    addMaterialStats(MaterialIds.skyslimeVine, BowstringMaterialStats.DEFAULT);

    // tier 2 - compat
    addMaterialStats(MaterialIds.platinum,
                     new LimbMaterialStats(400, -0.05f, 0, 0.1f),
                     new GripMaterialStats(1.05f, 0.05f, 1.5f));
    addMaterialStats(MaterialIds.aluminum,
                     new LimbMaterialStats(225, 0.15f, -0.15f, -0.05f),
                     new GripMaterialStats(0.85f, 0.15f, 2f));
    addMaterialStats(MaterialIds.tungsten,
                     new LimbMaterialStats(350, 0.2f, -0.3f, 0),
                     new GripMaterialStats(0.9f, 0.1f, 1.75f));
    addMaterialStats(MaterialIds.silver,
                     new LimbMaterialStats(300, -0.15f, 0.1f, -0.1f),
                     new GripMaterialStats(0.9f, -0.05f, 2.25f));
    addMaterialStats(MaterialIds.lead,
                     new LimbMaterialStats(200, -0.3f, 0.15f, -0.05f),
                     new GripMaterialStats(0.9f, -0.1f, 2.5f));

    // tier 3
    addMaterialStats(MaterialIds.slimesteel,
                     new LimbMaterialStats(1040, -0.1f, -0.05f, 0.15f),
                     new GripMaterialStats(1.2f, -0.1f, 2.5f));
    addMaterialStats(MaterialIds.nahuatl,
                     new LimbMaterialStats(350, 0.2f, -0.15f, 0.1f),
                     new GripMaterialStats(0.9f, -0.15f, 3f));
    addMaterialStats(MaterialIds.amethystBronze,
                     new LimbMaterialStats(720, -0.25f, 0.15f, -0.1f),
                     new GripMaterialStats(1.0f, 0.1f, 1.5f));
    addMaterialStats(MaterialIds.roseGold,
                     new LimbMaterialStats(175, 0.15f, -0.25f, 0.15f),
                     new GripMaterialStats(0.6f, 0.25f, 1.0f));
    addMaterialStats(MaterialIds.cobalt,
                     new LimbMaterialStats(800, 0.05f, 0.05f, 0.05f),
                     new GripMaterialStats(1.05f, 0.05f, 2.25f));
    addMaterialStats(MaterialIds.blazingBone,
                     new LimbMaterialStats(530, -0.3f, 0.2f, -0.15f),
                     new GripMaterialStats(0.85f, -0.10f, 3f));
    // tier 3 - bowstring
    addMaterialStats(MaterialIds.darkthread, BowstringMaterialStats.DEFAULT);

    // tier 3 - compat
    addMaterialStats(MaterialIds.invar,
                     new LimbMaterialStats(630, -0.15f, -0.1f, 0.2f),
                     new GripMaterialStats(1, 0.05f, 2.5f));
    addMaterialStats(MaterialIds.necronium,
                     new LimbMaterialStats(357, 0.15f, -0.1f, -0.05f),
                     new GripMaterialStats(0.8f, 0.15f, 2.75f));
    addMaterialStats(MaterialIds.constantan,
                     new LimbMaterialStats(675, 0.2f, -0.05f, -0.25f),
                     new GripMaterialStats(0.95f, 0.1f, 1.75f));
    addMaterialStats(MaterialIds.steel,
                     new LimbMaterialStats(775, -0.3f, 0.2f, -0.1f),
                     new GripMaterialStats(1.05f, -0.05f, 2.75f));
    addMaterialStats(MaterialIds.bronze,
                     new LimbMaterialStats(760, -0.2f, 0.15f, -0.2f),
                     new GripMaterialStats(1.1f, 0f, 2.25f));
    addMaterialStats(MaterialIds.electrum,
                     new LimbMaterialStats(225, -0.25f, 0.1f, 0.15f),
                     new GripMaterialStats(0.8f, 0.2f, 1.5f));
    addMaterialStats(MaterialIds.platedSlimewood,
                     new LimbMaterialStats(595, 0.15f, -0.15f, 0),
                     new GripMaterialStats(1.25f, -0.1f, 2f));


    // tier 4
    addMaterialStats(MaterialIds.queensSlime,
                     new LimbMaterialStats(1650, 0f, -0.15f, 0.2f),
                     new GripMaterialStats(1.35f, -0.15f, 2f));
    addMaterialStats(MaterialIds.hepatizon,
                     new LimbMaterialStats(975, 0.25f, -0.05f, -0.10f),
                     new GripMaterialStats(1.1f, 0.15f, 2.5f));
    addMaterialStats(MaterialIds.manyullyn,
                     new LimbMaterialStats(1250, -0.35f, 0.25f, -0.15f),
                     new GripMaterialStats(1.1f, -0.20f, 3.5f));
    // tier 4 (end)
    addMaterialStats(MaterialIds.enderslimeVine, BowstringMaterialStats.DEFAULT);
  }

  private void addMisc() {
    // travelers gear
    addMaterialStats(MaterialIds.leather, new RepairKitStats(150));
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
