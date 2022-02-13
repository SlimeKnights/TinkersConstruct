package slimeknights.tconstruct.library.recipe.modifiers.adding;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/** Common interface for modifier recipes that can show in JEI */
public interface IDisplayModifierRecipe extends IModifierRecipe {
  /** Gets the number of inputs for this recipe */
  int getInputCount();

  /**
   * Gets an ingredients to display in JEI.
   * @param  slot  Slot index to display
   * @return  Display item list
   */
  List<ItemStack> getDisplayItems(int slot);

  /** Gets the result tool before adding the modifier */
  List<ItemStack> getToolWithoutModifier();

  /** Gets the result tool with this modifier added */
  List<ItemStack> getToolWithModifier();

  /** Gets the modifier output of this recipe */
  ModifierEntry getDisplayResult();

  @Override
  default Modifier getModifier() {
    return getDisplayResult().getModifier();
  }

  /**
   * Gets the max level of this modifier
   * @return modifier max level, 0 if no max level
   */
  default int getMaxLevel() {
    return 0;
  }

  /** Gets the slot type used by this modifier */
  @Nullable
  default SlotCount getSlots() {
    return null;
  }

  @Nullable
  @Override
  default SlotType getSlotType() {
    SlotCount count = getSlots();
    if (count == null) {
      return null;
    }
    return count.getType();
  }

  /** If true, this recipe has additional requirements */
  default boolean hasRequirements() {
    return false;
  }

  /** Gets the message to display when requirements do not match, or empty if no requirements */
  default String getRequirementsError() {
    return "";
  }

  /** If true, this recipe can be applied incrementally */
  default boolean isIncremental() {
    return false;
  }


  /* Helpers */

  /** Gets a stream of all modifiable items for display */
  static Stream<Item> getAllModifiable() {
    return TinkerTags.Items.MODIFIABLE.getValues().stream();
  }

  /** Maps the stream from tool items to applicable tool stacks */
  Function<Item,ItemStack> MAP_TOOL_FOR_RENDERING = IModifiableDisplay::getDisplayStack;

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, @Nullable ModifierMatch match, @Nullable ModifierEntry newModifier) {
    return withModifiers(stack, match, newModifier, data -> {});
  }

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, @Nullable ModifierMatch match, @Nullable ModifierEntry newModifier, Consumer<ModDataNBT> persistentDataConsumer) {
    ItemStack output = stack.copy();
    CompoundTag nbt = output.getOrCreateTag();

    // build modifiers list
    ModifierNBT.Builder builder = ModifierNBT.builder();
    if (match != null) {
      match.apply(builder);
    }
    if (newModifier != null) {
      builder.add(newModifier);
    }
    ModifierNBT modifiers = builder.build();
    ListTag list = modifiers.serializeToNBT();
    nbt.put(ToolStack.TAG_UPGRADES, list);
    nbt.put(ToolStack.TAG_MODIFIERS, list);

    // build persistent and volatile NBT
    CompoundTag persistentNBT = new CompoundTag();
    ModDataNBT persistentData = ModDataNBT.readFromNBT(persistentNBT);
    CompoundTag volatileNBT = new CompoundTag();
    ModDataNBT volatileData = ModDataNBT.readFromNBT(volatileNBT);
    persistentDataConsumer.accept(persistentData);
    ToolRebuildContext context = new ToolRebuildContext(stack.getItem(), ToolDefinition.EMPTY, MaterialNBT.EMPTY, modifiers, modifiers, StatsNBT.EMPTY, persistentData, volatileData);
    for (ModifierEntry entry : modifiers.getModifiers()) {
      entry.getModifier().addVolatileData(context, entry.getLevel(), volatileData);
    }
    nbt.put(ToolStack.TAG_VOLATILE_MOD_DATA, volatileNBT);
    nbt.put(ToolStack.TAG_PERSISTENT_MOD_DATA, persistentNBT);

    return output;
  }
}
