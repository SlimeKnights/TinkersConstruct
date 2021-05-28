package slimeknights.tconstruct.library.materials;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.Color;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.MaterialRegistry;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class UpdateMaterialsPacket implements IThreadsafePacket {
  private final Collection<IMaterial> materials;

  public UpdateMaterialsPacket(PacketBuffer buffer) {
    int materialCount = buffer.readInt();
    this.materials = new ArrayList<>(materialCount);

    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int tier = buffer.readVarInt();
      int sortOrder = buffer.readVarInt();
      boolean craftable = buffer.readBoolean();
      Fluid fluid = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
      if (fluid == null) {
        fluid = Fluids.EMPTY;
      }
      int fluidPerUnit = buffer.readVarInt();
      int color = buffer.readInt();
      int temperature = buffer.readInt();
      this.materials.add(new Material(id, tier, sortOrder, fluid, fluidPerUnit, craftable, Color.fromInt(color), temperature));
    }
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.materials.size());
    this.materials.forEach(material -> {
      buffer.writeResourceLocation(material.getIdentifier());
      buffer.writeVarInt(material.getTier());
      buffer.writeVarInt(material.getSortOrder());
      buffer.writeBoolean(material.isCraftable());
      buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, material.getFluid());
      buffer.writeVarInt(material.getFluidPerUnit());
      // the color int getter is private
      buffer.writeInt(material.getColor().color);
      buffer.writeInt(material.getTemperature());
    });
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialsFromServer(this);
  }
}
