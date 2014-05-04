package tconstruct.client;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mantle.client.MProxyClient;
import mantle.client.block.SmallFontRenderer;
import mantle.lib.client.MantleClientRegistry;
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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.RenderBlockFluid;

import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;

import tconstruct.TConstruct;
import tconstruct.blocks.logic.AdaptiveSmelteryLogic;
import tconstruct.blocks.logic.CastingBasinLogic;
import tconstruct.blocks.logic.CastingTableLogic;
import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.blocks.logic.FrypanLogic;
import tconstruct.blocks.logic.FurnaceLogic;
import tconstruct.blocks.logic.PartBuilderLogic;
import tconstruct.blocks.logic.PatternChestLogic;
import tconstruct.blocks.logic.SmelteryLogic;
import tconstruct.blocks.logic.StencilTableLogic;
import tconstruct.blocks.logic.TileEntityLandmine;
import tconstruct.blocks.logic.ToolForgeLogic;
import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.client.block.BarricadeRender;
import tconstruct.client.block.BlockRenderCastingChannel;
import tconstruct.client.block.CastingBasinSpecialRender;
import tconstruct.client.block.CastingTableSpecialRenderer;
import tconstruct.client.block.DryingRackRender;
import tconstruct.client.block.DryingRackSpecialRender;
import tconstruct.client.block.FrypanRender;
import tconstruct.client.block.OreberryRender;
import tconstruct.client.block.PaneConnectedRender;
import tconstruct.client.block.PaneRender;
import tconstruct.client.block.PunjiRender;
import tconstruct.client.block.RenderLandmine;
import tconstruct.client.block.SearedRender;
import tconstruct.client.block.SlimeChannelRender;
import tconstruct.client.block.SlimePadRender;
import tconstruct.client.block.SmelteryRender;
import tconstruct.client.block.TableForgeRender;
import tconstruct.client.block.TableRender;
import tconstruct.client.block.TankAirRender;
import tconstruct.client.block.TankRender;
import tconstruct.client.entity.CartRender;
import tconstruct.client.entity.CrystalRender;
import tconstruct.client.entity.FancyItemRender;
import tconstruct.client.entity.SlimeRender;
import tconstruct.client.entity.item.ExplosiveRender;
import tconstruct.client.entity.projectile.ArrowRenderCustom;
import tconstruct.client.entity.projectile.DaggerRenderCustom;
import tconstruct.client.entity.projectile.LaunchedItemRender;
import tconstruct.client.gui.AdaptiveSmelteryGui;
import tconstruct.client.gui.ArmorExtendedGui;
import tconstruct.client.gui.CraftingStationGui;
import tconstruct.client.gui.FrypanGui;
import tconstruct.client.gui.FurnaceGui;
import tconstruct.client.gui.GuiLandmine;
import tconstruct.client.gui.KnapsackGui;
import tconstruct.client.gui.PartCrafterGui;
import tconstruct.client.gui.PatternChestGui;
import tconstruct.client.gui.SmelteryGui;
import tconstruct.client.gui.StencilTableGui;
import tconstruct.client.gui.ToolForgeGui;
import tconstruct.client.gui.ToolStationGui;
import tconstruct.client.pages.BlockCastPage;
import tconstruct.client.pages.MaterialPage;
import tconstruct.client.pages.ModifierPage;
import tconstruct.client.pages.ToolPage;
import tconstruct.client.tabs.InventoryTabArmorExtended;
import tconstruct.client.tabs.InventoryTabKnapsack;
import tconstruct.client.tabs.InventoryTabVanilla;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.TProxyCommon;
import tconstruct.common.TRepo;
import tconstruct.entity.BlueSlime;
import tconstruct.entity.CartEntity;
import tconstruct.entity.Crystal;
import tconstruct.entity.FancyEntityItem;
import tconstruct.entity.item.EntityLandmineFirework;
import tconstruct.entity.item.ExplosivePrimed;
import tconstruct.entity.projectile.ArrowEntity;
import tconstruct.entity.projectile.DaggerEntity;
import tconstruct.entity.projectile.LaunchedPotion;
import tconstruct.inventory.ContainerLandmine;
import tconstruct.items.ManualInfo;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.client.ToolGuiElement;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.player.ArmorExtended;
import tconstruct.util.player.KnapsackInventory;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.VillagerRegistry;
import tconstruct.util.player.TPlayerStats;

public class TProxyClient extends TProxyCommon
{
    public static SmallFontRenderer smallFontRenderer;
    public static IIcon metalBall;
    public static Minecraft mc;
    public static RenderItem itemRenderer = new RenderItem();

    public static ArmorExtended armorExtended = new ArmorExtended();
    public static KnapsackInventory knapsack = new KnapsackInventory();

