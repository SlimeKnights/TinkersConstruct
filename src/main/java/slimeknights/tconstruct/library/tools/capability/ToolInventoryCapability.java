package slimeknights.tconstruct.library.tools.capability;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.inventory.ToolContainer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** Capability for a tool with an inventory */
@RequiredArgsConstructor
public class ToolInventoryCapability implements IItemHandlerModifiable {
  /** Boolean key to set in volatile mod data to enable the fluid capability */
  public static final ResourceLocation TOTAL_SLOTS = TConstruct.getResource("total_item_slots");
  /** Boolean key to set in volatile mod data to enable the fluid capability */
  public static final ResourceLocation INCLUDE_OFFHAND = TConstruct.getResource("inventory_show_offhand");

  /** Supplier to the tool instance */
  private final Supplier<? extends IModifierToolStack> tool;
  /** Cache of all stacks that have been parsed thus far */
  private ItemStack[] cachedStacks;

  /** Cached slot count */
  private int slots = -1;

  @Override
  public int getSlots() {
    if (slots == -1) {
      slots = tool.get().getVolatileData().getInt(TOTAL_SLOTS);
    }
    return slots;
  }


  /* Basic inventory */

  /** Start index from {@link #getInventory(int)}, reduces object creation */
  private int startForSlot = 0;
  /** Modifier level from {@link #getInventory(int)}, reduces object creation */
  private int modifierLevel = 0;

  /** Gets the inventory instance for the given slot index */
  @Nullable
  private IInventoryModifier getInventory(int slot) {
    IModifierToolStack tool = this.tool.get();
    if (slot < getSlots()) {
      int start = 0;
      for (ModifierEntry entry : tool.getModifierList()) {
        IInventoryModifier inventory = entry.getModifier().getModule(IInventoryModifier.class);
        if (inventory != null) {
          int slots = inventory.getSlots(tool, entry.getLevel());
          if (slot < slots + start) {
            startForSlot = start;
            modifierLevel = entry.getLevel();
            return inventory;
          }
          start += slots;
        }
      }
    }
    return null;
  }

  /** If true, the given stack is blacklisted from being stored in a tool */
  public static boolean isBlacklisted(ItemStack stack) {
    return TinkerTags.Items.TOOL_INVENTORY_BLACKLIST.contains(stack.getItem()) || stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    // no nesting item handlers
    if (!stack.isEmpty() && isBlacklisted(stack)) {
      return false;
    }
    IInventoryModifier inventory = getInventory(slot);
    if (inventory != null) {
      return inventory.isItemValid(tool.get(), slot - startForSlot, stack);
    }
    return false;
  }

  @Override
  public int getSlotLimit(int slot) {
    IInventoryModifier inventory = getInventory(slot);
    if (inventory != null) {
      return inventory.getSlotLimit(tool.get(), slot - startForSlot);
    }
    return 0;
  }


  /* Item stack cache */

  /** Clears all cached data in the capability */
  private void clearCache() {
    slots = -1;
    cachedStacks = null;
  }

  /** Caches the stack in the given slot */
  private void cacheStack(int slot, ItemStack stack) {
    if (slot >= 0) {
      int slots = getSlots();
      if (slot < slots) {
        if (cachedStacks == null) {
          cachedStacks = new ItemStack[getSlots()];
        }
        cachedStacks[slot] = stack; // TODO: copy?
      }
    }
  }

  /** Gets the stack cached in the given slot */
  @Nullable
  private ItemStack getCachedStack(int slot) {
    if (cachedStacks != null && slot >= 0 && slot < getSlots()) {
      return cachedStacks[slot];
    }
    return null;
  }

  /** Gets a stack from the given inventory, caching it */
  private void setAndCache(IInventoryModifier inventory, int localSlot, int globalSlot, ItemStack stack) {
    inventory.setStack(tool.get(), modifierLevel, localSlot, stack);
    // cache the stack to save lookup times later
    cacheStack(globalSlot, stack);
  }


