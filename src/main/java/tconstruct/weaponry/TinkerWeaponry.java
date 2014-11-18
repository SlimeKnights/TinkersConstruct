package tconstruct.weaponry;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.*;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.library.tools.FletchlingLeafMaterial;
import tconstruct.library.util.IPattern;
import tconstruct.library.util.IToolPart;
import tconstruct.modifiers.tools.ModWindup;
import tconstruct.modifiers.tools.ModAmmoRestock;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.items.Bowstring;
import tconstruct.tools.items.Fletching;
import tconstruct.tools.items.Pattern;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.library.tools.DualMaterialToolPart;
import tconstruct.weaponry.items.GlassArrows;
import tconstruct.weaponry.items.WeaponryPattern;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.library.weaponry.AmmoWeapon;
import tconstruct.library.weaponry.ArrowShaftMaterial;
import tconstruct.library.weaponry.ProjectileWeapon;
import tconstruct.weaponry.weapons.*;
import tconstruct.world.TinkerWorld;

import java.util.Map;
import java.util.Random;

import static tconstruct.tools.TinkerTools.MaterialID;

@GameRegistry.ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers' Weaponry", description = "The main core of the mod! All of the tools, the tables, and the patterns are here.", pulsesRequired="Tinkers' Tools")
public class TinkerWeaponry {
    @SidedProxy(clientSide = "tconstruct.weaponry.WeaponryClientProxy", serverSide = "tconstruct.weaponry.WeaponryCommonProxy")
    public static WeaponryCommonProxy proxy;

    // Throwing Weapons
    public static AmmoWeapon shuriken;
    public static AmmoWeapon throwingknife;
    public static AmmoWeapon javelin;
    // Projectile Weapons
    public static ProjectileWeapon shortbow;
    public static ProjectileWeapon longbow;
    public static ProjectileWeapon crossbow;

    // Ammo
    public static AmmoItem arrowAmmo;
    public static AmmoItem boltAmmo;

    // Tool Parts
    public static Item bowstring;
    public static DynamicToolPart arrowhead;
    public static Item fletching;
    public static DynamicToolPart partShuriken;
    public static DynamicToolPart partArrowShaft; // not craftable, used internally
    public static DynamicToolPart partBowLimb;
    public static DynamicToolPart partCrossbowLimb;
    public static DynamicToolPart partCrossbowBody;
    public static DualMaterialToolPart partBolt;

    // patterns/casts
    public static Pattern woodPattern;
    public static Pattern metalPattern;

    // other stuff
    public static Random random = new Random();


    @Handler
    public void preInit(FMLPreInitializationEvent event)
    {
        registerItems();
        registerLegendaries();
        registerMaterials();
    }

    @Handler
    public void init(FMLInitializationEvent event)
    {
        addPartRecipies();
        addWeaponRecipies();
        registerBoltCasting();
        setupCreativeTab();

        ItemStack redstoneItem = new ItemStack(Items.redstone);
        ItemStack redstoneBlock = new ItemStack(Blocks.redstone_block);
        ModifyBuilder.registerModifier(new ModWindup(2, new ItemStack[] { redstoneItem, redstoneBlock }, new int[] { 1, 9 }));
        ModifyBuilder.registerModifier(new ModAmmoRestock());

        TConstructRegistry.registerActiveToolMod(new WeaponryActiveToolMod());
    }

    @Handler
    public void postInit(FMLPostInitializationEvent event)
    {
        // this handler takes care that ammo weapons get ammo.
        WeaponryHandler weaponryHandler = new WeaponryHandler();
        MinecraftForge.EVENT_BUS.register(weaponryHandler);
        FMLCommonHandler.instance().bus().register(weaponryHandler);

        proxy.init();
    }

