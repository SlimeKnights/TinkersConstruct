package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;

public class TConstructFluidTagsProvider extends FluidTagsProvider {

  public TConstructFluidTagsProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
    super(generatorIn, TConstruct.modID, helper);
  }

  @Override
  public void registerTags() {
    this.getOrCreateBuilder(TinkerTags.Fluids.SLIME)
        .addTag(TinkerTags.Fluids.EARTH_SLIME)
        .addTag(TinkerTags.Fluids.SKY_SLIME)
        .addTag(TinkerTags.Fluids.ENDER_SLIME);
    this.getOrCreateBuilder(TinkerTags.Fluids.SLIMELIKE)
        .add(TinkerFluids.magmaCream.get(), TinkerFluids.magmaCream.getFlowing(), TinkerFluids.blood.get(), TinkerFluids.blood.getFlowing())
        .addTag(TinkerTags.Fluids.SLIME);

    this.getOrCreateBuilder(TinkerTags.Fluids.EARTH_SLIME).add(TinkerFluids.earthSlime.get(), TinkerFluids.earthSlime.getFlowing());
    this.getOrCreateBuilder(TinkerTags.Fluids.SKY_SLIME).add(TinkerFluids.skySlime.get(), TinkerFluids.skySlime.getFlowing());
    this.getOrCreateBuilder(TinkerTags.Fluids.ENDER_SLIME).add(TinkerFluids.enderSlime.get(), TinkerFluids.enderSlime.getFlowing());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Fluid TinkerTags";
  }

}
