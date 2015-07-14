package tconstruct.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.property.IExtendedBlockState;

import tconstruct.common.block.BlockTable;
import tconstruct.library.client.model.ModelHelper;

public class TileTable extends TileInventory {

  public static final String FEET_TAG = "textureBlock";

  public TileTable(String name, int inventorySize) {
    super(name, inventorySize);
  }

  public TileTable(String name, int inventorySize, int maxStackSize) {
    super(name, inventorySize, maxStackSize);
  }

  public IExtendedBlockState writeExtendedBlockState(IExtendedBlockState state) {
    String texture = getTileData().getString("texture");

    // texture not loaded
    if(texture == null || texture.isEmpty()) {
      // load it from saved block
      ItemStack stack = ItemStack.loadItemStackFromNBT(getTileData().getCompoundTag(FEET_TAG));
      if(stack != null) {
        Block block = Block.getBlockFromItem(stack.getItem());
        texture = ModelHelper.getTextureFromBlock(block, stack.getItemDamage()).getIconName();
        getTileData().setString("texture", texture);
      }
    }

    if(texture != null && !texture.isEmpty()) {
      state = state.withProperty(BlockTable.TEXTURE, texture);
    }

    return state;
  }

  @Override
  public Packet getDescriptionPacket() {
    // note that this sends all of the tile data. you should change this if you use additional tile data
    return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), getTileData());
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    getTileData().setTag(FEET_TAG, pkt.getNbtCompound().getTag(FEET_TAG));
  }

  public void updateTextureBlock(NBTTagCompound tag) {
    getTileData().setTag(FEET_TAG, tag);
  }

  public NBTTagCompound getTextureBlock() {
    return getTileData().getCompoundTag(FEET_TAG);
  }
}
