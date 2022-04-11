package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

public class ShieldStrapModifier extends InventoryModifier implements IArmorInteractModifier {
  private static final ResourceLocation KEY = TConstruct.getResource("shield_strap");
  private static final Pattern PATTERN = new Pattern(TConstruct.MOD_ID, "shield_plus");
  public ShieldStrapModifier() {
    super(KEY, 1);
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
    return level + tool.getModifierLevel(TinkerModifiers.pocketChain.getId());
  }

  @Override
  public boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot equipmentSlot) {
    if (!player.isShiftKeyDown()) {
      if (player.level.isClientSide) {
        return false; // TODO: see below
      }
      // offhand must be able to go in the pants
      ItemStack offhand = player.getOffhandItem();
      int slots = getSlots(tool, level);
      if (offhand.isEmpty() || !ToolInventoryCapability.isBlacklisted(offhand)) {
        ItemStack newOffhand = ItemStack.EMPTY;
        ModDataNBT persistentData = tool.getPersistentData();
        ListTag list = new ListTag();
        // if we have existing items, shift all back by 1
        if (persistentData.contains(KEY, Tag.TAG_LIST)) {
          ListTag original = persistentData.get(KEY, GET_COMPOUND_LIST);
          for (int i = 0; i < original.size(); i++) {
            CompoundTag compoundNBT = original.getCompound(i);
            int slot = compoundNBT.getInt(TAG_SLOT);
            if (slot == 0) {
              newOffhand = ItemStack.of(compoundNBT);
            } else if (slot < slots) {
              CompoundTag copy = compoundNBT.copy();
              copy.putInt(TAG_SLOT, slot - 1);
              list.add(copy);
            }
          }
        }
        // add old offhand to the list
        if (!offhand.isEmpty()) {
          list.add(write(offhand, slots - 1));
        }
        // update offhand
        persistentData.put(KEY, list);
        player.setItemInHand(InteractionHand.OFF_HAND, newOffhand);

        // sound effect
        if (!newOffhand.isEmpty() || !list.isEmpty()) {
          player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        //return true; TODO: tuning to make this a blocking interaction
      }
    }
    return false;
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, int level, int slot, boolean hasStack) {
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
