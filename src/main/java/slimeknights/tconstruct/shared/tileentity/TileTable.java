package slimeknights.tconstruct.shared.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

public class TileTable extends TileInventory implements ILootContainer {

  public static final String FEET_TAG = "textureBlock";
  public static final String FACE_TAG = "facing";
  protected int displaySlot = 0;

  protected ResourceLocation lootTable;
  protected long lootTableSeed;

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
    if(texture.isEmpty()) {
      // load it from saved block
      ItemStack stack = new ItemStack(getTileData().getCompoundTag(FEET_TAG));
      if(!stack.isEmpty()) {
        Block block = Block.getBlockFromItem(stack.getItem());
        texture = ModelHelper.getTextureFromBlock(block, stack.getItemDamage()).getIconName();
        getTileData().setString("texture", texture);
      }
    }

    if(!texture.isEmpty()) {
      state = state.withProperty(BlockTable.TEXTURE, texture);
    }

    EnumFacing facing = getFacing();
    state = state.withProperty((IUnlistedProperty<EnumFacing>) BlockTable.FACING, facing);

    state = setInventoryDisplay(state);

    return state;
  }

  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();
    if(!getStackInSlot(displaySlot).isEmpty()) {
      ItemStack stack = getStackInSlot(displaySlot);
      PropertyTableItem.TableItem item = getTableItem(stack, getWorld(), null);
      if(item != null) {
        toDisplay.items.add(item);
      }
    }
    // add inventory if needed
    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }

  public boolean isInventoryEmpty() {
    for(int i = 0; i < this.getSizeInventory(); ++i) {
      if(!getStackInSlot(i).isEmpty()) {
        return false;
      }
    }

    return true;
  }

  @SideOnly(Side.CLIENT)
  public static PropertyTableItem.TableItem getTableItem(ItemStack stack, World world, EntityLivingBase entity) {
    if(stack.isEmpty()) {
      return null;
    }

    if(!Config.renderTableItems) {
      return new PropertyTableItem.TableItem(stack, null);
    }

    IBakedModel model = ModelHelper.getBakedModelForItem(stack, world, entity);

    PropertyTableItem.TableItem item = new PropertyTableItem.TableItem(stack, model, 0, -0.46875f, 0, 0.8f, (float) (Math.PI / 2));
    if(stack.getItem() instanceof ItemBlock) {
      if(!(Block.getBlockFromItem(stack.getItem()) instanceof BlockPane)) {
        item.y = -0.3125f;
        item.r = 0;
      }
      item.s = 0.375f;
    }
    return item;
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    // note that this sends all of the tile data. you should change this if you use additional tile data
    NBTTagCompound tag = getTileData().copy();
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

  // Loot Tables Start

  protected boolean checkLootTableAndRead(NBTTagCompound compound) {
    if(compound.hasKey("LootTable", 8)) {
      this.lootTable = new ResourceLocation(compound.getString("LootTable"));
      this.lootTableSeed = compound.getLong("LootTableSeed");
      return true;
    }
    else {
      return false;
    }
  }

  protected boolean checkLootTableAndWrite(NBTTagCompound compound) {
    if(this.lootTable != null) {
      compound.setString("LootTable", this.lootTable.toString());

      if(this.lootTableSeed != 0L) {
        compound.setLong("LootTableSeed", this.lootTableSeed);
      }

      return true;
    }
    else {
      return false;
    }
  }

  public void fillWithLootFromTable(@Nullable EntityPlayer player) {
    if(this.lootTable != null && this.getWorld() instanceof WorldServer) {
      LootTable loottable = this.getWorld().getLootTableManager().getLootTableFromLocation(this.lootTable);
      this.lootTable = null;
      Random random;

      if(this.lootTableSeed == 0L) {
        random = new Random();
      }
      else {
        random = new Random(this.lootTableSeed);
      }

      Builder builder = new Builder((WorldServer) this.getWorld());

      if(player != null) {
        builder.withLuck(player.getLuck());
      }

      loottable.fillInventory(this, random, builder.build());
    }
  }

  @Override
  public ResourceLocation getLootTable() {
    return this.lootTable;
  }

  public void setLootTable(ResourceLocation lootTableIn, long lootTableSeedIn) {
    this.lootTable = lootTableIn;
    this.lootTableSeed = lootTableSeedIn;
  }

  @Override
  public void writeInventoryToNBT(NBTTagCompound tag) {
    if(!this.checkLootTableAndWrite(tag)) {
      super.writeInventoryToNBT(tag);
    }
  }

  @Override
  public void readInventoryFromNBT(NBTTagCompound tag) {
    if(!this.checkLootTableAndRead(tag)) {
      super.readInventoryFromNBT(tag);
    }
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int slot) {
    this.fillWithLootFromTable(null);

    return super.getStackInSlot(slot);
  }

  @Override
  public ItemStack decrStackSize(int slot, int quantity) {
    this.fillWithLootFromTable(null);

    return super.decrStackSize(slot, quantity);
  }

  @Nonnull
  @Override
  public ItemStack removeStackFromSlot(int slot) {
    this.fillWithLootFromTable(null);

    return super.removeStackFromSlot(slot);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
    // Ensure no one spawns a pattern chest with random items in it like iron etc.
    if(isItemValidForSlot(slot, itemstack)) {
      this.fillWithLootFromTable(null);

      // we sync slot changes to all clients around
      if(getWorld() != null && getWorld() instanceof WorldServer && !getWorld().isRemote && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot))) {
        TinkerNetwork.sendToClients((WorldServer) getWorld(), this.pos, new InventorySlotSyncPacket(itemstack, slot, pos));
      }
      super.setInventorySlotContents(slot, itemstack);

      if(getWorld() != null && getWorld().isRemote && Config.renderTableItems) {
        Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, pos, null, null, 0);
      }
    }
  }

  @Override
  public void clear() {
    this.fillWithLootFromTable(null);

    super.clear();
  }
}
