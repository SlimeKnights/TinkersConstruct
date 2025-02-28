package slimeknights.tconstruct.tools.menu;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.inventory.EmptyItemHandler;
import slimeknights.mantle.inventory.SmartItemHandlerSlot;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Container for a tool inventory */
public class ToolContainerMenu extends AbstractContainerMenu {
  /** Size of a single slot */
  public static final int SLOT_SIZE = 18;
  /** Y start of the repeat slots background */
  public static final int REPEAT_BACKGROUND_START = 17;

  /** Stack containing the tool being rendered */
  @Getter
  private final ItemStack stack;
  /** Item handler being rendered */
  @Getter
  private final IItemHandler itemHandler;
  @Getter
  private final Player player;
  @Getter
  private final int slotIndex;
  @Getter
  private final boolean showOffhand;
  /** Index of the first player inventory slot */
  private final int playerInventoryStart;

  public ToolContainerMenu(int id, Inventory playerInventory, ItemStack stack, IItemHandlerModifiable itemHandler, int slotIndex) {
    this(TinkerTools.toolContainer.get(), id, playerInventory, stack, itemHandler, slotIndex);
  }

  /** Creates a new instance of this container on the client side */
  public static ToolContainerMenu forClient(int id, Inventory inventory, FriendlyByteBuf buffer) {
    int slotIndex = buffer.readVarInt();
    ItemStack stack = inventory.player.getInventory().getItem(slotIndex);
    IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).filter(cap -> cap instanceof IItemHandlerModifiable).orElse(EmptyItemHandler.INSTANCE);
    return new ToolContainerMenu(TinkerTools.toolContainer.get(), id, inventory, stack, handler, slotIndex);
  }

  protected ToolContainerMenu(@Nullable MenuType<?> type, int id, Inventory playerInventory, ItemStack stack, IItemHandler handler, int slotIndex) {
    super(type, id);
    this.stack = stack;
    this.itemHandler = handler;
    this.player = playerInventory.player;
    this.slotIndex = slotIndex;

    // add tool slots
    int slots = itemHandler.getSlots();
    for (int i = 0; i < slots; i++) {
      this.addSlot(new ToolContainerSlot(itemHandler, i, 8 + (i % 9) * SLOT_SIZE, (REPEAT_BACKGROUND_START + 1) + (i / 9) * SLOT_SIZE));
    }
    // add offhand if requested
    this.showOffhand = ModifierUtil.checkVolatileFlag(stack, ToolInventoryCapability.INCLUDE_OFFHAND);
    if (this.showOffhand) {
      int x = 8 + (slots % 9) * SLOT_SIZE;
      int y = (REPEAT_BACKGROUND_START + 1) + (slots / 9) * SLOT_SIZE;
      if (slotIndex == Inventory.SLOT_OFFHAND) {
        this.addSlot(new ReadOnlySlot(playerInventory, 40, x, y));
      } else {
        this.addSlot(new Slot(playerInventory, 40, x, y));
      }
      slots++;
    }

    this.playerInventoryStart = this.slots.size();

    // add player slots
    int playerY = 32 + SLOT_SIZE * ((slots + 8) / 9);
    for(int r = 0; r < 3; ++r) {
      for(int c = 0; c < 9; ++c) {
        int index = c + r * 9 + 9;
        if (index == slotIndex) {
          this.addSlot(new ReadOnlySlot(playerInventory, index, 8 + c * 18, playerY + r * 18));
        } else {
          this.addSlot(new Slot(        playerInventory, index, 8 + c * 18, playerY + r * 18));
        }
      }
    }
    int hotbarStart = playerY + 58;
    for(int c = 0; c < 9; ++c) {
      if (c == slotIndex) {
        this.addSlot(new ReadOnlySlot(playerInventory, c, 8 + c * 18, hotbarStart));
      } else {
        this.addSlot(new Slot(        playerInventory, c, 8 + c * 18, hotbarStart));
      }
    }
  }

  @Override
  public boolean stillValid(Player playerIn) {
    // if the stack ever leaves the slot, close the menu, as we have no way to recover then and dupes are likely
    return player == playerIn && !stack.isEmpty() && player.getInventory().getItem(slotIndex) == stack;
  }

  @Override
  public ItemStack quickMoveStack(Player playerIn, int index) {
    if (this.playerInventoryStart < 0) {
      return ItemStack.EMPTY;
    }
    ItemStack result = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);
    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      result = slotStack.copy();
      int end = this.slots.size();
      if (index < this.playerInventoryStart) {
        if (!this.moveItemStackTo(slotStack, this.playerInventoryStart, end, true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveItemStackTo(slotStack, 0, this.playerInventoryStart, false)) {
        return ItemStack.EMPTY;
      }
      if (slotStack.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }
    return result;
  }

  private static class ToolContainerSlot extends SmartItemHandlerSlot {

    private final int index;

    public ToolContainerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
      this.index = index;
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
}
