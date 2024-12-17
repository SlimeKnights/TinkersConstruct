package slimeknights.tconstruct.library.tools.capability.inventory;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.StackMatch;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Module adding an inventory to a tool
 * @param key            Location to save the inventory
 * @param slots          Slots to add to the tool
 * @param slotLimit      Maximum stack size in each slot
 * @param filter         Filter for valid items in the slot
 * @param pattern        Slot background to show
 * @param condition      Additional conditions
 */
public record InventoryModule(@Nullable ResourceLocation key, LevelingInt slots, LevelingInt slotLimit, IJsonPredicate<Item> filter, @Nullable Pattern pattern, ModifierCondition<IToolContext> condition, IntRange validationLevel) implements ModifierModule, InventoryModifierHook, VolatileDataModifierHook, ValidateModifierHook, ModifierRemovalHook, ModuleWithKey, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<InventoryModule>defaultHooks(ToolInventoryCapability.HOOK, ModifierHooks.VOLATILE_DATA, ModifierHooks.VALIDATE, ModifierHooks.REMOVE);
  /** Mod Data NBT mapper to get a compound list */
  public static final BiFunction<CompoundTag,String,ListTag> GET_COMPOUND_LIST = (nbt, name) -> nbt.getList(name, Tag.TAG_COMPOUND);
  /** Error for if the container has items preventing modifier removal */
  private static final Component HAS_ITEMS = TConstruct.makeTranslation("modifier", "inventory_cannot_remove");
  /** NBT key to store the slot for a stack */
  public static final String TAG_SLOT = "Slot";
  /** Loader instance */
  public static final RecordLoadable<InventoryModule> LOADER = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.nullableField("key", InventoryModule::key),
    LevelingInt.LOADABLE.requiredField("slots", InventoryModule::slots),
    LevelingInt.LOADABLE.defaultField("limit", LevelingInt.flat(64), InventoryModule::slotLimit),
    ItemPredicate.LOADER.defaultField("filter", InventoryModule::filter),
    Pattern.PARSER.nullableField("pattern", InventoryModule::pattern),
    ModifierCondition.CONTEXT_FIELD,
    ModifierEntry.VALID_LEVEL.defaultField("validation_level", InventoryModule::validationLevel),
    InventoryModule::new);

  @Override
  public RecordLoadable<InventoryModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }


  /* Properties */

  /** Gets the number of slots at the given level, assuming this module is active */
  private int getPotentialSlots(int level) {
    return Math.max(0, slots.computeForLevel(level));
  }

  @Override
  public int getSlots(IToolStackView tool, ModifierEntry modifier) {
    return condition.matches(tool, modifier) ? getPotentialSlots(modifier.intEffectiveLevel()) : 0;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    if (condition.matches(context, modifier)) {
      ToolInventoryCapability.addSlots(volatileData, getPotentialSlots(modifier.intEffectiveLevel()));
    }
  }

  @Override
  public int getSlotLimit(IToolStackView tool, ModifierEntry modifier, int slot) {
    return slotLimit.compute(modifier.intEffectiveLevel());
  }

  @Override
  public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
    return condition.matches(tool, modifier) && filter.matches(stack.getItem());
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
    return hasStack ? null : pattern;
  }


  /* Inventory */

  @Override
  public ItemStack getStack(IToolStackView tool, ModifierEntry modifier, int slot) {
    IModDataView modData = tool.getPersistentData();
    ResourceLocation key = getKey(modifier.getModifier());
    if (slot < getSlots(tool, modifier) && modData.contains(key, Tag.TAG_LIST)) {
      ListTag list = tool.getPersistentData().get(key, GET_COMPOUND_LIST);
      for (int i = 0; i < list.size(); i++) {
        CompoundTag compound = list.getCompound(i);
        if (compound.getInt(TAG_SLOT) == slot) {
          return ItemStack.of(compound);
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void setStack(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
    if (slot < getSlots(tool, modifier)) {
      ListTag list;
      ModDataNBT modData = tool.getPersistentData();
      // if the tag exists, fetch it
      ResourceLocation key = getKey(modifier.getModifier());
      if (modData.contains(key, Tag.TAG_LIST)) {
        list = modData.get(key, GET_COMPOUND_LIST);
        // first, try to find an existing stack in the slot
        for (int i = 0; i < list.size(); i++) {
          CompoundTag compound = list.getCompound(i);
          if (compound.getInt(TAG_SLOT) == slot) {
            if (stack.isEmpty()) {
              list.remove(i);
            } else {
              compound.getAllKeys().clear();
              writeStack(stack, slot, compound);
            }
            return;
          }
        }
      } else if (stack.isEmpty()) {
        // nothing to do if empty
        return;
      } else {
        list = new ListTag();
        modData.put(key, list);
      }

      // list did not contain the slot, so add it
      if (!stack.isEmpty()) {
        list.add(writeStack(stack, slot, new CompoundTag()));
      }
    }
  }


  /* Validation */

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    // don't validate if the module is not running
    if (condition.tool().matches(tool) && validationLevel.test(modifier.getLevel())) {
      IModDataView persistentData = tool.getPersistentData();
      ResourceLocation key = getKey(modifier.getModifier());
      int maxSlots = getSlots(tool, modifier);
      if (persistentData.contains(key, Tag.TAG_LIST)) {
        ListTag listNBT = persistentData.get(key, GET_COMPOUND_LIST);
        if (!listNBT.isEmpty()) {
          if (maxSlots == 0) {
            return HAS_ITEMS;
          }
          // first, see whether we have any available slots
          BitSet freeSlots = new BitSet(maxSlots);
          freeSlots.set(0, maxSlots, true);
          for (int i = 0; i < listNBT.size(); i++) {
            freeSlots.set(listNBT.getCompound(i).getInt(TAG_SLOT), false);
          }
          for (int i = 0; i < listNBT.size(); i++) {
            CompoundTag compoundNBT = listNBT.getCompound(i);
            if (compoundNBT.getInt(TAG_SLOT) >= maxSlots) {
              int free = freeSlots.stream().findFirst().orElse(-1);
              if (free == -1) {
                return HAS_ITEMS;
              } else {
                freeSlots.set(free, false);
                compoundNBT.putInt(TAG_SLOT, free);
              }
            }
          }
        }
      }
    }
    return null;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    // if we currently have item data, then return an error
    ModDataNBT persistentData = tool.getPersistentData();
    ResourceLocation key = getKey(modifier);
    if (persistentData.contains(key, Tag.TAG_LIST) && !persistentData.get(key, GET_COMPOUND_LIST).isEmpty()) {
      return HAS_ITEMS;
    }
    // remove the data key, should be empty
    persistentData.remove(key);
    return null;
  }


  /* Helpers */

  /**
   * Writes a stack to NBT, including the slot
   * @param stack  Stack to write
   * @param slot   Target slot
   * @param compound  Tag to write to, use new for new tag
   * @return Tag written to, same as {@code compound}.
   */
  public static CompoundTag writeStack(ItemStack stack, int slot, CompoundTag compound) {
    stack.save(compound);
    compound.putInt(TAG_SLOT, slot);
    return compound;
  }

  @Override
  public StackMatch findStack(IToolStackView tool, ModifierEntry modifier, Predicate<ItemStack> predicate) {
    // can save a lot of effort if we have no slots
    int max = getSlots(tool, modifier);
    if (max > 0) {
      IModDataView persistentData = tool.getPersistentData();
      ResourceLocation key = getKey(modifier.getModifier());
      ListTag slots = persistentData.get(key, GET_COMPOUND_LIST);
      if (!slots.isEmpty()) {
        // search all slots for the first match
        for (int i = 0; i < slots.size(); i++) {
          CompoundTag compound = slots.getCompound(i);
          // slot must be valid
          int slot = compound.getInt(TAG_SLOT);
          if (slot < max) {
            ItemStack stack = ItemStack.of(compound);
            if (!stack.isEmpty() && predicate.test(stack)) {
              return new StackMatch(stack, slot);
            }
          }
        }
      }
    }
    return StackMatch.EMPTY;
  }

  @Override
  public List<ItemStack> getAllStacks(IToolStackView tool, ModifierEntry entry, List<ItemStack> stackList) {
    // can save a lot of effort if we have no slots
    int max = getSlots(tool, entry);
    if (max > 0) {
      IModDataView modData = tool.getPersistentData();
      ResourceLocation key = getKey(entry.getModifier());
      if (modData.contains(key, Tag.TAG_LIST)) {
        ListTag list = modData.get(key, GET_COMPOUND_LIST);

        // make sure the stacks are in order, NBT could store them in any order
        ItemStack[] parsed = new ItemStack[max];
        for (int i = 0; i < list.size(); i++) {
          CompoundTag compound = list.getCompound(i);
          // slot must be valid
          int slot = compound.getInt(TAG_SLOT);
          if (slot < max) {
            parsed[slot] = ItemStack.of(compound);
          }
        }
        // add stacks into the list
        for (ItemStack stack : parsed) {
          if (stack != null && !stack.isEmpty()) {
            stackList.add(stack);
          }
        }
      }
    }
    return stackList;
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  @Accessors(fluent = true)
  @Setter
  public static class Builder extends ModuleBuilder.Context<Builder> {
    @Nullable
    private ResourceLocation key = null;
    private LevelingInt slotLimit = LevelingInt.flat(64);
    private IJsonPredicate<Item> filter = ItemPredicate.ANY;
    @Nullable
    private Pattern pattern = null;
    private IntRange validationLevel = ModifierEntry.VALID_LEVEL;

    private Builder() {}

    /** Sets the base slot limit */
    public Builder flatLimit(int limit) {
      return slotLimit(LevelingInt.flat(limit));
    }

    /** Sets the base slot limit */
    public Builder limitPerLevel(int limit) {
      return slotLimit(LevelingInt.eachLevel(limit));
    }

    /** Builds the final instance */
    public InventoryModule slots(int base, int perLevel) {
      return new InventoryModule(key, new LevelingInt(base, perLevel), slotLimit, filter, pattern, condition, validationLevel);
    }

    /** Builds the final instance */
    public InventoryModule flatSlots(int slots) {
      return slots(slots, 0);
    }

    /** Builds the final instance */
    public InventoryModule slotsPerLevel(int slots) {
      return slots(0, slots);
    }
  }
}
