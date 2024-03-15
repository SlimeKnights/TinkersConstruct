package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;

public class SlimeFungusFeature extends HugeFungusFeature {
  public SlimeFungusFeature(Codec<HugeFungusConfiguration> codec) {
    super(codec);
  }

  @Override
  public boolean place(FeaturePlaceContext<HugeFungusConfiguration> context) {
    if (!(context.config() instanceof SlimeFungusConfig config)) {
      return super.place(context);
    }
    // must be on the right ground
    WorldGenLevel level = context.level();
    BlockPos pos = context.origin();
    if (!level.getBlockState(pos.below()).is(config.getGroundTag())) {
      return false;
    }
    // ensure not too tall
    RandomSource random = context.random();
    int height = Mth.nextInt(random, 4, 13);
    if (random.nextInt(12) == 0) {
      height *= 2;
    }
    if (!config.planted && pos.getY() + height + 1 >= context.chunkGenerator().getGenDepth()) {
      return false;
    }
    // actual generation
    boolean flag = !config.planted && random.nextFloat() < 0.06F;
    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
    this.placeStem(level, random, config, pos, height, flag);
    this.placeHat(level, random, config, pos, height, flag);
    return true;
  }
}
