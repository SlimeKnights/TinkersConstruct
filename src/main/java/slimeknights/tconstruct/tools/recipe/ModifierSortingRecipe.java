package slimeknights.tconstruct.tools.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.worktable.AbstractWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/** Reorders modifiers ion a tool */
public class ModifierSortingRecipe extends AbstractWorktableRecipe {
  private static final Component TITLE = TConstruct.makeTranslation("recipe", "modifier_sorting.title");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "modifier_sorting.description");
  private static final Component NOT_ENOUGH_MODIFIERS = TConstruct.makeTranslation("recipe", "modifier_sorting.not_enough_modifiers");

  public static final RecordLoadable<ModifierSortingRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), INPUTS_FIELD, ModifierSortingRecipe::new);

  public ModifierSortingRecipe(ResourceLocation id, List<SizedIngredient> inputs) {
    super(id, inputs);
  }

  @Override
  public boolean matches(ITinkerableContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(TinkerTags.Items.MODIFIABLE)) {
      return false;
    }
    return ModifierRecipe.checkMatch(inv, inputs);
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    if (inv != null && inv.getTinkerable().getUpgrades().getModifiers().size() < 2) {
      return NOT_ENOUGH_MODIFIERS;
    }
    return DESCRIPTION;
  }

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv == null) {
      return ModifierRecipeLookup.getRecipeModifierList();
    }
    List<ModifierEntry> modifiers = inv.getTinkerable().getUpgrades().getModifiers();
    if (modifiers.size() < 2) {
      return List.of();
    }
    return modifiers;
  }

  @Override
  public RecipeResult<LazyToolStack> getResult(ITinkerableContainer inv, ModifierEntry modifier) {
    ToolStack tool = inv.getTinkerable();

    // find the modifier to remove
    List<ModifierEntry> upgrades = tool.getUpgrades().getModifiers();
    int toMove = IntStream.range(0, upgrades.size())
                          .filter(i -> upgrades.get(i).matches(modifier.getId()))
                          .findFirst().orElse(-1);
    // if no change, no need to do anything
    if (toMove == -1) {
      return RecipeResult.pass();
    }

    // reorder
    tool = tool.copy();
    List<ModifierEntry> newUpgrades = new ArrayList<>(upgrades);
    Collections.swap(newUpgrades, toMove, toMove == upgrades.size() - 1 ? 0 : toMove + 1);
    tool.setUpgrades(new ModifierNBT(newUpgrades));

    // no need to validate, its the same modifiers
    return RecipeResult.success(LazyToolStack.from(tool, inv.getTinkerableSize()));
  }

  @Override
  public void updateInputs(LazyToolStack result, ITinkerableContainer.Mutable inv, ModifierEntry selected, boolean isServer) {
    // input is not consumed
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSortingSerializer.get();
  }
}
