package tconstruct.library.utils;

import net.minecraft.item.ItemStack;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tools.IToolPart;

public final class TinkerUtil {

  private TinkerUtil() {
  }

  /**
   * Safe way of getting the material from an itemstack.
   */
  public static Material getMaterialFromStack(ItemStack stack) {
    if (stack == null || stack.getItem() == null) {
      return Material.UNKNOWN;
    }
    if (!(stack.getItem() instanceof IToolPart)) {
      return Material.UNKNOWN;
    }

    return ((IToolPart) stack.getItem()).getMaterial(stack);
  }

  // balantly stolen from StackOverflow and then optimized
  public static String getRomanNumeral(int value) {
    if (value < 1 || value > 3999)
      return "Really big";

    StringBuilder sb = new StringBuilder();
    while (value >= 1000) {
      sb.append("M");
      value -= 1000;        }
    while (value >= 900) {
      sb.append("CM");
      value -= 900;
    }
    while (value >= 500) {
      sb.append("D");
      value -= 500;
    }
    while (value >= 400) {
      sb.append("CD");
      value -= 400;
    }
    while (value >= 100) {
      sb.append("C");
      value -= 100;
    }
    while (value >= 90) {
      sb.append("XC");
      value -= 90;
    }
    while (value >= 50) {
      sb.append("L");
      value -= 50;
    }
    while (value >= 40) {
      sb.append("XL");
      value -= 40;
    }
    while (value >= 10) {
      sb.append("X");
      value -= 10;
    }
    while (value >= 9) {
      sb.append("IX");
      value -= 9;
    }
    while (value >= 5) {
      sb.append("V");
      value -= 5;
    }
    while (value >= 4) {
      sb.append("IV");
      value -= 4;
    }
    while (value >= 1) {
      sb.append("I");
      value -= 1;
    }
    return sb.toString();
  }
}
