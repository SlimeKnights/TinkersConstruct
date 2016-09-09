package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Set;

import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.IModifyable;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.tools.common.client.GuiToolStation;
import slimeknights.tconstruct.tools.common.network.ToolStationSelectionPacket;
import slimeknights.tconstruct.tools.common.network.ToolStationTextPacket;
import slimeknights.tconstruct.tools.common.tileentity.TileToolStation;

// also tool forge
public class ContainerToolStation extends ContainerTinkerStation<TileToolStation> {

  protected SlotToolStationOut out;
  protected ToolCore selectedTool; // needed for newly opened containers to sync
  protected int activeSlots;
  public String toolName;

  public ContainerToolStation(InventoryPlayer playerInventory, TileToolStation tile) {
    super(tile);

    // input slots
    int i;
    for(i = 0; i < tile.getSizeInventory(); i++) {
      addSlotToContainer(new SlotToolStationIn(tile, i, 0, 0, this));
    }

    // output slot
    out = new SlotToolStationOut(i, 124, 38, this);
    addSlotToContainer(out);

    this.addPlayerInventory(playerInventory, 8, 84 + 8);
    onCraftMatrixChanged(null);
  }

  public ItemStack getResult() {
    return out.getStack();
  }

  @Override
  protected void syncNewContainer(EntityPlayerMP player) {
    this.activeSlots = tile.getSizeInventory();
    TinkerNetwork.sendTo(new ToolStationSelectionPacket(null, tile.getSizeInventory()), player);
  }

  @Override
  protected void syncWithOtherContainer(BaseContainer<TileToolStation> otherContainer, EntityPlayerMP player) {
    this.syncWithOtherContainer((ContainerToolStation) otherContainer, player);
  }

  protected void syncWithOtherContainer(ContainerToolStation otherContainer, EntityPlayerMP player) {
    // set same selection as other container
    this.setToolSelection(otherContainer.selectedTool, otherContainer.activeSlots);
    this.setToolName(otherContainer.toolName);
    // also send the data to the player
    TinkerNetwork.sendTo(new ToolStationSelectionPacket(otherContainer.selectedTool, otherContainer.activeSlots), player);
    if(otherContainer.toolName != null && !otherContainer.toolName.isEmpty()) {
      TinkerNetwork.sendTo(new ToolStationTextPacket(otherContainer.toolName), player);
    }
  }

  public void setToolSelection(ToolCore tool, int activeSlots) {
    if(activeSlots > tile.getSizeInventory()) {
      activeSlots = tile.getSizeInventory();
    }

    this.activeSlots = activeSlots;
    this.selectedTool = tool;

    for(int i = 0; i < tile.getSizeInventory(); i++) {
      Slot slot = inventorySlots.get(i);
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
            List<PartMaterialType> pmts = tool.getRequiredComponents();
            if(i < pmts.size()) {
              slotToolPart.setRestriction(pmts.get(i));
            }
          }
        }

        if(world.isRemote) {
          slotToolPart.updateIcon();
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
      if(name != null && !name.isEmpty()) {
        out.inventory.getStackInSlot(0).setStackDisplayName(name);
      }
      else {
        out.inventory.getStackInSlot(0).clearCustomName();
      }
    }
  }

  // update crafting - called whenever the content of an input slot changes
  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    // reset gui state
    updateGUI();
    try {
      ItemStack result;
      // 1. try repairing
      result = repairTool(false);
      // 2. try swapping tool parts
      if(result == null) {
        result = replaceToolParts(false);
      }
      // 3. try modifying
      if(result == null) {
        result = modifyTool(false);
      }
      // 4. try building a new tool
      if(result == null) {
        result = buildTool();
      }

      out.inventory.setInventorySlotContents(0, result);
      updateGUI();
    } catch(TinkerGuiException e) {
      // error ;(
      out.inventory.setInventorySlotContents(0, null);
      this.error(e.getMessage());
    }
    // sync output with other open containers on the server
    if(!this.world.isRemote) {
      WorldServer server = (WorldServer) this.world;
      for(EntityPlayer player : server.playerEntities) {
        if(player.openContainer != this && player.openContainer instanceof ContainerToolStation && this.sameGui((ContainerToolStation) player.openContainer)) {
          ((ContainerToolStation) player.openContainer).out.inventory.setInventorySlotContents(0, out.getStack());
        }
      }
    }
  }

  // Called when the crafting result is taken out of its slot
  public void onResultTaken(EntityPlayer playerIn, ItemStack stack) {
    boolean resultTaken = false;

    try {
      resultTaken = repairTool(true) != null ||
                    replaceToolParts(true) != null ||
                    modifyTool(true) != null;
    } catch(TinkerGuiException e) {
      // no error updating needed
      e.printStackTrace();
    }

    if(resultTaken) {
      updateSlotsAfterToolAction();
    }
    else {
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
      }
    }
    onCraftMatrixChanged(null);

    this.playCraftSound(playerIn);
  }

  protected void playCraftSound(EntityPlayer player) {
    Sounds.playSoundForAll(player, Sounds.saw, 0.8f, 0.8f + 0.4f * TConstruct.random.nextFloat());
  }

  private ItemStack repairTool(boolean remove) {
    ItemStack repairable = inventorySlots.get(0).getStack();

    // modifying possible?
    if(repairable == null || !(repairable.getItem() instanceof IRepairable)) {
      return null;
    }

    return ToolBuilder.tryRepairTool(getInputs(), repairable, remove);
  }

  private ItemStack replaceToolParts(boolean remove) throws TinkerGuiException {
    ItemStack tool = inventorySlots.get(0).getStack();

    if(tool == null || !(tool.getItem() instanceof TinkersItem)) {
      return null;
    }

    return ToolBuilder.tryReplaceToolParts(tool, getInputs(), remove);
  }

  private ItemStack modifyTool(boolean remove) throws TinkerGuiException {
    ItemStack modifyable = inventorySlots.get(0).getStack();

    // modifying possible?
    if(modifyable == null || !(modifyable.getItem() instanceof IModifyable)) {
      return null;
    }

    return ToolBuilder.tryModifyTool(getInputs(), modifyable, remove);
  }

  private ItemStack buildTool() {
    ItemStack[] input = new ItemStack[tile.getSizeInventory()];
    for(int i = 0; i < input.length; i++) {
      input[i] = tile.getStackInSlot(i);
    }

    return ToolBuilder.tryBuildTool(input, toolName, getBuildableTools());
  }

  protected Set<ToolCore> getBuildableTools() {
    return TinkerRegistry.getToolStationCrafting();
  }


  /**
   * Removes the tool in the input slot and fixes all stacks that have stacksize 0 after being used up.
   */
  private void updateSlotsAfterToolAction() {
// perfect, items already got removed but we still have to clean up 0-stacks and remove the tool
    tile.setInventorySlotContents(0, null); // slot where the tool was
    for(int i = 1; i < tile.getSizeInventory(); i++) {
      if(tile.getStackInSlot(i) != null && tile.getStackInSlot(i).stackSize == 0) {
        tile.setInventorySlotContents(i, null);
      }
    }
  }

  private ItemStack[] getInputs() {
    ItemStack[] input = new ItemStack[tile.getSizeInventory() - 1];
    for(int i = 1; i < tile.getSizeInventory(); i++) {
      input[i - 1] = tile.getStackInSlot(i);
    }

    return input;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot != out && super.canMergeSlot(stack, slot);
  }
}
