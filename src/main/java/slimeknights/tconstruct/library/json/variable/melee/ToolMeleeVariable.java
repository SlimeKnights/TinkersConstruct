package slimeknights.tconstruct.library.json.variable.melee;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.NestedLoader;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable which fetches tool properties for the melee damage hook */
public record ToolMeleeVariable(ToolVariable tool) implements MeleeVariable {
  public static final IGenericLoader<ToolMeleeVariable> LOADER = new NestedLoader<>("tool_type", ToolVariable.LOADER, ToolMeleeVariable::new, ToolMeleeVariable::tool);

  @Override
  public float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker) {
    return this.tool.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends ToolMeleeVariable> getLoader() {
    return LOADER;
  }
}
