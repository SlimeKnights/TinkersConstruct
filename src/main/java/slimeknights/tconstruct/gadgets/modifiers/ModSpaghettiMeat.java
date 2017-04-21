package slimeknights.tconstruct.gadgets.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.Optional;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.gadgets.item.ItemMomsSpaghetti;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;

public class ModSpaghettiMeat extends ModSpaghettiMod {

  public ModSpaghettiMeat() {
    super("meat", 0x793e2d);

    addRecipeMatch(new MeatMixRecipeMatch());
  }

  @Override
  protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
    return super.canApplyCustom(stack) && ItemMomsSpaghetti.hasSauce(stack);
  }

  static class MeatMixRecipeMatch extends RecipeMatch.Oredict {

    public MeatMixRecipeMatch() {
      super("listAllmeatcooked", 1, 1);
    }

    @Override
    public List<ItemStack> getInputs() {
      return ImmutableList.of(
          new ItemStack(Items.COOKED_BEEF),
          new ItemStack(Items.COOKED_CHICKEN),
          new ItemStack(Items.COOKED_PORKCHOP));
    }

    @Override
    public Optional<Match> matches(NonNullList<ItemStack> stacks) {
      List<ItemStack> matches = Lists.newArrayList();

      Optional<Match> match = super.matches(stacks);

      while(match.isPresent() && matches.size() < 3) {
        ItemStack stack = match.get().stacks.get(0);
        matches.add(stack);

        // remove all meats of the same kind
        for(int i = 0; i < stacks.size(); i++) {
          if(stacks.get(i).getItem() == stack.getItem()) {
            stacks.set(i, ItemStack.EMPTY);
          }
        }

        match = super.matches(stacks);
      }

      if(matches.size() >= 3) {
        return Optional.of(new Match(matches, 1));
      }
      return Optional.empty();
    }
  }
}
