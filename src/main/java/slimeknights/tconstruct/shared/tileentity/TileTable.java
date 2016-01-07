package slimeknights.tconstruct.shared.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.tools.network.InventorySlotSyncPacket;

public class TileTable extends TileInventory {

  public static final String FEET_TAG = "textureBlock";
  public static final String FACE_TAG = "facing";
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

    EnumFacing facing = EnumFacing.getFront(getTileData().getInteger(FACE_TAG));
    state = state.withProperty((IUnlistedProperty<EnumFacing>)BlockTable.FACING, facing);

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

  @SideOnly(Side.CLIENT)
  public static PropertyTableItem.TableItem getTableItem(ItemStack stack) {
    if(stack == null)
      return null;
    IFlexibleBakedModel stackModel;
    IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
    if(model == null) {
      return null;
    }
    else if(model instanceof IFlexibleBakedModel) {
      stackModel = (IFlexibleBakedModel) model;
    }
    else {
      stackModel = new IFlexibleBakedModel.Wrapper(model, DefaultVertexFormats.ITEM);
    }

    PropertyTableItem.TableItem item = new PropertyTableItem.TableItem(stackModel, 0,-0.46875f,0, 0.8f, (float) (-Math.PI/2));
    if(stack.getItem() instanceof  ItemBlock) {
      item.y = -0.3125f;
      item.s = 0.375f;
      item.r = 0;
    }
    return item;
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
    NBTBase feet = tag.getTag(FEET_TAG);
    if(feet != null) {
      getTileData().setTag(FEET_TAG, feet);
    }
    NBTBase facing = tag.getTag(FACE_TAG);
    if(facing != null) {
      getTileData().setTag(FACE_TAG, facing);
    }
    readFromNBT(tag);
  }

  public void setFacing(EnumFacing face) {
    getTileData().setInteger(FACE_TAG, face.getIndex());
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
      TinkerNetwork.sendToClients((WorldServer) this.worldObj, this.pos, new InventorySlotSyncPacket(itemstack, slot, pos));
    }
    super.setInventorySlotContents(slot, itemstack);

    if(getWorld() != null && getWorld().isRemote && Config.renderTableItems) {
      Minecraft.getMinecraft().renderGlobal.markBlockForUpdate(pos);
    }
  }
}
