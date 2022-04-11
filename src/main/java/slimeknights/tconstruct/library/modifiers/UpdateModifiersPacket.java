package slimeknights.tconstruct.library.modifiers;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

import java.util.Collection;

/** Packet to sync modifiers */
@RequiredArgsConstructor
public class UpdateModifiersPacket implements IThreadsafePacket {
  private final Collection<Modifier> modifiers;

  public UpdateModifiersPacket(FriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    ImmutableList.Builder<Modifier> builder = ImmutableList.builder();
    for (int i = 0; i < size; i++) {
      builder.add(ModifierManager.INSTANCE.modifierSerializers.fromNetwork(buffer));
    }
    this.modifiers = builder.build();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeVarInt(modifiers.size());
    for (Modifier modifier : modifiers) {
      ModifierManager.INSTANCE.modifierSerializers.toNetwork(modifier, buffer);
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    ModifierManager.INSTANCE.updateModifiersFromServer(modifiers);
  }
}
