package slimeknights.tconstruct.library.json.variable.stat;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.NestedLoader;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable which fetches tool properties for the conditional stat hook */
public record ToolConditionalStatVariable(ToolVariable tool) implements ConditionalStatVariable {
  public static final IGenericLoader<ToolConditionalStatVariable> LOADER = new NestedLoader<>("tool_type", ToolVariable.LOADER, ToolConditionalStatVariable::new, ToolConditionalStatVariable::tool);

  @Override
  public float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
    return this.tool.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends ToolConditionalStatVariable> getLoader() {
    return LOADER;
  }
}
