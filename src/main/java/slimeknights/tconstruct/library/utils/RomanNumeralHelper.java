package slimeknights.tconstruct.library.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;

public class RomanNumeralHelper {
  private static final String TRANSLATION_KEY_PREFIX = "roman_numeral.value.";

  private RomanNumeralHelper() {}

  /** Cache of components for each numeral */
  private static final Int2ObjectMap<Component> NUMERAL_CACHE = new Int2ObjectOpenHashMap<>();

  /** Converts a value to a roman numeral string, based on https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java */
  private static String intToRomanNumeral(int value) {
    if (value < 1) {
      return Integer.toString(value);
    }
    StringBuilder builder = new StringBuilder();
    // M is 1000
    while (value >= 1000) {
      builder.append('M');
      value -= 1000;
    }
    // CM is 900
    if (value >= 900) {
      builder.append("CM");
      value -= 900;
    }
    // D is 500
    if (value >= 500) {
      builder.append('D');
      value -= 500;
    }
    // CD is 400
    if (value >= 400) {
      builder.append("CD");
      value -= 400;
    }
    // C is 100
    while (value >= 100) {
      builder.append('C');
      value -= 100;
    }
    // XC is 90
    if (value >= 90) {
      builder.append("XC");
      value -= 90;
    }
    // L is 50
    if (value >= 50) {
      builder.append('L');
      value -= 50;
    }
    // XL is 40
    if (value >= 40) {
      builder.append("XL");
      value -= 40;
    }
    // X is 10
    while (value >= 10) {
      builder.append('X');
      value -= 10;
    }
    // IX is 9
    if (value >= 9) {
      builder.append("IX");
      value -= 9;
    }
    // V is 5
    if (value >= 5) {
      builder.append('V');
      value -= 5;
    }
    // IV is 4
    if (value >= 4) {
      builder.append("IV");
      value -= 4;
    }
    // I is 1
    while (value >= 1) {
      builder.append('I');
      value -= 1;
    }

    return builder.toString();
  }

  /**
   * Gets a text component for the given numeral
   * @param value  Value of number
   * @return  Numeral
   */
  public static Component getNumeral(int value) {
    if (NUMERAL_CACHE.containsKey(value)) {
      return NUMERAL_CACHE.get(value);
    }
    String key = TRANSLATION_KEY_PREFIX + value;
    Component component;
    if (Util.canTranslate(key)) {
      component = Component.translatable(key);
    } else {
      component = Component.literal(intToRomanNumeral(value));
    }
    NUMERAL_CACHE.put(value, component);
    return component;
  }
}
