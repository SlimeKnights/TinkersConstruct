package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;

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

    // tier 2
    addMaterial(MaterialIds.iron,        2, ORDER_GENERAL, false, 0xD8D8D8);
    addMaterial(MaterialIds.copper,      2, ORDER_HARVEST, true,  0xF98648);
    addMaterial(MaterialIds.searedStone, 2, ORDER_WEAPON,  false, 0x4F4A47);
    addMaterial(MaterialIds.slimewood,   2, ORDER_SPECIAL, true,  0x82c873);
    // tier 2 - nether
    addMaterial(MaterialIds.scorchedStone, 2, ORDER_NETHER, false, 0x5B4C43);

    // tier 3
    addMaterial(MaterialIds.slimesteel,    3, ORDER_GENERAL, false, 0x27C6C6);
    addMaterial(MaterialIds.tinkersBronze, 3, ORDER_HARVEST, false, 0xE8B465);
    addMaterial(MaterialIds.nahuatl,       3, ORDER_WEAPON,  false, 0x3B2754);
    addMaterial(MaterialIds.roseGold,      3, ORDER_SPECIAL, false, 0xF7CDBB);
    addMaterial(MaterialIds.pigIron,       3, ORDER_SPECIAL, false, 0xF0A8A4);
    // tier 3 (nether)
    addMaterial(MaterialIds.cobalt, 3, ORDER_NETHER, false, 0x2376dd);

    // tier 4
    addMaterial(MaterialIds.queensSlime, 4, ORDER_GENERAL, false, 0x236c45);
    addMaterial(MaterialIds.hepatizon,   4, ORDER_HARVEST, false, 0x60496b);
    addMaterial(MaterialIds.manyullyn,   4, ORDER_WEAPON,  false, 0x9261cc);
    //addMetalMaterial(MaterialIds.soulsteel, 4, ORDER_SPECIAL, false, 0x6a5244);

    // tier 2 (end)
    //addMaterialNoFluid(MaterialIds.endstone, 2, ORDER_END, true, 0xe0d890);

    // tier 2 (mod integration)
    addCompatMetalMaterial(MaterialIds.lead,   2, ORDER_COMPAT, 0x575E79);
    addCompatMetalMaterial(MaterialIds.silver, 2, ORDER_COMPAT, 0xD3DFE8);
    // tier 3 (mod integration)
    addCompatMetalMaterial(MaterialIds.electrum,   3, ORDER_COMPAT,  0xD9C25F);
    addCompatMetalMaterial(MaterialIds.bronze,     3, ORDER_HARVEST, 0xD58F36);
    addCompatMetalMaterial(MaterialIds.steel,      3, ORDER_GENERAL, 0x959595);
    addCompatMetalMaterial(MaterialIds.constantan, 3, ORDER_COMPAT,  0x9C5643);
  }
}
