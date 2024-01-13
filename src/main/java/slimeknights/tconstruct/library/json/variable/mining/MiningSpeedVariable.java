package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable used for {@link slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule} */
public interface MiningSpeedVariable extends IHaveLoader<MiningSpeedVariable> {
  GenericLoaderRegistry<MiningSpeedVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

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
    public static final ConstantLoader<Constant> LOADER = new ConstantLoader<>(Constant::new);

    @Override
    public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
      return value;
    }

    @Override
    public IGenericLoader<? extends MiningSpeedVariable> getLoader() {
      return LOADER;
    }
  }
}
