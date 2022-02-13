package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

public class TConstructJEIConstants {
  public static final ResourceLocation PLUGIN = TConstruct.getResource("jei_plugin");

  // ingredient types
  @SuppressWarnings("rawtypes")
  public static final IIngredientType<EntityType> ENTITY_TYPE = () -> EntityType.class;
  public static final IIngredientType<ModifierEntry> MODIFIER_TYPE = () -> ModifierEntry.class;
  public static final IIngredientType<Pattern> PATTERN_TYPE = () -> Pattern.class;

  // casting
  public static final ResourceLocation CASTING_BASIN = TConstruct.getResource("casting_basin");
  public static final ResourceLocation CASTING_TABLE = TConstruct.getResource("casting_table");
  public static final ResourceLocation MOLDING = TConstruct.getResource("molding");

  // melting
  public static final ResourceLocation MELTING = TConstruct.getResource("melting");
  public static final ResourceLocation ENTITY_MELTING = TConstruct.getResource("entity_melting");
  public static final ResourceLocation ALLOY = TConstruct.getResource("alloy");
  public static final ResourceLocation FOUNDRY = TConstruct.getResource("foundry");

  // tinker station
  public static final ResourceLocation MODIFIERS = TConstruct.getResource("modifiers");
  public static final ResourceLocation SEVERING = TConstruct.getResource("severing");

  // part builder
  public static final ResourceLocation PART_BUILDER = TConstruct.getResource("part_builder");
}
