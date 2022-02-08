package slimeknights.tconstruct.library.materials.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UpdateMaterialsPacket implements IThreadsafePacket {
  private final Collection<IMaterial> materials;
  private final Map<MaterialId,MaterialId> redirects;

  public UpdateMaterialsPacket(FriendlyByteBuf buffer) {
    int materialCount = buffer.readInt();
    this.materials = new ArrayList<>(materialCount);

    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int tier = buffer.readVarInt();
      int sortOrder = buffer.readVarInt();
      boolean craftable = buffer.readBoolean();
      boolean hidden = buffer.readBoolean();
      this.materials.add(new Material(id, tier, sortOrder, craftable, hidden));
    }
    // process redirects
    int redirectCount = buffer.readVarInt();
    if (redirectCount == 0) {
      this.redirects = Collections.emptyMap();
    } else {
      this.redirects = new HashMap<>(redirectCount);
      for (int i = 0; i < redirectCount; i++) {
        this.redirects.put(new MaterialId(buffer.readUtf()), new MaterialId(buffer.readUtf()));
      }
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(this.materials.size());
    this.materials.forEach(material -> {
      buffer.writeResourceLocation(material.getIdentifier());
      buffer.writeVarInt(material.getTier());
      buffer.writeVarInt(material.getSortOrder());
      buffer.writeBoolean(material.isCraftable());
      buffer.writeBoolean(material.isHidden());
    });
    buffer.writeVarInt(this.redirects.size());
    this.redirects.forEach((key, value) -> {
      buffer.writeUtf(key.toString());
      buffer.writeUtf(value.toString());
    });
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialsFromServer(this);
  }
}
