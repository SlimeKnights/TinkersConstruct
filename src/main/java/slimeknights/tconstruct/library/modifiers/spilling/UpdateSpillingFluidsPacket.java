package slimeknights.tconstruct.library.modifiers.spilling;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

import java.util.Collections;
import java.util.List;

/** Packet to sync fluid predicates to the client */
@RequiredArgsConstructor
public class UpdateSpillingFluidsPacket implements IThreadsafePacket {
  private final List<SpillingFluid> fluids;

  /** Clientside constructor, sets ingredients */
  public UpdateSpillingFluidsPacket(FriendlyByteBuf buf) {
    int size = buf.readVarInt();
    ImmutableList.Builder<SpillingFluid> fluids = ImmutableList.builder();
    List<ISpillingEffect> effects = Collections.singletonList(NoEffect.INSTANCE); // list with a single effect for the client
    for (int i = 0; i < size; i++) {
      fluids.add(new SpillingFluid(FluidIngredient.read(buf), effects));
    }
    this.fluids = fluids.build();
  }

  @Override
  public void encode(FriendlyByteBuf buf) {
    buf.writeVarInt(fluids.size());
    for (SpillingFluid fluid : fluids) {
      fluid.ingredient().write(buf);
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    SpillingFluidManager.INSTANCE.updateFromServer(fluids);
  }

  private static class NoEffect implements ISpillingEffect {
    private static final ISpillingEffect INSTANCE = new NoEffect();

    @Override
    public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {}

    @Override
    public JsonObject serialize(JsonSerializationContext context) {
      throw new UnsupportedOperationException("Cannot serialize spilling fluids on the client");
    }
  }
}
