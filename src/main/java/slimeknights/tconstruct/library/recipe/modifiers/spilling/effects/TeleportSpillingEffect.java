package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.events.teleport.SpillingTeleportEvent;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;

/** Effect that teleports the entity */
public class TeleportSpillingEffect implements ISpillingEffect {
  public static final TeleportSpillingEffect INSTANCE = new TeleportSpillingEffect();
  public static final ISpillingEffectLoader.Singleton<TeleportSpillingEffect> LOADER = new ISpillingEffectLoader.Singleton<>(INSTANCE);
  private static final ITeleportEventFactory TELEPORT_PREDICATE = SpillingTeleportEvent::new;

  private TeleportSpillingEffect() {}

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      TeleportHelper.randomNearbyTeleport(target, TELEPORT_PREDICATE);
    }
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }
}
