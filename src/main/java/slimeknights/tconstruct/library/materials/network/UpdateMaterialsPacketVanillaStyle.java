package slimeknights.tconstruct.library.materials.network;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;

import java.util.ArrayList;
import java.util.List;

public class UpdateMaterialsPacketVanillaStyle implements IPacket<IClientPlayNetHandler> {

  private List<IMaterial> materials;

  public UpdateMaterialsPacketVanillaStyle() {
  }

  public UpdateMaterialsPacketVanillaStyle(List<IMaterial> materials) {
    this.materials = materials;
  }

  @Override
  public void readPacketData(PacketBuffer buf) {
    int materialCount = buf.readInt();
    materials = new ArrayList<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buf.readString());
      boolean craftable = buf.readBoolean();
      ResourceLocation fluidId = new ResourceLocation(buf.readString());
      Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
      ItemStack shard = buf.readItemStack();

      materials.add(new Material(id, fluid, craftable, shard));
    }
  }

  @Override
  public void writePacketData(PacketBuffer buf) {
    buf.writeInt(materials.size());
    materials.forEach(material -> {
      buf.writeString(material.getIdentifier().toString());
      buf.writeBoolean(material.isCraftable());
      buf.writeString(material.getFluid().getRegistryName().toString());
      buf.writeItemStack(material.getShard());
    });
  }

  @Override
  public void processPacket(IClientPlayNetHandler handler) {
    MaterialRegistry.updateMaterialsFromServer(materials);
  }
}
