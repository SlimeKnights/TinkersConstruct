package slimeknights.tconstruct.tools;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import static slimeknights.tconstruct.tools.TinkerToolParts.broadAxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.broadBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.hammerHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.largePlate;
import static slimeknights.tconstruct.tools.TinkerToolParts.pickaxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.smallAxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.smallBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolBinding;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolHandle;
import static slimeknights.tconstruct.tools.TinkerToolParts.toughHandle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolDefinitions {
  // rock
  public static final ToolDefinition PICKAXE = ToolDefinition
    .builder(ToolBaseStatDefinitions.PICKAXE)
    .addPart(pickaxeHead).addPart(toolHandle).addPart(toolBinding)
    .addModifier(TinkerModifiers.piercing, 1)
    .build();
  public static final ToolDefinition SLEDGE_HAMMER = ToolDefinition
    .builder(ToolBaseStatDefinitions.SLEDGE_HAMMER)
    .addPart(hammerHead).addPart(toughHandle).addPart(largePlate).addPart(largePlate)
    .addModifier(TinkerModifiers.smite, 2)
    .addModifier(TinkerModifiers.twoHanded)
    .build();
  public static final ToolDefinition VEIN_HAMMER = ToolDefinition
    .builder(ToolBaseStatDefinitions.VEIN_HAMMER)
    .addPart(hammerHead).addPart(toughHandle).addPart(pickaxeHead).addPart(largePlate)
    .addModifier(TinkerModifiers.piercing, 2)
    .addModifier(TinkerModifiers.twoHanded)
    .build();

  // dirt
  public static final ToolDefinition MATTOCK = ToolDefinition
    .builder(ToolBaseStatDefinitions.MATTOCK)
    .addPart(smallAxeHead).addPart(toolHandle).addPart(pickaxeHead)
    .addModifier(TinkerModifiers.knockback, 1)
    .addModifier(TinkerModifiers.shovelTransformHidden)
    .build();
  public static final ToolDefinition EXCAVATOR = ToolDefinition
    .builder(ToolBaseStatDefinitions.EXCAVATOR)
    .addPart(largePlate).addPart(toughHandle).addPart(largePlate).addPart(toughHandle)
    .addModifier(TinkerModifiers.knockback, 2)
    .addModifier(TinkerModifiers.shovelTransformHidden)
    .addModifier(TinkerModifiers.twoHanded)
    .build();

  // wood
  public static final ToolDefinition HAND_AXE = ToolDefinition
    .builder(ToolBaseStatDefinitions.HAND_AXE)
    .addPart(smallAxeHead).addPart(toolHandle).addPart(toolBinding)
    .addModifier(TinkerModifiers.axeTransformHidden)
    .build();
  public static final ToolDefinition BROAD_AXE = ToolDefinition
    .builder(ToolBaseStatDefinitions.BROAD_AXE)
    .addPart(broadAxeHead).addPart(toughHandle).addPart(pickaxeHead).addPart(toolBinding)
    .addModifier(TinkerModifiers.axeTransformHidden)
    .addModifier(TinkerModifiers.twoHanded)
    .build();

  // scythes
  public static final ToolDefinition KAMA = ToolDefinition
    .builder(ToolBaseStatDefinitions.KAMA)
    .addPart(smallBlade).addPart(toolHandle).addPart(toolBinding)
    .addModifier(TinkerModifiers.hoeTransformHidden)
    .addModifier(TinkerModifiers.shears)
    .addModifier(TinkerModifiers.harvest)
    .build();
  public static final ToolDefinition SCYTHE = ToolDefinition
    .builder(ToolBaseStatDefinitions.SCYTHE)
    .addPart(TinkerToolParts.broadBlade).addPart(TinkerToolParts.toughHandle).addPart(TinkerToolParts.toolBinding).addPart(TinkerToolParts.toughHandle)
    .addModifier(TinkerModifiers.hoeTransformHidden)
    .addModifier(TinkerModifiers.aoeSilkyShears)
    .addModifier(TinkerModifiers.harvest)
    .addModifier(TinkerModifiers.twoHanded)
    .build();

  // swords
  public static final ToolDefinition DAGGER = ToolDefinition
    .builder(ToolBaseStatDefinitions.DAGGER)
    .addPart(smallBlade).addPart(toolHandle)
    .addModifier(TinkerModifiers.padded, 1)
    .addModifier(TinkerModifiers.offhandAttack)
    .addModifier(TinkerModifiers.silkyShears)
    .build();
  public static final ToolDefinition SWORD = ToolDefinition
    .builder(ToolBaseStatDefinitions.SWORD)
    .addPart(smallBlade).addPart(toolHandle).addPart(toolHandle)
    .addModifier(TinkerModifiers.silkyShears)
    .build();
  public static final ToolDefinition CLEAVER = ToolDefinition
    .builder(ToolBaseStatDefinitions.CLEAVER)
    .addPart(broadBlade).addPart(toughHandle).addPart(toughHandle).addPart(largePlate)
    .addModifier(TinkerModifiers.severing, 2)
    .addModifier(TinkerModifiers.aoeSilkyShears)
    .addModifier(TinkerModifiers.twoHanded)
    //.addModifier(TinkerModifiers.reach, 1) TODO - attack reach
    .build();

  // special
  public static final ToolDefinition FLINT_AND_BRONZE = ToolDefinition
    .builder(ToolBaseStatDefinitions.FLINT_AND_BRONZE)
    .addModifier(TinkerModifiers.firestarterHidden)
    .addModifier(TinkerModifiers.fiery)
    .build();
}
