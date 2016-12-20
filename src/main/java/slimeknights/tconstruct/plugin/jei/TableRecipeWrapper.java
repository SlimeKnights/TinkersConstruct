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

import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.tools.common.TableRecipe;
import slimeknights.tconstruct.tools.common.item.ItemBlockTable;

public class TableRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper, ICustomCraftingRecipeWrapper {

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

  @SuppressWarnings("rawtypes")
  @Override
  @Deprecated
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

  private boolean isOutputBlock(ItemStack stack) {
    if(stack == null) {
      return false;
    }

    for(ItemStack output : recipe.outputBlocks) {
      // if the item matches the oredict entry, it is an output block
      if(OreDictionary.itemMatches(output, stack, false)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

    List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
    List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);

    // determine the focused stack
    IFocus<?> ifocus = recipeLayout.getFocus();
    Object focusObj = ifocus.getValue();

    // if the thing in focus is an itemstack
    if(focusObj instanceof ItemStack) {
      IGuiIngredientGroup<ItemStack> guiIngredients = recipeLayout.getIngredientsGroup(ItemStack.class);
      ItemStack focus = (ItemStack)focusObj;
      IFocus.Mode mode = ifocus.getMode();

      // input means we clicked on an ingredient, make sure it is one that affects the legs
      if(mode == IFocus.Mode.INPUT && isOutputBlock(focus)) {
        // first, get the output recipe
        ItemStack output = recipe.getPlainRecipeOutput();
        BlockTable block = (BlockTable) Block.getBlockFromItem(output.getItem());

        // then create a stack with the focus item (which we already validated above)
        ItemStack outputFocus = BlockTable.createItemstack(block, output.getItemDamage(), Block.getBlockFromItem(focus.getItem()),
            focus.getItemDamage());

        // and finally, set the focus override for the recipe
        guiIngredients.setOverrideDisplayFocus(new Focus<ItemStack>(IFocus.Mode.OUTPUT, outputFocus));
      }

      // if we clicked the table, remove all items which affect the legs textures that are not the leg item
      else if(mode == IFocus.Mode.OUTPUT) {
        // so determine the legs
        ItemStack legs = ItemBlockTable.getLegStack(focus);
        if(legs != null) {
          // and loop through all slots removing leg affecting inputs which don't match
          guiIngredients.setOverrideDisplayFocus(new Focus<ItemStack>(IFocus.Mode.INPUT, legs));
        }
      }
    }

    // add the itemstacks to the grid
    JEIPlugin.craftingGridHelper.setInputStacks(guiItemStacks, inputs, this.getWidth(), this.getHeight());
    JEIPlugin.craftingGridHelper.setOutput(guiItemStacks, outputs);
  }
}
