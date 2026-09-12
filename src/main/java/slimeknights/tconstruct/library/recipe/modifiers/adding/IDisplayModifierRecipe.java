package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.Streams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.RecipeSlot;
import slimeknights.tconstruct.library.recipe.RecipeSlots;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayTinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/** Common interface for modifier recipes that can show in JEI */
public interface IDisplayModifierRecipe extends IModifierRecipe, IDisplayTinkerStationRecipe {
  /** Gets the ID of this recipe. If this is a generated display recipe, uses the parent recipe ID */
  @Override
  @Nullable
  default ResourceLocation getRecipeId() {
    return null;
  }

  /** Gets the modifier output of this recipe */
  ModifierEntry getDisplayResult();

  @Override
  default Modifier getModifier() {
    return getDisplayResult().getModifier();
  }

  /**
   * Gets the range this modifier is valid for
   * @return  level range, defaults to {@link ModifierEntry#VALID_LEVEL}
   */
  default IntRange getLevel() {
    return ModifierEntry.VALID_LEVEL;
  }

  /** If true, this recipe checks the level from {@link IToolStackView#getModifiers()}. If false, it uses the level from {@link IToolStackView#getUpgrades()} */
  default boolean checkTraitLevel() {
    return false;
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
    return count.type();
  }

  /** If true, this recipe can be applied incrementally */
  default boolean isIncremental() {
    return false;
  }

  /** Gets a list of modifier slots obtained from this recipe. Generally just redirects to modifier behavior. */
  default List<SlotCount> getResultSlots() {
    return List.of();
  }


  /* Dynamic updates */

  @Override
  default boolean isSlotsDynamic() {
    return true;
  }

  @Override
  default void onDisplayUpdate(RecipeSlot<ItemStack> tool, RecipeSlots<ItemStack> inputs, RecipeSlot<ItemStack> output, ItemStack focus, boolean focusOutput) {
    // if the focus is a tool, filter the tools to show only the focus
    if (!focusOutput && !focus.isEmpty() && isTool(focus)) {
      // TODO: this is only needed as long as singlepart tool is a thing, we can ditch if we remove it
      Item item = focus.getItem();
      tool.set(getToolWithoutModifier().stream().filter(stack -> stack.is(item)).toList());
      output.set(getToolWithModifier().stream().filter(stack -> stack.is(item)).toList());
    }
  }

  /* Helpers */

  // TODO 1.21: move next two helpers to IDisplayTinkerStationRecipe
  /** Maps the stream from tool items to applicable tool stacks */
  Function<Item,ItemStack> MAP_TOOL_FOR_RENDERING = IModifiableDisplay::getDisplayStack;
  /** Maps the stream from tool items to applicable tool stacks */
  Function<ItemStack,ItemStack> MAP_TOOL_STACK_FOR_RENDERING = stack -> stack.getItem() instanceof IModifiableDisplay display ? display.getRenderTool() : stack;

  /**
   * Gets the list of modifiers to display for the given result
   * @param result  Resulting modifier
   * @param self    Current modifier to display, will typically be result or a lower level of result
   * @return  List of modifiers
   */
  static List<ModifierEntry> modifiersForResult(ModifierEntry result, @Nullable ModifierEntry self) {
    List<ModifierEntry> requirements = result.getHook(ModifierHooks.REQUIREMENTS).displayModifiers(result);
    if (self != null) {
      return Streams.concat(requirements.stream(), Stream.of(self)).toList();
    }
    return requirements;
  }

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, List<ModifierEntry> modifiers) {
    return withModifiers(stack, modifiers, data -> {});
  }

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, int maxSize, List<ModifierEntry> modifiers) {
    return withModifiers(stack, maxSize, modifiers, data -> {});
  }

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, List<ModifierEntry> modifierList, Consumer<ModDataNBT> persistentDataConsumer) {
    return withModifiers(stack, 1, modifierList, persistentDataConsumer);
  }

  /* Gets a copy of the stack with the given modifiers */
  static ItemStack withModifiers(ItemStack stack, int maxSize, List<ModifierEntry> modifierList, Consumer<ModDataNBT> persistentDataConsumer) {
    ItemStack output = stack.copyWithCount(Math.min(stack.getMaxStackSize(), maxSize));
    CompoundTag nbt = output.getOrCreateTag();

    // build modifiers list
    // go through the builder to ensure they are merged properly
    ModifierNBT modifiers = ModifierNBT.builder().add(modifierList).build();
    ListTag list = modifiers.serializeToNBT();
    nbt.put(ToolStack.TAG_UPGRADES, list);
    nbt.put(ToolStack.TAG_MODIFIERS, list);

    // build persistent and volatile NBT
    CompoundTag persistentNBT = new CompoundTag();
    ModDataNBT persistentData = ModDataNBT.readFromNBT(persistentNBT);
    CompoundTag volatileNBT = new CompoundTag();
    ToolDataNBT volatileData = ToolDataNBT.readFromNBT(volatileNBT);
    persistentDataConsumer.accept(persistentData);
    ToolRebuildContext context = new ToolRebuildContext(stack.getItem(), ToolDefinition.EMPTY, MaterialNBT.EMPTY, modifiers, modifiers, persistentData);
    for (ModifierEntry entry : modifiers.getModifiers()) {
      entry.getHook(ModifierHooks.VOLATILE_DATA).addVolatileData(context, entry, volatileData);
    }
    nbt.put(ToolStack.TAG_VOLATILE_MOD_DATA, volatileNBT);
    nbt.put(ToolStack.TAG_PERSISTENT_MOD_DATA, persistentNBT);

    return output;
  }
}
