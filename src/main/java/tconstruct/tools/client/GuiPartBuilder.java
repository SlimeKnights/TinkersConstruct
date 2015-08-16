package tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.library.Util;
import tconstruct.tools.client.module.GuiButtonsPartCrafter;
import tconstruct.tools.client.module.GuiSideInventory;
import tconstruct.tools.inventory.ContainerPartBuilder;
import tconstruct.tools.inventory.ContainerPatternChest;
import tconstruct.tools.inventory.ContainerSideInventory;
import tconstruct.tools.inventory.ContainerTinkerStation;
import tconstruct.tools.tileentity.TilePartBuilder;

@SideOnly(Side.CLIENT)
public class GuiPartBuilder extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  public static final int Column_Count = 4;

  protected GuiButtonsPartCrafter buttons;

  public GuiPartBuilder(InventoryPlayer playerInv, World world, BlockPos pos, TilePartBuilder tile) {
    super(world, pos, (ContainerTinkerStation) tile.createContainer(playerInv, world, pos));

    if(inventorySlots instanceof ContainerPartBuilder) {
      ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;

      // has part crafter buttons?
      if(container.isPartCrafter()) {
        buttons = new GuiButtonsPartCrafter(this, container, container.patternChest);
        this.addModule(buttons);
      }
      else {
        // has pattern chest inventory?
        ContainerSideInventory chestContainer = container.getSubContainer(ContainerPatternChest.SideInventory.class);
        if(chestContainer != null) {
          this.addModule(new GuiSideInventory(this, chestContainer, chestContainer
              .getSlotCount(), chestContainer.columns));
        }
      }
    }
  }

  @Override
  public void drawSlot(Slot slotIn) {
    if(inventorySlots instanceof ContainerPartBuilder) {
      ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
      if(container.isPartCrafter() && slotIn.inventory == container.patternChest)
        return;
    }

    super.drawSlot(slotIn);
  }

  @Override
  public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
    if(inventorySlots instanceof ContainerPartBuilder) {
      ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
      if(container.isPartCrafter() && slotIn.inventory == container.patternChest)
        return false;
    }
    return super.isMouseOverSlot(slotIn, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    // draw slot icons
    drawIconEmpty(container.getSlot(1), ICON_Shard);
    drawIconEmpty(container.getSlot(2), ICON_Pattern);
    drawIconEmpty(container.getSlot(3), ICON_Ingot);
    drawIconEmpty(container.getSlot(4), ICON_Block);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
