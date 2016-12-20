package slimeknights.tconstruct.tools.common.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.common.client.module.GuiGeneric;
import slimeknights.tconstruct.tools.common.inventory.ContainerPatternChest;
import slimeknights.tconstruct.tools.common.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.common.tileentity.TilePatternChest;

@SideOnly(Side.CLIENT)
public class GuiPatternChest extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/blank.png");

  protected static final GuiElementScalable background = GuiGeneric.slotEmpty;

  public GuiScalingChest guiInventory;

  public GuiPatternChest(InventoryPlayer playerInv, World world, BlockPos pos, TilePatternChest tile) {
    super(world, pos, (ContainerTinkerStation<TilePatternChest>) tile.createContainer(playerInv, world, pos));

    // we use the sideinventory class for the inventory itself
    // it doesn't contain the player inventory
    guiInventory = new GuiScalingChest(this, container.getSubContainer(ContainerPatternChest.DynamicChestInventory.class));
    addModule(guiInventory);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    guiInventory.update(mouseX, mouseY);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
