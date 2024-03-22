package slimeknights.tconstruct.library.json.variable.melee;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable which fetches tool properties for the melee damage hook */
public record ToolMeleeVariable(ToolVariable tool) implements MeleeVariable {
  public static final RecordLoadable<ToolMeleeVariable> LOADER = RecordLoadable.create(ToolVariable.LOADER.directField("tool_type", ToolMeleeVariable::tool), ToolMeleeVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker) {
    return this.tool.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends ToolMeleeVariable> getLoader() {
    return LOADER;
  }
}
