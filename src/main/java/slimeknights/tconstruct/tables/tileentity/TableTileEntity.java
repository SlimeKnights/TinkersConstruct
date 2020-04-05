package slimeknights.tconstruct.tables.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerNBTConstants;
import slimeknights.tconstruct.library.client.util.CombinedModelData;
import slimeknights.tconstruct.library.client.util.SinglePropertyModelData;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.client.model.ModelProperties;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TableTileEntity extends InventoryTileEntity {

  private CompoundNBT legTexture;

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn, new TranslationTextComponent(""), 0, 0);
  }

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize) {
    super(tileEntityTypeIn, new TranslationTextComponent(name), inventorySize);
  }

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, new TranslationTextComponent(name), inventorySize, maxStackSize);
  }

  @Override
  public void read(CompoundNBT tag) {
    super.read(tag);

    this.legTexture = tag.getCompound(TinkerNBTConstants.LEG_TEXTURE);
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tags) {
    super.write(tags);

    tags.put(TinkerNBTConstants.LEG_TEXTURE, this.legTexture);

    return tags;
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

    return new SUpdateTileEntityPacket(this.getPos(), 0, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    CompoundNBT tag = pkt.getNbtCompound();

    this.read(tag);
    this.requestModelDataUpdate();
  }

  @Override
  public void requestModelDataUpdate() {
    this.updateModelData();
    super.requestModelDataUpdate();
  }

  protected void updateModelData() {

  }

  @Nonnull
  @Override
  public IModelData getModelData() {
    return new CombinedModelData(new SinglePropertyModelData<>(this.getLegTexture(), ModelProperties.TEXTURE));
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

  public void setLegTexture(CompoundNBT tag) {
    this.legTexture = tag;
  }

  public CompoundNBT getLegTexture() {
    return this.legTexture;
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
