package slimeknights.tconstruct.tables.client.inventory.table;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

public class CraftingStationScreen extends TinkerStationScreen<CraftingStationTileEntity, TinkerStationContainer<CraftingStationTileEntity>> {

  private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

  public CraftingStationScreen(TinkerStationContainer<CraftingStationTileEntity> container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    if (this.container instanceof CraftingStationContainer) {
      CraftingStationContainer craftingStationContainer = (CraftingStationContainer) container;
      SideInventoryContainer sideInventoryContainer = container.getSubContainer(SideInventoryContainer.class);

      if (sideInventoryContainer != null) {
        ITextComponent sideInventoryName = title;

        if (sideInventoryContainer.getTileEntity() != null) {
          if (sideInventoryContainer.getTileEntity() instanceof ChestTileEntity) {
            // Fix: chests don't update their single/double chest status clientside once accessed
            //((ChestTileEntity) sideInventoryContainer.getTileEntity()).chestHandler = null;
          }

          if (sideInventoryContainer.getTileEntity() instanceof INamedContainerProvider) {
            sideInventoryName = ((INamedContainerProvider) sideInventoryContainer.getTileEntity()).getDisplayName();
          }
        }

        this.addModule(new SideInventoryScreen(this, sideInventoryContainer, playerInventory, sideInventoryName, sideInventoryContainer.getSlotCount(), sideInventoryContainer.columns));
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(CRAFTING_TABLE_GUI_TEXTURES);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
