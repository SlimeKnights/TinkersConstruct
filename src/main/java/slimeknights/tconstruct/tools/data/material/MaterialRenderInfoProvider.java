package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialRenderInfoProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;

public class MaterialRenderInfoProvider extends AbstractMaterialRenderInfoProvider {
  public MaterialRenderInfoProvider(PackOutput packOutput, AbstractMaterialSpriteProvider spriteProvider, ExistingFileHelper existingFileHelper) {
    super(packOutput, spriteProvider, existingFileHelper);
  }

  @Override
  protected void addMaterialRenderInfo() {
    // fallback
    buildRenderInfo(IMaterial.UNKNOWN_ID);

    // tier 1
    buildRenderInfo(MaterialIds.flint).color(0x3D3C3C).fallbacks("crystal", "rock", "stick");
    buildRenderInfo(MaterialIds.basalt);
    buildRenderInfo(MaterialIds.bone).color(0xE8E5D2).fallbacks("bone", "rock");
    buildRenderInfo(MaterialIds.chorus);
    buildRenderInfo(MaterialIds.string).color(0xFFFFFF);
    buildRenderInfo(MaterialIds.leather).color(0xC65C35);
    buildRenderInfo(MaterialIds.vine).color(0x48B518).fallbacks("vine");
    // tier 1 - wood
    buildRenderInfo(MaterialIds.wood).color(0x876627).fallbacks("wood", "stick", "primitive");
    buildRenderInfo(MaterialIds.oak);
    buildRenderInfo(MaterialIds.spruce);
    buildRenderInfo(MaterialIds.birch);
    buildRenderInfo(MaterialIds.jungle);
    buildRenderInfo(MaterialIds.darkOak);
    buildRenderInfo(MaterialIds.acacia);
    buildRenderInfo(MaterialIds.mangrove);
    buildRenderInfo(MaterialIds.cherry);
    buildRenderInfo(MaterialIds.crimson);
    buildRenderInfo(MaterialIds.warped);
    buildRenderInfo(MaterialIds.bamboo);
    // tier 1 - stone
    buildRenderInfo(MaterialIds.rock).materialTexture(MaterialIds.stone).color(0xB1AFAD).fallbacks("rock");
    buildRenderInfo(MaterialIds.stone).color(0xB1AFAD);
    buildRenderInfo(MaterialIds.andesite);
    buildRenderInfo(MaterialIds.diorite);
    buildRenderInfo(MaterialIds.granite);
    buildRenderInfo(MaterialIds.deepslate);
    buildRenderInfo(MaterialIds.blackstone);

    // tier 2
    buildRenderInfo(MaterialIds.iron).color(0xD8D8D8).fallbacks("metal");
    buildRenderInfo(MaterialIds.wroughtIron).color(0x3E4453).fallbacks("metal");
    buildRenderInfo(MaterialIds.oxidizedIron).color(0xE9C8B1).fallbacks("metal");
    buildRenderInfo(MaterialIds.copper).color(0xE77C56).fallbacks("metal");
    buildRenderInfo(MaterialIds.oxidizedCopper).color(0x4FAB90).fallbacks("metal");
    buildRenderInfo(MaterialIds.searedStone).color(0x4F4A47).fallbacks("rock");
    buildRenderInfo(MaterialIds.scorchedStone).color(0x5B4C43).fallbacks("rock");
    buildRenderInfo(MaterialIds.venombone).color(0xA2935E).fallbacks("bone", "rock");
    buildRenderInfo(MaterialIds.necroticBone).color(0x2A2A2A).fallbacks("bone", "rock");
    buildRenderInfo(MaterialIds.endstone);
    buildRenderInfo(MaterialIds.skyslimeVine).color(0x00F4DA).fallbacks("vine");
    // slimewood
    buildRenderInfo(MaterialIds.slimewood).materialTexture(MaterialIds.greenheart).color(0x82c873).fallbacks("wood", "primitive");
    buildRenderInfo(MaterialIds.greenheart);
    buildRenderInfo(MaterialIds.skyroot);
    buildRenderInfo(MaterialIds.bloodshroom);
    buildRenderInfo(MaterialIds.enderbark);

    // tier 3
    buildRenderInfo(MaterialIds.slimesteel).color(0x46ECE7).fallbacks("slime_metal", "metal");
    // default texture is tin even though silicon is the one we provide, as it makes the names cleaner
    buildRenderInfo(MaterialIds.amethystBronze).color(0xD9A2D0).fallbacks("metal");
    buildRenderInfo(MaterialIds.nahuatl).color(0x3B2754).fallbacks("wood", "stick");
    buildRenderInfo(MaterialIds.pigIron).color(0xF0A8A4).fallbacks("metal");
    buildRenderInfo(MaterialIds.roseGold).color(0xF7CDBB).fallbacks("metal");
    buildRenderInfo(MaterialIds.cobalt).color(0x2376dd).fallbacks("metal");
    buildRenderInfo(MaterialIds.darkthread);

    // tier 4
    buildRenderInfo(MaterialIds.queensSlime).color(0x809912).fallbacks("slime_metal", "metal").luminosity(9);
    buildRenderInfo(MaterialIds.hepatizon).color(0x60496b).fallbacks("metal");
    buildRenderInfo(MaterialIds.manyullyn).color(0x9261cc).fallbacks("metal");
    buildRenderInfo(MaterialIds.blazingBone).color(0xF2D500).fallbacks("bone", "rock").luminosity(15);
    buildRenderInfo(MaterialIds.blazewood).fallbacks("wood", "stick").luminosity(7);
    buildRenderInfo(MaterialIds.ancientHide);
    buildRenderInfo(MaterialIds.enderslimeVine).color(0xa92dff).fallbacks("vine");

    // tier 2 compat
    buildRenderInfo(MaterialIds.osmium).color(0xC1E6F4).fallbacks("metal");
    buildRenderInfo(MaterialIds.tungsten).color(0x6F6F62).fallbacks("metal");
    buildRenderInfo(MaterialIds.platinum).color(0xA3E7FE).fallbacks("metal");
    buildRenderInfo(MaterialIds.silver).color(0xDAF3ED).fallbacks("metal");
    buildRenderInfo(MaterialIds.lead).color(0x696579).fallbacks("metal");
    buildRenderInfo(MaterialIds.whitestone).color(0xE0E9EC).fallbacks("rock");
    buildRenderInfo(MaterialIds.aluminum);

    // tier 3 compat
    buildRenderInfo(MaterialIds.steel).color(0x959595).fallbacks("metal");
    buildRenderInfo(MaterialIds.bronze).color(0xD49765).fallbacks("metal");
    buildRenderInfo(MaterialIds.constantan).color(0xFF8B70).fallbacks("metal");
    buildRenderInfo(MaterialIds.invar).color(0xCADBD0).fallbacks("metal");
    buildRenderInfo(MaterialIds.necronium).color(0x9CBD89).fallbacks("bone", "metal");
    buildRenderInfo(MaterialIds.electrum).color(0xFFEA65).fallbacks("metal");
    buildRenderInfo(MaterialIds.platedSlimewood).color(0xFFE170).fallbacks("slime_metal", "metal");

    // plate
    buildRenderInfo(MaterialIds.gold).color(0xFDF55F).fallbacks("metal");
    buildRenderInfo(MaterialIds.obsidian);
    // slimeskull
    buildRenderInfo(MaterialIds.glass);
    buildRenderInfo(MaterialIds.enderPearl);
    buildRenderInfo(MaterialIds.rottenFlesh);
    // slimesuit
    buildRenderInfo(MaterialIds.earthslime);
    buildRenderInfo(MaterialIds.skyslime);
    buildRenderInfo(MaterialIds.blood);
    buildRenderInfo(MaterialIds.magma);
    buildRenderInfo(MaterialIds.ichor);
    buildRenderInfo(MaterialIds.enderslime);
    buildRenderInfo(MaterialIds.clay);
    buildRenderInfo(MaterialIds.honey);

    buildRenderInfo(MaterialIds.phantom);

    // UI internal
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(0)).color(0xD8D8D8).texture(MaterialIds.iron).fallbacks("metal");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(1)).color(0x745f38).texture(MaterialIds.wood).fallbacks("wood", "stick");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(2)).color(0x2376dd).texture(MaterialIds.cobalt).fallbacks("metal");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(3)).color(0x9261cc).texture(MaterialIds.manyullyn).fallbacks("metal");
    buildRenderInfo(ToolBuildHandler.getRenderMaterial(4)).color(0xF98648).texture(MaterialIds.copper).fallbacks("metal");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Render Info Provider";
  }
}
