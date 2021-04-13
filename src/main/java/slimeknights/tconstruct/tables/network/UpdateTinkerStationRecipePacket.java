package slimeknights.tconstruct.tables.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity;

/**
 * Packet to send the current crafting recipe to a player who opens the tinker station
 */
public class UpdateTinkerStationRecipePacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final Identifier recipe;
  public UpdateTinkerStationRecipePacket(BlockPos pos, ITinkerStationRecipe recipe) {
    this.pos = pos;
    this.recipe = recipe.getId();
  }

  public UpdateTinkerStationRecipePacket(PacketByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.recipe = buffer.readIdentifier();
  }

  @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeIdentifier(recipe);
  }

  @Override
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(UpdateTinkerStationRecipePacket packet) {
      World world = MinecraftClient.getInstance().world;
      if (world != null) {
        TileEntityHelper.getTile(TinkerStationTileEntity.class, world, packet.pos).ifPresent(te ->
          RecipeHelper.getRecipe(world.getRecipeManager(), packet.recipe, ITinkerStationRecipe.class).ifPresent(te::updateRecipe));
      }
    }
  }
}
