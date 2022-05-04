package slimeknights.tconstruct.library.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** Packet to sync modifiers */
public class UpdateModifiersPacket implements IThreadsafePacket {
  /** Collection of all modifiers */
  private final Map<ModifierId,Modifier> allModifiers;
  /** Collection of non-redirect modifiers */
  private Collection<Modifier> modifiers;
  /** Map of modifier redirect ID pairs */
  private Map<ModifierId,ModifierId> redirects;

  public UpdateModifiersPacket(Map<ModifierId,Modifier> map) {
    this.allModifiers = map;
  }

  /** Ensures both the modifiers and redirects lists are calculated, allows one packet to be used multiple times without redundant work */
  private void ensureCalculated() {
    if (this.modifiers == null || this.redirects == null) {
      ImmutableList.Builder<Modifier> modifiers = ImmutableList.builder();
      ImmutableMap.Builder<ModifierId,ModifierId> redirects = ImmutableMap.builder();
      for (Entry<ModifierId,Modifier> entry : allModifiers.entrySet()) {
        ModifierId id = entry.getKey();
        Modifier mod = entry.getValue();
        if (id.equals(mod.getId())) {
          modifiers.add(mod);
        } else {
          redirects.put(id, mod.getId());
        }
      }
      this.modifiers = modifiers.build();
      this.redirects = redirects.build();
    }
  }

  public UpdateModifiersPacket(FriendlyByteBuf buffer) {
    // read in modifiers
    int size = buffer.readVarInt();
    Map<ModifierId,Modifier> modifiers = new HashMap<>();
    for (int i = 0; i < size; i++) {
      ModifierId id = new ModifierId(buffer.readUtf(Short.MAX_VALUE));
      Modifier modifier = ModifierManager.MODIFIER_LOADERS.fromNetwork(buffer);
      modifier.setId(id);
      modifiers.put(id, modifier);
    }
    // read in redirects
    size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      ModifierId from = new ModifierId(buffer.readUtf(Short.MAX_VALUE));
      ModifierId to = new ModifierId(buffer.readUtf(Short.MAX_VALUE));
      Modifier modifier = ModifierManager.INSTANCE.getStatic(to);
      if (modifier == ModifierManager.INSTANCE.getDefaultValue()) {
        modifier = modifiers.get(to);
        if (modifier == null) {
          throw new DecoderException("Unknown modifier " + to);
        }
      }
      modifiers.put(from, modifier);
    }
    this.allModifiers = modifiers;
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    ensureCalculated();
    // write modifiers
    buffer.writeVarInt(modifiers.size());
    for (Modifier modifier : modifiers) {
      buffer.writeResourceLocation(modifier.getId());
      ModifierManager.MODIFIER_LOADERS.toNetwork(modifier, buffer);
    }
    // write redirects
    buffer.writeVarInt(redirects.size());
    for (Entry<ModifierId,ModifierId> entry : redirects.entrySet()) {
      buffer.writeResourceLocation(entry.getKey());
      buffer.writeResourceLocation(entry.getValue());
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    ModifierManager.INSTANCE.updateModifiersFromServer(allModifiers);
  }
}
