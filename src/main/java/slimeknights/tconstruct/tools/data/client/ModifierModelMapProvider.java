package slimeknights.tconstruct.tools.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.MaterialHasFallbackModifierModel;
import slimeknights.tconstruct.library.data.AbstractModifierModelMapProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.client.SlimeskullModifierModel;
import slimeknights.tconstruct.tools.data.ModifierIds;

/** Provider for modifier models on tools */
public class ModifierModelMapProvider extends AbstractModifierModelMapProvider {
  public ModifierModelMapProvider(PackOutput output) {
    super(output, TConstruct.MOD_ID);
  }

  @Override
  protected void addModels() {
    // small
    String pickaxeModifier = "pickaxe/modifiers/";
    String mattockModifier = "mattock/modifiers/";
    tool(TinkerTools.pickaxe)
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.blasting, ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 7, ModifierIds.haste)
      .luminosity( 2, ModifierIds.unbreakable);
    tool(TinkerTools.pickadze)
      .basic(ModifierIds.netherite, ModifierIds.magnetic, ModifierIds.silky, ModifierIds.knockback)
      .luminosity(10, ModifierIds.fiery)
      // shared with pickaxe
      .smallFolder(pickaxeModifier)
      .basic(
        ModifierIds.experienced, ModifierIds.luck,
        ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.smite,
        ModifierIds.blasting, ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity( 7, ModifierIds.haste)
      // shared with mattock
      .smallFolder(mattockModifier)
      .basic(
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.soulbound,
        ModifierIds.emerald, ModifierIds.cooling, ModifierIds.necrotic
      )
      .luminosity(15, ModifierIds.glowing)
      .luminosity( 2, ModifierIds.unbreakable);
    tool(TinkerTools.mattock).basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.soulbound,
        TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic
      )
      .luminosity(15, ModifierIds.glowing)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 2, ModifierIds.unbreakable)
      // shared with pickaxe
      .smallFolder(pickaxeModifier)
      .basic(
        ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck,
        ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.smite,
        ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity(7, ModifierIds.haste);
    tool(TinkerTools.handAxe)
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.hydraulic
      )
      .luminosity(15, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 7, ModifierIds.haste)
      .luminosity( 2, ModifierIds.unbreakable)
      // shared with pickaxe
      .smallFolder(pickaxeModifier)
      .basic(
        ModifierIds.luck,
        ModifierIds.antiaquatic
      ).fluid(ModifierIds.bucketing).tank();
    tool(TinkerTools.kama)
      .basic(
        ModifierIds.diamond, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.soulbound,
        TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic
      )
      .luminosity(15, ModifierIds.glowing)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 2, ModifierIds.unbreakable)
      // shared with pickaxe
      .smallFolder(pickaxeModifier)
      .basic(
        ModifierIds.emerald,
        ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck,
        ModifierIds.antiaquatic, ModifierIds.smite,
        ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity( 7, ModifierIds.haste);
    // melee weapon
    tool(TinkerTools.dagger)
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 7, ModifierIds.haste)
      .luminosity( 2, ModifierIds.unbreakable);
    tool(TinkerTools.sword)
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic, ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 7, ModifierIds.haste)
      .luminosity( 2, ModifierIds.unbreakable);
    // ranged weapon
    tool(TinkerTools.crossbow)
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.freezing,
        ModifierIds.arrowPierce, ModifierIds.pierce, ModifierIds.trueshot
      )
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 7, ModifierIds.quickCharge)
      .luminosity( 2, ModifierIds.unbreakable);
    for (int i = 1; i < 4; i++){
      tool(TinkerTools.crossbow, "/" + i).basic("_" + i, ModifierIds.quickCharge);
    }
    tool(TinkerTools.crossbow, "/broken").basic("_broken", ModifierIds.quickCharge);

    // broad
    String sledgeSmall = "sledge_hammer/modifiers/";
    String sledgeLarge = "sledge_hammer/large/modifiers/";
    tool(TinkerTools.sledgeHammer).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 7, ModifierIds.haste)
      .luminosity( 2, ModifierIds.unbreakable)
      .noLarge()
      .basic(ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .luminosity(15, ModifierIds.glowing);
    String veinLarge = "vein_hammer/large/modifiers/";
    tool(TinkerTools.veinHammer).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.netherite, ModifierIds.magnetic, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback
      ).tank()
      .luminosity(10, ModifierIds.fiery)
      // shared with sledgehammer
      .folder(sledgeSmall, sledgeLarge)
      .basic(ModifierIds.blasting, ModifierIds.hydraulic, ModifierIds.luck)
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity( 7, ModifierIds.haste)
      // partially shared with sledgehammer
      .folder(sledgeSmall, veinLarge)
      .basic(ModifierIds.emerald, ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced)
      .fluid(ModifierIds.bucketing)
      .luminosity(2, ModifierIds.unbreakable);
    String excavatorSmall = "excavator/modifiers/";
    String excavatorLarge = "excavator/large/modifiers/";
    tool(TinkerTools.excavator).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity(10, ModifierIds.fiery)
      .luminosity( 7, ModifierIds.haste)
      .luminosity( 2, ModifierIds.unbreakable)
      .noLarge()
      .basic(TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .luminosity(15, ModifierIds.glowing)
      // shared with sledgehammer
      .folder(sledgeSmall, sledgeLarge).basic(ModifierIds.experienced)
      .folder(sledgeSmall, null).basic(ModifierIds.soulbound);
    String broadAxeSmall = "broad_axe/modifiers/";
    String broadAxeLarge = "broad_axe/large/modifiers/";
    tool(TinkerTools.broadAxe).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.netherite,
        ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback
      )
      .luminosity(10, ModifierIds.fiery)
      .noLarge()
      .basic(ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .luminosity(15, ModifierIds.glowing)
      // partially shared with sledgehammer
      .folder(sledgeSmall, broadAxeLarge)
      .basic(ModifierIds.luck, ModifierIds.overforced, ModifierIds.reinforced)
      .fluid(ModifierIds.bucketing)
      .luminosity(2, ModifierIds.unbreakable)
      // partially shared with excavator
      .folder(excavatorSmall, broadAxeLarge)
      .basic(ModifierIds.emerald)
      .tank()
      .folder(broadAxeSmall, excavatorLarge)
      .basic(ModifierIds.hydraulic)
      .luminosity(7, ModifierIds.haste)
      .luminosity(15, ModifierIds.lightspeed);
    tool(TinkerTools.scythe).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(7, ModifierIds.haste)
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity(10, ModifierIds.fiery)
      .luminosity(2, ModifierIds.unbreakable)
      .noLarge()
      .basic(ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .luminosity(15, ModifierIds.glowing);
    // melee weapon
    String cleaverSmall = "cleaver/modifiers/";
    tool(TinkerTools.cleaver).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(7, ModifierIds.haste)
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity(10, ModifierIds.fiery)
      .luminosity(2, ModifierIds.unbreakable)
      .noLarge()
      .basic(ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .luminosity(15, ModifierIds.glowing)
      // partially shared with sledgehammer
      .folder(cleaverSmall, sledgeLarge).basic(ModifierIds.luck);
    String javelinLarge = "javelin/large/modifiers/";
    tool(TinkerTools.javelin).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.hydraulic
      )
      .fluid(ModifierIds.bucketing).tank()
      .luminosity(7, ModifierIds.haste)
      .luminosity(15, ModifierIds.lightspeed)
      .luminosity(10, ModifierIds.fiery)
      // partially shared with sledgehammer
      .folder(sledgeSmall, javelinLarge)
      .basic(ModifierIds.overforced, ModifierIds.reinforced)
      .luminosity(2, ModifierIds.unbreakable);
    // ranged weapon
    tool(TinkerTools.longbow).large('/')
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.freezing,
        ModifierIds.trueshot, ModifierIds.bounce, ModifierIds.power
      )
      .luminosity(10, ModifierIds.fiery)
      .luminosity(2, ModifierIds.unbreakable);
    for (int i = 1; i < 4; i++){
      tool(TinkerTools.longbow, "/" + i).large('/')
        .basic("_" + i,
          ModifierIds.diamond, ModifierIds.netherite,
          ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.freezing
        )
        .luminosity(10, "_" + i, ModifierIds.fiery)
        .luminosity(2, "_" + i, ModifierIds.unbreakable);
    }
    tool(TinkerTools.longbow, "/broken").large('/').basic( "_broken", ModifierIds.overforced, ModifierIds.reinforced);

    // ancient tools
    tool(TinkerTools.battlesign)
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing).tank()
      .luminosity(7, ModifierIds.haste)
      .luminosity(15, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, ModifierIds.fiery)
      .luminosity(2, ModifierIds.unbreakable);

    // misc
    tool(TinkerTools.flintAndBrick)
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced
      )
      .luminosity(15, ModifierIds.glowing)
      .luminosity(2, ModifierIds.unbreakable);

    // shared armor
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      String name = type.getName();
      tool("armor/" + name).smallFolder("armor/modifiers/" + name + '/').basic(
          ModifierIds.emerald, ModifierIds.thorns)
        .luminosity(10, ModifierIds.fiery)
        .trim(type);
    }
    // piece specific
    tool("armor/helmet").smallFolder("armor/modifiers/helmet/").basic(ModifierIds.aquaAffinity);
    tool("armor/chestplate").smallFolder("armor/modifiers/chestplate/")
      .basic(
        ModifierIds.knockback, TinkerModifiers.sleeves.getId(),
        ModifierIds.reach, ModifierIds.strength,TinkerModifiers.ambidextrous.getId()
      )
      .luminosity(7, ModifierIds.haste);
    tool("armor/leggings").smallFolder("armor/modifiers/leggings/").basic(
      ModifierIds.leaping, ModifierIds.luck, TinkerModifiers.shieldStrap.getId(),
      ModifierIds.speedy, ModifierIds.stepUp, ModifierIds.swiftSneak);
    tool("armor/boots").smallFolder("armor/modifiers/boots/").basic(
        ModifierIds.depthStrider, ModifierIds.doubleJump, ModifierIds.featherFalling,
        ModifierIds.longFall, ModifierIds.soulspeed)
      .luminosity(15, ModifierIds.lightspeed)
      .basic(ModifierIds.featherFall, "tconstruct_feather_falling");

    // plate armor
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      String item = "plate/" + type.getName();
      String root = "armor/" + item + "/maille";
      tool(item).armor()
        .basic(
          ModifierIds.diamond, ModifierIds.netherite,
          ModifierIds.ricochet, ModifierIds.springy,
          ModifierIds.freezing
        )
        .dyed(new MaterialHasFallbackModifierModel(1,
        new DyedModifierModel(toolMaterial(root + "_metal"), null),
        new DyedModifierModel(toolMaterial(root), null),
        "metal"
      ));
      tool(item + "_broken").dyed(new MaterialHasFallbackModifierModel(1,
        new DyedModifierModel(toolMaterial(root + "_broken_metal"), null),
        new DyedModifierModel(toolMaterial(root + "_broken"), null),
        "metal"
      ));
    }
    // other modifiers
    tool("plate/helmet") // armor is already set above
      .basic(TinkerModifiers.itemFrame.getId(), ModifierIds.respiration);
    // we include both folders, but limited for small
    tool("plate/shield").banner("armor/plate/shield/banner_small/", "armor/plate/shield/banner_large/");

    // travelers
    travelers("goggles");
    travelers("vest");
    travelers("pants");
    travelers("boots");
    travelers("shield");
    tool("travelers/goggles").customTrim("armor/travelers/goggles/trim", null);
    tool("travelers/pants").armor().basic(ModifierIds.swiftSneak);

    // slimesuit
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      tool("slime/" + type.getName());
    }
    tool("slime/wings")
      .customTrim("armor/slime/wings/trim", null)
      .dyed("armor/slime/wings/slime", null);
    tool("slime/wings_broken").dyed("armor/slime/wings/slime_broken", null);
    // slimesuit dyeing
    // skull texture is added via the modifier map instead of the tool JSON, since we have to fetch the material anyway to dye it, saves fetching it twice
    tool("slime/helmet").armor()
      .constant("__skull", new SlimeskullModifierModel(toolMaterial("armor/slime/helmet/skull"), 0, 1))
      .luminosity(10, ModifierIds.fiery);
    tool("slime/leggings").dyed("armor/slime/leggings/shell", null);
    tool("slime/leggings_broken").dyed("armor/slime/leggings/shell_broken", null);
    tool("slime/boots").dyed("armor/slime/boots/laces", null);
    String ribcage = "armor/slime/chestplate/ribcage";
    tool("slime/chestplate").dyed(new MaterialHasFallbackModifierModel(0,
      new DyedModifierModel(toolMaterial(ribcage + "_bone"), null),
      new DyedModifierModel(toolMaterial(ribcage), null),
      "bone"
    )).empty(
      ModifierIds.emerald // no shoulders to fit
    );
    tool("slime/chestplate_broken").dyed(new MaterialHasFallbackModifierModel(0,
      new DyedModifierModel(toolMaterial(ribcage + "_broken_bone"), null),
      new DyedModifierModel(toolMaterial(ribcage + "_broken"), null),
      "bone"
    ));

    // ammo
    tool(TinkerTools.arrow).smallFolder("ammo/arrow_modifiers/").tipped().smashing().dyed();
    tool(TinkerTools.shuriken).smallFolder("ammo/shuriken_modifiers/").tipped().smashing();
    tool(TinkerTools.throwingAxe).smallFolder("ammo/axe_modifiers/").tipped().smashing();
    // fishing rods have tipped
    tool(TinkerTools.fishingRod).basic(
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.lure,
      ModifierIds.punch, ModifierIds.trueshot, ModifierIds.bounce, ModifierIds.collecting,
      ModifierIds.grapple, ModifierIds.channeling).luminosity(7, ModifierIds.quickCharge)
      .luminosity(10, ModifierIds.fiery)
      .luminosity(2, ModifierIds.unbreakable)
      .tipped().constantFluid().compact(ModifierIds.tank);
    tool(TinkerTools.fishingRod, "/broken")
      .basic("_broken",
        ModifierIds.emerald, ModifierIds.netherite, ModifierIds.reinforced,
        ModifierIds.overforced
      ).emptyTipped();
    // some modifier textures display on the hook, so hide them when the rod is cast.
    tool(TinkerTools.fishingRod, "/cast")
      // broken texture just removes the hook part, works for cast too
      .basic("_broken", ModifierIds.netherite)
      .empty(
        ModifierIds.bounce, ModifierIds.collecting, ModifierIds.fiery, ModifierIds.freezing,
        ModifierIds.lure, ModifierIds.unbreakable, ModifierIds.quickCharge, ModifierIds.trueshot,
        ModifierIds.grapple
      ).emptyTipped();

    // tanks
    tool(TinkerTools.meltingPan).constantFluid();
    tool(TinkerTools.swasher).constantFluid();

    // staffs
    tool("staff").large('_')
      .basic(
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.firestarter,
        ModifierIds.overforced, ModifierIds.reinforced
      )
      .tank().embellishment().fluid(ModifierIds.bucketing)
      .luminosity(2, ModifierIds.unbreakable);
    staffDyed("earth");
    staffDyed("sky");
    staffDyed("ichor");
    staffDyed("ender");
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Model Map Provider";
  }

  /** Adds dyed textures for travelers gear */
  private void travelers(String name) {
    String root = "armor/travelers/" + name + "/modifiers/";
    String item = "travelers/" + name;
    tool(item).dyed(root + "dyed", null);
    tool(item + "_broken").dyed(root + "dyed_broken", null);
  }

  /** Adds dyed textures to a staff */
  private void staffDyed(String name) {
    String staff = "staff/" + name;
    String small = "staff/modifiers/" + name + "/dyed";
    String large = "staff/large_modifiers/" + name + "/dyed";
    tool(staff).dyed(small, large);
    tool(staff + "/broken").dyed(small + "_broken", large + "_broken");
    for (int i = 1; i <= 5; i++) {
      String variant = Integer.toString(i);
      tool(staff + '/' + variant).dyed(small + '_' + variant, large + '_' + variant);
    }
  }
}
