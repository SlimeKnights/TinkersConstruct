package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

/**
 * Effect to set an entity freezing
 */
public record SetFreezeSpillingEffect(int time) implements ISpillingEffect {
  public static final ResourceLocation ID = TConstruct.getResource("set_freeze");

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    Entity target = context.getTarget();
    if (target.canFreeze()) {
      target.setTicksFrozen(Math.max(target.getTicksRequiredToFreeze(), target.getTicksFrozen()) + time);
      target.setRemainingFireTicks(0);
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("time", time);
    return json;
  }

  /** Loader for this effect */
  public static final JsonDeserializer<SetFreezeSpillingEffect> LOADER = (element, type, context) ->
    new SetFreezeSpillingEffect(GsonHelper.getAsInt(element.getAsJsonObject(), "time"));
}
