package slimeknights.tconstruct.library.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class UpdateMaterialsPacket implements IThreadsafePacket {
  private final Collection<IMaterial> materials;
  public UpdateMaterialsPacket(PacketBuffer buffer) {
    int materialCount = buffer.readInt();
    this.materials = new ArrayList<>(materialCount);

    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      boolean craftable = buffer.readBoolean();
      ResourceLocation fluidId = buffer.readResourceLocation();
      Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
      if (fluid == null) {
        fluid = Fluids.EMPTY;
      }
      String textColor = buffer.readString();
      int temperature = buffer.readInt();

      this.materials.add(new Material(id, fluid, craftable, textColor, temperature));
    }
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.materials.size());
    this.materials.forEach(material -> {
      buffer.writeResourceLocation(material.getIdentifier());
      buffer.writeBoolean(material.isCraftable());
      buffer.writeResourceLocation(Objects.requireNonNull(material.getFluid().getRegistryName()));
      buffer.writeString(material.getTextColor());
      buffer.writeInt(material.getTemperature());
    });
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialsFromServer(this);
  }
}
