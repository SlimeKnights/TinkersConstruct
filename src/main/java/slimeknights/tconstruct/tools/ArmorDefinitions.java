package slimeknights.tconstruct.tools;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.IToolStatProvider;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolStatProviders;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.tools.stats.SkullStats;
import slimeknights.tconstruct.tools.stats.SkullToolStatsBuilder;

import java.util.List;

public class ArmorDefinitions {
  /** Stat provider for slimeskull */
  public static final IToolStatProvider SKULL_STAT_PROVIDER = new IToolStatProvider() {
    @Override
    public StatsNBT buildStats(ToolDefinition definition, MaterialNBT materials) {
      return SkullToolStatsBuilder.from(definition, materials).buildStats();
    }

    @Override
    public boolean isMultipart() {
      return true;
    }

    @Override
    public void validate(ToolDefinitionData data) {
      List<PartRequirement> requirements = data.getParts();
      if (requirements.isEmpty()) {
        throw new IllegalStateException("Must have at least one tool part for a skull tool");
      }
      for (PartRequirement req : requirements) {
        if (!req.getStatType().equals(SkullStats.ID)) {
          throw new IllegalStateException("Invalid skull part type, only supports skull type");
        }
      }
    }
  };

  /** Balanced armor set */
  public static final ModifiableArmorMaterial TRAVELERS = ModifiableArmorMaterial
    .builder(TConstruct.getResource("travelers"))
    .setStatsProvider(ToolStatProviders.NO_PARTS)
    .setSoundEvent(Sounds.EQUIP_TRAVELERS.getSound())
    .build();
  public static final ToolDefinition TRAVELERS_SHIELD = ToolDefinition.builder(TinkerTools.travelersShield).noParts().build();

  /** High defense armor set */
  public static final ModifiableArmorMaterial PLATE = ModifiableArmorMaterial
    .builder(TConstruct.getResource("plate"))
    .setStatsProvider(ToolStatProviders.NO_PARTS)
    .setSoundEvent(Sounds.EQUIP_PLATE.getSound())
    .build();
  public static final ToolDefinition PLATE_SHIELD = ToolDefinition.builder(TinkerTools.plateShield).noParts().build();

  /** High modifiers armor set */
  public static final ModifiableArmorMaterial SLIMESUIT = ModifiableArmorMaterial
    .builder(TConstruct.getResource("slime"))
    .setStatsProvider(ToolStatProviders.NO_PARTS)
    .setStatsProvider(ArmorSlotType.HELMET, SKULL_STAT_PROVIDER)
    .set(ArmorSlotType.HELMET, builder -> builder.setDefaultMaxTier(6))
    .setSoundEvent(Sounds.EQUIP_SLIME.getSound())
    .build();

}
