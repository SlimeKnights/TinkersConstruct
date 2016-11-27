package slimeknights.tconstruct.tools.common;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

import slimeknights.tconstruct.shared.block.BlockTable;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

public class TableRecipe extends ShapedOreRecipe {

  static {
    RecipeSorter.register("tconstruct:table", TableRecipe.class, SHAPED, "before:minecraft:shaped");
  }

  public final List<ItemStack> outputBlocks; // first one found of these determines the output block used

  public TableRecipe(List<ItemStack> variantItems, BlockTable result, int meta, Object... recipe) {
    super(new ItemStack(result, 1, meta), recipe);
    this.outputBlocks = variantItems;
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
    for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
      for(ItemStack ore : outputBlocks) {
        ItemStack stack = craftMatrix.getStackInSlot(i);
        if(OreDictionary.itemMatches(ore, stack, false) && Block.getBlockFromItem(stack.getItem()) != null) {
          BlockTable block = (BlockTable) Block.getBlockFromItem(output.getItem());
          return BlockTable.createItemstack(block, output.getItemDamage(), Block.getBlockFromItem(stack.getItem()),
                                            stack.getItemDamage());
        }
      }
    }

    return super.getCraftingResult(craftMatrix);
  }

  @Override
  public ItemStack getRecipeOutput() {
    if(!outputBlocks.isEmpty() && output != null) {
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
