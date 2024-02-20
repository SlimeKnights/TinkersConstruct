package slimeknights.tconstruct.library.json.variable.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.StringLoader;

/** Fetches a value of an integer property */
public record StatePropertyVariable(String name) implements BlockVariable {
  public static final IGenericLoader<StatePropertyVariable> LOADER = new StringLoader<>("name", StatePropertyVariable::new, StatePropertyVariable::name);

  @Override
  public float getValue(BlockState state) {
    Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
    if (property != null && Number.class.isAssignableFrom(property.getValueClass())) {
      return ((Number) state.getValue(property)).floatValue();
    }
    return 0;
  }

  @Override
  public IGenericLoader<? extends BlockVariable> getLoader() {
    return LOADER;
  }
}
