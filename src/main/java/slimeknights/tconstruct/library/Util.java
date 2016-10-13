/* Code for ctl and shift down from TicTooltips by squeek502
 * https://github.com/squeek502/TiC-Tooltips/blob/1.7.10/java/squeek/tictooltips/helpers/KeyHelper.java
 */

package slimeknights.tconstruct.library;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.Locale;

import slimeknights.mantle.util.RecipeMatchRegistry;

public class Util {

  public static final String MODID = "tconstruct";
  public static final String RESOURCE = MODID.toLowerCase(Locale.US);

  public static final DecimalFormat df = new DecimalFormat("#,###,###.##");
  public static final DecimalFormat dfPercent = new DecimalFormat("#%");

  public static Logger getLogger(String type) {
    String log = MODID;

    return LogManager.getLogger(log + "-" + type);
  }


  /**
   * Removes all whitespaces from the given string and makes it lowerspace.
   */
  public static String sanitizeLocalizationString(String string) {
    return string.toLowerCase(Locale.US).replaceAll(" ", "");
  }

  /**
   * Returns the given Resource prefixed with tinkers resource location. Use this function instead of hardcoding
   * resource locations.
   */
  public static String resource(String res) {
    return String.format("%s:%s", RESOURCE, res);
  }

  public static ResourceLocation getResource(String res) {
    return new ResourceLocation(RESOURCE, res);
  }

  public static ModelResourceLocation getModelResource(String res, String variant) {
    return new ModelResourceLocation(resource(res), variant);
  }

  public static ResourceLocation getModifierResource(String res) {
    return getResource("models/item/modifiers/" + res);
  }

  /**
   * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
   * namespace.
   */
  public static String prefix(String name) {
    return String.format("%s.%s", RESOURCE, name.toLowerCase(Locale.US));
  }

  /**
   * Translate the string, insert parameters into the translation key
   */
  public static String translate(String key, Object... pars) {
    // translates twice to allow rerouting/alias
    return I18n.translateToLocal(I18n.translateToLocal(String.format(key, (Object[]) pars)).trim()).trim();
  }

  /**
   * Translate the string, insert parameters into the result of the translation
   */
  public static String translateFormatted(String key, Object... pars) {
    // translates twice to allow rerouting/alias
    return I18n.translateToLocal(I18n.translateToLocalFormatted(key, (Object[]) pars).trim()).trim();
  }

  /**
   * Will be removed!
   * @deprecated use Item.getRegistryName
   */
  @Deprecated
  public static ResourceLocation getItemLocation(Item item) {
    return item.getRegistryName();
  }

  public static ItemStack[] copyItemStackArray(ItemStack[] in) {
    return RecipeMatchRegistry.copyItemStackArray(in);
  }


  /* Code for ctl and shift down  from TicTooltips by squeek502
   * https://github.com/squeek502/TiC-Tooltips/blob/1.7.10/java/squeek/tictooltips/helpers/KeyHelper.java
   */
  public static boolean isCtrlKeyDown() {
    // prioritize CONTROL, but allow OPTION as well on Mac (note: GuiScreen's isCtrlKeyDown only checks for the OPTION key on Mac)
    boolean isCtrlKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    if(!isCtrlKeyDown && Minecraft.IS_RUNNING_ON_MAC) {
      isCtrlKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
    }

    return isCtrlKeyDown;
  }

  public static boolean isShiftKeyDown() {
    return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

  }


  /**
   * Returns the actual color value for a chatformatting
   */
  public static int enumChatFormattingToColor(TextFormatting color) {
    int i = color.getColorIndex();
    int j = (i >> 3 & 1) * 85;
    int k = (i >> 2 & 1) * 170 + j;
    int l = (i >> 1 & 1) * 170 + j;
    int i1 = (i >> 0 & 1) * 170 + j;
    if(i == 6) {
      k += 85;
    }
    if(i >= 16) {
      k /= 4;
      l /= 4;
      i1 /= 4;
    }

    return (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
  }

}
