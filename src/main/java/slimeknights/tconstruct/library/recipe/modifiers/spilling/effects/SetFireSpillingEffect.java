package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/**
 * Effect to set an entity on fire
 */
@RequiredArgsConstructor
public class SetFireSpillingEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();

  private final int time;

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    context.getTarget().setSecondsOnFire(time);
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements ISpillingEffectLoader<SetFireSpillingEffect> {
    @Override
    public SetFireSpillingEffect deserialize(JsonObject json) {
      return new SetFireSpillingEffect(GsonHelper.getAsInt(json, "time"));
    }

    @Override
    public SetFireSpillingEffect read(FriendlyByteBuf buffer) {
      return new SetFireSpillingEffect(buffer.readVarInt());
    }

    @Override
    public void serialize(SetFireSpillingEffect effect, JsonObject json) {
      json.addProperty("time", effect.time);
    }

    @Override
    public void write(SetFireSpillingEffect effect, FriendlyByteBuf buffer) {
      buffer.writeVarInt(effect.time);
    }
  }
}
