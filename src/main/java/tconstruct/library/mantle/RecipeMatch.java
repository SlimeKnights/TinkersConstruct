package tconstruct.library.mantle;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import tconstruct.library.TinkerRegistry;

// todo: move to Mantle

/**
 * Utility class that allows to find a specific subset of items in a list of itemstacks.
 * Matches can be found through oredictionary, simple nbt-independant item-meta combinations etc.
 *
 * The match returned by this class can then be used to remove the found recipe match from the items.
 */
public abstract class RecipeMatch {

  public abstract Match matches(ItemStack[] stacks);

  /** Removes the match from the stacks */
  public static void removeMatch(ItemStack[] stacks, Match match) {
    for(ItemStack stack : match.stacks) {
      for(int i = 0; i < stacks.length; i++) {
        // nbt sensitive since toolparts etc. use nbt
        if(ItemStack.areItemsEqual(stack, stacks[i]) && ItemStack.areItemStackTagsEqual(stack, stacks[i])) {
          if(stacks[i].stackSize < stack.stackSize) {
            TinkerRegistry.log.error("RecipeMatch has incorrect stacksize! {}", stacks[i].toString());
            break;
          }
          else {
            stacks[i].stackSize -= stack.stackSize;
            if(stacks[i].stackSize == 0) {
              stacks[i] = null;
            }
          }
        }
      }
    }
  }

  /** A specific amount of a certain item is needed. Supports wildcard-metadata. Not NBT sensitive. */
  public static class Item extends RecipeMatch {
    private final ItemStack template;
    private final int amountNeeded;
    private final int amountMatched;

    public Item(ItemStack template, int amountNeeded) {
      this(template, amountNeeded, 1);
    }

    /**
     * @param template       The itemstack to match
     * @param amountNeeded   How many of the itemstack are needed for the match
     * @param amountMatched  If amountneeded itemstacks are present, as how many matches does this count?
     */
    public Item(ItemStack template, int amountNeeded, int amountMatched) {
      this.template = template;
      this.amountNeeded = amountNeeded;
      this.amountMatched = amountMatched;
    }

    @Override
    public Match matches(ItemStack[] stacks) {
      List<ItemStack> found = Lists.newLinkedList();
      int stillNeeded = amountNeeded;

      for(ItemStack stack : stacks) {
        if(OreDictionary.itemMatches(template, stack, false)) {
          // add the amount found to the list
          ItemStack copy = stack.copy();
          copy.stackSize = Math.min(copy.stackSize, stillNeeded);
          found.add(copy);
          stillNeeded -= copy.stackSize;

          // we found enough
          if(stillNeeded <= 0) {
            return new Match(found, amountMatched);
          }
        }
      }

      return null;
    }
  }

  /** A specific amount of an oredicted material is needed to match. */
  public static class Oredict extends RecipeMatch {

    private final String oredictEntry; // todo: change this to the actual list in the oredict
    private final int amountNeeded;
    private final int amountMatched;

    public Oredict(String oredictEntry, int amountNeeded) {
      this(oredictEntry, amountNeeded, 1);
    }

    public Oredict(String oredictEntry, int amountNeeded, int amountMatched) {
      this.oredictEntry = oredictEntry;
      this.amountNeeded = amountNeeded;
      this.amountMatched = amountMatched;
    }

    @Override
    public Match matches(ItemStack[] stacks) {
      List<ItemStack> found = Lists.newLinkedList();
      int stillNeeded = amountNeeded;

      for(ItemStack ore : OreDictionary.getOres(oredictEntry)) {
        for(ItemStack stack : stacks) {
          if(OreDictionary.itemMatches(ore, stack, false)) {
            // add the amount found to the list
            ItemStack copy = stack.copy();
            copy.stackSize = Math.min(copy.stackSize, stillNeeded);
            found.add(copy);
            stillNeeded -= copy.stackSize;

            // we found enough
            if(stillNeeded <= 0) {
              return new Match(found, amountMatched);
            }
          }
        }
      }

      return null;
    }
  }

  /** Represents a collection of items that match the recipies */
  public static class Match {

    /** The stacks that have to be removed to apply this match */
    public List<ItemStack> stacks;

    /** How often the recipe is found within this match */
    public int amount;

    public Match(List<ItemStack> stacks, int amount) {
      this.stacks = stacks;
      this.amount = amount;
    }
  }
}
