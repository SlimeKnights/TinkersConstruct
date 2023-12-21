package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;

@SuppressWarnings("unchecked")
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
    tagLocal(TinkerFluids.moltenAmethyst);
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
    tagAll(TinkerFluids.moltenAmethystBronze);
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
    // unplacable fluids
    tagAll(TinkerFluids.honey);
    tagAll(TinkerFluids.beetrootSoup);
    tagAll(TinkerFluids.mushroomStew);
    tagAll(TinkerFluids.rabbitStew);

    /* Normal tags */
    this.tag(TinkerTags.Fluids.SLIME)
        .addTag(TinkerFluids.earthSlime.getForgeTag())
        .addTag(TinkerFluids.skySlime.getLocalTag())
        .addTag(TinkerFluids.enderSlime.getLocalTag());

    this.tag(TinkerTags.Fluids.POTION).add(TinkerFluids.potion.get());
    this.tag(TinkerTags.Fluids.POWDERED_SNOW).add(TinkerFluids.powderedSnow.get());

    // tooltips //
    this.tag(TinkerTags.Fluids.GLASS_TOOLTIPS).addTags(TinkerFluids.moltenGlass.getLocalTag(), TinkerFluids.liquidSoul.getLocalTag(), TinkerFluids.moltenObsidian.getLocalTag());
    this.tag(TinkerTags.Fluids.SLIME_TOOLTIPS).addTags(TinkerFluids.magma.getForgeTag(), TinkerFluids.blood.getLocalTag(), TinkerFluids.moltenEnder.getForgeTag(), TinkerTags.Fluids.SLIME);
    this.tag(TinkerTags.Fluids.CLAY_TOOLTIPS).addTags(TinkerFluids.moltenClay.getLocalTag(), TinkerFluids.moltenPorcelain.getLocalTag(), TinkerFluids.searedStone.getLocalTag(), TinkerFluids.scorchedStone.getLocalTag());
    this.tag(TinkerTags.Fluids.METAL_TOOLTIPS).addTags(
        // vanilla ores
        TinkerFluids.moltenIron.getForgeTag(), TinkerFluids.moltenGold.getForgeTag(), TinkerFluids.moltenCopper.getForgeTag(), TinkerFluids.moltenCobalt.getForgeTag(), TinkerFluids.moltenDebris.getLocalTag(),
        // base alloys
        TinkerFluids.moltenSlimesteel.getLocalTag(), TinkerFluids.moltenAmethystBronze.getLocalTag(), TinkerFluids.moltenRoseGold.getForgeTag(), TinkerFluids.moltenPigIron.getLocalTag(),
        TinkerFluids.moltenManyullyn.getForgeTag(), TinkerFluids.moltenHepatizon.getForgeTag(), TinkerFluids.moltenQueensSlime.getLocalTag(), TinkerFluids.moltenNetherite.getForgeTag(),
        TinkerFluids.moltenSoulsteel.getLocalTag(), TinkerFluids.moltenKnightslime.getLocalTag(),
        // compat ores
        TinkerFluids.moltenTin.getForgeTag(), TinkerFluids.moltenAluminum.getForgeTag(), TinkerFluids.moltenLead.getForgeTag(), TinkerFluids.moltenSilver.getForgeTag(),
        TinkerFluids.moltenNickel.getForgeTag(), TinkerFluids.moltenZinc.getForgeTag(), TinkerFluids.moltenPlatinum.getForgeTag(),
        TinkerFluids.moltenTungsten.getForgeTag(), TinkerFluids.moltenOsmium.getForgeTag(), TinkerFluids.moltenUranium.getForgeTag(),
        // compat alloys
        TinkerFluids.moltenBronze.getForgeTag(), TinkerFluids.moltenBrass.getForgeTag(), TinkerFluids.moltenElectrum.getForgeTag(),
        TinkerFluids.moltenInvar.getForgeTag(), TinkerFluids.moltenConstantan.getForgeTag(), TinkerFluids.moltenPewter.getForgeTag(), TinkerFluids.moltenSteel.getForgeTag(),
        // thermal alloys
        TinkerFluids.moltenEnderium.getForgeTag(), TinkerFluids.moltenLumium.getForgeTag(), TinkerFluids.moltenSignalum.getForgeTag(),
        // mekanism alloys
        TinkerFluids.moltenRefinedGlowstone.getForgeTag(), TinkerFluids.moltenRefinedObsidian.getForgeTag());

    this.tag(TinkerTags.Fluids.LARGE_GEM_TOOLTIPS).addTags(TinkerFluids.moltenEmerald.getLocalTag(), TinkerFluids.moltenDiamond.getLocalTag());
    this.tag(TinkerTags.Fluids.SMALL_GEM_TOOLTIPS).addTags(TinkerFluids.moltenQuartz.getLocalTag(), TinkerFluids.moltenAmethyst.getLocalTag());
    this.tag(TinkerTags.Fluids.SOUP_TOOLTIPS).addTags(TinkerFluids.beetrootSoup.getLocalTag(), TinkerFluids.mushroomStew.getLocalTag(), TinkerFluids.rabbitStew.getLocalTag());
    this.tag(TinkerTags.Fluids.WATER_TOOLTIPS).addTag(MantleTags.Fluids.WATER);

    // spilling tags - reduces the number of recipes generated //
    this.tag(TinkerTags.Fluids.CLAY_SPILLING)
        .addTag(TinkerFluids.moltenClay.getLocalTag())
        .addTag(TinkerFluids.moltenPorcelain.getLocalTag())
        .addTag(TinkerFluids.searedStone.getLocalTag())
        .addTag(TinkerFluids.scorchedStone.getLocalTag());
    this.tag(TinkerTags.Fluids.GLASS_SPILLING)
        .addTag(TinkerFluids.moltenGlass.getLocalTag())
        .addTag(TinkerFluids.moltenObsidian.getLocalTag());
    this.tag(TinkerTags.Fluids.CHEAP_METAL_SPILLING)
        .addTag(TinkerFluids.moltenPlatinum.getForgeTag())
        .addTag(TinkerFluids.moltenTungsten.getForgeTag())
        .addTag(TinkerFluids.moltenOsmium.getForgeTag())
        .addTag(TinkerFluids.moltenAmethyst.getLocalTag());
    this.tag(TinkerTags.Fluids.AVERAGE_METAL_SPILLING)
        .addTag(TinkerFluids.moltenQuartz.getLocalTag())
        .addTag(TinkerFluids.moltenEmerald.getLocalTag())
        .addTag(TinkerFluids.moltenRefinedGlowstone.getForgeTag());
    this.tag(TinkerTags.Fluids.EXPENSIVE_METAL_SPILLING)
        .addTag(TinkerFluids.moltenDiamond.getLocalTag())
        .addTag(TinkerFluids.moltenDebris.getLocalTag())
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
