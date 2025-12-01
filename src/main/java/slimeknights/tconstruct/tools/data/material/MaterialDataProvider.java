package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import static slimeknights.mantle.Mantle.commonResource;

public class MaterialDataProvider extends AbstractMaterialDataProvider {
  public MaterialDataProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  public String getName() {
    return "Tinker's Construct Materials";
  }

  @Override
  protected void addMaterials() {
    // tier 1
    addMaterial(MaterialIds.wood,   0, ORDER_GENERAL, true);
    addMaterial(MaterialIds.rock,   1, ORDER_HARVEST, true);
    addMaterial(MaterialIds.flint,  1, ORDER_WEAPON,  true);
    addMaterial(MaterialIds.copper, 1, ORDER_SPECIAL, true);
    addMaterial(MaterialIds.bone,   1, ORDER_SPECIAL, true);
    addMaterial(MaterialIds.bamboo, 1, ORDER_RANGED,  true);
    // tier 1 - end
    addMaterial(MaterialIds.chorus, 1, ORDER_END,     true);
    // tier 1 - binding
    addMaterial(MaterialIds.string,  0, ORDER_GENERAL, true);
    addMaterial(MaterialIds.leather, 0, ORDER_BINDING, true);
    addMaterial(MaterialIds.vine,    1, ORDER_BINDING, true);
    // tier 1 - shield cores
    addMaterial(MaterialIds.cactus, 1, ORDER_BINDING, true);
    // tier 1 - ammo
    addMaterial(MaterialIds.feather, 0, ORDER_GENERAL, true);
    addMaterial(MaterialIds.wool,    1, ORDER_BINDING, true);
    addMaterial(MaterialIds.leaves,  1, ORDER_BINDING, true);
    addMaterial(MaterialIds.paper,   1, ORDER_BINDING, true);

    // tier 2
    addMaterial(MaterialIds.iron,        2, ORDER_GENERAL, false);
    addMaterial(MaterialIds.searedStone, 2, ORDER_HARVEST, false);
    addMaterial(MaterialIds.venombone,   2, ORDER_WEAPON,  true);
    addMaterial(MaterialIds.slimewood,   2, ORDER_SPECIAL, true);
    addMaterial(MaterialIds.slimeskin,   2, ORDER_BINDING, false);
    addMaterial(MaterialIds.gold,        2, ORDER_REPAIR, false);
    // tier 2 - nether
    addMaterial(MaterialIds.scorchedStone, 2, ORDER_NETHER, false);
    addMaterial(MaterialIds.necroticBone,  2, ORDER_NETHER, true);
    // tier 2 - end
    addMaterial(MaterialIds.whitestone, 2, ORDER_END, true);
    // tier 2 - binding
    addMaterial(MaterialIds.skyslimeVine, 2, ORDER_BINDING, true);
    addMaterial(MaterialIds.weepingVine,  2, ORDER_BINDING, true);
    addMaterial(MaterialIds.twistingVine, 2, ORDER_BINDING, true);
    // tier 2 - ammo
    addMaterial(MaterialIds.amethyst,   2, ORDER_REPAIR, false);
    addMaterial(MaterialIds.prismarine, 2, ORDER_REPAIR, true);
    addMaterial(MaterialIds.earthslime, 2, ORDER_REPAIR, true);
    addMaterial(MaterialIds.skyslime,   2, ORDER_REPAIR, true);
    addMaterial(MaterialIds.blaze,      2, ORDER_REPAIR, true);
    addMaterial(MaterialIds.enderPearl, 2, ORDER_REPAIR, false);
    addMaterial(MaterialIds.glass,      2, ORDER_REPAIR, false);
    addMaterial(MaterialIds.slimeball,  2, ORDER_REPAIR, true);
    addMaterial(MaterialIds.gunpowder,  2, ORDER_REPAIR, true);
    // bloodbone reworked into venombone
    addRedirect(new MaterialId(TConstruct.MOD_ID, "bloodbone"), redirect(MaterialIds.venombone));

    // tier 3
    addMaterial(MaterialIds.slimesteel,     3, ORDER_GENERAL, false);
    addMaterial(MaterialIds.amethystBronze, 3, ORDER_HARVEST, false);
    addMaterial(MaterialIds.nahuatl,        3, ORDER_WEAPON,  true);
    addMaterial(MaterialIds.obsidian,       3, ORDER_WEAPON,  false);
    addMaterial(MaterialIds.roseGold,       3, ORDER_SPECIAL, false);
    addMaterial(MaterialIds.pigIron,        3, ORDER_SPECIAL, false);
    // tier 3 (nether)
    addMaterial(MaterialIds.steel,  3, ORDER_NETHER, false);
    addMaterial(MaterialIds.cobalt, 3, ORDER_NETHER, false);
    // tier 3 - binding
    addMaterial(MaterialIds.darkthread, 3, ORDER_BINDING, false);
    addMaterial(MaterialIds.ichorskin,  3, ORDER_BINDING, false);
    // tier 3 - shield cores
    addMaterial(MaterialIds.ice, 3, ORDER_BINDING, true);
    // tier 3 - ammo
    addMaterial(MaterialIds.quartz,    3, ORDER_REPAIR, false);
    addMaterial(MaterialIds.ichor,     3, ORDER_REPAIR, true);
    addMaterial(MaterialIds.glowstone, 3, ORDER_REPAIR, true);
    addMaterial(MaterialIds.magnetite, 3, ORDER_REPAIR, true);
    addMaterial(MaterialIds.magma,     3, ORDER_REPAIR, true);

    // tier 4
    addMaterial(MaterialIds.queensSlime, 4, ORDER_GENERAL, false);
    addMaterial(MaterialIds.cinderslime, 4, ORDER_GENERAL, false);
    addMaterial(MaterialIds.hepatizon,   4, ORDER_HARVEST, false);
    addMaterial(MaterialIds.manyullyn,   4, ORDER_WEAPON,  false);
    addMaterial(MaterialIds.blazingBone, 4, ORDER_SPECIAL, true);
    addMaterial(MaterialIds.knightmetal, 4, ORDER_END,     false);
    //addMetalMaterial(MaterialIds.soulsteel, 4, ORDER_SPECIAL, false, 0x6a5244);
    // tier 4 - binding
    addMaterial(MaterialIds.ancientHide, 4, ORDER_BINDING, false);
    addMaterial(MaterialIds.ancient,     4, ORDER_NETHER,  false, true, null);
    addMaterial(MaterialIds.blazewood,   4, ORDER_BINDING, true);
    // tier 4 - ammo
    addMaterial(MaterialIds.shulker,     4, ORDER_REPAIR, true);
    addMaterial(MaterialIds.dragonScale, 4, ORDER_REPAIR, true);
    addMaterial(MaterialIds.enderslime,  4, ORDER_REPAIR, true);
    addMaterial(MaterialIds.knightly,    4, ORDER_REPAIR, true);
    addMaterial(MaterialIds.endRod,      4, ORDER_REPAIR, true);

    // tier 5 binding, temporarily in book 4
    addMaterial(MaterialIds.enderslimeVine, 4, ORDER_BINDING, true);

    // tier 2 (mod integration)
    addCompatMetalMaterial(MaterialIds.osmium,   2, ORDER_COMPAT + ORDER_GENERAL);
    addCompatMetalMaterial(MaterialIds.lead,     2, ORDER_COMPAT + ORDER_HARVEST);
    addCompatMetalMaterial(MaterialIds.silver,   2, ORDER_COMPAT + ORDER_WEAPON);
    addCompatMetalMaterial(MaterialIds.aluminum, 2, ORDER_COMPAT + ORDER_RANGED);
    // ironwood works in a part builder even though its ingots
    addCompatMaterial(MaterialIds.ironwood, 2, ORDER_COMPAT + ORDER_GENERAL, true, "ingots/ironwood");
    // treated wood comes from treated wood or creosote oil
    addMaterial(MaterialIds.treatedWood, 2, ORDER_COMPAT + ORDER_GENERAL, true, false,
      new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, tagExistsCondition("treated_wood"), new TagFilledCondition<>(FluidTags.create(commonResource("creosote")))));
    // tier 3 (mod integration)
    addCompatAlloy(MaterialIds.electrum,        3, ORDER_COMPAT + ORDER_GENERAL, "silver");
    addCompatAlloy(MaterialIds.bronze,          3, ORDER_COMPAT + ORDER_HARVEST, "tin");
    addCompatAlloy(MaterialIds.constantan,      3, ORDER_COMPAT + ORDER_HARVEST, "nickel");
    addCompatAlloy(MaterialIds.invar,           3, ORDER_COMPAT + ORDER_WEAPON,  "nickel");
    // TODO 1.21: consider making this an and condition, so we only get pewter if pewter is present or we have both
    addCompatAlloy(MaterialIds.pewter,          3, ORDER_COMPAT + ORDER_WEAPON,  new OrCondition(tagExistsCondition("ingots/tin"), tagExistsCondition("ingots/lead")));
    addCompatAlloy(MaterialIds.platedSlimewood, 3, ORDER_COMPAT + ORDER_SPECIAL, "zinc");
    addCompatMaterial(MaterialIds.necronium,       3, ORDER_COMPAT + ORDER_WEAPON, true, "ingots/uranium");
    addCompatMetalMaterial(MaterialIds.steeleaf, 3, ORDER_COMPAT + ORDER_SPECIAL);
    // tier 4 (mod integration)
    addCompatMetalMaterial(MaterialIds.fiery,           4, ORDER_COMPAT + ORDER_END);

