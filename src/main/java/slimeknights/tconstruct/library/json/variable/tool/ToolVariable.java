package slimeknights.tconstruct.library.json.variable.tool;

import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Variable that fetches a value from a tool instance */
public interface ToolVariable extends IHaveLoader<ToolVariable> {
  GenericLoaderRegistry<ToolVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

  /** Gets a value from the given block state */
  float getValue(IToolStackView tool);


  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, ToolVariable {
    public static final ConstantLoader<Constant> LOADER = new ConstantLoader<>(Constant::new);

    @Override
    public float getValue(IToolStackView tool) {
      return value;
    }

    @Override
    public IGenericLoader<? extends ToolVariable> getLoader() {
      return LOADER;
    }
  }
}
