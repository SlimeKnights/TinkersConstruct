package slimeknights.tconstruct.library.json.variable.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

/** Fetches a value of an integer property */
public record StatePropertyVariable(String name) implements BlockVariable {
  public static final RecordLoadable<StatePropertyVariable> LOADER = RecordLoadable.create(StringLoadable.DEFAULT.requiredField("name", StatePropertyVariable::name), StatePropertyVariable::new);

  @Override
  public float getValue(BlockState state) {
    Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
    if (property != null && Number.class.isAssignableFrom(property.getValueClass())) {
      return ((Number) state.getValue(property)).floatValue();
    }
    return 0;
  }

  @Override
  public RecordLoadable<StatePropertyVariable> getLoader() {
    return LOADER;
  }
}
