package slimeknights.tconstruct.library.utils;

import lombok.AllArgsConstructor;
import net.minecraft.util.IIntArray;

import java.util.function.Supplier;

@AllArgsConstructor
public class IntArrayWrapper implements IIntArray {
  private final Supplier<int[]> sup;

  @Override
  public int get(int index) {
    int[] array = sup.get();
    if (index >= 0 && index < array.length) {
      return array[index];
    }
    return 0;
  }

  @Override
  public void set(int index, int value) {
    int[] array = sup.get();
    if (index >= 0 && index < array.length) {
      array[index] = value;
    }
  }

  @Override
  public int size() {
    return sup.get().length;
  }
}
