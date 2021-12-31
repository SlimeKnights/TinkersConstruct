package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.events.teleport.SpillingTeleportEvent;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;

/** Effect that teleports the entity */
public class TeleportSpillingEffect implements ISpillingEffect {
  public static final TeleportSpillingEffect INSTANCE = new TeleportSpillingEffect();
  public static final IGenericLoader<TeleportSpillingEffect> LOADER = new SingletonLoader<>(INSTANCE);
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
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }
}
