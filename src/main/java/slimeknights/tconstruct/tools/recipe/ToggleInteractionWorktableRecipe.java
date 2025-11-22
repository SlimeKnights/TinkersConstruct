package slimeknights.tconstruct.tools.recipe;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.worktable.AbstractWorktableRecipe;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe.LIST_GETTER;

/** Recipe handling toggling for modifiers that work on left and right click.
 * @see slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction
 * @see slimeknights.tconstruct.library.tools.definition.module.interaction.ToggleableSetInteraction
 */
public class ToggleInteractionWorktableRecipe extends AbstractWorktableRecipe {
  private static final Component TITLE = TConstruct.makeTranslation("recipe", "toggle_interaction.title");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "toggle_interaction.description");
  private static final Predicate<ModifierEntry> FILTER = entry -> ModifierManager.isInTag(entry.getId(), TinkerTags.Modifiers.DUAL_INTERACTION);
  /** Loader instance */
  public static final RecordLoadable<ToggleInteractionWorktableRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), TOOL_FIELD, INPUTS_FIELD, ToggleInteractionWorktableRecipe::new);

  /** Cached list of modifiers shown in JEI */
  private List<ModifierEntry> filteredModifiers = null;

  public ToggleInteractionWorktableRecipe(ResourceLocation id, Ingredient toolRequirement, List<SizedIngredient> inputs) {
    super(id, toolRequirement, inputs);
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    if (inv != null && inv.getTinkerable().getModifiers().getModifiers().stream().noneMatch(FILTER)) {
      return ModifierSetWorktableRecipe.NO_MATCHES;
    }
    return DESCRIPTION;
  }

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv == null) {
      if (filteredModifiers == null) {
        filteredModifiers = ModifierRecipeLookup.getRecipeModifierList().stream().filter(FILTER).toList();
      }
      return filteredModifiers;
    }
    return inv.getTinkerable().getModifiers().getModifiers().stream().filter(FILTER).toList();
  }

  @Override
  public RecipeResult<LazyToolStack> getResult(ITinkerableContainer inv, ModifierEntry modifier) {
    ToolStack tool = inv.getTinkerable().copy();
    ModDataNBT persistentData = tool.getPersistentData();
    // start by obtaining the two lists
    ListTag leftList = persistentData.get(InteractionSource.LEFT_CLICK.getKey(), LIST_GETTER);
    ListTag rightList = persistentData.get(InteractionSource.RIGHT_CLICK.getKey(), LIST_GETTER);

    // try removing from both lists
    ModifierId id = modifier.getId();
    boolean removed = ModifierSetWorktableRecipe.isInSet(leftList, id, true) | ModifierSetWorktableRecipe.isInSet(rightList, id, true);
    // if we removed from a list, clear out empty lists
    if (removed) {
      if (leftList.isEmpty()) {
        persistentData.remove(InteractionSource.LEFT_CLICK.getKey());
      }
      if (rightList.isEmpty()) {
        persistentData.remove(InteractionSource.RIGHT_CLICK.getKey());
      }
    } else {
      // did not remove? means we want to add to a list
      // if it can interact on right, means we want it in the left list
      StringTag tag = StringTag.valueOf(id.toString());
      if (tool.getHook(ToolHooks.INTERACTION).canInteract(tool, id, InteractionSource.RIGHT_CLICK)) {
        leftList.add(tag);
        persistentData.put(InteractionSource.LEFT_CLICK.getKey(), leftList);
      } else {
        rightList.add(tag);
        persistentData.put(InteractionSource.RIGHT_CLICK.getKey(), rightList);
      }
    }
    return LazyToolStack.successCopy(tool, inv.getTinkerableStack());
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.toggleInteractionSerializer.get();
  }
}
