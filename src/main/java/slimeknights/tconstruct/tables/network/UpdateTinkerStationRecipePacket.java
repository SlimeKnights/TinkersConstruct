package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.tables.client.inventory.table.TinkerStationScreen;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

import java.util.Optional;

/**
 * Packet to send the current crafting recipe to a player who opens the tinker station
 */
public class UpdateTinkerStationRecipePacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final ResourceLocation recipe;
  public UpdateTinkerStationRecipePacket(BlockPos pos, ITinkerStationRecipe recipe) {
    this.pos = pos;
    this.recipe = recipe.getId();
  }

  public UpdateTinkerStationRecipePacket(PacketBuffer buffer) {
    this.pos = buffer.readBlockPos();
    this.recipe = buffer.readResourceLocation();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeResourceLocation(recipe);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(UpdateTinkerStationRecipePacket packet) {
      World world = Minecraft.getInstance().world;
      if (world != null) {
        Optional<ITinkerStationRecipe> recipe = RecipeHelper.getRecipe(world.getRecipeManager(), packet.recipe, ITinkerStationRecipe.class);

        // if the screen is open, use that to get the TE and update the screen
        boolean handled = false;
        Screen screen = Minecraft.getInstance().currentScreen;
        if (screen instanceof TinkerStationScreen) {
          TinkerStationScreen stationScreen = (TinkerStationScreen) screen;
          TinkerStationTileEntity te = stationScreen.getTileEntity();
          if (te.getPos().equals(packet.pos)) {
            recipe.ifPresent(te::updateRecipe);
            stationScreen.updateDisplay();
            handled = true;
          }
        }
        // if the wrong screen is open or no screen, use the tile directly
        if (!handled) {
          TileEntityHelper.getTile(TinkerStationTileEntity.class, world, packet.pos).ifPresent(te -> recipe.ifPresent(te::updateRecipe));
        }
      }
    }
  }
}
