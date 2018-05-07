package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.mantle.inventory.SlotCraftingCustom;
import slimeknights.mantle.inventory.SlotOut;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.utils.ListUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.shared.inventory.InventoryCraftingPersistent;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;
import slimeknights.tconstruct.tools.common.client.GuiPartBuilder;
import slimeknights.tconstruct.tools.common.tileentity.TilePartBuilder;
import slimeknights.tconstruct.tools.common.tileentity.TilePatternChest;

public class ContainerPartBuilder extends ContainerTinkerStation<TilePartBuilder> implements IContainerCraftingCustom {

  public IInventory craftResult;
  //public IInventory craftResultSecondary;

  private final Slot patternSlot;
  private final Slot secondarySlot;
  private final Slot input1;
  private final Slot input2;

  private final boolean partCrafter;
  private final EntityPlayer player;
  public final IInventory patternChest;

  public ContainerPartBuilder(InventoryPlayer playerInventory, TilePartBuilder tile) {
    super(tile);

    InventoryCraftingPersistent craftMatrix = new InventoryCraftingPersistent(this, tile, 1, 3);
    this.craftResult = new InventoryCraftResult();
    this.player = playerInventory.player;
    //InventoryCrafting craftMatrixSecondary = new InventoryCrafting(this, 1, 1);
    //this.craftResultSecondary = new InventoryCrafting(this, 1, 1);

    // output slots
    this.addSlotToContainer(new SlotCraftingCustom(this, playerInventory.player, craftMatrix, craftResult, 0, 106, 35));
    this.addSlotToContainer(secondarySlot = new SlotOut(tile, 3, 132, 35));


    // pattern slot
    this.addSlotToContainer(patternSlot = new SlotStencil(craftMatrix, 2, 26, 35, false));

    // material slots
    this.addSlotToContainer(input1 = new Slot(craftMatrix, 0, 48, 26));
    this.addSlotToContainer(input2 = new Slot(craftMatrix, 1, 48, 44));

    TilePatternChest chest = detectTE(TilePatternChest.class);
    // TE present?
    if(chest != null) {
      // crafting station and stencil table also present?
      boolean hasCraftingStation = false;
      boolean hasStencilTable = false;
      for(Pair<BlockPos, IBlockState> pair : tinkerStationBlocks) {
        if(!pair.getRight().getProperties().containsKey(BlockToolTable.TABLES)) {
          continue;
        }

        BlockToolTable.TableTypes type = pair.getRight().getValue(BlockToolTable.TABLES);
        if(type != null) {
          if(type == BlockToolTable.TableTypes.CraftingStation) {
            hasCraftingStation = true;
          }
          else if(type == BlockToolTable.TableTypes.StencilTable) {
            hasStencilTable = true;
          }
        }
      }

      // are we a PartCrafter?
      partCrafter = hasStencilTable && hasCraftingStation;

      Container sideInventory = new ContainerPatternChest.DynamicChestInventory(chest, chest, -6, 8, 6);
      addSubContainer(sideInventory, true);

      patternChest = chest;
    }
    else {
      partCrafter = false;
      patternChest = null;
    }

    this.addPlayerInventory(playerInventory, 8, 84);

    onCraftMatrixChanged(playerInventory);
  }

  public boolean isPartCrafter() {
    return partCrafter;
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    updateResult();
  }

