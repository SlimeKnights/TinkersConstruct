package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.tools.common.TableRecipe;

public class TableRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper {

  private final TableRecipe recipe;
  private final int width;
  private final int height;
  private final List<ItemStack> outputs;

  public TableRecipeWrapper(TableRecipe recipe) {
    this.recipe = recipe;

    for (Object input : this.recipe.getInput()) {
      if (input instanceof ItemStack) {
        ItemStack itemStack = (ItemStack) input;
        if (itemStack.stackSize != 1) {
          itemStack.stackSize = 1;
        }
      }
    }

    this.width = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, this.recipe, "width");
    this.height = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, this.recipe, "height");

    // sort the output entries into lists of items
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


  @Override
  public void getIngredients(IIngredients ingredients) {
    IStackHelper stackHelper = JEIPlugin.jeiHelpers.getStackHelper();

    List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(Arrays.asList(recipe.getInput()));
    ingredients.setInputLists(ItemStack.class, inputs);

    //ItemStack recipeOutput = recipe.getRecipeOutput();
    if (!outputs.isEmpty()) {
      ingredients.setOutputs(ItemStack.class, outputs);
    }
  }

  @Override
  public List getInputs() {
    return Arrays.asList(recipe.getInput());
  }

  @Nonnull
  @Override
  public List<ItemStack> getOutputs() {
    return outputs;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }
}
