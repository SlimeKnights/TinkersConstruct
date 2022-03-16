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
    addMaterial(MaterialIds.wood, 1, ORDER_GENERAL, true, 0x8e661b);
    addMaterial(MaterialIds.stone, 1, ORDER_HARVEST, true, 0x999999);
    addMaterial(MaterialIds.flint, 1, ORDER_WEAPON,  true, 0x696969);
    addMaterial(MaterialIds.bone,  1, ORDER_SPECIAL, true, 0xE8E5D2);
    // tier 1 - nether
    addMaterial(MaterialIds.necroticBone,  1, ORDER_SPECIAL, true, 0x4D4D4D);
    // tier 1 - binding
    addMaterial(MaterialIds.string,  1, ORDER_BINDING, true, 0xFFFFFF);
    addMaterial(MaterialIds.leather, 1, ORDER_BINDING, true, 0xC65C35);
    addMaterial(MaterialIds.vine,    1, ORDER_BINDING, true, 0x48B518);

    // tier 2
    addMaterial(MaterialIds.iron,        2, ORDER_GENERAL, false, 0xD8D8D8);
    addMaterial(MaterialIds.copper,      2, ORDER_HARVEST, true,  0xF98648);
    addMaterial(MaterialIds.searedStone, 2, ORDER_WEAPON,  false, 0x4F4A47);
    addMaterial(MaterialIds.slimewood,   2, ORDER_SPECIAL, true,  0x82c873);
    addMaterial(MaterialIds.bloodbone,   2, ORDER_SPECIAL, false, 0xB30000);
    // tier 2 - nether
    addMaterial(MaterialIds.scorchedStone, 2, ORDER_NETHER, false, 0x5B4C43);
    // tier 2 - binding
    addMaterial(MaterialIds.chain,        2, ORDER_BINDING, true, 0x3E4453);
    addMaterial(MaterialIds.skyslimeVine, 2, ORDER_BINDING, true, 0x00F4DA);

    // tier 3
    addMaterial(MaterialIds.slimesteel,    3, ORDER_GENERAL, false, 0x27C6C6);
    addMaterial(MaterialIds.tinkersBronze, 3, ORDER_HARVEST, false, 0xE8B465);
    addMaterial(MaterialIds.nahuatl,       3, ORDER_WEAPON,  false, 0x3B2754);
    addMaterial(MaterialIds.roseGold,      3, ORDER_SPECIAL, false, 0xF7CDBB);
    addMaterial(MaterialIds.pigIron,       3, ORDER_SPECIAL, false, 0xF0A8A4);
    // tier 3 (nether)
    addMaterial(MaterialIds.cobalt, 3, ORDER_NETHER, false, 0x2376dd);
    // tier 3 - binding
    addMaterial(MaterialIds.darkthread, 3, ORDER_BINDING, false, 0x3B2754);

    // tier 4
    addMaterial(MaterialIds.queensSlime, 4, ORDER_GENERAL, false, 0x236c45);
    addMaterial(MaterialIds.hepatizon,   4, ORDER_HARVEST, false, 0x60496b);
    addMaterial(MaterialIds.manyullyn,   4, ORDER_WEAPON,  false, 0x9261cc);
    addMaterial(MaterialIds.blazingBone, 4, ORDER_SPECIAL, false, 0xDBCC0B);
    // tier 4 - binding
    addMaterial(MaterialIds.ancientHide, 4, ORDER_BINDING, false, 0x7E6059);

    // tier 5 binding, temporarily in book 4
    addMaterial(MaterialIds.enderslimeVine, 4, ORDER_BINDING, true, 0xa92dff);

    // tier 2 (end)
    //addMaterialNoFluid(MaterialIds.endstone, 2, ORDER_END, true, 0xe0d890);

    // tier 2 (mod integration)
    addCompatMetalMaterial(MaterialIds.osmium,     2, ORDER_COMPAT + ORDER_GENERAL, 0xBED3CD);
    addCompatMetalMaterial(MaterialIds.tungsten,   2, ORDER_COMPAT + ORDER_HARVEST, 0xD1C08B);
    addCompatMetalMaterial(MaterialIds.platinum,   2, ORDER_COMPAT + ORDER_HARVEST, 0xA3E7FE);
    addCompatMetalMaterial(MaterialIds.silver,     2, ORDER_COMPAT + ORDER_WEAPON,  0xD3DFE8);
    addCompatMetalMaterial(MaterialIds.lead,       2, ORDER_COMPAT + ORDER_WEAPON,  0x575E79);
    ICondition condition = new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS,
                                           tagExistsCondition("ingots/aluminum"),
                                           tagExistsCondition("ingots/tin"),
                                           tagExistsCondition("ingots/zinc"));
    addMaterial(MaterialIds.whitestone, 2, ORDER_COMPAT + ORDER_SPECIAL, false, 0xE0E9EC, false, condition);
    // tier 3 (mod integration)
    addCompatMetalMaterial(MaterialIds.steel,           3, ORDER_COMPAT + ORDER_GENERAL, 0x959595);
    addCompatMetalMaterial(MaterialIds.bronze,          3, ORDER_COMPAT + ORDER_HARVEST, 0xD58F36);
    addCompatMetalMaterial(MaterialIds.constantan,      3, ORDER_COMPAT + ORDER_HARVEST, 0x9C5643);
    addCompatMetalMaterial(MaterialIds.invar,           3, ORDER_COMPAT + ORDER_WEAPON,  0xA3B1A8);
    addCompatMetalMaterial(MaterialIds.necronium,       3, ORDER_COMPAT + ORDER_WEAPON,  0x7F9374, "uranium");
    addCompatMetalMaterial(MaterialIds.electrum,        3, ORDER_COMPAT + ORDER_SPECIAL, 0xD9C25F);
    addCompatMetalMaterial(MaterialIds.platedSlimewood, 3, ORDER_COMPAT + ORDER_SPECIAL, 0xE6D08D, "brass");

    // plate
    addMaterial(MaterialIds.obsidian,  6, 9, false, 0x271E3D);
    addMaterial(MaterialIds.debris,    6, 9, false, 0x654740);
    addMaterial(MaterialIds.netherite, 6, 9, false, 0x4C4143);
    addCompatMetalMaterial(MaterialIds.aluminum, 6, 9, 0xCDD5D8);
    addCompatMetalMaterial(MaterialIds.nickel,   6, 9, 0xEBF1DE);
    addCompatMetalMaterial(MaterialIds.tin,      6, 9, 0xA1C6C2);
    addCompatMetalMaterial(MaterialIds.zinc,     6, 9, 0xA8AA93);
    addCompatMetalMaterial(MaterialIds.brass,    6, 9, 0xE6D08D);
    addCompatMetalMaterial(MaterialIds.uranium,  6, 9, 0xA3B1A8);
    // slimeskull - marked tier 6 to push to tne end of repair kits
    addMaterial(MaterialIds.gold,        6, 0, false, 0xFDF55F);
    addMaterial(MaterialIds.gunpowder,   6, 1, true,  0x95D78E);
    addMaterial(MaterialIds.rottenFlesh, 6, 2, true,  0x6F4D1B);
    addMaterial(MaterialIds.spider,      6, 4, true,  0x9D1E2D);
    addMaterial(MaterialIds.venom,       6, 5, true,  0xEDEDED);
    addMaterial(MaterialIds.enderPearl,  6, 6, true,  0x349988);
    // slimesuit - textures
    addMaterial(MaterialIds.earthslime, 6, 9, true, 0x01cd4e);
    addMaterial(MaterialIds.skyslime,   6, 9, true, 0x01cbcd);
    addMaterial(MaterialIds.blood,      6, 9, true, 0xb50101);
    addMaterial(MaterialIds.ichor,      6, 9, true, 0xff970d);
    addMaterial(MaterialIds.enderslime, 6, 9, true, 0xD37CFF);
    addMaterial(MaterialIds.clay,       6, 9, true, 0xAFB9D6);
    addMaterial(MaterialIds.honey,      6, 9, true, 0xFABF29);
    // slimesuit - repair
    addMaterial(MaterialIds.phantom,    6, 9, true, 0xC3B9A1);
    addMaterial(MaterialIds.chorus,     6, 9, true, 0x8F648F);
    addMaterial(MaterialIds.rabbit,     6, 9, true, 0xC79E67);
    // old slimeskull materials
    addRedirect(new MaterialId(TConstruct.MOD_ID, "potato"), redirect(MaterialIds.iron));
    addRedirect(new MaterialId(TConstruct.MOD_ID, "fish"),   redirect(MaterialIds.copper));
  }
}
