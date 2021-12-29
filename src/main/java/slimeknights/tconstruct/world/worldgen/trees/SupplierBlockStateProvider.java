package slimeknights.tconstruct.world.worldgen.trees;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
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
  protected BlockStateProviderType<?> type() {
    return TinkerStructures.supplierBlockstateProvider.get();
  }

  @Override
  public BlockState getState(Random randomIn, BlockPos blockPosIn) {
    return this.supplier.get();
  }
}
