package slimeknights.tconstruct.library.recipe.worktable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe.MAP_TOOL_FOR_RENDERING;

/** Default implementation of modifier worktable recipes, taking a list of inputs */
@RequiredArgsConstructor
public abstract class AbstractWorktableRecipe implements IModifierWorktableRecipe {
  @Getter
  private final ResourceLocation id;
  protected final List<SizedIngredient> inputs;

  /* JEI */
  @Nullable
  private List<ItemStack> tools;

  @Override
  public boolean matches(ITinkerableContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(TinkerTags.Items.MODIFIABLE)) {
      return false;
    }
    return ModifierRecipe.checkMatch(inv, inputs);
  }

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv == null) {
      return ModifierRecipeLookup.getRecipeModifierList();
    }
    return inv.getTinkerable().getUpgrades().getModifiers();
  }

  @Override
  public void updateInputs(IToolStackView result, ITinkerableContainer.Mutable inv, boolean isServer) {
    ModifierRecipe.updateInputs(inv, inputs);
  }


  /* JEI */

  /** Gets a list of tools to display */
  @Override
  public List<ItemStack> getInputTools() {
    if (tools == null) {
      tools = RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.DURABILITY).map(MAP_TOOL_FOR_RENDERING).toList();
    }
    return tools;
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    if (slot < 0 || slot >= inputs.size()) {
      return Collections.emptyList();
    }
    return inputs.get(slot).getMatchingStacks();
  }

  @Override
  public int getInputCount() {
    return inputs.size();
  }
}
