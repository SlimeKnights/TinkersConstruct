package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.events.teleport.SpillingTeleportEvent;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.library.utils.TeleportHelper.ITeleportEventFactory;

/** Effect that teleports the entity */
public class TeleportSpillingEffect implements ISpillingEffect {
  public static final ResourceLocation ID = TConstruct.getResource("teleport");
  public static final TeleportSpillingEffect INSTANCE = new TeleportSpillingEffect();
  public static final JsonDeserializer<TeleportSpillingEffect> LOADER = (json, type, context) -> INSTANCE;
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
  public JsonObject serialize(JsonSerializationContext context) {
    return JsonUtils.withType(ID);
  }
}
