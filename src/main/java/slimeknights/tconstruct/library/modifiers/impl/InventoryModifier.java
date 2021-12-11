package slimeknights.tconstruct.library.modifiers.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.IInventoryModifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/** Modifier that has an inventory */
public class InventoryModifier extends Modifier implements IInventoryModifier {
  /** Mod Data NBT mapper to get a compound list */
  private static final BiFunction<CompoundNBT,String,ListNBT> GET_COMPOUND_LIST = (nbt, name) -> nbt.getList(name, NBT.TAG_COMPOUND);
  /** NBT key to store the slot for a stack */
  private static final String TAG_SLOT = "Slot";

  /** Persistent data key for the inventory storage */
  private final ResourceLocation inventoryKey;
  /** Number of slots to add per modifier level */
  private final int slotsPerLevel;

  public InventoryModifier(int color, ResourceLocation inventoryKey, int slotsPerLevel) {
    super(color);
    this.inventoryKey = inventoryKey;
    this.slotsPerLevel = slotsPerLevel;
  }

  @Override
  public void addVolatileData(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    ToolInventoryCapability.addSlots(volatileData, level * slotsPerLevel);
  }

  @Override
  public ItemStack getStack(IModifierToolStack tool, int level, int slot) {
    IModDataReadOnly modData = tool.getPersistentData();
    if (slot < level * slotsPerLevel && modData.contains(inventoryKey, NBT.TAG_LIST)) {
      ListNBT list = tool.getPersistentData().get(inventoryKey, GET_COMPOUND_LIST);
      for (int i = 0; i < list.size(); i++) {
        CompoundNBT compound = list.getCompound(i);
        if (compound.getInt(TAG_SLOT) == slot) {
          return ItemStack.read(compound);
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void setStack(IModifierToolStack tool, int level, int slot, ItemStack stack) {
    if (slot < level * slotsPerLevel) {
      ListNBT list;
      ModDataNBT modData = tool.getPersistentData();
      // if the tag exists, fetch it
      if (modData.contains(inventoryKey, NBT.TAG_LIST)) {
        list = modData.get(inventoryKey, GET_COMPOUND_LIST);
        // first, try to find an existing stack in the slot
        for (int i = 0; i < list.size(); i++) {
          CompoundNBT compound = list.getCompound(i);
          if (compound.getInt(TAG_SLOT) == slot) {
            if (stack.isEmpty()) {
              list.remove(i);
            } else {
              compound.keySet().clear();
              stack.write(compound);
              compound.putInt(TAG_SLOT, slot);
            }
            return;
          }
        }
      } else if (stack.isEmpty()) {
        // nothing to do if empty
        return;
      } else {
        list = new ListNBT();
        modData.put(inventoryKey, list);
      }

      // list did not contain the slot, so add it
      if (!stack.isEmpty()) {
        CompoundNBT compound = new CompoundNBT();
        stack.write(compound);
        compound.putInt(TAG_SLOT, slot);
        list.add(compound);
      }
    }
  }

  @Override
  public int getSlots(IModifierToolStack tool, int level) {
    return level * slotsPerLevel;
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IInventoryModifier.class, this);
  }
}
