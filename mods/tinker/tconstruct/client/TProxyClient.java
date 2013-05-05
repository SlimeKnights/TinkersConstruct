package mods.tinker.tconstruct.client;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.CastingBasinLogic;
import mods.tinker.tconstruct.blocks.logic.CastingTableLogic;
import mods.tinker.tconstruct.blocks.logic.FrypanLogic;
import mods.tinker.tconstruct.blocks.logic.GolemCoreLogic;
import mods.tinker.tconstruct.blocks.logic.PartCrafterLogic;
import mods.tinker.tconstruct.blocks.logic.PatternChestLogic;
import mods.tinker.tconstruct.blocks.logic.PatternShaperLogic;
import mods.tinker.tconstruct.blocks.logic.SmelteryLogic;
import mods.tinker.tconstruct.blocks.logic.ToolStationLogic;
import mods.tinker.tconstruct.client.block.CastingBasinSpecialRender;
import mods.tinker.tconstruct.client.block.CastingTableSpecialRenderer;
import mods.tinker.tconstruct.client.block.FluidRender;
import mods.tinker.tconstruct.client.block.FrypanRender;
import mods.tinker.tconstruct.client.block.GolemCoreRender;
import mods.tinker.tconstruct.client.block.GolemCoreSpecialRender;
import mods.tinker.tconstruct.client.block.OreberryRender;
import mods.tinker.tconstruct.client.block.SearedRender;
import mods.tinker.tconstruct.client.block.SmallFontRenderer;
import mods.tinker.tconstruct.client.block.SmelteryRender;
import mods.tinker.tconstruct.client.block.TableRender;
import mods.tinker.tconstruct.client.block.TankRender;
import mods.tinker.tconstruct.client.entity.CartRender;
import mods.tinker.tconstruct.client.entity.CrystalRender;
import mods.tinker.tconstruct.client.entity.FancyItemRender;
import mods.tinker.tconstruct.client.entity.GolemRender;
import mods.tinker.tconstruct.client.entity.SkylaRender;
import mods.tinker.tconstruct.client.entity.SlimeRender;
import mods.tinker.tconstruct.client.entity.projectile.DaggerRender;
import mods.tinker.tconstruct.client.entity.projectile.LaunchedItemRender;
import mods.tinker.tconstruct.client.gui.ArmorExtendedGui;
import mods.tinker.tconstruct.client.gui.FrypanGui;
import mods.tinker.tconstruct.client.gui.GuiManual;
import mods.tinker.tconstruct.client.gui.InventoryTab;
import mods.tinker.tconstruct.client.gui.PartCrafterGui;
import mods.tinker.tconstruct.client.gui.PatternChestGui;
import mods.tinker.tconstruct.client.gui.PatternShaperGui;
import mods.tinker.tconstruct.client.gui.SmelteryGui;
import mods.tinker.tconstruct.client.gui.ToolStationGui;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.common.TProxyCommon;
import mods.tinker.tconstruct.entity.BlueSlime;
import mods.tinker.tconstruct.entity.CartEntity;
import mods.tinker.tconstruct.entity.Crystal;
import mods.tinker.tconstruct.entity.FancyEntityItem;
import mods.tinker.tconstruct.entity.GolemBase;
import mods.tinker.tconstruct.entity.NitroCreeper;
import mods.tinker.tconstruct.entity.Skyla;
import mods.tinker.tconstruct.entity.projectile.DaggerEntity;
import mods.tinker.tconstruct.entity.projectile.LaunchedPotion;
import mods.tinker.tconstruct.items.tools.Dagger;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import mods.tinker.tconstruct.library.client.ToolGuiElement;
import mods.tinker.tconstruct.library.crafting.ToolBuilder;
import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.util.player.ArmorExtended;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import org.w3c.dom.Document;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class TProxyClient extends TProxyCommon
{
    public static SmallFontRenderer smallFontRenderer;
    public static Icon metalBall;
    public static Minecraft mc;
    public static ArmorExtended armorExtended = new ArmorExtended();
    public static RenderItem itemRenderer = new RenderItem();

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == stationGuiID)
            return new ToolStationGui(player.inventory, (ToolStationLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == partGuiID)
            return new PartCrafterGui(player.inventory, (PartCrafterLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == pchestGuiID)
            return new PatternChestGui(player.inventory, (PatternChestLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == frypanGuiID)
            return new FrypanGui(player.inventory, (FrypanLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == smelteryGuiID)
            return new SmelteryGui(player.inventory, (SmelteryLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == pshaperGuiID)
            return new PatternShaperGui(player.inventory, (PatternShaperLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == manualGuiID)
        {
            ItemStack stack = player.getCurrentEquippedItem();
            return new GuiManual(stack, TProxyClient.getManualFromStack(stack));
        }
        if (ID == armorGuiID)
        {
            //TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            TProxyClient.armorExtended.init(Minecraft.getMinecraft().thePlayer);
            return new ArmorExtendedGui(player.inventory, TProxyClient.armorExtended);
        }
        return null;
    }

    public static void openInventoryGui ()
    {
        if (mc == null)
            mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
        addTabsToInventory();
    }

    public static void addTabsToInventory ()
    {
        if (mc == null)
            mc = Minecraft.getMinecraft();
        if (mc.currentScreen.getClass() == GuiInventory.class || TControls.classMatches(mc.currentScreen, "micdoodle8.mods.galacticraft.core.client.gui.GCCoreGuiInventory"))
        {
            GuiInventory gui = (GuiInventory) mc.currentScreen;
            int cornerX = (gui.width - gui.xSize) / 2;
            int cornerY = (gui.height - gui.ySize) / 2;
            gui.buttonList.clear();

            InventoryTab repairButton = new InventoryTab(2, cornerX, cornerY - 28, new ItemStack(Block.workbench), 0);
            repairButton.enabled = false;
            gui.buttonList.add(repairButton);
            repairButton = new InventoryTab(3, cornerX + 28, cornerY - 28, new ItemStack(Item.plateDiamond), 1);
            gui.buttonList.add(repairButton);
        }
    }

    public void registerTickHandler ()
    {
        TickRegistry.registerTickHandler(new TClientTickHandler(), Side.CLIENT);
        //TickRegistry.registerTickHandler(new TCommonTickHandler(), Side.CLIENT);
    }

    /* Registers any rendering code. */
    public void registerRenderer ()
    {
        Minecraft mc = Minecraft.getMinecraft();
        smallFontRenderer = new SmallFontRenderer(mc.gameSettings, "/font/default.png", mc.renderEngine, false);
        RenderingRegistry.registerBlockHandler(new TableRender());
        RenderingRegistry.registerBlockHandler(new FrypanRender());
        RenderingRegistry.registerBlockHandler(new SmelteryRender());
        RenderingRegistry.registerBlockHandler(new TankRender());
        RenderingRegistry.registerBlockHandler(new SearedRender());
        RenderingRegistry.registerBlockHandler(new FluidRender());
        RenderingRegistry.registerBlockHandler(new GolemCoreRender());
        RenderingRegistry.registerBlockHandler(new OreberryRender());
        //RenderingRegistry.registerBlockHandler(new BrickRender());
        //RenderingRegistry.registerBlockHandler(new BallRepeaterRender());

        //Tools
        /*IItemRenderer render = new SuperCustomToolRenderer();
        for (ToolCore tool : TConstructRegistry.tools)
        {
        	MinecraftForgeClient.registerItemRenderer(tool.itemID, render);
        }*/

        //MinecraftForgeClient.registerItemRenderer(TContent.chisel.itemID, new ChiselRotator());

        //Special Renderers
        ClientRegistry.bindTileEntitySpecialRenderer(CastingTableLogic.class, new CastingTableSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(GolemCoreLogic.class, new GolemCoreSpecialRender());
        ClientRegistry.bindTileEntitySpecialRenderer(CastingBasinLogic.class, new CastingBasinSpecialRender());

        //Entities
        RenderingRegistry.registerEntityRenderingHandler(FancyEntityItem.class, new FancyItemRender());
        RenderingRegistry.registerEntityRenderingHandler(NitroCreeper.class, new RenderCreeper());
        RenderingRegistry.registerEntityRenderingHandler(BlueSlime.class, new SlimeRender(new ModelSlime(16), new ModelSlime(0), 0.25F));
        RenderingRegistry.registerEntityRenderingHandler(GolemBase.class, new GolemRender(0));

        RenderingRegistry.registerEntityRenderingHandler(CartEntity.class, new CartRender());
        RenderingRegistry.registerEntityRenderingHandler(DaggerEntity.class, new DaggerRender());
        RenderingRegistry.registerEntityRenderingHandler(Skyla.class, new SkylaRender());
        RenderingRegistry.registerEntityRenderingHandler(Crystal.class, new CrystalRender());
        RenderingRegistry.registerEntityRenderingHandler(LaunchedPotion.class, new LaunchedItemRender(Item.potion, 16384));
        //RenderingRegistry.registerEntityRenderingHandler(net.minecraft.entity.player.EntityPlayer.class, new PlayerArmorRender()); // <-- Works, woo!

        addRenderMappings();
        addToolButtons();
    }

    public static Document diary;
    public static Document volume1;
    public static Document volume2;
    public static Document smelter;

    public void readManuals ()
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        diary = readManual("/mods/tinker/resources/manuals/diary.xml", dbFactory);
        volume1 = readManual("/mods/tinker/resources/manuals/firstday.xml", dbFactory);
        volume2 = readManual("/mods/tinker/resources/manuals/materials.xml", dbFactory);
        smelter = readManual("/mods/tinker/resources/manuals/smeltery.xml", dbFactory);
        initManualIcons();
        initManualRecipes();
    }

    Document readManual (String location, DocumentBuilderFactory dbFactory)
    {
        try
        {
            InputStream stream = TConstruct.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void initManualIcons ()
    {
        TConstructClientRegistry.registerManualIcon("smelterybook", new ItemStack(TContent.manualBook, 1, 2));
        TConstructClientRegistry.registerManualIcon("smeltery", new ItemStack(TContent.smeltery));
        TConstructClientRegistry.registerManualIcon("blankcast", new ItemStack(TContent.blankPattern, 1, 1));
        TConstructClientRegistry.registerManualIcon("castingtable", new ItemStack(TContent.searedBlock));
        TConstructClientRegistry.registerManualIcon("liquidiron", new ItemStack(TContent.liquidMetalStill));
        TConstructClientRegistry.registerManualIcon("lavatank", new ItemStack(TContent.lavaTank));
        TConstructClientRegistry.registerManualIcon("searedbrick", new ItemStack(TContent.smeltery, 1, 2));
        TConstructClientRegistry.registerManualIcon("drain", new ItemStack(TContent.smeltery, 1, 1));
        TConstructClientRegistry.registerManualIcon("faucet", new ItemStack(TContent.searedBlock, 1, 1));
        TConstructClientRegistry.registerManualIcon("bronzeingot", new ItemStack(TContent.materials, 1, 13));
        TConstructClientRegistry.registerManualIcon("alubrassingot", new ItemStack(TContent.materials, 1, 14));
        TConstructClientRegistry.registerManualIcon("manyullyningot", new ItemStack(TContent.materials, 1, 5));
        TConstructClientRegistry.registerManualIcon("alumiteingot", new ItemStack(TContent.materials, 1, 15));
        TConstructClientRegistry.registerManualIcon("blankpattern", new ItemStack(TContent.blankPattern, 1, 0));
        TConstructClientRegistry.registerManualIcon("toolstation", new ItemStack(TContent.toolStationWood, 1, 0));
        TConstructClientRegistry.registerManualIcon("partcrafter", new ItemStack(TContent.toolStationWood, 1, 1));
        TConstructClientRegistry.registerManualIcon("patternchest", new ItemStack(TContent.toolStationWood, 1, 5));
        TConstructClientRegistry.registerManualIcon("stenciltable", new ItemStack(TContent.toolStationWood, 1, 10));
        TConstructClientRegistry.registerManualIcon("torch", new ItemStack(Block.torchWood));
        TConstructClientRegistry.registerManualIcon("sapling", new ItemStack(Block.sapling));
        TConstructClientRegistry.registerManualIcon("workbench", new ItemStack(Block.workbench));
        TConstructClientRegistry.registerManualIcon("coal", new ItemStack(Item.coal));

        TConstructClientRegistry.registerManualIcon("obsidianingot", new ItemStack(TContent.materials, 1, 18));
        TConstructClientRegistry.registerManualIcon("lavacrystal", new ItemStack(TContent.materials, 1, 7));

        //Tool Materials
        TConstructClientRegistry.registerManualIcon("woodplanks", new ItemStack(Block.planks));
        TConstructClientRegistry.registerManualIcon("stoneblock", new ItemStack(Block.stone));
        TConstructClientRegistry.registerManualIcon("ironingot", new ItemStack(Item.ingotIron));
        TConstructClientRegistry.registerManualIcon("flint", new ItemStack(Item.flint));
        TConstructClientRegistry.registerManualIcon("cactus", new ItemStack(Block.cactus));
        TConstructClientRegistry.registerManualIcon("bone", new ItemStack(Item.bone));
        TConstructClientRegistry.registerManualIcon("obsidian", new ItemStack(Block.obsidian));
        TConstructClientRegistry.registerManualIcon("netherrack", new ItemStack(Block.netherrack));
        TConstructClientRegistry.registerManualIcon("blueslimecrystal", new ItemStack(TContent.materials, 1, 17));
        TConstructClientRegistry.registerManualIcon("slimecrystal", new ItemStack(TContent.materials, 1, 1));
        TConstructClientRegistry.registerManualIcon("paperstack", new ItemStack(TContent.materials, 1, 0));
        TConstructClientRegistry.registerManualIcon("cobaltingot", new ItemStack(TContent.materials, 1, 3));
        TConstructClientRegistry.registerManualIcon("arditeingot", new ItemStack(TContent.materials, 1, 4));
        TConstructClientRegistry.registerManualIcon("copperingot", new ItemStack(TContent.materials, 1, 9));
        TConstructClientRegistry.registerManualIcon("steelingot", new ItemStack(TContent.materials, 1, 16));

        //Tool parts
        TConstructClientRegistry.registerManualIcon("pickhead", new ItemStack(TContent.pickaxeHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("shovelhead", new ItemStack(TContent.shovelHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("axehead", new ItemStack(TContent.axeHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("swordblade", new ItemStack(TContent.swordBlade, 1, 2));
        TConstructClientRegistry.registerManualIcon("pan", new ItemStack(TContent.frypanHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("board", new ItemStack(TContent.signHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("knifeblade", new ItemStack(TContent.knifeBlade, 1, 2));
        TConstructClientRegistry.registerManualIcon("chiselhead", new ItemStack(TContent.chiselHead, 1, 2));

        TConstructClientRegistry.registerManualIcon("toolrod", new ItemStack(Item.stick));

        TConstructClientRegistry.registerManualIcon("binding", new ItemStack(TContent.binding, 1, 4));
        TConstructClientRegistry.registerManualIcon("wideguard", new ItemStack(TContent.wideGuard, 1, 4));
        TConstructClientRegistry.registerManualIcon("handguard", new ItemStack(TContent.handGuard, 1, 4));
        TConstructClientRegistry.registerManualIcon("crossbar", new ItemStack(TContent.crossbar, 1, 4));

        //ToolIcons
        TConstructClientRegistry.registerManualIcon("pickicon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.pickaxeHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.binding, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("shovelicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.shovelHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), null, ""));
        TConstructClientRegistry.registerManualIcon("axeicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.axeHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), null, ""));
        TConstructClientRegistry.registerManualIcon("mattockicon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.axeHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.shovelHead, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("swordicon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.swordBlade, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.wideGuard, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("longswordicon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.swordBlade, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.handGuard, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("rapiericon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.swordBlade, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.crossbar, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("daggerIcon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.knifeBlade, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.crossbar, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("frypanicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.frypanHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), null, ""));
        TConstructClientRegistry.registerManualIcon("battlesignicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.signHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), null, ""));
    }

    public void initManualRecipes ()
    {
        ItemStack pattern = new ItemStack(TContent.blankPattern, 1, 0);

        ItemStack stick = new ItemStack(Item.stick, 1, 0);
        ItemStack plank = new ItemStack(Block.planks, 1, 0);
        ItemStack workbench = new ItemStack(Block.workbench, 1, 0);
        ItemStack chest = new ItemStack(Block.chest, 1, 0);
        ItemStack log = new ItemStack(Block.wood, 1, 0);
        ItemStack mossycobble = new ItemStack(Block.cobblestoneMossy);
        ItemStack netherrack = new ItemStack(Block.netherrack);

        ItemStack dirt = new ItemStack(Block.dirt, 1, 0);
        ItemStack sand = new ItemStack(Block.sand, 1, 0);
        ItemStack gravel = new ItemStack(Block.gravel, 1, 0);
        ItemStack clay = new ItemStack(Item.clay, 1, 0);
        ItemStack glass = new ItemStack(Block.glass, 1, 0);

        ItemStack grout = new ItemStack(TContent.craftedSoil, 2, 1);
        ItemStack searedbrick = new ItemStack(TContent.materials, 1, 2);

        ItemStack coal = new ItemStack(Item.coal);
        ItemStack paper = new ItemStack(Item.paper);
        ItemStack slimeball = new ItemStack(Item.slimeBall);
        ItemStack slimyMud = new ItemStack(TContent.craftedSoil);
        ItemStack blazerod = new ItemStack(Item.blazeRod);
        ItemStack firecharge = new ItemStack(Item.fireballCharge);
        ItemStack string = new ItemStack(Item.silk);

        ItemStack silkyCloth = new ItemStack(TContent.materials, 1, 25);

        //TConstruct recipes
        TConstructClientRegistry.registerManualSmallRecipe("blankpattern", pattern, plank, stick, stick, plank);
        TConstructClientRegistry.registerManualSmallRecipe("toolstation", new ItemStack(TContent.toolStationWood, 1, 0), null, pattern, null, workbench);
        TConstructClientRegistry.registerManualSmallRecipe("partcrafter", new ItemStack(TContent.toolStationWood, 1, 1), null, pattern, null, log);
        TConstructClientRegistry.registerManualSmallRecipe("patternchest", new ItemStack(TContent.toolStationWood, 1, 5), null, pattern, null, chest);
        TConstructClientRegistry.registerManualSmallRecipe("stenciltable", new ItemStack(TContent.toolStationWood, 1, 10), null, pattern, null, plank);

        TConstructClientRegistry.registerManualLargeRecipe("slimymud", slimyMud, null, slimeball, slimeball, null, slimeball, slimeball, null, dirt, sand);
        TConstructClientRegistry.registerManualFurnaceRecipe("slimecrystal", new ItemStack(TContent.materials, 1, 1), slimyMud);
        TConstructClientRegistry.registerManualSmallRecipe("paperstack", new ItemStack(TContent.materials, 1, 0), paper, paper, paper, paper);
        TConstructClientRegistry.registerManualLargeRecipe("mossball", new ItemStack(TContent.materials, 1, 6), mossycobble, mossycobble, mossycobble, mossycobble, mossycobble, mossycobble,
                mossycobble, mossycobble, mossycobble);
        TConstructClientRegistry.registerManualLargeRecipe("lavacrystal", new ItemStack(TContent.materials, 1, 7), blazerod, firecharge, blazerod, firecharge, new ItemStack(Item.bucketLava),
                firecharge, blazerod, firecharge, blazerod);
        TConstructClientRegistry.registerManualLargeRecipe("silkycloth", silkyCloth, string, string, string, string, new ItemStack(TContent.materials, 1, 24), string, string, string, string);
        TConstructClientRegistry.registerManualLargeRecipe("silkyjewel", new ItemStack(TContent.materials, 1, 26), null, silkyCloth, null, silkyCloth, new ItemStack(Item.emerald), silkyCloth, null,
                silkyCloth, null);

        TConstructClientRegistry.registerManualSmallRecipe("grout", grout, sand, gravel, null, clay);
        TConstructClientRegistry.registerManualFurnaceRecipe("searedbrick", searedbrick, grout);
        TConstructClientRegistry.registerManualSmallRecipe("searedbricks", new ItemStack(TContent.smeltery, 1, 2), searedbrick, searedbrick, searedbrick, searedbrick);
        TConstructClientRegistry.registerManualLargeRecipe("smelterycontroller", new ItemStack(TContent.smeltery, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, null, searedbrick,
                searedbrick, searedbrick, searedbrick);
        TConstructClientRegistry.registerManualLargeRecipe("smelterydrain", new ItemStack(TContent.smeltery, 1, 1), searedbrick, null, searedbrick, searedbrick, null, searedbrick, searedbrick, null,
                searedbrick);

        TConstructClientRegistry.registerManualLargeRecipe("smelterytank1", new ItemStack(TContent.lavaTank, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, glass, searedbrick,
                searedbrick, searedbrick, searedbrick);
        TConstructClientRegistry.registerManualLargeRecipe("smelterytank2", new ItemStack(TContent.lavaTank, 1, 1), searedbrick, glass, searedbrick, glass, glass, glass, searedbrick, glass,
                searedbrick);
        TConstructClientRegistry.registerManualLargeRecipe("smelterytank3", new ItemStack(TContent.lavaTank, 1, 2), searedbrick, glass, searedbrick, searedbrick, glass, searedbrick, searedbrick,
                glass, searedbrick);

        TConstructClientRegistry.registerManualLargeRecipe("smelterytable", new ItemStack(TContent.searedBlock, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, null, searedbrick,
                searedbrick, null, searedbrick);
        TConstructClientRegistry.registerManualLargeRecipe("smelteryfaucet", new ItemStack(TContent.searedBlock, 1, 1), searedbrick, null, searedbrick, null, searedbrick, null, null, null, null);
        TConstructClientRegistry.registerManualLargeRecipe("smelterybasin", new ItemStack(TContent.searedBlock, 1, 2), searedbrick, null, searedbrick, searedbrick, null, searedbrick, searedbrick,
                searedbrick, searedbrick);

        //Modifier recipes
        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TContent.pickaxeHead, 1, 6), new ItemStack(TContent.toolRod, 1, 2), new ItemStack(TContent.binding, 1, 6), "");
        TConstructClientRegistry.registerManualIcon("ironpick", ironpick);
        ItemStack ironlongsword = ToolBuilder.instance.buildTool(new ItemStack(TContent.swordBlade, 1, 6), new ItemStack(TContent.toolRod, 1, 2), new ItemStack(TContent.handGuard, 1, 10), "");
        TConstructClientRegistry.registerManualIcon("ironlongsword", ironlongsword);

        TConstructClientRegistry.registerManualModifier("diamondmod", ironpick.copy(), new ItemStack(Item.diamond));
        TConstructClientRegistry.registerManualModifier("emeraldmod", ironpick.copy(), new ItemStack(Item.emerald));
        TConstructClientRegistry.registerManualModifier("redstonemod", ironpick.copy(), new ItemStack(Item.redstone), new ItemStack(Block.blockRedstone));
        TConstructClientRegistry.registerManualModifier("lavacrystalmod", ironpick.copy(), new ItemStack(TContent.materials, 1, 7));
        TConstructClientRegistry.registerManualModifier("lapismod", ironpick.copy(), new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Block.blockLapis));
        TConstructClientRegistry.registerManualModifier("mossmod", ironpick.copy(), new ItemStack(TContent.materials, 1, 6));
        TConstructClientRegistry.registerManualModifier("quartzmod", ironlongsword.copy(), new ItemStack(Item.netherQuartz), new ItemStack(Block.blockNetherQuartz));
        TConstructClientRegistry.registerManualModifier("blazemod", ironlongsword.copy(), new ItemStack(Item.blazePowder));
        TConstructClientRegistry.registerManualModifier("necroticmod", ironlongsword.copy(), new ItemStack(TContent.materials, 1, 8));
        TConstructClientRegistry.registerManualModifier("silkymod", ironpick.copy(), new ItemStack(TContent.materials, 1, 26));
        TConstructClientRegistry.registerManualModifier("electricmod", ironpick.copy(), new ItemStack(Block.dirt), new ItemStack(Block.dirt));
        TConstructClientRegistry.registerManualModifier("tier1free", ironpick.copy(), new ItemStack(Item.diamond), new ItemStack(Block.blockGold));
        TConstructClientRegistry.registerManualModifier("tier2free", ironpick.copy(), new ItemStack(Item.netherStar));
    }

    public static Document getManualFromStack (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return volume1;
        case 1:
            return volume2;
        case 2:
            return smelter;
        case 3:
            return diary;
        }

        return null;
    }

    @Override
    public File getLocation ()
    {
        return Minecraft.getMinecraftDir();
    }

    static int[][] itemIcons = { new int[] { 0, 3, 0 }, //Repair
            new int[] { 1, 4, 0 }, //Pickaxe
            new int[] { 2, 5, 0 }, //Shovel
            new int[] { 2, 6, 0 }, //Axe
            //new int[] {2, 9, 0}, //Lumber Axe
            //new int[] {1, 7, 0}, //Ice Axe
            new int[] { 3, 8, 0 }, //Mattock
            new int[] { 1, 0, 1 }, //Broadsword
            new int[] { 1, 1, 1 }, //Longsword
            new int[] { 1, 2, 1 }, //Rapier
            new int[] { 1, 5, 1 }, //Dagger
            new int[] { 2, 3, 1 }, //Frying pan
            new int[] { 2, 4, 1 }, //Battlesign
            new int[] { 2, 6, 1 } //Chisel
    };

    static int[][] iconCoords = { new int[] { 0, 1, 2 }, new int[] { 13, 13, 13 }, //Repair
            new int[] { 0, 0, 1 }, new int[] { 2, 3, 3 }, //Pickaxe
            new int[] { 3, 0, 13 }, new int[] { 2, 3, 13 }, //Shovel
            new int[] { 2, 0, 13 }, new int[] { 2, 3, 13 }, //Axe
            //new int[] { 6, 0, 13 }, new int[] { 2, 3, 13 }, //Lumber Axe
            //new int[] { 0, 0, 5 }, new int[] { 2, 3, 3 }, //Ice Axe
            new int[] { 2, 0, 3 }, new int[] { 2, 3, 2 }, //Mattock
            new int[] { 1, 0, 2 }, new int[] { 2, 3, 3 }, //Broadsword
            new int[] { 1, 0, 3 }, new int[] { 2, 3, 3 }, //Longsword
            new int[] { 1, 0, 4 }, new int[] { 2, 3, 3 }, //Rapier
            new int[] { 7, 0, 4 }, new int[] { 2, 3, 3 }, //Dagger
            new int[] { 4, 0, 13 }, new int[] { 2, 3, 13 }, //Frying Pan
            new int[] { 5, 0, 13 }, new int[] { 2, 3, 13 }, //Battlesign
            new int[] { 7, 0, 13 }, new int[] { 3, 3, 13 } //Chisel
    };

    static String[] toolNames = { "Repair and Modification", "Pickaxe", "Shovel", "Axe",
            //"Lumber Axe",
            //"Ice Axe",
            "Mattock", "Broadsword", "Longsword", "Rapier", "Dagger", "Frying Pan", "Battlesign", "Chisel" };

    static String[] toolDescriptions = {
            "The main way to repair or change your tools. Place a tool and a material on the left to get started.",
            "The Pickaxe is a basic mining tool. It is effective on stone and ores.\n\nRequired parts:\n- Pickaxe Head\n- Tool Binding\n- Handle",
            "The Shovel is a basic digging tool. It is effective on dirt, sand, and snow.\n\nRequired parts:\n- Shovel Head\n- Handle",
            "The Axe is a basic chopping tool. It is effective on wood and leaves.\n\nRequired parts:\n- Axe Head\n- Handle",
            //"The Lumber Axe is a broad chopping tool. It harvests wood in a wide range and can fell entire trees.\n\nRequired parts:\n- Broad Axe Head\n- Handle",
            //"The Ice Axe is a tool for harvesting ice, mining, and attacking foes.\n\nSpecial Ability:\n- Wall Climb\nNatural Ability:\n- Ice Harvest\nDamage: Moderate\n\nRequired parts:\n- Pickaxe Head\n- Spike\n- Handle",
            "The Cutter Mattock is a versatile farming tool. It is effective on wood, dirt, and plants.\n\nSpecial Ability: Hoe\n\nRequired parts:\n- Axe Head\n- Shovel Head\n- Handle",
            "The Broadsword is a defensive weapon. Blocking cuts damage in half.\n\nSpecial Ability: Block\nDamage: Moderate\nDurability: High\n\nRequired parts:\n- Sword Blade\n- Wide Guard\n- Handle",
            "The Longsword is an offensive weapon. It is often used for charging into battle at full speed.\n\nNatural Ability:\n- Charge Boost\nSpecial Ability: Lunge\n\nDamage: Moderate\nDurability: Moderate",
            "The Rapier is a special weapon that relies on quick strikes to defeat foes.\n\nNatural Abilities:\n- Armor Pierce\n- Quick Strike\n- Charge Boost\nSpecial Ability:\n- Backpedal\n\nDamage: High\nDurability: Low",
            "The Dagger is a short blade that can be thrown.\n\nSpecial Ability:\n- Throw Item\n\nDamage: Low\nDurability: Moderate\n\nRequired parts:\n- Knife Blade\n- Crossbar\n- Handle",
            "The Frying is a heavy weapon that uses sheer weight to stun foes.\n\nSpecial Ability: Block\nNatural Ability: Heavy\nShift+rClick: Place Frying Pan\nDamage: Low\nDurability: High\n\nRequired parts:\n- Pan\n- Handle",
            //"The Battlesign is an advance in weapon technology worthy of Zombie Pigmen everywhere.\n\nSpecial Ability: Block\nShift-rClick: Place sign\nDamage: Low\nDurability: Average\n\nRequired parts:\n- Board\n- Handle"
            "The Battlesign is an advance in weapon technology worthy of Zombie Pigmen everywhere.\n\nSpecial Ability: Block\nDamage: Low\nDurability: Average\n\nRequired parts:\n- Sign Board\n- Handle",
            "The Chisel is a utility tool that carves shapes into blocks.\n\nCrafting Grid:\n- Shape Items\nSpecial Ability: Chisel\nDurability: Average\n\nRequired parts:\n- Chisel Head\n- Handle" };

    void addToolButtons ()
    {
        for (int i = 0; i < toolNames.length; i++)
        {
            addToolButton(itemIcons[i][0], itemIcons[i][1], itemIcons[i][2], iconCoords[i * 2], iconCoords[i * 2 + 1], toolNames[i], toolDescriptions[i]);
        }
    }

    void addToolButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body)
    {
        TConstructClientRegistry.addToolButton(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body));
    }

    void addRenderMappings ()
    {
        String[] partTypes = { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel",
                "blueslime" };
        String[] effectTypes = { "diamond", "emerald", "redstone", "glowstone", "moss", "ice", "lava", "blaze", "necrotic", "electric", "lapis", "quartz", "silk" };
        int[] validHarvestEffects = { 0, 1, 2, 4, 6, 7, 8, 9, 10, 11, 12 };
        int[] validWeaponEffects = { 0, 1, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        int[] validUtilityEffects = { 0, 1, 4, 9 };

        int[] validDaggerEffects = { 0, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

        for (int partIter = 0; partIter < partTypes.length; partIter++)
        {
            TConstructClientRegistry.addMaterialRenderMapping(partIter, "tinker", partTypes[partIter], true);
        }

        for (ToolCore tool : TConstructRegistry.getToolMapping())
        {
            if (tool instanceof Dagger)
            {
                for (int i = 0; i < validDaggerEffects.length; i++)
                {
                    TConstructClientRegistry.addEffectRenderMapping(validDaggerEffects[i], "tinker", effectTypes[validDaggerEffects[i]], true);
                }
                return;
            }
            List list = Arrays.asList(tool.toolCategories());
            if (list.contains("harvest"))
            {
                for (int i = 0; i < validHarvestEffects.length; i++)
                {
                    TConstructClientRegistry.addEffectRenderMapping(validHarvestEffects[i], "tinker", effectTypes[validHarvestEffects[i]], true);
                }
            }
            else if (list.contains("melee"))
            {
                for (int i = 0; i < validWeaponEffects.length; i++)
                {
                    TConstructClientRegistry.addEffectRenderMapping(validWeaponEffects[i], "tinker", effectTypes[validWeaponEffects[i]], true);
                }
            }
            else if (list.contains("utility"))
            {
                for (int i = 0; i < validUtilityEffects.length; i++)
                {
                    TConstructClientRegistry.addEffectRenderMapping(validUtilityEffects[i], "tinker", effectTypes[validUtilityEffects[i]], true);
                }
            }
            //return list.contains("throwing");
        }
        /*for (int effectIter = 0; effectIter < 3; effectIter++)
        {
            TConstructClientRegistry.addEffectRenderMapping(effectIter, "tinker", effectTypes[effectIter], true);
        }
        for (int effectIter = 4; effectIter < effectTypes.length; effectIter++)
        {
            TConstructClientRegistry.addEffectRenderMapping(effectIter, "tinker", effectTypes[effectIter], true);
        }*/
    }

    /* Keybindings */
    public static TControls controlInstance;

    public void registerKeys ()
    {
        controlInstance = new TControls();
        TickRegistry.registerTickHandler(controlInstance, Side.CLIENT);
        uploadKeyBindingsToGame(Minecraft.getMinecraft().gameSettings, controlInstance);
    }

    public void uploadKeyBindingsToGame (GameSettings settings, TKeyHandler keyhandler)
    {
        ArrayList<KeyBinding> harvestedBindings = Lists.newArrayList();
        for (KeyBinding kb : keyhandler.keyBindings)
        {
            harvestedBindings.add(kb);
        }

        KeyBinding[] modKeyBindings = harvestedBindings.toArray(new KeyBinding[harvestedBindings.size()]);
        KeyBinding[] allKeys = new KeyBinding[settings.keyBindings.length + modKeyBindings.length];
        System.arraycopy(settings.keyBindings, 0, allKeys, 0, settings.keyBindings.length);
        System.arraycopy(modKeyBindings, 0, allKeys, settings.keyBindings.length, modKeyBindings.length);
        settings.keyBindings = allKeys;
        settings.loadOptions();
    }

    public void spawnParticle (String particle, double xPos, double yPos, double zPos, double velX, double velY, double velZ)
    {
        this.doSpawnParticle(particle, xPos, yPos, zPos, velX, velY, velZ);
    }

    public EntityFX doSpawnParticle (String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        if (this.mc == null)
            this.mc = Minecraft.getMinecraft();

        if (this.mc.renderViewEntity != null && this.mc.effectRenderer != null)
        {
            int i = this.mc.gameSettings.particleSetting;

            if (i == 1 && mc.theWorld.rand.nextInt(3) == 0)
            {
                i = 2;
            }

            double d6 = this.mc.renderViewEntity.posX - par2;
            double d7 = this.mc.renderViewEntity.posY - par4;
            double d8 = this.mc.renderViewEntity.posZ - par6;
            EntityFX entityfx = null;

            if (par1Str.equals("hugeexplosion"))
            {
                this.mc.effectRenderer.addEffect(entityfx = new EntityHugeExplodeFX(mc.theWorld, par2, par4, par6, par8, par10, par12));
            }
            else if (par1Str.equals("largeexplode"))
            {
                this.mc.effectRenderer.addEffect(entityfx = new EntityLargeExplodeFX(mc.renderEngine, mc.theWorld, par2, par4, par6, par8, par10, par12));
            }
            else if (par1Str.equals("fireworksSpark"))
            {
                this.mc.effectRenderer.addEffect(entityfx = new EntityFireworkSparkFX(mc.theWorld, par2, par4, par6, par8, par10, par12, this.mc.effectRenderer));
            }

            if (entityfx != null)
            {
                return (EntityFX) entityfx;
            }
            else
            {
                double d9 = 16.0D;

                if (d6 * d6 + d7 * d7 + d8 * d8 > d9 * d9)
                {
                    return null;
                }
                else if (i > 1)
                {
                    return null;
                }
                else
                {
                    if (par1Str.equals("bubble"))
                    {
                        entityfx = new EntityBubbleFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("suspended"))
                    {
                        entityfx = new EntitySuspendFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("depthsuspend"))
                    {
                        entityfx = new EntityAuraFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("townaura"))
                    {
                        entityfx = new EntityAuraFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("crit"))
                    {
                        entityfx = new EntityCritFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("smoke"))
                    {
                        entityfx = new EntitySmokeFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("mobSpell"))
                    {
                        entityfx = new EntitySpellParticleFX(mc.theWorld, par2, par4, par6, 0.0D, 0.0D, 0.0D);
                        ((EntityFX) entityfx).setRBGColorF((float) par8, (float) par10, (float) par12);
                    }
                    else if (par1Str.equals("mobSpellAmbient"))
                    {
                        entityfx = new EntitySpellParticleFX(mc.theWorld, par2, par4, par6, 0.0D, 0.0D, 0.0D);
                        ((EntityFX) entityfx).setAlphaF(0.15F);
                        ((EntityFX) entityfx).setRBGColorF((float) par8, (float) par10, (float) par12);
                    }
                    else if (par1Str.equals("spell"))
                    {
                        entityfx = new EntitySpellParticleFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("instantSpell"))
                    {
                        entityfx = new EntitySpellParticleFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                        ((EntitySpellParticleFX) entityfx).setBaseSpellTextureIndex(144);
                    }
                    else if (par1Str.equals("witchMagic"))
                    {
                        entityfx = new EntitySpellParticleFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                        ((EntitySpellParticleFX) entityfx).setBaseSpellTextureIndex(144);
                        float f = mc.theWorld.rand.nextFloat() * 0.5F + 0.35F;
                        ((EntityFX) entityfx).setRBGColorF(1.0F * f, 0.0F * f, 1.0F * f);
                    }
                    else if (par1Str.equals("note"))
                    {
                        entityfx = new EntityNoteFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("portal"))
                    {
                        entityfx = new EntityPortalFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("enchantmenttable"))
                    {
                        entityfx = new EntityEnchantmentTableParticleFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("explode"))
                    {
                        entityfx = new EntityExplodeFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("flame"))
                    {
                        entityfx = new EntityFlameFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("lava"))
                    {
                        entityfx = new EntityLavaFX(mc.theWorld, par2, par4, par6);
                    }
                    else if (par1Str.equals("footstep"))
                    {
                        entityfx = new EntityFootStepFX(mc.renderEngine, mc.theWorld, par2, par4, par6);
                    }
                    else if (par1Str.equals("splash"))
                    {
                        entityfx = new EntitySplashFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("largesmoke"))
                    {
                        entityfx = new EntitySmokeFX(mc.theWorld, par2, par4, par6, par8, par10, par12, 2.5F);
                    }
                    else if (par1Str.equals("cloud"))
                    {
                        entityfx = new EntityCloudFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("reddust"))
                    {
                        entityfx = new EntityReddustFX(mc.theWorld, par2, par4, par6, (float) par8, (float) par10, (float) par12);
                    }
                    else if (par1Str.equals("snowballpoof"))
                    {
                        entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, Item.snowball, mc.renderEngine);
                    }
                    else if (par1Str.equals("dripWater"))
                    {
                        entityfx = new EntityDropParticleFX(mc.theWorld, par2, par4, par6, Material.water);
                    }
                    else if (par1Str.equals("dripLava"))
                    {
                        entityfx = new EntityDropParticleFX(mc.theWorld, par2, par4, par6, Material.lava);
                    }
                    else if (par1Str.equals("snowshovel"))
                    {
                        entityfx = new EntitySnowShovelFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("blueslime"))
                    {
                        entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, TContent.strangeFood, mc.renderEngine);
                    }
                    else if (par1Str.equals("heart"))
                    {
                        entityfx = new EntityHeartFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                    }
                    else if (par1Str.equals("angryVillager"))
                    {
                        entityfx = new EntityHeartFX(mc.theWorld, par2, par4 + 0.5D, par6, par8, par10, par12);
                        ((EntityFX) entityfx).setParticleTextureIndex(81);
                        ((EntityFX) entityfx).setRBGColorF(1.0F, 1.0F, 1.0F);
                    }
                    else if (par1Str.equals("happyVillager"))
                    {
                        entityfx = new EntityAuraFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
                        ((EntityFX) entityfx).setParticleTextureIndex(82);
                        ((EntityFX) entityfx).setRBGColorF(1.0F, 1.0F, 1.0F);
                    }
                    else if (par1Str.startsWith("iconcrack_"))
                    {
                        int j = Integer.parseInt(par1Str.substring(par1Str.indexOf("_") + 1));
                        entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, par8, par10, par12, Item.itemsList[j], mc.renderEngine);
                    }
                    else if (par1Str.startsWith("tilecrack_"))
                    {
                        String[] astring = par1Str.split("_", 3);
                        int k = Integer.parseInt(astring[1]);
                        int l = Integer.parseInt(astring[2]);
                        entityfx = (new EntityDiggingFX(mc.theWorld, par2, par4, par6, par8, par10, par12, Block.blocksList[k], 0, l, mc.renderEngine)).applyRenderColor(l);
                    }

                    if (entityfx != null)
                    {
                        this.mc.effectRenderer.addEffect((EntityFX) entityfx);
                    }

                    return (EntityFX) entityfx;
                }
            }
        }
        else
        {
            return null;
        }
    }
}
