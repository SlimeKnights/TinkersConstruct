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
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/** Spilling effect that pulls the potion from a NBT potion fluid and applies it */
@RequiredArgsConstructor
public class PotionFluidEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();

  /** Amount to scale the potion duration by, or the scale for instant effects */
  private final float effectScale;

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    LivingEntity attacker = context.getAttacker();
    if (target != null) {
      Potion potion = PotionUtils.getPotionTypeFromNBT(fluid.getTag());
      if (potion != Potions.EMPTY) {
        // prevent effects like instant damage from hitting hurt resistance
        int oldHurtResistance = target.hurtResistantTime;
        float totalScale = scale * effectScale;
        for (EffectInstance instance : potion.getEffects()) {
          Effect effect = instance.getPotion();
          if (effect.isInstant()) {
            target.hurtResistantTime = 0;
            effect.affectEntity(attacker, attacker, target, instance.getAmplifier(), totalScale);
          } else {
            int duration = (int)(instance.getDuration() * totalScale);
            if (duration > 10) {
              target.addPotionEffect(new EffectInstance(effect, duration, instance.getAmplifier(), instance.isAmbient(), instance.doesShowParticles(), instance.isShowIcon()));
            }
          }
        }
        target.hurtResistantTime = oldHurtResistance;
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
      float scale = JSONUtils.getFloat(json, "scale");
      return new PotionFluidEffect(scale);
    }

    @Override
    public PotionFluidEffect read(PacketBuffer buffer) {
      float scale = buffer.readFloat();
      return new PotionFluidEffect(scale);
    }

    @Override
    public void serialize(PotionFluidEffect effect, JsonObject json) {
      json.addProperty("scale", effect.effectScale);
    }

    @Override
    public void write(PotionFluidEffect effect, PacketBuffer buffer) {
      buffer.writeFloat(effect.effectScale);
    }
  }
}
