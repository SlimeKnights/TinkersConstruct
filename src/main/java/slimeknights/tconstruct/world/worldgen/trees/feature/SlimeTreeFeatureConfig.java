package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

import java.util.List;

public class SlimeTreeFeatureConfig extends BaseTreeFeatureConfig {

  public final BlockStateProvider vineProvider;
  public final int randomTreeHeight;
  public final boolean hasVines;

  protected SlimeTreeFeatureConfig(BlockStateProvider trunkProviderIn, BlockStateProvider leavesProviderIn, BlockStateProvider vineProviderIn, List<TreeDecorator> decoratorsIn, int baseHeightIn, int randomTreeHeightIn, boolean hasVinesIn) {
    super(trunkProviderIn, leavesProviderIn, decoratorsIn, baseHeightIn);
    this.vineProvider = vineProviderIn;
    this.randomTreeHeight = randomTreeHeightIn;
    this.hasVines = hasVinesIn;
  }

  @Override
  public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
    ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
    builder.put(ops.createString("vine_provider"), this.vineProvider.serialize(ops)).put(ops.createString("random_height"), ops.createInt(this.randomTreeHeight)).put(ops.createString("has_vines"), ops.createBoolean(this.hasVines));
    Dynamic<T> dynamic = new Dynamic<>(ops, ops.createMap(builder.build()));
    return dynamic.merge(super.serialize(ops));
  }

  @Override
  protected SlimeTreeFeatureConfig setSapling(net.minecraftforge.common.IPlantable value) {
    super.setSapling(value);
    return this;
  }

  public static <T> SlimeTreeFeatureConfig deserialize(Dynamic<T> dynamic) {
    BaseTreeFeatureConfig basetreefeatureconfig = BaseTreeFeatureConfig.deserialize(dynamic);
    BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCK_STATE_PROVIDER_TYPE.getOrDefault(new ResourceLocation(dynamic.get("vine_provider").get("type").asString().orElseThrow(RuntimeException::new)));
    return new SlimeTreeFeatureConfig(basetreefeatureconfig.trunkProvider, basetreefeatureconfig.leavesProvider, blockstateprovidertype.func_227399_a_(dynamic.get("vine_provider").orElseEmptyMap()), basetreefeatureconfig.decorators, basetreefeatureconfig.baseHeight, dynamic.get("random_height").asInt(0), dynamic.get("has_vines").asBoolean(false));
  }

  public static class Builder extends BaseTreeFeatureConfig.Builder {

    public final BlockStateProvider vineProvider;
    private List<TreeDecorator> decorators = ImmutableList.of();
    private int baseHeight;
    private int randomHeight;
    private boolean hasVines;

    public Builder(BlockStateProvider trunkProviderIn, BlockStateProvider leavesProviderIn, BlockStateProvider vineProviderIn) {
      super(trunkProviderIn, leavesProviderIn);
      this.vineProvider = vineProviderIn;
    }

    public SlimeTreeFeatureConfig.Builder decorators(List<TreeDecorator> decorators) {
      this.decorators = decorators;
      return this;
    }

    @Override
    public SlimeTreeFeatureConfig.Builder baseHeight(int baseHeightIn) {
      this.baseHeight = baseHeightIn;
      return this;
    }

    public SlimeTreeFeatureConfig.Builder randomHeight(int randomHeightIn) {
      this.randomHeight = randomHeightIn;
      return this;
    }

    public SlimeTreeFeatureConfig.Builder hasVines(boolean hasVinesIn) {
      this.hasVines = hasVinesIn;
      return this;
    }

    @Override
    public SlimeTreeFeatureConfig.Builder setSapling(net.minecraftforge.common.IPlantable value) {
      super.setSapling(value);
      return this;
    }

    @Override
    public SlimeTreeFeatureConfig build() {
      return new SlimeTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.vineProvider, this.decorators, this.baseHeight, this.randomHeight, this.hasVines).setSapling(this.sapling);
    }
  }
}
