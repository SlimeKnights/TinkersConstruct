package slimeknights.tconstruct.world.worldgen;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

import java.util.Random;
import java.util.function.Supplier;

public class SupplierBlockStateProvider extends BlockStateProvider {

  private final Supplier<BlockState> blockStateSupplier;

  public SupplierBlockStateProvider(Supplier<BlockState> blockStateSupplier) {
    super(BlockStateProviderType.field_227394_a_);
    this.blockStateSupplier = blockStateSupplier;
  }

  public <T> SupplierBlockStateProvider(Dynamic<T> dynamic) {
    this(() -> BlockState.deserialize(dynamic.get("state").orElseEmptyMap()));
  }

  @Override
  public BlockState func_225574_a_(Random random, BlockPos blockPos) {
    return this.blockStateSupplier.get();
  }

  @Override
  public <T> T serialize(DynamicOps<T> dynamicOps) {
    ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
    builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getKey(this.field_227393_a_).toString())).put(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.blockStateSupplier.get()).getValue());
    return (new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()))).getValue();
  }
}
