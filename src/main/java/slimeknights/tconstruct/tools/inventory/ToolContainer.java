package slimeknights.tconstruct.tools.inventory;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.inventory.EmptyItemHandler;
import slimeknights.mantle.inventory.ItemHandlerSlot;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;

/** Container for a tool inventory */
public class ToolContainer extends Container {
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
  private final PlayerEntity player;
  @Getter
  private final int selectedHotbarSlot;
  @Getter
  private final boolean showOffhand;

  public ToolContainer(int id, PlayerInventory playerInventory, ItemStack stack, IItemHandlerModifiable itemHandler, EquipmentSlotType slotType) {
    this(TinkerTools.toolContainer.get(), id, playerInventory, stack, itemHandler, slotType);
  }

  /** Creates a new instance of this container on the client side */
  public static ToolContainer forClient(int id, PlayerInventory inventory, PacketBuffer buffer) {
    EquipmentSlotType slotType = buffer.readEnumValue(EquipmentSlotType.class);
    ItemStack stack = inventory.player.getItemStackFromSlot(slotType);
    IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).filter(cap -> cap instanceof IItemHandlerModifiable).orElse(EmptyItemHandler.INSTANCE);
    return new ToolContainer(TinkerTools.toolContainer.get(), id, inventory, stack, handler, slotType);
  }

  protected ToolContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, ItemStack stack, IItemHandler handler, EquipmentSlotType slotType) {
    super(type, id);
    this.stack = stack;
    this.itemHandler = handler;
    this.player = playerInventory.player;

    // add tool slots
    int slots = itemHandler.getSlots();
    for (int i = 0; i < slots; i++) {
      this.addSlot(new ItemHandlerSlot(itemHandler, i, 8 + (i % 9) * SLOT_SIZE, (REPEAT_BACKGROUND_START + 1) + (i / 9) * SLOT_SIZE));
    }
    // add offhand if requested
    this.showOffhand = ModifierUtil.checkVolatileFlag(stack, ToolInventoryCapability.INCLUDE_OFFHAND);
    if (this.showOffhand) {
      int x = 8 + (slots % 9) * SLOT_SIZE;
      int y = (REPEAT_BACKGROUND_START + 1) + (slots / 9) * SLOT_SIZE;
      if (slotType == EquipmentSlotType.OFFHAND) {
        this.addSlot(new ReadOnlySlot(playerInventory, 40, x, y));
      } else {
        this.addSlot(new Slot(playerInventory, 40, x, y));
      }
    }

    // add player slots
    int playerY = 32 + SLOT_SIZE * ((slots + 8) / 9);
    for(int r = 0; r < 3; ++r) {
      for(int c = 0; c < 9; ++c) {
        this.addSlot(new Slot(playerInventory, c + r * 9 + 9, 8 + c * 18, playerY + r * 18));
      }
    }
    int hotbarStart = playerY + 58;
    selectedHotbarSlot = slotType == EquipmentSlotType.MAINHAND ? playerInventory.currentItem : (slotType == EquipmentSlotType.OFFHAND ? 10 : -1);
    for(int c = 0; c < 9; ++c) {
      if (c == selectedHotbarSlot) {
        this.addSlot(new ReadOnlySlot(playerInventory, c, 8 + c * 18, hotbarStart));
      } else {
        this.addSlot(new Slot(playerInventory, c, 8 + c * 18, hotbarStart));
      }
    }
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return player == playerIn;
  }
}
