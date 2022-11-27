package slimeknights.tconstruct.library.tools.capability;

import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * Logic to iterate through modifier hooks based on size, used for both fluids and inventories
 * @param <H>  Hook class
 * @param <I>  Object used in iteration
 */
public abstract class CompoundIndexHookIterator<H,I> {
  /** Start index from {@link #findHook(IToolStackView, int)}, reduces object creation */
  protected int startIndex = 0;

  /** Gets the iterator used to iterate the modifiers */
  protected abstract Iterator<I> getIterator(IToolStackView tool);

  /** Gets the hook at the given index */
  protected abstract H getHook(I entry);

  /** Gets the side of the given hook */
  protected abstract int getSize(IToolStackView tool, H hook);

  /** Gets the hook for the given tool and index */
  @Nullable
  protected H findHook(IToolStackView tool, int index) {
    int start = 0;
    Iterator<I> iterator = getIterator(tool);
    while (iterator.hasNext()) {
      H hook = getHook(iterator.next());
      int size = getSize(tool, hook);
      if (index < size + start) {
        startIndex = start;
        return hook;
      }
      // subtract tanks in the current modifier, tank is 0 indexed from the modifier
      start += size;
    }
    return null;
  }
}
