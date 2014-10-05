package tconstruct.weaponry;


import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructCreativeTab;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.*;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.IPattern;
import tconstruct.library.util.IToolPart;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.items.Pattern;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.library.tools.DualMaterialToolPart;
import tconstruct.weaponry.items.WeaponryPattern;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.library.weaponry.AmmoWeapon;
import tconstruct.library.weaponry.ArrowShaftMaterial;
import tconstruct.library.weaponry.ProjectileWeapon;
import tconstruct.weaponry.weapons.*;

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
    public static TConstructCreativeTab creativeTab = new TConstructCreativeTab("TConstructWeaponry");
    public static Random random = new Random();


    @Handler
    public void preInit(FMLPreInitializationEvent event)
    {
        registerItems();
        registerMaterials();
    }

    @Handler
    public void init(FMLInitializationEvent event)
    {
        addPartRecipies();
        addWeaponRecipies();
        registerBoltCasting();
    }

    @Handler
    public void postInit(FMLPostInitializationEvent event)
    {
        // this handler takes care that ammo weapons get ammo.
        MinecraftForge.EVENT_BUS.register(new WeaponryHandler());

        proxy.init();
    }

    private void registerItems()
    {
        // create tool part
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
        shortbow = new ShortBow();
        longbow = new LongBow();
        crossbow = new Crossbow();
        // create ammo
        arrowAmmo = new ArrowAmmo();
        boltAmmo = new BoltAmmo();

        woodPattern = new WeaponryPattern("pattern_", "Pattern");
        metalPattern = new WeaponryPattern("cast_", "Cast");

        // register tool parts
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
        StencilBuilder.registerStencil(woodPattern, 0);
        StencilBuilder.registerStencil(woodPattern, 1);
        StencilBuilder.registerStencil(woodPattern, 2);
        StencilBuilder.registerStencil(woodPattern, 3);

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
                int fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                ItemStack metalCast = new ItemStack(patternOutputs[i], 1, liquidDamage[iterTwo]);
                tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
            }
        }

    }

    private void registerMaterials()
    {
        // todo: un-hax
        TConstructRegistry.arrowMaterials.clear();
        TConstructRegistry.bowMaterials.clear();

        // todo: remove durability from this
        // Bow Materials: Material ID, durability, drawspeed, arrow speed
        // speed 3.0 == exactly the vanilla bow if 2 parts of speed 3 are used
        // Wooden stuff is flexible, therefore good
        TConstructRegistry.addBowMaterial(TinkerTools.MaterialID.Wood,      0, 18, 3.0f); // Wood
        // other organic materials also are good
        TConstructRegistry.addBowMaterial(MaterialID.Cactus,    0, 20, 2.4f); // Cactus
        TConstructRegistry.addBowMaterial(MaterialID.Bone,      0, 38, 2.0f); // Bone
        TConstructRegistry.addBowMaterial(MaterialID.Slime,     0, 28, 4.0f); // Slime
        TConstructRegistry.addBowMaterial(MaterialID.BlueSlime, 0, 21, 4.0f); // Blue Slime
        TConstructRegistry.addBowMaterial(MaterialID.Paper,     0, 25, 2.3f); // Paper
        // Metal stuff has a lot of POW, but takes LONG to wind up since it's so hard
        TConstructRegistry.addBowMaterial(MaterialID.Iron,      0, 40, 5.2f); // Iron
        TConstructRegistry.addBowMaterial(MaterialID.Steel,     0, 50, 5.5f); // Steel
        TConstructRegistry.addBowMaterial(MaterialID.PigIron,   0, 30, 5.2f); // Pig Iron - it's meat!
        TConstructRegistry.addBowMaterial(MaterialID.Ardite,    0, 50, 4.5f); // Ardite
        TConstructRegistry.addBowMaterial(MaterialID.Cobalt,    0, 35, 5.2f); // Cobalt
        TConstructRegistry.addBowMaterial(MaterialID.Manyullyn, 0, 45, 4.5f); // Manyullyn
        TConstructRegistry.addBowMaterial(MaterialID.Copper,    0, 30, 5.1f); // Copper
        TConstructRegistry.addBowMaterial(MaterialID.Bronze,    0, 35, 5.2f); // Bronze
        TConstructRegistry.addBowMaterial(MaterialID.Alumite,   0, 35, 4.8f); // Alumite - a bit stone-ish since it has obsidian
        // Stone doesn't bend. takes forever, has no pow. WHY WOULD YOU DO THAT
        TConstructRegistry.addBowMaterial(MaterialID.Stone,     0, 80, 1.0f); // Stone
        TConstructRegistry.addBowMaterial(MaterialID.Flint,     0, 80, 1.0f); // Flint
        TConstructRegistry.addBowMaterial(MaterialID.Obsidian,  0, 99, 1.0f); // Obsidian
        TConstructRegistry.addBowMaterial(MaterialID.Netherrack,0, 70, 1.0f); // Netherrack

        // Arrow Head Materials: Material ID, mass, fragility
        TConstructRegistry.addArrowMaterial(MaterialID.Wood,        0.69F,  1.0F,  100F); //Wood
        TConstructRegistry.addArrowMaterial(MaterialID.Stone,       2.05F,  5.0F,  100F); //Stone
        TConstructRegistry.addArrowMaterial(MaterialID.Iron,        3.6F,   0.5F,  100F); //Iron
        TConstructRegistry.addArrowMaterial(MaterialID.Flint,       1.325F, 1.0F,  100F); //Flint
        TConstructRegistry.addArrowMaterial(MaterialID.Cactus,      0.76F,  1.0F,  100F); //Cactus
        TConstructRegistry.addArrowMaterial(MaterialID.Bone,        0.69F,  1.0F,  100F); //Bone
        TConstructRegistry.addArrowMaterial(MaterialID.Obsidian,    2.4F,   1.0F,  100F); //Obsidian
        TConstructRegistry.addArrowMaterial(MaterialID.Netherrack,  1.5F,   1.0F,  100F); //Netherrack
        TConstructRegistry.addArrowMaterial(MaterialID.Slime,       0.22F,  0.0F,  100F); //Slime
        TConstructRegistry.addArrowMaterial(MaterialID.Paper,       0.69F,  3.0F,   90F); //Paper
        TConstructRegistry.addArrowMaterial(MaterialID.Cobalt,      3.0F,   0.25F, 100F); //Cobalt
        TConstructRegistry.addArrowMaterial(MaterialID.Ardite,      1.25F,  0.25F, 100F); //Ardite
        TConstructRegistry.addArrowMaterial(MaterialID.Manyullyn,   2.25F,  0.1F,  100F); //Manyullyn
        TConstructRegistry.addArrowMaterial(MaterialID.Copper,      2.7F,   0.5F,  100F); //Copper
        TConstructRegistry.addArrowMaterial(MaterialID.Bronze,      3.6F,   0.25F, 100F); //Bronze
        TConstructRegistry.addArrowMaterial(MaterialID.Alumite,     1.1F,   0.25F, 100F); //Alumite
        TConstructRegistry.addArrowMaterial(MaterialID.Steel,       3.6F,   0.25F, 100F); //Steel
        TConstructRegistry.addArrowMaterial(MaterialID.BlueSlime,   0.22F,  0.0F,  100F); //Blue Slime
        TConstructRegistry.addArrowMaterial(MaterialID.PigIron,     3.6F,   0.5F,  100F); //Pigiron

        // Arrow Shaft Materials: Material ID, crafting item, durability-medifier, mass, fragility
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(0, Items.stick, 1.0f, 1.0f, 0.15f, 0x866526)); // wood: reference material, 10% break chance
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(1, Items.bone,      0.95f, 1.2f, 0.05f, 0xede6bf)); // bone: heavier, but durable
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(2, Items.reeds,     1.5f,  0.8f, 0.66f, 0xc7ff87)); // reed: light, but less durable
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(3, Items.blaze_rod, 1.2f,  0.9f, 0.08f, 0xfff32d)); // blaze: tad lighter, tad more durable, fieryyyy
        // also add the tool rod variants, same stats
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(0, TinkerTools.toolRod, MaterialID.Wood, 1.0f, 1.0f, 0.15f, 0x866526)); // wood: reference material, 10% break chance
        TConstructRegistry.addCustomMaterial(ArrowShaftMaterial.createMaterial(1, TinkerTools.toolRod, MaterialID.Bone, 0.95f, 1.2f, 0.01f, 0xede6bf)); // bone: heavier, but durable

        // Arrow Fletching Materials: Material ID
    }

    private void addWeaponRecipies()
    {
        TConstructRegistry.addToolRecipe(shuriken, partShuriken, partShuriken, partShuriken, partShuriken);
        TConstructRegistry.addToolRecipe(throwingknife, TinkerTools.knifeBlade, TinkerTools.toolRod);
        TConstructRegistry.addToolRecipe(javelin, TinkerTools.arrowhead, TinkerTools.toughRod, TinkerTools.toughRod);

        // hax
        //ToolRecipe recipe = ToolBuilder.instance.recipeList.get(TinkerTools.shortbow);
        //ToolBuilder.instance.recipeList.remove(TinkerTools.shortbow);
        //ToolBuilder.instance.combos.remove(recipe);

        // todo
        TConstructRegistry.addToolRecipe(shortbow, partBowLimb, TinkerTools.bowstring, partBowLimb);
        TConstructRegistry.addToolRecipe(longbow, partBowLimb, TinkerTools.bowstring, partBowLimb, TinkerTools.largePlate);
        TConstructRegistry.addToolRecipe(crossbow, partCrossbowLimb, partCrossbowBody, TinkerTools.bowstring, TinkerTools.toughBinding);

        TConstructRegistry.addToolRecipe(arrowAmmo, TinkerTools.arrowhead, partArrowShaft, TinkerTools.fletching);
        TConstructRegistry.addToolRecipe(boltAmmo, partBolt, partBolt, TinkerTools.fletching);
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
        tool.setTagCompound(compound);

        TConstructRegistry.weaponryTab.init(tool);
    }
}
