package slimeknights.tconstruct.library;

import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

public class DryingRecipe {
  public final int time;
  public final RecipeMatch input;
  public final ItemStack output;

  DryingRecipe(RecipeMatch input, ItemStack output, int time ) {
      this.time = time;
      this.input = input;
      this.output = output;
  }

  public boolean matches (ItemStack input) {
    // makes all drying rack recipes compatible with stuff killed by a frying pan
    /* is this still used for the achievement?
    if(input.hasTagCompound()) {
      input = input.copy();
      input.getTagCompound().removeTag("frypanKill");
      if(input.getTagCompound().hasNoTags()) {
        input.setTagCompound(null);
      }
    }
    */
    
    if ( this.input != null ) {
      return this.input.matches(new ItemStack[]{input}) != null;
    }
	
    return false;
  }

  public ItemStack getResult ()
  {
    return output.copy();
  }

  public int getTime ()
  {
    return time;
  }
}
