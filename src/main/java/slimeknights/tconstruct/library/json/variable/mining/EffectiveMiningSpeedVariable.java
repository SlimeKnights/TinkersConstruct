package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Optional;

/** Variable providing a different value based on whether the modifier is effective or not */
public record EffectiveMiningSpeedVariable(MiningSpeedVariable ifTrue, MiningSpeedVariable ifFalse) implements MiningSpeedVariable {
  public static final RecordLoadable<EffectiveMiningSpeedVariable> LOADER = RecordLoadable.create(
    MiningSpeedVariable.LOADER.requiredField("if_true", EffectiveMiningSpeedVariable::ifTrue),
    MiningSpeedVariable.LOADER.requiredField("if_false", EffectiveMiningSpeedVariable::ifFalse),
    EffectiveMiningSpeedVariable::new);

  public EffectiveMiningSpeedVariable(float ifTrue, float ifFalse) {
    this(new MiningSpeedVariable.Constant(ifTrue), new MiningSpeedVariable.Constant(ifFalse));
  }

  @Override
  public RecordLoadable<? extends IHaveLoader> getLoader() {
    return LOADER;
  }

  @SuppressWarnings("deprecation")
  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    MiningSpeedVariable variable = ifTrue;
    if (event != null && player != null) {
      Optional<BlockPos> pos = event.getPosition();
      if (pos.isEmpty() || !IsEffectiveToolHook.isEffective(tool, player.level().getBlockState(pos.get()))) {
        variable = ifFalse;
      }
    }
    return variable.getValue(tool, event, player, sideHit);
  }

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeedContext context, @Nullable Player player) {
    return (context != null && context.isEffective() ? ifTrue : ifFalse).getValue(tool, context, player);
  }
}
