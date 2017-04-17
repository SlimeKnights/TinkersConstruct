package slimeknights.tconstruct.library.utils;

import net.minecraft.util.NonNullList;

import java.util.Arrays;

public final class ListUtil {

  public static <E> NonNullList<E> getListFrom(E... element) {
    NonNullList<E> list = NonNullList.create();
    list.addAll(Arrays.asList(element));
    return list;
  }

  private ListUtil() {}
}
