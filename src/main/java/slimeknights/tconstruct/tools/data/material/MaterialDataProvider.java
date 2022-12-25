package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.TConstruct;
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
    addMaterial(MaterialIds.wood,   1, ORDER_GENERAL, true);
    addMaterial(MaterialIds.rock,   1, ORDER_HARVEST, true);
    addMaterial(MaterialIds.flint,  1, ORDER_WEAPON,  true);
    addMaterial(MaterialIds.copper, 1, ORDER_SPECIAL, true);
    addMaterial(MaterialIds.bone,   1, ORDER_SPECIAL, true);
    addMaterial(MaterialIds.bamboo, 1, ORDER_RANGED,  true);
    // tier 1 - end
    addMaterial(MaterialIds.chorus, 1, ORDER_END,     true);
    // tier 1 - binding
    addMaterial(MaterialIds.string,  1, ORDER_BINDING, true);
    addMaterial(MaterialIds.leather, 1, ORDER_BINDING, true);
    addMaterial(MaterialIds.vine,    1, ORDER_BINDING, true);

    // tier 2
    addMaterial(MaterialIds.iron,        2, ORDER_GENERAL, false);
    addMaterial(MaterialIds.searedStone, 2, ORDER_HARVEST, false);
    addMaterial(MaterialIds.bloodbone,   2, ORDER_WEAPON,  true);
    addMaterial(MaterialIds.slimewood,   2, ORDER_SPECIAL, true);
    // tier 2 - nether
    addMaterial(MaterialIds.scorchedStone, 2, ORDER_NETHER, false);
    addMaterial(MaterialIds.necroticBone,  2, ORDER_NETHER, true);
    // tier 2 - end
    addMaterial(MaterialIds.whitestone, 2, ORDER_END, true);
    // tier 2 - binding
    addMaterial(MaterialIds.chain,        2, ORDER_BINDING, true);
    addMaterial(MaterialIds.skyslimeVine, 2, ORDER_BINDING, true);

    // tier 3
    addMaterial(MaterialIds.slimesteel,     3, ORDER_GENERAL, false);
    addMaterial(MaterialIds.amethystBronze, 3, ORDER_HARVEST, false);
    addMaterial(MaterialIds.nahuatl,        3, ORDER_WEAPON,  false);
    addMaterial(MaterialIds.roseGold,       3, ORDER_SPECIAL, false);
    addMaterial(MaterialIds.pigIron,        3, ORDER_SPECIAL, false);
    // tier 3 (nether)
    addMaterial(MaterialIds.cobalt, 3, ORDER_NETHER, false);
    // tier 3 - binding
    addMaterial(MaterialIds.darkthread, 3, ORDER_BINDING, false);

    // tier 4
    addMaterial(MaterialIds.queensSlime, 4, ORDER_GENERAL, false);
    addMaterial(MaterialIds.hepatizon,   4, ORDER_HARVEST, false);
    addMaterial(MaterialIds.manyullyn,   4, ORDER_WEAPON,  false);
    addMaterial(MaterialIds.blazingBone, 4, ORDER_SPECIAL, true);
    //addMetalMaterial(MaterialIds.soulsteel, 4, ORDER_SPECIAL, false, 0x6a5244);
    // tier 4 - binding
    addMaterial(MaterialIds.ancientHide, 4, ORDER_BINDING, false);

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
    addCompatMetalMaterial(MaterialIds.aluminum,   2, ORDER_COMPAT + ORDER_RANGED);
    // tier 3 (mod integration)
    addCompatMetalMaterial(MaterialIds.steel,           3, ORDER_COMPAT + ORDER_GENERAL);
    addCompatMetalMaterial(MaterialIds.bronze,          3, ORDER_COMPAT + ORDER_HARVEST);
    addCompatMetalMaterial(MaterialIds.constantan,      3, ORDER_COMPAT + ORDER_HARVEST);
    addCompatMetalMaterial(MaterialIds.invar,           3, ORDER_COMPAT + ORDER_WEAPON);
    addCompatMaterial     (MaterialIds.necronium,       3, ORDER_COMPAT + ORDER_WEAPON, "ingots/uranium", true);
    addCompatMetalMaterial(MaterialIds.electrum,        3, ORDER_COMPAT + ORDER_SPECIAL);
    addCompatMetalMaterial(MaterialIds.platedSlimewood, 3, ORDER_COMPAT + ORDER_SPECIAL, "brass");

    // plate
    addMaterial(MaterialIds.obsidian,  3, ORDER_REPAIR, false);
    addMaterial(MaterialIds.debris,    3, ORDER_REPAIR, false);
    addMaterial(MaterialIds.netherite, 4, ORDER_REPAIR, false);
    addCompatMetalMaterial(MaterialIds.nickel,   2, ORDER_REPAIR);
    addCompatMetalMaterial(MaterialIds.tin,      2, ORDER_REPAIR);
    addCompatMetalMaterial(MaterialIds.zinc,     2, ORDER_REPAIR);
    addCompatMetalMaterial(MaterialIds.brass,    3, ORDER_REPAIR);
    addCompatMetalMaterial(MaterialIds.uranium,  2, ORDER_REPAIR);
    // slimeskull - put in the most appropriate tier
    addMaterial(MaterialIds.gold,        2, ORDER_REPAIR, false);
    addMaterial(MaterialIds.glass,       2, ORDER_REPAIR, false);
    addMaterial(MaterialIds.rottenFlesh, 1, ORDER_REPAIR, true);
    addMaterial(MaterialIds.enderPearl,  2, ORDER_REPAIR, false);
    // slimesuit - textures
    addMaterial(MaterialIds.earthslime, 1, ORDER_REPAIR, true);
    addMaterial(MaterialIds.skyslime,   1, ORDER_REPAIR, true);
    addMaterial(MaterialIds.blood,      2, ORDER_REPAIR, true);
    addMaterial(MaterialIds.ichor,      3, ORDER_REPAIR, true);
    addMaterial(MaterialIds.enderslime, 4, ORDER_REPAIR, true);
    addMaterial(MaterialIds.clay,       1, ORDER_REPAIR, true);
    addMaterial(MaterialIds.honey,      1, ORDER_REPAIR, true);
    //addMaterial(MaterialIds.venom,      3, ORDER_REPAIR, true);
    // slimesuit - repair
    addMaterial(MaterialIds.phantom,    1, ORDER_REPAIR, true);

    // legacy
    addRedirect(new MaterialId(TConstruct.MOD_ID, "stone"),     redirect(MaterialIds.rock));
    addRedirect(new MaterialId(TConstruct.MOD_ID, "gunpowder"), redirect(MaterialIds.glass));
    addRedirect(new MaterialId(TConstruct.MOD_ID, "spider"),    redirect(MaterialIds.string));
    addRedirect(new MaterialId(TConstruct.MOD_ID, "venom"),     redirect(MaterialIds.darkthread));
    addRedirect(new MaterialId(TConstruct.MOD_ID, "rabbit"),    redirect(MaterialIds.leather));
    addRedirect(new MaterialId(TConstruct.MOD_ID, "tinkers_bronze"), conditionalRedirect(MaterialIds.bronze, tagExistsCondition("ingots/bronze")), redirect(MaterialIds.copper));
  }
}
