package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.damagesource.DamageTypes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.ThornsModule;

/** @deprecated use {@link ThornsModule} */
@Deprecated(forRemoval = true)
public class ThornsModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ThornsModule.type(DamageTypes.THORNS).constantFlat(1).randomFlat(3).build());
  }
}
