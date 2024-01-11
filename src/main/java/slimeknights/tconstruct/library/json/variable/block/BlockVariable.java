package slimeknights.tconstruct.library.json.variable.block;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;

/** Variable that fetches a property from a block state */
public interface BlockVariable extends IHaveLoader<BlockVariable> {
  GenericLoaderRegistry<BlockVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

  /** Gets a value from the given block state */
  float getValue(BlockState state);
  

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
