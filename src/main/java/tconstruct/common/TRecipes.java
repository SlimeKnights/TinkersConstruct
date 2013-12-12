package tconstruct.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mantle.blocks.abstracts.MultiServantLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.*;
import tconstruct.items.blocks.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.*;
import tconstruct.library.util.IPattern;
import tconstruct.modifiers.tools.*;
import tconstruct.util.RecipeRemover;
import tconstruct.util.TDispenserBehaviorArrow;
import tconstruct.util.TDispenserBehaviorSpawnEgg;
import tconstruct.util.config.PHConstruct;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class TRecipes
{
    static void registerBlockRecipes ()
    {
        //Tool Tables
        GameRegistry.registerBlock(TRepo.toolStationWood, ToolStationItemBlock.class, "ToolStationBlock");
        GameRegistry.registerTileEntity(ToolStationLogic.class, "ToolStation");
        GameRegistry.registerTileEntity(PartBuilderLogic.class, "PartCrafter");
        GameRegistry.registerTileEntity(PatternChestLogic.class, "PatternHolder");
        GameRegistry.registerTileEntity(StencilTableLogic.class, "PatternShaper");
        GameRegistry.registerBlock(TRepo.toolForge, MetadataItemBlock.class, "ToolForgeBlock");
        GameRegistry.registerTileEntity(ToolForgeLogic.class, "ToolForge");
        GameRegistry.registerBlock(TRepo.craftingStationWood, "CraftingStation");
        GameRegistry.registerTileEntity(CraftingStationLogic.class, "CraftingStation");
        GameRegistry.registerBlock(TRepo.craftingSlabWood, CraftingSlabItemBlock.class, "CraftingSlab");
        GameRegistry.registerBlock(TRepo.furnaceSlab, "FurnaceSlab");
        GameRegistry.registerTileEntity(FurnaceLogic.class, "TConstruct.Furnace");

        GameRegistry.registerBlock(TRepo.heldItemBlock, "HeldItemBlock");
        GameRegistry.registerTileEntity(FrypanLogic.class, "FrypanLogic");

        GameRegistry.registerBlock(TRepo.craftedSoil, CraftedSoilItemBlock.class, "CraftedSoil");

        GameRegistry.registerBlock(TRepo.searedSlab, SearedSlabItem.class, "SearedSlab");
        GameRegistry.registerBlock(TRepo.speedSlab, SpeedSlabItem.class, "SpeedSlab");

        GameRegistry.registerBlock(TRepo.metalBlock, MetalItemBlock.class, "MetalBlock");
        GameRegistry.registerBlock(TRepo.meatBlock, HamboneItemBlock.class, "MeatBlock");

        OreDictionary.registerOre("hambone", new ItemStack(TRepo.meatBlock));
        GameRegistry.addRecipe(new ItemStack(TRepo.meatBlock), "mmm", "mbm", "mmm", 'b', new ItemStack(Item.bone), 'm', new ItemStack(Item.porkRaw));

        GameRegistry.registerBlock(TRepo.glueBlock, "GlueBlock");
        OreDictionary.registerOre("blockRubber", new ItemStack(TRepo.glueBlock));

        GameRegistry.registerBlock(TRepo.woolSlab1, WoolSlab1Item.class, "WoolSlab1");
        GameRegistry.registerBlock(TRepo.woolSlab2, WoolSlab2Item.class, "WoolSlab2");

        //Smeltery stuff
        GameRegistry.registerBlock(TRepo.smeltery, SmelteryItemBlock.class, "Smeltery");
        GameRegistry.registerBlock(TRepo.smelteryNether, SmelteryItemBlock.class, "SmelteryNether");
        if (PHConstruct.newSmeltery)
        {
            GameRegistry.registerTileEntity(AdaptiveSmelteryLogic.class, "TConstruct.Smeltery");
            GameRegistry.registerTileEntity(AdaptiveDrainLogic.class, "TConstruct.SmelteryDrain");
        }
        else
        {
            GameRegistry.registerTileEntity(SmelteryLogic.class, "TConstruct.Smeltery");
            GameRegistry.registerTileEntity(SmelteryDrainLogic.class, "TConstruct.SmelteryDrain");
        }
        GameRegistry.registerTileEntity(MultiServantLogic.class, "TConstruct.Servants");
        GameRegistry.registerBlock(TRepo.lavaTank, LavaTankItemBlock.class, "LavaTank");
        GameRegistry.registerBlock(TRepo.lavaTankNether, LavaTankItemBlock.class, "LavaTankNether");
        GameRegistry.registerTileEntity(LavaTankLogic.class, "TConstruct.LavaTank");

        GameRegistry.registerBlock(TRepo.searedBlock, SearedTableItemBlock.class, "SearedBlock");
        GameRegistry.registerBlock(TRepo.searedBlockNether, SearedTableItemBlock.class, "SearedBlockNether");
        GameRegistry.registerTileEntity(CastingTableLogic.class, "CastingTable");
        GameRegistry.registerTileEntity(FaucetLogic.class, "Faucet");
        GameRegistry.registerTileEntity(CastingBasinLogic.class, "CastingBasin");

        GameRegistry.registerBlock(TRepo.castingChannel, CastingChannelItem.class, "CastingChannel");
        GameRegistry.registerTileEntity(CastingChannelLogic.class, "CastingChannel");

        GameRegistry.registerBlock(TRepo.tankAir, "TankAir");
        GameRegistry.registerTileEntity(TankAirLogic.class, "tconstruct.tank.air");

        //Traps
        GameRegistry.registerBlock(TRepo.landmine, ItemBlockLandmine.class, "Redstone.Landmine");
        GameRegistry.registerTileEntity(TileEntityLandmine.class, "Landmine");
        GameRegistry.registerBlock(TRepo.punji, "trap.punji");
        GameRegistry.registerBlock(TRepo.barricadeOak, BarricadeItem.class, "trap.barricade.oak");
        GameRegistry.registerBlock(TRepo.barricadeSpruce, BarricadeItem.class, "trap.barricade.spruce");
        GameRegistry.registerBlock(TRepo.barricadeBirch, BarricadeItem.class, "trap.barricade.birch");
        GameRegistry.registerBlock(TRepo.barricadeJungle, BarricadeItem.class, "trap.barricade.jungle");
        GameRegistry.registerBlock(TRepo.slimeExplosive, MetadataItemBlock.class, "explosive.slime");

        GameRegistry.registerBlock(TRepo.dryingRack, "Armor.DryingRack");
        GameRegistry.registerTileEntity(DryingRackLogic.class, "Armor.DryingRack");
        //fluids

        //Slime Islands
        GameRegistry.registerBlock(TRepo.slimeGel, SlimeGelItemBlock.class, "slime.gel");
        GameRegistry.registerBlock(TRepo.slimeGrass, SlimeGrassItemBlock.class, "slime.grass");
        GameRegistry.registerBlock(TRepo.slimeTallGrass, SlimeTallGrassItem.class, "slime.grass.tall");
        GameRegistry.registerBlock(TRepo.slimeLeaves, SlimeLeavesItemBlock.class, "slime.leaves");
        GameRegistry.registerBlock(TRepo.slimeSapling, SlimeSaplingItemBlock.class, "slime.sapling");
        GameRegistry.registerBlock(TRepo.slimeChannel, "slime.channel");
        GameRegistry.registerBlock(TRepo.bloodChannel, "blood.channel");
        GameRegistry.registerBlock(TRepo.slimePad, "slime.pad");
        TConstructRegistry.drawbridgeState[TRepo.slimePad.blockID] = 1;
        TConstructRegistry.drawbridgeState[TRepo.bloodChannel.blockID] = 1;

        //Decoration
        GameRegistry.registerBlock(TRepo.stoneTorch, "decoration.stonetorch");
        GameRegistry.registerBlock(TRepo.stoneLadder, "decoration.stoneladder");
        GameRegistry.registerBlock(TRepo.multiBrick, MultiBrickItem.class, "decoration.multibrick");
        GameRegistry.registerBlock(TRepo.multiBrickFancy, MultiBrickFancyItem.class, "decoration.multibrickfancy");

        //Ores
        GameRegistry.registerBlock(TRepo.oreBerry, OreberryBushItem.class, "ore.berries.one");
        GameRegistry.registerBlock(TRepo.oreBerrySecond, OreberryBushSecondItem.class, "ore.berries.two");
        GameRegistry.registerBlock(TRepo.oreSlag, MetalOreItemBlock.class, "SearedBrick");
        GameRegistry.registerBlock(TRepo.oreGravel, GravelOreItem.class, "GravelOre");

        GameRegistry.registerBlock(TRepo.speedBlock, SpeedBlockItem.class, "SpeedBlock");

        //Glass
        GameRegistry.registerBlock(TRepo.clearGlass, GlassBlockItem.class, "GlassBlock");
        GameRegistry.registerBlock(TRepo.glassPane, GlassPaneItem.class, "GlassPane");
        GameRegistry.registerBlock(TRepo.stainedGlassClear, StainedGlassClearItem.class, "GlassBlock.StainedClear");
        GameRegistry.registerBlock(TRepo.stainedGlassClearPane, StainedGlassClearPaneItem.class, "GlassPaneClearStained");

        //Rail
        GameRegistry.registerBlock(TRepo.woodenRail, "rail.wood");
    }

    static void registerItemRecipes ()
    {

    }

    public static void addOreDictionarySmelteryRecipes ()
    {
        List<FluidType> exceptions = Arrays.asList(new FluidType[] { FluidType.Water, FluidType.Stone, FluidType.Ender, FluidType.Glass, FluidType.Slime });
        for (FluidType ft : FluidType.values())
        {
            if (exceptions.contains(ft))
                continue;

            // Nuggets
            Smeltery.addDictionaryMelting("nugget" + ft.toString(), ft, -100, TConstruct.nuggetLiquidValue);

            // Ingots, Dust
            registerIngotCasting(ft);
            Smeltery.addDictionaryMelting("ingot" + ft.toString(), ft, -50, TConstruct.ingotLiquidValue);
            Smeltery.addDictionaryMelting("dust" + ft.toString(), ft, -75, TConstruct.ingotLiquidValue);

            // Factorization support
            Smeltery.addDictionaryMelting("crystalline" + ft.toString(), ft, -50, TConstruct.ingotLiquidValue);

            // Ores
            Smeltery.addDictionaryMelting("ore" + ft.toString(), ft, 0, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre));

            // NetherOres support
            Smeltery.addDictionaryMelting("oreNether" + ft.toString(), ft, 75, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPerOre * 2));

            // Blocks
            Smeltery.addDictionaryMelting("block" + ft.toString(), ft, 100, TConstruct.blockLiquidValue);

            if (ft.isToolpart)
            {
                registerPatternMaterial("ingot" + ft.toString(), 2, ft.toString());
                registerPatternMaterial("block" + ft.toString(), 18, ft.toString());
            }
        }

        //Compressed materials
        for (int i = 1; i <= 8; i++)
        {
            Smeltery.addDictionaryMelting("compressedCobblestone" + i + "x", FluidType.Stone, 0, TConstruct.ingotLiquidValue / 18 * (9 ^ i));
        }
        Smeltery.addDictionaryMelting("compressedSand1x", FluidType.Glass, 175, FluidContainerRegistry.BUCKET_VOLUME * 9);

        registerPatternMaterial("plankWood", 2, "Wood");
        registerPatternMaterial("stickWood", 1, "Wood");
        registerPatternMaterial("slabWood", 1, "Wood");
        registerPatternMaterial("compressedCobblestone1x", 18, "Stone");
    }

    protected static void addRecipesForToolBuilder ()
    {
        ToolBuilder tb = ToolBuilder.instance;
        tb.addNormalToolRecipe(TRepo.pickaxe, TRepo.pickaxeHead, TRepo.toolRod, TRepo.binding);
        tb.addNormalToolRecipe(TRepo.broadsword, TRepo.swordBlade, TRepo.toolRod, TRepo.wideGuard);
        tb.addNormalToolRecipe(TRepo.hatchet, TRepo.hatchetHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.shovel, TRepo.shovelHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.longsword, TRepo.swordBlade, TRepo.toolRod, TRepo.handGuard);
        tb.addNormalToolRecipe(TRepo.rapier, TRepo.swordBlade, TRepo.toolRod, TRepo.crossbar);
        tb.addNormalToolRecipe(TRepo.frypan, TRepo.frypanHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.battlesign, TRepo.signHead, TRepo.toolRod);
        tb.addNormalToolRecipe(TRepo.mattock, TRepo.hatchetHead, TRepo.toolRod, TRepo.shovelHead);
        tb.addNormalToolRecipe(TRepo.dagger, TRepo.knifeBlade, TRepo.toolRod, TRepo.crossbar);
        tb.addNormalToolRecipe(TRepo.cutlass, TRepo.swordBlade, TRepo.toolRod, TRepo.fullGuard);
        tb.addNormalToolRecipe(TRepo.chisel, TRepo.chiselHead, TRepo.toolRod);

        tb.addNormalToolRecipe(TRepo.scythe, TRepo.scytheBlade, TRepo.toughRod, TRepo.toughBinding, TRepo.toughRod);
        tb.addNormalToolRecipe(TRepo.lumberaxe, TRepo.broadAxeHead, TRepo.toughRod, TRepo.largePlate, TRepo.toughBinding);
        tb.addNormalToolRecipe(TRepo.cleaver, TRepo.largeSwordBlade, TRepo.toughRod, TRepo.largePlate, TRepo.toughRod);
        tb.addNormalToolRecipe(TRepo.excavator, TRepo.excavatorHead, TRepo.toughRod, TRepo.largePlate, TRepo.toughBinding);
        tb.addNormalToolRecipe(TRepo.hammer, TRepo.hammerHead, TRepo.toughRod, TRepo.largePlate, TRepo.largePlate);
        tb.addNormalToolRecipe(TRepo.battleaxe, TRepo.broadAxeHead, TRepo.toughRod, TRepo.broadAxeHead, TRepo.toughBinding);

        //tb.addNormalToolRecipe(TRepo.shortbow, TRepo.toolRod, TRepo.bowstring, TRepo.toolRod);
        BowRecipe recipe = new BowRecipe(TRepo.toolRod, TRepo.bowstring, TRepo.toolRod, TRepo.shortbow);
        tb.addCustomToolRecipe(recipe);
        tb.addNormalToolRecipe(TRepo.arrow, TRepo.arrowhead, TRepo.toolRod, TRepo.fletching);

        ItemStack diamond = new ItemStack(Item.diamond);
        tb.registerToolMod(new ModRepair());
        tb.registerToolMod(new ModDurability(new ItemStack[] { diamond }, 0, 500, 0f, 3, "Diamond", "\u00a7bDurability +500", "\u00a7b"));
        tb.registerToolMod(new ModDurability(new ItemStack[] { new ItemStack(Item.emerald) }, 1, 0, 0.5f, 2, "Emerald", "\u00a72Durability +50%", "\u00a72"));

        TRepo.modF = new ModFlux();
        tb.registerToolMod(TRepo.modF);

        ItemStack redstoneItem = new ItemStack(Item.redstone);
        ItemStack redstoneBlock = new ItemStack(Block.blockRedstone);
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem }, 2, 1));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem, redstoneItem }, 2, 2));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneBlock }, 2, 9));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneItem, redstoneBlock }, 2, 10));
        tb.registerToolMod(new ModRedstone(new ItemStack[] { redstoneBlock, redstoneBlock }, 2, 18));

        ItemStack lapisItem = new ItemStack(Item.dyePowder, 1, 4);
        ItemStack lapisBlock = new ItemStack(Block.blockLapis);
        TRepo.modL = new ModLapis(new ItemStack[] { lapisItem }, 10, 1);
        tb.registerToolMod(TRepo.modL);
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisItem, lapisItem }, 10, 2));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisBlock }, 10, 9));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisItem, lapisBlock }, 10, 10));
        tb.registerToolMod(new ModLapis(new ItemStack[] { lapisBlock, lapisBlock }, 10, 18));

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(TRepo.materials, 1, 6) }, 4, "Moss", 3, "\u00a72", "Auto-Repair"));
        ItemStack blazePowder = new ItemStack(Item.blazePowder);
        tb.registerToolMod(new ModBlaze(new ItemStack[] { blazePowder }, 7, 1));
        tb.registerToolMod(new ModBlaze(new ItemStack[] { blazePowder, blazePowder }, 7, 2));
        tb.registerToolMod(new ModAutoSmelt(new ItemStack[] { new ItemStack(TRepo.materials, 1, 7) }, 6, "Lava", "\u00a74", "Auto-Smelt"));
        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(TRepo.materials, 1, 8) }, 8, "Necrotic", 1, "\u00a78", "Life Steal"));

        ItemStack quartzItem = new ItemStack(Item.netherQuartz);
        ItemStack quartzBlock = new ItemStack(Block.blockNetherQuartz, 1, Short.MAX_VALUE);
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem }, 11, 1));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem, quartzItem }, 11, 2));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzBlock }, 11, 4));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzItem, quartzBlock }, 11, 5));
        tb.registerToolMod(new ModAttack("Quartz", new ItemStack[] { quartzBlock, quartzBlock }, 11, 8));

        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { diamond, new ItemStack(Block.blockGold) }, "Tier1Free"));
        tb.registerToolMod(new ModExtraModifier(new ItemStack[] { new ItemStack(Item.netherStar) }, "Tier2Free"));

        ItemStack silkyJewel = new ItemStack(TRepo.materials, 1, 26);
        tb.registerToolMod(new ModButtertouch(new ItemStack[] { silkyJewel }, 12));

        ItemStack piston = new ItemStack(Block.pistonBase);
        tb.registerToolMod(new ModPiston(new ItemStack[] { piston }, 3, 1));
        tb.registerToolMod(new ModPiston(new ItemStack[] { piston, piston }, 3, 2));

        tb.registerToolMod(new ModInteger(new ItemStack[] { new ItemStack(Block.obsidian), new ItemStack(Item.enderPearl) }, 13, "Beheading", 1, "\u00a7d", "Beheading"));

        ItemStack holySoil = new ItemStack(TRepo.craftedSoil, 1, 4);
        tb.registerToolMod(new ModSmite("Smite", new ItemStack[] { holySoil }, 14, 1));
        tb.registerToolMod(new ModSmite("Smite", new ItemStack[] { holySoil, holySoil }, 14, 2));

        ItemStack spidereyeball = new ItemStack(Item.fermentedSpiderEye);
        tb.registerToolMod(new ModAntiSpider("Anti-Spider", new ItemStack[] { spidereyeball }, 15, 1));
        tb.registerToolMod(new ModAntiSpider("Anti-Spider", new ItemStack[] { spidereyeball, spidereyeball }, 15, 2));

        ItemStack obsidianPlate = new ItemStack(TRepo.largePlate, 1, 6);
        tb.registerToolMod(new ModReinforced(new ItemStack[] { obsidianPlate }, 16, 1));

        TConstructRegistry.registerActiveToolMod(new TActiveOmniMod());
    }

    protected static void addPartMapping ()
    {
        /* Tools */
        TRepo.patternOutputs = new Item[] { TRepo.toolRod, TRepo.pickaxeHead, TRepo.shovelHead, TRepo.hatchetHead, TRepo.swordBlade, TRepo.wideGuard, TRepo.handGuard, TRepo.crossbar, TRepo.binding,
                TRepo.frypanHead, TRepo.signHead, TRepo.knifeBlade, TRepo.chiselHead, TRepo.toughRod, TRepo.toughBinding, TRepo.largePlate, TRepo.broadAxeHead, TRepo.scytheBlade, TRepo.excavatorHead,
                TRepo.largeSwordBlade, TRepo.hammerHead, TRepo.fullGuard, null, null, TRepo.arrowhead, null };

        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };

        if (PHConstruct.craftMetalTools)
        {
            for (int mat = 0; mat < 18; mat++)
            {
                for (int meta = 0; meta < TRepo.patternOutputs.length; meta++)
                {
                    if (TRepo.patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(TRepo.woodPattern.itemID, meta + 1, mat, new ItemStack(TRepo.patternOutputs[meta], 1, mat));
                }
            }
        }
        else
        {
            for (int mat = 0; mat < nonMetals.length; mat++)
            {
                for (int meta = 0; meta < TRepo.patternOutputs.length; meta++)
                {
                    if (TRepo.patternOutputs[meta] != null)
                        TConstructRegistry.addPartMapping(TRepo.woodPattern.itemID, meta + 1, nonMetals[mat], new ItemStack(TRepo.patternOutputs[meta], 1, nonMetals[mat]));
                }
            }
        }
    }

    protected static void addRecipesForTableCasting ()
    {
        /* Smeltery */
        ItemStack ingotcast = new ItemStack(TRepo.metalPattern, 1, 0);
        ItemStack gemcast = new ItemStack(TRepo.metalPattern, 1, 26);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        //Blank
        tableCasting.addCastingRecipe(new ItemStack(TRepo.blankPattern, 1, 1), new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), 80);
        tableCasting.addCastingRecipe(new ItemStack(TRepo.blankPattern, 1, 2), new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(Item.emerald), 80);
        tableCasting.addCastingRecipe(gemcast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(Item.emerald), 80);

        //Ingots
        tableCasting.addCastingRecipe(new ItemStack(TRepo.materials, 1, 2), new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 4), ingotcast, 80); //stone

        //Misc
        tableCasting.addCastingRecipe(new ItemStack(Item.emerald), new FluidStack(TRepo.moltenEmeraldFluid, 640), gemcast, 80);
        tableCasting.addCastingRecipe(new ItemStack(TRepo.materials, 1, 36), new FluidStack(TRepo.glueFluid, TConstruct.ingotLiquidValue), null, 50);
        tableCasting.addCastingRecipe(new ItemStack(TRepo.strangeFood, 1, 1), new FluidStack(TRepo.bloodFluid, 160), null, 50);

        //Buckets
        ItemStack bucket = new ItemStack(Item.bucketEmpty);

        for (int sc = 0; sc < 24; sc++)
        {
            tableCasting.addCastingRecipe(new ItemStack(TRepo.buckets, 1, sc), new FluidStack(TRepo.fluids[sc], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
        }
        tableCasting.addCastingRecipe(new ItemStack(TRepo.buckets, 1, 26), new FluidStack(TRepo.fluids[26], FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

        // Clear glass pane casting
        tableCasting.addCastingRecipe(new ItemStack(TRepo.glassPane), new FluidStack(TRepo.moltenGlassFluid, 250), null, 80);

        // Metal toolpart casting
        TRepo.liquids = new FluidStack[] { new FluidStack(TRepo.moltenIronFluid, 1), new FluidStack(TRepo.moltenCopperFluid, 1), new FluidStack(TRepo.moltenCobaltFluid, 1),
                new FluidStack(TRepo.moltenArditeFluid, 1), new FluidStack(TRepo.moltenManyullynFluid, 1), new FluidStack(TRepo.moltenBronzeFluid, 1), new FluidStack(TRepo.moltenAlumiteFluid, 1),
                new FluidStack(TRepo.moltenObsidianFluid, 1), new FluidStack(TRepo.moltenSteelFluid, 1), new FluidStack(TRepo.pigIronFluid, 1) };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16, 18 }; //ItemStack damage value
        int fluidAmount = 0;
        Fluid fs = null;

        for (int iter = 0; iter < TRepo.patternOutputs.length; iter++)
        {
            if (TRepo.patternOutputs[iter] != null)
            {
                ItemStack cast = new ItemStack(TRepo.metalPattern, 1, iter + 1);

                tableCasting.addCastingRecipe(cast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(TRepo.patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);
                tableCasting.addCastingRecipe(cast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(TRepo.patternOutputs[iter], 1, Short.MAX_VALUE), false, 50);

                for (int iterTwo = 0; iterTwo < TRepo.liquids.length; iterTwo++)
                {
                    fs = TRepo.liquids[iterTwo].getFluid();
                    fluidAmount = ((IPattern) TRepo.metalPattern).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                    ItemStack metalCast = new ItemStack(TRepo.patternOutputs[iter], 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                    Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }
            }
        }

        ItemStack[] ingotShapes = { new ItemStack(Item.brick), new ItemStack(Item.netherrackBrick), new ItemStack(TRepo.materials, 1, 2), new ItemStack(TRepo.materials, 1, 37) };
        for (int i = 0; i < ingotShapes.length; i++)
        {
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), ingotShapes[i], false, 50);
            tableCasting.addCastingRecipe(ingotcast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), ingotShapes[i], false, 50);
        }

        ItemStack fullguardCast = new ItemStack(TRepo.metalPattern, 1, 22);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(TRepo.fullGuard, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(fullguardCast, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(TRepo.fullGuard, 1, Short.MAX_VALUE), false, 50);

        // Golden Food Stuff
        FluidStack goldAmount = null;
        if (PHConstruct.goldAppleRecipe)
        {
            goldAmount = new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 8);
        }
        else
        {
            goldAmount = new FluidStack(TRepo.moltenGoldFluid, TConstruct.nuggetLiquidValue * 8);
        }
        tableCasting.addCastingRecipe(new ItemStack(Item.appleGold, 1), goldAmount, new ItemStack(Item.appleRed), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(Item.goldenCarrot, 1), goldAmount, new ItemStack(Item.carrot), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(Item.speckledMelon, 1), goldAmount, new ItemStack(Item.melon), true, 50);
        tableCasting.addCastingRecipe(new ItemStack(TRepo.goldHead), goldAmount, new ItemStack(Item.skull, 1, 3), true, 50);
    }

    protected static void addRecipesForFurnace ()
    {
        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 3, new ItemStack(TRepo.craftedSoil, 1, 4), 0.2f); //Concecrated Soil

        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 0, new ItemStack(TRepo.materials, 1, 1), 2f); //Slime
        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 1, new ItemStack(TRepo.materials, 1, 2), 2f); //Seared brick item
        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 2, new ItemStack(TRepo.materials, 1, 17), 2f); //Blue Slime
        FurnaceRecipes.smelting().addSmelting(TRepo.craftedSoil.blockID, 6, new ItemStack(TRepo.materials, 1, 37), 2f); //Nether seared brick

        //FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 1, new ItemStack(TRepo.materials, 1, 3), 3f);
        //FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 2, new ItemStack(TRepo.materials, 1, 4), 3f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 3, new ItemStack(TRepo.materials, 1, 9), 0.5f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 4, new ItemStack(TRepo.materials, 1, 10), 0.5f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreSlag.blockID, 5, new ItemStack(TRepo.materials, 1, 12), 0.5f);

        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 0, new ItemStack(TRepo.materials, 1, 19), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 1, new ItemStack(Item.goldNugget), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 2, new ItemStack(TRepo.materials, 1, 20), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 3, new ItemStack(TRepo.materials, 1, 21), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 4, new ItemStack(TRepo.materials, 1, 22), 0.2f);
        //FurnaceRecipes.smelting().addSmelting(TRepo.oreBerries.itemID, 5, new ItemStack(TRepo.materials, 1, 23), 0.2f);

        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 0, new ItemStack(Item.ingotIron), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 1, new ItemStack(Item.ingotGold), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 2, new ItemStack(TRepo.materials, 1, 9), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 3, new ItemStack(TRepo.materials, 1, 10), 0.2f);
        FurnaceRecipes.smelting().addSmelting(TRepo.oreGravel.blockID, 4, new ItemStack(TRepo.materials, 1, 12), 0.2f);

        FurnaceRecipes.smelting().addSmelting(TRepo.speedBlock.blockID, 0, new ItemStack(TRepo.speedBlock, 1, 2), 0.2f);
    }

    protected static void addRecipesForCraftingTable ()
    {
        String[] patBlock = { "###", "###", "###" };
        String[] patSurround = { "###", "#m#", "###" };

        Object[] toolForgeBlocks = { "blockIron", "blockGold", Block.blockDiamond, Block.blockEmerald, "blockCobalt", "blockArdite", "blockManyullyn", "blockCopper", "blockBronze", "blockTin",
                "blockAluminum", "blockAluminumBrass", "blockAlumite", "blockSteel" };

        // ToolForge Recipes (Metal Version)
        for (int sc = 0; sc < toolForgeBlocks.length; sc++)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolForge, 1, sc), "bbb", "msm", "m m", 'b', new ItemStack(TRepo.smeltery, 1, 2), 's', new ItemStack(TRepo.toolStationWood,
                    1, 0), 'm', toolForgeBlocks[sc]));
            // adding slab version recipe
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 5), "bbb", "msm", "m m", 'b', new ItemStack(TRepo.smeltery, 1, 2), 's', new ItemStack(
                    TRepo.craftingSlabWood, 1, 1), 'm', toolForgeBlocks[sc]));
        }

        // ToolStation Recipes (Wooden Version)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(TRepo.craftingStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 0), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(TRepo.craftingSlabWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 2), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 3), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 4), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 5), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', Block.chest);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 1), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "logWood"));
        if (PHConstruct.stencilTableCrafting)
        {
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 0));
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 11), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 1));
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 12), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 2));
            GameRegistry.addRecipe(new ItemStack(TRepo.toolStationWood, 1, 13), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', new ItemStack(Block.planks, 1, 3));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.toolStationWood, 1, 10), "p", "w", 'p', new ItemStack(TRepo.blankPattern, 1, 0), 'w', "plankWood"));
        }
        GameRegistry.addRecipe(new ItemStack(TRepo.furnaceSlab, 1, 0), "###", "# #", "###", '#', new ItemStack(Block.stoneSingleSlab, 1, 3));

        // Blank Pattern Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.blankPattern, 1, 0), "ps", "sp", 'p', "plankWood", 's', "stickWood"));
        // Manual Book Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.manualBook), "wp", 'w', new ItemStack(TRepo.blankPattern, 1, 0), 'p', Item.paper);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 2, 0), new ItemStack(TRepo.manualBook, 1, 0), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 1, 1), new ItemStack(TRepo.manualBook, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 2, 1), new ItemStack(TRepo.manualBook, 1, 1), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 1, 2), new ItemStack(TRepo.manualBook, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 2, 2), new ItemStack(TRepo.manualBook, 1, 2), Item.book);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.manualBook, 1, 3), new ItemStack(TRepo.manualBook, 1, 2));
        // alternative Vanilla Book Recipe
        GameRegistry.addShapelessRecipe(new ItemStack(Item.book), Item.paper, Item.paper, Item.paper, Item.silk, TRepo.blankPattern, TRepo.blankPattern);
        // Paperstack Recipe
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 0), "pp", "pp", 'p', Item.paper);
        // Mossball Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.materials, 1, 6), patBlock, '#', "stoneMossy"));
        // LavaCrystal Recipes -Auto-smelt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'c', Item.fireballCharge, 'x', Item.blazeRod);
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 7), "xcx", "cbc", "xcx", 'b', Item.bucketLava, 'x', Item.fireballCharge, 'c', Item.blazeRod);
        // Slimy sand Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 1, 0), Item.slimeBall, Item.slimeBall, Item.slimeBall, Item.slimeBall, Block.sand, Block.dirt);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 1, 2), TRepo.strangeFood, TRepo.strangeFood, TRepo.strangeFood, TRepo.strangeFood, Block.sand, Block.dirt);
        // Grout Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 2, 1), Item.clay, Block.sand, Block.gravel);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 8, 1), new ItemStack(Block.blockClay, 1, Short.MAX_VALUE), Block.sand, Block.sand, Block.sand, Block.sand, Block.gravel,
                Block.gravel, Block.gravel, Block.gravel);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 2, 6), Item.netherStalkSeeds, Block.slowSand, Block.gravel);
        // Graveyard Soil Recipes
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 1, 3), Block.dirt, Item.rottenFlesh, new ItemStack(Item.dyePowder, 1, 15));
        // Silky Cloth Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 25), patSurround, 'm', new ItemStack(TRepo.materials, 1, 24), '#', new ItemStack(Item.silk));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.materials, 1, 25), patSurround, 'm', "nuggetGold", '#', new ItemStack(Item.silk)));
        // Silky Jewel Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 26), " c ", "cec", " c ", 'c', new ItemStack(TRepo.materials, 1, 25), 'e', new ItemStack(Item.emerald));
        // Wooden Armor Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.helmetWood, new Object[] { "www", "w w", 'w', "logWood" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.chestplateWood, new Object[] { "w w", "www", "www", 'w', "logWood" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.leggingsWood, new Object[] { "www", "w w", "w w", 'w', "logWood" }));
        GameRegistry.addRecipe(new ShapedOreRecipe(TRepo.bootsWood, new Object[] { "w w", "w w", 'w', "logWood" }));
        // Metal conversion Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 3), patBlock, '#', new ItemStack(TRepo.materials, 1, 9)); // Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 5), patBlock, '#', new ItemStack(TRepo.materials, 1, 10)); // Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 6), patBlock, '#', new ItemStack(TRepo.materials, 1, 11)); // Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 6), patBlock, '#', new ItemStack(TRepo.materials, 1, 12)); // Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 4), patBlock, '#', new ItemStack(TRepo.materials, 1, 13)); // Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 7), patBlock, '#', new ItemStack(TRepo.materials, 1, 14)); // AluBrass
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 0), patBlock, '#', new ItemStack(TRepo.materials, 1, 3)); // Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 1), patBlock, '#', new ItemStack(TRepo.materials, 1, 4)); // Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 2), patBlock, '#', new ItemStack(TRepo.materials, 1, 5)); // Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 8), patBlock, '#', new ItemStack(TRepo.materials, 1, 15)); // Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 9), patBlock, '#', new ItemStack(TRepo.materials, 1, 16)); // Steel
        GameRegistry.addRecipe(new ItemStack(TRepo.metalBlock, 1, 11), patBlock, '#', new ItemStack(TRepo.materials, 1, 12)); //Aluminum raw -> ingot

        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 9), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 3)); //Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 10), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 5)); //Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 12), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 6)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 13), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 4)); //Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 14), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 7)); //AluBrass
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 3), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 0)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 4), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 1)); //Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 5), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 2)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 15), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 8)); //Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 16), "m", 'm', new ItemStack(TRepo.metalBlock, 1, 9)); //Steel

        GameRegistry.addRecipe(new ItemStack(Item.ingotIron), patBlock, '#', new ItemStack(TRepo.materials, 1, 19)); //Iron
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 9), patBlock, '#', new ItemStack(TRepo.materials, 1, 20)); //Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 10), patBlock, '#', new ItemStack(TRepo.materials, 1, 21)); //Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 11), patBlock, '#', new ItemStack(TRepo.materials, 1, 22)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 14), patBlock, '#', new ItemStack(TRepo.materials, 1, 24)); //Aluminum Brass
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 18), patBlock, '#', new ItemStack(TRepo.materials, 1, 27)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 3), patBlock, '#', new ItemStack(TRepo.materials, 1, 28)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 4), patBlock, '#', new ItemStack(TRepo.materials, 1, 29)); //Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 5), patBlock, '#', new ItemStack(TRepo.materials, 1, 30)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 13), patBlock, '#', new ItemStack(TRepo.materials, 1, 31)); //Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 15), patBlock, '#', new ItemStack(TRepo.materials, 1, 32)); //Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 1, 16), patBlock, '#', new ItemStack(TRepo.materials, 1, 33)); //Steel    

        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 19), "m", 'm', new ItemStack(Item.ingotIron)); //Iron
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 20), "m", 'm', new ItemStack(TRepo.materials, 1, 9)); //Copper
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 21), "m", 'm', new ItemStack(TRepo.materials, 1, 10)); //Tin
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 22), "m", 'm', new ItemStack(TRepo.materials, 1, 11)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 22), "m", 'm', new ItemStack(TRepo.materials, 1, 12)); //Aluminum
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 24), "m", 'm', new ItemStack(TRepo.materials, 1, 14)); //Aluminum Brass
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 27), "m", 'm', new ItemStack(TRepo.materials, 1, 18)); //Obsidian
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 28), "m", 'm', new ItemStack(TRepo.materials, 1, 3)); //Cobalt
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 29), "m", 'm', new ItemStack(TRepo.materials, 1, 4)); //Ardite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 30), "m", 'm', new ItemStack(TRepo.materials, 1, 5)); //Manyullyn
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 31), "m", 'm', new ItemStack(TRepo.materials, 1, 13)); //Bronze
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 32), "m", 'm', new ItemStack(TRepo.materials, 1, 15)); //Alumite
        GameRegistry.addRecipe(new ItemStack(TRepo.materials, 9, 33), "m", 'm', new ItemStack(TRepo.materials, 1, 16)); //Steel 

        // stained Glass Recipes
        String[] dyeTypes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue",
                "dyeMagenta", "dyeOrange", "dyeWhite" };
        String color = "";
        for (int i = 0; i < 16; i++)
        {
            color = dyeTypes[15 - i];
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.cloth, 8, i), patSurround, 'm', color, '#', new ItemStack(Block.cloth, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClear, 8, i), patSurround, 'm', color, '#', TRepo.clearGlass));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClear, 1, i), color, TRepo.clearGlass));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClear, 8, i), patSurround, 'm', color, '#', new ItemStack(TRepo.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClear, 1, i), color, new ItemStack(TRepo.stainedGlassClear, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', TRepo.glassPane));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 1, i), color, TRepo.glassPane));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 8, i), patSurround, 'm', color, '#', new ItemStack(TRepo.stainedGlassClearPane, 1, Short.MAX_VALUE)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.stainedGlassClearPane, 1, i), color, new ItemStack(TRepo.stainedGlassClearPane, 1, Short.MAX_VALUE)));
        }

        // Glass Recipes
        GameRegistry.addRecipe(new ItemStack(Item.glassBottle, 3), new Object[] { "# #", " # ", '#', TRepo.clearGlass });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.daylightSensor), new Object[] { "GGG", "QQQ", "WWW", 'G', "glass", 'Q', Item.netherQuartz, 'W', "slabWood" }));
        GameRegistry.addRecipe(new ItemStack(Block.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', TRepo.clearGlass, 'S', Item.netherStar, 'O', Block.obsidian });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.glassPane, 16, 0), "GGG", "GGG", 'G', TRepo.clearGlass));

        // Smeltery Components Recipes
        ItemStack searedBrick = new ItemStack(TRepo.materials, 1, 2);
        GameRegistry.addRecipe(new ItemStack(TRepo.smeltery, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(TRepo.smeltery, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(TRepo.smeltery, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTank, 1, 0), patSurround, '#', searedBrick, 'm', "glass")); //Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTank, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "glass")); //Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTank, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "glass")); //Window
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlock, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlock, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlock, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin
        GameRegistry.addRecipe(new ItemStack(TRepo.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); //Channel

        searedBrick = new ItemStack(TRepo.materials, 1, 37);
        GameRegistry.addRecipe(new ItemStack(TRepo.smelteryNether, 1, 0), "bbb", "b b", "bbb", 'b', searedBrick); //Controller
        GameRegistry.addRecipe(new ItemStack(TRepo.smelteryNether, 1, 1), "b b", "b b", "b b", 'b', searedBrick); //Drain
        GameRegistry.addRecipe(new ItemStack(TRepo.smelteryNether, 1, 2), "bb", "bb", 'b', searedBrick); //Bricks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTankNether, 1, 0), patSurround, '#', searedBrick, 'm', "glass")); //Tank
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTankNether, 1, 1), "bgb", "ggg", "bgb", 'b', searedBrick, 'g', "glass")); //Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.lavaTankNether, 1, 2), "bgb", "bgb", "bgb", 'b', searedBrick, 'g', "glass")); //Window
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlockNether, 1, 0), "bbb", "b b", "b b", 'b', searedBrick); //Table
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlockNether, 1, 1), "b b", " b ", 'b', searedBrick); //Faucet
        GameRegistry.addRecipe(new ItemStack(TRepo.searedBlockNether, 1, 2), "b b", "b b", "bbb", 'b', searedBrick); //Basin
        GameRegistry.addRecipe(new ItemStack(TRepo.castingChannel, 4, 0), "b b", "bbb", 'b', searedBrick); //Channel

        // Jack o'Latern Recipe - Stone Torch
        GameRegistry.addRecipe(new ItemStack(Block.pumpkinLantern, 1, 0), "p", "s", 'p', new ItemStack(Block.pumpkin), 's', new ItemStack(TRepo.stoneTorch));
        // Stone Torch Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stoneTorch, 4), "p", "w", 'p', new ItemStack(Item.coal, 1, Short.MAX_VALUE), 'w', "stoneRod"));
        // Stone Ladder Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.stoneLadder, 3), "w w", "www", "w w", 'w', "stoneRod"));
        // Wooden Rail Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.woodenRail, 4, 0), "b b", "bxb", "b b", 'b', "plankWood", 'x', "stickWood"));
        // Stonesticks Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.toolRod, 4, 1), "c", "c", 'c', new ItemStack(Block.stone));
        GameRegistry.addRecipe(new ItemStack(TRepo.toolRod, 2, 1), "c", "c", 'c', new ItemStack(Block.cobblestone));
        // 
        ItemStack aluBrass = new ItemStack(TRepo.materials, 1, 14);
        // Clock Recipe - Vanilla alternativ
        GameRegistry.addRecipe(new ItemStack(Item.pocketSundial), " i ", "iri", " i ", 'i', aluBrass, 'r', new ItemStack(Item.redstone));
        // Gold Pressure Plate -  Vanilla alternativ
        GameRegistry.addRecipe(new ItemStack(Block.pressurePlateGold), "ii", 'i', aluBrass);
        //Accessories
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.heartCanister, 1, 0), "##", "##", '#', "ingotAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.heartCanister, 1, 0), "##", "##", '#', "ingotAluminium"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.heartCanister, 1, 0), "##", "##", '#', "ingotNaturalAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.heartCanister, 1, 0), " # ", "#B#", " # ", '#', "ingotTin", 'B', Item.bone));

        GameRegistry.addRecipe(new ItemStack(TRepo.diamondApple), " d ", "d#d", " d ", 'd', new ItemStack(Item.diamond), '#', new ItemStack(Item.appleRed));
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.heartCanister, 1, 2), new ItemStack(TRepo.diamondApple), new ItemStack(TRepo.materials, 1, 8), new ItemStack(TRepo.heartCanister, 1, 0),
                new ItemStack(TRepo.heartCanister, 1, 1));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(TRepo.toughRod, 1, 2), 'm',
                "ingotGold"));
        GameRegistry.addRecipe(new ItemStack(TRepo.knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Item.leather), 'r', new ItemStack(TRepo.toughRod, 1, 2), 'm', aluBrass);
        // Drying Rack Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.dryingRack, 1, 0), "bbb", 'b', "slabWood"));
        //Landmine Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 0), "mcm", "rpr", 'm', "plankWood", 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 1), "mcm", "rpr", 'm', Block.stone, 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 2), "mcm", "rpr", 'm', Block.obsidian, 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone, 'p',
                Block.pressurePlateStone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.landmine, 1, 3), "mcm", "rpr", 'm', Item.redstoneRepeater, 'c', new ItemStack(TRepo.blankPattern, 1, 1), 'r', Item.redstone,
                'p', Block.pressurePlateStone));

        //Ultra hardcore recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.goldHead), patSurround, '#', new ItemStack(Item.ingotGold), 'm', new ItemStack(Item.skull, 1, 3));

        // Slab Smeltery Components Recipes
        for (int i = 0; i < 7; i++)
        {
            GameRegistry.addRecipe(new ItemStack(TRepo.speedSlab, 6, i), "bbb", 'b', new ItemStack(TRepo.speedBlock, 1, i));
        }
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 0), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 1), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 2), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 3), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 6));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 4), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 8));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 5), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 9));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 6), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TRepo.searedSlab, 6, 7), "bbb", 'b', new ItemStack(TRepo.smeltery, 1, 11));

        // Wool Slab Recipes
        for (int sc = 0; sc <= 7; sc++)
        {
            GameRegistry.addRecipe(new ItemStack(TRepo.woolSlab1, 6, sc), "www", 'w', new ItemStack(Block.cloth, 1, sc));
            GameRegistry.addRecipe(new ItemStack(TRepo.woolSlab2, 6, sc), "www", 'w', new ItemStack(Block.cloth, 1, sc + 8));

            GameRegistry.addShapelessRecipe(new ItemStack(Block.cloth, 1, sc), new ItemStack(TRepo.woolSlab1, 1, sc), new ItemStack(TRepo.woolSlab1, 1, sc));
            GameRegistry.addShapelessRecipe(new ItemStack(Block.cloth, 1, sc + 8), new ItemStack(TRepo.woolSlab2, 1, sc), new ItemStack(TRepo.woolSlab2, 1, sc));
        }
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.cloth, 1, 0), "slabCloth", "slabCloth"));
        //Trap Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.punji, 5, 0), "b b", " b ", "b b", 'b', new ItemStack(Item.reed));
        GameRegistry.addRecipe(new ItemStack(TRepo.barricadeSpruce, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TRepo.barricadeBirch, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.barricadeJungle, 1, 0), "b", "b", 'b', new ItemStack(Block.wood, 1, 3));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.barricadeOak, 1, 0), "b", "b", 'b', "logWood"));
        // Advanced WorkBench Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingStationWood, 1, 0), "b", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingStationWood, 1, 0), "b", 'b', "craftingTableWood"));
        //Slab crafters
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingSlabWood, 6, 0), "bbb", 'b', "crafterWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TRepo.craftingSlabWood, 6, 0), "bbb", 'b', "craftingTableWood"));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 0), "b", 'b', new ItemStack(TRepo.craftingStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 1), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 1));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 2));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 3));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 2), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 4));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 4), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 5));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 10));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 11));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 12));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 3), "b", 'b', new ItemStack(TRepo.toolStationWood, 1, 13));
        GameRegistry.addRecipe(new ItemStack(TRepo.craftingSlabWood, 1, 5), "b", 'b', new ItemStack(TRepo.toolForge, 1, Short.MAX_VALUE));
        // EssenceExtractor Recipe
        //Slime Recipes
        GameRegistry.addRecipe(new ItemStack(TRepo.slimeGel, 1, 0), "##", "##", '#', TRepo.strangeFood);
        GameRegistry.addRecipe(new ItemStack(TRepo.strangeFood, 4, 0), "#", '#', new ItemStack(TRepo.slimeGel, 1, 0));
        GameRegistry.addRecipe(new ItemStack(TRepo.slimeGel, 1, 1), "##", "##", '#', Item.slimeBall);
        GameRegistry.addRecipe(new ItemStack(Item.slimeBall, 4, 0), "#", '#', new ItemStack(TRepo.slimeGel, 1, 1));
        //slimeExplosive
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.slimeExplosive, 1, 0), Item.slimeBall, Block.tnt);
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.slimeExplosive, 1, 2), TRepo.strangeFood, Block.tnt);
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.slimeExplosive, 1, 0), "slimeball", Block.tnt));

        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.slimeChannel, 1, 0), new ItemStack(TRepo.slimeGel, 1, Short.MAX_VALUE), new ItemStack(Item.redstone));
        GameRegistry.addShapelessRecipe(new ItemStack(TRepo.bloodChannel, 1, 0), new ItemStack(TRepo.strangeFood, 1, 1), new ItemStack(TRepo.strangeFood, 1, 1),
                new ItemStack(TRepo.strangeFood, 1, 1), new ItemStack(TRepo.strangeFood, 1, 1), new ItemStack(Item.redstone));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.slimeChannel, 1, 0), "slimeball", "slimeball", "slimeball", "slimeball", new ItemStack(Item.redstone)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TRepo.slimePad, 1, 0), TRepo.slimeChannel, "slimeball"));
    }

    protected static void addRecipesForDryingRack ()
    {
        //Drying rack
        DryingRackRecipes.addDryingRecipe(Item.beefRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 0));
        DryingRackRecipes.addDryingRecipe(Item.chickenRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 1));
        DryingRackRecipes.addDryingRecipe(Item.porkRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 2));
        //DryingRackRecipes.addDryingRecipe(Item.muttonRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 3));
        DryingRackRecipes.addDryingRecipe(Item.fishRaw, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 4));
        DryingRackRecipes.addDryingRecipe(Item.rottenFlesh, 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 5));
        DryingRackRecipes.addDryingRecipe(new ItemStack(TRepo.strangeFood, 1, 0), 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 6));
        DryingRackRecipes.addDryingRecipe(new ItemStack(TRepo.strangeFood, 1, 1), 20 * 60 * 5, new ItemStack(TRepo.jerky, 1, 7));

        //DryingRackRecipes.addDryingRecipe(new ItemStack(TRepo.jerky, 1, 5), 20 * 60 * 10, Item.leather);
    }

    protected static void addRecipesForChisel ()
    {
        /* Detailing */
        Detailing chiseling = TConstructRegistry.getChiselDetailing();
        chiseling.addDetailing(Block.stone, 0, Block.stoneBrick, 0, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 0, TRepo.speedBlock, 1, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 2, TRepo.speedBlock, 3, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 3, TRepo.speedBlock, 4, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 4, TRepo.speedBlock, 5, TRepo.chisel);
        chiseling.addDetailing(TRepo.speedBlock, 5, TRepo.speedBlock, 6, TRepo.chisel);

        chiseling.addDetailing(Block.obsidian, 0, TRepo.multiBrick, 0, TRepo.chisel);
        chiseling.addDetailing(Block.sandStone, 0, Block.sandStone, 2, TRepo.chisel);
        chiseling.addDetailing(Block.sandStone, 2, Block.sandStone, 1, TRepo.chisel);
        chiseling.addDetailing(Block.sandStone, 1, TRepo.multiBrick, 1, TRepo.chisel);
        //chiseling.addDetailing(Block.netherrack, 0, TRepo.multiBrick, 2, TRepo.chisel);
        //chiseling.addDetailing(Block.stone_refined, 0, TRepo.multiBrick, 3, TRepo.chisel);
        chiseling.addDetailing(Item.ingotIron, 0, TRepo.multiBrick, 4, TRepo.chisel);
        chiseling.addDetailing(Item.ingotGold, 0, TRepo.multiBrick, 5, TRepo.chisel);
        chiseling.addDetailing(Item.dyePowder, 4, TRepo.multiBrick, 6, TRepo.chisel);
        chiseling.addDetailing(Item.diamond, 0, TRepo.multiBrick, 7, TRepo.chisel);
        chiseling.addDetailing(Item.redstone, 0, TRepo.multiBrick, 8, TRepo.chisel);
        chiseling.addDetailing(Item.bone, 0, TRepo.multiBrick, 9, TRepo.chisel);
        chiseling.addDetailing(Item.slimeBall, 0, TRepo.multiBrick, 10, TRepo.chisel);
        chiseling.addDetailing(TRepo.strangeFood, 0, TRepo.multiBrick, 11, TRepo.chisel);
        chiseling.addDetailing(Block.whiteStone, 0, TRepo.multiBrick, 12, TRepo.chisel);
        chiseling.addDetailing(TRepo.materials, 18, TRepo.multiBrick, 13, TRepo.chisel);

        // adding multiBrick / multiBrickFanxy meta 0-13 to list
        for (int sc = 0; sc < 14; sc++)
        {
            chiseling.addDetailing(TRepo.multiBrick, sc, TRepo.multiBrickFancy, sc, TRepo.chisel);
        }

        chiseling.addDetailing(Block.stoneBrick, 0, TRepo.multiBrickFancy, 15, TRepo.chisel);
        chiseling.addDetailing(TRepo.multiBrickFancy, 15, TRepo.multiBrickFancy, 14, TRepo.chisel);
        chiseling.addDetailing(TRepo.multiBrickFancy, 14, Block.stoneBrick, 3, TRepo.chisel);
        /*chiseling.addDetailing(TRepo.multiBrick, 14, TRepo.multiBrickFancy, 14, TRepo.chisel);
        chiseling.addDetailing(TRepo.multiBrick, 15, TRepo.multiBrickFancy, 15, TRepo.chisel);*/

        chiseling.addDetailing(TRepo.smeltery, 4, TRepo.smeltery, 6, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 6, TRepo.smeltery, 11, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 11, TRepo.smeltery, 2, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 2, TRepo.smeltery, 8, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 8, TRepo.smeltery, 9, TRepo.chisel);
        chiseling.addDetailing(TRepo.smeltery, 9, TRepo.smeltery, 10, TRepo.chisel);
    }

    public static void oreRegistry ()
    {
        OreDictionary.registerOre("oreCobalt", new ItemStack(TRepo.oreSlag, 1, 1));
        OreDictionary.registerOre("oreArdite", new ItemStack(TRepo.oreSlag, 1, 2));
        OreDictionary.registerOre("oreCopper", new ItemStack(TRepo.oreSlag, 1, 3));
        OreDictionary.registerOre("oreTin", new ItemStack(TRepo.oreSlag, 1, 4));
        OreDictionary.registerOre("oreAluminum", new ItemStack(TRepo.oreSlag, 1, 5));
        OreDictionary.registerOre("oreAluminium", new ItemStack(TRepo.oreSlag, 1, 5));

        OreDictionary.registerOre("oreIron", new ItemStack(TRepo.oreGravel, 1, 0));
        OreDictionary.registerOre("oreGold", new ItemStack(TRepo.oreGravel, 1, 1));
        OreDictionary.registerOre("oreCobalt", new ItemStack(TRepo.oreGravel, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(TRepo.oreGravel, 1, 2));
        OreDictionary.registerOre("oreTin", new ItemStack(TRepo.oreGravel, 1, 3));
        OreDictionary.registerOre("oreAluminum", new ItemStack(TRepo.oreGravel, 1, 4));
        OreDictionary.registerOre("oreAluminium", new ItemStack(TRepo.oreGravel, 1, 4));

        OreDictionary.registerOre("ingotCobalt", new ItemStack(TRepo.materials, 1, 3));
        OreDictionary.registerOre("ingotArdite", new ItemStack(TRepo.materials, 1, 4));
        OreDictionary.registerOre("ingotManyullyn", new ItemStack(TRepo.materials, 1, 5));
        OreDictionary.registerOre("ingotCopper", new ItemStack(TRepo.materials, 1, 9));
        OreDictionary.registerOre("ingotTin", new ItemStack(TRepo.materials, 1, 10));
        OreDictionary.registerOre("ingotAluminum", new ItemStack(TRepo.materials, 1, 11));
        OreDictionary.registerOre("ingotAluminium", new ItemStack(TRepo.materials, 1, 11));
        OreDictionary.registerOre("ingotBronze", new ItemStack(TRepo.materials, 1, 13));
        OreDictionary.registerOre("ingotAluminumBrass", new ItemStack(TRepo.materials, 1, 14));
        OreDictionary.registerOre("ingotAluminiumBrass", new ItemStack(TRepo.materials, 1, 14));
        OreDictionary.registerOre("ingotAlumite", new ItemStack(TRepo.materials, 1, 15));
        OreDictionary.registerOre("ingotSteel", new ItemStack(TRepo.materials, 1, 16));
        ensureOreIsRegistered("ingotIron", new ItemStack(Item.ingotIron));
        ensureOreIsRegistered("ingotGold", new ItemStack(Item.ingotGold));
        OreDictionary.registerOre("ingotObsidian", new ItemStack(TRepo.materials, 1, 18));
        OreDictionary.registerOre("ingotPigIron", new ItemStack(TRepo.materials, 1, 34));
        OreDictionary.registerOre("itemRawRubber", new ItemStack(TRepo.materials, 1, 36));

        OreDictionary.registerOre("blockCobalt", new ItemStack(TRepo.metalBlock, 1, 0));
        OreDictionary.registerOre("blockArdite", new ItemStack(TRepo.metalBlock, 1, 1));
        OreDictionary.registerOre("blockManyullyn", new ItemStack(TRepo.metalBlock, 1, 2));
        OreDictionary.registerOre("blockCopper", new ItemStack(TRepo.metalBlock, 1, 3));
        OreDictionary.registerOre("blockBronze", new ItemStack(TRepo.metalBlock, 1, 4));
        OreDictionary.registerOre("blockTin", new ItemStack(TRepo.metalBlock, 1, 5));
        OreDictionary.registerOre("blockAluminum", new ItemStack(TRepo.metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminium", new ItemStack(TRepo.metalBlock, 1, 6));
        OreDictionary.registerOre("blockAluminumBrass", new ItemStack(TRepo.metalBlock, 1, 7));
        OreDictionary.registerOre("blockAluminiumBrass", new ItemStack(TRepo.metalBlock, 1, 7));
        OreDictionary.registerOre("blockAlumite", new ItemStack(TRepo.metalBlock, 1, 8));
        OreDictionary.registerOre("blockSteel", new ItemStack(TRepo.metalBlock, 1, 9));
        ensureOreIsRegistered("blockIron", new ItemStack(Block.blockIron));
        ensureOreIsRegistered("blockGold", new ItemStack(Block.blockGold));

        OreDictionary.registerOre("nuggetIron", new ItemStack(TRepo.materials, 1, 19));
        OreDictionary.registerOre("nuggetIron", new ItemStack(TRepo.oreBerries, 1, 0));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(TRepo.materials, 1, 20));
        OreDictionary.registerOre("nuggetCopper", new ItemStack(TRepo.oreBerries, 1, 2));
        OreDictionary.registerOre("nuggetTin", new ItemStack(TRepo.materials, 1, 21));
        OreDictionary.registerOre("nuggetTin", new ItemStack(TRepo.oreBerries, 1, 3));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(TRepo.materials, 1, 22));
        OreDictionary.registerOre("nuggetAluminum", new ItemStack(TRepo.oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(TRepo.materials, 1, 22));
        OreDictionary.registerOre("nuggetAluminium", new ItemStack(TRepo.oreBerries, 1, 4));
        OreDictionary.registerOre("nuggetAluminumBrass", new ItemStack(TRepo.materials, 1, 24));
        OreDictionary.registerOre("nuggetAluminiumBrass", new ItemStack(TRepo.materials, 1, 24));
        OreDictionary.registerOre("nuggetObsidian", new ItemStack(TRepo.materials, 1, 27));
        OreDictionary.registerOre("nuggetCobalt", new ItemStack(TRepo.materials, 1, 28));
        OreDictionary.registerOre("nuggetArdite", new ItemStack(TRepo.materials, 1, 29));
        OreDictionary.registerOre("nuggetManyullyn", new ItemStack(TRepo.materials, 1, 30));
        OreDictionary.registerOre("nuggetBronze", new ItemStack(TRepo.materials, 1, 31));
        OreDictionary.registerOre("nuggetAlumite", new ItemStack(TRepo.materials, 1, 32));
        OreDictionary.registerOre("nuggetSteel", new ItemStack(TRepo.materials, 1, 33));
        OreDictionary.registerOre("nuggetGold", new ItemStack(TRepo.oreBerries, 1, 1));
        ensureOreIsRegistered("nuggetGold", new ItemStack(Item.goldNugget));
        OreDictionary.registerOre("nuggetPigIron", new ItemStack(TRepo.materials, 1, 35));

        OreDictionary.registerOre("slabCloth", new ItemStack(TRepo.woolSlab1, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("slabCloth", new ItemStack(TRepo.woolSlab2, 1, Short.MAX_VALUE));

        ensureOreIsRegistered("stoneMossy", new ItemStack(Block.stoneBrick, 1, 1));
        ensureOreIsRegistered("stoneMossy", new ItemStack(Block.cobblestoneMossy));

        OreDictionary.registerOre("crafterWood", new ItemStack(Block.workbench, 1));
        OreDictionary.registerOre("craftingTableWood", new ItemStack(Block.workbench, 1));

        OreDictionary.registerOre("torchStone", new ItemStack(TRepo.stoneTorch));

        String[] matNames = { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel",
                "blueslime" };
        for (int i = 0; i < matNames.length; i++)
            OreDictionary.registerOre(matNames[i] + "Rod", new ItemStack(TRepo.toolRod, 1, i));
        OreDictionary.registerOre("thaumiumRod", new ItemStack(TRepo.toolRod, 1, 31));

        String[] glassTypes = { "glassBlack", "glassRed", "glassGreen", "glassBrown", "glassBlue", "glassPurple", "glassCyan", "glassLightGray", "glassGray", "glassPink", "glassLime", "glassYellow",
                "glassLightBlue", "glassMagenta", "glassOrange", "glassWhite" };
        for (int i = 0; i < 16; i++)
        {
            OreDictionary.registerOre(glassTypes[15 - i], new ItemStack(TRepo.stainedGlassClear, 1, i));
        }

        BlockDispenser.dispenseBehaviorRegistry.putObject(TRepo.titleIcon, new TDispenserBehaviorSpawnEgg());

        BlockDispenser.dispenseBehaviorRegistry.putObject(TRepo.arrow, new TDispenserBehaviorArrow());

        //Vanilla stuff
        OreDictionary.registerOre("slimeball", new ItemStack(Item.slimeBall));
        OreDictionary.registerOre("slimeball", new ItemStack(TRepo.strangeFood, 1, 0));
        OreDictionary.registerOre("slimeball", new ItemStack(TRepo.strangeFood, 1, 1));
        OreDictionary.registerOre("slimeball", new ItemStack(TRepo.materials, 1, 36));
        OreDictionary.registerOre("glass", new ItemStack(TRepo.clearGlass));
        OreDictionary.registerOre("glass", new ItemStack(Block.glass));
        RecipeRemover.removeShapedRecipe(new ItemStack(Block.pistonStickyBase));
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.magmaCream));
        RecipeRemover.removeShapedRecipe(new ItemStack(Item.leash));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.pistonStickyBase), "slimeball", Block.pistonBase));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.magmaCream), "slimeball", Item.blazePowder));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.leash, 2), "ss ", "sS ", "  s", 's', Item.silk, 'S', "slimeball"));
    }

    private static void ensureOreIsRegistered (String oreName, ItemStack is)
    {
        int oreId = OreDictionary.getOreID(is);
        if (oreId == -1)
        {
            OreDictionary.registerOre(oreName, is);
        }
    }

    public static void addShapedRecipeFirst (List recipeList, ItemStack itemstack, Object... objArray)
    {
        String var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;

        if (objArray[var4] instanceof String[])
        {
            String[] var7 = ((String[]) objArray[var4++]);

            for (int var8 = 0; var8 < var7.length; ++var8)
            {
                String var9 = var7[var8];
                ++var6;
                var5 = var9.length();
                var3 = var3 + var9;
            }
        }
        else
        {
            while (objArray[var4] instanceof String)
            {
                String var11 = (String) objArray[var4++];
                ++var6;
                var5 = var11.length();
                var3 = var3 + var11;
            }
        }

        HashMap var12;

        for (var12 = new HashMap(); var4 < objArray.length; var4 += 2)
        {
            Character var13 = (Character) objArray[var4];
            ItemStack var14 = null;

            if (objArray[var4 + 1] instanceof Item)
            {
                var14 = new ItemStack((Item) objArray[var4 + 1]);
            }
            else if (objArray[var4 + 1] instanceof Block)
            {
                var14 = new ItemStack((Block) objArray[var4 + 1], 1, Short.MAX_VALUE);
            }
            else if (objArray[var4 + 1] instanceof ItemStack)
            {
                var14 = (ItemStack) objArray[var4 + 1];
            }

            var12.put(var13, var14);
        }

        ItemStack[] var15 = new ItemStack[var5 * var6];

        for (int var16 = 0; var16 < var5 * var6; ++var16)
        {
            char var10 = var3.charAt(var16);

            if (var12.containsKey(Character.valueOf(var10)))
            {
                var15[var16] = ((ItemStack) var12.get(Character.valueOf(var10))).copy();
            }
            else
            {
                var15[var16] = null;
            }
        }

        ShapedRecipes var17 = new ShapedRecipes(var5, var6, var15, itemstack);
        recipeList.add(0, var17);
    }

    public static void modRecipes ()
    {
        if (!TRepo.initRecipes)
        {
            TRepo.initRecipes = true;
            if (PHConstruct.removeVanillaToolRecipes)
            {
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeWood));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordWood));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeStone));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordStone));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeIron));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordIron));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeDiamond));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordDiamond));

                RecipeRemover.removeAnyRecipe(new ItemStack(Item.pickaxeGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.axeGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.shovelGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.hoeGold));
                RecipeRemover.removeAnyRecipe(new ItemStack(Item.swordGold));
            }
        }
    }

    private static void registerPatternMaterial (String oreName, int value, String materialName)
    {
        for (ItemStack ore : OreDictionary.getOres(oreName))
        {
            PatternBuilder.instance.registerMaterial(ore, value, materialName);
        }
    }

    private static void registerIngotCasting (FluidType ft)
    {
        ItemStack pattern = new ItemStack(TRepo.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        for (ItemStack ore : OreDictionary.getOres("ingot" + ft.toString()))
        {
            tableCasting.addCastingRecipe(pattern, new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(ore.itemID, 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(pattern, new FluidStack(TRepo.moltenGoldFluid, TConstruct.oreLiquidValue), new ItemStack(ore.itemID, 1, ore.getItemDamage()), false, 50);
            tableCasting.addCastingRecipe(new ItemStack(ore.itemID, 1, ore.getItemDamage()), new FluidStack(ft.fluid, TConstruct.ingotLiquidValue), pattern, 80);
        }

    }

    protected static void addRecipesForBasinCasting ()
    {
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
        // Block Casting
        basinCasting.addCastingRecipe(new ItemStack(Block.blockIron), new FluidStack(TRepo.moltenIronFluid, TConstruct.blockLiquidValue), null, true, 100); //Iron
        basinCasting.addCastingRecipe(new ItemStack(Block.blockGold), new FluidStack(TRepo.moltenGoldFluid, TConstruct.blockLiquidValue), null, true, 100); //gold
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 3), new FluidStack(TRepo.moltenCopperFluid, TConstruct.blockLiquidValue), null, true, 100); //copper
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 5), new FluidStack(TRepo.moltenTinFluid, TConstruct.blockLiquidValue), null, true, 100); //tin
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 6), new FluidStack(TRepo.moltenAluminumFluid, TConstruct.blockLiquidValue), null, true, 100); //aluminum
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 0), new FluidStack(TRepo.moltenCobaltFluid, TConstruct.blockLiquidValue), null, true, 100); //cobalt
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 1), new FluidStack(TRepo.moltenArditeFluid, TConstruct.blockLiquidValue), null, true, 100); //ardite
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 4), new FluidStack(TRepo.moltenBronzeFluid, TConstruct.blockLiquidValue), null, true, 100); //bronze
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 7), new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.blockLiquidValue), null, true, 100); //albrass
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 2), new FluidStack(TRepo.moltenManyullynFluid, TConstruct.blockLiquidValue), null, true, 100); //manyullyn
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 8), new FluidStack(TRepo.moltenAlumiteFluid, TConstruct.blockLiquidValue), null, true, 100); //alumite
        basinCasting.addCastingRecipe(new ItemStack(Block.obsidian), new FluidStack(TRepo.moltenObsidianFluid, TConstruct.oreLiquidValue), null, true, 100);// obsidian
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock, 1, 9), new FluidStack(TRepo.moltenSteelFluid, TConstruct.blockLiquidValue), null, true, 100); //steel
        basinCasting.addCastingRecipe(new ItemStack(TRepo.clearGlass, 1, 0), new FluidStack(TRepo.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100); //glass
        basinCasting.addCastingRecipe(new ItemStack(TRepo.smeltery, 1, 4), new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue), null, true, 100); //seared stone
        basinCasting.addCastingRecipe(new ItemStack(TRepo.smeltery, 1, 5), new FluidStack(TRepo.moltenStoneFluid, TConstruct.chunkLiquidValue), new ItemStack(Block.cobblestone), true, 100);
        basinCasting.addCastingRecipe(new ItemStack(Block.blockEmerald), new FluidStack(TRepo.moltenEmeraldFluid, 640 * 9), null, true, 100); //emerald
        basinCasting.addCastingRecipe(new ItemStack(TRepo.speedBlock, 1, 0), new FluidStack(TRepo.moltenTinFluid, TConstruct.nuggetLiquidValue), new ItemStack(Block.gravel), true, 100); //brownstone
        basinCasting.addCastingRecipe(new ItemStack(Block.whiteStone), new FluidStack(TRepo.moltenEnderFluid, TConstruct.chunkLiquidValue), new ItemStack(Block.obsidian), true, 100); //endstone
        basinCasting.addCastingRecipe(new ItemStack(TRepo.metalBlock.blockID, 1, 10), new FluidStack(TRepo.moltenEnderFluid, 1000), null, true, 100); //ender
        basinCasting.addCastingRecipe(new ItemStack(TRepo.glueBlock), new FluidStack(TRepo.glueFluid, TConstruct.blockLiquidValue), null, true, 100); //glue

        // basinCasting.addCastingRecipe(new ItemStack(slimeGel, 1, 0), new FluidStack(blueSlimeFluid, FluidContainerRegistry.BUCKET_VOLUME), null, true, 100);

        //Armor casts
        /*FluidRenderProperties frp = new FluidRenderProperties(Applications.BASIN.minHeight, 0.65F, Applications.BASIN);
        FluidStack aluFlu = new FluidStack(TRepo.moltenAlubrassFluid, TConstruct.ingotLiquidValue * 10);
        FluidStack gloFlu = new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 10);
        ItemStack[] armor = { new ItemStack(helmetWood), new ItemStack(chestplateWood), new ItemStack(leggingsWood), new ItemStack(bootsWood) };
        for (int sc = 0; sc < armor.length; sc++)
        {
            basinCasting.addCastingRecipe(new ItemStack(armorPattern, 1, sc), aluFlu, armor[sc], 50, frp);
            basinCasting.addCastingRecipe(new ItemStack(armorPattern, 1, sc), gloFlu, armor[sc], 50, frp);
        }*/
    }

    protected static void addRecipesForSmeltery ()
    {
        //Alloy Smelting
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenBronzeFluid, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsBronzeAlloy)), new FluidStack(TRepo.moltenCopperFluid,
                TConstruct.ingotLiquidValue * 3), new FluidStack(TRepo.moltenTinFluid, TConstruct.ingotLiquidValue)); //Bronze          
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenAlubrassFluid, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsAluminumBrassAlloy)), new FluidStack(TRepo.moltenAluminumFluid,
                TConstruct.ingotLiquidValue * 3), new FluidStack(TRepo.moltenCopperFluid, TConstruct.ingotLiquidValue * 1)); //Aluminum Brass
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenAlumiteFluid, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsAlumiteAlloy)), new FluidStack(TRepo.moltenAluminumFluid,
                TConstruct.ingotLiquidValue * 5), new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 2), new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2)); //Alumite
        Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenManyullynFluid, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsManyullynAlloy)), new FluidStack(TRepo.moltenCobaltFluid,
                TConstruct.ingotLiquidValue), new FluidStack(TRepo.moltenArditeFluid, TConstruct.ingotLiquidValue)); //Manyullyn
        Smeltery.addAlloyMixing(new FluidStack(TRepo.pigIronFluid, (int) (TConstruct.ingotLiquidValue * PHConstruct.ingotsPigironAlloy)), new FluidStack(TRepo.moltenIronFluid,
                TConstruct.ingotLiquidValue), new FluidStack(TRepo.moltenEmeraldFluid, 640), new FluidStack(TRepo.bloodFluid, 80)); //Pigiron 

        // Stone parts
        for (int sc = 0; sc < TRepo.patternOutputs.length; sc++)
        {
            if (TRepo.patternOutputs[sc] != null)
            {
                Smeltery.addMelting(FluidType.Stone, new ItemStack(TRepo.patternOutputs[sc], 1, 1), 1,
                        (8 * ((IPattern) TRepo.woodPattern).getPatternCost(new ItemStack(TRepo.woodPattern, 1, sc + 1))) / 2);
            }
        }

        // Chunks
        Smeltery.addMelting(FluidType.Stone, new ItemStack(TRepo.toolShard, 1, 1), 0, 4);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(TRepo.toolShard, 1, 2), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Obsidian, new ItemStack(TRepo.toolShard, 1, 6), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Cobalt, new ItemStack(TRepo.toolShard, 1, 10), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Ardite, new ItemStack(TRepo.toolShard, 1, 11), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Manyullyn, new ItemStack(TRepo.toolShard, 1, 12), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Copper, new ItemStack(TRepo.toolShard, 1, 13), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Bronze, new ItemStack(TRepo.toolShard, 1, 14), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Alumite, new ItemStack(TRepo.toolShard, 1, 15), 0, TConstruct.chunkLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(TRepo.toolShard, 1, 16), 0, TConstruct.chunkLiquidValue);

        // Items

        Smeltery.addMelting(FluidType.AluminumBrass, new ItemStack(TRepo.blankPattern, 4, 1), -50, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(TRepo.blankPattern, 4, 2), -50, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.Glue, new ItemStack(TRepo.materials, 1, 36), 0, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(FluidType.Ender, new ItemStack(Item.enderPearl, 4), 0, 250);
        Smeltery.addMelting(TRepo.metalBlock, 10, 50, new FluidStack(TRepo.moltenEnderFluid, 1000));
        Smeltery.addMelting(FluidType.Water, new ItemStack(Item.snowball, 1, 0), 0, 125);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.flintAndSteel, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.compass, 1, 0), 0, TConstruct.ingotLiquidValue * 4);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.bucketEmpty), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartEmpty), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartCrate), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartPowered), 0, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.minecartHopper), 50, TConstruct.ingotLiquidValue * 10);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.doorIron), 0, TConstruct.ingotLiquidValue * 6);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.cauldron), 0, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.shears), 0, TConstruct.ingotLiquidValue * 2);
        Smeltery.addMelting(FluidType.Emerald, new ItemStack(Item.emerald), -50, 640);

        //Blocks melt as themselves!
        //Ore
        Smeltery.addMelting(Block.oreIron, 0, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.oreGold, 0, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TRepo.oreGravel, 0, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TRepo.oreGravel, 1, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 2));

        //Blocks
        Smeltery.addMelting(Block.blockIron, 0, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.blockGold, 0, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue * 9));
        Smeltery.addMelting(Block.obsidian, 0, 800, new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(Block.ice, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 1000));
        Smeltery.addMelting(Block.blockSnow, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 500));
        Smeltery.addMelting(Block.snow, 0, 75, new FluidStack(FluidRegistry.getFluid("water"), 250));
        Smeltery.addMelting(Block.sand, 0, 625, new FluidStack(TRepo.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.glass, 0, 625, new FluidStack(TRepo.moltenGlassFluid, FluidContainerRegistry.BUCKET_VOLUME));
        Smeltery.addMelting(Block.thinGlass, 0, 625, new FluidStack(TRepo.moltenGlassFluid, 250));
        Smeltery.addMelting(Block.stone, 0, 800, new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 18));
        Smeltery.addMelting(Block.cobblestone, 0, 800, new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 18));
        Smeltery.addMelting(Block.blockEmerald, 0, 800, new FluidStack(TRepo.moltenEmeraldFluid, 640 * 9));
        Smeltery.addMelting(TRepo.glueBlock, 0, 250, new FluidStack(TRepo.glueFluid, TConstruct.blockLiquidValue));
        Smeltery.addMelting(TRepo.craftedSoil, 1, 600, new FluidStack(TRepo.moltenStoneFluid, TConstruct.ingotLiquidValue / 4));

        Smeltery.addMelting(TRepo.clearGlass, 0, 500, new FluidStack(TRepo.moltenGlassFluid, 1000));
        Smeltery.addMelting(TRepo.glassPane, 0, 350, new FluidStack(TRepo.moltenGlassFluid, 250));

        for (int i = 0; i < 16; i++)
        {
            Smeltery.addMelting(TRepo.stainedGlassClear, i, 500, new FluidStack(TRepo.moltenGlassFluid, 1000));
            Smeltery.addMelting(TRepo.stainedGlassClearPane, i, 350, new FluidStack(TRepo.moltenGlassFluid, 250));
        }

        //Bricks
        Smeltery.addMelting(TRepo.multiBrick, 4, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrickFancy, 4, 600, new FluidStack(TRepo.moltenIronFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrick, 5, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrickFancy, 5, 400, new FluidStack(TRepo.moltenGoldFluid, TConstruct.ingotLiquidValue));
        Smeltery.addMelting(TRepo.multiBrick, 0, 800, new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));
        Smeltery.addMelting(TRepo.multiBrickFancy, 0, 800, new FluidStack(TRepo.moltenObsidianFluid, TConstruct.ingotLiquidValue * 2));

        //Vanilla blocks
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.fenceIron), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.pressurePlateIron), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Block.pressurePlateGold, 4), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.rail), 0, TConstruct.ingotLiquidValue * 6 / 16);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Block.railPowered), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.railDetector), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.railActivator), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Obsidian, new ItemStack(Block.enchantmentTable), 0, TConstruct.ingotLiquidValue * 4);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.cauldron), 0, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.anvil, 1, 0), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.anvil, 1, 1), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.anvil, 1, 2), 200, TConstruct.ingotLiquidValue * 31);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Block.hopperBlock), 0, TConstruct.ingotLiquidValue * 5);

        //Vanilla Armor
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.helmetIron, 1, 0), 50, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.plateIron, 1, 0), 50, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.legsIron, 1, 0), 50, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.bootsIron, 1, 0), 50, TConstruct.ingotLiquidValue * 4);

        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.helmetGold, 1, 0), 50, TConstruct.ingotLiquidValue * 5);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.plateGold, 1, 0), 50, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.legsGold, 1, 0), 50, TConstruct.ingotLiquidValue * 7);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.bootsGold, 1, 0), 50, TConstruct.ingotLiquidValue * 4);

        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.helmetChain, 1, 0), 25, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.plateChain, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.legsChain, 1, 0), 50, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Steel, new ItemStack(Item.bootsChain, 1, 0), 25, TConstruct.ingotLiquidValue);

        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.horseArmorIron, 1), 100, TConstruct.ingotLiquidValue * 8);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.horseArmorGold, 1), 100, TConstruct.ingotLiquidValue * 8);

        //Vanilla tools
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.hoeIron, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.swordIron, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.shovelIron, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.pickaxeIron, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(FluidType.Iron, new ItemStack(Item.axeIron, 1, 0), 0, TConstruct.ingotLiquidValue * 3);

        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.hoeGold, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.swordGold, 1, 0), 0, TConstruct.oreLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.shovelGold, 1, 0), 0, TConstruct.ingotLiquidValue);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.pickaxeGold, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
        Smeltery.addMelting(FluidType.Gold, new ItemStack(Item.axeGold, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
    }

    public static void modIntegration ()
    {
        /* Natura */
        Block taintedSoil = GameRegistry.findBlock("Natura", "soil.tainted");
        Block heatSand = GameRegistry.findBlock("Natura", "heatsand");
        if (taintedSoil != null && heatSand != null)
            GameRegistry.addShapelessRecipe(new ItemStack(TRepo.craftedSoil, 2, 6), Item.netherStalkSeeds, taintedSoil, heatSand);

        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TRepo.pickaxeHead, 1, 6), new ItemStack(TRepo.toolRod, 1, 2), new ItemStack(TRepo.binding, 1, 6), "");
        /*TE3 Flux*/
        ItemStack batHardened = GameRegistry.findItemStack("ThermalExpansion", "capacitorHardened", 1);
        if (batHardened != null)
        {
            TRepo.modF.batteries.add(batHardened);
        }
        ItemStack basicCell = GameRegistry.findItemStack("ThermalExpansion", "cellBasic", 1);
        if (basicCell != null)
        {
            TRepo.modF.batteries.add(basicCell);
        }
        if (batHardened != null)
            TConstructClientRegistry.registerManualModifier("fluxmod", ironpick.copy(), (ItemStack) batHardened);
        if (basicCell != null)
            TConstructClientRegistry.registerManualModifier("fluxmod2", ironpick.copy(), (ItemStack) basicCell);

        /* Thaumcraft */
        Object obj = getStaticItem("itemResource", "thaumcraft.common.config.ConfigItems");
        if (obj != null)
        {
            TConstruct.logger.info("Thaumcraft detected. Adding thaumium tools.");
            TRepo.thaumcraftAvailable = true;
            TConstructClientRegistry.addMaterialRenderMapping(31, "tinker", "thaumium", true);
            TConstructRegistry.addToolMaterial(31, "Thaumium", 3, 400, 700, 2, 1.3F, 0, 0f, "\u00A75", "Thaumic");
            PatternBuilder.instance.registerFullMaterial(new ItemStack((Item) obj, 1, 2), 2, "Thaumium", new ItemStack(TRepo.toolShard, 1, 31), new ItemStack(TRepo.toolRod, 1, 31), 31);
            for (int meta = 0; meta < TRepo.patternOutputs.length; meta++)
            {
                if (TRepo.patternOutputs[meta] != null)
                    TConstructRegistry.addPartMapping(TRepo.woodPattern.itemID, meta + 1, 31, new ItemStack(TRepo.patternOutputs[meta], 1, 31));
            }

            TConstructRegistry.addBowstringMaterial(1, 2, new ItemStack((Item) obj, 1, 7), new ItemStack(TRepo.bowstring, 1, 1), 1F, 1F, 0.9f);
            TConstructRegistry.addBowMaterial(31, 576, 40, 1.2f);
            TConstructRegistry.addArrowMaterial(31, 1.8F, 0.5F, 100F);
        }
        else
        {
            TConstruct.logger.warning("Thaumcraft not detected.");
        }

        if (Loader.isModLoaded("Natura"))
        {
            try
            {
                Object plantItem = getStaticItem("plantItem", "mods.natura.common.NContent");
                TConstructRegistry.addBowstringMaterial(2, 2, new ItemStack((Item) plantItem, 1, 7), new ItemStack(TRepo.bowstring, 1, 2), 1.2F, 0.8F, 1.3f);
            }
            catch (Exception e)
            {
            } //No need to handle
        }

        ItemStack ingotcast = new ItemStack(TRepo.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.instance.getTableCasting();
        LiquidCasting basinCasting = TConstructRegistry.instance.getBasinCasting();

        /* Thermal Expansion 3 Metals */
        ArrayList<ItemStack> ores = OreDictionary.getOres("ingotNickel");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenNickelFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotLead");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenLeadFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotSilver");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenSilverFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotPlatinum");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenShinyFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
        }
        ores = OreDictionary.getOres("ingotInvar");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenInvarFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenInvarFluid, 24), new FluidStack(TRepo.moltenIronFluid, 16), new FluidStack(TRepo.moltenNickelFluid, 8)); //Invar
        }
        ores = OreDictionary.getOres("ingotElectrum");
        if (ores.size() > 0)
        {
            tableCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.ingotLiquidValue), ingotcast, 80);
            Smeltery.addAlloyMixing(new FluidStack(TRepo.moltenElectrumFluid, 16), new FluidStack(TRepo.moltenGoldFluid, 8), new FluidStack(TRepo.moltenSilverFluid, 8)); //Electrum
        }

        ores = OreDictionary.getOres("blockNickel");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenNickelFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockLead");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenLeadFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockSilver");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenSilverFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockPlatinum");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenShinyFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockInvar");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenInvarFluid, TConstruct.blockLiquidValue), null, 100);
        }
        ores = OreDictionary.getOres("blockElectrum");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(ores.get(0), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.blockLiquidValue), null, 100);
        }

        /* Extra Utilities */
        ores = OreDictionary.getOres("compressedGravel1x");
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(TRepo.speedBlock, 9), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.blockLiquidValue), ores.get(0), 100);
        }
        ores = OreDictionary.getOres("compressedGravel2x"); //Higher won't save properly
        if (ores.size() > 0)
        {
            basinCasting.addCastingRecipe(new ItemStack(TRepo.speedBlock, 81), new FluidStack(TRepo.moltenElectrumFluid, TConstruct.blockLiquidValue * 9), ores.get(0), 100);
        }

        /* Rubber */
        ores = OreDictionary.getOres("itemRubber");
        if (ores.size() > 0)
        {
            FurnaceRecipes.smelting().addSmelting(TRepo.materials.itemID, 36, ores.get(0), 0.2f);
        }
    }

    public static Object getStaticItem (String name, String classPackage)
    {
        try
        {
            Class clazz = Class.forName(classPackage);
            Field field = clazz.getDeclaredField(name);
            Object ret = field.get(null);
            if (ret != null && (ret instanceof ItemStack || ret instanceof Item))
                return ret;
            return null;
        }
        catch (Exception e)
        {
            TConstruct.logger.warning("Could not find " + name + "from " + classPackage);
            return null;
        }
    }

}
