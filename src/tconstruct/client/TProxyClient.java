package tconstruct.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;
import java.io.InputStream;
import java.util.*;
import javax.xml.parsers.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.settings.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.RenderBlockFluid;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import tconstruct.TConstruct;
import tconstruct.blocks.SlimeExplosive;
import tconstruct.blocks.logic.*;
import tconstruct.client.armor.RenderArmorCast;
import tconstruct.client.block.*;
import tconstruct.client.entity.*;
import tconstruct.client.entity.item.ExplosiveRender;
import tconstruct.client.entity.projectile.*;
import tconstruct.client.gui.*;
import tconstruct.client.pages.*;
import tconstruct.client.tabs.*;
import tconstruct.common.*;
import tconstruct.entity.*;
import tconstruct.entity.item.*;
import tconstruct.entity.item.*;
import tconstruct.entity.projectile.*;
import tconstruct.inventory.ContainerLandmine;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.*;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.player.*;

public class TProxyClient extends TProxyCommon
{
    public static SmallFontRenderer smallFontRenderer;
    public static Icon metalBall;
    public static Minecraft mc;
    public static RenderItem itemRenderer = new RenderItem();

    public static ArmorExtended armorExtended = new ArmorExtended();
    public static KnapsackInventory knapsack = new KnapsackInventory();

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == toolStationID)
            return new ToolStationGui(player.inventory, (ToolStationLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == partBuilderID)
            return new PartCrafterGui(player.inventory, (PartBuilderLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == patternChestID)
            return new PatternChestGui(player.inventory, (PatternChestLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == frypanGuiID)
            return new FrypanGui(player.inventory, (FrypanLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == smelteryGuiID)
            return new AdaptiveSmelteryGui(player.inventory, (AdaptiveSmelteryLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == stencilTableID)
            return new StencilTableGui(player.inventory, (StencilTableLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == toolForgeID)
            return new ToolForgeGui(player.inventory, (ToolForgeLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == drawbridgeID)
            return new DrawbridgeGui(player.inventory, (DrawbridgeLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == landmineID)
            return new GuiLandmine(new ContainerLandmine(player, (TileEntityLandmine) world.getBlockTileEntity(x, y, z)));
        if (ID == craftingStationID)
            return new CraftingStationGui(player.inventory, (CraftingStationLogic) world.getBlockTileEntity(x, y, z), x, y, z);
        if (ID == advDrawbridgeID)
        	return new AdvDrawbridgeGui(player, (AdvancedDrawbridgeLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);

        if (ID == manualGuiID)
        {
            ItemStack stack = player.getCurrentEquippedItem();
            return new GuiManual(stack, TProxyClient.getManualFromStack(stack));
        }
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
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
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
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
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

    public void registerTickHandler ()
    {
        TickRegistry.registerTickHandler(new TClientTickHandler(), Side.CLIENT);
        // TickRegistry.registerTickHandler(new TimeTicker(), Side.CLIENT);
        // TickRegistry.registerTickHandler(new TCommonTickHandler(),
        // Side.CLIENT);
    }

    /* Registers any rendering code. */
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
        RenderingRegistry.registerBlockHandler(new MachineRender());
        RenderingRegistry.registerBlockHandler(new DryingRackRender());
        RenderingRegistry.registerBlockHandler(new PaneRender());
        RenderingRegistry.registerBlockHandler(new PaneConnectedRender());
        RenderingRegistry.registerBlockHandler(new RenderLandmine());
        RenderingRegistry.registerBlockHandler(new PunjiRender());
        RenderingRegistry.registerBlockHandler(new RenderBlockFluid());
        RenderingRegistry.registerBlockHandler(new BlockRenderCastingChannel());
        RenderingRegistry.registerBlockHandler(new SlimeChannelRender());
        RenderingRegistry.registerBlockHandler(new SlimePadRender());

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
        RenderingRegistry.registerEntityRenderingHandler(LaunchedPotion.class, new LaunchedItemRender(Item.potion, 16384));
        RenderingRegistry.registerEntityRenderingHandler(DaggerEntity.class, new DaggerRenderCustom());
        RenderingRegistry.registerEntityRenderingHandler(ArrowEntity.class, new ArrowRenderCustom());
        RenderingRegistry.registerEntityRenderingHandler(EntityLandmineFirework.class, new RenderSnowball(Item.firework));
        RenderingRegistry.registerEntityRenderingHandler(ExplosivePrimed.class, new ExplosiveRender());
        // RenderingRegistry.registerEntityRenderingHandler(net.minecraft.entity.player.EntityPlayer.class,
        // new PlayerArmorRender()); // <-- Works, woo!

        MinecraftForgeClient.registerItemRenderer(TContent.shortbow.itemID, new CustomBowRenderer());
        VillagerRegistry.instance().registerVillagerSkin(78943, new ResourceLocation("tinker", "textures/mob/villagertools.png"));

        ToolCoreRenderer renderer = new ToolCoreRenderer();
        MinecraftForgeClient.registerItemRenderer(TContent.arrow.itemID, renderer);
        MinecraftForgeClient.registerItemRenderer(TContent.dagger.itemID, renderer);

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
        diary = readManual("/assets/tinker/manuals/diary.xml", dbFactory);
        volume1 = readManual("/assets/tinker/manuals/firstday.xml", dbFactory);
        volume2 = readManual("/assets/tinker/manuals/materials.xml", dbFactory);
        smelter = readManual("/assets/tinker/manuals/smeltery.xml", dbFactory);
        initManualIcons();
        initManualRecipes();
        initManualPages();
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
        // TConstructClientRegistry.registerManualIcon("liquidiron", new
        // ItemStack(TContent.liquidMetalStill));
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

        // Tool Materials
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

        // Tool parts
        TConstructClientRegistry.registerManualIcon("pickhead", new ItemStack(TContent.pickaxeHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("shovelhead", new ItemStack(TContent.shovelHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("axehead", new ItemStack(TContent.hatchetHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("swordblade", new ItemStack(TContent.swordBlade, 1, 2));
        TConstructClientRegistry.registerManualIcon("pan", new ItemStack(TContent.frypanHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("board", new ItemStack(TContent.signHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("knifeblade", new ItemStack(TContent.knifeBlade, 1, 2));
        TConstructClientRegistry.registerManualIcon("chiselhead", new ItemStack(TContent.chiselHead, 1, 2));

        TConstructClientRegistry.registerManualIcon("hammerhead", new ItemStack(TContent.hammerHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("excavatorhead", new ItemStack(TContent.excavatorHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("scythehead", new ItemStack(TContent.scytheBlade, 1, 2));
        TConstructClientRegistry.registerManualIcon("broadaxehead", new ItemStack(TContent.broadAxeHead, 1, 2));
        TConstructClientRegistry.registerManualIcon("largeswordblade", new ItemStack(TContent.largeSwordBlade, 1, 2));

        TConstructClientRegistry.registerManualIcon("toolrod", new ItemStack(Item.stick));

        TConstructClientRegistry.registerManualIcon("binding", new ItemStack(TContent.binding, 1, 4));
        TConstructClientRegistry.registerManualIcon("wideguard", new ItemStack(TContent.wideGuard, 1, 4));
        TConstructClientRegistry.registerManualIcon("handguard", new ItemStack(TContent.handGuard, 1, 4));
        TConstructClientRegistry.registerManualIcon("crossbar", new ItemStack(TContent.crossbar, 1, 4));

        TConstructClientRegistry.registerManualIcon("toughrod", new ItemStack(TContent.toughRod, 1, 0));
        TConstructClientRegistry.registerManualIcon("toughbinding", new ItemStack(TContent.toughBinding, 1, 17));
        TConstructClientRegistry.registerManualIcon("largeplate", new ItemStack(TContent.largePlate, 1, 17));

        TConstructClientRegistry.registerManualIcon("bowstring", new ItemStack(TContent.bowstring, 1, 0));
        TConstructClientRegistry.registerManualIcon("arrowhead", new ItemStack(TContent.arrowhead, 1, 2));
        TConstructClientRegistry.registerManualIcon("fletching", new ItemStack(TContent.fletching, 1, 0));

        // ToolIcons
        TConstructClientRegistry.registerManualIcon("pickicon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.pickaxeHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.binding, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("shovelicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.shovelHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), null, ""));
        TConstructClientRegistry.registerManualIcon("axeicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.hatchetHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), null, ""));
        TConstructClientRegistry.registerManualIcon("mattockicon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.hatchetHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.shovelHead, 1, 12), ""));
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
        TConstructClientRegistry.registerManualIcon("chiselicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.chiselHead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), null, ""));
        TConstructClientRegistry.registerManualIcon("shortbowIcon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.toolRod, 1, 10), new ItemStack(TContent.bowstring, 1, 0), new ItemStack(TContent.toolRod, 1, 12), ""));
        TConstructClientRegistry.registerManualIcon("arrowIcon",
                ToolBuilder.instance.buildTool(new ItemStack(TContent.arrowhead, 1, 10), new ItemStack(TContent.toolRod, 1, 11), new ItemStack(TContent.fletching, 1, 0), ""));

        TConstructClientRegistry.registerManualIcon("hammericon", ToolBuilder.instance.buildTool(new ItemStack(TContent.hammerHead, 1, 10), new ItemStack(TContent.toughRod, 1, 11), new ItemStack(
                TContent.largePlate, 1, 12), new ItemStack(TContent.largePlate, 8), ""));
        TConstructClientRegistry.registerManualIcon("lumbericon", ToolBuilder.instance.buildTool(new ItemStack(TContent.broadAxeHead, 1, 10), new ItemStack(TContent.toughRod, 1, 11), new ItemStack(
                TContent.largePlate, 1, 12), new ItemStack(TContent.toughBinding, 8), ""));
        TConstructClientRegistry.registerManualIcon("excavatoricon", ToolBuilder.instance.buildTool(new ItemStack(TContent.excavatorHead, 1, 10), new ItemStack(TContent.toughRod, 1, 11),
                new ItemStack(TContent.largePlate, 1, 12), new ItemStack(TContent.toughBinding, 8), ""));
        TConstructClientRegistry.registerManualIcon("scytheicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.scytheBlade, 1, 10), new ItemStack(TContent.toughRod, 1, 11), new ItemStack(
                TContent.toughBinding, 1, 12), new ItemStack(TContent.toughRod, 8), ""));
        TConstructClientRegistry.registerManualIcon("cleavericon", ToolBuilder.instance.buildTool(new ItemStack(TContent.largeSwordBlade, 1, 10), new ItemStack(TContent.toughRod, 1, 11),
                new ItemStack(TContent.largePlate, 1, 12), new ItemStack(TContent.toughRod, 8), ""));
        TConstructClientRegistry.registerManualIcon("battleaxeicon", ToolBuilder.instance.buildTool(new ItemStack(TContent.broadAxeHead, 1, 10), new ItemStack(TContent.toughRod, 1, 11),
                new ItemStack(TContent.broadAxeHead, 1, 12), new ItemStack(TContent.toughBinding, 8), ""));
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
        ItemStack ironblock = new ItemStack(Block.blockIron, 1, 0);

        ItemStack grout = new ItemStack(TContent.craftedSoil, 2, 1);
        ItemStack searedbrick = new ItemStack(TContent.materials, 1, 2);
        ItemStack searedbrickBlock = new ItemStack(TContent.smeltery, 1, 2);

        ItemStack coal = new ItemStack(Item.coal);
        ItemStack paper = new ItemStack(Item.paper);
        ItemStack slimeball = new ItemStack(Item.slimeBall);
        ItemStack slimyMud = new ItemStack(TContent.craftedSoil);
        ItemStack blazerod = new ItemStack(Item.blazeRod);
        ItemStack firecharge = new ItemStack(Item.fireballCharge);
        ItemStack string = new ItemStack(Item.silk);

        ItemStack silkyCloth = new ItemStack(TContent.materials, 1, 25);

        ItemStack graveyardsoil = new ItemStack(TContent.craftedSoil, 1, 3);
        ItemStack consecratedsoil = new ItemStack(TContent.craftedSoil, 1, 4);

        // TConstruct recipes
        TConstructClientRegistry.registerManualSmallRecipe("patternbook1", new ItemStack(TContent.manualBook, 1, 0), new ItemStack(Item.paper), pattern, null, null);
        TConstructClientRegistry.registerManualSmallRecipe("patternbook2", new ItemStack(TContent.manualBook, 1, 1), new ItemStack(TContent.manualBook, 1, 0), null, null, null);
        TConstructClientRegistry.registerManualSmallRecipe("patternbook3", new ItemStack(TContent.manualBook, 1, 2), new ItemStack(TContent.manualBook, 1, 1), null, null, null);
        TConstructClientRegistry.registerManualSmallRecipe("blankpattern", pattern, plank, stick, stick, plank);
        TConstructClientRegistry.registerManualSmallRecipe("toolstation", new ItemStack(TContent.toolStationWood, 1, 0), null, pattern, null, workbench);
        TConstructClientRegistry.registerManualSmallRecipe("partcrafter", new ItemStack(TContent.toolStationWood, 1, 1), null, pattern, null, log);
        TConstructClientRegistry.registerManualSmallRecipe("patternchest", new ItemStack(TContent.toolStationWood, 1, 5), null, pattern, null, chest);
        TConstructClientRegistry.registerManualSmallRecipe("stenciltable", new ItemStack(TContent.toolStationWood, 1, 10), null, pattern, null, plank);
        TConstructClientRegistry.registerManualLargeRecipe("toolforge", new ItemStack(TContent.toolForge, 1, 0), searedbrickBlock, searedbrickBlock, searedbrickBlock, ironblock, new ItemStack(
                TContent.toolStationWood, 1, 0), ironblock, ironblock, null, ironblock);

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

        TConstructClientRegistry.registerManualSmallRecipe("graveyardsoil", graveyardsoil, new ItemStack(Block.dirt), new ItemStack(Item.rottenFlesh), new ItemStack(Item.dyePowder, 1, 15), null);
        TConstructClientRegistry.registerManualFurnaceRecipe("consecratedsoil", consecratedsoil, graveyardsoil);

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

        //Traps
        ItemStack reed = new ItemStack(Item.reed);
        TConstructClientRegistry.registerManualLargeRecipe("punji", new ItemStack(TContent.punji), reed, null, reed, null, reed, null, reed, null, reed);
        TConstructClientRegistry.registerManualSmallRecipe("barricade", new ItemStack(TContent.barricadeOak), null, log, null, log);

        //Machines
        ItemStack alubrassIngot = new ItemStack(TContent.materials, 1, 14);
        ItemStack bronzeIngot = new ItemStack(TContent.materials, 1, 13);
        ItemStack blankCast = new ItemStack(TContent.blankPattern, 1, 1);
        ItemStack redstone = new ItemStack(Item.redstone);

        TConstructClientRegistry.registerManualLargeRecipe("drawbridge", new ItemStack(TContent.redstoneMachine, 1, 0), alubrassIngot, blankCast, alubrassIngot, bronzeIngot, new ItemStack(
                Block.dispenser), bronzeIngot, bronzeIngot, redstone, bronzeIngot);
        TConstructClientRegistry.registerManualLargeRecipe("igniter", new ItemStack(TContent.redstoneMachine, 1, 1), alubrassIngot, new ItemStack(TContent.largePlate, 1, 7), alubrassIngot,
                bronzeIngot, new ItemStack(Item.flintAndSteel), bronzeIngot, bronzeIngot, redstone, bronzeIngot);

        // Modifier recipes
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
        TConstructClientRegistry.registerManualModifier("reinforcedmod", ironpick.copy(), new ItemStack(TContent.largePlate, 1, 6));

        TConstructClientRegistry.registerManualModifier("pistonmod", ironlongsword.copy(), new ItemStack(Block.pistonBase));
        TConstructClientRegistry.registerManualModifier("beheadingmod", ironlongsword.copy(), new ItemStack(Item.enderPearl), new ItemStack(Block.obsidian));
        TConstructClientRegistry.registerManualModifier("spidermod", ironlongsword.copy(), new ItemStack(Item.fermentedSpiderEye));
        TConstructClientRegistry.registerManualModifier("smitemod", ironlongsword.copy(), new ItemStack(TContent.craftedSoil, 1, 4));

        TConstructClientRegistry.registerManualModifier("electricmod", ironpick.copy(), new ItemStack(Block.dirt), new ItemStack(Block.dirt));
        TConstructClientRegistry.registerManualModifier("tier1free", ironpick.copy(), new ItemStack(Item.diamond), new ItemStack(Block.blockGold));
        TConstructClientRegistry.registerManualModifier("tier2free", ironpick.copy(), new ItemStack(Item.netherStar));

        TConstructClientRegistry.registerManualSmeltery("brownstone", new ItemStack(TContent.speedBlock), new ItemStack(TContent.moltenTin, 1), new ItemStack(Block.gravel));
        TConstructClientRegistry.registerManualSmeltery("clearglass", new ItemStack(TContent.clearGlass), new ItemStack(TContent.moltenGlass, 1), null);
        TConstructClientRegistry.registerManualSmeltery("searedstone", new ItemStack(TContent.smeltery, 1, 4), new ItemStack(TContent.moltenStone, 1), null);
        TConstructClientRegistry.registerManualSmeltery("endstone", new ItemStack(Block.whiteStone), new ItemStack(TContent.moltenEnder, 1), new ItemStack(Block.obsidian));

    }

    public static Map<String, Class<? extends BookPage>> pageClasses = new HashMap<String, Class<? extends BookPage>>();

    public static void registerManualPage (String type, Class<? extends BookPage> clazz)
    {
        pageClasses.put(type, clazz);
    }

    public static Class<? extends BookPage> getPageClass (String type)
    {
        return pageClasses.get(type);
    }

    void initManualPages ()
    {
        TProxyClient.registerManualPage("crafting", CraftingPage.class);
        TProxyClient.registerManualPage("picture", PicturePage.class);
        TProxyClient.registerManualPage("text", TextPage.class);
        TProxyClient.registerManualPage("intro", TextPage.class);
        TProxyClient.registerManualPage("sectionpage", SectionPage.class);
        TProxyClient.registerManualPage("intro", TitlePage.class);
        TProxyClient.registerManualPage("contents", ContentsTablePage.class);
        TProxyClient.registerManualPage("furnace", FurnacePage.class);
        TProxyClient.registerManualPage("sidebar", SidebarPage.class);
        TProxyClient.registerManualPage("materialstats", MaterialPage.class);
        TProxyClient.registerManualPage("toolpage", ToolPage.class);
        TProxyClient.registerManualPage("modifier", ModifierPage.class);
        TProxyClient.registerManualPage("blockcast", BlockCastPage.class);

        TProxyClient.registerManualPage("blank", BlankPage.class);
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
            "The Battlesign is an advance in weapon technology worthy of Zombie Pigmen everywhere.\n\nSpecial Ability: Block\nDamage: Low\nDurability: Average\n\nRequired parts:\n- Sign Board\n- Handle",
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
                "blueslime" };
        // String[] modPartTypes = { "thaumium" };
        String[] effectTypes = { "diamond", "emerald", "redstone", "piston", "moss", "ice", "lava", "blaze", "necrotic", "electric", "lapis", "quartz", "silk", "beheading", "smite", "spider",
                "reinforced" };
        int[] universalEffects = { 0, 1, 4, 9, 16 };
        int[] weaponEffects = { 3, 5, 7, 13, 14, 15 };
        int[] harvestEffects = { 2 };
        int[] nonUtility = { 6, 8, 10, 11, 12 };

        for (int partIter = 0; partIter < partTypes.length; partIter++)
        {
            TConstructClientRegistry.addMaterialRenderMapping(partIter, "tinker", partTypes[partIter], true);
        }

        /*
         * for (int iter = 0; iter < modPartTypes.length; iter++) {
         * TConstructClientRegistry.addMaterialRenderMapping(31 + iter,
         * "tinker", modPartTypes[iter], true); }
         */

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
            TConstructClientRegistry.addAlternateMaterialRenderMapping(TContent.shortbow, bowIter, "tinker", bowstringTypes[bowIter], true);
        }

        String[] fletching = { "feather", "leaf", "slime", "blueslime" };
        for (int arrowIter = 0; arrowIter < fletching.length; arrowIter++)
        {
            TConstructClientRegistry.addAlternateMaterialRenderMapping(TContent.arrow, arrowIter, "tinker", fletching[arrowIter], true);
        }
    }

    /* Keybindings */
    public static TControls controlInstance;

    public void registerKeys ()
    {
        controlInstance = new TControls();
        TickRegistry.registerTickHandler(controlInstance, Side.CLIENT);
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
                        entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, Item.snowball);
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
                        entityfx = new EntityBreakingFX(mc.theWorld, par2, par4, par6, TContent.strangeFood);
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
	public void postInit() {
		MinecraftForgeClient.registerItemRenderer(TContent.armorPattern.itemID, new RenderArmorCast());
	}
    
}
