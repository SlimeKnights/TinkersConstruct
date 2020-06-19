package slimeknights.tconstruct.world.worldgen.trees.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import slimeknights.tconstruct.world.TinkerStructures;

import java.util.Random;
import java.util.function.Supplier;

public class SupplierBlockStateProvider extends BlockStateProvider {

  private final Supplier<BlockState> blockStateSupplier;

  public SupplierBlockStateProvider(Supplier<BlockState> blockStateSupplier) {
    super(TinkerStructures.supplierBlockstateProvider.get());
    this.blockStateSupplier = blockStateSupplier;
  }

  public <T> SupplierBlockStateProvider(Dynamic<T> dynamic) {
    this(() -> BlockState.deserialize(dynamic.get("state").orElseEmptyMap()));
  }

  @Override
  public BlockState getBlockState(Random random, BlockPos blockPos) {
    return this.blockStateSupplier.get();
  }

  @Override
  public <T> T serialize(DynamicOps<T> dynamicOps) {
    ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
    builder.put(dynamicOps.createString("type"), dynamicOps.createString(this.blockStateProvider.getRegistryName().toString())).put(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.blockStateSupplier.get()).getValue());
    return (new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()))).getValue();
  }
}
