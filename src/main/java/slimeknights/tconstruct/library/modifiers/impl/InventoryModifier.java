package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.INamespacedNBTView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.function.BiFunction;

/** @deprecated use {@link InventoryModule} */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class InventoryModifier extends Modifier implements InventoryModifierHook, VolatileDataModifierHook, ValidateModifierHook, ModifierRemovalHook {
  /** @deprecated use {@link InventoryModule#GET_COMPOUND_LIST} */
  @Deprecated(forRemoval = true)
  protected static final BiFunction<CompoundTag,String,ListTag> GET_COMPOUND_LIST = InventoryModule.GET_COMPOUND_LIST;
  /** Error for if the container has items preventing modifier removal */
  private static final Component HAS_ITEMS = TConstruct.makeTranslation("modifier", "inventory_cannot_remove");
  /** @deprecated use {@link InventoryModule#TAG_SLOT} */
  @Deprecated(forRemoval = true)
  protected static final String TAG_SLOT = InventoryModule.TAG_SLOT;

  /** Persistent data key for the inventory storage, if null uses the modifier ID */
  @Nullable
  private final ResourceLocation inventoryKey;
  /** Number of slots to add per modifier level */
  protected final int slotsPerLevel;

  public InventoryModifier(int slotsPerLevel) {
    this(null, slotsPerLevel);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ToolInventoryCapability.HOOK, ModifierHooks.VOLATILE_DATA, ModifierHooks.VALIDATE, ModifierHooks.REMOVE);
  }

  /** Gets the inventory key used for NBT serializing */
  protected ResourceLocation getInventoryKey() {
    return inventoryKey == null ? getId() : inventoryKey;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    ToolInventoryCapability.addSlots(volatileData, getSlots(volatileData, modifier));
  }

  /**
   * Same as {@link ValidateModifierHook#validate(IToolStackView, ModifierEntry)} but allows passing in a max slots count.
   * Allows the subclass to validate on a different max slots if needed
   * @param tool      Tool to check
   * @param maxSlots  Max slots to use in the check
   * @return  True if the number of slots is valid
   */
  @Nullable
  protected Component validateForMaxSlots(IToolStackView tool, int maxSlots) {
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
    return null;
  }

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    return validateForMaxSlots(tool, getSlots(tool, modifier));
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    Component component = validateForMaxSlots(tool, 0);
    if (component != null) {
      return component;
    }
    tool.getPersistentData().remove(getInventoryKey());
    return null;
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
  public int getSlots(INamespacedNBTView volatileData, ModifierEntry modifier) {
    return modifier.intEffectiveLevel() * slotsPerLevel;
  }

  @Override
  public final int getSlots(IToolStackView tool, ModifierEntry modifier) {
    return getSlots(tool.getVolatileData(), modifier);
  }

  /** @deprecated use {@link InventoryModule#writeStack(ItemStack, int, CompoundTag)} */
  @Deprecated
  protected static CompoundTag write(ItemStack stack, int slot) {
    return InventoryModule.writeStack(stack, slot, new CompoundTag());
  }
}