    private void registerItems()
    {
        // create tool part
        TinkerTools.bowstring = bowstring = new Bowstring().setUnlocalizedName("tconstruct.Bowstring");
        TinkerTools.arrowhead = arrowhead = new DynamicToolPart("_arrowhead", "ArrowHead");
        TinkerTools.fletching = fletching = new Fletching().setUnlocalizedName("tconstruct.Fletching");
        partShuriken = new DynamicToolPart("_shuriken", "Shuriken");
        partArrowShaft = new DynamicToolPart("_arrow_shaft", "Shaft");
        partBowLimb = new DynamicToolPart("_bow_limb", "BowLimb");
        partCrossbowLimb = new DynamicToolPart("_crossbow_limb", "CrossbowLimb");
        partCrossbowBody = new DynamicToolPart("_crossbow_body", "CrossbowBody");
        partBolt = new DualMaterialToolPart("_bolt", "Bolt");

        // create throwing weapons
        shuriken = new Shuriken();
        throwingknife = new ThrowingKnife();
        javelin = new Javelin();
        // create projectile weapons
        TinkerTools.shortbow = shortbow = new ShortBow();
        longbow = new LongBow();
        crossbow = new Crossbow();
        // create ammo
        arrowAmmo = new ArrowAmmo();
        boltAmmo = new BoltAmmo();

        woodPattern = new WeaponryPattern("pattern_", "Pattern");
        metalPattern = new WeaponryPattern("cast_", "MetalPattern");

        // register tool parts
        GameRegistry.registerItem(bowstring, "bowstring"); // 1.8 todo: rename properly?
        GameRegistry.registerItem(arrowhead, "arrowhead");
        GameRegistry.registerItem(fletching, "fletching");
        GameRegistry.registerItem(partShuriken, "ShurikenPart");
        GameRegistry.registerItem(partBowLimb, "BowLimbPart");
        GameRegistry.registerItem(partCrossbowLimb, "CrossbowLimbPart");
        GameRegistry.registerItem(partCrossbowBody, "CrossbowBodyPart");
        GameRegistry.registerItem(partBolt, "BoltPart");
        // register throwing weapons
        GameRegistry.registerItem(shuriken, "Shuriken");
        GameRegistry.registerItem(throwingknife, "ThrowingKnife");
        GameRegistry.registerItem(javelin, "Javelin");
        // register projectile weapons
        GameRegistry.registerItem(shortbow, "ShortBow");
        GameRegistry.registerItem(longbow, "LongBow");
        GameRegistry.registerItem(crossbow, "Crossbow");
        // register ammo
        GameRegistry.registerItem(arrowAmmo, "ArrowAmmo");
        GameRegistry.registerItem(boltAmmo, "BoltAmmo");
        // register patterns/casts
        GameRegistry.registerItem(woodPattern, "Pattern");
        GameRegistry.registerItem(metalPattern, "Cast");
    }

    private void addPartRecipies()
    {
        StencilBuilder.registerStencil(21, TinkerTools.woodPattern, 25); // arrow head
        StencilBuilder.registerStencil(22, TinkerTools.woodPattern, 24); // fletchling
        StencilBuilder.registerStencil(23, TinkerTools.woodPattern, 23); // bowstring

        StencilBuilder.registerStencil(24, woodPattern, 0);
        StencilBuilder.registerStencil(25, woodPattern, 1);
        StencilBuilder.registerStencil(26, woodPattern, 2);
        StencilBuilder.registerStencil(27, woodPattern, 3);

        PatternBuilder.instance.addToolPattern(woodPattern);

        // todo: integrate into tcon
        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };
        int[] liquidDamage = new int[] { 2, 13, 10, 11, 12, 14, 15, 6, 16, 18 };
        Item[] patternOutputs = new Item[] { partShuriken, partCrossbowLimb, partCrossbowBody, partBowLimb };

        // register part crafting
        for (int m = 0; m < patternOutputs.length; m++)
            for (int nonMetal : nonMetals)
                TConstructRegistry.addPartMapping(woodPattern, m, nonMetal, new ItemStack(patternOutputs[m], 1, nonMetal));

