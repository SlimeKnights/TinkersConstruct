package slimeknights.tconstruct.library.tools.capability;

import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/** Capability for a tool with an inventory */
@RequiredArgsConstructor
public class ToolInventoryCapability extends InventoryModifierHookIterator<ModifierEntry> implements IItemHandlerModifiable {
  /** Boolean key to set in volatile mod data to enable the fluid capability */
  public static final ResourceLocation TOTAL_SLOTS = TConstruct.getResource("total_item_slots");
  /** Boolean key to set in volatile mod data to enable the fluid capability */
  public static final ResourceLocation INCLUDE_OFFHAND = TConstruct.getResource("inventory_show_offhand");

  /** Modifier hook instance to make an inventory modifier */
  @SuppressWarnings("deprecation")
  public static final ModifierHook<InventoryModifierHook> HOOK = ModifierHooks.register(TConstruct.getResource("inventory"), InventoryModifierHook.class, new InventoryModifierHook() {
    @Override
    public int getSlots(IToolStackView tool, ModifierEntry modifier) {
      IInventoryModifier inventory = modifier.getModifier().getModule(IInventoryModifier.class);
      if (inventory != null) {
        return inventory.getSlots(tool, modifier.getLevel());
      }
      return 0;
    }

    @Override
    public ItemStack getStack(IToolStackView tool, ModifierEntry modifier, int slot) {
      IInventoryModifier inventory = modifier.getModifier().getModule(IInventoryModifier.class);
      if (inventory != null) {
        return inventory.getStack(tool, modifier.getLevel(), slot);
      }
      return ItemStack.EMPTY;
    }

    @Override
    public void setStack(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
      IInventoryModifier inventory = modifier.getModifier().getModule(IInventoryModifier.class);
      if (inventory != null) {
        inventory.setStack(tool, modifier.getLevel(), slot, stack);
      }
    }

    @Override
    public int getSlotLimit(IToolStackView tool, ModifierEntry modifier, int slot) {
      IInventoryModifier inventory = modifier.getModifier().getModule(IInventoryModifier.class);
      if (inventory != null) {
        return inventory.getSlotLimit(tool, slot);
      }
      return 0;
    }

    @Override
    public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
      IInventoryModifier inventory = modifier.getModifier().getModule(IInventoryModifier.class);
      if (inventory != null) {
        return inventory.isItemValid(tool, slot, stack);
      }
      return false;
    }

    @Nullable
    @Override
    public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
      IInventoryModifier inventory = modifier.getModifier().getModule(IInventoryModifier.class);
      if (inventory != null) {
        return inventory.getPattern(tool, modifier.getLevel(), slot, hasStack);
      }
      return null;
    }
  }, InventoryModifierHookMerger::new);

  /** Supplier to the tool instance */
  private final Supplier<? extends IToolStackView> tool;
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

  @Override
  protected Iterator<ModifierEntry> getIterator(IToolStackView tool) {
    // iterate in reverse order, as that allows us to put shield strap/tool belt later in the UI without breaking the keybind
    return new ReversedListIterator<>(tool.getModifierList());
  }

  @Override
  protected InventoryModifierHook getHook(ModifierEntry entry) {
    indexEntry = entry;
    return entry.getHook(HOOK);
  }

  /** If true, the given stack is blacklisted from being stored in a tool */
  public static boolean isBlacklisted(ItemStack stack) {
    return !stack.getItem().canFitInsideContainerItems() || stack.is(TinkerTags.Items.TOOL_INVENTORY_BLACKLIST) || stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    // no nesting item handlers
    if (!stack.isEmpty() && isBlacklisted(stack)) {
      return false;
    }
    IToolStackView tool = this.tool.get();
    InventoryModifierHook inventory = findHook(tool, slot);
    if (inventory != null) {
      return inventory.isItemValid(tool, indexEntry, slot - startIndex, stack);
    }
    return false;
  }

  @Override
  public int getSlotLimit(int slot) {
    IToolStackView tool = this.tool.get();
    InventoryModifierHook inventory = findHook(tool, slot);
    if (inventory != null) {
      return inventory.getSlotLimit(tool, indexEntry, slot - startIndex);
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
  private void setAndCache(InventoryModifierHook inventory, int localSlot, int globalSlot, ItemStack stack) {
    inventory.setStack(tool.get(), indexEntry, localSlot, stack);
    // cache the stack to save lookup times later
    cacheStack(globalSlot, stack);
  }


  /* Get and set */

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    InventoryModifierHook inventory = findHook(tool.get(), slot);
    if (inventory != null) {
      setAndCache(inventory, slot - startIndex, slot, stack);
    }
  }

  /** Gets a stack from the given inventory, caching it */
  private ItemStack getAndCache(InventoryModifierHook inventory, int localSlot, int globalSlot) {
    ItemStack stack = inventory.getStack(tool.get(), indexEntry, localSlot);
    cacheStack(globalSlot, stack);
    return stack;
  }

  /** Gets the stack from cache, if failing parses it */
  private ItemStack getCached(InventoryModifierHook inventory, int localSlot, int globalSlot) {
    ItemStack stack = getCachedStack(globalSlot);
    if (stack == null) {
      stack = getAndCache(inventory, localSlot, globalSlot);
    }
    return stack;
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int slot) {
    ItemStack cached = getCachedStack(slot);
    if (cached != null) {
      return cached;
    }
    InventoryModifierHook inventory = findHook(tool.get(), slot);
    if (inventory != null) {
      return getAndCache(inventory, slot - startIndex, slot);
    }
    return ItemStack.EMPTY;
  }

  @Nonnull
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
    IToolStackView tool = this.tool.get();
    InventoryModifierHook inventory = findHook(tool, slot);
    if (inventory == null) {
      return stack;
    }
    // next, is the item valid for the slot?
    int localSlot = slot - startIndex;
    if (!inventory.isItemValid(tool, indexEntry, localSlot, stack)) {
      return stack;
    }

    // do we have a stack?
    ItemStack current = getCached(inventory, localSlot, slot);

    // nothing currently? place the item in
    int leftover;
    int slotLimit = inventory.getSlotLimit(tool, indexEntry, localSlot);
    if (current.isEmpty()) {
      int canInsert = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), slotLimit));
      leftover = stack.getCount() - canInsert;
      if (!simulate) {
        setAndCache(inventory, localSlot, slot, ItemHandlerHelper.copyStackWithSize(stack, canInsert));
      }
    } else {
      // space leftover? does it match?
      int limit = Math.min(current.getMaxStackSize(), slotLimit);
      if (current.getCount() >= limit || !current.sameItem(stack)) {
        return stack;
      }
      int maxSize = current.getCount() + stack.getCount();
      int newSize = Math.min(maxSize, limit);
      leftover = maxSize - newSize;
      // store new stack
      if (!simulate) {
        current.setCount(newSize);
        inventory.setStack(tool, indexEntry, localSlot, current); // update stack in NBT
      }
    }

    // return leftover
    if (leftover == 0) {
      return ItemStack.EMPTY;
    }
    return ItemHandlerHelper.copyStackWithSize(stack, leftover);
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    // first, are you wasting our time?
    if (amount <= 0) {
      return ItemStack.EMPTY;
    }
    // next, do we have an inventory?
    IToolStackView tool = this.tool.get();
    InventoryModifierHook inventory = findHook(tool, slot);
    if (inventory == null) {
      return ItemStack.EMPTY;
    }
    int localSlot = slot - startIndex;

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
        inventory.setStack(tool, indexEntry, localSlot, current); // update in NBT
      }
    }
    return result;
  }

  /** @deprecated use {@link InventoryModifierHook} */
  @SuppressWarnings({"unused", "DeprecatedIsStillUsed"})
  @Deprecated
  public interface IInventoryModifier {
    /** Gets the number of item slots used by the given tool. The number returned here must also be added into volatile data under {@link #TOTAL_SLOTS} */
    int getSlots(IToolStackView tool, int level);

    /** Sets the stack in the given slot */
    ItemStack getStack(IToolStackView tool, int level, int slot);

    /** Sets the stack in the given slot */
    void setStack(IToolStackView tool, int level, int slot, ItemStack stack);

    /** Gets the max stack size for the given slot */
    default int getSlotLimit(IToolStackView tool, int slot) {
      return 64;
    }

    /** Checks if the item is valid for the given slot */
    default boolean isItemValid(IToolStackView tool, int slot, ItemStack stack) {
      return true;
    }

    /** Gets the pattern to render when the given slot is empty */
    @Nullable
		default Pattern getPattern(IToolStackView tool, int level, int slot, boolean hasStack) {
      return null;
    }
	}

  /** Interface for an inventory modifier to use */
  @SuppressWarnings("unused")
  public interface InventoryModifierHook {
    /** Gets the number of item slots used by the given tool. The number returned here must also be added into volatile data under {@link #TOTAL_SLOTS} */
    int getSlots(IToolStackView tool, ModifierEntry modifier);

    /** Sets the stack in the given slot */
    ItemStack getStack(IToolStackView tool, ModifierEntry modifier, int slot);

    /** Sets the stack in the given slot */
    void setStack(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack);

    /** Gets the max stack size for the given slot */
    default int getSlotLimit(IToolStackView tool, ModifierEntry modifier, int slot) {
      return 64;
    }

    /** Checks if the item is valid for the given slot */
    default boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
      return true;
    }

    /** Gets the pattern to render when the given slot is empty */
    @Nullable
    default Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
      return null;
    }
  }

  /** Merger for inventory modifier hooks */
  @RequiredArgsConstructor
  private static class InventoryModifierHookMerger extends InventoryModifierHookIterator<InventoryModifierHook> implements InventoryModifierHook {
    private final Collection<InventoryModifierHook> modules;

    @Override
    protected Iterator<InventoryModifierHook> getIterator(IToolStackView tool) {
      return modules.iterator();
    }

    @Override
    protected InventoryModifierHook getHook(InventoryModifierHook hook) {
      return hook;
    }

    /** Gets the inventory instance for the given slot index */
    @Nullable
    private InventoryModifierHook findHook(IToolStackView tool, ModifierEntry modifier, int slot) {
      indexEntry = modifier;
      return this.findHook(tool, slot);
    }

    @Override
    public int getSlots(IToolStackView tool, ModifierEntry modifier) {
      int sum = 0;
      for (InventoryModifierHook module : modules) {
        sum += module.getSlots(tool, modifier);
      }
      return sum;
    }

    @Override
    public ItemStack getStack(IToolStackView tool, ModifierEntry modifier, int slot) {
      InventoryModifierHook module = findHook(tool, modifier, slot);
      if (module != null) {
        return module.getStack(tool, modifier, slot - startIndex);
      }
      return ItemStack.EMPTY;
    }

    @Override
    public void setStack(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
      InventoryModifierHook module = findHook(tool, modifier, slot);
      if (module != null) {
        module.setStack(tool, modifier, slot - startIndex, stack);
      }
    }

    @Override
    public int getSlotLimit(IToolStackView tool, ModifierEntry modifier, int slot) {
      InventoryModifierHook module = findHook(tool, modifier, slot);
      if (module != null) {
        return module.getSlotLimit(tool, modifier, slot - startIndex);
      }
      return 0;
    }

    @Override
    public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
      InventoryModifierHook module = findHook(tool, modifier, slot);
      if (module != null) {
        return module.isItemValid(tool, modifier, slot - startIndex, stack);
      }
      return false;
    }

    @Nullable
    @Override
    public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
      InventoryModifierHook module = findHook(tool, modifier, slot);
      if (module != null) {
        return module.getPattern(tool, modifier, slot - startIndex, hasStack);
      }
      return null;
    }
  }

  /** Provider for an inventory tool capability */
  public static class Provider implements IToolCapabilityProvider {
    private final LazyOptional<ToolInventoryCapability> handler;
    @SuppressWarnings("unused")
    public Provider(ItemStack stack, Supplier<? extends IToolStackView> tool) {
      handler = LazyOptional.of(() -> new ToolInventoryCapability(tool));
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap) {
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
  public static InteractionResult tryOpenContainer(ItemStack stack, IToolStackView tool, Player player, EquipmentSlot slotType) {
    return tryOpenContainer(stack, tool, tool.getDefinition(), player, slotType);
  }

  /** Opens the tool inventory container if an inventory is present on the given tool */
  public static InteractionResult tryOpenContainer(ItemStack stack, @Nullable IToolStackView tool, ToolDefinition definition, Player player, EquipmentSlot slotType) {
    IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).filter(cap -> cap instanceof IItemHandlerModifiable).orElse(null);
    if (handler != null) {
      if (player instanceof ServerPlayer serverPlayer) {
        NetworkHooks.openGui(serverPlayer, new SimpleMenuProvider(
          (id, inventory, p) -> new ToolContainerMenu(id, inventory, stack, (IItemHandlerModifiable)handler, slotType),
          TooltipUtil.getDisplayName(stack, tool, definition)
        ), buf -> buf.writeEnum(slotType));
      }
      return InteractionResult.sidedSuccess(player.level.isClientSide);
    }
    return InteractionResult.PASS;
  }

  /** Iterator that goes through a list in reverse order */
  private static class ReversedListIterator<T> implements Iterator<T> {
    /** List to iterate */
    private final List<T> list;
    /** Index of the next element */
    private int index;

    public ReversedListIterator(List<T> list) {
      this.list = list;
      this.index = list.size() - 1;
    }

    @Override
    public boolean hasNext() {
      return index >= 0;
    }

    @Override
    public T next() {
      T element = list.get(index);
      index--;
      return element;
    }
  }
}
