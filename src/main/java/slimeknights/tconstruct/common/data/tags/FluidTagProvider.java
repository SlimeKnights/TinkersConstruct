package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
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
  public void registerTags() {
    // first, register common tags
    // slime
    tagLocal(TinkerFluids.blood);
    tagAll(TinkerFluids.earthSlime);
    tagLocal(TinkerFluids.skySlime);
    tagLocal(TinkerFluids.enderSlime);
    tagAll(TinkerFluids.magma);
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
    tagLocal(TinkerFluids.moltenEnder);
    tagLocal(TinkerFluids.blazingBlood);
    // ores
    tagLocal(TinkerFluids.moltenIron);
    tagLocal(TinkerFluids.moltenGold);
    tagLocal(TinkerFluids.moltenCopper);
    tagLocal(TinkerFluids.moltenCobalt);
    tagLocal(TinkerFluids.moltenDebris);
    // alloys
    tagLocal(TinkerFluids.moltenSlimesteel);
    tagLocal(TinkerFluids.moltenTinkersBronze);
    tagLocal(TinkerFluids.moltenRoseGold);
    tagLocal(TinkerFluids.moltenPigIron);
    // nether alloys
    tagLocal(TinkerFluids.moltenManyullyn);
    tagLocal(TinkerFluids.moltenHepatizon);
    tagLocal(TinkerFluids.moltenQueensSlime);
    tagLocal(TinkerFluids.moltenSoulsteel);
    tagLocal(TinkerFluids.moltenNetherite);
    // end alloys
    tagLocal(TinkerFluids.moltenKnightslime);
    // compat ores
    tagLocal(TinkerFluids.moltenTin);
    tagLocal(TinkerFluids.moltenAluminum);
    tagLocal(TinkerFluids.moltenLead);
    tagLocal(TinkerFluids.moltenSilver);
    tagLocal(TinkerFluids.moltenNickel);
    tagLocal(TinkerFluids.moltenZinc);
    tagLocal(TinkerFluids.moltenPlatinum);
    tagLocal(TinkerFluids.moltenTungsten);
    tagLocal(TinkerFluids.moltenOsmium);
    tagLocal(TinkerFluids.moltenUranium);
    // compat alloys
    tagLocal(TinkerFluids.moltenBronze);
    tagLocal(TinkerFluids.moltenBrass);
    tagLocal(TinkerFluids.moltenElectrum);
    tagLocal(TinkerFluids.moltenInvar);
    tagLocal(TinkerFluids.moltenConstantan);
    tagLocal(TinkerFluids.moltenPewter);
    tagLocal(TinkerFluids.moltenSteel);

    /* Normal tags */
    this.getOrCreateBuilder(TinkerTags.Fluids.SLIME)
        .addTag(TinkerFluids.earthSlime.getForgeTag())
        .addTag(TinkerFluids.skySlime.getLocalTag())
        .addTag(TinkerFluids.enderSlime.getLocalTag());
    this.getOrCreateBuilder(TinkerTags.Fluids.SLIMELIKE)
        .addTag(TinkerFluids.magma.getForgeTag())
        .addTag(TinkerFluids.blood.getLocalTag())
        .addTag(TinkerFluids.moltenEnder.getLocalTag())
        .addTag(TinkerTags.Fluids.SLIME);

    // these fluids should get ingot and nugget values assigned even if they lack casting recipes
    this.getOrCreateBuilder(TinkerTags.Fluids.METAL_LIKE)
        .addTag(TinkerFluids.moltenEmerald.getLocalTag())
        .addTag(TinkerFluids.moltenQuartz.getLocalTag())
        .addTag(TinkerFluids.moltenDiamond.getLocalTag())
        .addTag(TinkerFluids.moltenIron.getLocalTag())
        .addTag(TinkerFluids.moltenGold.getLocalTag())
        .addTag(TinkerFluids.moltenCopper.getLocalTag())
        .addTag(TinkerFluids.moltenCobalt.getLocalTag())
        .addTag(TinkerFluids.moltenDebris.getLocalTag())
        .addTag(TinkerFluids.moltenSlimesteel.getLocalTag())
        .addTag(TinkerFluids.moltenTinkersBronze.getLocalTag())
        .addTag(TinkerFluids.moltenRoseGold.getLocalTag())
        .addTag(TinkerFluids.moltenPigIron.getLocalTag())
        .addTag(TinkerFluids.moltenManyullyn.getLocalTag())
        .addTag(TinkerFluids.moltenHepatizon.getLocalTag())
        .addTag(TinkerFluids.moltenQueensSlime.getLocalTag())
        .addTag(TinkerFluids.moltenSoulsteel.getLocalTag())
        .addTag(TinkerFluids.moltenNetherite.getLocalTag())
        .addTag(TinkerFluids.moltenKnightslime.getLocalTag())
        .addTag(TinkerFluids.moltenTin.getLocalTag())
        .addTag(TinkerFluids.moltenAluminum.getLocalTag())
        .addTag(TinkerFluids.moltenLead.getLocalTag())
        .addTag(TinkerFluids.moltenSilver.getLocalTag())
        .addTag(TinkerFluids.moltenNickel.getLocalTag())
        .addTag(TinkerFluids.moltenZinc.getLocalTag())
        .addTag(TinkerFluids.moltenPlatinum.getLocalTag())
        .addTag(TinkerFluids.moltenTungsten.getLocalTag())
        .addTag(TinkerFluids.moltenOsmium.getLocalTag())
        .addTag(TinkerFluids.moltenUranium.getLocalTag())
        .addTag(TinkerFluids.moltenBronze.getLocalTag())
        .addTag(TinkerFluids.moltenBrass.getLocalTag())
        .addTag(TinkerFluids.moltenElectrum.getLocalTag())
        .addTag(TinkerFluids.moltenInvar.getLocalTag())
        .addTag(TinkerFluids.moltenConstantan.getLocalTag())
        .addTag(TinkerFluids.moltenPewter.getLocalTag())
        .addTag(TinkerFluids.moltenSteel.getLocalTag());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Fluid TinkerTags";
  }

  /** Tags this fluid using local tags */
  private void tagLocal(FluidObject<?> fluid) {
    getOrCreateBuilder(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
  }

  /** Tags this fluid with local and forge tags */
  private void tagAll(FluidObject<?> fluid) {
    tagLocal(fluid);
    getOrCreateBuilder(fluid.getForgeTag()).addTag(fluid.getLocalTag());
  }
}
