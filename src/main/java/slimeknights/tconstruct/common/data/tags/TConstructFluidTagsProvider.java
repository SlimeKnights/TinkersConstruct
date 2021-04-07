package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;

public class TConstructFluidTagsProvider extends FluidTagsProvider {

  public TConstructFluidTagsProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
    super(generatorIn, TConstruct.modID, helper);
  }

  @Override
  public void registerTags() {
    // first, register common tags
    // slime
    tagLocal(TinkerFluids.blood);
    tagAll(TinkerFluids.earthSlime);
    tagLocal(TinkerFluids.skySlime);
    tagLocal(TinkerFluids.enderSlime);
    tagAll(TinkerFluids.magmaCream);
    // molten
    tagLocal(TinkerFluids.searedStone);
    tagLocal(TinkerFluids.moltenClay);
    tagLocal(TinkerFluids.moltenGlass);
    tagLocal(TinkerFluids.liquidSoul);
    tagLocal(TinkerFluids.moltenObsidian);
    tagLocal(TinkerFluids.moltenEmerald);
    tagLocal(TinkerFluids.moltenEnder);
    tagLocal(TinkerFluids.moltenBlaze);
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
        .addTag(TinkerFluids.magmaCream.getForgeTag())
        .addTag(TinkerFluids.blood.getLocalTag())
        .addTag(TinkerTags.Fluids.SLIME);
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
