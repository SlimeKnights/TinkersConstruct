package slimeknights.tconstruct.tables.client.inventory.table;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

public class CraftingStationScreen extends BaseStationScreen<CraftingStationTileEntity, CraftingStationContainer> {
  private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

  public CraftingStationScreen(CraftingStationContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);
    addChestSideInventory();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, CRAFTING_TABLE_GUI_TEXTURES);
    super.drawGuiContainerBackgroundLayer(matrices, partialTicks, mouseX, mouseY);
  }
}