  // Sets the result in the output slot depending on the input!
  public void updateResult() {
    // no pattern -> no output
    if(!patternSlot.getHasStack() || (!input1.getHasStack() && !input2.getHasStack() && !secondarySlot.getHasStack())) {
      craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
      updateGUI();
    }
    else {
      Throwable throwable = null;
      NonNullList<ItemStack> toolPart;
      try {
        toolPart = ToolBuilder.tryBuildToolPart(patternSlot.getStack(), ListUtil.getListFrom(input1.getStack(), input2.getStack()), false);
        if(toolPart != null && !toolPart.get(0).isEmpty()) {
          TinkerCraftingEvent.ToolPartCraftingEvent.fireEvent(toolPart.get(0), player);
        }
      } catch(TinkerGuiException e) {
        toolPart = null;
        throwable = e;
      }

      ItemStack secondary = secondarySlot.getStack();

      // got output?
      if(toolPart != null &&
         // got no secondary output or does it stack with the current one?
         (secondary.isEmpty() || toolPart.get(1).isEmpty() || ItemStack.areItemsEqual(secondary, toolPart.get(1)) && ItemStack.areItemStackTagsEqual(secondary, toolPart.get(1)))) {
        craftResult.setInventorySlotContents(0, toolPart.get(0));
      }
      else {
        craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
      }

      if(throwable != null) {
        error(throwable.getMessage());
      }
      else {
        updateGUI();
      }
    }
  }

  /** Looks for a pattern that matches the given one in the PatternChest and exchanges it with the pattern slot */
  public void setPattern(ItemStack wanted) {
    if(patternChest == null) {
      return;
    }

    // check chest contents for wanted
    for(int i = 0; i < patternChest.getSizeInventory(); i++) {
      if(ItemStack.areItemStacksEqual(wanted, patternChest.getStackInSlot(i))) {
        // found it! exchange it with the pattern slot!
        ItemStack slotStack = patternSlot.getStack();
        patternSlot.putStack(patternChest.getStackInSlot(i));
        patternChest.setInventorySlotContents(i, slotStack);
        break;
      }
    }
  }

  @Override
  public void onCrafting(EntityPlayer player, ItemStack output, IInventory craftMatrix) {
    NonNullList<ItemStack> toolPart = NonNullList.create();
    try {
      toolPart = ToolBuilder.tryBuildToolPart(patternSlot.getStack(), ListUtil.getListFrom(input1.getStack(), input2.getStack()), true);
    } catch(TinkerGuiException e) {
      // don't need any user information at this stage
    }
    if(toolPart == null) {
      // undefined :I
      return;
    }

  ItemStack secondOutput = toolPart.get(1);
    ItemStack secondary = secondarySlot.getStack();
    if(secondary.isEmpty()) {
      putStackInSlot(secondarySlot.slotNumber, secondOutput);
    }
    else if(!secondOutput.isEmpty() && ItemStack.areItemsEqual(secondary, secondOutput) && ItemStack.areItemStackTagsEqual(secondary, secondOutput)) {
      secondary.grow(secondOutput.getCount());
    }

    // clean up 0 size stacks
    // todo: this shouldn't be needed anymore, check
    /*for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
      if(craftMatrix.getStackInSlot(i).isEmpty() != null && craftMatrix.getStackInSlot(i).stackSize == 0) {
        craftMatrix.setInventorySlotContents(i, null);
      }
    }*/

    updateResult();
  }

  @Override
  public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
    // prevents that doubleclicking on an item pulls the same out of the crafting slot
    return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
  }

  @Override
  public String getInventoryDisplayName() {
    if(partCrafter) {
      return Util.translate("gui.partcrafter.name");
    }

    return super.getInventoryDisplayName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void putStackInSlot(int p_75141_1_, @Nonnull ItemStack p_75141_2_) {
    super.putStackInSlot(p_75141_1_, p_75141_2_);

    // this is called solely to update the gui buttons
    Minecraft mc = Minecraft.getMinecraft();
    if(mc.currentScreen instanceof GuiPartBuilder) {
      ((GuiPartBuilder) mc.currentScreen).updateButtons();
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemStack slotClick(int slotId, int dragType, ClickType type, EntityPlayer player) {
    ItemStack ret = super.slotClick(slotId, dragType, type, player);
    // this is called solely to update the gui buttons
    Minecraft mc = Minecraft.getMinecraft();
    if(mc.currentScreen instanceof GuiPartBuilder) {
      ((GuiPartBuilder) mc.currentScreen).updateButtons();
    }

    return ret;
  }
}
