package slimeknights.tconstruct.shared.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.client.BakedColoredItemModel;
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

    EnumFacing facing = getFacing();
    state = state.withProperty((IUnlistedProperty<EnumFacing>)BlockTable.FACING, facing);

    state = setInventoryDisplay(state);

    return state;
  }

  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();
    if(getStackInSlot(displaySlot) != null) {
      ItemStack stack = getStackInSlot(displaySlot);
      PropertyTableItem.TableItem item = getTableItem(stack, worldObj, null);
      if(item != null) {
        toDisplay.items.add(item);
      }
    }
    // add inventory if needed
    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }

  @SideOnly(Side.CLIENT)
  public static PropertyTableItem.TableItem getTableItem(ItemStack stack, World world, EntityLivingBase entity) {
    if(stack == null)
      return null;

    IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, entity);
    if(model == null || model.isBuiltInRenderer()) {
      // missing model so people don't go paranoid when their chests go missing
      model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
    }
    else {
      // take color into account
      model = new BakedColoredItemModel(stack, model);
    }

    PropertyTableItem.TableItem item = new PropertyTableItem.TableItem(model, 0,-0.46875f,0, 0.8f, (float) (Math.PI/2));
    if(stack.getItem() instanceof ItemBlock) {
      item.y = -0.3125f;
      item.s = 0.375f;
      item.r = 0;
    }
    return item;
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    // note that this sends all of the tile data. you should change this if you use additional tile data
    NBTTagCompound tag = (NBTTagCompound) getTileData().copy();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
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

  @Nonnull
  @Override
  public NBTTagCompound getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
    readFromNBT(tag);
  }

  public void setFacing(EnumFacing face) {
    getTileData().setInteger(FACE_TAG, face.getIndex());
  }

  public EnumFacing getFacing() {
    return EnumFacing.getFront(getTileData().getInteger(FACE_TAG));
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
      Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, pos, null, null, 0);
    }
  }
}
