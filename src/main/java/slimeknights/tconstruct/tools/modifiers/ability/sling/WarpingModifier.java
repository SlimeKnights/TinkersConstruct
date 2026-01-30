package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.interaction.sling.SlingTeleportModule;

/** @deprecated use {@link SlingTeleportModule} */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public class WarpingModifier extends SlingModifier {
  private static final SlingTeleportModule WARPING = new SlingTeleportModule(6, 1.5f, LivingEntityPredicate.ANY, ModifierCondition.ANY_TOOL);
  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    WARPING.beforeReleaseUsing(tool, modifier, entity, useDuration, timeLeft, activeModifier);
  }
}
