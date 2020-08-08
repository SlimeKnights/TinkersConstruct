package slimeknights.tconstruct.library.recipe.crafting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ShapedFallbackRecipe extends ShapedRecipe {

  /** Recipes to skip if they match */
  private final List<ResourceLocation> alternatives;
  private List<ICraftingRecipe> alternativeCache;

  /**
   * Main constructor, creates a recipe from all parameters
   * @param id             Recipe ID
   * @param group          Recipe group
   * @param width          Recipe width
   * @param height         Recipe height
   * @param ingredients    Recipe input ingredients
   * @param output         Recipe output
   * @param alternatives   List of recipe names to fail this match if they match
   */
  public ShapedFallbackRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack output, List<ResourceLocation> alternatives) {
    super(id, group, width, height, ingredients, output);
    this.alternatives = alternatives;
  }

  /**
   * Creates a recipe using a shaped recipe as a base
   * @param base          Shaped recipe to copy data from
   * @param alternatives  List of recipe names to fail this match if they match
   */
  public ShapedFallbackRecipe(ShapedRecipe base, List<ResourceLocation> alternatives) {
    this(base.getId(), base.getGroup(), base.getWidth(), base.getHeight(), base.getIngredients(), base.getRecipeOutput(), alternatives);
  }

  @Override
  public boolean matches(CraftingInventory inv, World world) {
    // if this recipe does not match, fail it
    if (!super.matches(inv, world)) {
      return false;
    }

    // fetch all alternatives, fail if any match
    // cache to save effort down the line
    if (alternativeCache == null) {
      RecipeManager manager = world.getRecipeManager();
      alternativeCache = alternatives.stream()
                                     .map(manager::getRecipe)
                                     .filter(Optional::isPresent)
                                     .map(Optional::get)
                                     .filter(recipe -> {
                                       // only allow exact shaped or shapeless match, prevent infinite recursion due to complex recipes
                                       Class<?> clazz = recipe.getClass();
                                       return clazz == ShapedRecipe.class || clazz == ShapelessRecipe.class;
                                     })
                                     .map(recipe -> (ICraftingRecipe) recipe).collect(Collectors.toList());
    }
    // fail if any alterntaive matches
    return this.alternativeCache.stream().noneMatch(recipe -> recipe.matches(inv, world));
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerCommons.shapedFallbackRecipe.get();
  }

  public static class Serializer extends ShapedRecipe.Serializer {
    @Override
    public ShapedFallbackRecipe read(ResourceLocation id, JsonObject json) {
      ShapedRecipe base = super.read(id, json);
      JsonArray array = JSONUtils.getJsonArray(json, "alternatives");
      ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
      for (int i = 0; i < array.size(); i++) {
        builder.add(new ResourceLocation(JSONUtils.getString(array.get(i), "alternatives[" + i + "]")));
      }
      return new ShapedFallbackRecipe(base, builder.build());
    }

    @Override
    public ShapedFallbackRecipe read(ResourceLocation id, PacketBuffer buffer) {
      ShapedRecipe base = super.read(id, buffer);
      assert base != null;
      int size = buffer.readVarInt();
      ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(buffer.readResourceLocation());
      }
      return new ShapedFallbackRecipe(base, builder.build());
    }

    @Override
    public void write(PacketBuffer buffer, ShapedRecipe recipe) {
      // write base recipe
      super.write(buffer, recipe);
      // write extra data
      assert recipe instanceof ShapedFallbackRecipe;
      List<ResourceLocation> alternatives = ((ShapedFallbackRecipe) recipe).alternatives;
      buffer.writeVarInt(alternatives.size());
      for (ResourceLocation alternative : alternatives) {
        buffer.writeResourceLocation(alternative);
      }
    }
  }
}
