package slimeknights.tconstruct.plugin.jei.material;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.material.MaterialsCraftingTableRecipe;
import slimeknights.tconstruct.library.recipe.material.ShapelessMaterialsRecipe;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/** Common logic for {@link ShapedMaterialsExtension} and {@link #shapeless(ShapelessMaterialsRecipe)} */
public class MaterialsCraftingExtension<T extends CraftingRecipe & MaterialsCraftingTableRecipe> implements ICraftingCategoryExtension {
  protected final T recipe;
  private final ItemStack plainResult;
  private final List<List<ItemStack>> inputs;
  private final List<ItemStack> result;
  @Nullable
  private final int[] materialSlots;

  public MaterialsCraftingExtension(T recipe) {
    this.recipe = recipe;
    this.plainResult = recipe.getResultItem(Objects.requireNonNull(SafeClientAccess.getRegistryAccess()));

    // if we have just the one part, set the output to match its material
    int partCount = recipe.getPartCount();
    if (partCount == 1) {
      Ingredient firstPart = recipe.getParts().get(0);
      this.result = Arrays.stream(firstPart.getItems()).map(variant -> {
        ItemStack stack = plainResult.copy();
        if (variant.getItem() instanceof IMaterialItem materialItem) {
          recipe.setMaterial(stack, materialItem.getMaterial(variant));
        } else {
          recipe.setMaterial(stack, MaterialRecipeCache.findRecipe(variant).getMaterial().getVariant());
        }
        return stack;
      }).toList();
      this.materialSlots = getMaterialSlots(recipe, firstPart);
      this.inputs = getInputs(recipe.getIngredients());
      // otherwise, use a display material. allow display tool part if it has just 1 material
    } else {
      // expand parts out to all combinations of parts
      List<Ingredient> partIngredients = recipe.getParts();
      int[] partSizes = new int[partCount];
      int product = 1;
      // to start, we need to know how big each group is
      for (int i = 0; i < partCount; i++) {
        int size = partIngredients.get(i).getItems().length;
        partSizes[i] = size;
        product *= size;
      }
      List<List<ItemStack>> partItems = new ArrayList<>(partCount);
      for (int part = 0; part < partCount; part++) {
        // item repeats once per previous part
        int repeatItem = 1;
        for (int i = 0; i < part; i++) {
          repeatItem *= partSizes[i];
        }
        // entire list repeats once per future part
        int repeatGroup = 1;
        for (int i = part + 1; i < partCount; i++) {
          repeatGroup *= partSizes[i];
        }
        ItemStack[] originalStacks = partIngredients.get(part).getItems();
        List<ItemStack> finalList = new ArrayList<>(product);
        for (int i = 0; i < repeatGroup; i++) {
          for (ItemStack stack : originalStacks) {
            for (int j = 0; j < repeatItem; j++) {
              finalList.add(stack);
            }
          }
        }
        partItems.add(List.copyOf(finalList));
      }
      // next, it's time to build the ingredient lists
      List<Ingredient> ingredients = recipe.getIngredients();
      int ingredientCount = ingredients.size();
      List<List<ItemStack>> inputs = new ArrayList<>(ingredientCount);
      IntList materialSlots = new IntArrayList();
      ingredientLoop:
      for (int i = 0; i < ingredientCount; i++) {
        Ingredient ingredient = ingredients.get(i);
        // if it matches a part ingredient, replace with the expanded list and add to our focus links
        for (int part = 0; part < partCount; part++) {
          if (ingredient == partIngredients.get(part)) {
            inputs.add(partItems.get(part));
            materialSlots.add(i);
            continue ingredientLoop;
          }
        }
        // not a part? add its items directly with no focus link
        inputs.add(List.of(ingredient.getItems()));
      }
      this.inputs = List.copyOf(inputs);
      this.materialSlots = materialSlots.toIntArray();

      // finally, compute result variants for each input
      List<ItemStack> results = new ArrayList<>(product);
      List<MaterialVariantId> extraMaterials = recipe.getExtraMaterials();
      for (int i = 0; i < product; i++) {
        MaterialNBT.Builder builder = MaterialNBT.builder();
        for (int part = 0; part < partCount; part++) {
          builder.add(MaterialRecipeCache.findRecipe(partItems.get(part).get(i)).getMaterial());
        }
        builder.add(extraMaterials);

        ItemStack result = plainResult.copy();
        ToolStack.from(result).setMaterials(builder.build());
        results.add(result);
      }
      this.result = List.copyOf(results);
    }
  }

