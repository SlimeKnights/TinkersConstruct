package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to make a tool part from a material item in the part builder
 */
@RequiredArgsConstructor
public class PartRecipe implements IPartBuilderRecipe, IMultiRecipe<ItemPartRecipe> {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Pattern pattern;
  protected final Ingredient patternItem;
  /** Recipe material cost */
  @Getter
  protected final int cost;
  /** Recipe result, used to fetch a material */
  protected final IMaterialItem output;
  /** Count for the recipe output */
  protected final int outputCount;

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.partRecipeSerializer.get();
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    // first, must have a pattern
    if (!patternItem.test(inv.getPatternStack())) {
      return false;
    }
    // if there is a material item, it must have a valid material and be craftable
    if (!inv.getStack().isEmpty()) {
      MaterialRecipe materialRecipe = inv.getMaterial();
      if (materialRecipe == null) {
        return false;
      }
      MaterialVariant material = materialRecipe.getMaterial();
      return material.get().isCraftable() && output.canUseMaterial(material.getId());
    }
    // no material item? return match in case we get one later
    return true;
  }

  /**
   * Checks if the recipe is valid for the given input. Assumes {@link #partialMatch(IPartBuilderContainer)} is true
   * @param inv    Inventory instance
   * @param world  World instance
   * @return  True if this recipe matches
   */
  @Override
  public boolean matches(IPartBuilderContainer inv, Level world) {
    // must have a material
    MaterialRecipe materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      // material must be craftable, usable in the item, and have a cost we can afford
      MaterialVariant material = materialRecipe.getMaterial();
      return material.get().isCraftable() && output.canUseMaterial(material.getId())
             && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
    }
    return false;
  }

  /** @deprecated use {@link #getRecipeOutput(MaterialVariantId)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return new ItemStack(output);
  }

  /**
   * Gets the output of the recipe for display
   * @param material  Material to use
   * @return  Output of the recipe
   */
  @SuppressWarnings("WeakerAccess")
  public ItemStack getRecipeOutput(MaterialVariantId material) {
    ItemStack stack = output.withMaterial(material);
    stack.setCount(outputCount);
    return stack;
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv) {
    MaterialVariant material = MaterialVariant.UNKNOWN;
    MaterialRecipe materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      material = materialRecipe.getMaterial();
    }
    return this.getRecipeOutput(material.getVariant());
  }

  /** Cache of recipes for display in JEI */
  @Nullable
  private List<ItemPartRecipe> multiRecipes;

  @Override
  public List<ItemPartRecipe> getRecipes() {
    if (multiRecipes == null) {
      // TODO: recipe per variant instead of per material?
      multiRecipes = MaterialRegistry
        .getMaterials().stream()
        .filter(mat -> mat.isCraftable() && output.canUseMaterial(mat))
        .map(mat -> {
          MaterialId materialId = mat.getIdentifier();
          return new ItemPartRecipe(materialId, mat.getIdentifier(), pattern, patternItem, getCost(), ItemOutput.fromStack(output.withMaterial(materialId)));
        })
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }

  public static class Serializer extends LoggingRecipeSerializer<PartRecipe> {
    @Override
    public PartRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
      String group = GsonHelper.getAsString(json, "group", "");
      Pattern pattern = new Pattern(GsonHelper.getAsString(json, "pattern"));
      Ingredient patternItem;
      if (json.has("pattern_item")) {
        patternItem = Ingredient.fromJson(json.get("pattern_item"));
      } else {
        patternItem = Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS);
      }
      int cost = GsonHelper.getAsInt(json, "cost");

      // output fetch as a material item, its an error if it does not implement that interface
      JsonObject output = GsonHelper.getAsJsonObject(json, "result");
      IMaterialItem item = RecipeHelper.deserializeItem(GsonHelper.getAsString(output, "item"), "result", IMaterialItem.class);
      int count = GsonHelper.getAsInt(output, "count", 1);

      return new PartRecipe(recipeId, group, pattern, patternItem, cost, item, count);
    }

    @Nullable
    @Override
    protected PartRecipe fromNetworkSafe(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      Pattern pattern = new Pattern(buffer.readUtf(Short.MAX_VALUE));
      Ingredient patternItem = Ingredient.fromNetwork(buffer);
      int cost = buffer.readInt();
      // output must be a material item
      IMaterialItem item = RecipeHelper.readItem(buffer, IMaterialItem.class);
      int count = buffer.readByte();
      return new PartRecipe(recipeId, group, pattern, patternItem, cost, item, count);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, PartRecipe recipe) {
      buffer.writeUtf(recipe.group);
      buffer.writeUtf(recipe.pattern.toString());
      recipe.patternItem.toNetwork(buffer);
      buffer.writeInt(recipe.cost);
      RecipeHelper.writeItem(buffer, recipe.output);
      buffer.writeByte(recipe.outputCount);
    }
  }
}
