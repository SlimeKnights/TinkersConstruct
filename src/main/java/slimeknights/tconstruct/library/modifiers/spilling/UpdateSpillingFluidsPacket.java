package slimeknights.tconstruct.library.modifiers.spilling;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.ISimplePacket;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

import java.util.List;
import java.util.function.Supplier;

/** Packet to sync fluid predicates to the client */
@RequiredArgsConstructor
public class UpdateSpillingFluidsPacket implements ISimplePacket {
  private final List<SpillingFluid> fluids;

  /** Clientside constructor, sets ingredients */
  public UpdateSpillingFluidsPacket(FriendlyByteBuf buf) {
    int size = buf.readVarInt();
    ImmutableList.Builder<SpillingFluid> fluids = ImmutableList.builder();
    for (int i = 0; i < size; i++) {
      fluids.add(new SpillingFluid(FluidIngredient.read(buf)));
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
  public void handle(Supplier<Context> context) {
    SpillingFluidManager.INSTANCE.updateFromServer(fluids);
  }
}
