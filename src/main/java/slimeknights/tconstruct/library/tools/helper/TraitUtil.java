package slimeknights.tconstruct.library.tools.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraitUtil {

  /**
   * Runs the given action for all of the traits on the stack
   *
   * @param stack the tool
   * @param action the action to run
   */
  public static void forEachTrait(ItemStack stack, Consumer<ITrait> action) {
    // todo
  }

}
