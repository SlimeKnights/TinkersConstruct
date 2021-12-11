package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

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
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    super.addVolatileData(context, level, volatileData);
    volatileData.putBoolean(ToolInventoryCapability.INCLUDE_OFFHAND, true);
  }

  @Override
  public int getSlots(IToolContext tool, int level) {
    return level + tool.getModifierLevel(TinkerModifiers.pocketChain.get());
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType equipmentSlot) {
    if (!player.isSneaking()) {
      if (player.world.isRemote) {
        return false; // TODO: see below
      }
      // offhand must be able to go in the pants
      ItemStack offhand = player.getHeldItemOffhand();
      int slots = getSlots(tool, level);
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
            } else if (slot < slots) {
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
          list.add(write(offhand, slots - 1));
        }
        // update offhand
        player.setHeldItem(Hand.OFF_HAND, newOffhand);

        // sound effect
        if (!newOffhand.isEmpty() || !list.isEmpty()) {
          player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
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
