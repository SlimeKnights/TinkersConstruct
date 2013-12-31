package tconstruct.common;

import tconstruct.blocks.OreberryBush;
import tconstruct.blocks.slime.SlimeLeaves;
import tconstruct.blocks.slime.SlimeSapling;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.*;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TRepo
{
    // Supresses console spam when iguana's tweaks remove stuff
    public static boolean supressMissingToolLogs = false;

    //Patterns and other materials
    public static Item blankPattern;
    public static Item materials;
    public static Item toolRod;
    public static Item toolShard;
    public static Item woodPattern;
    public static Item metalPattern;
    //public static Item armorPattern;

    public static Item manualBook;
    public static Item buckets;
    public static Item titleIcon;

    public static Item strangeFood;
    public static Item diamondApple;
    public static Item jerky;
    //public static Item stonePattern;
    //public static Item netherPattern;

    //Tools
    public static ToolCore pickaxe;
    public static ToolCore shovel;
    public static ToolCore hatchet;
    public static ToolCore broadsword;
    public static ToolCore longsword;
    public static ToolCore rapier;
    public static ToolCore dagger;
    public static ToolCore cutlass;

    public static ToolCore frypan;
    public static ToolCore battlesign;
    public static ToolCore chisel;
    public static ToolCore mattock;

    public static ToolCore scythe;
    public static ToolCore lumberaxe;
    public static ToolCore cleaver;
    public static ToolCore excavator;
    public static ToolCore hammer;
    public static ToolCore battleaxe;

    public static ToolCore shortbow;
    public static ToolCore arrow;

    public static Item potionLauncher;

    //Tool parts
    public static Item binding;
    public static Item toughBinding;
    public static Item toughRod;
    public static Item largePlate;

    public static Item pickaxeHead;
    public static Item shovelHead;
    public static Item hatchetHead;
    public static Item frypanHead;
    public static Item signHead;
    public static Item chiselHead;
    public static Item scytheBlade;
    public static Item broadAxeHead;
    public static Item excavatorHead;
    public static Item hammerHead;

    public static Item swordBlade;
    public static Item largeSwordBlade;
    public static Item knifeBlade;

    public static Item wideGuard;
    public static Item handGuard;
    public static Item crossbar;
    public static Item fullGuard;

    public static Item bowstring;
    public static Item arrowhead;
    public static Item fletching;

    //Crafting blocks
    public static Block toolStationWood;
    public static Block toolStationStone;
    public static Block toolForge;
    public static Block craftingStationWood;
    public static Block craftingSlabWood;
    public static Block furnaceSlab;

    public static Block heldItemBlock;
    public static Block craftedSoil;

    public static Block smeltery;
    public static Block lavaTank;
    public static Block searedBlock;
    public static Block castingChannel;
    public static Block metalBlock;
    public static Block tankAir;
    public static Block smelteryNether;
    public static Block lavaTankNether;
    public static Block searedBlockNether;

    public static Block dryingRack;

    //Decoration
    public static Block stoneTorch;
    public static Block stoneLadder;
    public static Block multiBrick;
    public static Block multiBrickFancy;

    public static Block searedSlab;
    public static Block speedSlab;

    public static Block meatBlock;
    public static Block woolSlab1;
    public static Block woolSlab2;
    public static Block glueBlock;

    //Traps
    public static Block landmine;
    public static Block punji;
    public static Block barricadeOak;
    public static Block barricadeSpruce;
    public static Block barricadeBirch;
    public static Block barricadeJungle;
    public static Block slimeExplosive;

    //InfiBlocks
    public static Block speedBlock;
    public static Block clearGlass;
    //public static Block stainedGlass;
    public static Block stainedGlassClear;
    public static Block glassPane;
    //public static Block stainedGlassPane;
    public static Block stainedGlassClearPane;
    public static Block glassMagicSlab;
    public static Block stainedGlassMagicSlab;
    public static Block stainedGlassClearMagicSlab;

    //Liquids
    public static Material liquidMetal;

    public static Fluid moltenIronFluid;
    public static Fluid moltenGoldFluid;
    public static Fluid moltenCopperFluid;
    public static Fluid moltenTinFluid;
    public static Fluid moltenAluminumFluid;
    public static Fluid moltenCobaltFluid;
    public static Fluid moltenArditeFluid;
    public static Fluid moltenBronzeFluid;
    public static Fluid moltenAlubrassFluid;
    public static Fluid moltenManyullynFluid;
    public static Fluid moltenAlumiteFluid;
    public static Fluid moltenObsidianFluid;
    public static Fluid moltenSteelFluid;
    public static Fluid moltenGlassFluid;
    public static Fluid moltenStoneFluid;
    public static Fluid moltenEmeraldFluid;
    public static Fluid bloodFluid;
    public static Fluid moltenNickelFluid;
    public static Fluid moltenLeadFluid;
    public static Fluid moltenSilverFluid;
    public static Fluid moltenShinyFluid;
    public static Fluid moltenInvarFluid;
    public static Fluid moltenElectrumFluid;
    public static Fluid moltenEnderFluid;
    public static Fluid blueSlimeFluid;
    public static Fluid pigIronFluid;

    public static Block moltenIron;
    public static Block moltenGold;
    public static Block moltenCopper;
    public static Block moltenTin;
    public static Block moltenAluminum;
    public static Block moltenCobalt;
    public static Block moltenArdite;
    public static Block moltenBronze;
    public static Block moltenAlubrass;
    public static Block moltenManyullyn;
    public static Block moltenAlumite;
    public static Block moltenObsidian;
    public static Block moltenSteel;
    public static Block moltenGlass;
    public static Block moltenStone;
    public static Block moltenEmerald;
    public static Block blood;
    public static Block moltenNickel;
    public static Block moltenLead;
    public static Block moltenSilver;
    public static Block moltenShiny;
    public static Block moltenInvar;
    public static Block moltenElectrum;
    public static Block moltenEnder;

    //Slime
    public static StepSound slimeStep;
    public static Block slimePool;
    public static Block slimeGel;
    public static Block slimeGrass;
    public static Block slimeTallGrass;
    public static SlimeLeaves slimeLeaves;
    public static SlimeSapling slimeSapling;

    public static Block slimeChannel;
    public static Block slimePad;
    public static Block bloodChannel;

    //Glue
    public static Fluid glueFluid;
    public static Block glueFluidBlock;

    //Ores
    public static Block oreSlag;
    public static Block oreGravel;
    public static OreberryBush oreBerry;
    public static OreberryBush oreBerrySecond;
    public static Item oreBerries;

    //Tool modifiers
    public static ModFlux modFlux;
    public static ModLapis modLapis;
    public static ModAttack modAttack;

    //Wearables
    public static Item heavyHelmet;
    public static Item heavyChestplate;
    public static Item heavyPants;
    public static Item heavyBoots;
    public static Item glove;
    public static Item knapsack;

    public static Item heartCanister;
    public static Item goldHead;

    //Rail-related
    public static Block woodenRail;

    //Chest hooks
    public static ChestGenHooks tinkerHouseChest;
    public static ChestGenHooks tinkerHousePatterns;

    //Armor - basic
    public static Item helmetWood;
    public static Item chestplateWood;
    public static Item leggingsWood;
    public static Item bootsWood;
    public static ArmorMaterial materialWood;

    //Armor - exosuit
    public static Item exoGoggles;
    public static Item exoChest;
    public static Item exoPants;
    public static Item exoShoes;

    public static Fluid[] fluids = new Fluid[27];
    public static Block[] fluidBlocks = new Block[26];
    
    //recipe stuff
    public static boolean thaumcraftAvailable;
    public static boolean initRecipes;
    public static Item[] patternOutputs;
    public static FluidStack[] liquids;


}
