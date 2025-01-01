package slimeknights.tconstruct.world.worldgen.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class SlimeTreeConfig implements FeatureConfiguration {

  public static final Codec<SlimeTreeConfig> CODEC = RecordCodecBuilder.create((treeConfig) -> treeConfig.group(
    BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(instance -> instance.trunkProvider),
    BlockStateProvider.CODEC.fieldOf("leaves_provider").forGetter(instance -> instance.leavesProvider),
    BlockStateProvider.CODEC.fieldOf("vines_provider").forGetter(instance -> instance.vinesProvider),
    Codec.INT.fieldOf("base_height").orElse(0).forGetter(instance -> instance.baseHeight),
    Codec.INT.fieldOf("random_height").orElse(0).forGetter(instance -> instance.randomHeight),
    Codec.BOOL.fieldOf("can_double_height").orElse(false).forGetter(instance -> instance.canDoubleHeight),
    Codec.BOOL.fieldOf("has_vines").orElse(false).forGetter(instance -> instance.hasVines),
    Codec.BOOL.fieldOf("planted").orElse(false).forGetter(instance -> instance.planted)
  ).apply(treeConfig, SlimeTreeConfig::new));

  public final BlockStateProvider trunkProvider;
  public final BlockStateProvider leavesProvider;
  public final BlockStateProvider vinesProvider;
  public final int baseHeight;
  public final int randomHeight;
  public final boolean canDoubleHeight;
  public final boolean hasVines;
  public final boolean planted;

  public SlimeTreeConfig(BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, BlockStateProvider vinesProvider, int baseHeight, int randomHeight, boolean canDoubleHeight, boolean hasVines, boolean planted) {
    this.trunkProvider = trunkProvider;
    this.leavesProvider = leavesProvider;
    this.vinesProvider = vinesProvider;
    this.baseHeight = baseHeight;
    this.randomHeight = randomHeight;
    this.canDoubleHeight = canDoubleHeight;
    this.hasVines = hasVines;
    this.planted = planted;
  }

  @NoArgsConstructor
  @Accessors(fluent = true)
  public static class Builder {
    private static final BlockStateProvider AIR_PROVIDER = BlockStateProvider.simple(Blocks.AIR);

    @Setter
    private BlockStateProvider trunkProvider = AIR_PROVIDER;
    @Setter
    private BlockStateProvider leavesProvider = AIR_PROVIDER;
    private BlockStateProvider vinesProvider = AIR_PROVIDER;
    @Setter
    private int baseHeight = 5;
    @Setter
    private int randomHeight = 4;
    private boolean canDoubleHeight = false;
    private boolean hasVines = false;
    private boolean planted = false;

    /** Sets the tree as a planted tree */
    public Builder canDoubleHeight() {
      this.canDoubleHeight = true;
      return this;
    }

    /** Sets the trunk */
    public Builder trunk(Block block) {
      return trunkProvider(BlockStateProvider.simple(block));
    }

    /** Sets the leaves */
    public Builder leaves(Block block) {
      return leavesProvider(BlockStateProvider.simple(block));
    }

    /** Sets the vines */
    public Builder vinesProvider(BlockStateProvider supplier) {
      this.vinesProvider = supplier;
      this.hasVines = true;
      return this;
    }

    /** Sets the vines */
    public Builder vines(BlockState block) {
      return vinesProvider(BlockStateProvider.simple(block));
    }

    /** Sets the tree as a planted tree */
    public Builder planted() {
      this.planted = true;
      return this;
    }

    /** Builds the config */
    public SlimeTreeConfig build() {
      return new SlimeTreeConfig(this.trunkProvider, this.leavesProvider, this.vinesProvider, this.baseHeight, this.randomHeight, this.canDoubleHeight, this.hasVines, this.planted);
    }
  }
}
