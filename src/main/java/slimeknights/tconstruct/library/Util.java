/* Code for ctl and shift down from TicTooltips by squeek502
 * https://github.com/squeek502/TiC-Tooltips/blob/1.7.10/java/squeek/tictooltips/helpers/KeyHelper.java
 */

package slimeknights.tconstruct.library;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.ImmutableMap;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import slimeknights.mantle.util.RecipeMatchRegistry;

@SuppressWarnings("deprecation")
public class Util {

  public static final String MODID = "tconstruct";
  public static final String RESOURCE = MODID.toLowerCase(Locale.US);

  public static final DecimalFormat df = new DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US));
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
    return I18n.translateToLocal(I18n.translateToLocal(String.format(key, pars)).trim()).trim();
  }

  /**
   * Translate the string, insert parameters into the result of the translation
   */
  public static String translateFormatted(String key, Object... pars) {
    // translates twice to allow rerouting/alias
    return I18n.translateToLocal(I18n.translateToLocalFormatted(key, pars).trim()).trim();
  }

  /** Returns a fixed size DEEP copy of the list */
  public static NonNullList<ItemStack> deepCopyFixedNonNullList(NonNullList<ItemStack> in) {
    return RecipeMatchRegistry.copyItemStackArray(in);
  }

  /** @deprecated use deepCopyFixedNonNullList */
  @Deprecated
  public static NonNullList<ItemStack> copyItemStackArray(NonNullList<ItemStack> in) {
    return deepCopyFixedNonNullList(in);
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

  /* Position helpers */
  private static ImmutableMap<Vec3i, EnumFacing> offsetMap;
  static {
    ImmutableMap.Builder<Vec3i, EnumFacing> builder = ImmutableMap.builder();
    for(EnumFacing facing : EnumFacing.VALUES) {
      builder.put(facing.getDirectionVec(), facing);
    }
    offsetMap = builder.build();
  }

  /**
   * Gets the offset direction from two blocks
   * @param offset  Position offset
   * @return  Direction of the offset, or null if no direction
   */
  public static EnumFacing facingFromOffset(BlockPos offset) {
    return offsetMap.get(offset);
  }

  /**
   * Gets the offset direction from two blocks
   * @param pos       Base position
   * @param neighbor  Position Neighbor position
   * @return  Direction of the offset, or null if no direction
   */
  public static EnumFacing facingFromNeighbor(BlockPos pos, BlockPos neighbor) {
    // neighbor is first. For example, neighbor height is 11, pos is 10, so result is 1 or up
    return facingFromOffset(neighbor.subtract(pos));
  }

  /**
   * Returns true if the player clicked within the specified bounding box
   * @param aabb  Bounding box clicked
   * @param hitX  X hit location
   * @param hitY  Y hit location
   * @param hitZ  Z hit location
   * @return  True if the click was within the box
   */
  public static boolean clickedAABB(AxisAlignedBB aabb, float hitX, float hitY, float hitZ) {
    return aabb.minX <= hitX && hitX <= aabb.maxX
        && aabb.minY <= hitY && hitY <= aabb.maxY
        && aabb.minZ <= hitZ && hitZ <= aabb.maxZ;
  }

  private static boolean celsiusPref = false;

  /**
   * Sets the preference from the config. For internal use only
   * @param celsius  true for celsius, false for kelvin
   */
  public static void setTemperaturePref(boolean celsius) {
    celsiusPref = celsius;
  }

  /**
   * Formats the given temperature as a string using the config preference
   * @param temperature  Temperature in kelvin
   * @return  Formatted string
   */
  public static String temperatureString(int temperature) {
    return temperatureString(temperature, celsiusPref);
  }

  /**
   * Formats the given temperature as a string
   * @param temperature  Temperature in kelvin
   * @param celsius      If true, displays a celsius, false kelvin
   * @return  Formatted string
   */
  public static String temperatureString(int temperature, boolean celsius) {
    if(celsius) {
      return translateFormatted("gui.general.temperature.celsius", temperature - 300);
    }
    return translateFormatted("gui.general.temperature.kelvin", temperature);
  }
}