  /** {@return Instance of the shapeless extension, or null if the recipe is invalid for display} */
  @Nullable
  public static MaterialsCraftingExtension<ShapelessMaterialsRecipe> shapeless(ShapelessMaterialsRecipe recipe) {
    List<Ingredient> parts = recipe.getIngredients();
    for (int i = 0; i < recipe.getPartCount(); i++) {
      if (parts.get(i).getItems().length == 0) {
        return null;
      }
    }
    return new MaterialsCraftingExtension<>(recipe);
  }

  /** Gets the material slots for the given recipe */
  protected int[] getMaterialSlots(T recipe, Ingredient firstPart) {
    return new int[] {0};
  }

  @Override
  public ResourceLocation getRegistryName() {
    return recipe.getId();
  }

  /** Gets a list of item lists per slot from a list of ingredients */
  private static List<List<ItemStack>> getInputs(List<Ingredient> ingredients) {
    return ingredients.stream().map(ingredient -> List.of(ingredient.getItems())).toList();
  }

  /** @deprecated use {@link #setRecipe(ICraftingCategoryExtension, IRecipeLayoutBuilder, ICraftingGridHelper, ResourceLocation, List, List, ItemStack, int[])} */
  @Deprecated(forRemoval = true)
  public static void setRecipe(ICraftingCategoryExtension self, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, CraftingRecipe recipe, List<ItemStack> result, ItemStack plainResult, @Nullable int[] materialSlots) {
    setRecipe(self, builder, craftingGridHelper, recipe.getId(), getInputs(recipe.getIngredients()), result, plainResult, materialSlots);
  }

  /** Sets the recipe in the builder */
  public static void setRecipe(ICraftingCategoryExtension self, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, ResourceLocation id, List<List<ItemStack>> inputStacks, List<ItemStack> result, ItemStack plainResult, @Nullable int[] materialSlots) {
    builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(plainResult);

    // apply ingredient stacks
    // shapeless needs its width and height set, but we also want to recover those sizes, so calculate it locally
    int width = self.getWidth();
    int height = self.getHeight();
    if (width <= 0 || height <= 0) {
      width = height = getShapelessSize(inputStacks.size());
      builder.setShapeless();
    }
    List<IRecipeSlotBuilder> inputs = craftingGridHelper.createAndSetInputs(builder, inputStacks, width, height);
    IRecipeSlotBuilder output = craftingGridHelper.createAndSetOutputs(builder, result);
    if (inputs.size() != 9) {
      Mantle.logger.error("Failed to create focus link for {} as the layout {} is not 3x3", id, builder.getClass().getName());
    } else if (materialSlots != null) {
      // apply focus links
      int finalWidth = width;
      int finalHeight = height;
      builder.createFocusLink(Streams.concat(
        Stream.of(output),
        Arrays.stream(materialSlots).mapToObj(i -> inputs.get(MantleJEIConstants.getCraftingIndex(i, finalWidth, finalHeight)))
      ).toArray(IRecipeSlotBuilder[]::new));
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
    setRecipe(this, builder, craftingGridHelper, recipe.getId(), inputs, result, plainResult, materialSlots);
  }

  /** Gets the width and height of the grid for a shapeless recipe. */
  private static int getShapelessSize(int total) {
    if (total > 4) {
      return 3;
    } else if (total > 1) {
      return 2;
    } else {
      return 1;
    }
  }
}
