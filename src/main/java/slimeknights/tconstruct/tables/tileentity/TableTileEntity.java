package slimeknights.tconstruct.tables.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.util.CombinedModelData;
import slimeknights.tconstruct.library.client.util.SinglePropertyModelData;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.client.model.ModelProperties;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TableTileEntity extends InventoryTileEntity {

  public static final String FEET_TAG = "textureBlock";

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn, new TranslationTextComponent(""), 0, 0);
  }

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize) {
    super(tileEntityTypeIn, new TranslationTextComponent(name), inventorySize);
  }

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, new TranslationTextComponent(name), inventorySize, maxStackSize);
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return null;
  }

  public boolean isInventoryEmpty() {
    for (int i = 0; i < this.getSizeInventory(); ++i) {
      if (!this.getStackInSlot(i).isEmpty()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT tag = this.getTileData().copy();

    this.write(tag);

    return new SUpdateTileEntityPacket(this.getPos(), -9999, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    CompoundNBT tag = pkt.getNbtCompound();
    INBT feet = tag.get(FEET_TAG);

    if (feet != null) {
      this.getTileData().put(FEET_TAG, feet);
    }

    this.read(tag);
    this.requestModelDataUpdate();
  }

  @Override
  public void requestModelDataUpdate() {
    updateModelData();
    super.requestModelDataUpdate();
  }

  protected void updateModelData() {

  }

  @Nonnull
  @Override
  public IModelData getModelData() {
    return new CombinedModelData(new SinglePropertyModelData<>(this.getTextureBlock(), ModelProperties.TEXTURE));
  }

  @Nonnull
  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public void handleUpdateTag(@Nonnull CompoundNBT tag) {
    this.read(tag);
  }

  public void updateTextureBlock(CompoundNBT tag) {
    this.getTileData().put(FEET_TAG, tag);
  }

  public CompoundNBT getTextureBlock() {
    return this.getTileData().getCompound(FEET_TAG);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
    if (this.getWorld() != null && this.getWorld() instanceof ServerWorld && !this.getWorld().isRemote && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot))) {
      TinkerNetwork.getInstance().sendToClientsAround(new InventorySlotSyncPacket(itemstack, slot, pos), (ServerWorld) this.getWorld(), this.pos);
    }

    super.setInventorySlotContents(slot, itemstack);

    if (this.getWorld() != null && this.getWorld().isRemote && Config.CLIENT.renderTableItems.get()) {
      Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, pos, null, null, 0);
    }
  }
}
