package slimeknights.tconstruct.world.worldgen.old;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import slimeknights.tconstruct.blocks.WorldBlocks;

import javax.annotation.Nullable;
import java.util.Random;

public class MagmaSlimeTree {

  @Nullable
  protected SlimeTreeFeature func_225546_b_(Random random) {
    return new SlimeTreeFeature(NoFeatureConfig::deserialize, true, 5, 4, WorldBlocks.congealed_magma_slime.getDefaultState(), WorldBlocks.orange_slime_leaves.getDefaultState(), null, WorldBlocks.orange_slime_sapling, true);
  }

}
