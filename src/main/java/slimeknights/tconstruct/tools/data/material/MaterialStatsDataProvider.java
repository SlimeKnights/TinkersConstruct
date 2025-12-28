package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.SkullStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import static net.minecraft.world.item.Tiers.DIAMOND;
import static net.minecraft.world.item.Tiers.GOLD;
import static net.minecraft.world.item.Tiers.IRON;
import static net.minecraft.world.item.Tiers.NETHERITE;
import static net.minecraft.world.item.Tiers.STONE;
import static net.minecraft.world.item.Tiers.WOOD;

public class MaterialStatsDataProvider extends AbstractMaterialStatsDataProvider {
  public MaterialStatsDataProvider(PackOutput packOutput, AbstractMaterialDataProvider materials) {
    super(packOutput, materials);
  }

  @Override
  public String getName() {
    return "Tinker's Construct Material Stats";
  }

  @Override
  protected void addMaterialStats() {
    addMeleeHarvest();
    addRanged();
    addAmmo();
    addArmor();
    addMisc();
  }

  private void addMeleeHarvest() {
    // head order is durability, mining speed, mining level, damage

    // tier 1
    // vanilla wood: 59, 2f, WOOD, 0f
    addMaterialStats(MaterialIds.wood,
                     new HeadMaterialStats(60, 2f, WOOD, 0f),
                     HandleMaterialStats.percents().build(), // flat all around
                     StatlessMaterialStats.BINDING);
    // vanilla stone: 131, 4f, STONE, 1f
    addMaterialStats(MaterialIds.rock,
                     new HeadMaterialStats(130, 4f, STONE, 1f),
                     HandleMaterialStats.multipliers().durability(0.9f).miningSpeed(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.flint,
                     new HeadMaterialStats(85, 3.5f, STONE, 1.25f),
                     HandleMaterialStats.multipliers().durability(0.85f).attackDamage(1.1f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.copper,
                     new HeadMaterialStats(210, 5.0f, IRON, 0.5f),
                     HandleMaterialStats.multipliers().durability(0.80f).miningSpeed(1.1f).attackDamage(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.bone,
                     new HeadMaterialStats(100, 2.5f, STONE, 1.25f),
                     HandleMaterialStats.multipliers().durability(0.75f).attackSpeed(1.1f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.chorus,
                     new HeadMaterialStats(180, 3.0f, STONE, 1.0f),
                     HandleMaterialStats.multipliers().durability(1.1f).miningSpeed(0.95f).attackSpeed(0.9f).build(),
                     StatlessMaterialStats.BINDING);
    // tier 1 - binding
    addMaterialStats(MaterialIds.string, StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.leather, StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.vine, StatlessMaterialStats.BINDING);

    // tier 2
    // vanilla iron: 250, 6f, IRON, 2f
    addMaterialStats(MaterialIds.iron,
                     new HeadMaterialStats(250, 6f, IRON, 2f),
                     HandleMaterialStats.multipliers().durability(1.10f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.searedStone,
                     new HeadMaterialStats(225, 6.5f, IRON, 1.5f),
                     HandleMaterialStats.multipliers().durability(0.85f).miningSpeed(1.10f).attackDamage(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.venombone,
                     new HeadMaterialStats(175, 4.5f, IRON, 2.25f),
                     HandleMaterialStats.multipliers().durability(0.9f).attackSpeed(1.1f).attackDamage(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.slimewood,
                     new HeadMaterialStats(375, 4f, IRON, 1f),
                     HandleMaterialStats.multipliers().durability(1.3f).miningSpeed(0.85f).attackDamage(0.85f).build(),
                     StatlessMaterialStats.BINDING);
    // tier 2 - nether
    addMaterialStats(MaterialIds.scorchedStone,
                     new HeadMaterialStats(120, 4.5f, IRON, 2.5f),
                     HandleMaterialStats.multipliers().durability(0.8f).attackSpeed(1.05f).attackDamage(1.1f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.necroticBone,
                     new HeadMaterialStats(125, 4f, IRON, 2.25f),
                     HandleMaterialStats.multipliers().durability(0.7f).attackSpeed(1.15f).attackDamage(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    // tier 2 - end
    addMaterialStats(MaterialIds.whitestone,
                     new HeadMaterialStats(275, 6.0f, IRON, 1.25f),
                     HandleMaterialStats.multipliers().durability(0.95f).miningSpeed(1.1f).attackSpeed(0.95f).build(),
                     StatlessMaterialStats.BINDING);
    // tier 2 - bindings
    addMaterialStats(MaterialIds.skyslimeVine, StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.weepingVine, StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.twistingVine, StatlessMaterialStats.BINDING);

    // tier 2 (mod integration)
    addMaterialStats(MaterialIds.osmium,
                     new HeadMaterialStats(500, 4.5f, IRON, 2.0f),
                     HandleMaterialStats.multipliers().durability(1.2f).attackSpeed(0.9f).miningSpeed(0.9f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.silver,
                     new HeadMaterialStats(300, 5.5f, IRON, 2.25f),
                     HandleMaterialStats.multipliers().durability(0.9f).miningSpeed(1.05f).attackSpeed(1.1f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.lead,
                     new HeadMaterialStats(200, 6.5f, IRON, 1.75f),
                     HandleMaterialStats.multipliers().durability(0.9f).miningSpeed(1.1f).attackSpeed(0.9f).attackDamage(1.1f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.treatedWood,
      new HeadMaterialStats(300, 3.5f, STONE, 1.5f),
      HandleMaterialStats.multipliers().durability(1.25f).attackDamage(0.9f).miningSpeed(0.9f).build(),
      StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.ironwood,
      new HeadMaterialStats(512, 6.5f, IRON, 2f),
      HandleMaterialStats.multipliers().durability(1.15f).attackSpeed(0.95f).build(),
      StatlessMaterialStats.BINDING);


    // tier 3
    // vanilla diamond: 1561, 8f, DIAMOND, 3f
    addMaterialStats(MaterialIds.slimesteel,
                     new HeadMaterialStats(1040, 6f, DIAMOND, 2.5f),
                     HandleMaterialStats.multipliers().durability(1.2f).attackSpeed(0.95f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.amethystBronze,
                     new HeadMaterialStats(720, 7f, DIAMOND, 1.5f),
                     HandleMaterialStats.multipliers().miningSpeed(1.10f).attackSpeed(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.nahuatl,
                     new HeadMaterialStats(350, 4.5f, DIAMOND, 3f),
                     HandleMaterialStats.multipliers().durability(0.9f).attackSpeed(0.9f).attackDamage(1.25f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.pigIron,
                     new HeadMaterialStats(580, 6f, DIAMOND, 2.5f),
                     HandleMaterialStats.multipliers().durability(1.10f).miningSpeed(0.85f).attackDamage(1.15f).build(),
                     StatlessMaterialStats.BINDING);
    // vanilla gold: 32, 12f, WOOD, 0f
    addMaterialStats(MaterialIds.roseGold,
                     new HeadMaterialStats(175, 9f, GOLD, 1f), // gold mining level technically puts it in tier 0, but lets see if some mod does something weird
                     HandleMaterialStats.multipliers().durability(0.7f).miningSpeed(1.10f).attackSpeed(1.10f).build(),
                     StatlessMaterialStats.BINDING);
    // tier 3 (nether)
    addMaterialStats(MaterialIds.cobalt,
                     new HeadMaterialStats(800, 6.5f, DIAMOND, 2.25f),
                     HandleMaterialStats.multipliers().durability(1.05f).miningSpeed(1.05f).attackSpeed(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.steel,
                     new HeadMaterialStats(775, 6f, DIAMOND, 2.75f),
                     HandleMaterialStats.multipliers().durability(1.05f).miningSpeed(1.05f).attackSpeed(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    // tier 3 - binding
    addMaterialStats(MaterialIds.darkthread, StatlessMaterialStats.BINDING);

    // tier 3 (mod integration)
    addMaterialStats(MaterialIds.bronze,
                     new HeadMaterialStats(760, 6.5f, DIAMOND, 2.25f),
                     HandleMaterialStats.multipliers().durability(1.10f).miningSpeed(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.constantan,
                     new HeadMaterialStats(675, 7.5f, DIAMOND, 1.75f),
                     HandleMaterialStats.multipliers().durability(0.95f).miningSpeed(1.15f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.invar,
                     new HeadMaterialStats(630, 5.5f, DIAMOND, 2.5f),
                     HandleMaterialStats.multipliers().miningSpeed(0.9f).attackSpeed(1.05f).attackDamage(1.15f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.pewter,
      new HeadMaterialStats(316, 3.5f, DIAMOND, 3.0f),
      HandleMaterialStats.multipliers().durability(0.75f).miningSpeed(0.8f).attackDamage(1.2f).build(),
      StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.necronium,
                     new HeadMaterialStats(357, 4.0f, DIAMOND, 2.75f),
                     HandleMaterialStats.multipliers().durability(0.8f).attackSpeed(1.15f).attackDamage(1.1f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.electrum,
                     new HeadMaterialStats(225, 8f, IRON, 1.5f),
                     HandleMaterialStats.multipliers().durability(0.65f).attackSpeed(1.15f).miningSpeed(1.15f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.platedSlimewood,
                     new HeadMaterialStats(595, 5.0f, DIAMOND, 2.0f),
                     HandleMaterialStats.multipliers().durability(1.25f).miningSpeed(0.9f).attackSpeed(0.9f).attackDamage(1.05f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.steeleaf,
      new HeadMaterialStats(200, 8, DIAMOND, 3),
      HandleMaterialStats.multipliers().durability(0.65f).attackSpeed(1.15f).miningSpeed(1.15f).build(),
      StatlessMaterialStats.BINDING);

    // tier 4
    // vanilla netherite: 2031, 9f, NETHERITE, 4f
    addMaterialStats(MaterialIds.cinderslime,
                     new HeadMaterialStats(1221, 6.5f, NETHERITE, 2.25f),
                     HandleMaterialStats.multipliers().durability(1.2f).miningSpeed(1.1f).attackSpeed(0.90f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.queensSlime,
                     new HeadMaterialStats(1650, 6f, NETHERITE, 2f),
                     HandleMaterialStats.multipliers().durability(1.35f).miningSpeed(0.9f).attackSpeed(0.95f).attackDamage(0.95f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.hepatizon,
                     new HeadMaterialStats(975, 8f, NETHERITE, 2.5f),
                     HandleMaterialStats.multipliers().durability(1.1f).miningSpeed(1.2f).attackDamage(0.9f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.manyullyn,
                     new HeadMaterialStats(1250, 6.5f, NETHERITE, 3.5f),
                     HandleMaterialStats.multipliers().durability(1.1f).miningSpeed(0.9f).attackSpeed(0.95f).attackDamage(1.20f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.blazingBone,
                     new HeadMaterialStats(530, 6f, IRON, 3f),
                     HandleMaterialStats.multipliers().durability(0.85f).attackDamage(1.05f).attackSpeed(1.2f).build(),
                     StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.ancientHide, StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.ancient, new HeadMaterialStats(745, 7f, NETHERITE, 2.5f));

    // tier 4 (end)
    addMaterialStats(MaterialIds.knightmetal,
      new HeadMaterialStats(512, 8f, NETHERITE, 3.0f),
      HandleMaterialStats.multipliers().miningSpeed(0.85f).attackSpeed(1.05f).attackDamage(1.15f).build(),
      StatlessMaterialStats.BINDING);
    addMaterialStats(MaterialIds.enderslimeVine, StatlessMaterialStats.BINDING);

    // tier 4 (mod integration)
    addMaterialStats(MaterialIds.fiery,
      new HeadMaterialStats(1024, 8, NETHERITE, 3.5f),
      HandleMaterialStats.multipliers().miningSpeed(1.15f).attackSpeed(0.9f).attackDamage(1.15f).build(),
      StatlessMaterialStats.BINDING);
  }

  private void addRanged() {
    // limb order is durability, drawspeed, velocity, accuracy
    // grip order is durability, accuracy, melee

    // tier 1 - wood is basically the only one from vanilla so it has (mostly) vanilla stats
    addMaterialStats(MaterialIds.wood,
                     new LimbMaterialStats(60, 0, 0, 0),
                     new GripMaterialStats(0f, 0, 0));
    addMaterialStats(MaterialIds.bamboo,
                     new LimbMaterialStats(70, 0.1f, -0.05f, -0.05f),
                     new GripMaterialStats(-0.05f, 0.05f, 0.75f));
    addMaterialStats(MaterialIds.bone,
                     new LimbMaterialStats(100, 0.05f, -0.05f, 0.05f),
                     new GripMaterialStats(-0.25f, 0.05f, 1.25f));
    addMaterialStats(MaterialIds.copper,
                     new LimbMaterialStats(210, -0.10f, 0.05f, 0f),
                     new GripMaterialStats(-0.2f, 0f, 0.5f));
    addMaterialStats(MaterialIds.chorus,
                     new LimbMaterialStats(180, -0.05f, -0.05f, 0.1f),
                     new GripMaterialStats(0.1f, -0.1f, 1.0f));
    // tier 1 - bowstring
    addMaterialStats(MaterialIds.string, StatlessMaterialStats.BOWSTRING);
    addMaterialStats(MaterialIds.vine, StatlessMaterialStats.BOWSTRING);
    addMaterialStats(MaterialIds.leather, StatlessMaterialStats.BOWSTRING);

    // tier 2
    addMaterialStats(MaterialIds.slimewood,
                     new LimbMaterialStats(375, 0, -0.05f, 0.1f),
                     new GripMaterialStats(0.4f, -0.2f, 1f));
    addMaterialStats(MaterialIds.venombone,
                     new LimbMaterialStats(175, 0.1f, -0.1f, 0.05f),
                     new GripMaterialStats(-0.1f, -0.1f, 2.25f));
    addMaterialStats(MaterialIds.iron,
                     new LimbMaterialStats(250, -0.2f, 0.1f, 0),
                     new GripMaterialStats(0.1f, 0f, 2f));
    addMaterialStats(MaterialIds.necroticBone,
                     new LimbMaterialStats(125, 0.05f, 0.05f, -0.15f),
                     new GripMaterialStats(-0.3f, 0.1f, 2.25f));
    // tier 2 - bowstring
    addMaterialStats(MaterialIds.skyslimeVine, StatlessMaterialStats.BOWSTRING);
    addMaterialStats(MaterialIds.weepingVine, StatlessMaterialStats.BOWSTRING);
    addMaterialStats(MaterialIds.twistingVine, StatlessMaterialStats.BOWSTRING);
    addMaterialStats(MaterialIds.slimeskin, StatlessMaterialStats.BOWSTRING);

    // tier 2 - compat
    addMaterialStats(MaterialIds.aluminum,
                     new LimbMaterialStats(225, 0.15f, -0.15f, -0.05f),
                     new GripMaterialStats(-0.15f, 0.15f, 2f));
    addMaterialStats(MaterialIds.silver,
                     new LimbMaterialStats(300, -0.05f, 0, 0.1f),
                     new GripMaterialStats(-0.1f, -0.05f, 2.25f));
    addMaterialStats(MaterialIds.lead,
                     new LimbMaterialStats(200, -0.3f, 0.15f, -0.05f),
                     new GripMaterialStats(-0.1f, 0.1f, 1.75f));
    addMaterialStats(MaterialIds.treatedWood,
      new LimbMaterialStats(300, 0.05f, -0.1f, 0.05f),
      new GripMaterialStats(0.25f, -0.15f, 1.5f));
    addMaterialStats(MaterialIds.ironwood,
      new LimbMaterialStats(512, 0.05f, 0.05f, -0.15f),
      new GripMaterialStats(0.15f, -0.15f, 2f));

    // tier 3
    addMaterialStats(MaterialIds.slimesteel,
                     new LimbMaterialStats(1040, -0.05f, -0.05f, 0.15f),
                     new GripMaterialStats(0.2f, -0.1f, 2.5f));
    addMaterialStats(MaterialIds.nahuatl,
                     new LimbMaterialStats(350, 0.2f, -0.15f, 0.1f),
                     new GripMaterialStats(-0.1f, -0.15f, 3f));
    addMaterialStats(MaterialIds.amethystBronze,
                     new LimbMaterialStats(720, -0.25f, 0.15f, -0.1f),
                     new GripMaterialStats(0f, 0.1f, 1.5f));
    addMaterialStats(MaterialIds.roseGold,
                     new LimbMaterialStats(175, 0.15f, -0.25f, 0.15f),
                     new GripMaterialStats(-0.3f, 0.25f, 1.0f),
                     StatlessMaterialStats.BOWSTRING);
    addMaterialStats(MaterialIds.cobalt,
                     new LimbMaterialStats(800, 0.05f, 0.05f, 0.05f),
                     new GripMaterialStats(0.05f, 0.05f, 2.25f));
    addMaterialStats(MaterialIds.blazingBone,
                     new LimbMaterialStats(530, 0.1f, 0.1f, -0.3f),
                     new GripMaterialStats(-0.15f, -0.10f, 3f));
    // tier 3 - bowstring
    addMaterialStats(MaterialIds.darkthread, StatlessMaterialStats.BOWSTRING);

    // tier 3 - compat
    addMaterialStats(MaterialIds.invar,
                     new LimbMaterialStats(630, -0.15f, -0.1f, 0.2f),
                     new GripMaterialStats(0, 0.05f, 2.5f));
    addMaterialStats(MaterialIds.pewter,
      new LimbMaterialStats(316, 0.1f, -0.05f, -0.2f),
      new GripMaterialStats(-0.2f, 0.15f, 3.0f));
    addMaterialStats(MaterialIds.necronium,
                     new LimbMaterialStats(357, 0.15f, -0.1f, -0.05f),
                     new GripMaterialStats(-0.2f, 0.15f, 2.75f));
    addMaterialStats(MaterialIds.constantan,
                     new LimbMaterialStats(675, 0.2f, -0.05f, -0.25f),
                     new GripMaterialStats(-0.05f, 0.1f, 1.75f));
    addMaterialStats(MaterialIds.steel,
                     new LimbMaterialStats(775, -0.3f, 0.2f, -0.1f),
                     new GripMaterialStats(0.05f, -0.05f, 2.75f));
    addMaterialStats(MaterialIds.bronze,
                     new LimbMaterialStats(760, -0.2f, 0.15f, -0.2f),
                     new GripMaterialStats(0.1f, 0f, 2.25f));
    addMaterialStats(MaterialIds.electrum,
                     new LimbMaterialStats(225, -0.25f, 0.1f, 0.15f),
                     new GripMaterialStats(-0.35f, 0.2f, 1.5f));
    addMaterialStats(MaterialIds.platedSlimewood,
                     new LimbMaterialStats(595, 0.15f, -0.15f, 0),
                     new GripMaterialStats(0.25f, -0.1f, 2f));
    addMaterialStats(MaterialIds.steeleaf,
      new LimbMaterialStats(200, 0, 0, 0.15f),
      new GripMaterialStats(-0.35f, 0, 2.75f));


    // tier 4
    addMaterialStats(MaterialIds.cinderslime,
                     new LimbMaterialStats(1221, -0.2f, 0, 0.25f),
                     new GripMaterialStats(0.20f, 0.05f, 2.25f));
    addMaterialStats(MaterialIds.queensSlime,
                     new LimbMaterialStats(1650, 0f, -0.15f, 0.25f),
                     new GripMaterialStats(0.35f, -0.15f, 2f));
    addMaterialStats(MaterialIds.hepatizon,
                     new LimbMaterialStats(975, 0.25f, -0.05f, -0.10f),
                     new GripMaterialStats(0.1f, 0.15f, 2.5f));
    addMaterialStats(MaterialIds.manyullyn,
                     new LimbMaterialStats(1250, -0.35f, 0.25f, 0f),
                     new GripMaterialStats(0.1f, -0.20f, 3.5f));
    addMaterialStats(MaterialIds.ancient, new LimbMaterialStats(745, -0.15f, 0.1f, 0.1f));
    addMaterialStats(MaterialIds.ancientHide, StatlessMaterialStats.BOWSTRING);

    // tier 4 (end)
    addMaterialStats(MaterialIds.knightmetal,
      new LimbMaterialStats(512, 0.2f, 0.05f, -0.1f),
      new GripMaterialStats(0, 0.1f, 3.0f));
    addMaterialStats(MaterialIds.enderslimeVine, StatlessMaterialStats.BOWSTRING);

    // tier 4 (compat)
    addMaterialStats(MaterialIds.fiery,
      new LimbMaterialStats(1024, -0.25f, 0.2f, -0.05f),
      new GripMaterialStats(0, 0.05f, 3.5f));
  }

  private void addAmmo() {
    // tier 1
    addMaterialStats(MaterialIds.flint, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.wool, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.wood, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.bone, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.bamboo, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.chorus, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.feather, StatlessMaterialStats.FLETCHING);
    addMaterialStats(MaterialIds.leaves, StatlessMaterialStats.FLETCHING);
    addMaterialStats(MaterialIds.paper, StatlessMaterialStats.FLETCHING);
    // tier 2
    addMaterialStats(MaterialIds.amethyst, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.prismarine, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.earthslime, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.skyslime, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.enderPearl, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.glass, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.slimewood, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.necroticBone, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.blaze, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.venombone, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.steeleaf, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.slimeball, StatlessMaterialStats.FLETCHING);
    addMaterialStats(MaterialIds.gunpowder, StatlessMaterialStats.ARROW_HEAD);
    // tier 3
    addMaterialStats(MaterialIds.ice, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.quartz, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.ichor, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.glowstone, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.nahuatl, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.necronium, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.magnetite, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.magma, StatlessMaterialStats.FLETCHING);
    // tier 4
    addMaterialStats(MaterialIds.enderslime, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.dragonScale, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.shulker, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.blazewood, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.blazingBone, StatlessMaterialStats.ARROW_SHAFT);
    addMaterialStats(MaterialIds.knightly, StatlessMaterialStats.ARROW_HEAD);
    addMaterialStats(MaterialIds.endRod, StatlessMaterialStats.ARROW_SHAFT);
  }

  private void addArmor() {
    // tier 1
    addMaterialStats(MaterialIds.wood, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.bamboo, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.chorus, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.ice, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.cactus, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.bone, StatlessMaterialStats.SHIELD_CORE);
    addArmorShieldStats(MaterialIds.copper, PlatingMaterialStats.builder().durabilityFactor(13).armor(1, 2, 3, 1), StatlessMaterialStats.MAILLE);
    addMaterialStats(MaterialIds.leather, StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
    addMaterialStats(MaterialIds.vine, StatlessMaterialStats.MAILLE);
    // tier 2
    addMaterialStats(MaterialIds.slimewood, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.venombone, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.necroticBone, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.slimeskin, StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
    addMaterialStats(MaterialIds.skyslimeVine, StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
    addMaterialStats(MaterialIds.weepingVine, StatlessMaterialStats.MAILLE);
    addMaterialStats(MaterialIds.twistingVine, StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.iron,          PlatingMaterialStats.builder().durabilityFactor(15).armor(2, 4, 5, 2), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.gold,          PlatingMaterialStats.builder().durabilityFactor( 7).armor(1, 3, 4, 1), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.searedStone,   PlatingMaterialStats.builder().durabilityFactor(14).armor(1, 3, 4, 2).knockbackResistance(0.1f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.scorchedStone, PlatingMaterialStats.builder().durabilityFactor(10).armor(1, 4, 5, 2).knockbackResistance(0.05f), StatlessMaterialStats.MAILLE);
    // tier 2 - compat
    addArmorShieldStats(MaterialIds.osmium,   PlatingMaterialStats.builder().durabilityFactor(25).armor(1, 3, 5, 2).knockbackResistance(0.05f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.aluminum, PlatingMaterialStats.builder().durabilityFactor(13).armor(2, 6, 4, 2), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.silver,   PlatingMaterialStats.builder().durabilityFactor(18).armor(1, 4, 5, 2).knockbackResistance(0.05f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.lead,     PlatingMaterialStats.builder().durabilityFactor(12).armor(1, 3, 4, 2).knockbackResistance(0.1f), StatlessMaterialStats.MAILLE);
    addMaterialStats(MaterialIds.treatedWood, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.ironwood, StatlessMaterialStats.SHIELD_CORE, StatlessMaterialStats.MAILLE);
    // tier 3
    addMaterialStats(MaterialIds.nahuatl, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.ichorskin, StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
    addArmorShieldStats(MaterialIds.slimesteel,     PlatingMaterialStats.builder().durabilityFactor(40).armor(2, 5, 6, 2), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.amethystBronze, PlatingMaterialStats.builder().durabilityFactor(28).armor(2, 5, 6, 2).toughness(2), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.obsidian,       PlatingMaterialStats.builder().durabilityFactor(11).armor(2, 4, 5, 2).knockbackResistance(0.15f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.roseGold,       PlatingMaterialStats.builder().durabilityFactor( 9).armor(1, 3, 5, 2), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.pigIron,        PlatingMaterialStats.builder().durabilityFactor(23).armor(1, 3, 4, 1).toughness(1).knockbackResistance(0.1f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.cobalt,         PlatingMaterialStats.builder().durabilityFactor(30).armor(2, 5, 7, 2).toughness(1).knockbackResistance(0.05f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.steel,          PlatingMaterialStats.builder().durabilityFactor(29).armor(2, 5, 7, 2).toughness(2), StatlessMaterialStats.MAILLE);
    // tier 3 - compat
    addMaterialStats(MaterialIds.necronium, StatlessMaterialStats.SHIELD_CORE);
    addArmorShieldStats(MaterialIds.bronze,     PlatingMaterialStats.builder().durabilityFactor(28).armor(2, 5, 6, 2).knockbackResistance(0.1f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.constantan, PlatingMaterialStats.builder().durabilityFactor(25).armor(1, 4, 5, 2).toughness(2).knockbackResistance(0.05f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.invar,      PlatingMaterialStats.builder().durabilityFactor(24).armor(1, 3, 5, 2).knockbackResistance(0.1f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.pewter,     PlatingMaterialStats.builder().durabilityFactor(16).armor(2, 5, 7, 2), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.electrum,   PlatingMaterialStats.builder().durabilityFactor(14).armor(1, 3, 4, 2), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.steeleaf,   PlatingMaterialStats.builder().durabilityFactor(10).armor(2, 5, 7, 2), StatlessMaterialStats.MAILLE);
    // tier 4
    addMaterialStats(MaterialIds.blazewood, StatlessMaterialStats.SHIELD_CORE);
    addMaterialStats(MaterialIds.blazingBone, StatlessMaterialStats.SHIELD_CORE);
    addArmorShieldStats(MaterialIds.cinderslime, PlatingMaterialStats.builder().durabilityFactor(42).armor(2, 5, 7, 2).knockbackResistance(0.10f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.queensSlime, PlatingMaterialStats.builder().durabilityFactor(50).armor(2, 5, 7, 2).toughness(1), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.hepatizon,   PlatingMaterialStats.builder().durabilityFactor(32).armor(2, 5, 7, 2).toughness(2).knockbackResistance(0.10f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.manyullyn,   PlatingMaterialStats.builder().durabilityFactor(35).armor(2, 5, 7, 2).toughness(3).knockbackResistance(0.05f), StatlessMaterialStats.MAILLE);
    addArmorShieldStats(MaterialIds.ancient,     PlatingMaterialStats.builder().durabilityFactor(25).armor(2, 4, 6, 2).knockbackResistance(0.15f));
    addMaterialStats(MaterialIds.ancientHide, StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
    // tier 4 (end)
    addArmorShieldStats(MaterialIds.knightmetal, PlatingMaterialStats.builder().durabilityFactor(20).armor(2, 5, 7, 2).toughness(2).knockbackResistance(0.05f), StatlessMaterialStats.MAILLE);
    addMaterialStats(MaterialIds.enderslimeVine, StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
    addMaterialStats(MaterialIds.dragonScale, StatlessMaterialStats.MAILLE);
    addMaterialStats(MaterialIds.shulker, StatlessMaterialStats.MAILLE);
    // tier 4 (compat)
    addArmorShieldStats(MaterialIds.fiery, PlatingMaterialStats.builder().durabilityFactor(25).armor(3, 6, 8, 3).toughness(1.5f), StatlessMaterialStats.MAILLE);
  }

  private void addMisc() {
    // travelers gear
    addMaterialStats(MaterialIds.leather, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.slimeskin, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.skyslimeVine, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.ichorskin, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.enderslimeVine, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.ancientHide, StatlessMaterialStats.REPAIR_KIT);
    // travelers's shield
    addMaterialStats(MaterialIds.ice, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.cactus, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.blazewood, StatlessMaterialStats.REPAIR_KIT);
    // slimeskull
    addMaterialStats(MaterialIds.glass,        new SkullStats( 90, 0));
    addMaterialStats(MaterialIds.enderPearl,   new SkullStats(180, 0));
    addMaterialStats(MaterialIds.bone,         new SkullStats(100, 0));
    addMaterialStats(MaterialIds.venombone,    new SkullStats(175, 1));
    addMaterialStats(MaterialIds.necroticBone, new SkullStats(125, 0));
    addMaterialStats(MaterialIds.string,       new SkullStats(140, 0));
    addMaterialStats(MaterialIds.darkthread,   new SkullStats(200, 1));
    addMaterialStats(MaterialIds.leather,      new SkullStats(150, 2));
    addMaterialStats(MaterialIds.iron,         new SkullStats(165, 2));
    addMaterialStats(MaterialIds.copper,       new SkullStats(145, 2));
    addMaterialStats(MaterialIds.blazingBone,  new SkullStats(205, 1));
    addMaterialStats(MaterialIds.gold,         new SkullStats(125, 0));
    addMaterialStats(MaterialIds.roseGold,     new SkullStats(175, 1));
    addMaterialStats(MaterialIds.pigIron,      new SkullStats(150, 2));
    // slimesuit
    addMaterialStats(MaterialIds.enderslime, StatlessMaterialStats.REPAIR_KIT);
    addMaterialStats(MaterialIds.phantom, StatlessMaterialStats.REPAIR_KIT);
    // slimesuit embellishments
    addMaterialStats(MaterialIds.blood);
    addMaterialStats(MaterialIds.clay);
    addMaterialStats(MaterialIds.honey);
  }
}
