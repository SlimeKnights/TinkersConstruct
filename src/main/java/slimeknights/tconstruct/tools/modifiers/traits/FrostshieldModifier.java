package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.tags.DamageTypeTags;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.capacity.CapacityBarModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.DamageToCapacityModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.DurabilityShieldModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** @deprecated use {@link CapacityBarModule}, {@link DurabilityShieldModule}, and {@link DamageToCapacityModule} */
@Deprecated(forRemoval = true)
public class FrostshieldModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new CapacityBarModule(LevelingInt.eachLevel(100), ToolStats.DURABILITY));
    hookBuilder.addModule(new DurabilityShieldModule(0xAAFFFF));
    hookBuilder.addModule(DamageToCapacityModule.source(DamageSourcePredicate.tag(DamageTypeTags.IS_FREEZING)).reduceDamage().flat(1));
  }


  /* Shield */

  @Override
  public int getPriority() {
    // higher than overslime, to ensure this is removed first
    return 175;
  }
}
