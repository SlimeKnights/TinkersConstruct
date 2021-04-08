package slimeknights.tconstruct.library.utils;

import java.util.Arrays;
import net.minecraft.util.collection.DefaultedList;

public final class ListUtil {

  @SafeVarargs
  public static <E> DefaultedList<E> getListFrom(E... element) {
    DefaultedList<E> list = DefaultedList.of();
    list.addAll(Arrays.asList(element));
    return list;
  }

  private ListUtil() {}
}
