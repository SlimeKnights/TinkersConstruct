package slimeknights.tconstruct.world.worldgen.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class BaseSlimeTreeFeatureConfig implements FeatureConfig {

  public static final Codec<BaseSlimeTreeFeatureConfig> CODEC = RecordCodecBuilder.create((treeConfig) -> treeConfig.group(BlockStateProvider.TYPE_CODEC.fieldOf("trunk_provider").forGetter((object) -> object.trunkProvider),
    BlockStateProvider.TYPE_CODEC.fieldOf("leaves_provider").forGetter((instance) -> instance.leavesProvider),
    BlockStateProvider.TYPE_CODEC.fieldOf("vines_provider").forGetter((instance) -> instance.vinesProvider),
    Codec.INT.fieldOf("base_height").orElse(0).forGetter((instance) -> instance.baseHeight),
    Codec.INT.fieldOf("random_height").orElse(0).forGetter((instance) -> instance.randomHeight),
    Codec.BOOL.fieldOf("has_vines").orElse(false).forGetter((instance) -> instance.hasVines)
  ).apply(treeConfig, BaseSlimeTreeFeatureConfig::new));

  public final BlockStateProvider trunkProvider;
  public final BlockStateProvider leavesProvider;
  public final BlockStateProvider vinesProvider;
  public transient boolean forcePlacement;
  public final int baseHeight;
  public final int randomHeight;
  public final boolean hasVines;

  public BaseSlimeTreeFeatureConfig(BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, BlockStateProvider vinesProvider, int baseHeight, int randomHeight, boolean hasVines) {
    this.trunkProvider = trunkProvider;
    this.leavesProvider = leavesProvider;
    this.vinesProvider = vinesProvider;
    this.baseHeight = baseHeight;
    this.randomHeight = randomHeight;
    this.hasVines = hasVines;
  }

  public void forcePlacement() {
    this.forcePlacement = true;
  }

  public static class Builder {

    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider leavesProvider;
    public final BlockStateProvider vinesProvider;
    public final int baseHeight;
    public final int randomHeight;
    public final boolean hasVines;

    public Builder(BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, BlockStateProvider vinesProvider, int baseHeight, int randomHeight, boolean hasVines) {
      this.trunkProvider = trunkProvider;
      this.leavesProvider = leavesProvider;
      this.vinesProvider = vinesProvider;
      this.baseHeight = baseHeight;
      this.randomHeight = randomHeight;
      this.hasVines = hasVines;
    }

    public BaseSlimeTreeFeatureConfig build() {
      return new BaseSlimeTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.vinesProvider, this.baseHeight, this.randomHeight, this.hasVines);
    }
  }
}
