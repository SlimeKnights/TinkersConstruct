package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

public class MaterialDataProvider extends AbstractMaterialDataProvider {
  public MaterialDataProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  public String getName() {
    return "Tinker's Construct Materials";
  }

  @Override
  protected void addMaterials() {
    // tier 1
    addMaterial(MaterialIds.wood,  1, ORDER_GENERAL, true);
    addMaterial(MaterialIds.rock,  1, ORDER_HARVEST, true);
    addMaterial(MaterialIds.flint, 1, ORDER_WEAPON,  true);
    addMaterial(MaterialIds.bone,  1, ORDER_SPECIAL, true);
    // tier 1 - nether
    addMaterial(MaterialIds.necroticBone,  1, ORDER_SPECIAL, true);
    // tier 1 - binding
    addMaterial(MaterialIds.string,  1, ORDER_BINDING, true);
    addMaterial(MaterialIds.leather, 1, ORDER_BINDING, true);
    addMaterial(MaterialIds.vine,    1, ORDER_BINDING, true);

    // tier 2
    addMaterial(MaterialIds.iron,        2, ORDER_GENERAL, false);
    addMaterial(MaterialIds.copper,      2, ORDER_HARVEST, true);
    addMaterial(MaterialIds.searedStone, 2, ORDER_WEAPON,  false);
    addMaterial(MaterialIds.slimewood,   2, ORDER_SPECIAL, true);
    addMaterial(MaterialIds.bloodbone,   2, ORDER_SPECIAL, false);
    // tier 2 - nether
    addMaterial(MaterialIds.scorchedStone, 2, ORDER_NETHER, false);
    // tier 2 - binding
    addMaterial(MaterialIds.chain,        2, ORDER_BINDING, true);
    addMaterial(MaterialIds.skyslimeVine, 2, ORDER_BINDING, true);

    // tier 3
    addMaterial(MaterialIds.slimesteel, 3, ORDER_GENERAL, false);
    addMaterial(MaterialIds.bronze,     3, ORDER_HARVEST, false);
    addMaterial(MaterialIds.nahuatl,    3, ORDER_WEAPON,  false);
    addMaterial(MaterialIds.roseGold,   3, ORDER_SPECIAL, false);
    addMaterial(MaterialIds.pigIron,    3, ORDER_SPECIAL, false);
    // tier 3 (nether)
    addMaterial(MaterialIds.cobalt, 3, ORDER_NETHER, false);

    // tier 4
    addMaterial(MaterialIds.queensSlime, 4, ORDER_GENERAL, false);
    addMaterial(MaterialIds.hepatizon,   4, ORDER_HARVEST, false);
    addMaterial(MaterialIds.manyullyn,   4, ORDER_WEAPON,  false);
    addMaterial(MaterialIds.blazingBone, 4, ORDER_SPECIAL, false);
    //addMetalMaterial(MaterialIds.soulsteel, 4, ORDER_SPECIAL, false, 0x6a5244);

    // tier 5 binding, temporarily in book 4
    addMaterial(MaterialIds.enderslimeVine, 4, ORDER_BINDING, true);

    // tier 2 (end)
    //addMaterialNoFluid(MaterialIds.endstone, 2, ORDER_END, true, 0xe0d890);

    // tier 2 (mod integration)
    addCompatMetalMaterial(MaterialIds.osmium,     2, ORDER_COMPAT + ORDER_GENERAL);
    addCompatMetalMaterial(MaterialIds.tungsten,   2, ORDER_COMPAT + ORDER_HARVEST);
    addCompatMetalMaterial(MaterialIds.platinum,   2, ORDER_COMPAT + ORDER_HARVEST);
    addCompatMetalMaterial(MaterialIds.silver,     2, ORDER_COMPAT + ORDER_WEAPON);
    addCompatMetalMaterial(MaterialIds.lead,       2, ORDER_COMPAT + ORDER_WEAPON);
    ICondition condition = new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS,
                                           tagExistsCondition("ingots/aluminum"),
                                           tagExistsCondition("ingots/tin"),
                                           tagExistsCondition("ingots/zinc"));
    addMaterial(MaterialIds.whitestone, 2, ORDER_COMPAT + ORDER_SPECIAL, false, false, condition);
    // tier 3 (mod integration)
    addCompatMetalMaterial(MaterialIds.steel,           3, ORDER_COMPAT + ORDER_GENERAL);
    addCompatMetalMaterial(MaterialIds.constantan,      3, ORDER_COMPAT + ORDER_HARVEST);
    addCompatMetalMaterial(MaterialIds.invar,           3, ORDER_COMPAT + ORDER_WEAPON);
    addCompatMetalMaterial(MaterialIds.necronium,       3, ORDER_COMPAT + ORDER_WEAPON, "uranium");
    addCompatMetalMaterial(MaterialIds.electrum,        3, ORDER_COMPAT + ORDER_SPECIAL);
    addCompatMetalMaterial(MaterialIds.platedSlimewood, 3, ORDER_COMPAT + ORDER_SPECIAL, "brass");

    // plate
    addMaterial(MaterialIds.netherite, 6, 9, false);
    addCompatMetalMaterial(MaterialIds.aluminum, 6, 9);
    addCompatMetalMaterial(MaterialIds.nickel,   6, 9);
    addCompatMetalMaterial(MaterialIds.tin,      6, 9);
    addCompatMetalMaterial(MaterialIds.zinc,     6, 9);
    addCompatMetalMaterial(MaterialIds.brass,    6, 9);
    addCompatMetalMaterial(MaterialIds.uranium,  6, 9);
    // slimeskull - marked tier 6 to push to tne end of repair kits
    addMaterial(MaterialIds.gold,        6, 0, false);
    addMaterial(MaterialIds.gunpowder,   6, 1, true);
    addMaterial(MaterialIds.rottenFlesh, 6, 2, true);
    addMaterial(MaterialIds.spider,      6, 4, true);
    addMaterial(MaterialIds.venom,       6, 5, true);
    addMaterial(MaterialIds.enderPearl,  6, 6, true);
    // slimesuit - textures
    addMaterial(MaterialIds.earthslime, 6, 9, true);
    addMaterial(MaterialIds.skyslime,   6, 9, true);
    addMaterial(MaterialIds.blood,      6, 9, true);
    addMaterial(MaterialIds.ichor,      6, 9, true);
    addMaterial(MaterialIds.enderslime, 6, 9, true);
    // slimesuit - repair
    addMaterial(MaterialIds.phantom,    6, 9, true);
    addMaterial(MaterialIds.chorus,     6, 9, true);
    addMaterial(MaterialIds.rabbit,     6, 9, true);

    // legacy
    addRedirect(new MaterialId(TConstruct.MOD_ID, "stone"), redirect(MaterialIds.rock));
    addRedirect(new MaterialId(TConstruct.MOD_ID, "tinkers_bronze"), redirect(MaterialIds.bronze));
  }
}