        // register part casting
        LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
        for(int i = 0; i < patternOutputs.length; i++)
        {
            ItemStack cast = new ItemStack(metalPattern, 1, i);

            tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(patternOutputs[i], 1, Short.MAX_VALUE), false, 50);
            tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(patternOutputs[i], 1, Short.MAX_VALUE), false, 50);

            for (int iterTwo = 0; iterTwo < TinkerSmeltery.liquids.length; iterTwo++)
            {
                Fluid fs = TinkerSmeltery.liquids[iterTwo].getFluid();
                int fluidAmount = metalPattern.getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                ItemStack metalCast = new ItemStack(patternOutputs[i], 1, liquidDamage[iterTwo]);
                tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
            }
        }

        // arrowhead is still integrated in tinkertools.. bla n stuff
        for (int nonMetal : nonMetals)
            TConstructRegistry.addPartMapping(TinkerTools.woodPattern, 25, nonMetal, new ItemStack(arrowhead, 1, nonMetal));

        ItemStack cast = new ItemStack(TinkerSmeltery.metalPattern, 1, 25);
        tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue), new ItemStack(arrowhead, 1, Short.MAX_VALUE), false, 50);
        tableCasting.addCastingRecipe(cast, new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2), new ItemStack(arrowhead, 1, Short.MAX_VALUE), false, 50);

        for (int iterTwo = 0; iterTwo < TinkerSmeltery.liquids.length; iterTwo++)
        {
            Fluid fs = TinkerSmeltery.liquids[iterTwo].getFluid();
            int fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
            ItemStack metalCast = new ItemStack(arrowhead, 1, liquidDamage[iterTwo]);
            tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
            Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
        }
    }

    private void registerLegendaries()
    {
        GameRegistry.registerItem(new GlassArrows(), "GlassArrows");
    }

    private void registerMaterials()
    {
        // Bow Materials: Material ID, durability, drawspeed, arrow speed
        // speed 3.0 == exactly the vanilla bow if 2 parts of speed 3 are used
        // Wooden stuff is flexible, therefore good. Reference.
        TConstructRegistry.addBowMaterial(MaterialID.Wood, 18, 3.0f); // Wood
        // other organic materials also are good
        TConstructRegistry.addBowMaterial(MaterialID.Cactus,     20, 3.4f); // Cactus
        TConstructRegistry.addBowMaterial(MaterialID.Bone,       38, 3.0f); // Bone
        TConstructRegistry.addBowMaterial(MaterialID.Slime,      28, 4.2f); // Slime
        TConstructRegistry.addBowMaterial(MaterialID.BlueSlime,  21, 4.0f); // Blue Slime
        TConstructRegistry.addBowMaterial(MaterialID.Paper,      10, 1.1f); // Paper
        // Metal stuff has a lot of POW, but takes LONG to wind up since it's so hard
        TConstructRegistry.addBowMaterial(MaterialID.Iron,       54, 5.2f); // Iron
        TConstructRegistry.addBowMaterial(MaterialID.Steel,      60, 5.5f); // Steel
        TConstructRegistry.addBowMaterial(MaterialID.PigIron,    42, 5.2f); // Pig Iron - it's meat!
        TConstructRegistry.addBowMaterial(MaterialID.Ardite,     55, 4.9f); // Ardite
        TConstructRegistry.addBowMaterial(MaterialID.Cobalt,     45, 5.3f); // Cobalt
        TConstructRegistry.addBowMaterial(MaterialID.Manyullyn,  50, 5.0f); // Manyullyn
        TConstructRegistry.addBowMaterial(MaterialID.Copper,     40, 4.9f); // Copper
        TConstructRegistry.addBowMaterial(MaterialID.Bronze,     45, 5.1f); // Bronze
        TConstructRegistry.addBowMaterial(MaterialID.Alumite,    45, 5.0f); // Alumite - a bit stone-ish since it has obsidian
        // Stone doesn't bend. takes forever, has no pow. WHY WOULD YOU DO THAT
        TConstructRegistry.addBowMaterial(MaterialID.Stone,      90, 1.0f); // Stone
        TConstructRegistry.addBowMaterial(MaterialID.Flint,      90, 1.0f); // Flint
        TConstructRegistry.addBowMaterial(MaterialID.Obsidian,   109, 1.0f); // Obsidian
        TConstructRegistry.addBowMaterial(MaterialID.Netherrack, 80, 1.0f); // Netherrack

        // Arrow Head Materials: Material ID, mass, fragility
        TConstructRegistry.addArrowMaterial(MaterialID.Wood,        0.69F,  1.0F); //Wood
        TConstructRegistry.addArrowMaterial(MaterialID.Stone,       2.05F,  5.0F); //Stone
        TConstructRegistry.addArrowMaterial(MaterialID.Iron,        3.3F,   0.5F); //Iron
        TConstructRegistry.addArrowMaterial(MaterialID.Flint,       1.325F, 1.0F); //Flint
        TConstructRegistry.addArrowMaterial(MaterialID.Cactus,      0.76F,  1.0F); //Cactus
        TConstructRegistry.addArrowMaterial(MaterialID.Bone,        1.69F,  1.0F); //Bone
        TConstructRegistry.addArrowMaterial(MaterialID.Obsidian,    2.4F,   1.0F); //Obsidian
        TConstructRegistry.addArrowMaterial(MaterialID.Netherrack,  1.5F,   1.0F); //Netherrack
        TConstructRegistry.addArrowMaterial(MaterialID.Slime,       0.22F,  0.0F); //Slime
        TConstructRegistry.addArrowMaterial(MaterialID.Paper,       0.69F,  3.0F); //Paper
        TConstructRegistry.addArrowMaterial(MaterialID.Cobalt,      3.0F,   0.25f); //Cobalt
        TConstructRegistry.addArrowMaterial(MaterialID.Ardite,      1.25F,  0.25f); //Ardite
        TConstructRegistry.addArrowMaterial(MaterialID.Manyullyn,   2.25F,  0.1F); //Manyullyn
        TConstructRegistry.addArrowMaterial(MaterialID.Copper,      2.7F,   0.5F); //Copper
        TConstructRegistry.addArrowMaterial(MaterialID.Bronze,      3.2F,   0.25f); //Bronze
        TConstructRegistry.addArrowMaterial(MaterialID.Alumite,     1.1F,   0.25f); //Alumite
        TConstructRegistry.addArrowMaterial(MaterialID.Steel,       3.6F,   0.25f); //Steel
        TConstructRegistry.addArrowMaterial(MaterialID.BlueSlime,   0.22F,  0.0F); //Blue Slime
        TConstructRegistry.addArrowMaterial(MaterialID.PigIron,     3.6F,   0.5F); //Pigiron

        // Arrow Shaft Materials: Material ID, crafting item, durability-medifier, mass, fragility
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(0, Items.stick,     1.0f,  1.0f, 0.15f, 0x866526)); // wood: reference material, 10% break chance
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(1, Items.bone,      0.95f, 1.8f, 0.02f, 0xede6bf)); // bone: heavier, but durable
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(2, Items.reeds,     1.5f,  0.5f, 0.66f, 0xc7ff87)); // reed: light, but less durable
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(3, Items.blaze_rod, 1.2f,  0.9f, 0.08f, 0xfff32d)); // blaze: tad lighter, tad more durable, fieryyyy
        // also add the tool rod variants, same stats
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(0, TinkerTools.toolRod, MaterialID.Wood, 1.0f, 1.0f, 0.15f, 0x866526)); // wood: reference material, 10% break chance
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(1, TinkerTools.toolRod, MaterialID.Bone, 0.95f, 1.8f, 0.02f, 0xede6bf)); // bone: heavier, but durable

        // Arrow Fletching Materials
        TConstructRegistry.addFletchingMaterial(0, 2, new ItemStack(Items.feather), new ItemStack(TinkerWeaponry.fletching, 1, 0), 95F, 0.05F, 1.0f, 0xffffff); // Feather
        TConstructRegistry.addCustomMaterial(new FletchlingLeafMaterial(1, 2, "treeLeaves", new ItemStack(TinkerWeaponry.fletching, 1, 1), 75F, 0F, 2.5f)); // all vanilla and oredicted leaves. and all leaves in general.
        TConstructRegistry.addFletchingMaterial(2, 2, new ItemStack(TinkerTools.materials, 1, 1), new ItemStack(TinkerWeaponry.fletching, 1, 2), 100F, 0.005F, 0.8f, 0x82c873); // Slime
        TConstructRegistry.addFletchingMaterial(3, 2, new ItemStack(TinkerTools.materials, 1, 17), new ItemStack(TinkerWeaponry.fletching, 1, 3), 100F, 0.005F, 0.8f, 0x74c8c7); // BlueSlime
        TConstructRegistry.addFletchingMaterial(4, 2, new ItemStack(TinkerWorld.slimeLeaves, 1, 0), new ItemStack(TinkerWeaponry.fletching, 1, 4), 98F, 0.02F, 1.4F, 0x74e8c7); // Slime leaves

        // Bowstring Materials
        TConstructRegistry.addBowstringMaterial(0, 2, new ItemStack(Items.string), new ItemStack(TinkerWeaponry.bowstring, 1, 0), 1F, 1F, 1f, 0xeeeeee); // String
    }

    private void addWeaponRecipies()
    {
        TConstructRegistry.addToolRecipe(shuriken, partShuriken, partShuriken, partShuriken, partShuriken);
        TConstructRegistry.addToolRecipe(throwingknife, TinkerTools.knifeBlade, TinkerTools.toolRod);
        TConstructRegistry.addToolRecipe(javelin, arrowhead, TinkerTools.toughRod, TinkerTools.toughRod);

        TConstructRegistry.addToolRecipe(shortbow, partBowLimb, bowstring, partBowLimb);
        TConstructRegistry.addToolRecipe(longbow, partBowLimb, bowstring, partBowLimb, TinkerTools.largePlate);
        TConstructRegistry.addToolRecipe(crossbow, partCrossbowLimb, partCrossbowBody, bowstring, TinkerTools.toughBinding);

        TConstructRegistry.addToolRecipe(arrowAmmo, arrowhead, partArrowShaft, fletching);
        TConstructRegistry.addToolRecipe(boltAmmo, partBolt, partBolt, fletching);
    }

    private void registerBoltCasting()
    {
        LiquidCasting tb = TConstructRegistry.getTableCasting();

        // any fluid that is a toolpart material can be used
        for(Map.Entry<String, FluidType> entry : FluidType.fluidTypes.entrySet()) {
            // is tool material?
            if(!entry.getValue().isToolpart)
                continue;

            // get a casting recipe for it D:
            FluidStack liquid = new FluidStack(entry.getValue().fluid, TConstruct.ingotLiquidValue);
            CastingRecipe recipe = tb.getCastingRecipe(liquid, new ItemStack(TinkerSmeltery.metalPattern, 1, 2)); // pickaxe
            // no recipe found
            if(recipe == null)
                continue;

            // material id for the pickaxe head == material id for the fluid! such hack. wow.
            int matID = recipe.getResult().getItemDamage();

            // register our casting stuff
            for(Integer id : TConstructRegistry.toolMaterials.keySet()) {
                ItemStack rod = new ItemStack(TinkerTools.toolRod, 1, id);
                if(((IToolPart)TinkerTools.toolRod).getMaterialID(rod) == -1)
                    continue;

                tb.addCastingRecipe(DualMaterialToolPart.createDualMaterial(partBolt, id, matID), liquid, rod, true, 150);
            }
        }
    }

    private void setupCreativeTab()
    {
        ItemStack tool = new ItemStack(TinkerWeaponry.longbow, 1, 0);

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("InfiTool", new NBTTagCompound());
        compound.getCompoundTag("InfiTool").setInteger("RenderHead", 0);
        compound.getCompoundTag("InfiTool").setInteger("RenderHandle", 0);
        compound.getCompoundTag("InfiTool").setInteger("RenderAccessory", 0);
        compound.getCompoundTag("InfiTool").setInteger("RenderExtra", 1);
        tool.setTagCompound(compound);

        TConstructRegistry.weaponryTab.init(tool);
    }
}
