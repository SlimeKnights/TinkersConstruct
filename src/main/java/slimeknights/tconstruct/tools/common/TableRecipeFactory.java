package slimeknights.tconstruct.tools.common;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
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

    String oreNames = JsonUtils.getString(json, "ore_names");

    return new TableRecipe(recipe.getGroup().isEmpty() ? null : new ResourceLocation(recipe.getGroup()), OreDictionary.getOres(oreNames), recipe.getRecipeOutput(), primer);
  }

  public static class TableRecipe extends ShapedOreRecipe {
    public final NonNullList<ItemStack> outputBlocks; // first one found of these determines the output block used

    public TableRecipe(ResourceLocation group, NonNullList<ItemStack> variantItems, ItemStack result, ShapedPrimer primer) {
      super(group, result, primer);

      this.outputBlocks = variantItems;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
      for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
        for(ItemStack ore : outputBlocks) {
          ItemStack stack = craftMatrix.getStackInSlot(i);
          if(OreDictionary.itemMatches(ore, stack, false) && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) {
            BlockTable block = (BlockTable) Block.getBlockFromItem(output.getItem());
            return BlockTable.createItemstack(block, output.getItemDamage(), Block.getBlockFromItem(stack.getItem()),
                                              stack.getItemDamage());
          }
        }
      }

      return super.getCraftingResult(craftMatrix);
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
      if(!outputBlocks.isEmpty() && !output.isEmpty()) {
        ItemStack stack = outputBlocks.get(0);
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
