package slimeknights.tconstruct.tools.modifiers.ability.fluid;

import net.minecraftforge.fluids.FluidType;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.tools.modules.combat.SpillingModule;

/** @deprecated use {@link SpillingModule}, {@link ToolTankHelper#CAPACITY_STAT}, and {@link ToolTankHelper#TANK_HANDLER} */
@Deprecated(forRemoval = true)
public class SpillingModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    hookBuilder.addModule(new SpillingModule(LevelingValue.eachLevel(1), ModifierCondition.ANY_TOOL));
  }
}
