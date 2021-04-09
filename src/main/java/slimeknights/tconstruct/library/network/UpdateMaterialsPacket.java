package slimeknights.tconstruct.library.network;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TextColor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateMaterialsPacket implements IThreadsafePacket {
  private final Collection<IMaterial> materials;

  public UpdateMaterialsPacket(PacketByteBuf buffer) {
    int materialCount = buffer.readInt();
    this.materials = new ArrayList<>(materialCount);

    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readIdentifier());
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
      // buffer has a boolean stating if the trait is nonnull
      ImmutableList.Builder<ModifierEntry> builder = ImmutableList.builder();
      int size = buffer.readVarInt();
      for (int t = 0; t < size; t++) {
        builder.add(ModifierEntry.read(buffer));
      }
      this.materials.add(new Material(id, tier, sortOrder, fluid, fluidPerUnit, craftable, TextColor.fromRgb(color), temperature, builder.build()));
    }
  }

  @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeInt(this.materials.size());
    this.materials.forEach(material -> {
      buffer.writeIdentifier(material.getIdentifier());
      buffer.writeVarInt(material.getTier());
      buffer.writeVarInt(material.getSortOrder());
      buffer.writeBoolean(material.isCraftable());
      buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, material.getFluid());
      buffer.writeVarInt(material.getFluidPerUnit());
      // the color int getter is private
      buffer.writeInt(material.getColor().rgb);
      buffer.writeInt(material.getTemperature());
      List<ModifierEntry> traits = material.getTraits();
      buffer.writeVarInt(traits.size());
      for (ModifierEntry entry : traits) {
        entry.write(buffer);
      }
    });
  }

  @Override
  public void handleThreadsafe(PacketSender context) {
    MaterialRegistry.updateMaterialsFromServer(this);
  }
}
