package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.NestedLoader;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable which fetches tool properties for the mining speed hook */
public record ToolMiningSpeedVariable(ToolVariable tool) implements MiningSpeedVariable {
  public static final IGenericLoader<ToolMiningSpeedVariable> LOADER = new NestedLoader<>("tool_type", ToolVariable.LOADER, ToolMiningSpeedVariable::new, ToolMiningSpeedVariable::tool);

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    return this.tool.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends MiningSpeedVariable> getLoader() {
    return LOADER;
  }
}
