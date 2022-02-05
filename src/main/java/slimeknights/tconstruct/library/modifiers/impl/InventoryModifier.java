package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.IInventoryModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/** Modifier that has an inventory */
@RequiredArgsConstructor
public class InventoryModifier extends Modifier implements IInventoryModifier {
  /** Mod Data NBT mapper to get a compound list */
  protected static final BiFunction<CompoundTag,String,ListTag> GET_COMPOUND_LIST = (nbt, name) -> nbt.getList(name, Tag.TAG_COMPOUND);
  /** Error for if the container has items preventing modifier removal */
  private static final ValidatedResult HAS_ITEMS = ValidatedResult.failure(TConstruct.makeTranslationKey("modifier", "inventory_cannot_remove"));
  /** NBT key to store the slot for a stack */
  protected static final String TAG_SLOT = "Slot";

  /** Persistent data key for the inventory storage */
  private final ResourceLocation inventoryKey;
  /** Number of slots to add per modifier level */
  private final int slotsPerLevel;

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    ToolInventoryCapability.addSlots(volatileData, getSlots(context, level));
  }

  @Override
  public ValidatedResult validate(IToolStackView tool, int level) {
    IModDataView persistentData = tool.getPersistentData();
    if (persistentData.contains(inventoryKey, Tag.TAG_LIST)) {
      ListTag listNBT = persistentData.get(inventoryKey, GET_COMPOUND_LIST);
      if (!listNBT.isEmpty()) {
        if (level == 0) {
          return HAS_ITEMS;
        }
        // determine the largest index we are using
        int maxSlots = getSlots(tool, level);
        for (int i = 0; i < listNBT.size(); i++) {
          CompoundTag compoundNBT = listNBT.getCompound(i);
          if (compoundNBT.getInt(TAG_SLOT) >= maxSlots) {
            return HAS_ITEMS;
          }
        }
      }
    }
    return ValidatedResult.PASS;
  }

  @Override
  public void onRemoved(IToolStackView tool) {
    tool.getPersistentData().remove(inventoryKey);
  }

  @Override
  public ItemStack getStack(IToolStackView tool, int level, int slot) {
    IModDataView modData = tool.getPersistentData();
    if (slot < getSlots(tool, level) && modData.contains(inventoryKey, Tag.TAG_LIST)) {
      ListTag list = tool.getPersistentData().get(inventoryKey, GET_COMPOUND_LIST);
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
  public void setStack(IToolStackView tool, int level, int slot, ItemStack stack) {
    if (slot < getSlots(tool, level)) {
      ListTag list;
      ModDataNBT modData = tool.getPersistentData();
      // if the tag exists, fetch it
      if (modData.contains(inventoryKey, Tag.TAG_LIST)) {
        list = modData.get(inventoryKey, GET_COMPOUND_LIST);
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
        modData.put(inventoryKey, list);
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
  public final int getSlots(IToolStackView tool, int level) {
    return getSlots((IToolContext) tool, level);
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IInventoryModifier.class, this);
  }

  /** Writes a stack to NBT, including the slot */
  protected static CompoundTag write(ItemStack stack, int slot) {
    CompoundTag compound = new CompoundTag();
    stack.save(compound);
    compound.putInt(TAG_SLOT, slot);
    return compound;
  }
}
