package slimeknights.tconstruct.plugin.jei.material;

import com.google.common.collect.Streams;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.material.MaterialsCraftingTableRecipe;
import slimeknights.tconstruct.library.recipe.material.ShapelessMaterialsRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Common logic for {@link ShapedMaterialsExtension} and {@link ShapelessMaterialsRecipe} */
public class MaterialsCraftingExtension<T extends CraftingRecipe & MaterialsCraftingTableRecipe> implements ICraftingCategoryExtension {
  private static final String RESULT_SLOT = "result";

  protected final T recipe;
  private final ItemStack plainResult;
  private final List<ItemStack> result;
  /** List of slot indices matching the first part to link to the output */
  @Nullable
  private final int[] outputLink;
  /** List of slot indices matching each part */
  private final List<int[]> partSlots;

  public MaterialsCraftingExtension(T recipe) {
    this.recipe = recipe;
    this.plainResult = recipe.getResultItem(Objects.requireNonNull(SafeClientAccess.getRegistryAccess()));

    // if we have just the one part, set the output to match its material
    if (recipe.getPartCount() == 1) {
      Ingredient firstPart = recipe.getParts().get(0);
      this.result = Arrays.stream(firstPart.getItems()).map(variant -> {
        ItemStack stack = plainResult.copy();
        recipe.setMaterial(stack, MaterialRecipeCache.getMaterial(variant));
        return stack;
      }).toList();
      this.outputLink = getMaterialSlots(recipe, firstPart);
      this.partSlots = List.of();
      // otherwise, use a display material. allow display tool part if it has just 1 material
    } else {
      // just set a display tool, it should never be seen
      this.result = List.of(IModifiableDisplay.getDisplayStack(plainResult));
      this.outputLink = null;
      this.partSlots = recipe.getParts().stream().map(part -> getMaterialSlots(recipe, part)).toList();
    }
  }

  /** {@return Instance of the shapeless extension, or null if the recipe is invalid for display} */
  @Nullable
  public static MaterialsCraftingExtension<ShapelessMaterialsRecipe> shapeless(ShapelessMaterialsRecipe recipe) {
    // TODO 1.21: move this to ShapelessMaterialsExtension
    List<Ingredient> parts = recipe.getIngredients();
    for (int i = 0; i < recipe.getPartCount(); i++) {
      if (parts.get(i).getItems().length == 0) {
        return null;
      }
    }
    return new ShapelessMaterialsExtension(recipe);
  }

  /** Gets the material slots for the given recipe. Indices should be mapped via {@link slimeknights.mantle.plugin.jei.MantleJEIConstants#getCraftingIndex(int, int, int)} */
  protected int[] getMaterialSlots(T recipe, Ingredient firstPart) {
    return new int[] {0};
  }

  @Override
  public ResourceLocation getRegistryName() {
    return recipe.getId();
  }

  /** Gets a modifiable list of inputs to the given recipe */
  private static List<List<ItemStack>> getInputs(CraftingRecipe recipe) {
    // using Collectors.toList to ensure we can mutate the list
    return recipe.getIngredients().stream().map(ingredient -> List.of(ingredient.getItems())).collect(Collectors.toList());
  }

  /** Sets the recipe in the builder */
  @Deprecated
  public static void setRecipe(ICraftingCategoryExtension self, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, CraftingRecipe recipe, List<ItemStack> result, ItemStack plainResult, @Nullable int[] materialSlots) {
    setRecipe(self, builder, craftingGridHelper, recipe.getId(), getInputs(recipe), result, plainResult, materialSlots);
  }

  /** Sets the recipe in the builder */
  public static void setRecipe(ICraftingCategoryExtension self, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ResourceLocation id, List<List<ItemStack>> inputStacks, List<ItemStack> result, ItemStack plainResult, @Nullable int[] materialSlots) {
    builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(plainResult);

    // shapeless needs its width and height set, but we also want to recover those sizes, so calculate it locally
    int width = self.getWidth();
    int height = self.getHeight();
    if (width <= 0 || height <= 0) {
      width = height = getShapelessSize(inputStacks.size());
      builder.setShapeless();
    }
    List<IRecipeSlotBuilder> inputs = craftingGridHelper.createAndSetInputs(builder, inputStacks, width, height);
    IRecipeSlotBuilder output = craftingGridHelper.createAndSetOutputs(builder, result).setSlotName(RESULT_SLOT);
    if (inputs.size() != 9) {
      Mantle.logger.error("Failed to create focus link for {} as the layout {} is not 3x3", id, builder.getClass().getName());
    } else if (materialSlots != null) {
      // apply focus links
      builder.createFocusLink(Streams.concat(
        Stream.of(output),
        Arrays.stream(materialSlots).mapToObj(inputs::get)
      ).toArray(IRecipeSlotBuilder[]::new));
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
    List<List<ItemStack>> inputs = getInputs(recipe);

    // if we have a focused tool, use it to filter the inputs to match
    // we just assume the first 9 slots are inputs to avoid needing to make a new list
    ItemStack focus = CategoryUtil.getResultItemFocus(focuses);
    // if we have an output focus, set the input slots to match
    outputFocus:
    if (!focus.isEmpty()) {
      MaterialIdNBT materials = MaterialIdNBT.from(focus);
      // loop through finding all overrides to display, but don't override yet in case any materials on the tool are absent
      int partCount = partSlots.size();
      List<List<ItemStack>> slotOverrides = new ArrayList<>(partCount);
      for (int i = 0; i < partCount; i++) {
        // find all inputs matching the material
        MaterialVariantId material = materials.getMaterial(i);
        List<ItemStack> matchingStacks = Arrays.stream(recipe.getParts().get(i).getItems())
          .filter(stack -> material.matchesVariant(MaterialRecipeCache.getMaterial(stack)))
          .toList();
        // if filtered to empty, just display the original stacks
        // this happens if the focus has materials that are not in this recipe
        if (matchingStacks.isEmpty()) break outputFocus;
        slotOverrides.add(matchingStacks);
      }
      // all materials are present, so time to override
      for (int i = 0; i < partCount; i++) {
        List<ItemStack> override = slotOverrides.get(i);
        for (int slot : partSlots.get(i)) {
          inputs.set(slot, override);
        }
      }
    }
    setRecipe(this, builder, craftingGridHelper, recipe.getId(), inputs, result, plainResult, outputLink);
  }

  @Override
  public void onDisplayedIngredientsUpdate(List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
    // don't care if only 1 part
    if (partSlots.size() > 1) {
      IRecipeSlotDrawable resultSlot = CategoryUtil.findSlot(recipeSlots, RESULT_SLOT);
      if (resultSlot != null) {
        // find input materials and use to set the output
        List<MaterialVariantId> variants = new ArrayList<>(partSlots.size());
        for (int[] partSlot : partSlots) {
          ItemStack stack = recipeSlots.get(partSlot[0]).getDisplayedItemStack().orElse(ItemStack.EMPTY);
          variants.add(MaterialRecipeCache.getMaterial(stack));
        }
        variants.addAll(recipe.getExtraMaterials());
        resultSlot.createDisplayOverrides().addItemStack(new MaterialIdNBT(variants).updateStack(plainResult.copy()));
      }
    }
  }

  /** Gets the width and height of the grid for a shapeless recipe. */
  static int getShapelessSize(int total) {
    if (total > 4) {
      return 3;
    } else if (total > 1) {
      return 2;
    } else {
      return 1;
    }
  }
}
