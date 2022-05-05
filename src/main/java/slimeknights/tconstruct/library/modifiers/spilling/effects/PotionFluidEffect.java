package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.recipe.TagPredicate;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

/** Spilling effect that pulls the potion from a NBT potion fluid and applies it */
public record PotionFluidEffect(float effectScale, TagPredicate predicate) implements ISpillingEffect {
  public static final ResourceLocation ID = TConstruct.getResource("potion_fluid");

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    LivingEntity attacker = context.getAttacker();
    // must match the tag predicate
    if (target != null && predicate.test(fluid.getTag())) {
      Potion potion = PotionUtils.getPotion(fluid.getTag());
      if (potion != Potions.EMPTY) {
        // prevent effects like instant damage from hitting hurt resistance
        int oldInvulnerableTime = target.invulnerableTime;
        float totalScale = scale * effectScale;
        for (MobEffectInstance instance : potion.getEffects()) {
          MobEffect effect = instance.getEffect();
          if (effect.isInstantenous()) {
            target.invulnerableTime = 0;
            effect.applyInstantenousEffect(attacker, attacker, target, instance.getAmplifier(), totalScale);
          } else {
            int duration = (int)(instance.getDuration() * totalScale);
            if (duration > 10) {
              target.addEffect(new MobEffectInstance(effect, duration, instance.getAmplifier(), instance.isAmbient(), instance.isVisible(), instance.showIcon()));
            }
          }
        }
        target.invulnerableTime = oldInvulnerableTime;
      }
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("scale", effectScale);
    if (predicate != TagPredicate.ANY) {
      json.add("predicate", predicate.serialize());
    }
    return json;
  }

  public static final JsonDeserializer<PotionFluidEffect> LOADER = (element, type, context) -> {
    JsonObject json = element.getAsJsonObject();
    float scale = GsonHelper.getAsFloat(json, "scale");
    TagPredicate predicate = TagPredicate.ANY;
    if (json.has("predicate")) {
      predicate = TagPredicate.deserialize(json.get("predicate"));
    }
    return new PotionFluidEffect(scale, predicate);
  };
}
