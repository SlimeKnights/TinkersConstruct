package slimeknights.tconstruct.tools.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.MaterialModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.MaterialHasFallbackModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.TrimModifierModel;
import slimeknights.tconstruct.library.data.AbstractModifierModelMapProvider;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
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
    // weapon
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
    // weapon
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
    ModifierId dyed = TinkerModifiers.dyed.getId();
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      String root = "armor/plate/" + type.getName() + "/maille";
      String item = "plate/" + type.getName();
      tool(item).modifier(dyed, new MaterialHasFallbackModifierModel(1,
        new DyedModifierModel(toolMaterial(root + "_metal"), null),
        new DyedModifierModel(toolMaterial(root), null),
        "metal"
      )).trim(type);
      tool(item + "_broken").modifier(dyed, new MaterialHasFallbackModifierModel(1,
        new DyedModifierModel(toolMaterial(root + "_broken_metal"), null),
        new DyedModifierModel(toolMaterial(root + "_broken"), null),
        "metal"
      ));
    }

    // travelers
    travelers("goggles", null);
    travelers("vest", ArmorItem.Type.CHESTPLATE);
    travelers("pants", ArmorItem.Type.LEGGINGS);
    travelers("boots", ArmorItem.Type.BOOTS);
    travelers("shield", null);
    tool("travelers/goggles").customTrim("armor/travelers/goggles", null);

    // slimesuit
    slime("skull", ArmorItem.Type.HELMET);
    slime("wings", null);
    slime("shell", ArmorItem.Type.LEGGINGS);
    slime("boot",  ArmorItem.Type.BOOTS);
    tool("slime/wings").modifier(TinkerModifiers.trim.getId(), new TrimModifierModel.Custom(toolMaterial("armor/slime/wings_trim").texture(), null));

    // ammo
    tool(TinkerTools.arrow).tipped("ammo/arrow_modifiers/tipped").smashing("ammo/arrow_modifiers/smashing")
      .modifier(dyed, new DyedModifierModel(toolMaterial("ammo/arrow_modifiers/dyed"), null));
    tool(TinkerTools.shuriken).tipped("ammo/shuriken_modifiers/tipped").smashing("ammo/shuriken_modifiers/smashing");
    tool(TinkerTools.throwingAxe).tipped("ammo/axe_modifiers/tipped").smashing("ammo/axe_modifiers/smashing");
    // fishing rods just have tipped
    tool(TinkerTools.fishingRod).tipped("fishing_rod/modifiers/tipped").fluid().compact(ModifierIds.tank);
    tool(TinkerTools.fishingRod, "/broken").constant("tipped", ModifierModel.EMPTY);
    tool(TinkerTools.fishingRod, "/cast").constant("tipped", ModifierModel.EMPTY);

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
    ModifierId dyed = TinkerModifiers.dyed.getId();
    String item = "travelers/" + name;
    Builder b = tool(item).modifier(dyed, new DyedModifierModel(toolMaterial(root + "dyed"), null));
    if (type != null) {
      b.trim(type);
    }
    tool(item + "_broken").modifier(dyed, new DyedModifierModel(toolMaterial(root + "dyed_broken"), null));
  }

  /** Adds dyed textures for travelers gear */
  private void slime(String name, @Nullable ArmorItem.Type type) {
    String root = "armor/slime/" + name + "_modifiers/";
    ModifierId embellishment = TinkerModifiers.embellishment.getId();
    String item = "slime/" + name;
    Builder b = tool(item).modifier(embellishment, new MaterialModifierModel(toolMaterial(root + "tconstruct_embellishment"), null));
    if (type != null) {
      b.trim(type);
    }
    tool(item + "_broken").modifier(embellishment, new MaterialModifierModel(toolMaterial(root + "broken/tconstruct_embellishment"), null));
  }

  /** Adds dyed textures to a staff */
  private void staffDyed(String name) {
    String staff = "staff/" + name;
    String small = "staff/modifiers/" + name + "/dyed";
    String large = "staff/large_modifiers/" + name + "/dyed";
    ModifierId dyed = TinkerModifiers.dyed.getId();
    tool(staff).modifier(dyed, new DyedModifierModel(toolMaterial(small), toolMaterial(large)));
    tool(staff + "/broken").modifier(dyed, new DyedModifierModel(toolMaterial(small + "_broken"), toolMaterial(large + "_broken")));
    for (int i = 1; i <= 5; i++) {
      String variant = Integer.toString(i);
      tool(staff + '/' + variant).modifier(dyed, new DyedModifierModel(toolMaterial(small + '_' + variant), toolMaterial(large + '_' + variant)));
    }
  }
}
