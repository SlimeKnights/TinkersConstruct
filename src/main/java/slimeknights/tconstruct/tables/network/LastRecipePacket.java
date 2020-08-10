package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;

public class LastRecipePacket implements IThreadsafePacket {

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
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(LastRecipePacket packet) {
      PlayerEntity player = Minecraft.getInstance().player;
      if (player == null) {
        return;
      }
      if (!(player.openContainer instanceof CraftingStationContainer)) {
        return;
      }
      CraftingStationContainer container = (CraftingStationContainer)player.openContainer;

      // ensure a recipe was set
      if (packet.recipe != NO_RECIPE) {
        // fetch the recipe
        ICraftingRecipe recipe = RecipeUtil.getRecipe(player.getEntityWorld().getRecipeManager(), packet.recipe, ICraftingRecipe.class).orElse(null);
        if (recipe != null) {
          container.updateLastRecipeFromServer(recipe);
          return;
        }
      }
      container.updateLastRecipeFromServer(null);
    }
  }
}
