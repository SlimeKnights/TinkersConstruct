package slimeknights.tconstruct.library.materials.definition;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.utils.GenericTagUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UpdateMaterialsPacket implements IThreadsafePacket {
  private final Map<MaterialId,IMaterial> materials;
  private final Map<MaterialId,MaterialId> redirects;
  private final Map<TagKey<IMaterial>,List<IMaterial>> tags;

  public UpdateMaterialsPacket(FriendlyByteBuf buffer) {
    int materialCount = buffer.readInt();
    ImmutableMap.Builder<MaterialId,IMaterial> materials = ImmutableMap.builder();

    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int tier = buffer.readVarInt();
      int sortOrder = buffer.readVarInt();
      boolean craftable = buffer.readBoolean();
      boolean hidden = buffer.readBoolean();
      materials.put(id, new Material(id, tier, sortOrder, craftable, hidden));
    }
    this.materials = materials.build();
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
    this.tags = GenericTagUtil.decodeTags(buffer, MaterialManager.REGISTRY_KEY, id -> this.materials.get(new MaterialId(id)));
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(this.materials.size());
    this.materials.values().forEach(material -> {
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
    GenericTagUtil.encodeTags(buffer, IMaterial::getIdentifier, this.tags);
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialsFromServer(this);
  }
}
