package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialRenderInfoProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;

public class MaterialRenderInfoProvider extends AbstractMaterialRenderInfoProvider {
  public MaterialRenderInfoProvider(DataGenerator gen, AbstractMaterialSpriteProvider spriteProvider) {
    super(gen, spriteProvider);
  }

  @Override
  protected void addMaterialRenderInfo() {
    // tier 1
    buildRenderInfo(MaterialIds.wood).color(0x745f38).fallbacks("wood", "stick");
    buildRenderInfo(MaterialIds.stone).color(0x696969).fallbacks("rock");
    buildRenderInfo(MaterialIds.flint).color(0x3D3C3C).fallbacks("flint");
    buildRenderInfo(MaterialIds.bone).color(0xE8E5D2).fallbacks("bone", "rock");
    buildRenderInfo(MaterialIds.necroticBone).color(0x2A2A2A).fallbacks("bone", "rock");
    buildRenderInfo(MaterialIds.string).color(0xFFFFFF);
    buildRenderInfo(MaterialIds.leather).color(0xC65C35);
    buildRenderInfo(MaterialIds.vine).color(0x48B518).fallbacks("vine");

    // tier 2
    buildRenderInfo(MaterialIds.iron).color(0xD8D8D8).fallbacks("metal");
    buildRenderInfo(MaterialIds.copper).color(0xF98648).fallbacks("metal");
    buildRenderInfo(MaterialIds.searedStone).color(0x4F4A47).fallbacks("rock");
    buildRenderInfo(MaterialIds.scorchedStone).color(0x5B4C43).fallbacks("rock");
    buildRenderInfo(MaterialIds.slimewood).color(0x82c873).fallbacks("wood", "stick");
    buildRenderInfo(MaterialIds.bloodbone).color(0xB30000).fallbacks("bone", "rock");
    buildRenderInfo(MaterialIds.chain).color(0x3E4453).fallbacks("chain", "metal");
    buildRenderInfo(MaterialIds.skyslimeVine).color(0x00F4DA).fallbacks("vine");

    // tier 3
    buildRenderInfo(MaterialIds.slimesteel).color(0x27C6C6).fallbacks("slime_metal", "metal");
    buildRenderInfo(MaterialIds.tinkersBronze).color(0xE8B465).fallbacks("metal");
    buildRenderInfo(MaterialIds.nahuatl).color(0x3B2754).fallbacks("contrast");
    buildRenderInfo(MaterialIds.pigIron).color(0xF0A8A4).fallbacks("metal");
    buildRenderInfo(MaterialIds.roseGold).color(0xF7CDBB).fallbacks("metal");
    buildRenderInfo(MaterialIds.cobalt).color(0x2376dd).fallbacks("metal");

    // tier 4
    buildRenderInfo(MaterialIds.queensSlime).color(0x236c45).fallbacks("slime_metal", "metal");
    buildRenderInfo(MaterialIds.hepatizon).color(0x60496b).fallbacks("metal");
    buildRenderInfo(MaterialIds.manyullyn).color(0x9261cc).fallbacks("metal");
    buildRenderInfo(MaterialIds.blazingBone).color(0xF2D500).fallbacks("bone", "rock").luminosity(15);
    buildRenderInfo(MaterialIds.enderslimeVine).color(0xa92dff).fallbacks("vine");

    // tier 2 compat
    buildRenderInfo(MaterialIds.osmium).color(0xBED3CD).fallbacks("metal");
    buildRenderInfo(MaterialIds.tungsten).color(0xD1C08B).fallbacks("metal");
    buildRenderInfo(MaterialIds.platinum).color(0xA3E7FE).fallbacks("metal");
    buildRenderInfo(MaterialIds.silver).color(0xD3DFE8).fallbacks("metal");
    buildRenderInfo(MaterialIds.lead).color(0x575E79).fallbacks("metal");
    buildRenderInfo(MaterialIds.whitestone).color(0xE0E9EC).fallbacks("rock");

    // tier 3 compat
    buildRenderInfo(MaterialIds.steel).color(0x959595).fallbacks("metal");
    buildRenderInfo(MaterialIds.bronze).color(0xD58F36).fallbacks("metal");
    buildRenderInfo(MaterialIds.constantan).color(0x9C5643).fallbacks("metal");
    buildRenderInfo(MaterialIds.invar).color(0xA3B1A8).fallbacks("metal");
    buildRenderInfo(MaterialIds.necronium).color(0x7F9374).fallbacks("bone", "metal");
    buildRenderInfo(MaterialIds.electrum).color(0xD9C25F).fallbacks("metal");
    buildRenderInfo(MaterialIds.platedSlimewood).color(0xE6D08D).fallbacks("slime_metal", "metal");

    // UI internal
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(0)).color(0xD8D8D8).texture(new ResourceLocation(TConstruct.MOD_ID, "iron")).fallbacks("metal");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(1)).color(0x745f38).texture(new ResourceLocation(TConstruct.MOD_ID, "wood")).fallbacks("wood", "stick");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(2)).color(0x2376dd).texture(new ResourceLocation(TConstruct.MOD_ID, "cobalt")).fallbacks("metal");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(3)).color(0x9261cc).texture(new ResourceLocation(TConstruct.MOD_ID, "manyullyn")).fallbacks("metal");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(4)).color(0xF98648).texture(new ResourceLocation(TConstruct.MOD_ID, "copper")).fallbacks("metal");
  }

  /** Adds an internal render material */
  protected void buildInternalMaterial(int index, int color, String... fallbacks) {
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(index)).color(color).fallbacks(fallbacks).skipUniqueTexture(true);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Render Info Provider";
  }
}
