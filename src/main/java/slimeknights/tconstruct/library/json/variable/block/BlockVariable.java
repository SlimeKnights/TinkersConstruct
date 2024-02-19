package slimeknights.tconstruct.library.json.variable.block;

import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/** Variable that fetches a property from a block state */
public interface BlockVariable extends IHaveLoader<BlockVariable> {
  GenericLoaderRegistry<BlockVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

  /** Gets a value from the given block state */
  float getValue(BlockState state);


  /* Singletons */

  /** Creates a new singleton variable getter */
  private static BlockVariable simple(ToFloatFunction<BlockState> getter) {
    return singleton(loader -> new BlockVariable() {
      @Override
      public float getValue(BlockState state) {
        return getter.apply(state);
      }

      @Override
      public IGenericLoader<? extends BlockVariable> getLoader() {
        return loader;
      }
    });
  }

  /** Gets the block's blast resistance */
  BlockVariable BLAST_RESISTANCE = simple(state -> state.getBlock().getExplosionResistance());
  /** Gets the block's hardness */
  BlockVariable HARDNESS = simple(state -> state.getBlock().defaultDestroyTime());


  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, BlockVariable {
    public static final ConstantLoader<Constant> LOADER = new ConstantLoader<>(Constant::new);

    @Override
    public float getValue(BlockState state) {
      return value;
    }

    @Override
    public IGenericLoader<? extends BlockVariable> getLoader() {
      return LOADER;
    }
  }
}
