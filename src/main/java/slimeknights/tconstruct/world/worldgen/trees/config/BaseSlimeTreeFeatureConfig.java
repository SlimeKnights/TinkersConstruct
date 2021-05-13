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

public class BaseSlimeTreeFeatureConfig implements IFeatureConfig {

  public static final Codec<BaseSlimeTreeFeatureConfig> CODEC = RecordCodecBuilder.create((treeConfig) -> treeConfig.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((object) -> object.trunkProvider),
    BlockStateProvider.CODEC.fieldOf("leaves_provider").forGetter((instance) -> instance.leavesProvider),
    BlockStateProvider.CODEC.fieldOf("vines_provider").forGetter((instance) -> instance.vinesProvider),
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

  @NoArgsConstructor
  public static class Builder {
    private static final SupplierBlockStateProvider AIR_PROVIDER = new SupplierBlockStateProvider(Blocks.AIR::getDefaultState);

    @Setter @Accessors(fluent = true)
    private BlockStateProvider trunkProvider = AIR_PROVIDER;
    @Setter @Accessors(fluent = true)
    private BlockStateProvider leavesProvider = AIR_PROVIDER;
    private BlockStateProvider vinesProvider = AIR_PROVIDER;
    @Setter @Accessors(fluent = true)
    private int baseHeight = 5;
    @Setter @Accessors(fluent = true)
    private int randomHeight = 4;
    private boolean hasVines = false;

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

    /** Builds the config */
    public BaseSlimeTreeFeatureConfig build() {
      return new BaseSlimeTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.vinesProvider, this.baseHeight, this.randomHeight, this.hasVines);
    }
  }
}
