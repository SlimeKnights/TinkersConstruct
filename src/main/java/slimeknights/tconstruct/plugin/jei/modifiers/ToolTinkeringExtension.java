package slimeknights.tconstruct.plugin.jei.modifiers;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayCraftingTinkering;
import slimeknights.tconstruct.plugin.jei.modifiers.AbstractTinkerStationCategory.RecipeLayout;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;
import slimeknights.tconstruct.plugin.jei.util.RecipeSlotWrapper;
import slimeknights.tconstruct.plugin.jei.util.RecipeSlotsWrapper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static slimeknights.mantle.plugin.jei.MantleJEIConstants.getCraftingIndex;
import static slimeknights.tconstruct.plugin.jei.modifiers.AbstractTinkerStationCategory.RESULT_TOOL_SLOT;
import static slimeknights.tconstruct.plugin.jei.modifiers.AbstractTinkerStationCategory.TOOL_SLOT;

/** Crafting extension to display tinkers anvil recipes in the crafting table. Used for some recipes that work in both the tinker station and crafting table. */
@RequiredArgsConstructor
public class ToolTinkeringExtension implements ICraftingCategoryExtension {
  @Nullable
  private static IDrawable information, errorArrow;

  /** Called on startup to create the drawables for this extension */
  @Internal
  public static void prepareDrawables(IGuiHelper helper) {
    information = helper.createDrawable(AbstractTinkerStationCategory.BACKGROUND_LOC, 144, 16, 10, 9);
    errorArrow = helper.createDrawable(AbstractTinkerStationCategory.BACKGROUND_LOC, 144, 33, 22, 17);
  }

  private final IDisplayCraftingTinkering recipe;

  @Nullable
  @Override
  public ResourceLocation getRegistryName() {
    return recipe.getId();
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
    RecipeLayout layout = RecipeLayout.fromRecipe(recipe, focuses, recipe.getInputCount());
    // put tool at index 0, other inputs at 1+
    List<List<ItemStack>> inputs = layout.inputs();
    int inputCount = inputs.size();
    List<ItemStack> toolWithoutModifier = layout.toolWithoutModifier();
    inputs.add(0, toolWithoutModifier);

    // always shapeless
    int size = CategoryUtil.getShapelessSize(inputs.size());
    builder.setShapeless();

    // make slots
    List<IRecipeSlotBuilder> inputSlots = craftingGridHelper.createAndSetInputs(builder, inputs, size, size);
    // mark the tool slot so we can find it easier
    IRecipeSlotBuilder toolSlot = inputSlots.get(getCraftingIndex(0, size, size));
    toolSlot.setSlotName(TOOL_SLOT);
    // output may be a catalyst
    List<ItemStack> toolWithModifier = layout.toolWithModifier();
    IRecipeSlotBuilder outputSlot = builder.addSlot(recipe.isToolCatalyst() ? RecipeIngredientRole.RENDER_ONLY : RecipeIngredientRole.OUTPUT, 95, 19)
      .setOutputSlotBackground().addItemStacks(toolWithModifier).setSlotName(RESULT_TOOL_SLOT);

    // handle singlepart tool focuses
    layout.handleSinglepart(builder, RecipeIngredientRole.INPUT);

    // apply focus links
    int toolSize = toolWithModifier.size();
    if (toolSize > 1) {
      int[] linkToOutput = recipe.linkToOutput();
      if (linkToOutput.length > 0) {
        // if given a list, filter to ensure they are all valid
        // need input slot size to match output size
        IRecipeSlotBuilder[] linked = Stream.concat(
          // include both tool slots if they are the same size
          toolWithoutModifier.size() == toolSize ? Stream.of(outputSlot, toolSlot) : Stream.of(outputSlot),
          Arrays.stream(linkToOutput).filter(i -> i < inputCount && inputs.get(i+1).size() == toolSize).mapToObj(i -> inputSlots.get(getCraftingIndex(i, size, size)))
        ).toArray(IRecipeSlotBuilder[]::new);
        if (linked.length > 0) {
          builder.createFocusLink(linked);
        }
        // if no links, try linking tool to output
      } else if (toolWithoutModifier.size() == toolSize) {
        builder.createFocusLink(toolSlot, outputSlot);
      }
    }
  }

  @Override
  public void onDisplayedIngredientsUpdate(List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
    // handle dynamic hook
    if (recipe.isSlotsDynamic()) {
      List<IRecipeSlotDrawable> inputs = new ArrayList<>(5);
      int inputCount = recipe.getInputCount();
      int size = CategoryUtil.getShapelessSize(inputCount + 1);
      for (int i = 0; i < inputCount; i++) {
        inputs.add(recipeSlots.get(getCraftingIndex(i+1, size, size)));
      }
      recipe.onDisplayUpdate(
        RecipeSlotWrapper.createItem(recipeSlots.get(getCraftingIndex(0, size, size))),
        RecipeSlotsWrapper.createItem(inputs),
        RecipeSlotWrapper.createItem(recipeSlots, RESULT_TOOL_SLOT)
      );
    }
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
    // add title and tooltip via an icon
    if (information != null) {
      builder.addDrawableWidget(information).setPosition(56, 0).setTooltip(recipe.getInformation());
    }
    Component variantText = recipe.getVariant();
    if (variantText != null) {
      builder.addText(variantText, 57, 9).setPosition(60, 44).setColor(Color.GRAY.getRGB());
    }
    if (errorArrow != null) {
      AbstractTinkerStationCategory.createErrorArrow(builder, recipe, errorArrow, 61, 19);
    }
  }
}
