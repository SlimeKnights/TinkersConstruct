package slimeknights.tconstruct.library.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMaterialsPacket implements INetworkSendable {

  private Collection<IMaterial> materials;

  public UpdateMaterialsPacket(PacketBuffer buffer) {
    decode(buffer);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    int materialCount = buffer.readInt();
    materials = new ArrayList<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      boolean craftable = buffer.readBoolean();
      ResourceLocation fluidId = buffer.readResourceLocation();
      Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
      ItemStack shard = buffer.readItemStack();

      materials.add(new Material(id, fluid, craftable, shard));
    }
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(materials.size());
    materials.forEach(material -> {
      buffer.writeResourceLocation(material.getIdentifier());
      buffer.writeBoolean(material.isCraftable());
      buffer.writeResourceLocation(material.getFluid().getRegistryName());
      buffer.writeItemStack(material.getShard());
    });
  }
}
