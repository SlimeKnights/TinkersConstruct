package slimeknights.tconstruct.world.worldgen.trees;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import slimeknights.tconstruct.world.TinkerStructures;

import java.util.Random;
import java.util.function.Supplier;

public class SupplierBlockStateProvider extends BlockStateProvider {

  public static final Codec<SupplierBlockStateProvider> CODEC =
    BlockState.CODEC.fieldOf("state")
      .xmap(SupplierBlockStateProvider::new, (provider) -> provider.supplier.get()).codec();

  private final Supplier<BlockState> supplier;

  private SupplierBlockStateProvider(BlockState state) {
    this(() -> state);
  }

  public SupplierBlockStateProvider(Supplier<BlockState> blockStateSupplier) {
    this.supplier = blockStateSupplier;
  }

  @Override
  protected BlockStateProviderType<?> getType() {
    return TinkerStructures.supplierBlockstateProvider.get();
  }

  @Override
  public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
    return this.supplier.get();
  }
}
