package slimeknights.tconstruct.plugin.jei;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;

// TODO: constant case
public class TConstructRecipeCategoryUid {
  public static final ResourceLocation pluginUid = TConstruct.getResource("jei_plugin");

  // casting
  public static final ResourceLocation castingBasin = TConstruct.getResource("casting_basin");
  public static final ResourceLocation castingTable = TConstruct.getResource("casting_table");
  public static final ResourceLocation molding = TConstruct.getResource("molding");

  // melting
  public static final ResourceLocation melting = TConstruct.getResource("melting");
  public static final ResourceLocation entityMelting = TConstruct.getResource("entity_melting");
  public static final ResourceLocation alloy = TConstruct.getResource("alloy");
  public static final ResourceLocation foundry = TConstruct.getResource("foundry");

  // tinker station
  public static final ResourceLocation modifiers = TConstruct.getResource("modifiers");
  public static final ResourceLocation severing = TConstruct.getResource("severing");

  // part builder
  public static final ResourceLocation partBuilder = TConstruct.getResource("part_builder");
}
