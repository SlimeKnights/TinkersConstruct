package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

import java.util.Objects;

/** Spilling effect to apply a potion effect */
@RequiredArgsConstructor
public class EffectSpillingEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();

  private final Effect effect;
  /** Potion time in seconds */
  private final int time;
  /** Potion level starting at 1 */
  private final int level;

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      int time = (int)(this.time * 20 * scale);
      if (time > 0) {
        target.addPotionEffect(new EffectInstance(effect, time, level - 1));
      }
    }
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements ISpillingEffectLoader<EffectSpillingEffect> {
    @Override
    public EffectSpillingEffect deserialize(JsonObject json) {
      ResourceLocation id = JsonHelper.getResourceLocation(json, "name");
      if (!ForgeRegistries.POTIONS.containsKey(id)) {
        throw new JsonSyntaxException("Unknown effect " + id);
      }
      Effect effect = Objects.requireNonNull(ForgeRegistries.POTIONS.getValue(id));
      int time = JSONUtils.getInt(json, "time");
      int level = JSONUtils.getInt(json, "level", 1);
      return new EffectSpillingEffect(effect, time, level);
    }

    @Override
    public EffectSpillingEffect read(PacketBuffer buffer) {
      Effect effect = buffer.readRegistryIdUnsafe(ForgeRegistries.POTIONS);
      int level = buffer.readVarInt();
      int time = buffer.readVarInt();
      return new EffectSpillingEffect(effect, time, level);
    }

    @Override
    public void serialize(EffectSpillingEffect effect, JsonObject json) {
      json.addProperty("name", Objects.requireNonNull(effect.effect.getRegistryName()).toString());
      json.addProperty("time", effect.time);
      json.addProperty("level", effect.level);
    }

    @Override
    public void write(EffectSpillingEffect effect, PacketBuffer buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.POTIONS, effect.effect);
      buffer.writeVarInt(effect.time);
      buffer.writeVarInt(effect.level);
    }
  }
}
