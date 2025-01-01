package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable used for {@link slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule} */
public interface MiningSpeedVariable extends IHaveLoader {
  GenericLoaderRegistry<MiningSpeedVariable> LOADER = new VariableLoaderRegistry<>("Mining Speed Variable", Constant::new);

  /**
   * Gets the value of this variable
   * @param tool      Tool instance
   * @param event     Break speed event, may be null on tooltips
   * @param player    Player instance, may be defined when event is null, but still may be null on tooltips
   * @param sideHit   Block side hit, may be null on tooltips
   * @return  Value of this variable, using a fallback if appropiate
   */
  float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit);

  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, MiningSpeedVariable {
    public static final RecordLoadable<Constant> LOADER = VariableLoaderRegistry.constantLoader(Constant::new);

    @Override
    public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
      return value;
    }

    @Override
    public RecordLoadable<Constant> getLoader() {
      return LOADER;
    }
  }
}
