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
        .addTag(TinkerTags.Fluids.GREEN_SLIME)
        .addTag(TinkerTags.Fluids.BLUE_SLIME)
        .addTag(TinkerTags.Fluids.PURPLE_SLIME);
    this.getOrCreateBuilder(TinkerTags.Fluids.SLIMELIKE)
        .add(TinkerFluids.magmaCream.get(), TinkerFluids.magmaCream.getFlowing())
        .addTag(TinkerTags.Fluids.SLIME);

    this.getOrCreateBuilder(TinkerTags.Fluids.GREEN_SLIME).add(TinkerFluids.greenSlime.get(), TinkerFluids.greenSlime.getFlowing());
    this.getOrCreateBuilder(TinkerTags.Fluids.BLUE_SLIME).add(TinkerFluids.blueSlime.get(), TinkerFluids.blueSlime.getFlowing());
    this.getOrCreateBuilder(TinkerTags.Fluids.PURPLE_SLIME).add(TinkerFluids.purpleSlime.get(), TinkerFluids.purpleSlime.getFlowing());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Fluid TinkerTags";
  }

}
