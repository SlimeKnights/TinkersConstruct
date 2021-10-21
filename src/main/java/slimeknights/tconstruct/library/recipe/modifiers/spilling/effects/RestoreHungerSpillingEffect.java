package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/** Effect to restore hunger to the target */
@RequiredArgsConstructor
public class RestoreHungerSpillingEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();

  private final int hunger;
  private final float saturation;

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) target;
      if (player.canEat(false)) {
        player.getFoodStats().addStats((int)(hunger * scale), saturation * scale);
      }
    }
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements ISpillingEffectLoader<RestoreHungerSpillingEffect> {
    @Override
    public RestoreHungerSpillingEffect deserialize(JsonObject json) {
      int hunger = JSONUtils.getInt(json, "hunger");
      float saturation = JSONUtils.getFloat(json, "saturation");
      return new RestoreHungerSpillingEffect(hunger, saturation);
    }

    @Override
    public void serialize(RestoreHungerSpillingEffect effect, JsonObject json) {
      json.addProperty("hunger", effect.hunger);
      json.addProperty("saturation", effect.saturation);
    }

    @Override
    public RestoreHungerSpillingEffect read(PacketBuffer buffer) {
      int hunger = buffer.readVarInt();
      float saturation = buffer.readFloat();
      return new RestoreHungerSpillingEffect(hunger, saturation);
    }

    @Override
    public void write(RestoreHungerSpillingEffect effect, PacketBuffer buffer) {
      buffer.writeVarInt(effect.hunger);
      buffer.writeFloat(effect.saturation);
    }
  }
}