    public static WingModel wings = new WingModel();
    public static BootBump bootbump = new BootBump();
    public static GloveModel glove = new GloveModel();

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == toolStationID)
            return new ToolStationGui(player.inventory, (ToolStationLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == partBuilderID)
            return new PartCrafterGui(player.inventory, (PartBuilderLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == patternChestID)
            return new PatternChestGui(player.inventory, (PatternChestLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == frypanGuiID)
            return new FrypanGui(player.inventory, (FrypanLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == smelteryGuiID)
        {
            if (PHConstruct.newSmeltery)
                return new AdaptiveSmelteryGui(player.inventory, (AdaptiveSmelteryLogic) world.getTileEntity(x, y, z), world, x, y, z);
            else
                return new SmelteryGui(player.inventory, (SmelteryLogic) world.getTileEntity(x, y, z), world, x, y, z);
        }
        if (ID == stencilTableID)
            return new StencilTableGui(player.inventory, (StencilTableLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == toolForgeID)
            return new ToolForgeGui(player.inventory, (ToolForgeLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == landmineID)
            return new GuiLandmine(new ContainerLandmine(player, (TileEntityLandmine) world.getTileEntity(x, y, z)));
        if (ID == craftingStationID)
            return new CraftingStationGui(player.inventory, (CraftingStationLogic) world.getTileEntity(x, y, z), world, x, y, z);

        if (ID == furnaceID)
            return new FurnaceGui(player.inventory, (FurnaceLogic) world.getTileEntity(x, y, z));

        if (ID == inventoryGui)
        {
            GuiInventory inventory = new GuiInventory(player);
            TabRegistry.addTabsToInventory(inventory);
            return inventory;
        }
        if (ID == armorGuiID)
        {
            TProxyClient.armorExtended.init(Minecraft.getMinecraft().thePlayer);
            return new ArmorExtendedGui(player.inventory, TProxyClient.armorExtended);
        }
        if (ID == knapsackGuiID)
        {
            TProxyClient.knapsack.init(Minecraft.getMinecraft().thePlayer);
            return new KnapsackGui(player.inventory, TProxyClient.knapsack);
        }
        return null;
    }

    public static void renderStandardInvBlock (RenderBlocks renderblocks, Block block, int meta)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(0, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(1, meta)));// block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(2, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(3, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(4, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(5, meta)));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    public static void renderInvBlockFace (RenderBlocks renderblocks, Block block, int meta)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glScalef(2f, 2f, 2f);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glRotatef(45, 0, 1, 0);
        GL11.glRotatef(60, 1, 0, 0);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(0, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(1, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(2, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(3, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(4, meta)));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, getIcon(block.getIcon(5, meta)));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    public static boolean classMatches (Object paramObject, String paramString)
    {
        try
        {
            return paramObject.getClass().getName().equals(paramString);
        }
        catch (Exception localException)
        {
            return false;
        }
    }

    @Override
    public void registerTickHandler ()
    {
        super.registerTickHandler();
        FMLCommonHandler.instance().bus().register(new TClientTickHandler());
        new TClientTickHandler();
    }

    /* Registers any rendering code. */
    @Override
    public void registerRenderer ()
    {
        Minecraft mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(new TClientEvents());
        smallFontRenderer = new SmallFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
        RenderingRegistry.registerBlockHandler(new TableRender());
        RenderingRegistry.registerBlockHandler(new TableForgeRender());
        RenderingRegistry.registerBlockHandler(new FrypanRender());
        RenderingRegistry.registerBlockHandler(new TankRender());
        RenderingRegistry.registerBlockHandler(new TankAirRender());
        RenderingRegistry.registerBlockHandler(new SearedRender());
        RenderingRegistry.registerBlockHandler(new OreberryRender());
        RenderingRegistry.registerBlockHandler(new BarricadeRender());
        RenderingRegistry.registerBlockHandler(new DryingRackRender());
        RenderingRegistry.registerBlockHandler(new PaneRender());
        RenderingRegistry.registerBlockHandler(new PaneConnectedRender());
        RenderingRegistry.registerBlockHandler(new RenderLandmine());
        RenderingRegistry.registerBlockHandler(new PunjiRender());
        RenderingRegistry.registerBlockHandler(new RenderBlockFluid());
        RenderingRegistry.registerBlockHandler(new BlockRenderCastingChannel());
        RenderingRegistry.registerBlockHandler(new SlimeChannelRender());
        RenderingRegistry.registerBlockHandler(new SlimePadRender());

        if (!PHConstruct.newSmeltery)
            RenderingRegistry.registerBlockHandler(new SmelteryRender());

        // Special Renderers
        ClientRegistry.bindTileEntitySpecialRenderer(CastingTableLogic.class, new CastingTableSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CastingBasinLogic.class, new CastingBasinSpecialRender());
        ClientRegistry.bindTileEntitySpecialRenderer(DryingRackLogic.class, new DryingRackSpecialRender());
        // ClientRegistry.bindTileEntitySpecialRenderer(GolemCoreLogic.class,
        // new GolemCoreSpecialRender());

        // Entities
        RenderingRegistry.registerEntityRenderingHandler(FancyEntityItem.class, new FancyItemRender());
        RenderingRegistry.registerEntityRenderingHandler(BlueSlime.class, new SlimeRender(new ModelSlime(16), new ModelSlime(0), 0.25F));

        RenderingRegistry.registerEntityRenderingHandler(CartEntity.class, new CartRender());
        RenderingRegistry.registerEntityRenderingHandler(Crystal.class, new CrystalRender());
        RenderingRegistry.registerEntityRenderingHandler(LaunchedPotion.class, new LaunchedItemRender(Items.potionitem, 16384));
        RenderingRegistry.registerEntityRenderingHandler(DaggerEntity.class, new DaggerRenderCustom());
        RenderingRegistry.registerEntityRenderingHandler(ArrowEntity.class, new ArrowRenderCustom());
        RenderingRegistry.registerEntityRenderingHandler(EntityLandmineFirework.class, new RenderSnowball(Items.fireworks));
        RenderingRegistry.registerEntityRenderingHandler(ExplosivePrimed.class, new ExplosiveRender());
        // RenderingRegistry.registerEntityRenderingHandler(net.minecraft.entity.player.EntityPlayer.class,
        // new PlayerArmorRender()); // <-- Works, woo!

        MinecraftForgeClient.registerItemRenderer(TRepo.shortbow, new CustomBowRenderer());
        VillagerRegistry.instance().registerVillagerSkin(78943, new ResourceLocation("tinker", "textures/mob/villagertools.png"));

        ToolCoreRenderer renderer = new ToolCoreRenderer();
        MinecraftForgeClient.registerItemRenderer(TRepo.shortbow, renderer);
        MinecraftForgeClient.registerItemRenderer(TRepo.arrow, renderer);
        MinecraftForgeClient.registerItemRenderer(TRepo.dagger, renderer);

        addRenderMappings();
        addToolButtons();
    }

    public static Document diary;
    public static Document volume1;
    public static Document volume2;
    public static Document smelter;
    public static ManualInfo manualData;

    @Override
    public void readManuals ()
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        diary = readManual("/assets/tinker/manuals/diary.xml", dbFactory);
        volume1 = readManual("/assets/tinker/manuals/firstday.xml", dbFactory);
        volume2 = readManual("/assets/tinker/manuals/materials.xml", dbFactory);
        smelter = readManual("/assets/tinker/manuals/smeltery.xml", dbFactory);
        initManualIcons();
        initManualRecipes();
        initManualPages();
        manualData = new ManualInfo();
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
        MantleClientRegistry.registerManualIcon("smelterybook", new ItemStack(TRepo.manualBook, 1, 2));
        MantleClientRegistry.registerManualIcon("smeltery", new ItemStack(TRepo.smeltery));
        MantleClientRegistry.registerManualIcon("blankcast", new ItemStack(TRepo.blankPattern, 1, 1));
        MantleClientRegistry.registerManualIcon("castingtable", new ItemStack(TRepo.searedBlock));
        // MantleClientRegistry.registerManualIcon("liquidiron", new
        // ItemStack(TRepo.liquidMetalStill));
        MantleClientRegistry.registerManualIcon("lavatank", new ItemStack(TRepo.lavaTank));
        MantleClientRegistry.registerManualIcon("searedbrick", new ItemStack(TRepo.smeltery, 1, 2));
        MantleClientRegistry.registerManualIcon("drain", new ItemStack(TRepo.smeltery, 1, 1));
        MantleClientRegistry.registerManualIcon("faucet", new ItemStack(TRepo.searedBlock, 1, 1));
        MantleClientRegistry.registerManualIcon("bronzeingot", new ItemStack(TRepo.materials, 1, 13));
        MantleClientRegistry.registerManualIcon("alubrassingot", new ItemStack(TRepo.materials, 1, 14));
        MantleClientRegistry.registerManualIcon("manyullyningot", new ItemStack(TRepo.materials, 1, 5));
        MantleClientRegistry.registerManualIcon("alumiteingot", new ItemStack(TRepo.materials, 1, 15));
        MantleClientRegistry.registerManualIcon("blankpattern", new ItemStack(TRepo.blankPattern, 1, 0));
        MantleClientRegistry.registerManualIcon("toolstation", new ItemStack(TRepo.toolStationWood, 1, 0));
        MantleClientRegistry.registerManualIcon("partcrafter", new ItemStack(TRepo.toolStationWood, 1, 1));
        MantleClientRegistry.registerManualIcon("patternchest", new ItemStack(TRepo.toolStationWood, 1, 5));
        MantleClientRegistry.registerManualIcon("stenciltable", new ItemStack(TRepo.toolStationWood, 1, 10));
        MantleClientRegistry.registerManualIcon("torch", new ItemStack(Blocks.torch));
        MantleClientRegistry.registerManualIcon("sapling", new ItemStack(Blocks.sapling));
        MantleClientRegistry.registerManualIcon("workbench", new ItemStack(Blocks.crafting_table));
        MantleClientRegistry.registerManualIcon("coal", new ItemStack(Items.coal));

        MantleClientRegistry.registerManualIcon("obsidianingot", new ItemStack(TRepo.materials, 1, 18));
        MantleClientRegistry.registerManualIcon("lavacrystal", new ItemStack(TRepo.materials, 1, 7));

        // Tool Materials
        MantleClientRegistry.registerManualIcon("woodplanks", new ItemStack(Blocks.planks));
        MantleClientRegistry.registerManualIcon("stoneblock", new ItemStack(Blocks.stone));
        MantleClientRegistry.registerManualIcon("ironingot", new ItemStack(Items.iron_ingot));
        MantleClientRegistry.registerManualIcon("flint", new ItemStack(Items.flint));
        MantleClientRegistry.registerManualIcon("cactus", new ItemStack(Blocks.cactus));
        MantleClientRegistry.registerManualIcon("bone", new ItemStack(Items.bone));
        MantleClientRegistry.registerManualIcon("obsidian", new ItemStack(Blocks.obsidian));
        MantleClientRegistry.registerManualIcon("netherrack", new ItemStack(Blocks.netherrack));
        MantleClientRegistry.registerManualIcon("blueslimecrystal", new ItemStack(TRepo.materials, 1, 17));
        MantleClientRegistry.registerManualIcon("slimecrystal", new ItemStack(TRepo.materials, 1, 1));
        MantleClientRegistry.registerManualIcon("paperstack", new ItemStack(TRepo.materials, 1, 0));
        MantleClientRegistry.registerManualIcon("cobaltingot", new ItemStack(TRepo.materials, 1, 3));
        MantleClientRegistry.registerManualIcon("arditeingot", new ItemStack(TRepo.materials, 1, 4));
        MantleClientRegistry.registerManualIcon("copperingot", new ItemStack(TRepo.materials, 1, 9));
        MantleClientRegistry.registerManualIcon("steelingot", new ItemStack(TRepo.materials, 1, 16));
        MantleClientRegistry.registerManualIcon("pigironingot", new ItemStack(TRepo.materials, 1, 34));

        // Tool parts
        MantleClientRegistry.registerManualIcon("pickhead", new ItemStack(TRepo.pickaxeHead, 1, 2));
        MantleClientRegistry.registerManualIcon("shovelhead", new ItemStack(TRepo.shovelHead, 1, 2));
        MantleClientRegistry.registerManualIcon("axehead", new ItemStack(TRepo.hatchetHead, 1, 2));
        MantleClientRegistry.registerManualIcon("swordblade", new ItemStack(TRepo.swordBlade, 1, 2));
        MantleClientRegistry.registerManualIcon("pan", new ItemStack(TRepo.frypanHead, 1, 2));
        MantleClientRegistry.registerManualIcon("board", new ItemStack(TRepo.signHead, 1, 2));
        MantleClientRegistry.registerManualIcon("knifeblade", new ItemStack(TRepo.knifeBlade, 1, 2));
        MantleClientRegistry.registerManualIcon("chiselhead", new ItemStack(TRepo.chiselHead, 1, 2));

        MantleClientRegistry.registerManualIcon("hammerhead", new ItemStack(TRepo.hammerHead, 1, 2));
        MantleClientRegistry.registerManualIcon("excavatorhead", new ItemStack(TRepo.excavatorHead, 1, 2));
        MantleClientRegistry.registerManualIcon("scythehead", new ItemStack(TRepo.scytheBlade, 1, 2));
        MantleClientRegistry.registerManualIcon("broadaxehead", new ItemStack(TRepo.broadAxeHead, 1, 2));
        MantleClientRegistry.registerManualIcon("largeswordblade", new ItemStack(TRepo.largeSwordBlade, 1, 2));

        MantleClientRegistry.registerManualIcon("toolrod", new ItemStack(Items.stick));

        MantleClientRegistry.registerManualIcon("binding", new ItemStack(TRepo.binding, 1, 4));
        MantleClientRegistry.registerManualIcon("wideguard", new ItemStack(TRepo.wideGuard, 1, 4));
        MantleClientRegistry.registerManualIcon("handguard", new ItemStack(TRepo.handGuard, 1, 4));
        MantleClientRegistry.registerManualIcon("crossbar", new ItemStack(TRepo.crossbar, 1, 4));

        MantleClientRegistry.registerManualIcon("toughrod", new ItemStack(TRepo.toughRod, 1, 0));
        MantleClientRegistry.registerManualIcon("toughbinding", new ItemStack(TRepo.toughBinding, 1, 17));
        MantleClientRegistry.registerManualIcon("largeplate", new ItemStack(TRepo.largePlate, 1, 17));

        MantleClientRegistry.registerManualIcon("bowstring", new ItemStack(TRepo.bowstring, 1, 0));
        MantleClientRegistry.registerManualIcon("arrowhead", new ItemStack(TRepo.arrowhead, 1, 2));
        MantleClientRegistry.registerManualIcon("fletching", new ItemStack(TRepo.fletching, 1, 0));

        MantleClientRegistry.registerManualIcon("bloodbucket", new ItemStack(TRepo.buckets, 1, 16));
        MantleClientRegistry.registerManualIcon("emeraldbucket", new ItemStack(TRepo.buckets, 1, 15));
        MantleClientRegistry.registerManualIcon("gluebucket", new ItemStack(TRepo.buckets, 1, 25));
        MantleClientRegistry.registerManualIcon("slimebucket", new ItemStack(TRepo.buckets, 1, 24));
        MantleClientRegistry.registerManualIcon("enderbucket", new ItemStack(TRepo.buckets, 1, 23));

        // ToolIcons
        MantleClientRegistry.registerManualIcon("pickicon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.pickaxeHead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), new ItemStack(TRepo.binding, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("shovelicon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.shovelHead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("axeicon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.hatchetHead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("mattockicon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.hatchetHead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), new ItemStack(TRepo.shovelHead, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("swordicon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.swordBlade, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), new ItemStack(TRepo.wideGuard, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("longswordicon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.swordBlade, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), new ItemStack(TRepo.handGuard, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("rapiericon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.swordBlade, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), new ItemStack(TRepo.crossbar, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("daggerIcon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.knifeBlade, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), new ItemStack(TRepo.crossbar, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("frypanicon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.frypanHead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("battlesignicon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.signHead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("chiselicon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.chiselHead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("shortbowIcon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.toolRod, 1, 10), new ItemStack(TRepo.bowstring, 1, 0), new ItemStack(TRepo.toolRod, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("arrowIcon",
                ToolBuilder.instance.buildTool(new ItemStack(TRepo.arrowhead, 1, 10), new ItemStack(TRepo.toolRod, 1, 11), new ItemStack(TRepo.fletching, 1, 0), ""));

        MantleClientRegistry.registerManualIcon("hammericon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.hammerHead, 1, 10), new ItemStack(TRepo.toughRod, 1, 11), new ItemStack(
                TRepo.largePlate, 1, 12), new ItemStack(TRepo.largePlate, 8), ""));
        MantleClientRegistry.registerManualIcon("lumbericon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.broadAxeHead, 1, 10), new ItemStack(TRepo.toughRod, 1, 11), new ItemStack(
                TRepo.largePlate, 1, 12), new ItemStack(TRepo.toughBinding, 8), ""));
        MantleClientRegistry.registerManualIcon("excavatoricon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.excavatorHead, 1, 10), new ItemStack(TRepo.toughRod, 1, 11), new ItemStack(
                TRepo.largePlate, 1, 12), new ItemStack(TRepo.toughBinding, 8), ""));
        MantleClientRegistry.registerManualIcon("scytheicon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.scytheBlade, 1, 10), new ItemStack(TRepo.toughRod, 1, 11), new ItemStack(
                TRepo.toughBinding, 1, 12), new ItemStack(TRepo.toughRod, 8), ""));
        MantleClientRegistry.registerManualIcon("cleavericon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.largeSwordBlade, 1, 10), new ItemStack(TRepo.toughRod, 1, 11), new ItemStack(
                TRepo.largePlate, 1, 12), new ItemStack(TRepo.toughRod, 8), ""));
        MantleClientRegistry.registerManualIcon("battleaxeicon", ToolBuilder.instance.buildTool(new ItemStack(TRepo.broadAxeHead, 1, 10), new ItemStack(TRepo.toughRod, 1, 11), new ItemStack(
                TRepo.broadAxeHead, 1, 12), new ItemStack(TRepo.toughBinding, 8), ""));
    }

    public void initManualRecipes ()
    {
        ItemStack pattern = new ItemStack(TRepo.blankPattern, 1, 0);

        ItemStack stick = new ItemStack(Items.stick, 1, 0);
        ItemStack plank = new ItemStack(Blocks.planks, 1, 0);
        ItemStack plankSlab = new ItemStack(Blocks.wooden_slab, 1, 0);
        ItemStack workbench = new ItemStack(Blocks.crafting_table, 1, 0);
        ItemStack chest = new ItemStack(Blocks.chest, 1, 0);
        ItemStack log = new ItemStack(Blocks.log, 1, 0);
        ItemStack mossycobble = new ItemStack(Blocks.mossy_cobblestone);
        ItemStack netherrack = new ItemStack(Blocks.netherrack);

        ItemStack dirt = new ItemStack(Blocks.dirt, 1, 0);
        ItemStack sand = new ItemStack(Blocks.sand, 1, 0);
        ItemStack gravel = new ItemStack(Blocks.gravel, 1, 0);
        ItemStack clay = new ItemStack(Items.clay_ball, 1, 0);
        ItemStack glass = new ItemStack(Blocks.glass, 1, 0);
        ItemStack ironblock = new ItemStack(Blocks.iron_block, 1, 0);

        ItemStack grout = new ItemStack(TRepo.craftedSoil, 2, 1);
        ItemStack searedbrick = new ItemStack(TRepo.materials, 1, 2);
        ItemStack searedbrickBlock = new ItemStack(TRepo.smeltery, 1, 2);

        ItemStack coal = new ItemStack(Items.coal);
        ItemStack paper = new ItemStack(Items.paper);
        ItemStack slimeball = new ItemStack(Items.slime_ball);
        ItemStack slimyMud = new ItemStack(TRepo.craftedSoil);
        ItemStack blazerod = new ItemStack(Items.blaze_rod);
        ItemStack firecharge = new ItemStack(Items.fire_charge);
        ItemStack string = new ItemStack(Items.string);

        ItemStack silkyCloth = new ItemStack(TRepo.materials, 1, 25);

        ItemStack graveyardsoil = new ItemStack(TRepo.craftedSoil, 1, 3);
        ItemStack consecratedsoil = new ItemStack(TRepo.craftedSoil, 1, 4);

        // TConstruct recipes
        MantleClientRegistry.registerManualLargeRecipe("alternatebook", new ItemStack(Items.book), paper, paper, paper, string, pattern, pattern, null, null, null);
        MantleClientRegistry.registerManualSmallRecipe("patternbook1", new ItemStack(TRepo.manualBook, 1, 0), new ItemStack(Items.paper), pattern, null, null);
        MantleClientRegistry.registerManualSmallRecipe("patternbook2", new ItemStack(TRepo.manualBook, 1, 1), new ItemStack(TRepo.manualBook, 1, 0), null, null, null);
        MantleClientRegistry.registerManualSmallRecipe("patternbook3", new ItemStack(TRepo.manualBook, 1, 2), new ItemStack(TRepo.manualBook, 1, 1), null, null, null);
        MantleClientRegistry.registerManualSmallRecipe("blankpattern", pattern, plank, stick, stick, plank);
        MantleClientRegistry.registerManualSmallRecipe("toolstation", new ItemStack(TRepo.toolStationWood, 1, 0), null, pattern, null, workbench);
        MantleClientRegistry.registerManualSmallRecipe("partcrafter", new ItemStack(TRepo.toolStationWood, 1, 1), null, pattern, null, log);
        MantleClientRegistry.registerManualSmallRecipe("patternchest", new ItemStack(TRepo.toolStationWood, 1, 5), null, pattern, null, chest);
        MantleClientRegistry.registerManualSmallRecipe("stenciltable", new ItemStack(TRepo.toolStationWood, 1, 10), null, pattern, null, plank);
        MantleClientRegistry.registerManualSmallRecipe("slimechannel", new ItemStack(TRepo.slimeChannel, 1, 0), new ItemStack(TRepo.slimeGel, 1, 0), new ItemStack(Items.redstone), null, null);
        MantleClientRegistry.registerManualSmallRecipe("bouncepad", new ItemStack(TRepo.slimePad, 1, 0), new ItemStack(TRepo.slimeChannel), new ItemStack(Items.slime_ball), null, null);
        MantleClientRegistry.registerManualLargeRecipe("toolforge", new ItemStack(TRepo.toolForge, 1, 0), searedbrickBlock, searedbrickBlock, searedbrickBlock, ironblock, new ItemStack(
                TRepo.toolStationWood, 1, 0), ironblock, ironblock, null, ironblock);

        MantleClientRegistry.registerManualLargeRecipe("slimymud", slimyMud, null, slimeball, slimeball, null, slimeball, slimeball, null, dirt, sand);
        MantleClientRegistry.registerManualFurnaceRecipe("slimecrystal", new ItemStack(TRepo.materials, 1, 1), slimyMud);
        MantleClientRegistry.registerManualSmallRecipe("paperstack", new ItemStack(TRepo.materials, 1, 0), paper, paper, paper, paper);
        MantleClientRegistry.registerManualLargeRecipe("mossball", new ItemStack(TRepo.materials, 1, 6), mossycobble, mossycobble, mossycobble, mossycobble, mossycobble, mossycobble, mossycobble,
                mossycobble, mossycobble);
        MantleClientRegistry.registerManualLargeRecipe("lavacrystal", new ItemStack(TRepo.materials, 1, 7), blazerod, firecharge, blazerod, firecharge, new ItemStack(Items.lava_bucket), firecharge,
                blazerod, firecharge, blazerod);
        MantleClientRegistry.registerManualLargeRecipe("silkycloth", silkyCloth, string, string, string, string, new ItemStack(TRepo.materials, 1, 24), string, string, string, string);
        MantleClientRegistry.registerManualLargeRecipe("silkyjewel", new ItemStack(TRepo.materials, 1, 26), null, silkyCloth, null, silkyCloth, new ItemStack(Items.emerald), silkyCloth, null,
                silkyCloth, null);

        MantleClientRegistry.registerManualSmallRecipe("graveyardsoil", graveyardsoil, new ItemStack(Blocks.dirt), new ItemStack(Items.rotten_flesh), new ItemStack(Items.dye, 1, 15), null);
        MantleClientRegistry.registerManualFurnaceRecipe("consecratedsoil", consecratedsoil, graveyardsoil);

        MantleClientRegistry.registerManualSmallRecipe("grout", grout, sand, gravel, null, clay);
        MantleClientRegistry.registerManualFurnaceRecipe("searedbrick", searedbrick, grout);
        MantleClientRegistry.registerManualSmallRecipe("searedbricks", new ItemStack(TRepo.smeltery, 1, 2), searedbrick, searedbrick, searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterycontroller", new ItemStack(TRepo.smeltery, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, null, searedbrick, searedbrick,
                searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("dryingrack", new ItemStack(TRepo.dryingRack, 1, 0), null, null, null, plankSlab, plankSlab, plankSlab, null, null, null);

        MantleClientRegistry.registerManualLargeRecipe("smelterydrain", new ItemStack(TRepo.smeltery, 1, 1), searedbrick, null, searedbrick, searedbrick, null, searedbrick, searedbrick, null,
                searedbrick);

        MantleClientRegistry.registerManualLargeRecipe("smelterytank1", new ItemStack(TRepo.lavaTank, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, glass, searedbrick, searedbrick,
                searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterytank2", new ItemStack(TRepo.lavaTank, 1, 1), searedbrick, glass, searedbrick, glass, glass, glass, searedbrick, glass, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterytank3", new ItemStack(TRepo.lavaTank, 1, 2), searedbrick, glass, searedbrick, searedbrick, glass, searedbrick, searedbrick, glass,
                searedbrick);

        MantleClientRegistry.registerManualLargeRecipe("smelterytable", new ItemStack(TRepo.searedBlock, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, null, searedbrick, searedbrick,
                null, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelteryfaucet", new ItemStack(TRepo.searedBlock, 1, 1), searedbrick, null, searedbrick, null, searedbrick, null, null, null, null);
        MantleClientRegistry.registerManualLargeRecipe("castingchannel", new ItemStack(TRepo.castingChannel), null, null, null, searedbrick, null, searedbrick, searedbrick, searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterybasin", new ItemStack(TRepo.searedBlock, 1, 2), searedbrick, null, searedbrick, searedbrick, null, searedbrick, searedbrick,
                searedbrick, searedbrick);

        // Traps
        ItemStack reed = new ItemStack(Items.sugar);
        MantleClientRegistry.registerManualLargeRecipe("punji", new ItemStack(TRepo.punji), reed, null, reed, null, reed, null, reed, null, reed);
        MantleClientRegistry.registerManualSmallRecipe("barricade", new ItemStack(TRepo.barricadeOak), null, log, null, log);

        // Machines
        ItemStack alubrassIngot = new ItemStack(TRepo.materials, 1, 14);
        ItemStack bronzeIngot = new ItemStack(TRepo.materials, 1, 13);
        ItemStack blankCast = new ItemStack(TRepo.blankPattern, 1, 1);
        ItemStack redstone = new ItemStack(Items.redstone);

        // Modifier recipes
        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TRepo.pickaxeHead, 1, 6), new ItemStack(TRepo.toolRod, 1, 2), new ItemStack(TRepo.binding, 1, 6), "");
        MantleClientRegistry.registerManualIcon("ironpick", ironpick);
        ItemStack ironlongsword = ToolBuilder.instance.buildTool(new ItemStack(TRepo.swordBlade, 1, 6), new ItemStack(TRepo.toolRod, 1, 2), new ItemStack(TRepo.handGuard, 1, 10), "");
        MantleClientRegistry.registerManualIcon("ironlongsword", ironlongsword);

        TConstructClientRegistry.registerManualModifier("diamondmod", ironpick.copy(), new ItemStack(Items.diamond));
        TConstructClientRegistry.registerManualModifier("emeraldmod", ironpick.copy(), new ItemStack(Items.emerald));
        TConstructClientRegistry.registerManualModifier("redstonemod", ironpick.copy(), new ItemStack(Items.redstone), new ItemStack(Blocks.redstone_block));
        TConstructClientRegistry.registerManualModifier("lavacrystalmod", ironpick.copy(), new ItemStack(TRepo.materials, 1, 7));
        TConstructClientRegistry.registerManualModifier("lapismod", ironpick.copy(), new ItemStack(Items.dye, 1, 4), new ItemStack(Blocks.lapis_block));
        TConstructClientRegistry.registerManualModifier("mossmod", ironpick.copy(), new ItemStack(TRepo.materials, 1, 6));
        TConstructClientRegistry.registerManualModifier("quartzmod", ironlongsword.copy(), new ItemStack(Items.quartz), new ItemStack(Blocks.quartz_block));
        TConstructClientRegistry.registerManualModifier("blazemod", ironlongsword.copy(), new ItemStack(Items.blaze_powder));
        TConstructClientRegistry.registerManualModifier("necroticmod", ironlongsword.copy(), new ItemStack(TRepo.materials, 1, 8));
        TConstructClientRegistry.registerManualModifier("silkymod", ironpick.copy(), new ItemStack(TRepo.materials, 1, 26));
        TConstructClientRegistry.registerManualModifier("reinforcedmod", ironpick.copy(), new ItemStack(TRepo.largePlate, 1, 6));

        TConstructClientRegistry.registerManualModifier("pistonmod", ironlongsword.copy(), new ItemStack(Blocks.piston));
        TConstructClientRegistry.registerManualModifier("beheadingmod", ironlongsword.copy(), new ItemStack(Items.ender_pearl), new ItemStack(Blocks.obsidian));
        TConstructClientRegistry.registerManualModifier("spidermod", ironlongsword.copy(), new ItemStack(Items.fermented_spider_eye));
        TConstructClientRegistry.registerManualModifier("smitemod", ironlongsword.copy(), new ItemStack(TRepo.craftedSoil, 1, 4));

        TConstructClientRegistry.registerManualModifier("fluxmod", ironpick.copy(), new ItemStack(Blocks.dirt));
        TConstructClientRegistry.registerManualModifier("fluxmod2", ironpick.copy(), new ItemStack(Blocks.dirt));

        TConstructClientRegistry.registerManualModifier("tier1free", ironpick.copy(), new ItemStack(Items.diamond), new ItemStack(Blocks.gold_block));
        TConstructClientRegistry.registerManualModifier("tier1.5free", ironpick.copy(), new ItemStack(Items.golden_apple, 1, 1), new ItemStack(Blocks.diamond_block));
        TConstructClientRegistry.registerManualModifier("tier2free", ironpick.copy(), new ItemStack(Items.nether_star));
        TConstructClientRegistry.registerManualModifier("creativefree", ironpick.copy(), new ItemStack(TRepo.creativeModifier));

        TConstructClientRegistry.registerManualSmeltery("brownstone", new ItemStack(TRepo.speedBlock), new ItemStack(TRepo.moltenTin, 1), new ItemStack(Blocks.gravel));
        TConstructClientRegistry.registerManualSmeltery("clearglass", new ItemStack(TRepo.clearGlass), new ItemStack(TRepo.moltenGlass, 1), null);
        TConstructClientRegistry.registerManualSmeltery("searedstone", new ItemStack(TRepo.smeltery, 1, 4), new ItemStack(TRepo.moltenStone, 1), null);
        TConstructClientRegistry.registerManualSmeltery("endstone", new ItemStack(Blocks.end_stone), new ItemStack(TRepo.moltenEnder, 1), new ItemStack(Blocks.obsidian));
        TConstructClientRegistry.registerManualSmeltery("glueball", new ItemStack(TRepo.materials, 1, 36), new ItemStack(TRepo.glueFluidBlock, 1), null);

    }

    void initManualPages ()
    {
        MProxyClient.registerManualPage("materialstats", MaterialPage.class);
        MProxyClient.registerManualPage("toolpage", ToolPage.class);
        MProxyClient.registerManualPage("modifier", ModifierPage.class);
        MProxyClient.registerManualPage("blockcast", BlockCastPage.class);

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

    static int[][] itemIcons = { new int[] { 0, 3, 0 }, // Repair
            new int[] { 1, 4, 0 }, // Pickaxe
            new int[] { 2, 5, 0 }, // Shovel
            new int[] { 2, 6, 0 }, // Axe
            // new int[] {2, 9, 0}, //Lumber Axe
            // new int[] {1, 7, 0}, //Ice Axe
            new int[] { 3, 8, 0 }, // Mattock
            new int[] { 1, 0, 1 }, // Broadsword
            new int[] { 1, 1, 1 }, // Longsword
            new int[] { 1, 2, 1 }, // Rapier
            new int[] { 1, 5, 1 }, // Dagger
            new int[] { 2, 3, 1 }, // Frying pan
            new int[] { 2, 4, 1 }, // Battlesign
            new int[] { 2, 6, 1 } // Chisel
    };

    static int[][] iconCoords = { new int[] { 0, 1, 2, 13 }, new int[] { 13, 13, 13, 13 }, // Repair
            new int[] { 0, 0, 1, 13 }, new int[] { 2, 3, 3, 13 }, // Pickaxe
            new int[] { 3, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Shovel
            new int[] { 2, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Axe
            // new int[] { 6, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, //Lumber
            // Axe
            // new int[] { 0, 0, 5, 13 }, new int[] { 2, 3, 3, 13 }, //Ice Axe
            new int[] { 2, 0, 3, 13 }, new int[] { 2, 3, 2, 13 }, // Mattock
            new int[] { 1, 0, 2, 13 }, new int[] { 2, 3, 3, 13 }, // Broadsword
            new int[] { 1, 0, 3, 13 }, new int[] { 2, 3, 3, 13 }, // Longsword
            new int[] { 1, 0, 4, 13 }, new int[] { 2, 3, 3, 13 }, // Rapier
            new int[] { 7, 0, 4, 13 }, new int[] { 2, 3, 3, 13 }, // Dagger
            new int[] { 4, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Frying
                                                                    // Pan
            new int[] { 5, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Battlesign
            new int[] { 7, 0, 13, 13 }, new int[] { 3, 3, 13, 13 } // Chisel
    };

    static String[] toolNames = { "Repair and Modification", "Pickaxe", "Shovel", "Hatchet",
            // "Lumber Axe",
            // "Ice Axe",
            "Mattock", "Broadsword", "Longsword", "Rapier", "Dagger", "Frying Pan", "Battlesign", "Chisel" };

    static String[] toolDescriptions = {
            "The main way to repair or change your tools. Place a tool and a material on the left to get started.",
            "The Pickaxe is a precise mining tool. It is effective on stone and ores.\n\nRequired parts:\n- Pickaxe Head\n- Tool Binding\n- Handle",
            "The Shovel is a precise digging tool. It is effective on dirt, sand, and snow.\n\nRequired parts:\n- Shovel Head\n- Handle",
            "The Hatchet is a basic chopping tool. It is effective on wood and leaves.\n\nRequired parts:\n- Axe Head\n- Handle",
            // "The Lumber Axe is a broad chopping tool. It harvests wood in a wide range and can fell entire trees.\n\nRequired parts:\n- Broad Axe Head\n- Handle",
            // "The Ice Axe is a tool for harvesting ice, mining, and attacking foes.\n\nSpecial Ability:\n- Wall Climb\nNatural Ability:\n- Ice Harvest\nDamage: Moderate\n\nRequired parts:\n- Pickaxe Head\n- Spike\n- Handle",
            "The Cutter Mattock is a versatile farming tool. It is effective on wood, dirt, and plants.\n\nSpecial Ability: Hoe\n\nRequired parts:\n- Axe Head\n- Shovel Head\n- Handle",
            "The Broadsword is a defensive weapon. Blocking cuts damage in half.\n\nSpecial Ability: Block\nDamage: Moderate\nDurability: High\n\nRequired parts:\n- Sword Blade\n- Wide Guard\n- Handle",
            "The Longsword is an offensive weapon. It is often used for charging into battle at full speed.\n\nNatural Ability:\n- Charge Boost\nSpecial Ability: Lunge\n\nDamage: Moderate\nDurability: Moderate",
            "The Rapier is a special weapon that relies on quick strikes to defeat foes.\n\nNatural Abilities:\n- Armor Pierce\n- Quick Strike\n- Charge Boost\nSpecial Ability:\n- Backpedal\n\nDamage: Low\nDurability: Low",
            "The Dagger is a short blade that can be thrown.\n\nSpecial Ability:\n- Throw Item\n\nDamage: Low\nDurability: Moderate\n\nRequired parts:\n- Knife Blade\n- Crossbar\n- Handle",
            "The Frying is a heavy weapon that uses sheer weight to stun foes.\n\nSpecial Ability: Block\nNatural Ability: Heavy\nShift+rClick: Place Frying Pan\nDamage: Low\nDurability: High\n\nRequired parts:\n- Pan\n- Handle",
            // "The Battlesign is an advance in weapon technology worthy of Zombie Pigmen everywhere.\n\nSpecial Ability: Block\nShift-rClick: Place sign\nDamage: Low\nDurability: Average\n\nRequired parts:\n- Board\n- Handle"
            "The Battlesign is an advance in weapon technology worthy of Zombie Pigmen everywhere.\n\nSpecial Ability:\nDamage Reflector\n\nNatural Ability: Writable\n\nDamage: Low\nDurability: Average",
            "The Chisel is a utility tool that carves shapes into blocks.\n\nCrafting Grid:\n- Shape Items\nSpecial Ability: Chisel\nDurability: Average\n\nRequired parts:\n- Chisel Head\n- Handle" };

    void addToolButtons ()
    {
        for (int i = 0; i < toolNames.length; i++)
        {
            addToolButton(itemIcons[i][0], itemIcons[i][1], itemIcons[i][2], iconCoords[i * 2], iconCoords[i * 2 + 1], toolNames[i], toolDescriptions[i]);
        }

        addToolButton(3, 9, 1, new int[] { 0, 10, 0, 13 }, new int[] { 3, 3, 3, 13 }, "Shortbow",
                "The Shortbow is a ranged weapon. It fires arrows quickly and precisely at its foes.\n\nDraw Speed: Quick\n\nRequired parts:\n- Tool Rod\n- Bowstring\n- Tool Rod");
        addToolButton(7, 10, 1, new int[] { 11, 0, 12, 13 }, new int[] { 3, 3, 3, 13 }, "Arrow",
                "Arrows are projectiles usually fired from bows.\n\nRequired parts:\n- Arrowhead\n- Tool Rod\n- Fletching");
        addTierTwoButton(6, 13, 0, new int[] { 11, 8, 9, 9 }, new int[] { 2, 3, 2, 2 }, "Hammer",
                "The Hammer is a broad mining tool. It harvests blocks in a wide range and is effective against undead.\n\nNatural Abilities:\nArea of Effect\n- (3x3)\n- Smite\n\nDurability: High");
        addTierTwoButton(5, 11, 0, new int[] { 6, 8, 9, 9 }, new int[] { 2, 3, 2, 3 }, "Lumber Axe",
                "The Lumber Axe is a broad chopping tool. It can fell entire trees or gather wood in a wide range.\n\nNatural Abilities:\nArea of Effect\n- Fell Trees\n- (3x3x3)\n\nDurability: Average");
        addTierTwoButton(5, 12, 0, new int[] { 10, 8, 9, 9 }, new int[] { 2, 3, 2, 3 }, "Excavator",
                "The Excavator is a broad digging tool. It harvests soil and snow in a wide range.\n\nNatural Ability:\n- Area of Effect\n- (3x3)\n\nDurability: Average");
        addTierTwoButton(4, 10, 0, new int[] { 8, 8, 9, 8 }, new int[] { 2, 3, 3, 3 }, "Scythe",
                "The Scythe is a broad reaping tool. It is effective on plants and attacks enemies in a wide range.\n\nNatural Ability:\nArea of Effect\n- (3x3x3)\n\nDurability: Average\nDamage: Low, AoE");
        addTierTwoButton(5, 7, 1, new int[] { 6, 8, 9, 8 }, new int[] { 3, 3, 2, 3 }, "Cleaver",
                "The Cleaver is a heavy defensive weapon. It has powerful strikes, but is difficult to wield.\n\nSpecial Ability: Block\nNatural Ability:\n- Beheading\n\nDamage: High\nDurability: Average");
        addTierTwoButton(5, 8, 1, new int[] { 6, 8, 6, 9 }, new int[] { 2, 3, 2, 3 }, "Battleaxe",
                "The Battleaxe is a heavy offensive weapon. It is capable of bringing down small trees and can send foes flying.\n\nSpecial Ability: Block\nNatural Abilities:"
                        + "\n- Knockback\n- Area of Effect\n- (1x9)\n\nDamage: Average\nDurability: Average");
    }

    void addToolButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body)
    {
        TConstructClientRegistry.addToolButton(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body, "tinker", "textures/gui/icons.png"));
    }

    void addTierTwoButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body)
    {
        TConstructClientRegistry.addTierTwoButton(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body, "tinker", "textures/gui/icons.png"));
    }

    void addRenderMappings ()
    {
        String[] partTypes = { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel",
                "blueslime", "pigiron" };
        String[] effectTypes = { "diamond", "emerald", "redstone", "piston", "moss", "ice", "lava", "blaze", "necrotic", "flux", "lapis", "quartz", "silk", "beheading", "smite", "spider",
                "reinforced", "flux" };
        int[] universalEffects = { 0, 1, 4, 9, 16 };
        int[] weaponEffects = { 3, 5, 7, 13, 14, 15 };
        int[] harvestEffects = { 2 };
        int[] nonUtility = { 6, 8, 10, 11, 12 };

        for (int partIter = 0; partIter < partTypes.length; partIter++)
        {
            TConstructClientRegistry.addMaterialRenderMapping(partIter, "tinker", partTypes[partIter], true);
        }

        for (ToolCore tool : TConstructRegistry.getToolMapping())
        {
            for (int i = 0; i < effectTypes.length; i++)
            {
                TConstructClientRegistry.addEffectRenderMapping(tool, i, "tinker", effectTypes[i], true);
            }
        }

        String[] bowstringTypes = { "string", "magicfabric", "flamestring" };
        for (int bowIter = 0; bowIter < bowstringTypes.length; bowIter++)
        {
            TConstructClientRegistry.addAlternateMaterialRenderMapping(TRepo.shortbow, bowIter, "tinker", bowstringTypes[bowIter], true);
        }

        String[] fletching = { "feather", "leaf", "slime", "blueslime" };
        for (int arrowIter = 0; arrowIter < fletching.length; arrowIter++)
        {
            TConstructClientRegistry.addAlternateMaterialRenderMapping(TRepo.arrow, arrowIter, "tinker", fletching[arrowIter], true);
        }
    }

    /* Keybindings */
    public static TControls controlInstance;

    @Override
    public void registerKeys ()
    {
        controlInstance = new TControls();
        uploadKeyBindingsToGame(Minecraft.getMinecraft().gameSettings, controlInstance);

        TabRegistry.registerTab(new InventoryTabVanilla());
        TabRegistry.registerTab(new InventoryTabArmorExtended());
        TabRegistry.registerTab(new InventoryTabKnapsack());
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

    @Override
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
                        entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, Items.snowball);
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
                        entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, TRepo.strangeFood);
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

    @Override
    public void postInit ()
    {
        // MinecraftForgeClient.registerItemRenderer(TRepo.armorPattern.itemID,
        // new RenderArmorCast());
    }

    public void recalculateHealth ()
    {
        armorExtended.recalculateAttributes(mc.thePlayer, TPlayerStats.get(mc.thePlayer));
    }

    private static IIcon getIcon (IIcon icon)
    {
        if (icon != null)
            return icon;
        return ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
    }

}
