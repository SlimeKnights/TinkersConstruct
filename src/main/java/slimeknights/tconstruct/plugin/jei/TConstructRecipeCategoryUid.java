package slimeknights.tconstruct.plugin.jei;

import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;

// TODO: constant case
public class TConstructRecipeCategoryUid {
  public static final ResourceLocation pluginUid = Util.getResource("jei_plugin");

  // casting
  public static final ResourceLocation castingBasin = Util.getResource("casting_basin");
  public static final ResourceLocation castingTable = Util.getResource("casting_table");
  public static final ResourceLocation molding = Util.getResource("molding");

  // melting
  public static final ResourceLocation melting = Util.getResource("melting");
  public static final ResourceLocation entityMelting = Util.getResource("entity_melting");
  public static final ResourceLocation alloy = Util.getResource("alloy");

  // tinker station
  public static final ResourceLocation modifiers = Util.getResource("modifiers");
  public static final ResourceLocation beheading = Util.getResource("beheading");
}
