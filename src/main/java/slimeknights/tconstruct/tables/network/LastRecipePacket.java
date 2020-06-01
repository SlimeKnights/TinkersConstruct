package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;

import java.util.Optional;
import java.util.function.Supplier;

public class LastRecipePacket extends AbstractPacket {

  public static final ResourceLocation NO_RECIPE = Util.getResource("null");
  private ResourceLocation recipe;

  public LastRecipePacket(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public LastRecipePacket(PacketBuffer buffer) {
    this.recipe = buffer.readResourceLocation();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeResourceLocation(this.recipe);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      PlayerEntity playerEntity = Minecraft.getInstance().player;

      if (playerEntity != null) {
        Container container = Minecraft.getInstance().player.openContainer;

        if (this.recipe != NO_RECIPE) {
          Optional<? extends IRecipe<?>> optional = playerEntity.getEntityWorld().getRecipeManager().getRecipe(this.recipe);

          if (optional.isPresent()) {
            IRecipe<?> recipe = optional.get();

            if (recipe.getType() == IRecipeType.CRAFTING) {
              ICraftingRecipe craftingRecipe = (ICraftingRecipe) recipe;

              if (container instanceof CraftingStationContainer) {
                ((CraftingStationContainer) container).updateLastRecipeFromServer(craftingRecipe);
              }
            } else {
              if (container instanceof CraftingStationContainer) {
                ((CraftingStationContainer) container).updateLastRecipeFromServer(null);
              }
            }
          } else {
            if (container instanceof CraftingStationContainer) {
              ((CraftingStationContainer) container).updateLastRecipeFromServer(null);
            }
          }
        } else {
          if (container instanceof CraftingStationContainer) {
            ((CraftingStationContainer) container).updateLastRecipeFromServer(null);
          }
        }
      }
    });
    supplier.get().setPacketHandled(true);
  }
}
