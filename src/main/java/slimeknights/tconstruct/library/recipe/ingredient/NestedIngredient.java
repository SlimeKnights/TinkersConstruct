package slimeknights.tconstruct.library.recipe.ingredient;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;

import javax.annotation.Nullable;

/** Ingredient that contains another ingredient nested inside */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NestedIngredient extends AbstractIngredient {
  protected final Ingredient nested;


  /* Defer to nested */

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return nested.test(stack);
  }

  @Override
  public ItemStack[] getItems() {
    return nested.getItems();
  }

  @Override
  public IntList getStackingIds() {
    return nested.getStackingIds();
  }

  @Override
  public boolean isEmpty() {
    return nested.isEmpty();
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    nested.checkInvalidation();
  }

  @Override
  public boolean isSimple() {
    return nested.isSimple();
  }
}
