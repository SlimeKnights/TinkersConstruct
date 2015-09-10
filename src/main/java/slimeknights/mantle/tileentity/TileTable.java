package slimeknights.mantle.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import slimeknights.mantle.block.BlockTable;
import slimeknights.mantle.property.PropertyTableItem;
import slimeknights.tconstruct.library.client.model.ModelHelper;

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
      PropertyTableItem.TableItem item = new PropertyTableItem.TableItem(stackModel, 0,-0.46875f,0, 0.8f, (float) (Math.PI/2));
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
