package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;

public class FluidTagProvider extends FluidTagsProvider {

  public FluidTagProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
    super(generatorIn, TConstruct.MOD_ID, helper);
  }

  @Override
  public void addTags() {
    // first, register common tags
    // slime
    tagLocal(TinkerFluids.blood);
    tagAll(TinkerFluids.earthSlime);
    tagLocal(TinkerFluids.skySlime);
    tagLocal(TinkerFluids.enderSlime);
    tagAll(TinkerFluids.magma);
    tagLocal(TinkerFluids.venom);
    // basic molten
    tagLocal(TinkerFluids.searedStone);
    tagLocal(TinkerFluids.scorchedStone);
    tagLocal(TinkerFluids.moltenClay);
    tagLocal(TinkerFluids.moltenGlass);
    tagLocal(TinkerFluids.liquidSoul);
    tagLocal(TinkerFluids.moltenPorcelain);
    // fancy molten
    tagLocal(TinkerFluids.moltenObsidian);
    tagLocal(TinkerFluids.moltenEmerald);
    tagLocal(TinkerFluids.moltenQuartz);
    tagLocal(TinkerFluids.moltenDiamond);
    tagAll(TinkerFluids.moltenEnder);
    tagLocal(TinkerFluids.blazingBlood);
    // ores
    tagAll(TinkerFluids.moltenIron);
    tagAll(TinkerFluids.moltenGold);
    tagAll(TinkerFluids.moltenCopper);
    tagAll(TinkerFluids.moltenCobalt);
    tagLocal(TinkerFluids.moltenDebris);
    // alloys
    tagLocal(TinkerFluids.moltenSlimesteel);
    tagAll(TinkerFluids.moltenTinkersBronze);
    tagAll(TinkerFluids.moltenRoseGold);
    tagLocal(TinkerFluids.moltenPigIron);
    // nether alloys
    tagAll(TinkerFluids.moltenManyullyn);
    tagAll(TinkerFluids.moltenHepatizon);
    tagLocal(TinkerFluids.moltenQueensSlime);
    tagLocal(TinkerFluids.moltenSoulsteel);
    tagAll(TinkerFluids.moltenNetherite);
    // end alloys
    tagLocal(TinkerFluids.moltenKnightslime);
    // compat ores
    tagAll(TinkerFluids.moltenTin);
    tagAll(TinkerFluids.moltenAluminum);
    tagAll(TinkerFluids.moltenLead);
    tagAll(TinkerFluids.moltenSilver);
    tagAll(TinkerFluids.moltenNickel);
    tagAll(TinkerFluids.moltenZinc);
    tagAll(TinkerFluids.moltenPlatinum);
    tagAll(TinkerFluids.moltenTungsten);
    tagAll(TinkerFluids.moltenOsmium);
    tagAll(TinkerFluids.moltenUranium);
    // compat alloys
    tagAll(TinkerFluids.moltenBronze);
    tagAll(TinkerFluids.moltenBrass);
    tagAll(TinkerFluids.moltenElectrum);
    tagAll(TinkerFluids.moltenInvar);
    tagAll(TinkerFluids.moltenConstantan);
    tagAll(TinkerFluids.moltenPewter);
    tagAll(TinkerFluids.moltenSteel);
    // thermal compat alloys
    tagAll(TinkerFluids.moltenEnderium);
    tagAll(TinkerFluids.moltenLumium);
    tagAll(TinkerFluids.moltenSignalum);
    // mekanism compat alloys
    tagAll(TinkerFluids.moltenRefinedGlowstone);
    tagAll(TinkerFluids.moltenRefinedObsidian);

    /* Normal tags */
    this.tag(TinkerTags.Fluids.SLIME)
        .addTag(TinkerFluids.earthSlime.getForgeTag())
        .addTag(TinkerFluids.skySlime.getLocalTag())
        .addTag(TinkerFluids.enderSlime.getLocalTag());
    this.tag(TinkerTags.Fluids.SLIMELIKE)
        .addTag(TinkerFluids.magma.getForgeTag())
        .addTag(TinkerFluids.blood.getLocalTag())
        .addTag(TinkerFluids.moltenEnder.getForgeTag())
        .addTag(TinkerTags.Fluids.SLIME);

    // these fluids should get ingot and nugget values assigned even if they lack casting recipes
    this.tag(TinkerTags.Fluids.METAL_LIKE)
        // non-metal
        .addTag(TinkerFluids.moltenEmerald.getLocalTag())
        .addTag(TinkerFluids.moltenQuartz.getLocalTag())
        .addTag(TinkerFluids.moltenDiamond.getLocalTag())
        // vanilla ores
        .addTag(TinkerFluids.moltenIron.getForgeTag())
        .addTag(TinkerFluids.moltenGold.getForgeTag())
        .addTag(TinkerFluids.moltenCopper.getForgeTag())
        .addTag(TinkerFluids.moltenCobalt.getForgeTag())
        .addTag(TinkerFluids.moltenDebris.getLocalTag())
        // base alloys
        .addTag(TinkerFluids.moltenSlimesteel.getLocalTag())
        .addTag(TinkerFluids.moltenTinkersBronze.getLocalTag())
        .addTag(TinkerFluids.moltenRoseGold.getForgeTag())
        .addTag(TinkerFluids.moltenPigIron.getLocalTag())
        .addTag(TinkerFluids.moltenManyullyn.getForgeTag())
        .addTag(TinkerFluids.moltenHepatizon.getForgeTag())
        .addTag(TinkerFluids.moltenQueensSlime.getLocalTag())
        .addTag(TinkerFluids.moltenSoulsteel.getLocalTag())
        .addTag(TinkerFluids.moltenNetherite.getForgeTag())
        .addTag(TinkerFluids.moltenKnightslime.getLocalTag())
        // compat ores
        .addTag(TinkerFluids.moltenTin.getForgeTag())
        .addTag(TinkerFluids.moltenAluminum.getForgeTag())
        .addTag(TinkerFluids.moltenLead.getForgeTag())
        .addTag(TinkerFluids.moltenSilver.getForgeTag())
        .addTag(TinkerFluids.moltenNickel.getForgeTag())
        .addTag(TinkerFluids.moltenZinc.getForgeTag())
        .addTag(TinkerFluids.moltenPlatinum.getForgeTag())
        .addTag(TinkerFluids.moltenTungsten.getForgeTag())
        .addTag(TinkerFluids.moltenOsmium.getForgeTag())
        .addTag(TinkerFluids.moltenUranium.getForgeTag())
        // compat alloys
        .addTag(TinkerFluids.moltenBronze.getForgeTag())
        .addTag(TinkerFluids.moltenBrass.getForgeTag())
        .addTag(TinkerFluids.moltenElectrum.getForgeTag())
        .addTag(TinkerFluids.moltenInvar.getForgeTag())
        .addTag(TinkerFluids.moltenConstantan.getForgeTag())
        .addTag(TinkerFluids.moltenPewter.getForgeTag())
        .addTag(TinkerFluids.moltenSteel.getForgeTag())
        // thermal alloys
        .addTag(TinkerFluids.moltenEnderium.getForgeTag())
        .addTag(TinkerFluids.moltenLumium.getForgeTag())
        .addTag(TinkerFluids.moltenSignalum.getForgeTag())
        // thermal alloys
        .addTag(TinkerFluids.moltenRefinedGlowstone.getForgeTag())
        .addTag(TinkerFluids.moltenRefinedObsidian.getForgeTag());

    // spilling tags - reduces the number of recipes generated
    this.tag(TinkerTags.Fluids.CLAY_SPILLING)
        .addTag(TinkerFluids.moltenClay.getLocalTag())
        .addTag(TinkerFluids.moltenPorcelain.getLocalTag());
    this.tag(TinkerTags.Fluids.GLASS_SPILLING)
        .addTag(TinkerFluids.moltenGlass.getLocalTag())
        .addTag(TinkerFluids.moltenObsidian.getLocalTag());
    this.tag(TinkerTags.Fluids.CHEAP_METAL_SPILLING)
        .addTag(TinkerFluids.searedStone.getLocalTag())
        .addTag(TinkerFluids.scorchedStone.getLocalTag())
        .addTag(TinkerFluids.moltenIron.getForgeTag())
        .addTag(TinkerFluids.moltenTin.getForgeTag())
        .addTag(TinkerFluids.moltenAluminum.getForgeTag())
        .addTag(TinkerFluids.moltenLead.getForgeTag())
        .addTag(TinkerFluids.moltenSilver.getForgeTag())
        .addTag(TinkerFluids.moltenNickel.getForgeTag())
        .addTag(TinkerFluids.moltenZinc.getForgeTag())
        .addTag(TinkerFluids.moltenPlatinum.getForgeTag())
        .addTag(TinkerFluids.moltenTungsten.getForgeTag())
        .addTag(TinkerFluids.moltenOsmium.getForgeTag());
    this.tag(TinkerTags.Fluids.AVERAGE_METAL_SPILLING)
        .addTag(TinkerFluids.moltenQuartz.getLocalTag())
        .addTag(TinkerFluids.moltenEmerald.getLocalTag())
        .addTag(TinkerFluids.moltenCobalt.getForgeTag())
        .addTag(TinkerFluids.moltenTinkersBronze.getLocalTag())
        .addTag(TinkerFluids.moltenRoseGold.getForgeTag())
        .addTag(TinkerFluids.moltenSlimesteel.getLocalTag())
        .addTag(TinkerFluids.moltenBronze.getForgeTag())
        .addTag(TinkerFluids.moltenBrass.getForgeTag())
        .addTag(TinkerFluids.moltenElectrum.getForgeTag())
        .addTag(TinkerFluids.moltenInvar.getForgeTag())
        .addTag(TinkerFluids.moltenConstantan.getForgeTag())
        .addTag(TinkerFluids.moltenPewter.getForgeTag())
        .addTag(TinkerFluids.moltenSteel.getForgeTag())
        .addTag(TinkerFluids.moltenRefinedGlowstone.getForgeTag());
    this.tag(TinkerTags.Fluids.EXPENSIVE_METAL_SPILLING)
        .addTag(TinkerFluids.moltenDiamond.getLocalTag())
        .addTag(TinkerFluids.moltenDebris.getLocalTag())
        .addTag(TinkerFluids.moltenManyullyn.getForgeTag())
        .addTag(TinkerFluids.moltenHepatizon.getForgeTag())
        .addTag(TinkerFluids.moltenQueensSlime.getLocalTag())
        .addTag(TinkerFluids.moltenNetherite.getForgeTag())
        .addTag(TinkerFluids.moltenEnderium.getForgeTag())
        .addTag(TinkerFluids.moltenLumium.getForgeTag())
        .addTag(TinkerFluids.moltenSignalum.getForgeTag())
        .addTag(TinkerFluids.moltenRefinedObsidian.getForgeTag());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Fluid TinkerTags";
  }

  /** Tags this fluid using local tags */
  private void tagLocal(FluidObject<?> fluid) {
    tag(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
  }

  /** Tags this fluid with local and forge tags */
  private void tagAll(FluidObject<?> fluid) {
    tagLocal(fluid);
    tag(fluid.getForgeTag()).addTag(fluid.getLocalTag());
  }
}
