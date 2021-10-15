package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoJson;
import slimeknights.tconstruct.library.data.material.AbstractMaterialRenderInfoProvider;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;

public class MaterialRenderInfoProvider extends AbstractMaterialRenderInfoProvider {
  public MaterialRenderInfoProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  protected void addMaterialRenderInfo() {
    // tier 1
    buildRenderInfo(MaterialIds.wood).color(0x745f38).fallbacks("wood", "stick").build();
    buildRenderInfo(MaterialIds.stone).color(0x696969).fallbacks("rock").build();
    buildRenderInfo(MaterialIds.flint).color(0x3D3C3C).fallbacks("flint").build();
    buildRenderInfo(MaterialIds.bone).color(0xE8E5D2).fallbacks("bone", "rock").build();
    buildRenderInfo(MaterialIds.necroticBone).color(0x2A2A2A).fallbacks("bone", "rock").build();

    // tier 2
    buildRenderInfo(MaterialIds.iron).color(0xD8D8D8).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.copper).color(0xF98648).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.searedStone).color(0x4F4A47).fallbacks("rock").build();
    buildRenderInfo(MaterialIds.scorchedStone).color(0x5B4C43).fallbacks("rock").build();
    buildRenderInfo(MaterialIds.slimewood).color(0x82c873).fallbacks("wood", "stick").build();
    buildRenderInfo(MaterialIds.bloodbone).color(0xB30000).fallbacks("bone", "rock").build();

    // tier 3
    buildRenderInfo(MaterialIds.slimesteel).color(0x27C6C6).fallbacks("slime_metal", "metal").build();
    buildRenderInfo(MaterialIds.tinkersBronze).color(0xE8B465).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.nahuatl).color(0x3B2754).fallbacks("contrast").build();
    buildRenderInfo(MaterialIds.pigIron).color(0xF0A8A4).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.roseGold).color(0xF7CDBB).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.cobalt).color(0x2376dd).fallbacks("metal").build();

    // tier 4
    buildRenderInfo(MaterialIds.queensSlime).color(0x236c45).fallbacks("slime_metal", "metal").build();
    buildRenderInfo(MaterialIds.hepatizon).color(0x60496b).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.manyullyn).color(0x9261cc).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.blazingBone).color(0xF2D500).fallbacks("bone", "rock").luminosity(15).build();

    // tier 2 compat
    buildRenderInfo(MaterialIds.lead).color(0x575E79).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.silver).color(0xD3DFE8).fallbacks("metal").build();

    // tier 3 compat
    buildRenderInfo(MaterialIds.steel).color(0x959595).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.bronze).color(0xD58F36).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.constantan).color(0x9C5643).fallbacks("metal").build();
    buildRenderInfo(MaterialIds.electrum).color(0xD9C25F).fallbacks("metal").build();

    // UI internal
    addInternalMaterial(0, 0xcacaca, "metal");
    addInternalMaterial(1, 0x745f38, "stick");
    addInternalMaterial(2, 0x2882d4, "metal");
    addInternalMaterial(3, 0xa15cf8, "rock");
    addInternalMaterial(4, 0x236c45, "rock");
  }

  /** Adds an internal render material */
  protected void addInternalMaterial(int index, int color, String... fallbacks) {
    addRenderInfo(ToolBuildHandler.getRenderMaterial(index), new MaterialRenderInfoJson(null, fallbacks, toColorString(color), true, 0));
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Render Info Provider";
  }
}
