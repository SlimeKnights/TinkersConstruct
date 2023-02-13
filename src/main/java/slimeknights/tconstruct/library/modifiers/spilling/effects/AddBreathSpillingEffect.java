package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

/**
 * Effect to increase or decrease the players breath
 */
public record AddBreathSpillingEffect(int amount) implements ISpillingEffect {
  /** ID of this effect */
  public static final ResourceLocation ID = TConstruct.getResource("add_breath");

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      target.setAirSupply(Mth.clamp(target.getAirSupply() + amount, 0, target.getMaxAirSupply()));
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("amount", amount);
    return json;
  }

  /** Loader instance */
  public static final JsonDeserializer<AddBreathSpillingEffect> LOADER = (element, type, context) ->
    new AddBreathSpillingEffect(GsonHelper.getAsInt(element.getAsJsonObject(), "amount"));
}
