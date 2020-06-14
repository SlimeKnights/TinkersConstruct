package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.inventory.CraftingCustomSlot;
import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.mantle.inventory.OutSlot;
import slimeknights.tconstruct.shared.inventory.PersistentCraftingInventory;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.client.inventory.table.PartBuilderScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternSlot;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nullable;

public class PartBuilderContainer extends TinkerStationContainer<PartBuilderTileEntity> implements IContainerCraftingCustom {

  public IInventory craftResult;

  private final Slot patternSlot;
  private final Slot secondarySlot;
  private final Slot input1;
  private final Slot input2;

  private final PlayerEntity player;

  public PartBuilderContainer(int id, @Nullable PlayerInventory inv, PartBuilderTileEntity partBuilderTileEntity) {
    super(TinkerTables.partBuilderContainer.get(), id, inv, partBuilderTileEntity);

    PersistentCraftingInventory craftMatrix = new PersistentCraftingInventory(this, tile, 1, 3);
    this.craftResult = new CraftResultInventory();
    this.player = inv.player;

    this.addSlot(new CraftingCustomSlot(this, inv.player, craftMatrix, this.craftResult, 0, 106, 35));
    this.addSlot(this.secondarySlot = new OutSlot(tile, 3, 132, 35));

    // pattern slot
    this.addSlot(this.patternSlot = new PatternSlot(craftMatrix, 2, 26, 35));

    // material slots
    this.addSlot(this.input1 = new Slot(craftMatrix, 0, 48, 26));
    this.addSlot(this.input2 = new Slot(craftMatrix, 1, 48, 44));

    this.addInventorySlots();

    this.onCraftMatrixChanged(inv);
  }

  public PartBuilderContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, PartBuilderTileEntity.class));
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    this.updateResult();
  }

  // Sets the result in the output slot depending on the input!
  public void updateResult() {
    // no pattern -> no output
    if(!this.patternSlot.getHasStack() || (!this.input1.getHasStack() && !this.input2.getHasStack() && !this.secondarySlot.getHasStack())) {
      this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
      this.updateGUI();
    }
    else {
      Throwable throwable = null;
      NonNullList<ItemStack> toolPart;

      /*
      TODO FIX
      try {
        toolPart = ToolBuilder.tryBuildToolPart(this.patternSlot.getStack(), ListUtil.getListFrom(this.input1.getStack(), this.input2.getStack()), false);
        if(toolPart != null && !toolPart.get(0).isEmpty()) {
          TinkerCraftingEvent.ToolPartCraftingEvent.fireEvent(toolPart.get(0), player);
        }
      } catch(TinkerGuiException e) {
        toolPart = null;
        throwable = e;
      }*/

      toolPart = NonNullList.from(ItemStack.EMPTY, new ItemStack(Blocks.PUMPKIN));

      ItemStack secondary = this.secondarySlot.getStack();

      // got output?
      if(toolPart != null &&
        // got no secondary output or does it stack with the current one?
        (secondary.isEmpty() || toolPart.get(1).isEmpty() || ItemStack.areItemsEqual(secondary, toolPart.get(1)) && ItemStack.areItemStackTagsEqual(secondary, toolPart.get(1)))) {
        this.craftResult.setInventorySlotContents(0, toolPart.get(0));
      }
      else {
        this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
      }

      if(throwable != null) {
        error(throwable.getMessage());
      }
      else {
        updateGUI();
      }
    }
  }

  @Override
  public void onCrafting(PlayerEntity playerEntity, ItemStack output, IInventory craftMatrix) {
    NonNullList<ItemStack> toolPart = NonNullList.create();

    /*
    TODO FIX
    try {
      toolPart = ToolBuilder.tryBuildToolPart(this.patternSlot.getStack(), ListUtil.getListFrom(this.input1.getStack(), this.input2.getStack()), true);
    } catch (TinkerGuiException e) {
      // don't need any user information at this stage
    }*/
    toolPart = NonNullList.from(ItemStack.EMPTY, new ItemStack(Blocks.PUMPKIN), ItemStack.EMPTY);

    if (toolPart == null) {
      // undefined :I
      return;
    }

    ItemStack secondOutput = toolPart.get(1);
    ItemStack secondary = this.secondarySlot.getStack();

    if (secondary.isEmpty()) {
      this.putStackInSlot(this.secondarySlot.slotNumber, secondOutput);
    } else if (!secondOutput.isEmpty() && ItemStack.areItemsEqual(secondary, secondOutput) && ItemStack.areItemStackTagsEqual(secondary, secondOutput)) {
      secondary.grow(secondOutput.getCount());
    }

    this.updateResult();
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
    return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
  }
}
