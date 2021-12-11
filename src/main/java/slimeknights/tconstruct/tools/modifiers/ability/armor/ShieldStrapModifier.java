package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import javax.annotation.Nullable;

public class ShieldStrapModifier extends InventoryModifier implements IArmorInteractModifier {
  private static final ResourceLocation KEY = TConstruct.getResource("shield_strap");
  private static final Pattern PATTERN = new Pattern(TConstruct.MOD_ID, "shield_plus");
  public ShieldStrapModifier() {
    super(0x01cbcd, KEY, 1);
  }

  @Override
  public int getPriority() {
    return 75; // after pockets
  }

  @Override
  public void addVolatileData(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    super.addVolatileData(item, toolDefinition, baseStats, persistentData, level, volatileData);
    volatileData.putBoolean(ToolInventoryCapability.INCLUDE_OFFHAND, true);
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType equipmentSlot) {
    if (!player.isSneaking()) {
      if (player.world.isRemote) {
        return false; // TODO: see below
      }
      // offhand must be able to go in the pants
      ItemStack offhand = player.getHeldItemOffhand();
      if (offhand.isEmpty() || !ToolInventoryCapability.isBlacklisted(offhand)) {
        ItemStack newOffhand = ItemStack.EMPTY;
        ModDataNBT persistentData = tool.getPersistentData();
        ListNBT list;
        // if we have existing items, shift all back by 1
        if (persistentData.contains(KEY, NBT.TAG_LIST)) {
          list = persistentData.get(KEY, GET_COMPOUND_LIST);
          boolean removeFirst = false; // if true, need to remove list element at index 0
          for (int i = 0; i < list.size(); i++) {
            CompoundNBT compoundNBT = list.getCompound(i);
            int slot = compoundNBT.getInt(TAG_SLOT);
            if (slot == 0) {
              newOffhand = ItemStack.read(compoundNBT);
              removeFirst = true;
            } else {
              compoundNBT.putInt(TAG_SLOT, slot - 1);
            }
          }
          if (removeFirst) {
            list.remove(0);
          }
        } else {
          list = new ListNBT();
          persistentData.put(KEY, list);
        }
        // add old offhand to the list
        if (!offhand.isEmpty()) {
          list.add(write(offhand, level - 1));
        }
        // update offhand, return true if something happened
        player.setHeldItem(Hand.OFF_HAND, newOffhand);
        //return true; TODO: tuning to make this a blocking interaction
      }
    }
    return false;
  }

  @Nullable
  @Override
  public Pattern getPattern(IModifierToolStack tool, int level, int slot, boolean hasStack) {
    return hasStack ? null : PATTERN;
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
