package tconstruct.tools.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.library.modifiers.ModifyException;
import tconstruct.library.tinkering.IModifyable;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.utils.ToolBuilder;
import tconstruct.tools.client.GuiToolStation;
import tconstruct.tools.tileentity.TileToolStation;

public class ContainerToolStation extends ContainerMultiModule<TileToolStation> {

  protected SlotToolStationOut out;
  public String toolName;

  public ContainerToolStation(InventoryPlayer playerInventory, TileToolStation tile) {
    super(tile);

    // modular slots for input
    // Area: 7,18, 80x64

    // slot1 - center for repairing/main slot
    int x = 7 + 80/2 - 8;
    int y = 18 + 64/2 - 8;

    x -= 6;

    int i;
    for(i = 0; i < tile.getSizeInventory(); i++) {
      addSlotToContainer(new SlotToolStationIn(tile, i, 0, 0, this));
    }

    // output slot
    out = new SlotToolStationOut(i, 124,38, this);
    addSlotToContainer(out);

    this.addPlayerInventory(playerInventory, 8, 84 + 8);
  }

  public void setToolSelection(ToolCore tool, int activeSlots) {
    if(activeSlots > tile.getSizeInventory())
      activeSlots = tile.getSizeInventory();

    for(int i = 0; i < tile.getSizeInventory(); i++) {
      Slot slot = (Slot)inventorySlots.get(i);
      // set part info for the slot
      if(slot instanceof SlotToolStationIn) {
        SlotToolStationIn slotToolPart = (SlotToolStationIn) slot;

        slotToolPart.setRestriction(null);

        // deactivate not needed slots
        if(i >= activeSlots) {
          slotToolPart.deactivate();
        }
        // activate the other slots and set toolpart if possible
        else {
          slotToolPart.activate();
          if(tool != null) {
            PartMaterialType[] pmts = tool.requiredComponents;
            if(i < pmts.length) {
              slotToolPart.setRestriction(pmts[i]);
            }
          }
        }
      }
    }
  }

  public void setToolName(String name) {
    this.toolName = name;

    if(world.isRemote) {
      GuiScreen screen = Minecraft.getMinecraft().currentScreen;
      if(screen instanceof GuiToolStation) {
        ((GuiToolStation) screen).textField.setText(name);
      }
    }

    if(out.getHasStack()) {
      out.inventory.getStackInSlot(0).setStackDisplayName(name);
    }
  }

  // update crafting - called whenever the content of an input slot changes
  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    ItemStack result = modifyTool(false);
    if(result == null) result = buildTool();

    out.inventory.setInventorySlotContents(0, result);
  }

  // Called when the crafting result is taken out of its slot
  public void onResultTaken(EntityPlayer playerIn, ItemStack stack) {
    // modify?
    if(modifyTool(true) != null) {
      // perfect, items already got removed but we still have to clean up 0-stacks and remove the tool
      tile.setInventorySlotContents(0, null); // slot where the tool was
      for(int i = 1; i < tile.getSizeInventory(); i++) {
        if(tile.getStackInSlot(i) != null && tile.getStackInSlot(i).stackSize == 0) {
          tile.setInventorySlotContents(i, null);
        }
      }
      onCraftMatrixChanged(null);
      return;
    }

    // calculate the result again (serverside)
    ItemStack tool = buildTool();

    // we built a tool
    if(tool != null) {
      // remove 1 of each in the slots
      // it's guaranteed that each slot that has an item has used exactly 1 item to build the tool
      for(int i = 0; i < tile.getSizeInventory(); i++) {
        tile.decrStackSize(i, 1);
      }

      setToolName("");
      onCraftMatrixChanged(null);
    }
  }

  private ItemStack modifyTool(boolean remove) {
    ItemStack[] input = new ItemStack[tile.getSizeInventory()];
    for(int i = 0; i < input.length; i++) {
      input[i] = tile.getStackInSlot(i);
    }

    // modify or actual building?
    ItemStack modifyable = ((Slot)inventorySlots.get(0)).getStack();
    if(modifyable != null && modifyable.getItem() instanceof IModifyable) {
      try {
        return ToolBuilder.tryModifyTool(input, modifyable, remove);
      } catch(ModifyException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  private ItemStack buildTool() {
    ItemStack[] input = new ItemStack[tile.getSizeInventory()];
    for(int i = 0; i < input.length; i++) {
      input[i] = tile.getStackInSlot(i);
    }

    return ToolBuilder.tryBuildTool(input, toolName);
  }

  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot != out && super.canMergeSlot(stack, slot);
  }

  public boolean sameGui(ContainerToolStation otherContainer) {
    return this.tile == otherContainer.tile;
  }
}
