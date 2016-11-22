package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.modifiers.ModAntiMonsterType;
import slimeknights.tconstruct.tools.modifiers.ModBeheading;
import slimeknights.tconstruct.tools.modifiers.ModBlasting;
import slimeknights.tconstruct.tools.modifiers.ModCreative;
import slimeknights.tconstruct.tools.modifiers.ModDiamond;
import slimeknights.tconstruct.tools.modifiers.ModEmerald;
import slimeknights.tconstruct.tools.modifiers.ModFiery;
import slimeknights.tconstruct.tools.modifiers.ModFins;
import slimeknights.tconstruct.tools.modifiers.ModFortify;
import slimeknights.tconstruct.tools.modifiers.ModGlowing;
import slimeknights.tconstruct.tools.modifiers.ModHarvestSize;
import slimeknights.tconstruct.tools.modifiers.ModHaste;
import slimeknights.tconstruct.tools.modifiers.ModKnockback;
import slimeknights.tconstruct.tools.modifiers.ModLuck;
import slimeknights.tconstruct.tools.modifiers.ModMendingMoss;
import slimeknights.tconstruct.tools.modifiers.ModNecrotic;
import slimeknights.tconstruct.tools.modifiers.ModReinforced;
import slimeknights.tconstruct.tools.modifiers.ModSharpness;
import slimeknights.tconstruct.tools.modifiers.ModShulking;
import slimeknights.tconstruct.tools.modifiers.ModSilktouch;
import slimeknights.tconstruct.tools.modifiers.ModSoulbound;
import slimeknights.tconstruct.tools.modifiers.ModWebbed;
import slimeknights.tconstruct.tools.traits.InfiTool;

@Pulse(
    id = TinkerModifiers.PulseId,
    description = "All the modifiers in one handy package",
    pulsesRequired = TinkerTools.PulseId,
    forced = true)
public class TinkerModifiers extends AbstractToolPulse {

  public static final String PulseId = "TinkerModifiers";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.tools.ToolClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // Modifiers
  public static Modifier modBaneOfArthopods;
  public static Modifier modBeheading;
  public static Modifier modBlasting;
  public static Modifier modDiamond;
  public static Modifier modEmerald;
  public static Modifier modFiery;
  public static Modifier modFins;
  public static Modifier modGlowing;
  public static Modifier modHaste;
  public static Modifier modHarvestWidth;
  public static Modifier modHarvestHeight;
  public static Modifier modKnockback;
  public static ModLuck  modLuck;
  public static Modifier modMendingMoss;
  public static Modifier modNecrotic;
  public static Modifier modReinforced;
  public static Modifier modSharpness;
  public static Modifier modShulking;
  public static Modifier modSilktouch;
  public static Modifier modWebbed;
  public static Modifier modSmite;
  public static Modifier modSoulbound;

  public static Modifier modCreative;

  public static List<Modifier> fortifyMods;

  @Override
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    registerModifiers();
  }

  // POST-INITIALIZATION
  @Override
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerFortifyModifiers();
  }

  protected void registerModifiers() {
    ItemStack tnt = new ItemStack(Blocks.TNT);
    ItemStack glowstoneDust = new ItemStack(Items.GLOWSTONE_DUST);

    // create the modifiers and add their items
    modBaneOfArthopods = new ModAntiMonsterType("bane_of_arthopods", 0x61ba49, 5, 24, EnumCreatureAttribute.ARTHROPOD);
    modBaneOfArthopods = registerModifier(modBaneOfArthopods);
    modBaneOfArthopods.addItem(Items.FERMENTED_SPIDER_EYE);

    modBeheading = registerModifier(new ModBeheading());
    modBeheading.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(Items.ENDER_PEARL), new ItemStack(Blocks.OBSIDIAN)));

    modBlasting = registerModifier(new ModBlasting());
    modBlasting.addRecipeMatch(new RecipeMatch.ItemCombination(1, tnt, tnt, tnt));

    modDiamond = registerModifier(new ModDiamond());
    modDiamond.addItem("gemDiamond");

    modEmerald = registerModifier(new ModEmerald());
    modEmerald.addItem("gemEmerald");

    modFiery = registerModifier(new ModFiery());
    modFiery.addItem(Items.BLAZE_POWDER);

    modFins = registerModifier(new ModFins());
    modFins.addItem("fish");

    modGlowing = registerModifier(new ModGlowing());
    modGlowing.addRecipeMatch(new RecipeMatch.ItemCombination(1, glowstoneDust, new ItemStack(Items.ENDER_EYE), glowstoneDust));

    modHaste = registerModifier(new ModHaste(50));
    modHaste.addItem("dustRedstone");
    modHaste.addItem("blockRedstone", 1, 9);

    modHarvestWidth = registerModifier(new ModHarvestSize("width"));
    modHarvestWidth.addItem(TinkerCommons.matExpanderW, 1, 1);

    modHarvestHeight = registerModifier(new ModHarvestSize("height"));
    modHarvestHeight.addItem(TinkerCommons.matExpanderH, 1, 1);

    modKnockback = registerModifier(new ModKnockback());
    modKnockback.addItem(Blocks.PISTON, 1);
    modKnockback.addItem(Blocks.STICKY_PISTON, 1);

    modLuck = registerModifier(new ModLuck());
    modLuck.addItem("gemLapis");
    modLuck.addItem("blockLapis", 1, 9);

    modMendingMoss = registerModifier(new ModMendingMoss());
    modMendingMoss.addItem(TinkerCommons.matMendingMoss, 1, 1);

    modNecrotic = registerModifier(new ModNecrotic());
    modNecrotic.addItem(TinkerCommons.matNecroticBone, 1, 1);

    modReinforced = registerModifier(new ModReinforced());
    modReinforced.addItem(TinkerCommons.matReinforcement, 1, 1);

    modSharpness = registerModifier(new ModSharpness(72));
    modSharpness.addItem("gemQuartz");
    modSharpness.addItem("blockQuartz", 1, 4);

    modShulking = registerModifier(new ModShulking());
    modShulking.addItem(Items.CHORUS_FRUIT_POPPED);

    modSilktouch = registerModifier(new ModSilktouch());
    modSilktouch.addItem(TinkerCommons.matSilkyJewel, 1, 1);

    modWebbed = registerModifier(new ModWebbed());
    modWebbed.addItem(Blocks.WEB, 1);

    modSmite = new ModAntiMonsterType("smite", 0xe8d500, 5, 24, EnumCreatureAttribute.UNDEAD);
    modSmite = registerModifier(modSmite);
    modSmite.addItem(TinkerCommons.consecratedSoil, 1, 1);

    modSoulbound = registerModifier(new ModSoulbound());
    modSoulbound.addItem(Items.NETHER_STAR);

    modCreative = registerModifier(new ModCreative());
    modCreative.addItem(TinkerCommons.matCreativeModifier, 1, 1);

    // ensure infitool trait
    TinkerRegistry.addTrait(InfiTool.INSTANCE);
  }


  private void registerFortifyModifiers() {
    fortifyMods = Lists.newArrayList();
    for(Material mat : TinkerRegistry.getAllMaterialsWithStats(MaterialTypes.HEAD)) {
      fortifyMods.add(new ModFortify(mat));
    }
  }
}
