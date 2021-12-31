package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IGenericLoader;

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
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<SetFireSpillingEffect> {
    @Override
    public SetFireSpillingEffect deserialize(JsonObject json) {
      return new SetFireSpillingEffect(GsonHelper.getAsInt(json, "time"));
    }

    @Override
    public SetFireSpillingEffect fromNetwork(FriendlyByteBuf buffer) {
      return new SetFireSpillingEffect(buffer.readVarInt());
    }

    @Override
    public void serialize(SetFireSpillingEffect effect, JsonObject json) {
      json.addProperty("time", effect.time);
    }

    @Override
    public void toNetwork(SetFireSpillingEffect effect, FriendlyByteBuf buffer) {
      buffer.writeVarInt(effect.time);
    }
  }
}
