package slimeknights.tconstruct.library.json.variable.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.ToFloatFunction;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;
import slimeknights.tconstruct.library.json.variable.melee.MeleeVariable;
import slimeknights.tconstruct.library.json.variable.stat.ConditionalStatVariable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

import static slimeknights.mantle.data.registry.GenericLoaderRegistry.SingletonLoader.singleton;

/**
 * Variable that fetches a value from a tool instance.
 * All tool variables automatically work as melee, mining speed, and conditional stat variables due to the superset parameter space.
 */
public interface ToolVariable extends IHaveLoader, MeleeVariable, ConditionalStatVariable {
  GenericLoaderRegistry<ToolVariable> LOADER = new VariableLoaderRegistry<>("Tool Variable", ToolVariable.Constant.LOADER.constructor());

  /** Gets a value from the given tool */
  float getValue(IToolStackView tool);

  @Override
  IGenericLoader<? extends ToolVariable> getLoader();


  /* delegating methods, all tool variables are automatically the other types */

  @Override
  default float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
    return getValue(tool);
  }

  @Override
  default float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker) {
    return getValue(tool);
  }


  /* Singletons */

  /** Creates a new singleton variable getter */
  static ToolVariable simple(ToFloatFunction<IToolStackView> getter) {
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


  /** Registers a variable with tools, melee, conditional stat, and mining speed */
  static void register(ResourceLocation name, IGenericLoader<? extends ToolVariable> loader) {
    LOADER.register(name, loader);
    MeleeVariable.LOADER.register(name, loader);
    ConditionalStatVariable.register(name, loader);
  }

  
  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, ToolVariable {
    public static final ConstantLoader<ToolVariable.Constant> LOADER = new ConstantLoader<>(ToolVariable.Constant::new);

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
