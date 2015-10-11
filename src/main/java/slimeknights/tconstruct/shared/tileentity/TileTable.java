package slimeknights.tconstruct.shared.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.List;

import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.common.Config;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.tools.network.InventorySlotSyncPacket;

public class TileTable extends TileInventory {

  public static final String FEET_TAG = "textureBlock";
  protected int displaySlot = 0;

  // default constructor for loading
  public TileTable() {
    super("", 0, 0);
  }

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

    state = setInventoryDisplay(state);

    return state;
  }

  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();
    if(getStackInSlot(displaySlot) != null) {
      ItemStack stack = getStackInSlot(displaySlot);
      PropertyTableItem.TableItem item = getTableItem(stack);
      if(item != null) {
        toDisplay.items.add(item);
      }
    }
    // add inventory if needed
    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }

  protected PropertyTableItem.TableItem getTableItem(ItemStack stack) {
    if(stack == null)
      return null;
    IFlexibleBakedModel stackModel =
        (IFlexibleBakedModel) Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
    if(stackModel != null) {
      PropertyTableItem.TableItem item = new PropertyTableItem.TableItem(stackModel, 0,-0.46875f,0, 0.8f, (float) (-Math.PI/2));
      if(stack.getItem() instanceof  ItemBlock) {
        item.y = -0.3125f;
        item.s = 0.375f;
        item.r = 0;
      }
      return item;
    }
    return null;
  }

  @Override
  public Packet getDescriptionPacket() {
    // note that this sends all of the tile data. you should change this if you use additional tile data
    NBTTagCompound tag = (NBTTagCompound) getTileData().copy();
    writeToNBT(tag);
    return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    NBTTagCompound tag = pkt.getNbtCompound();
    getTileData().setTag(FEET_TAG, tag.getTag(FEET_TAG));
    readFromNBT(tag);
  }

  public void updateTextureBlock(NBTTagCompound tag) {
    getTileData().setTag(FEET_TAG, tag);
  }

  public NBTTagCompound getTextureBlock() {
    return getTileData().getCompoundTag(FEET_TAG);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    // we sync slot changes to all clients around
    if(this.worldObj != null  && this.worldObj instanceof WorldServer && !this.worldObj.isRemote && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot))) {
      Chunk chunk = getWorld().getChunkFromBlockCoords(getPos());
      for(EntityPlayer player : (List<EntityPlayer>) getWorld().playerEntities) {
        // only send to relevant players
        if(!(player instanceof EntityPlayerMP)) {
          continue;
        }
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        if(((WorldServer) worldObj).getPlayerManager().isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition)) {
          TinkerNetwork.sendTo(new InventorySlotSyncPacket(itemstack, slot, pos), playerMP);
        }
      }
    }
    super.setInventorySlotContents(slot, itemstack);

    if(getWorld() != null && getWorld().isRemote && Config.renderTableItems) {
      Minecraft.getMinecraft().renderGlobal.markBlockForUpdate(pos);
    }
  }
}
