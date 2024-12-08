package slimeknights.tconstruct.common.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.IOreRate;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.utils.Orientation2D;
import slimeknights.tconstruct.world.TinkerHeadType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Config {
  /**
   * Common specific configuration
   */
  public static class Common {

    public final BooleanValue shouldSpawnWithTinkersBook;
    public final List<ConfigurableAction> toolTweaks;

    // recipes
    public final BooleanValue addGravelToFlintRecipe;
    public final BooleanValue cheaperNetheriteAlloy;
    public final BooleanValue witherBoneDrop;
    public final BooleanValue witherBoneConversion;
    public final BooleanValue slimeRecipeFix;
    public final BooleanValue glassRecipeFix;
    public final Map<TinkerHeadType,BooleanValue> headDrops;
    public final DoubleValue repairKitAmount;

    // loot
    public final BooleanValue slimyLootChests;
    public final BooleanValue dropDragonScales;
    public final IntValue wandererAncientToolWeight;

    public final OreRate melterOreRate;
    public final OreRate smelteryOreRate;
    public final OreRate foundryOreRate, foundryByproductRate;

    // overworld
    public final ConfigValue<String> showOnlyToolMaterial;
    public final ConfigValue<String> showOnlyPartMaterial;
    public final BooleanValue showAllTableVariants;
    public final BooleanValue showAllAnvilVariants;
    public final BooleanValue showAllSmelteryVariants;

    // debug
    public final BooleanValue forceIntegrationMaterials;
    public final EnumValue<LogInvalidToolStack> logInvalidToolStack;
    public enum LogInvalidToolStack { STACKTRACE, WARNING, IGNORED };

    Common(ForgeConfigSpec.Builder builder) {
      builder.comment("Everything to do with gameplay").push("gameplay");

      this.shouldSpawnWithTinkersBook = builder
        .comment("Set this to false to disable new players spawning with the Tinkers' Book.")
        .translation("tconstruct.configgui.shouldSpawnWithTinkersBook")
        .worldRestart()
        .define("shouldSpawnWithTinkersBook", true);

      ImmutableList.Builder<ConfigurableAction> actions = ImmutableList.builder();
      actions.add(new ConfigurableAction(builder, "extendFireProtectionSlots", true,
                                         "If true, extends the applicable slots for the fire protection enchantment to work better with shields. Will not impact gameplay with the vanilla enchantment.\nIf false, fire protection on a shield will not reduce fire tick time.",
                                         () -> Enchantments.FIRE_PROTECTION.slots = EquipmentSlot.values()));

      builder.comment("Tweaks to vanilla damage sources to better work with armor").push("damageTweaks");
      actions.add(new ConfigurableAction(builder, "wither", true, "Makes withering damage count as magic", DamageSource.WITHER::setMagic));
      actions.add(new ConfigurableAction(builder, "dragon_breath", true, "Makes dragons breath count as magic", DamageSource.DRAGON_BREATH::setMagic));
      actions.add(new ConfigurableAction(builder, "falling_block", false, "Makes falling blocks count as projectile", () -> {
        DamageSource.FALLING_BLOCK.setProjectile();
        DamageSource.ANVIL.setProjectile();
        DamageSource.FALLING_STALACTITE.setProjectile();
      }));
      actions.add(new ConfigurableAction(builder, "lightning", true, "Makes lightning count as fire damage", DamageSource.LIGHTNING_BOLT::setIsFire));
      toolTweaks = actions.build();

      this.repairKitAmount = builder
        .comment("Amount of durability restored by a repair kit in terms of ingots. Does not affect the cost to create the kit, that is controlled by JSON.")
        .defineInRange("repairKitAmount", 2f, 0f, Short.MAX_VALUE);

      builder.pop();

//      this.chestsKeepInventory = builder
//        .comment("Pattern and Part chests keep their inventory when harvested.")
//        .translation("tconstruct.configgui.chestsKeepInventory")
//        .worldRestart()
//        .define("chestsKeepInventory", true);

      this.showOnlyToolMaterial = builder
        .comment("If non-empty, only this material will be shown on tools in creative and JEI (or the first valid material if this is invalid for the tool).", "If empty, all materials will show")
        .translation("tconstruct.configgui.showOnlyToolMaterial")
        .worldRestart()
        .define("showOnlyToolMaterial", "");

      this.showOnlyPartMaterial = builder
        .comment("If non-empty, only material will be shown on parts in creative and JEI (or the first valid material if this is invalid for the part).", "If empty, all materials will show")
        .translation("tconstruct.configgui.showOnlyPartMaterial")
        .worldRestart()
        .define("showOnlyPartMaterial", "");

      this.showAllTableVariants = builder
        .comment("If true, tables such as the part builder and tinker station will show all variants. If false shows only a variant with a default texture.")
        .translation("tconstruct.configgui.showAllTableVariants")
        .define("showAllTableVariants", true);

      this.showAllAnvilVariants = builder
        .comment("If true, anvils will show all metal variants. If false, shows only a variant with the default texture")
        .translation("tconstruct.configgui.showAllAnvilVariants")
        .define("showAllAnvilVariants", true);

      this.showAllSmelteryVariants = builder
        .comment("If true, smeltery and foundry controllers, drains, ducts, and chutes will show all variants")
        .translation("tconstruct.configgui.showAllSmelteryVariants")
        .define("showAllSmelteryVariants", true);

      builder.pop();

      builder.comment("Options related to recipes, limited options as a datapack allows most recipes to be modified").push("recipes");

      this.addGravelToFlintRecipe = builder
        .comment("Add a recipe that allows you to craft a piece of flint using 3 gravel")
        .translation("tconstruct.configgui.addGravelToFlintRecipe")
        .worldRestart()
        .define("addGravelToFlintRecipe", true);

      this.cheaperNetheriteAlloy = builder
        .comment("Makes the recipe to alloy netherite in the smeltery only cost 2 gold per netherite ingot. If false uses the vanilla rate of 4 gold per ingot. Disable if there are crafting duplications.")
        .translation("tconstruct.configgui.cheaperNetheriteAlloy")
        .worldRestart()
        .define("cheaperNetheriteAlloy", true);

      this.witherBoneDrop = builder
        .comment("Makes wither skeletons drop necrotic bones")
        .translation("tconstruct.configgui.witherBoneDrop")
        .worldRestart()
        .define("witherBoneDrop", true);

      this.witherBoneConversion = builder
        .comment("Allows converting wither bones to regular bones")
        .translation("tconstruct.configgui.witherBoneConversion")
        .worldRestart()
        .define("witherBoneConversion", true);

      this.slimeRecipeFix = builder
        .comment("Slimeballs not being usable in vanilla recipes that require slimeballs. Config option exists to disable easily in case this fix is redundant to another mod")
        .worldRestart()
        .define("slimeRecipeFix", true);

      this.glassRecipeFix = builder
        .comment("Fixes clear glass not being usable in vanilla recipes that require glass. Config option exists to disable easily in case this fix is redundant to another mod")
        .translation("tconstruct.configgui.glassRecipeFix")
        .worldRestart()
        .define("glassRecipeFix", true);

      builder.push("ore_rates");
      {
        builder.comment("Ore rates when melting in the melter").push("melter");
        this.melterOreRate = new OreRate(builder, 12, 8);
        builder.pop();

        builder.comment("Ore rates when melting in the smeltery").push("smeltery");
        this.smelteryOreRate = new OreRate(builder, 12, 8);
        builder.pop();

        builder.comment("Ore rates when melting in the foundry").push("foundry");
        this.foundryOreRate = new OreRate(builder, 9, 4);
        builder.pop();

        builder.comment("Byprouct rates when melting in the foundry").push("foundry_byproduct");
        this.foundryByproductRate = new OreRate(builder, 3, 4);
        builder.pop();
      }
      builder.pop();

      builder.comment("Entity head drops when killed by a charged creeper").push("heads");
      headDrops = new EnumMap<>(TinkerHeadType.class);
      for (TinkerHeadType headType : TinkerHeadType.values()) {
        headDrops.put(headType, builder
          .translation("tconstruct.configgui.heads." + headType.getSerializedName())
          .define(headType.getSerializedName(), true));
      }

      builder.pop(2);

      builder.comment(
        "Options related to loot table injections. Note some of the changes are done via global loot managers, these only control injecting loot into loot pools",
        "If your modpack makes extensive loot table changes, many of these may be automatically disabled. You can also manually set up tables for more control.").push("loot");

      slimyLootChests = builder
        .comment("Adds slimy saplings and seeds into various loot chests. Helps for worlds without slime islands")
        .worldRestart()
        .define("slimy_loot", true);
      dropDragonScales = builder
        .comment("If true, ender dragons will drop scales when damaged by explosions")
        .define("drop_dragon_Scales", true);
      wandererAncientToolWeight = builder
        .comment("Weight of the ancient tool trade for the wandering trader. All traders randomly choose 1 rare trade, so this is roughly the chance the trade occurs compared to the vanilla options (of which there are 6).")
        .defineInRange("wanderer_ancient_tool_weight", 6, 0, 100);

      builder.pop();

      builder.comment("Features to use in debugging gameplay and mechanics, generally should not be enabled in packs").push("debug");
      this.forceIntegrationMaterials = builder
        .comment("If true, forces integration materials to be enabled, even if the relevant metal is missing. Useful for testing material balance.",
                 "Does not provide recipes for any of them, they will only be available to cheat in creative.")
        .worldRestart()
        .define("forceIntegrationMaterials", false);
      this.logInvalidToolStack = builder
        .comment("If STACKTRACE, logs the stacktrace whenever a tool stack is created from a non-modifiable item. If WARNING (default), logs a shorter but more efficient error. If IGNORE, disables logging (useful for modpacks/players *after* they reported the issue). The stacktrace helps debug which mod is causing it, but is rather expensive on the chance they are doing it a lot.")
        .defineEnum("logInvalidToolStack", LogInvalidToolStack.WARNING);
      builder.pop();
    }
  }

  /**
   * Client specific configuration - only loaded clientside from tconstruct-client.toml
   */
  public static class Client {
    //public final ForgeConfigSpec.BooleanValue temperatureInCelsius;
    public final ForgeConfigSpec.BooleanValue tankFluidModel;
    public final ForgeConfigSpec.BooleanValue extraToolTips; // TODO: do we even need this config option? who would turn it off?
    public final ForgeConfigSpec.BooleanValue logMissingMaterialTextures;
    public final ForgeConfigSpec.BooleanValue logMissingModifierTextures;
    public final ForgeConfigSpec.BooleanValue showModifiersInJEI;
    public final ForgeConfigSpec.BooleanValue renderShieldSlotItem;
    public final ForgeConfigSpec.BooleanValue modifiersIDsInAdvancedTooltips;
    public final ForgeConfigSpec.IntValue maxSmelteryItemQuads;

    // framed modifier
    public final ForgeConfigSpec.BooleanValue renderItemFrame;
    public final ForgeConfigSpec.IntValue itemFrameXOffset;
    public final ForgeConfigSpec.IntValue itemFrameYOffset;
    public final ForgeConfigSpec.EnumValue<Orientation2D> itemFrameLocation;
    public final ForgeConfigSpec.IntValue itemsPerRow;

    Client(ForgeConfigSpec.Builder builder) {
      builder.comment("Client only settings").push("client");

//      this.temperatureInCelsius = builder
//        .comment("If true, temperatures in the smeltery and in JEI will display in celsius. If false they will use the internal units of Kelvin, which may be better for developers")
//        .translation("tconstruct.configgui.temperatureInCelsius")
//        .define("temperatureInCelsius", true);

      this.tankFluidModel = builder
        .comment(
          "Experimental. If true, renders fluids in tanks using a dynamic model, being more efficient when the tank is static",
          "If false, renders fluids in tanks using a TESR, which is more efficient when the tank contents are changing"
         )
        .translation("tconstruct.configgui.tankFluidModel")
        .define("tankFluidModel", false);

      this.extraToolTips = builder
        .comment("If true tools will show additional info in their tooltips")
        .translation("tconstruct.configgui.extraToolTips")
        .define("extraToolTips", true);

      this.logMissingMaterialTextures = builder
        .comment("If true, the game will log all material textures which do not exist in resource packs but can be added, can be helpful for moddevs or resourcepack makers")
        .translation("tconstruct.configgui.logMissingMaterialTextures")
        .define("logMissingMaterialTextures", false);

      this.logMissingModifierTextures = builder
        .comment("If true, the game will log all modifier textures which do not exist in resource packs but can be added, can be helpful for moddevs or resourcepack makers")
        .translation("tconstruct.configgui.logMissingMaterialTextures")
        .define("logMissingModifierTextures", false);

      this.showModifiersInJEI = builder
        .comment("If true, modifiers will be added to the JEI ingredient list. If false, they will only be visible in the modifiers recipe tab.")
        .translation("tconstruct.configgui.showModifiersInJEI")
        .define("showModifiersInJEI", true);

      this.maxSmelteryItemQuads = builder
        .comment("Maximum number of quads to render for items in the smeltery. Most blocks are about 6 quads, items like ingots are around 26.",
                 "Setting this lower will cause fewer items to be renderer (but never a partial item). Set to -1 to allow unlimited quads, and 0 to disable the item renderer.")
        .defineInRange("maxSmelteryItemQuads", 3500, -1, Short.MAX_VALUE);

      this.modifiersIDsInAdvancedTooltips = builder
        .comment("If true, shows modifier IDs in advanced tooltips for tools and tool parts.",
                 "They are more intrusive than most advanced tooltip content, so this option is provided in case some mod made poor design decisions and put essential gameplay info in tooltips or for pack makers who do not need modifier info.")
        .define("modifiersIDsInAdvancedTooltips", true);

      builder.comment("Settings related to modifiers").push("modifiers");
      {

        this.renderShieldSlotItem = builder
          .comment("If true, the shield slot legging modifier will render the next offhand item above the offhand slot.")
          .define("renderShieldSlotItem", true);

        builder.comment("Settings related to the frame helmet modifier").push("itemFrame");
        {
          this.renderItemFrame = builder
            .comment("If true, the item frame modifier for helmets will render its items. Turning this to false makes the modifier useless.")
            .define("render", true);
          this.itemFrameXOffset = builder
            .comment("Offset in the X direction for the frame items.")
            .defineInRange("xOffset", 0, Short.MIN_VALUE, Short.MAX_VALUE);
          this.itemFrameYOffset = builder
            .comment("Offset in the Y direction for the frame items.")
            .defineInRange("yOffset", 0, Short.MIN_VALUE, Short.MAX_VALUE);
          this.itemFrameLocation = builder
            .comment("Location of the frame on the screen.")
            .defineEnum("location", Orientation2D.TOP_LEFT);
          this.itemsPerRow = builder
            .comment("Number of items to display in each row of the item frame.")
            .defineInRange("itemsPerRow", 5, 0, 100);
        }
        builder.pop();
      }
      builder.pop();

      builder.pop();
    }
  }

  public static final ForgeConfigSpec clientSpec;
  public static final Client CLIENT;

  static {
    final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
    clientSpec = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public static final ForgeConfigSpec commonSpec;
  public static final Common COMMON;

  static {
    final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
    commonSpec = specPair.getRight();
    COMMON = specPair.getLeft();
  }

  /** Registers any relevant listeners for config */
  public static void init() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);

    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
  }

  /** Configuration for an ore rate, such as melter or foundry */
  public static class OreRate implements IOreRate {
    private final ConfigValue<Integer> nuggetsPerMetal;
    private final ConfigValue<Integer> shardsPerGem;

    public OreRate(ForgeConfigSpec.Builder builder, int defaultNuggets, int defaultQuarters) {
      nuggetsPerMetal = builder
        .comment("Number of nuggets produced per metal ore unit melted. 9 nuggets would give 1 ingot")
        .defineInRange("nuggetsPerMetal", defaultNuggets, 1, 45);
      shardsPerGem = builder
        .comment("Number of gem shards produced per gem ore unit melted. 4 gem shards would give 1 gem")
        .defineInRange("shardsPerGem", defaultQuarters, 1, 20);
    }

    @Override
    public int applyOreBoost(OreRateType rate, int amount) {
      return switch (rate) {
        case METAL -> amount * nuggetsPerMetal.get() / 9;
        case GEM -> amount * shardsPerGem.get() / 4;
        default -> amount;
      };
    }
  }
}
