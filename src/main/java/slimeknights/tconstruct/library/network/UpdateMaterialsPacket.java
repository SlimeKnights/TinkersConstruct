package slimeknights.tconstruct.library.network;

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
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

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
      boolean craftable = buffer.readBoolean();
      Fluid fluid = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
      if (fluid == null) {
        fluid = Fluids.EMPTY;
      }
      int fluidPerUnit = buffer.readVarInt();
      int color = buffer.readInt();
      int temperature = buffer.readInt();
      // buffer has a boolean stating if the trait is nonnull
      ModifierEntry trait = null;
      if (buffer.readBoolean()) {
        trait = new ModifierEntry(buffer.readRegistryIdUnsafe(TinkerRegistries.MODIFIERS), buffer.readVarInt());
      }
      this.materials.add(new Material(id, fluid, fluidPerUnit, craftable, Color.fromInt(color), temperature, trait));
    }
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.materials.size());
    this.materials.forEach(material -> {
      buffer.writeResourceLocation(material.getIdentifier());
      buffer.writeBoolean(material.isCraftable());
      buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, material.getFluid());
      buffer.writeVarInt(material.getFluidPerUnit());
      // the color int getter is private
      buffer.writeInt(material.getColor().color);
      buffer.writeInt(material.getTemperature());
      ModifierEntry trait = material.getTrait();
      // write boolean to signify this s null
      if (trait == null) {
        buffer.writeBoolean(false);
      } else {
        buffer.writeBoolean(true);
        buffer.writeRegistryIdUnsafe(TinkerRegistries.MODIFIERS, trait.getModifier());
        buffer.writeVarInt(trait.getLevel());
      }
    });
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialsFromServer(this);
  }
}
