package slimeknights.tconstruct.library.recipe.ingredient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

import javax.annotation.Nullable;
import java.util.stream.Stream;

/** Ingredient that contains another ingredient nested inside */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NestedIngredient implements ICustomIngredient {
  protected final Ingredient nested;


  /* Defer to nested */

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return nested.test(stack);
  }

  @Override
  public Stream<ItemStack> getItems() {
    return Stream.of(nested.getItems());
  }

  public boolean isEmpty() {
    return nested.isEmpty();
  }

  @Override
  public boolean isSimple() {
    return nested.isSimple();
  }
}
