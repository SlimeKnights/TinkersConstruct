package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
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
    context.getTarget().setFire(time);
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }

  private static class Loader implements ISpillingEffectLoader<SetFireSpillingEffect> {
    @Override
    public SetFireSpillingEffect deserialize(JsonObject json) {
      return new SetFireSpillingEffect(JSONUtils.getInt(json, "time"));
    }

    @Override
    public SetFireSpillingEffect read(PacketBuffer buffer) {
      return new SetFireSpillingEffect(buffer.readVarInt());
    }

    @Override
    public void serialize(SetFireSpillingEffect effect, JsonObject json) {
      json.addProperty("time", effect.time);
    }

    @Override
    public void write(SetFireSpillingEffect effect, PacketBuffer buffer) {
      buffer.writeVarInt(effect.time);
    }
  }
}
