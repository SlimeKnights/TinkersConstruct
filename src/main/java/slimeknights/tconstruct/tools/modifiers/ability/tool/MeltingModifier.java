package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraftforge.fluids.FluidType;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.tools.modules.MeltingModule;

/** @deprecated use {@link MeltingModule}, {@link ToolTankHelper#TANK_HANDLER}, and {@link ToolTankHelper#CAPACITY_STAT} */
@Deprecated(forRemoval = true)
public class MeltingModifier extends NoLevelsModifier {
  @SuppressWarnings("removal")
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    hookBuilder.addModule(new MeltingModule(LevelingInt.flat(1000), LevelingInt.flat(9), LevelingInt.flat(4), ModifierCondition.ANY_TOOL, Config.COMMON.melterOreRate));
  }
}
