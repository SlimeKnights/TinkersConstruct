package slimeknights.tconstruct.tables.client.inventory.table;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;
import slimeknights.tconstruct.tables.client.inventory.module.SideInventoryScreen;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

public class CraftingStationScreen extends BaseStationScreen<CraftingStationTileEntity, CraftingStationContainer> {
  private static final Identifier CRAFTING_TABLE_GUI_TEXTURES = new Identifier("textures/gui/container/crafting_table.png");

  public CraftingStationScreen(CraftingStationContainer container, PlayerInventory playerInventory, Text title) {
    super(container, playerInventory, title);

    SideInventoryContainer<?> sideInventoryContainer = container.getSubContainer(SideInventoryContainer.class);
    if (sideInventoryContainer != null) {
      Text sideInventoryName = title;

      BlockEntity te = sideInventoryContainer.getTile();
      if (te instanceof NamedScreenHandlerFactory) {
          sideInventoryName = ((NamedScreenHandlerFactory) te).getDisplayName();
      }

      this.addModule(new SideInventoryScreen<>(this, sideInventoryContainer, playerInventory, sideInventoryName, sideInventoryContainer.getSlotCount(), sideInventoryContainer.getColumns()));
    }
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, CRAFTING_TABLE_GUI_TEXTURES);
    super.drawBackground(matrices, partialTicks, mouseX, mouseY);
  }
}
