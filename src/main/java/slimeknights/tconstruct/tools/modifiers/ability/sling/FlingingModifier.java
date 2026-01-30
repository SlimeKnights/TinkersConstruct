package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.interaction.sling.SlingLeapModule;

/** @deprecated use {@link SlingLeapModule} */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public class FlingingModifier extends SlingModifier {
  private static final SlingLeapModule FLINGING = new SlingLeapModule(4, false, 1.5f, 3, false, LivingEntityPredicate.and(LivingEntityPredicate.ON_GROUND, TinkerPredicate.TARGETING_BLOCK), ModifierCondition.ANY_TOOL);

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    FLINGING.beforeReleaseUsing(tool, modifier, entity, useDuration, timeLeft, activeModifier);
  }
}
