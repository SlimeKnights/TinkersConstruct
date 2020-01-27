package slimeknights.tconstruct.library.network;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;

import java.util.ArrayList;
import java.util.Collection;

public class UpdateMaterialsPacket implements ITinkerPacket {

  private Collection<IMaterial> materials;

  public UpdateMaterialsPacket() {
  }

  public UpdateMaterialsPacket(Collection<IMaterial> materials) {
    this.materials = materials;
  }

  public UpdateMaterialsPacket(PacketBuffer buffer) {
    decode(buffer);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    int materialCount = buffer.readInt();
    materials = new ArrayList<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readString());
      boolean craftable = buffer.readBoolean();
      ResourceLocation fluidId = new ResourceLocation(buffer.readString());
      Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
      ItemStack shard = buffer.readItemStack();

      materials.add(new Material(id, fluid, craftable, shard));
    }
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(materials.size());
    materials.forEach(material -> {
      buffer.writeString(material.getIdentifier().toString());
      buffer.writeBoolean(material.isCraftable());
      buffer.writeString(material.getFluid().getRegistryName().toString());
      buffer.writeItemStack(material.getShard());
    });
  }

  @Override
  public void handle(NetworkEvent.Context context) {
    MaterialRegistry.updateMaterialsFromServer(materials);
  }
}
