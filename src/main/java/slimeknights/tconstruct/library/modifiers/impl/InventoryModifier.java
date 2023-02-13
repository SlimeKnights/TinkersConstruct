package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.function.BiFunction;

/** Modifier that has an inventory */
@RequiredArgsConstructor
public class InventoryModifier extends Modifier implements InventoryModifierHook {
  /** Mod Data NBT mapper to get a compound list */
  protected static final BiFunction<CompoundTag,String,ListTag> GET_COMPOUND_LIST = (nbt, name) -> nbt.getList(name, Tag.TAG_COMPOUND);
  /** Error for if the container has items preventing modifier removal */
  private static final ValidatedResult HAS_ITEMS = ValidatedResult.failure(TConstruct.makeTranslationKey("modifier", "inventory_cannot_remove"));
  /** NBT key to store the slot for a stack */
  protected static final String TAG_SLOT = "Slot";

  /** Persistent data key for the inventory storage, if null uses the modifier ID */
  @Nullable
  private final ResourceLocation inventoryKey;
  /** Number of slots to add per modifier level */
  protected final int slotsPerLevel;

  public InventoryModifier(int slotsPerLevel) {
    this(null, slotsPerLevel);
  }

  /** Gets the inventory key used for NBT serializing */
  protected ResourceLocation getInventoryKey() {
    return inventoryKey == null ? getId() : inventoryKey;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    ToolInventoryCapability.addSlots(volatileData, getSlots(context, level));
  }

  /**
   * Same as {@link #validate(IToolStackView, int)} but allows passing in a max slots count.
   * Allows the subclass to validate on a different max slots if needed
   * @param tool      Tool to check
   * @param maxSlots  Max slots to use in the check
   * @return  True if the number of slots is valid
   */
  protected ValidatedResult validateForMaxSlots(IToolStackView tool, int maxSlots) {
    IModDataView persistentData = tool.getPersistentData();
    ResourceLocation key = getInventoryKey();
    if (persistentData.contains(key, Tag.TAG_LIST)) {
      ListTag listNBT = persistentData.get(key, GET_COMPOUND_LIST);
      if (!listNBT.isEmpty()) {
        if (maxSlots == 0) {
          return HAS_ITEMS;
        }
        // first, see whether we have any available slots
        BitSet freeSlots = new BitSet(maxSlots);
        freeSlots.set(0, maxSlots-1, true);
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
    return ValidatedResult.PASS;
  }

  @Override
  public ValidatedResult validate(IToolStackView tool, int level) {
    return validateForMaxSlots(tool, level == 0 ? 0 : getSlots(tool, level));
  }

  @Override
  public void onRemoved(IToolStackView tool) {
    tool.getPersistentData().remove(getInventoryKey());
  }

  @Override
  public ItemStack getStack(IToolStackView tool, ModifierEntry modifier, int slot) {
    IModDataView modData = tool.getPersistentData();
    ResourceLocation key = getInventoryKey();
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
      ResourceLocation key = getInventoryKey();
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
              stack.save(compound);
              compound.putInt(TAG_SLOT, slot);
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
        list.add(write(stack, slot));
      }
    }
  }

  /** Gets the number of slots for this modifier */
  public int getSlots(IToolContext tool, int level) {
    return level * slotsPerLevel;
  }

  @Override
  public final int getSlots(IToolStackView tool, ModifierEntry modifier) {
    return getSlots(tool, modifier.getLevel());
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ToolInventoryCapability.HOOK);
  }

  /** Writes a stack to NBT, including the slot */
  protected static CompoundTag write(ItemStack stack, int slot) {
    CompoundTag compound = new CompoundTag();
    stack.save(compound);
    compound.putInt(TAG_SLOT, slot);
    return compound;
  }
}
