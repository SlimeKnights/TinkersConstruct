package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

public class CraftingStationScreen extends BaseStationScreen<CraftingStationTileEntity, CraftingStationContainer> {
  private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

  public CraftingStationScreen(CraftingStationContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    SideInventoryContainer<?> sideInventoryContainer = container.getSubContainer(SideInventoryContainer.class);
    if (sideInventoryContainer != null) {
      ITextComponent sideInventoryName = title;

      TileEntity te = sideInventoryContainer.getTile();
      if (te instanceof INamedContainerProvider) {
          sideInventoryName = ((INamedContainerProvider) te).getDisplayName();
      }

      this.addModule(new SideInventoryScreen<>(this, sideInventoryContainer, playerInventory, sideInventoryName, sideInventoryContainer.getSlotCount(), sideInventoryContainer.getColumns()));
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, CRAFTING_TABLE_GUI_TEXTURES);
    super.drawGuiContainerBackgroundLayer(matrices, partialTicks, mouseX, mouseY);
  }
}
