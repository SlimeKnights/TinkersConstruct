package slimeknights.tconstruct.tools.menu;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferDirection;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferResult;
import slimeknights.mantle.inventory.EmptyItemHandler;
import slimeknights.mantle.inventory.SmartItemHandlerSlot;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.fluid.SimpleFluidTank;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.network.ToolContainerFluidUpdatePacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Container for a tool inventory */
public class ToolContainerMenu extends AbstractContainerMenu {
  /** Size of a single slot */
  public static final int SLOT_SIZE = 18;
  /** Y start of the repeat slots background */
  public static final int TITLE_SIZE = 13;
  /** Y start of the repeat slots background */
  public static final int UI_START = 4;
  /** Max number of rows in the repeat slots background */
  public static final int REPEAT_BACKGROUND_START = UI_START + TITLE_SIZE;

  /** Stack containing the tool being rendered */
  @Getter
  private final ItemStack stack;
  /** Tool hosting this tank */
  @Getter
  private final IToolStackView tool;
  /** Item handler being rendered */
  @Getter
  private final IItemHandler itemHandler;
  /** Tank in the tool */
  @Getter
  private final SimpleFluidTank tank;
  @Getter
  private final Player player;
  @Getter
  private final int slotIndex;
  @Getter
  private final boolean showOffhand;
  @Nullable
  private final CraftingContainer craftingContainer;
  @Nullable
  private final ResultContainer resultContainer;
  /** Start index of the tool slots */
  @Getter
  private final int toolInventoryStart;
  /** Index of the first player inventory slot */
  @Getter
  private final int playerInventoryStart;

  public ToolContainerMenu(int id, Inventory playerInventory, ItemStack stack, IItemHandler itemHandler, int slotIndex) {
    this(TinkerTools.toolContainer.get(), id, playerInventory, stack, itemHandler, slotIndex);
  }

