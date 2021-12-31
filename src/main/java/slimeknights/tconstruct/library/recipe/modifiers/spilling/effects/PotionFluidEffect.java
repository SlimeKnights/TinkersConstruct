package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.TagPredicate;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IGenericLoader;

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
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<PotionFluidEffect> {
    @Override
    public PotionFluidEffect deserialize(JsonObject json) {
      float scale = GsonHelper.getAsFloat(json, "scale");
      TagPredicate predicate = TagPredicate.ANY;
      if (json.has("predicate")) {
        predicate = TagPredicate.deserialize(json.get("predicate"));
      }
      return new PotionFluidEffect(scale, predicate);
    }

    @Override
    public PotionFluidEffect fromNetwork(FriendlyByteBuf buffer) {
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
    public void toNetwork(PotionFluidEffect effect, FriendlyByteBuf buffer) {
      buffer.writeFloat(effect.effectScale);
      effect.predicate.write(buffer);
    }
  }
}
