package tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

import tconstruct.tools.block.BlockTable;

public class TableRecipe extends ShapedOreRecipe {

  protected final List<ItemStack> outputBlocks; // first one found of these determines the output block used


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
}
