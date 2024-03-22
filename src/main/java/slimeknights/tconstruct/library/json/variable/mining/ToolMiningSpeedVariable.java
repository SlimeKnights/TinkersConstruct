package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable which fetches tool properties for the mining speed hook */
public record ToolMiningSpeedVariable(ToolVariable tool) implements MiningSpeedVariable {
  public static final RecordLoadable<ToolMiningSpeedVariable> LOADER = RecordLoadable.create(ToolVariable.LOADER.directField("tool_type", ToolMiningSpeedVariable::tool), ToolMiningSpeedVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    return this.tool.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends MiningSpeedVariable> getLoader() {
    return LOADER;
  }
}
