package slimeknights.tconstruct.tools.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.MaterialHasFallbackModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.data.AbstractModifierModelMapProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.client.SlimeskullModifierModel;
import slimeknights.tconstruct.tools.data.ModifierIds;

import javax.annotation.Nullable;

/** Provider for modifier models on tools */
public class ModifierModelMapProvider extends AbstractModifierModelMapProvider {
  public ModifierModelMapProvider(PackOutput output) {
    super(output, TConstruct.MOD_ID);
  }

  @Override
  protected void addModels() {
    // small
    tool(TinkerTools.pickaxe).basic(SMALL,
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
      ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
      ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
      ModifierIds.knockback, ModifierIds.necrotic,
      ModifierIds.blasting, ModifierIds.hydraulic
    ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    tool(TinkerTools.pickadze).basic(SMALL,
        ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    tool(TinkerTools.mattock).basic(SMALL,
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    tool(TinkerTools.handAxe).basic(SMALL,
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    tool(TinkerTools.kama).basic(SMALL,
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    // melee weapon
    tool(TinkerTools.dagger).basic(SMALL,
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    tool(TinkerTools.sword).basic(SMALL,
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    // ranged weapon
    tool(TinkerTools.crossbow).basic(SMALL,
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.freezing,
        ModifierIds.arrowPierce, ModifierIds.pierce, ModifierIds.trueshot
      ).luminosity(7, SMALL, ModifierIds.quickCharge)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);
    for (int i = 1; i < 4; i++){
      tool(TinkerTools.crossbow, "/" + i).basic(SMALL, "_" + i, ModifierIds.quickCharge);
    }
    tool(TinkerTools.crossbow, "/broken").basic(SMALL, "_broken", ModifierIds.quickCharge);

    // broad
    tool(TinkerTools.sledgeHammer).basic('/',
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).basic(SMALL, ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .fluid(ModifierIds.bucketing, '/').tank('/')
      .luminosity(7, '/', ModifierIds.haste)
      .luminosity(15, '/', ModifierIds.lightspeed)
      .luminosity(15, SMALL, ModifierIds.glowing)
      .luminosity(10, '/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    tool(TinkerTools.veinHammer).basic('/',
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, '/').tank('/')
      .luminosity(7, '/', ModifierIds.haste)
      .luminosity(15, '/', ModifierIds.lightspeed)
      .luminosity(10, '/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    tool(TinkerTools.excavator).basic('/',
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).basic(SMALL, ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .fluid(ModifierIds.bucketing, '/').tank('/')
      .luminosity(7, '/', ModifierIds.haste)
      .luminosity(15, '/', ModifierIds.lightspeed)
      .luminosity(15, SMALL, ModifierIds.glowing)
      .luminosity(10, '/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    tool(TinkerTools.broadAxe).basic('/',
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).basic(SMALL, ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .fluid(ModifierIds.bucketing, '/').tank('/')
      .luminosity(7, '/', ModifierIds.haste)
      .luminosity(15, '/', ModifierIds.lightspeed)
      .luminosity(15, SMALL, ModifierIds.glowing)
      .luminosity(10, '/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    tool(TinkerTools.scythe).basic('/',
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).basic(SMALL, ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .fluid(ModifierIds.bucketing, '/').tank('/')
      .luminosity(7, '/', ModifierIds.haste)
      .luminosity(15, '/', ModifierIds.lightspeed)
      .luminosity(15, SMALL, ModifierIds.glowing)
      .luminosity(10, '/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    // melee weapon
    tool(TinkerTools.cleaver).basic('/',
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).basic(SMALL, ModifierIds.soulbound, TinkerModifiers.severing.getId(), ModifierIds.necrotic)
      .fluid(ModifierIds.bucketing, '/').tank('/')
      .luminosity(7, '/', ModifierIds.haste)
      .luminosity(15, '/', ModifierIds.lightspeed)
      .luminosity(15, SMALL, ModifierIds.glowing)
      .luminosity(10, '/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    tool(TinkerTools.javelin).basic('/',
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic,
        ModifierIds.experienced, ModifierIds.luck, ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, '/').tank('/')
      .luminosity(7, '/', ModifierIds.haste)
      .luminosity(15, '/', ModifierIds.lightspeed)
      .luminosity(10, '/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    // ranged weapon
    tool(TinkerTools.longbow).basic('/',
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.freezing,
      ModifierIds.trueshot, ModifierIds.bounce, ModifierIds.power
      )
      .luminosity(10,'/', ModifierIds.fiery)
      .luminosity(2, '/', ModifierIds.unbreakable);
    for (int i = 1; i < 4; i++){
      tool(TinkerTools.longbow, "/" + i).basic('/', "_" + i,
        ModifierIds.diamond, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.freezing
        ) .luminosity(10,'/', "_" + i,ModifierIds.fiery)
        .luminosity(2, '/', "_" + i, ModifierIds.unbreakable);
    }
    tool(TinkerTools.longbow, "/broken").basic('/', "_broken", ModifierIds.overforced, ModifierIds.reinforced);

    // ancient tools
    tool(TinkerTools.battlesign).basic(SMALL,
        ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
        ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.magnetic, ModifierIds.soulbound,
        ModifierIds.experienced, ModifierIds.luck, TinkerModifiers.severing.getId(), ModifierIds.silky,
        ModifierIds.sharpness, ModifierIds.smite, ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling,
        ModifierIds.knockback, ModifierIds.necrotic,
        ModifierIds.blasting, ModifierIds.hydraulic
      ).fluid(ModifierIds.bucketing, SMALL).tank(SMALL)
      .luminosity(7, SMALL, ModifierIds.haste)
      .luminosity(15, SMALL, ModifierIds.lightspeed, ModifierIds.glowing)
      .luminosity(10, SMALL, ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable);

    // misc
    tool(TinkerTools.flintAndBrick).basic(SMALL,
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.reinforced, ModifierIds.overforced
    ).luminosity(15, SMALL, ModifierIds.glowing)
      .luminosity(2, SMALL, ModifierIds.unbreakable);

    // plate armor
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      String root = "armor/plate/" + type.getName() + "/maille";
      String item = "plate/" + type.getName();
      String path = "armor/" + item + "/modifiers";
      tool(item).basic(path, null,
          ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
          ModifierIds.ricochet, ModifierIds.springy,
          ModifierIds.thorns, ModifierIds.freezing)
        .luminosity(10, path, null, ModifierIds.fiery)
        .dyed(new MaterialHasFallbackModifierModel(1,
        new DyedModifierModel(toolMaterial(root + "_metal"), null),
        new DyedModifierModel(toolMaterial(root), null),
        "metal"
      )).trim(type);
      tool(item + "_broken").dyed(new MaterialHasFallbackModifierModel(1,
        new DyedModifierModel(toolMaterial(root + "_broken_metal"), null),
        new DyedModifierModel(toolMaterial(root + "_broken"), null),
        "metal"
      ));
    }
    // other modifiers
    tool("plate/helmet").basic("armor/plate/helmet/modifiers", null,
      ModifierIds.aquaAffinity, TinkerModifiers.itemFrame.getId(), ModifierIds.respiration);
    tool("plate/chestplate").basic("armor/plate/chestplate/modifiers", null,
      ModifierIds.strength, ModifierIds.knockback,
      ModifierIds.reach, TinkerModifiers.sleeves.getId(), TinkerModifiers.ambidextrous.getId()
    ).luminosity(7, "armor/plate/chestplate/modifiers", null, ModifierIds.haste);
    tool("plate/leggings").basic("armor/plate/leggings/modifiers", null,
      ModifierIds.leaping, ModifierIds.luck, TinkerModifiers.shieldStrap.getId(),
      ModifierIds.speedy, ModifierIds.stepUp, ModifierIds.swiftSneak);
    tool("plate/boots").basic("armor/plate/boots/modifiers", null,
      ModifierIds.depthStrider, ModifierIds.doubleJump, ModifierIds.featherFalling,
      ModifierIds.longFall, ModifierIds.soulspeed)
      .luminosity(15, "armor/plate/boots/modifiers", null, ModifierIds.lightspeed);
    // we include both folders, but limited for small
    tool("plate/shield").banner("armor/plate/shield/banner_small/", "armor/plate/shield/banner_large/");

    // travelers
    travelers("goggles", null);
    travelers("vest", ArmorItem.Type.CHESTPLATE);
    travelers("pants", ArmorItem.Type.LEGGINGS);
    travelers("boots", ArmorItem.Type.BOOTS);
    travelers("shield", null);
    tool("travelers/goggles").customTrim("armor/travelers/goggles", null);

    // slimesuit
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      tool("slime/" + type.getName()).trim(type);
    }
    tool("slime/wings")
      .customTrim("armor/slime/wings")
      .dyed("armor/slime/wings/slime");
    tool("slime/wings_broken").dyed("armor/slime/wings/slime_broken");
    // slimesuit dyeing
    // skull texture is added via the modifier map instead of the tool JSON, since we have to fetch the material anyway to dye it, saves fetching it twice
    tool("slime/helmet").constant("__skull", new SlimeskullModifierModel(toolMaterial("armor/slime/helmet/skull"), 0, 1));
    tool("slime/leggings").dyed("armor/slime/leggings/shell");
    tool("slime/leggings_broken").dyed("armor/slime/leggings/shell_broken");
    tool("slime/boots").dyed("armor/slime/boots/laces");
    String ribcage = "armor/slime/chestplate/ribcage";
    tool("slime/chestplate").dyed(new MaterialHasFallbackModifierModel(0,
      new DyedModifierModel(toolMaterial(ribcage + "_bone"), null),
      new DyedModifierModel(toolMaterial(ribcage), null),
      "bone"
    ));
    tool("slime/chestplate_broken").dyed(new MaterialHasFallbackModifierModel(0,
      new DyedModifierModel(toolMaterial(ribcage + "_broken_bone"), null),
      new DyedModifierModel(toolMaterial(ribcage + "_broken"), null),
      "bone"
    ));

    // ammo
    tool(TinkerTools.arrow).tipped("ammo/arrow_modifiers/tipped").smashing("ammo/arrow_modifiers/smashing").dyed("ammo/arrow_modifiers/dyed");
    tool(TinkerTools.shuriken).tipped("ammo/shuriken_modifiers/tipped").smashing("ammo/shuriken_modifiers/smashing");
    tool(TinkerTools.throwingAxe).tipped("ammo/axe_modifiers/tipped").smashing("ammo/axe_modifiers/smashing");
    // fishing rods have tipped
    tool(TinkerTools.fishingRod).basic(SMALL,
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.experienced, ModifierIds.lure,
      ModifierIds.punch, ModifierIds.trueshot, ModifierIds.bounce, ModifierIds.collecting,
      ModifierIds.grapple, ModifierIds.channeling).luminosity(7, SMALL,ModifierIds.quickCharge)
      .luminosity(10, SMALL,ModifierIds.fiery)
      .luminosity(2, SMALL, ModifierIds.unbreakable)
      .tipped("fishing_rod/modifiers/tipped").fluid().compact(ModifierIds.tank);
    tool(TinkerTools.fishingRod, "/broken").basic(SMALL, "_broken",
        ModifierIds.emerald, ModifierIds.netherite, ModifierIds.reinforced,
        ModifierIds.overforced
        ).constant("tipped", ModifierModel.EMPTY);
    // some modifier textures display on the hook, so hide them when the rod is cast.
    tool(TinkerTools.fishingRod, "/cast").basic(SMALL, "_broken", ModifierIds.netherite).empty(
      ModifierIds.bounce, ModifierIds.collecting, ModifierIds.fiery, ModifierIds.freezing,
      ModifierIds.lure, ModifierIds.unbreakable, ModifierIds.quickCharge, ModifierIds.trueshot,
      ModifierIds.grapple).constant("tipped", ModifierModel.EMPTY);

    // tanks
    tool(TinkerTools.meltingPan).fluid();
    tool(TinkerTools.swasher).fluid();

    // staffs
    tool("staff").basic('_',
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.firestarter,
      ModifierIds.overforced, ModifierIds.reinforced
    ).tank('_').embellishment('_').fluid(ModifierIds.bucketing, '_')
      .luminosity(2, '_', ModifierIds.unbreakable);
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
  private void travelers(String name, @Nullable ArmorItem.Type type) {
    String root = "armor/travelers/" + name + "/modifiers/";
    String item = "travelers/" + name;
    Builder b = tool(item).dyed(root + "dyed");
    if (type != null) {
      b.trim(type);
    }
    tool(item + "_broken").dyed(root + "dyed_broken");
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
