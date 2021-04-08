package slimeknights.tconstruct.plugin.jei;

import net.minecraft.util.Identifier;
import slimeknights.tconstruct.library.Util;

// TODO: constant case
public class TConstructRecipeCategoryUid {
  public static final Identifier pluginUid = Util.getResource("jei_plugin");

  // casting
  public static final Identifier castingBasin = Util.getResource("casting_basin");
  public static final Identifier castingTable = Util.getResource("casting_table");
  public static final Identifier molding = Util.getResource("molding");

  // melting
  public static final Identifier melting = Util.getResource("melting");
  public static final Identifier entityMelting = Util.getResource("entity_melting");
  public static final Identifier alloy = Util.getResource("alloy");

  // tinker station
  public static final Identifier modifiers = Util.getResource("modifiers");
  public static final Identifier beheading = Util.getResource("beheading");
}
