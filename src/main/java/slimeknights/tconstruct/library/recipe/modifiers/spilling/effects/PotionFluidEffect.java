package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.TagPredicate;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/** Spilling effect that pulls the potion from a NBT potion fluid and applies it */
@RequiredArgsConstructor
public class PotionFluidEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();

  /** Amount to scale the potion duration by, or the scale for instant effects */
  private final float effectScale;
  /** NBT that must match on the fluid for this effect to run */
  private final TagPredicate predicate;

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
        for (EffectInstance instance : potion.getEffects()) {
          Effect effect = instance.getEffect();
          if (effect.isInstantenous()) {
            target.invulnerableTime = 0;
            effect.applyInstantenousEffect(attacker, attacker, target, instance.getAmplifier(), totalScale);
          } else {
            int duration = (int)(instance.getDuration() * totalScale);
            if (duration > 10) {
              target.addEffect(new EffectInstance(effect, duration, instance.getAmplifier(), instance.isAmbient(), instance.isVisible(), instance.showIcon()));
            }
          }
        }
        target.invulnerableTime = oldInvulnerableTime;
      }
    }
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements ISpillingEffectLoader<PotionFluidEffect> {
    @Override
    public PotionFluidEffect deserialize(JsonObject json) {
      float scale = JSONUtils.getAsFloat(json, "scale");
      TagPredicate predicate = TagPredicate.ANY;
      if (json.has("predicate")) {
        predicate = TagPredicate.deserialize(json.get("predicate"));
      }
      return new PotionFluidEffect(scale, predicate);
    }

    @Override
    public PotionFluidEffect read(PacketBuffer buffer) {
      float scale = buffer.readFloat();
      TagPredicate predicate = TagPredicate.read(buffer);
      return new PotionFluidEffect(scale, predicate);
    }

    @Override
    public void serialize(PotionFluidEffect effect, JsonObject json) {
      json.addProperty("scale", effect.effectScale);
      if (effect.predicate != TagPredicate.ANY) {
        json.add("predicate", effect.predicate.serialize());
      }
    }

    @Override
    public void write(PotionFluidEffect effect, PacketBuffer buffer) {
      buffer.writeFloat(effect.effectScale);
      effect.predicate.write(buffer);
    }
  }
}