    // slimesuit - textures
    addMaterial(MaterialIds.blood, 2, ORDER_REPAIR, true);
    addMaterial(MaterialIds.clay,  1, ORDER_REPAIR, true);
    addMaterial(MaterialIds.honey, 1, ORDER_REPAIR, true);
    //addMaterial(MaterialIds.venom,      3, ORDER_REPAIR, true);
    // slimesuit - repair
    addMaterial(MaterialIds.phantom, 1, ORDER_REPAIR, true);

    // rose gold is most comparable to chain as you can use the extra slot for reinforced
    addRedirect(id("chain"), redirect(MaterialIds.roseGold));
    // zombies now use leather instead of flesh for their skull
    addRedirect(id("rotten_flesh"), redirect(MaterialIds.leather));
    addRedirect(id("platinum"), redirect(MaterialIds.searedStone));
    addRedirect(id("tungsten"),
      conditionalRedirect(MaterialIds.lead, tagExistsCondition("ingots/lead")),
      conditionalRedirect(MaterialIds.invar, new OrCondition(tagExistsCondition("ingots/invar"), tagExistsCondition("ingots/nickel"))),
      redirect(MaterialIds.iron));
  }
  /**
   * Creates a new material ID
   * @param name  ID name
   * @return  Material ID object
   */
  private static MaterialId id(String name) {
    return new MaterialId(TConstruct.MOD_ID, name);
  }
}
