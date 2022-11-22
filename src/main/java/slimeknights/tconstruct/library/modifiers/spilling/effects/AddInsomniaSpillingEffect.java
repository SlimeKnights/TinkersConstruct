package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

/**
 * Effect to make the player either more or less likely to spawn phantoms
 */
public record AddInsomniaSpillingEffect(int amount) implements ISpillingEffect {
  /** ID of this effect */
  public static final ResourceLocation ID = TConstruct.getResource("add_insomnia");

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    if (context.getLivingTarget() instanceof Player player) {
      player.awardStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST), amount);
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("amount", amount);
    return json;
  }

  /** Loader instance */
  public static final JsonDeserializer<AddInsomniaSpillingEffect> LOADER = (element, type, context) ->
    new AddInsomniaSpillingEffect(GsonHelper.getAsInt(element.getAsJsonObject(), "amount"));
}
