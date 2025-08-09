package slimeknights.tconstruct.tools.modifiers.traits.ranged;

import net.minecraft.world.entity.MobType;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;

/** @deprecated use {@link ConditionalPowerModule} */
@Deprecated(forRemoval = true)
public class HolyModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ConditionalPowerModule.builder().target(new MobTypePredicate(MobType.UNDEAD)).eachLevel(0.75f));
  }
}
