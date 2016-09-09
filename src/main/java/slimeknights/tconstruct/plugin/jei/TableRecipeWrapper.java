package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeWrapper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.tools.common.TableRecipe;

public class TableRecipeWrapper extends ShapedOreRecipeWrapper {

  private final List<ItemStack> outputs;

  public TableRecipeWrapper(TableRecipe recipe) {
    super(recipe);

    ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
    for(ItemStack stack : recipe.outputBlocks) {
      BlockTable block = (BlockTable) BlockTable.getBlockFromItem(recipe.getRecipeOutput().getItem());
      Block legBlock = Block.getBlockFromItem(stack.getItem());
      if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
        for(ItemStack sub : JEIPlugin.jeiHelpers.getStackHelper().getSubtypes(stack)) {
          builder.add(BlockTable.createItemstack(block, recipe.getRecipeOutput().getItemDamage(), legBlock, sub.getItemDamage()));
        }
      }
      else {
        builder.add(BlockTable.createItemstack(block, recipe.getRecipeOutput().getItemDamage(), legBlock, stack.getItemDamage()));
      }
    }
    outputs = builder.build();
  }

  @Nonnull
  @Override
  public List<ItemStack> getOutputs() {
    return outputs;
  }
}
