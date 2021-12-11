package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.Iterator;

import static slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.isBlacklisted;

public class ToolBeltModifier extends InventoryModifier implements IArmorInteractModifier {
  private static final ResourceLocation KEY = TConstruct.getResource("tool_belt");
  private static final Pattern PATTERN = new Pattern(TConstruct.MOD_ID, "tool_belt");
  public ToolBeltModifier() {
    super(0x893B25, KEY, 1);
  }

  @Override
  public int getPriority() {
    return 85; // after pockets, before shield strap
  }

  @Override
  protected int getSlots(int level) {
    return level == 2 ? 9 : 4;
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType equipmentSlot) {
    if (!player.isSneaking()) {
      if (player.world.isRemote) {
        return true;
      }

      int slots = getSlots(level);
      ModDataNBT persistentData = tool.getPersistentData();
      ListNBT list;
      boolean[] swapped = new boolean[slots];
      // if we have existing items, swap stacks at each index
      if (persistentData.contains(KEY, NBT.TAG_LIST)) {
        list = persistentData.get(KEY, GET_COMPOUND_LIST);
        if (!list.isEmpty()) {
          Iterator<INBT> iterator = list.iterator();
          while (iterator.hasNext()) {
            INBT next = iterator.next();
            if (next.getId() == NBT.TAG_COMPOUND) {
              CompoundNBT compoundNBT = (CompoundNBT)next;
              int slot = compoundNBT.getInt(TAG_SLOT);
              if (slot < slots) {
                // ensure we can store the hotbar item
                ItemStack hotbar = player.inventory.getStackInSlot(slot);
                if (hotbar.isEmpty() || !isBlacklisted(hotbar)) {
                  // swap the two items
                  ItemStack parsed = ItemStack.read(compoundNBT);
                  player.inventory.setInventorySlotContents(slot, parsed);
                  if (!hotbar.isEmpty()) {
                    compoundNBT.keySet().clear();
                    hotbar.write(compoundNBT);
                    compoundNBT.putInt(TAG_SLOT, slot);
                  } else {
                    iterator.remove();
                  }
                }
                swapped[slot] = true;
              }
            } else {
              iterator.remove();
            }
          }
        }
      } else {
        // no items
        list = new ListNBT();
        persistentData.put(KEY, list);
      }

      // list is empty, makes loop simplier
      for (int i = 0; i < slots; i++) {
        if (!swapped[i]) {
          ItemStack hotbar = player.inventory.getStackInSlot(i);
          if (!hotbar.isEmpty() && !isBlacklisted(hotbar)) {
            list.add(write(hotbar, i));
            player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
          }
        }
      }

      return true;
    }
    return false;
  }

  @Nullable
  @Override
  public Pattern getPattern(IModifierToolStack tool, int level, int slot, boolean hasStack) {
    return PATTERN;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IArmorInteractModifier.class) {
      return (T) this;
    }
    return super.getModule(type);
  }
}
