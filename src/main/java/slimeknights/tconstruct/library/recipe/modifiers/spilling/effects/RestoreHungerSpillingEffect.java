package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
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
    if (target instanceof Player player) {
      if (player.canEat(false)) {
        player.getFoodData().eat((int)(hunger * scale), saturation * scale);
      }
    }
  }

  @Override
  public IGenericLoader<? extends ISpillingEffect> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<RestoreHungerSpillingEffect> {
    @Override
    public RestoreHungerSpillingEffect deserialize(JsonObject json) {
      int hunger = GsonHelper.getAsInt(json, "hunger");
      float saturation = GsonHelper.getAsFloat(json, "saturation");
      return new RestoreHungerSpillingEffect(hunger, saturation);
    }

    @Override
    public void serialize(RestoreHungerSpillingEffect effect, JsonObject json) {
      json.addProperty("hunger", effect.hunger);
      json.addProperty("saturation", effect.saturation);
    }

    @Override
    public RestoreHungerSpillingEffect fromNetwork(FriendlyByteBuf buffer) {
      int hunger = buffer.readVarInt();
      float saturation = buffer.readFloat();
      return new RestoreHungerSpillingEffect(hunger, saturation);
    }

    @Override
    public void toNetwork(RestoreHungerSpillingEffect effect, FriendlyByteBuf buffer) {
      buffer.writeVarInt(effect.hunger);
      buffer.writeFloat(effect.saturation);
    }
  }
}
