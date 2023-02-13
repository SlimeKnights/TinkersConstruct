package slimeknights.tconstruct.tools.modifiers.ability.armor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.isBlacklisted;

public class ToolBeltModifier extends InventoryMenuModifier {
  private static final Pattern PATTERN = new Pattern(TConstruct.MOD_ID, "tool_belt");
  private static final ResourceLocation SLOT_OVERRIDE = TConstruct.getResource("tool_belt_override");

  /** Loader instance */
  public static final IGenericLoader<ToolBeltModifier> LOADER = new IGenericLoader<>() {
    @Override
    public ToolBeltModifier deserialize(JsonObject json) {
      JsonArray slotJson = GsonHelper.getAsJsonArray(json, "level_slots");
      int[] slots = new int[slotJson.size()];
      for (int i = 0; i < slots.length; i++) {
        slots[i] = GsonHelper.convertToInt(slotJson.get(i), "level_slots["+i+"]");
        if (i > 0 && slots[i] <= slots[i-1]) {
          throw new JsonSyntaxException("level_slots must be increasing");
        }
      }
      return new ToolBeltModifier(slots);
    }

    @Override
    public ToolBeltModifier fromNetwork(FriendlyByteBuf buffer) {
      return new ToolBeltModifier(buffer.readVarIntArray());
    }

    @Override
    public void serialize(ToolBeltModifier object, JsonObject json) {
      JsonArray jsonArray = new JsonArray();
      for (int i : object.counts) {
        jsonArray.add(i);
      }
      json.add("level_slots", jsonArray);
    }

    @Override
    public void toNetwork(ToolBeltModifier object, FriendlyByteBuf buffer) {
      buffer.writeVarIntArray(object.counts);
    }
  };

  private final int[] counts;
  public ToolBeltModifier(int[] counts) {
    super(counts[0]);
    this.counts = counts;
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  @Override
  public Component getDisplayName(int level) {
    return ModifierLevelDisplay.PLUSES.nameForLevel(this, level);
  }

  @Override
  public int getPriority() {
    return 85; // after shield strap, before pockets
  }

  /** Gets the proper number of slots for the given level */
  private int getProperSlots(int level) {
    if (level <= 0) {
      return 0;
    }
    if (level > counts.length) {
      return 9;
    } else {
      return counts[level - 1];
    }
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    int properSlots = getProperSlots(level);
    int slots;
    // find the largest slot index and either add or update the override as needed
    // TODO: can probably remove this code for 1.19
    if (properSlots < 9) {
      slots = properSlots;
      ResourceLocation key = getInventoryKey();
      IModDataView modData = context.getPersistentData();
      if (modData.contains(key, Tag.TAG_LIST)) {
        ListTag list = modData.get(key, GET_COMPOUND_LIST);
        int maxSlot = 0;
        for (int i = 0; i < list.size(); i++) {
          int newSlot = list.getCompound(i).getInt(TAG_SLOT);
          if (newSlot > maxSlot) {
            maxSlot = newSlot;
          }
        }
        maxSlot = Math.min(maxSlot + 1, 9);
        if (maxSlot > properSlots) {
          volatileData.putInt(SLOT_OVERRIDE, maxSlot);
          slots = maxSlot;
        }
      }
    } else {
      slots = 9;
    }
    ToolInventoryCapability.addSlots(volatileData, slots);
  }

  @Override
  public int getSlots(IToolContext tool, int level) {
    int properSlots = getProperSlots(level);
    if (properSlots >= 9) {
      return 9;
    }
    return Mth.clamp(tool.getVolatileData().getInt(SLOT_OVERRIDE), properSlots, 9);
  }

  @Override
  public ValidatedResult validate(IToolStackView tool, int level) {
    return validateForMaxSlots(tool, getProperSlots(level));
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot equipmentSlot, TooltipKey keyModifier) {
    if (keyModifier == TooltipKey.SHIFT) {
      return super.startInteract(tool, modifier, player, equipmentSlot, keyModifier);
    }
    if (keyModifier == TooltipKey.NORMAL || keyModifier == TooltipKey.CONTROL) {
      if (player.level.isClientSide) {
        return true;
      }

      boolean didChange = false;
      int slots = getSlots(tool, modifier.getLevel());
      ModDataNBT persistentData = tool.getPersistentData();
      ListTag list = new ListTag();
      boolean[] swapped = new boolean[slots];
      // if we have existing items, swap stacks at each index
      Inventory inventory = player.getInventory();
      ResourceLocation key = getInventoryKey();
      if (persistentData.contains(key, Tag.TAG_LIST)) {
        ListTag original = persistentData.get(key, GET_COMPOUND_LIST);
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
              } else {
                list.add(compoundNBT);
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
        persistentData.put(key, list);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
      }
      return true;
    }
    return false;
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
    return hasStack ? null : PATTERN;
  }
}