  /* Get and set */

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    IInventoryModifier inventory = getInventory(slot);
    if (inventory != null) {
      setAndCache(inventory, slot - startForSlot, slot, stack);
    }
  }

  /** Gets a stack from the given inventory, caching it */
  private ItemStack getAndCache(IInventoryModifier inventory, int localSlot, int globalSlot) {
    ItemStack stack = inventory.getStack(tool.get(), modifierLevel, localSlot);
    cacheStack(globalSlot, stack);
    return stack;
  }

  /** Gets the stack from cache, if failing parses it */
  private ItemStack getCached(IInventoryModifier inventory, int localSlot, int globalSlot) {
    ItemStack stack = getCachedStack(globalSlot);
    if (stack == null) {
      stack = getAndCache(inventory, localSlot, globalSlot);
    }
    return stack;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    ItemStack cached = getCachedStack(slot);
    if (cached != null) {
      return cached;
    }
    IInventoryModifier inventory = getInventory(slot);
    if (inventory != null) {
      return getAndCache(inventory, slot - startForSlot, slot);
    }
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    // no nesting item handlers
    if (isBlacklisted(stack)) {
      return stack;
    }
    // first, do we have an inventory?
    IInventoryModifier inventory = getInventory(slot);
    if (inventory == null) {
      return stack;
    }
    // next, is the item valid for the slot?
    int localSlot = slot - startForSlot;
    IModifierToolStack tool = this.tool.get();
    if (!inventory.isItemValid(tool, localSlot, stack)) {
      return stack;
    }

    // do we have a stack?
    ItemStack current = getCached(inventory, localSlot, slot);

    // nothing currently? place the item in
    int leftover;
    int slotLimit = inventory.getSlotLimit(tool, localSlot);
    if (current.isEmpty()) {
      int canInsert = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), slotLimit));
      leftover = stack.getCount() - canInsert;
      if (!simulate) {
        setAndCache(inventory, localSlot, slot, ItemHandlerHelper.copyStackWithSize(stack, canInsert));
      }
    } else {
      // space leftover? does it match?
      int limit = Math.min(current.getMaxStackSize(), slotLimit);
      if (current.getCount() >= limit || !current.isItemEqual(stack)) {
        return stack;
      }
      int maxSize = current.getCount() + stack.getCount();
      int newSize = Math.min(maxSize, limit);
      leftover = maxSize - newSize;
      // store new stack
      if (!simulate) {
        current.setCount(newSize);
        inventory.setStack(tool, modifierLevel, localSlot, current); // update stack in NBT
      }
    }

    // return leftover
    if (leftover == 0) {
      return ItemStack.EMPTY;
    }
    return ItemHandlerHelper.copyStackWithSize(stack, leftover);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    // first, are you wasting our time?
    if (amount <= 0) {
      return ItemStack.EMPTY;
    }
    // next, do we have an inventory?
    IInventoryModifier inventory = getInventory(slot);
    if (inventory == null) {
      return ItemStack.EMPTY;
    }
    int localSlot = slot - startForSlot;

    // do we have anything in the slot?
    ItemStack current = getCached(inventory, localSlot, slot);
    if (current.isEmpty()) {
      return ItemStack.EMPTY;
    }
    // they want more than we can give? just say no
    if (amount > current.getCount()) {
      amount = current.getCount();
    }
    // get the result before modifying current
    ItemStack result = ItemHandlerHelper.copyStackWithSize(current, amount);
    if (!simulate) {
      if (amount == current.getCount()) {
        setAndCache(inventory, localSlot, slot, ItemStack.EMPTY);
      } else {
        current.shrink(amount);
        inventory.setStack(tool.get(), modifierLevel, localSlot, current); // update in NBT
      }
    }
    return result;
  }

  /** Interface for an inventory modifier to use */
  public interface IInventoryModifier {
    /** Gets the number of item slots used by the given tool. The number returned here must also be added into volatile data under {@link #TOTAL_SLOTS} */
    int getSlots(IModifierToolStack tool, int level);

    /** Sets the stack in the given slot */
    ItemStack getStack(IModifierToolStack tool, int level, int slot);

    /** Sets the stack in the given slot */
    void setStack(IModifierToolStack tool, int level, int slot, ItemStack stack);

    /** Gets the max stack size for the given slot */
    default int getSlotLimit(IModifierToolStack tool, int slot) {
      return 64;
    }

    /** Checks if the item is valid for the given slot */
    default boolean isItemValid(IModifierToolStack tool, int slot, ItemStack stack) {
      return true;
    }

    /** Gets the pattern to render when the given slot is empty */
    @Nullable
		default Pattern getPattern(IModifierToolStack tool, int level, int slot, boolean hasStack) {
      return null;
    }
	}

  /** Provider for an inventory tool capability */
  public static class Provider implements IToolCapabilityProvider {
    private final LazyOptional<ToolInventoryCapability> handler;
    public Provider(ItemStack stack, Supplier<? extends IModifierToolStack> tool) {
      handler = LazyOptional.of(() -> new ToolInventoryCapability(tool));
    }

    @Override
    public <T> LazyOptional<T> getCapability(IModifierToolStack tool, Capability<T> cap) {
      if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && tool.getVolatileData().getInt(TOTAL_SLOTS) > 0) {
        return handler.cast();
      }
      return LazyOptional.empty();
    }

    @Override
    public void clearCache() {
      handler.ifPresent(ToolInventoryCapability::clearCache);
    }
  }


  /* Helpers */

  /** Adds the given number of slots to the data */
  public static void addSlots(ModDataNBT volatileData, int count) {
    volatileData.putInt(TOTAL_SLOTS, volatileData.getInt(TOTAL_SLOTS) + count);
  }


  /** Opens the tool inventory container if an inventory is present on the given tool */
  public static ActionResultType tryOpenContainer(ItemStack stack, IModifierToolStack tool, PlayerEntity player, EquipmentSlotType slotType) {
    return tryOpenContainer(stack, tool, tool.getDefinition(), player, slotType);
  }

  /** Opens the tool inventory container if an inventory is present on the given tool */
  public static ActionResultType tryOpenContainer(ItemStack stack, @Nullable IModifierToolStack tool, ToolDefinition definition, PlayerEntity player, EquipmentSlotType slotType) {
    IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).filter(cap -> cap instanceof IItemHandlerModifiable).orElse(null);
    if (handler != null) {
      if (player instanceof ServerPlayerEntity) {
        NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider(
          (id, inventory, p) -> new ToolContainer(id, inventory, stack, (IItemHandlerModifiable)handler, slotType),
          TooltipUtil.getDisplayName(stack, tool, definition)
        ), buf -> buf.writeEnumValue(slotType));
      }
      return ActionResultType.func_233537_a_(player.world.isRemote);
    }
    return ActionResultType.PASS;
  }
}
