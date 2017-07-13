package slimeknights.tconstruct.tools.common;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import slimeknights.tconstruct.shared.block.BlockTable;

public class TableRecipeFactory implements IRecipeFactory {
  @Override
  public IRecipe parse(JsonContext context, JsonObject json) {
    ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

    ShapedPrimer primer = new ShapedPrimer();
    primer.width = recipe.getWidth();
    primer.height = recipe.getHeight();
    primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
    primer.input = recipe.getIngredients();

    JsonElement elem = getElement(json, "variants");

    return new TableRecipe(recipe.getGroup().isEmpty() ? null : new ResourceLocation(recipe.getGroup()), CraftingHelper.getIngredient(elem, context), recipe.getRecipeOutput(), primer);
  }

  public static JsonElement getElement(JsonObject json, String memberName) {
    if(json.has(memberName)) {
      return json.get(memberName);
    }
    else {
      throw new JsonSyntaxException("Missing " + memberName + " from the current json, Invalid JSON!");
    }
  }

  public static class TableRecipe extends ShapedOreRecipe {
    public final Ingredient ingredients; // first one found of these determines the output block used

    public TableRecipe(ResourceLocation group, Ingredient ingredientIn, ItemStack result, ShapedPrimer primer) {
      super(group, result, primer);

      this.ingredients = ingredientIn;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
      for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
        for(ItemStack ore : ingredients.getMatchingStacks()) {
          ItemStack stack = craftMatrix.getStackInSlot(i);
          if(OreDictionary.itemMatches(ore, stack, false) && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) {
            BlockTable block = (BlockTable) Block.getBlockFromItem(output.getItem());
            return BlockTable.createItemstack(block, output.getItemDamage(), Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
          }
        }
      }

      return super.getCraftingResult(craftMatrix);
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
      if(!(ingredients.getMatchingStacks().length == 0) && !output.isEmpty()) {
        ItemStack stack = ingredients.getMatchingStacks()[0];
        BlockTable block = (BlockTable) Block.getBlockFromItem(output.getItem());
        int meta = stack.getItemDamage();

        if(meta == OreDictionary.WILDCARD_VALUE) {
          meta = 0;
        }

        return BlockTable.createItemstack(block, output.getItemDamage(), Block.getBlockFromItem(stack.getItem()), meta);
      }

      return super.getRecipeOutput();
    }

    /**
     * Gets the recipe output without applying the legs block
     */
    public ItemStack getPlainRecipeOutput() {
      return output;
    }
  }
}
