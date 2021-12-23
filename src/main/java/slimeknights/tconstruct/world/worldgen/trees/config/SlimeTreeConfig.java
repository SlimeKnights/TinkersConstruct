package slimeknights.tconstruct.world.worldgen.trees.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.IFeatureConfig;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;

import java.util.function.Supplier;

public class SlimeTreeConfig implements IFeatureConfig {

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
    private static final SupplierBlockStateProvider AIR_PROVIDER = new SupplierBlockStateProvider(Blocks.AIR::getDefaultState);

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
    public Builder trunk(Supplier<BlockState> supplier) {
      return trunkProvider(new SupplierBlockStateProvider(supplier));
    }

    /** Sets the leaves */
    public Builder leaves(Supplier<BlockState> supplier) {
      return leavesProvider(new SupplierBlockStateProvider(supplier));
    }

    /** Sets the vines */
    public Builder vinesProvider(SupplierBlockStateProvider supplier) {
      this.vinesProvider = supplier;
      this.hasVines = true;
      return this;
    }

    /** Sets the vines */
    public Builder vines(Supplier<BlockState> supplier) {
      return vinesProvider(new SupplierBlockStateProvider(supplier));
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
