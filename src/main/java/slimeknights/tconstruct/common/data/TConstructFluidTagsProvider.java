package slimeknights.tconstruct.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;

public class TConstructFluidTagsProvider extends FluidTagsProvider {

  public TConstructFluidTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  public void registerTags() {
    this.getOrCreateBuilder(TinkerTags.Fluids.MILK).add(TinkerFluids.milk.get());

    this.getOrCreateBuilder(TinkerTags.Fluids.SLIME)
        .addTag(TinkerTags.Fluids.BLUE_SLIME)
        .addTag(TinkerTags.Fluids.PURPLE_SLIME);

    this.getOrCreateBuilder(TinkerTags.Fluids.BLUE_SLIME).add(TinkerFluids.blueSlime.get(), TinkerFluids.blueSlime.getFlowing());
    this.getOrCreateBuilder(TinkerTags.Fluids.PURPLE_SLIME).add(TinkerFluids.purpleSlime.get(), TinkerFluids.purpleSlime.getFlowing());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Fluid TinkerTags";
  }

}
