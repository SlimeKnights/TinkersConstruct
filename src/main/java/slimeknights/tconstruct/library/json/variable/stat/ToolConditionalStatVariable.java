package slimeknights.tconstruct.library.json.variable.stat;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable which fetches tool properties for the conditional stat hook */
public record ToolConditionalStatVariable(ToolVariable tool) implements ConditionalStatVariable {
  public static final RecordLoadable<ToolConditionalStatVariable> LOADER = RecordLoadable.create(ToolVariable.LOADER.directField("tool_type", ToolConditionalStatVariable::tool), ToolConditionalStatVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
    return this.tool.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends ToolConditionalStatVariable> getLoader() {
    return LOADER;
  }
}
