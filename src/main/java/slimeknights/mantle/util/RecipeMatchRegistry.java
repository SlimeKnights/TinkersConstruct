package slimeknights.mantle.util;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Contains a set of matches. Allows you to easily find if a set of itemstacks matches one of them.
 */
// MANTLE
public class RecipeMatchRegistry {

  protected final List<RecipeMatch> items = Lists.newArrayList();

  public RecipeMatch.Match matches(Collection<ItemStack> stacks) {
    return matches(stacks.toArray(new ItemStack[stacks.size()]));
  }

  // looks for a match in the given itemstacks
  public RecipeMatch.Match matches(ItemStack... stacks) {
    for(RecipeMatch recipe : items) {
      RecipeMatch.Match match = recipe.matches(stacks);
      if(match != null) {
        return match;
      }
    }

    return null;
  }

  // looks for a match with at least the given amount in the given itemstacks
  public RecipeMatch.Match matches(ItemStack[] stacks, int minAmount) {
    stacks = copyItemStackArray(stacks); // copy so we don't modify original

    List<RecipeMatch.Match> matches = Lists.newLinkedList();

    RecipeMatch.Match match;
    int sum = 0;
    while(sum < minAmount && (match = matches(stacks)) != null) {
      matches.add(match);
      RecipeMatch.removeMatch(stacks, match);

      sum += match.amount;
    }

    // not enough found
    if(sum < minAmount) {
      return null;
    }

    // merge all found matches into one match
    List<ItemStack> foundStacks = Lists.newLinkedList();
    for(RecipeMatch.Match m : matches) {
      foundStacks.addAll(m.stacks);
    }

    return new RecipeMatch.Match(foundStacks, sum);
  }

  /**
   * Associates an oredict entry with this material. Used for repairing and other.
   *
   * @param oredictItem   Oredict-String
   * @param amountNeeded  How many of this item are needed to count as one full material item.
   * @param amountMatched If both item and amount are present, how often did they match?
   */
  public void addItem(String oredictItem, int amountNeeded, int amountMatched) {
    items.add(new RecipeMatch.Oredict(oredictItem, amountNeeded, amountMatched));
  }

  public void addItem(String oredictItem) {
    addItem(oredictItem, 1, 1);
  }

  /**
   * Associates a block with this material. Used for repairing and other.
   *
   * @param amountMatched For how many matches the block counts (e.g. redstone dust = 1 match, Redstone block = 9)
   */
  public void addItem(Block block, int amountMatched) {
    items.add(new RecipeMatch.Item(new ItemStack(block), 1, amountMatched));
  }

  /**
   * Associates an item entry with this material. Used for repairing and other.
   *
   * @param item          The item
   * @param amountNeeded  How many of this item are needed to count as one full material item.
   * @param amountMatched If both item and amount are present, how often did they match?
   */
  public void addItem(Item item, int amountNeeded, int amountMatched) {
    items.add(new RecipeMatch.Item(new ItemStack(item), amountNeeded, amountMatched));
  }

  /**
   * Associates an item with this material. Used for repairing and other.
   */
  public void addItem(Item item) {
    addItem(item, 1, 1);
  }

  public void addRecipeMatch(RecipeMatch match) {
    items.add(match);
  }



  public static ItemStack[] copyItemStackArray(ItemStack[] in) {
    ItemStack[] stacksCopy = new ItemStack[in.length];
    for(int i = 0; i < in.length; i++) {
      if(in[i] != null) {
        stacksCopy[i] = in[i].copy();
      }
    }

    return stacksCopy;
  }
}
