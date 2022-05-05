package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

/** Effect which extinguishes fire on an entity */
public class ExtinguishSpillingEffect implements ISpillingEffect {
  public static final ExtinguishSpillingEffect INSTANCE = new ExtinguishSpillingEffect();
  public static final ResourceLocation ID = TConstruct.getResource("extinguish");
  public static final JsonDeserializer<ExtinguishSpillingEffect> LOADER = (json, type, context) -> INSTANCE;
  private ExtinguishSpillingEffect() {}

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    context.getTarget().clearFire();
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    return JsonUtils.withType(ID);
  }
}
