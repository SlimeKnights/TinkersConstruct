package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.Objects;

/**
 * Spilling effect to apply a potion effect
 * @param effect  Effect to apply
 * @param time    Potion time in seconds
 * @param level   Potion level starting at 1
 */
public record EffectSpillingEffect(MobEffect effect, int time, int level) implements ISpillingEffect {
  public static final ResourceLocation ID = TConstruct.getResource("effect");

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      int time = (int)(this.time * 20 * scale);
      if (time > 0) {
        target.addEffect(new MobEffectInstance(effect, time, level - 1));
      }
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("name", Objects.requireNonNull(effect.getRegistryName()).toString());
    json.addProperty("time", time);
    json.addProperty("level", level);
    return json;
  }

  public static final JsonDeserializer<EffectSpillingEffect> LOADER = (element, type, context) -> {
    JsonObject json = element.getAsJsonObject();
    ResourceLocation id = JsonHelper.getResourceLocation(json, "name");
    if (!ForgeRegistries.MOB_EFFECTS.containsKey(id)) {
      throw new JsonSyntaxException("Unknown effect " + id);
    }
    MobEffect effect = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getValue(id));
    int time = GsonHelper.getAsInt(json, "time");
    int level = GsonHelper.getAsInt(json, "level", 1);
    return new EffectSpillingEffect(effect, time, level);
  };
}
