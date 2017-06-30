package slimeknights.tconstruct.library.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.Optional;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.MaterialItem;

public class MaterialMatch extends RecipeMatch {

  private final Material material;

  public MaterialMatch(Material material, int amountNeeded, int amountMatched) {
    super(amountMatched, amountMatched);
    this.material = material;
  }

  public MaterialMatch(Material material, int amountNeeded) {
    this(material, amountNeeded, 2);
  }

  public MaterialMatch(Material material) {
    this(material, 1, 2);
  }

  @Override
  public List<ItemStack> getInputs() {
    return ImmutableList.of(); // todo
  }

  @Override
  public Optional<Match> matches(NonNullList<ItemStack> stacks) {
    List<ItemStack> found = Lists.newLinkedList();
    int stillNeeded = amountNeeded;

    for(ItemStack stack : stacks) {

      if(stack.getItem() instanceof MaterialItem) {
        if(material == ((MaterialItem) stack.getItem()).getMaterial(stack)) {
          // add the amount found to the list
          ItemStack copy = stack.copy();
          copy.setCount(Math.min(copy.getCount(), stillNeeded));
          found.add(copy);
          stillNeeded -= copy.getCount();

          // we found enough
          if(stillNeeded <= 0) {
            return Optional.of(new Match(found, amountMatched));
          }
        }
      }
    }

    return Optional.empty();
  }
}
