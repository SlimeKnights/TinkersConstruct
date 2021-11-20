package slimeknights.tconstruct.tools;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolStatProviders;

public class ArmorDefinitions {
  /** Balanced armor set */
  public static final ModifiableArmorMaterial TRAVELERS = ModifiableArmorMaterial
    .builder(TConstruct.getResource("travelers"))
    .setStatsProvider(ToolStatProviders.NO_PARTS)
    .setSoundEvent(Sounds.EQUIP_TRAVELERS.getSound())
    .build();

  /** High defense armor set */
  public static final ModifiableArmorMaterial PLATE = ModifiableArmorMaterial
    .builder(TConstruct.getResource("plate"))
    .setStatsProvider(ToolStatProviders.NO_PARTS)
    .setSoundEvent(Sounds.EQUIP_PLATE.getSound())
    .build();

  /** High modifiers armor set */
  public static final ModifiableArmorMaterial SLIMESUIT = ModifiableArmorMaterial
    .builder(TConstruct.getResource("slimesuit"))
    .setStatsProvider(ToolStatProviders.NO_PARTS)
    .setSoundEvent(Sounds.EQUIP_SLIME.getSound())
    .build();

}