  /** Creates a new instance of this container on the client side */
  public static ToolContainerMenu forClient(int id, Inventory inventory, FriendlyByteBuf buffer) {
    int slotIndex = buffer.readVarInt();
    ItemStack stack = buffer.readItem();
    inventory.setItem(slotIndex, stack);
    IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).filter(cap -> cap instanceof IItemHandlerModifiable).orElse(EmptyItemHandler.INSTANCE);
    return new ToolContainerMenu(TinkerTools.toolContainer.get(), id, inventory, stack, handler, slotIndex);
  }

  protected ToolContainerMenu(@Nullable MenuType<?> type, int id, Inventory playerInventory, ItemStack stack, IItemHandler handler, int slotIndex) {
    super(type, id);
    this.stack = stack;
    this.tool = ToolStack.from(stack);
    this.itemHandler = handler;
    this.player = playerInventory.player;
    this.tank = new ToolFluidHandler(tool, player.level().isClientSide ? null : player);
    this.slotIndex = slotIndex;

    // if requested, add 3x3 crafting area
    int slots = itemHandler.getSlots();
    int craftingOffset = (slots == 0 ? REPEAT_BACKGROUND_START : UI_START) + 1;
    if (ModifierUtil.checkVolatileFlag(stack, ToolInventoryCapability.CRAFTING_TABLE)) {
      this.craftingContainer = new TransientCraftingContainer(this, 3, 3);
      this.resultContainer = new ResultContainer();
      this.addSlot(new ResultSlot(this.player, this.craftingContainer, resultContainer, 0, 124, craftingOffset + 18));
      for (int r = 0; r < 3; ++r) {
        for (int c = 0; c < 3; ++c) {
          this.addSlot(new Slot(this.craftingContainer, c + r * 3, 30 + c * 18, craftingOffset + r * 18));
        }
      }
      // if no 3x3, check if 2x2 was requested
    } else if (ModifierUtil.checkVolatileFlag(stack, ToolInventoryCapability.INVENTORY_CRAFTING)) {
      this.craftingContainer = new TransientCraftingContainer(this, 2, 2);
      this.resultContainer = new ResultContainer();
      this.addSlot(new ResultSlot(this.player, this.craftingContainer, resultContainer, 0, 108, craftingOffset + 10));
      for(int r = 0; r < 2; ++r) {
        for(int c = 0; c < 2; ++c) {
          this.addSlot(new Slot(this.craftingContainer, c + r * 2, 52 + c * 18, craftingOffset + r * 18));
        }
      }
    } else {
      this.craftingContainer = null;
      this.resultContainer = null;
    }
    this.toolInventoryStart = this.slots.size();

    // add tool slots
    int yOffset = REPEAT_BACKGROUND_START + getCraftingHeight() * SLOT_SIZE + 1;
    for (int i = 0; i < slots; i++) {
      this.addSlot(new ToolContainerSlot(itemHandler, i, 8 + (i % 9) * SLOT_SIZE, yOffset + (i / 9) * SLOT_SIZE));
    }
    // add offhand if requested
    this.showOffhand = ModifierUtil.checkVolatileFlag(stack, ToolInventoryCapability.INCLUDE_OFFHAND);
    if (this.showOffhand) {
      int x = 8 + (slots % 9) * SLOT_SIZE;
      int y = yOffset + (slots / 9) * SLOT_SIZE;
      if (slotIndex == Inventory.SLOT_OFFHAND) {
        this.addSlot(new ReadOnlySlot(playerInventory, 40, x, y));
      } else {
        this.addSlot(new Slot(playerInventory, 40, x, y));
      }
      slots++;
    }

    this.playerInventoryStart = this.slots.size();

    // add player slots
    yOffset += TITLE_SIZE + ((slots + 8) / 9) * SLOT_SIZE;
    if (tank.getCapacity() > 0) {
      yOffset += 14;
    }
    for(int r = 0; r < 3; ++r) {
      for(int c = 0; c < 9; ++c) {
        int index = c + r * 9 + 9;
        if (index == slotIndex) {
          this.addSlot(new ReadOnlySlot(playerInventory, index, 8 + c * 18, yOffset + r * 18));
        } else {
          this.addSlot(new Slot(        playerInventory, index, 8 + c * 18, yOffset + r * 18));
        }
      }
    }
    yOffset += 58; // 3 slots + 4 pixel divider
    for(int c = 0; c < 9; ++c) {
      if (c == slotIndex) {
        this.addSlot(new ReadOnlySlot(playerInventory, c, 8 + c * 18, yOffset));
      } else {
        this.addSlot(new Slot(        playerInventory, c, 8 + c * 18, yOffset));
      }
    }
  }

  /** Gets the height of the crafting area in slots */
  public int getCraftingHeight() {
    return craftingContainer != null ? craftingContainer.getHeight() : 0;
  }

  @Override
  public boolean stillValid(Player playerIn) {
    // if the stack ever leaves the slot, close the menu, as we have no way to recover then and dupes are likely
    return player == playerIn && !stack.isEmpty() && player.getInventory().getItem(slotIndex) == stack;
  }

  @Override
  public ItemStack quickMoveStack(Player playerIn, int index) {
    ItemStack result = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);
    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      result = slotStack.copy();
      int end = this.slots.size();
      // if its in a crafting slot, move it anywhere
      if (index < toolInventoryStart) {
        if (!this.moveItemStackTo(slotStack, toolInventoryStart, end, true)) {
          return ItemStack.EMPTY;
        }
        if (index == 0) {
          slot.onQuickCraft(slotStack, result);
        }
        // if its in the tool inventory, move to player inventory
      } else if (index < playerInventoryStart) {
        if (!this.moveItemStackTo(slotStack, playerInventoryStart, end, true)) {
          return ItemStack.EMPTY;
        }
        // if its in the player inventory, move to the tool inventory
      } else if (!this.moveItemStackTo(slotStack, toolInventoryStart, playerInventoryStart, false)) {
        return ItemStack.EMPTY;
      }
      // if we moved the whole stack, clear the slot
      if (slotStack.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
      // if we moved nothing, give up
      if (slotStack.getCount() == result.getCount()) {
        return ItemStack.EMPTY;
      }
      // drop the crafted result if it didn't entirely move
      slot.onTake(player, slotStack);
      if (toolInventoryStart > 0 && index == 0) {
        player.drop(slotStack, false);
      }

    }
    return result;
  }

  @Override
  public void slotsChanged(Container pContainer) {
    super.slotsChanged(pContainer);
    if (craftingContainer != null && resultContainer != null) {
      CraftingMenu.slotChangedCraftingGrid(this, player.level(), player, craftingContainer, resultContainer);
    }
  }

  @Override
  public void removed(Player pPlayer) {
    super.removed(pPlayer);
    if (resultContainer != null) {
      resultContainer.clearContent();
    }
    if (craftingContainer != null) {
      clearContainer(pPlayer, craftingContainer);
    }
  }

  @Override
  public boolean clickMenuButton(Player player, int id) {
    ItemStack held = getCarried();
    if ((id == 0 || id == 1) && !held.isEmpty() && !player.isSpectator()) {
      if (!player.level().isClientSide) {
        TransferResult result = FluidTransferHelper.interactWithStack(tank, held, id == 0 ? TransferDirection.FILL_ITEM : TransferDirection.EMPTY_ITEM);
        setCarried(FluidTransferHelper.handleUIResult(player, held, result));
      }
      return true;
    }
    return false;
  }

  private static class ToolContainerSlot extends SmartItemHandlerSlot {
    private final int index;

    public ToolContainerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
      this.index = index;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
      return true;
    }

    @Override
    public void set(@Nonnull ItemStack stack) {
      // using set as an indicator it changed, so no need to call setChanged anymore here
      ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, stack);
    }

    @Override
    public void setChanged() {
      // no proper setChanged method on item handler, so just set the existing stack
      set(getItem());
    }
  }

  /** Logic handling the fluid tank in the UI */
  private record ToolFluidHandler(IToolStackView tool, @Nullable Player player) implements SimpleFluidTank {
    @Nonnull
    @Override
    public FluidStack getFluid() {
      return ToolTankHelper.TANK_HELPER.getFluid(tool);
    }

    @Override
    public void setFluid(FluidStack fluid) {
      ToolTankHelper.TANK_HELPER.setFluid(tool, fluid);
    }

    @Override
    public void updateFluid(FluidStack updated, int change) {
      if (change != 0) {
        setFluid(updated);
        if (player != null) {
          TinkerNetwork.getInstance().sendTo(new ToolContainerFluidUpdatePacket(updated), player);
        }
      }
    }

    @Override
    public int getCapacity() {
      return ToolTankHelper.TANK_HELPER.getCapacity(tool);
    }
  }
}
