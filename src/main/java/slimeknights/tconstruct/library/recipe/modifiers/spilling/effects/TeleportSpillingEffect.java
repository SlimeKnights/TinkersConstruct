package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.events.teleport.SpillingTeleportEvent;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;

/** Effect that teleports the entity */
public class TeleportSpillingEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();
  public static final TeleportSpillingEffect INSTANCE = new TeleportSpillingEffect();
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

  private static class Loader implements ISpillingEffectLoader<TeleportSpillingEffect> {
    @Override
    public TeleportSpillingEffect deserialize(JsonObject json) {
      return INSTANCE;
    }

    @Override
    public TeleportSpillingEffect read(PacketBuffer buffer) {
      return INSTANCE;
    }
  }
}
