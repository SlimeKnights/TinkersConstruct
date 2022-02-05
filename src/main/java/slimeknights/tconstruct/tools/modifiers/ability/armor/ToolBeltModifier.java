package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.isBlacklisted;

public class ToolBeltModifier extends InventoryModifier implements IArmorInteractModifier {
  private static final ResourceLocation KEY = TConstruct.getResource("tool_belt");
  private static final Pattern PATTERN = new Pattern(TConstruct.MOD_ID, "tool_belt");
  public ToolBeltModifier() {
    super(KEY, 4);
  }

  @Override
  public int getPriority() {
    return 85; // after pockets, before shield strap
  }

  @Override
  public int getSlots(IToolContext tool, int level) {
    return Math.min(9, level * 4 + tool.getModifierLevel(TinkerModifiers.pocketChain.get()));
  }

  @Override
  public boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot equipmentSlot) {
    if (!player.isShiftKeyDown()) {
      if (player.level.isClientSide) {
        return false; // TODO: see below
      }

      boolean didChange = false;
      int slots = getSlots(tool, level);
      ModDataNBT persistentData = tool.getPersistentData();
      ListTag list = new ListTag();
      boolean[] swapped = new boolean[slots];
      // if we have existing items, swap stacks at each index
      Inventory inventory = player.getInventory();
      if (persistentData.contains(KEY, Tag.TAG_LIST)) {
        ListTag original = persistentData.get(KEY, GET_COMPOUND_LIST);
        if (!original.isEmpty()) {
          for (int i = 0; i < original.size(); i++) {
            CompoundTag compoundNBT = original.getCompound(i);
            int slot = compoundNBT.getInt(TAG_SLOT);
            if (slot < slots) {
              // ensure we can store the hotbar item
              ItemStack hotbar = inventory.getItem(slot);
              if (hotbar.isEmpty() || !isBlacklisted(hotbar)) {
                // swap the two items
                ItemStack parsed = ItemStack.of(compoundNBT);
                inventory.setItem(slot, parsed);
                if (!hotbar.isEmpty()) {
                  list.add(write(hotbar, slot));
                }
                didChange = true;
              }
              swapped[slot] = true;
            }
          }
        }
      }

      // list is empty, makes loop simplier
      for (int i = 0; i < slots; i++) {
        if (!swapped[i]) {
          ItemStack hotbar = player.getInventory().getItem(i);
          if (!hotbar.isEmpty() && !isBlacklisted(hotbar)) {
            list.add(write(hotbar, i));
            inventory.setItem(i, ItemStack.EMPTY);
            didChange = true;
          }
        }
      }

      // sound effect
      if (didChange) {
        persistentData.put(KEY, list);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
      }
      //return true; TODO: tuning to make this a blocking interaction
    }
    return false;
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, int level, int slot, boolean hasStack) {
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
