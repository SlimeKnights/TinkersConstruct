package slimeknights.tconstruct.tables.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

/**
 * Packet to send the current crafting recipe to a player who opens the crafting station
 */
public class UpdateCraftingRecipePacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final Identifier recipe;
  public UpdateCraftingRecipePacket(BlockPos pos, CraftingRecipe recipe) {
    this.pos = pos;
    this.recipe = recipe.getId();
  }

  public UpdateCraftingRecipePacket(PacketByteBuf buffer) {
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
    private static void handle(UpdateCraftingRecipePacket packet) {
      World world = MinecraftClient.getInstance().world;
      if (world != null) {
        TileEntityHelper.getTile(CraftingStationTileEntity.class, world, packet.pos).ifPresent(te ->
          RecipeHelper.getRecipe(world.getRecipeManager(), packet.recipe, CraftingRecipe.class).ifPresent(te::updateRecipe));
      }
    }
  }
}
