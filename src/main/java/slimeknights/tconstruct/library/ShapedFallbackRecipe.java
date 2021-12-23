package slimeknights.tconstruct.library;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class ShapedFallbackRecipe extends ShapedOreRecipe {

  private Ingredient[] ignore;
  private int need;
  public ShapedFallbackRecipe(ResourceLocation group, ItemStack result, CraftingHelper.ShapedPrimer primer, Ingredient[] ignore, int need) {
    super(group, result, primer);
    this.ignore = ignore;
    this.need = need;
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
    if (!super.matches(inv, world)) {
      return false;
    }

    int[] matches = new int[ignore.length];
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }
      for (int s = 0; s < ignore.length; s++) {
        if (ignore[s].apply(stack)) {
          matches[s]++;
          if (matches[s] >= need) {
            return false;
          }
        }
      }
    }

    return true;
  }

  public static class Factory implements IRecipeFactory {
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
      // use shaped parser to start
      ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
      CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
      primer.width = recipe.getRecipeWidth();
      primer.height = recipe.getRecipeHeight();
      primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
      primer.input = recipe.getIngredients();
      ResourceLocation group = recipe.getGroup().isEmpty() ? null : new ResourceLocation(recipe.getGroup());

      // custom properties
      JsonArray elements = JsonUtils.getJsonArray(json, "ignore");
      Ingredient[] ignore = new Ingredient[elements.size()];
      int i = 0;
      for (JsonElement ele : elements) {
        ignore[i++] = CraftingHelper.getIngredient(ele, context);
      }
      int need = JsonUtils.getInt(json, "need", 1);

      // return recipe
      return new ShapedFallbackRecipe(group, recipe.getRecipeOutput(), primer, ignore, need);
    }
  }
}
