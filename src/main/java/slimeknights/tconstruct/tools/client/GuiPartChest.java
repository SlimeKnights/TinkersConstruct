package slimeknights.tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.tconstruct.tools.client.module.GuiGeneric;
import slimeknights.tconstruct.tools.inventory.ContainerPartChest;
import slimeknights.tconstruct.tools.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.tileentity.TilePartChest;

@SideOnly(Side.CLIENT)
public class GuiPartChest extends GuiTinkerStation {

  protected static final GuiElementScalable background = GuiGeneric.slotEmpty;

  public GuiDynInventory guiInventory;

  public GuiPartChest(InventoryPlayer playerInv, World world, BlockPos pos, TilePartChest tile) {
    super(world, pos, (ContainerTinkerStation)tile.createContainer(playerInv, world, pos));

    // we use the sideinventory class for the inventory itself
    // it doesn't contain the player inventory
    guiInventory = new GuiDynInventory(this, container.getSubContainer(ContainerPartChest.SideInventory.class));
    addModule(guiInventory);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BLANK_BACK);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
