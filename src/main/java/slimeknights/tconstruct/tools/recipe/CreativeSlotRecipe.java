package slimeknights.tconstruct.tools.recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;
import slimeknights.tconstruct.tools.modifiers.slotless.CreativeSlotModifier;

import javax.annotation.Nullable;

/**
 * Recipe to add additional slots with the creative modifier
 */
@RequiredArgsConstructor
public class CreativeSlotRecipe implements ITinkerStationRecipe, IModifierRecipe {
  @Getter
  private final ResourceLocation id;

  /**
   * Finds the slot type from the inventory
   * @param inv             Inventory
   * @param stopAfterFirst  If true, stops after the first item is found
   * @return  Slot type found, or null if invalid
   */
  @Nullable
  private SlotType findSlotType(ITinkerStationContainer inv, boolean stopAfterFirst) {
    // goal is to find exactly 1 stack of creative modifiers
    SlotType type = null;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // must be the first creative slot modifer, anymore than one is invalid
        if (type != null || stack.getItem() != TinkerModifiers.creativeSlotItem.get()) {
          return null;
        }
        // must have a valid slot
        type = CreativeSlotItem.getSlot(stack);
        if (type == null) {
          return null;
        }
        if (stopAfterFirst) {
          return type;
        }
      }
    }
    return type;
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    // must be modifiable
    if (!inv.getTinkerableStack().is(TinkerTags.Items.MODIFIABLE)) {
      return false;
    }
    return findSlotType(inv, false) != null;
  }

  @Override
  public int shrinkToolSlotBy() {
    return 64;
  }

  @Override
  public RecipeResult<ItemStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack toolStack = inv.getTinkerable().copy();

    // first, fetch the slots compound
    CompoundTag slots;
    ModDataNBT persistentData = toolStack.getPersistentData();
    if (persistentData.contains(CreativeSlotModifier.KEY_SLOTS, Tag.TAG_COMPOUND)) {
      slots = persistentData.getCompound(CreativeSlotModifier.KEY_SLOTS);
    } else {
      slots = new CompoundTag();
      persistentData.put(CreativeSlotModifier.KEY_SLOTS, slots);
    }

    // find the input
    SlotType slotType = findSlotType(inv, true);
    if (slotType != null) {
      String name = slotType.getName();
      slots.putInt(name, slots.getInt(name) + 1);
    }

    // add the modifier if needed
    ModifierId creative = TinkerModifiers.creativeSlot.getId();
    if (toolStack.getModifierLevel(creative) == 0) {
      toolStack.addModifier(creative, 1);
    } else {
      toolStack.rebuildStats();
    }
    return RecipeResult.success(toolStack.createStack(inv.getTinkerableSize()));
  }

  @Override
  public Modifier getModifier() {
    return TinkerModifiers.creativeSlot.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.creativeSlotSerializer.get();
  }
}
