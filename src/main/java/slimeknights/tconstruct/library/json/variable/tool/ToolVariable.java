package slimeknights.tconstruct.library.json.variable.tool;

import net.minecraft.util.ToFloatFunction;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/** Variable that fetches a value from a tool instance */
public interface ToolVariable extends IHaveLoader<ToolVariable> {
  GenericLoaderRegistry<ToolVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

  /** Gets a value from the given block state */
  float getValue(IToolStackView tool);


  /* Singletons */

  /** Creates a new singleton variable getter */
  private static ToolVariable simple(ToFloatFunction<IToolStackView> getter) {
    return singleton(loader -> new ToolVariable() {
      @Override
      public float getValue(IToolStackView tool) {
        return getter.apply(tool);
      }

      @Override
      public IGenericLoader<? extends ToolVariable> getLoader() {
        return loader;
      }
    });
  }

  /** Current durability of the tool */
  ToolVariable CURRENT_DURABILITY = simple(IToolStackView::getCurrentDurability);

  
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
