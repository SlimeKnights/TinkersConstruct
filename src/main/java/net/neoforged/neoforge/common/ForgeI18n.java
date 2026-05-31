package slimeknights.tconstruct.compat.neoforged.neoforge.common;

import net.minecraft.locale.Language;

/** Compatibility shim for old ForgeI18n calls. */
public final class ForgeI18n {
  private ForgeI18n() {}

  public static String getPattern(String key) {
    return Language.getInstance().getOrDefault(key);
  }
}
