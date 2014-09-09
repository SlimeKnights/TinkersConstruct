package tconstruct.tools;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mantle.client.MProxyClient;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.TConstruct;
import tconstruct.client.ToolCoreRenderer;
import tconstruct.client.entity.projectile.*;
import tconstruct.client.pages.*;
import tconstruct.common.TProxyCommon;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.*;
import tconstruct.library.crafting.*;
import tconstruct.library.tools.ToolCore;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.entity.*;
import tconstruct.tools.gui.*;
import tconstruct.tools.logic.*;
import tconstruct.tools.model.*;

public class ToolProxyClient extends ToolProxyCommon
{
    @Override
    public void initialize ()
    {
        MinecraftForge.EVENT_BUS.register(this);
        registerRenderer();
        registerGuiHandler();
        registerManualIcons();
        registerManualRecipes();
        addToolRenderMappings();
        addStencilButtons();
        addToolButtons();
    }

    public void registerRenderer ()
    {
        RenderingRegistry.registerBlockHandler(new TableRender());
        RenderingRegistry.registerBlockHandler(new FrypanRender());
        RenderingRegistry.registerBlockHandler(new BattlesignRender());

        RenderingRegistry.registerEntityRenderingHandler(LaunchedPotion.class, new LaunchedItemRender(Items.potionitem, 16384));
        RenderingRegistry.registerEntityRenderingHandler(DaggerEntity.class, new DaggerRenderCustom());
        RenderingRegistry.registerEntityRenderingHandler(ArrowEntity.class, new ArrowRenderCustom());
        RenderingRegistry.registerEntityRenderingHandler(FancyEntityItem.class, new FancyItemRender());

        //MinecraftForgeClient.registerItemRenderer(TinkerTools.shortbow, new CustomBowRenderer());
        MinecraftForgeClient.registerItemRenderer(TinkerTools.shortbow, new ToolCoreRenderer(false));
        ToolCoreRenderer renderer = new ToolCoreRenderer(true);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.arrow, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.dagger, renderer);

        TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(BattlesignLogic.class, new BattlesignTesr());
    }

    public void registerManualIcons ()
    {

        // ToolIcons
        MantleClientRegistry.registerManualIcon("pickicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.pickaxeHead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerTools.binding, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("shovelicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.shovelHead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("axeicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.hatchetHead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("mattockicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.hatchetHead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerTools.shovelHead, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("swordicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.swordBlade, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerTools.wideGuard, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("longswordicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.swordBlade, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerTools.handGuard, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("rapiericon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.swordBlade, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerTools.crossbar, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("daggerIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.knifeBlade, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerTools.crossbar, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("frypanicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.frypanHead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("battlesignicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.signHead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("chiselicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.chiselHead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("shortbowIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.toolRod, 1, 10), new ItemStack(TinkerTools.bowstring, 1, 0), new ItemStack(TinkerTools.toolRod, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("arrowIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.arrowhead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerTools.fletching, 1, 0), ""));

        MantleClientRegistry.registerManualIcon("hammericon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.hammerHead, 1, 10), new ItemStack(TinkerTools.toughRod, 1, 11), new ItemStack(TinkerTools.largePlate, 1, 12), new ItemStack(TinkerTools.largePlate, 8), ""));
        MantleClientRegistry.registerManualIcon("lumbericon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.broadAxeHead, 1, 10), new ItemStack(TinkerTools.toughRod, 1, 11), new ItemStack(TinkerTools.largePlate, 1, 12), new ItemStack(TinkerTools.toughBinding, 8), ""));
        MantleClientRegistry.registerManualIcon("excavatoricon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.excavatorHead, 1, 10), new ItemStack(TinkerTools.toughRod, 1, 11), new ItemStack(TinkerTools.largePlate, 1, 12), new ItemStack(TinkerTools.toughBinding, 8), ""));
        MantleClientRegistry.registerManualIcon("scytheicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.scytheBlade, 1, 10), new ItemStack(TinkerTools.toughRod, 1, 11), new ItemStack(TinkerTools.toughBinding, 1, 12), new ItemStack(TinkerTools.toughRod, 8), ""));
        MantleClientRegistry.registerManualIcon("cleavericon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.largeSwordBlade, 1, 10), new ItemStack(TinkerTools.toughRod, 1, 11), new ItemStack(TinkerTools.largePlate, 1, 12), new ItemStack(TinkerTools.toughRod, 8), ""));
        MantleClientRegistry.registerManualIcon("battleaxeicon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.broadAxeHead, 1, 10), new ItemStack(TinkerTools.toughRod, 1, 11), new ItemStack(TinkerTools.broadAxeHead, 1, 12), new ItemStack(TinkerTools.toughBinding, 8), ""));

        // Tool parts
        MantleClientRegistry.registerManualIcon("pickhead", new ItemStack(TinkerTools.pickaxeHead, 1, 2));
        MantleClientRegistry.registerManualIcon("shovelhead", new ItemStack(TinkerTools.shovelHead, 1, 2));
        MantleClientRegistry.registerManualIcon("axehead", new ItemStack(TinkerTools.hatchetHead, 1, 2));
        MantleClientRegistry.registerManualIcon("swordblade", new ItemStack(TinkerTools.swordBlade, 1, 2));
        MantleClientRegistry.registerManualIcon("pan", new ItemStack(TinkerTools.frypanHead, 1, 2));
        MantleClientRegistry.registerManualIcon("board", new ItemStack(TinkerTools.signHead, 1, 2));
        MantleClientRegistry.registerManualIcon("knifeblade", new ItemStack(TinkerTools.knifeBlade, 1, 2));
        MantleClientRegistry.registerManualIcon("chiselhead", new ItemStack(TinkerTools.chiselHead, 1, 2));

        MantleClientRegistry.registerManualIcon("hammerhead", new ItemStack(TinkerTools.hammerHead, 1, 2));
        MantleClientRegistry.registerManualIcon("excavatorhead", new ItemStack(TinkerTools.excavatorHead, 1, 2));
        MantleClientRegistry.registerManualIcon("scythehead", new ItemStack(TinkerTools.scytheBlade, 1, 2));
        MantleClientRegistry.registerManualIcon("broadaxehead", new ItemStack(TinkerTools.broadAxeHead, 1, 2));
        MantleClientRegistry.registerManualIcon("largeswordblade", new ItemStack(TinkerTools.largeSwordBlade, 1, 2));

        MantleClientRegistry.registerManualIcon("toolrod", new ItemStack(Items.stick));

        MantleClientRegistry.registerManualIcon("binding", new ItemStack(TinkerTools.binding, 1, 4));
        MantleClientRegistry.registerManualIcon("wideguard", new ItemStack(TinkerTools.wideGuard, 1, 4));
        MantleClientRegistry.registerManualIcon("handguard", new ItemStack(TinkerTools.handGuard, 1, 4));
        MantleClientRegistry.registerManualIcon("crossbar", new ItemStack(TinkerTools.crossbar, 1, 4));

        MantleClientRegistry.registerManualIcon("toughrod", new ItemStack(TinkerTools.toughRod, 1, 0));
        MantleClientRegistry.registerManualIcon("toughbinding", new ItemStack(TinkerTools.toughBinding, 1, 17));
        MantleClientRegistry.registerManualIcon("largeplate", new ItemStack(TinkerTools.largePlate, 1, 17));

        MantleClientRegistry.registerManualIcon("bowstring", new ItemStack(TinkerTools.bowstring, 1, 0));
        MantleClientRegistry.registerManualIcon("arrowhead", new ItemStack(TinkerTools.arrowhead, 1, 2));
        MantleClientRegistry.registerManualIcon("fletching", new ItemStack(TinkerTools.fletching, 1, 0));

        //Tables
        MantleClientRegistry.registerManualIcon("blankpattern", new ItemStack(TinkerTools.blankPattern, 1, 0));
        MantleClientRegistry.registerManualIcon("toolstation", new ItemStack(TinkerTools.toolStationWood, 1, 0));
        MantleClientRegistry.registerManualIcon("partcrafter", new ItemStack(TinkerTools.toolStationWood, 1, 1));
        MantleClientRegistry.registerManualIcon("patternchest", new ItemStack(TinkerTools.toolStationWood, 1, 5));
        MantleClientRegistry.registerManualIcon("stenciltable", new ItemStack(TinkerTools.toolStationWood, 1, 10));

        //TODO: Untwine this
        MantleClientRegistry.registerManualIcon("blueslimecrystal", new ItemStack(TinkerTools.materials, 1, 17));
        MantleClientRegistry.registerManualIcon("slimecrystal", new ItemStack(TinkerTools.materials, 1, 1));
        MantleClientRegistry.registerManualIcon("paperstack", new ItemStack(TinkerTools.materials, 1, 0));
        MantleClientRegistry.registerManualIcon("cobaltingot", new ItemStack(TinkerTools.materials, 1, 3));
        MantleClientRegistry.registerManualIcon("arditeingot", new ItemStack(TinkerTools.materials, 1, 4));
        MantleClientRegistry.registerManualIcon("copperingot", new ItemStack(TinkerTools.materials, 1, 9));
        MantleClientRegistry.registerManualIcon("steelingot", new ItemStack(TinkerTools.materials, 1, 16));
        MantleClientRegistry.registerManualIcon("pigironingot", new ItemStack(TinkerTools.materials, 1, 34));
        MantleClientRegistry.registerManualIcon("obsidianingot", new ItemStack(TinkerTools.materials, 1, 18));
        MantleClientRegistry.registerManualIcon("lavacrystal", new ItemStack(TinkerTools.materials, 1, 7));
        MantleClientRegistry.registerManualIcon("bronzeingot", new ItemStack(TinkerTools.materials, 1, 13));
        MantleClientRegistry.registerManualIcon("alubrassingot", new ItemStack(TinkerTools.materials, 1, 14));
        MantleClientRegistry.registerManualIcon("manyullyningot", new ItemStack(TinkerTools.materials, 1, 5));
        MantleClientRegistry.registerManualIcon("alumiteingot", new ItemStack(TinkerTools.materials, 1, 15));

        MProxyClient.registerManualPage("materialstats", MaterialPage.class);
        MProxyClient.registerManualPage("toolpage", ToolPage.class);
        MProxyClient.registerManualPage("modifier", ModifierPage.class);
    }

    void registerManualRecipes ()
    {

        ItemStack pattern = new ItemStack(TinkerTools.blankPattern, 1, 0);
        ItemStack silkyCloth = new ItemStack(TinkerTools.materials, 1, 25);

        ItemStack stick = new ItemStack(Items.stick, 1, 0);
        ItemStack paper = new ItemStack(Items.paper);
        ItemStack string = new ItemStack(Items.string);
        ItemStack plank = new ItemStack(Blocks.planks, 1, 0);
        ItemStack workbench = new ItemStack(Blocks.crafting_table, 1, 0);
        ItemStack chest = new ItemStack(Blocks.chest, 1, 0);
        ItemStack log = new ItemStack(Blocks.log, 1, 0);
        ItemStack ironblock = new ItemStack(Blocks.iron_block, 1, 0);
        ItemStack blazerod = new ItemStack(Items.blaze_rod);
        ItemStack mossycobble = new ItemStack(Blocks.mossy_cobblestone);
        ItemStack slimeball = new ItemStack(Items.slime_ball);
        ItemStack slimyMud = new ItemStack(TinkerTools.craftedSoil);
        ItemStack firecharge = new ItemStack(Items.fire_charge);
        ItemStack dirt = new ItemStack(Blocks.dirt, 1, 0);
        ItemStack sand = new ItemStack(Blocks.sand, 1, 0);
        ItemStack searedbrickBlock = new ItemStack(TinkerSmeltery.smeltery, 1, 2);

        MantleClientRegistry.registerManualLargeRecipe("alternatebook", new ItemStack(Items.book), paper, paper, paper, string, pattern, pattern, null, null, null);
        MantleClientRegistry.registerManualSmallRecipe("patternbook1", new ItemStack(TinkerTools.manualBook, 1, 0), new ItemStack(Items.paper), pattern, null, null);
        MantleClientRegistry.registerManualSmallRecipe("patternbook2", new ItemStack(TinkerTools.manualBook, 1, 1), new ItemStack(TinkerTools.manualBook, 1, 0), null, null, null);
        MantleClientRegistry.registerManualSmallRecipe("patternbook3", new ItemStack(TinkerTools.manualBook, 1, 2), new ItemStack(TinkerTools.manualBook, 1, 1), null, null, null);
        MantleClientRegistry.registerManualSmallRecipe("blankpattern", pattern, plank, stick, stick, plank);
        MantleClientRegistry.registerManualSmallRecipe("toolstation", new ItemStack(TinkerTools.toolStationWood, 1, 0), null, pattern, null, workbench);
        MantleClientRegistry.registerManualSmallRecipe("partcrafter", new ItemStack(TinkerTools.toolStationWood, 1, 1), null, pattern, null, log);
        MantleClientRegistry.registerManualSmallRecipe("patternchest", new ItemStack(TinkerTools.toolStationWood, 1, 5), null, pattern, null, chest);
        MantleClientRegistry.registerManualSmallRecipe("stenciltable", new ItemStack(TinkerTools.toolStationWood, 1, 10), null, pattern, null, plank);

        MantleClientRegistry.registerManualLargeRecipe("toolforge", new ItemStack(TinkerTools.toolForge, 1, 0), searedbrickBlock, searedbrickBlock, searedbrickBlock, ironblock, new ItemStack(TinkerTools.toolStationWood, 1, 0), ironblock, ironblock, null, ironblock); //TODO: Alternate recipe for Smeltery disabled

        MantleClientRegistry.registerManualLargeRecipe("slimymud", slimyMud, null, slimeball, slimeball, null, slimeball, slimeball, null, dirt, sand);
        MantleClientRegistry.registerManualFurnaceRecipe("slimecrystal", new ItemStack(TinkerTools.materials, 1, 1), slimyMud);
        MantleClientRegistry.registerManualSmallRecipe("paperstack", new ItemStack(TinkerTools.materials, 1, 0), paper, paper, paper, paper);
        MantleClientRegistry.registerManualLargeRecipe("mossball", new ItemStack(TinkerTools.materials, 1, 6), mossycobble, mossycobble, mossycobble, mossycobble, mossycobble, mossycobble, mossycobble, mossycobble, mossycobble);
        MantleClientRegistry.registerManualLargeRecipe("lavacrystal", new ItemStack(TinkerTools.materials, 1, 7), blazerod, firecharge, blazerod, firecharge, new ItemStack(Items.lava_bucket), firecharge, blazerod, firecharge, blazerod);
        MantleClientRegistry.registerManualLargeRecipe("silkycloth", silkyCloth, string, string, string, string, new ItemStack(TinkerTools.materials, 1, 24), string, string, string, string);
        MantleClientRegistry.registerManualLargeRecipe("silkyjewel", new ItemStack(TinkerTools.materials, 1, 26), null, silkyCloth, null, silkyCloth, new ItemStack(Items.emerald), silkyCloth, null, silkyCloth, null);

        // Modifier recipes
        ItemStack ironpick = ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.pickaxeHead, 1, 6), new ItemStack(TinkerTools.toolRod, 1, 2), new ItemStack(TinkerTools.binding, 1, 6), "");
        MantleClientRegistry.registerManualIcon("ironpick", ironpick);
        ItemStack ironlongsword = ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.swordBlade, 1, 6), new ItemStack(TinkerTools.toolRod, 1, 2), new ItemStack(TinkerTools.handGuard, 1, 10), "");
        MantleClientRegistry.registerManualIcon("ironlongsword", ironlongsword);

        TConstructClientRegistry.registerManualModifier("diamondmod", ironpick.copy(), new ItemStack(Items.diamond));
        TConstructClientRegistry.registerManualModifier("emeraldmod", ironpick.copy(), new ItemStack(Items.emerald));
        TConstructClientRegistry.registerManualModifier("redstonemod", ironpick.copy(), new ItemStack(Items.redstone), new ItemStack(Blocks.redstone_block));
        TConstructClientRegistry.registerManualModifier("lavacrystalmod", ironpick.copy(), new ItemStack(TinkerTools.materials, 1, 7));
        TConstructClientRegistry.registerManualModifier("lapismod", ironpick.copy(), new ItemStack(Items.dye, 1, 4), new ItemStack(Blocks.lapis_block));
        TConstructClientRegistry.registerManualModifier("mossmod", ironpick.copy(), new ItemStack(TinkerTools.materials, 1, 6));
        TConstructClientRegistry.registerManualModifier("quartzmod", ironlongsword.copy(), new ItemStack(Items.quartz), new ItemStack(Blocks.quartz_block));
        TConstructClientRegistry.registerManualModifier("blazemod", ironlongsword.copy(), new ItemStack(Items.blaze_powder));
        TConstructClientRegistry.registerManualModifier("necroticmod", ironlongsword.copy(), new ItemStack(TinkerTools.materials, 1, 8));
        TConstructClientRegistry.registerManualModifier("silkymod", ironpick.copy(), new ItemStack(TinkerTools.materials, 1, 26));
        TConstructClientRegistry.registerManualModifier("reinforcedmod", ironpick.copy(), new ItemStack(TinkerTools.largePlate, 1, 6));

        TConstructClientRegistry.registerManualModifier("pistonmod", ironlongsword.copy(), new ItemStack(Blocks.piston));
        TConstructClientRegistry.registerManualModifier("beheadingmod", ironlongsword.copy(), new ItemStack(Items.ender_pearl), new ItemStack(Blocks.obsidian));
        TConstructClientRegistry.registerManualModifier("spidermod", ironlongsword.copy(), new ItemStack(Items.fermented_spider_eye));
        TConstructClientRegistry.registerManualModifier("smitemod", ironlongsword.copy(), new ItemStack(TinkerTools.craftedSoil, 1, 4));

        TConstructClientRegistry.registerManualModifier("fluxmod", ironpick.copy(), new ItemStack(Blocks.dirt));
        TConstructClientRegistry.registerManualModifier("fluxmod2", ironpick.copy(), new ItemStack(Blocks.dirt));

        TConstructClientRegistry.registerManualModifier("tier1free", ironpick.copy(), new ItemStack(Items.diamond), new ItemStack(Blocks.gold_block));
        TConstructClientRegistry.registerManualModifier("tier1.5free", ironpick.copy(), new ItemStack(Items.golden_apple, 1, 1), new ItemStack(Blocks.diamond_block));
        TConstructClientRegistry.registerManualModifier("tier2free", ironpick.copy(), new ItemStack(Items.nether_star));
        TConstructClientRegistry.registerManualModifier("creativefree", ironpick.copy(), new ItemStack(TinkerTools.creativeModifier));

        TConstructClientRegistry.registerManualSmeltery("brownstone", new ItemStack(TinkerSmeltery.speedBlock), new ItemStack(TinkerSmeltery.moltenTin, 1), new ItemStack(Blocks.gravel));
        TConstructClientRegistry.registerManualSmeltery("clearglass", new ItemStack(TinkerSmeltery.clearGlass), new ItemStack(TinkerSmeltery.moltenGlass, 1), null);
        TConstructClientRegistry.registerManualSmeltery("searedstone", new ItemStack(TinkerSmeltery.smeltery, 1, 4), new ItemStack(TinkerSmeltery.moltenStone, 1), null);
        TConstructClientRegistry.registerManualSmeltery("endstone", new ItemStack(Blocks.end_stone), new ItemStack(TinkerSmeltery.moltenEnder, 1), new ItemStack(Blocks.obsidian));
        TConstructClientRegistry.registerManualSmeltery("glueball", new ItemStack(TinkerTools.materials, 1, 36), new ItemStack(TinkerSmeltery.glueFluidBlock, 1), null);
    }

    @Override
    protected void registerGuiHandler ()
    {
        super.registerGuiHandler();
        TProxyCommon.registerClientGuiHandler(toolStationID, this);
        TProxyCommon.registerClientGuiHandler(partBuilderID, this);
        TProxyCommon.registerClientGuiHandler(patternChestID, this);
        TProxyCommon.registerClientGuiHandler(stencilTableID, this);
        TProxyCommon.registerClientGuiHandler(frypanGuiID, this);
        TProxyCommon.registerClientGuiHandler(toolForgeID, this);
        TProxyCommon.registerClientGuiHandler(furnaceID, this);
        TProxyCommon.registerClientGuiHandler(craftingStationID, this);
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == ToolProxyCommon.toolStationID)
            return new ToolStationGui(player.inventory, (ToolStationLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.partBuilderID)
            return new PartCrafterGui(player.inventory, (PartBuilderLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.patternChestID)
            return new PatternChestGui(player.inventory, (PatternChestLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.frypanGuiID)
            return new FrypanGui(player.inventory, (FrypanLogic) world.getTileEntity(x, y, z), world, x, y, z);

        if (ID == ToolProxyCommon.stencilTableID)
            return new StencilTableGui(player.inventory, (StencilTableLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.toolForgeID)
            return new ToolForgeGui(player.inventory, (ToolForgeLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.craftingStationID)
            return new CraftingStationGui(player.inventory, (CraftingStationLogic) world.getTileEntity(x, y, z), world, x, y, z);

        if (ID == ToolProxyCommon.furnaceID)
            return new FurnaceGui(player.inventory, (FurnaceLogic) world.getTileEntity(x, y, z));

        return null;
    }

    @SubscribeEvent
    public void onSound (SoundLoadEvent event)
    {
        try
        {
            /*
             * SoundManager soundmanager = event.manager;
             * soundmanager.addSound("tinker:frypan_hit.ogg");
             * soundmanager.addSound("tinker:little_saw.ogg");
             * soundmanager.addSound("tinker:launcher_clank.ogg");
             * TConstruct.logger.info("Successfully loaded sounds.");
             */
        }
        catch (Exception e)
        {
            TConstruct.logger.error("Failed to register one or more sounds");
        }

        MinecraftForge.EVENT_BUS.unregister(this);
    }

    void addStencilButtons ()
    {
        int[][] icons = { { 0, 3 }, // tool rod
                { 1, 3 }, // binding
                { 8, 3 }, // large tool rod
                { 9, 3 }, // large binding

                { 0, 2 }, // pickaxe head
                { 3, 2 }, // shovel head
                { 2, 2 }, // hatchet head
                { 8, 2 }, // scythe

                { 11, 2 }, // hammer head
                { 10, 2 }, // excavator head
                { 6, 2 }, // lumberaxe head
                { 9, 2 }, // large plate

                {}, { 4, 2 }, // frying pan
                { 5, 2 }, // battlesign
                { 7, 3 }, // chisel

                {}, { 7, 2 }, // knifeblade
                { 1, 2 }, // swordblade
                { 6, 3 }, // cleaver blade

                {}, { 4, 3 }, // crossbar
                { 3, 3 }, // small guard
                { 2, 3 }, // wide guard

                {}, { 11, 3 }, // arrow head
                { 12, 3 }, // fletchling
                { 10, 3 }, // bowstring
        };

        int i = 0;
        for (ItemStack stack : StencilBuilder.getStencils())
        {
            // spacer
            while (icons[i].length == 0)
            {
                addStencilButton(0, 0, -1);
                i++;
            }
            addStencilButton(icons[i][0], icons[i][1], StencilBuilder.getIndex(stack));
            i++;
        }
    }

    void addStencilButton (int xButton, int yButton, int index)
    {
        TConstructClientRegistry.addStencilButton(xButton, yButton, index, "tinker", "textures/gui/icons.png");
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

    static String[] toolDescriptions = { "The main way to repair or change your tools. Place a tool and a material on the left to get started.", "The Pickaxe is a precise mining tool. It is effective on stone and ores.\n\nRequired parts:\n- Pickaxe Head\n- Tool Binding\n- Handle", "The Shovel is a precise digging tool. It is effective on dirt, sand, and snow.\n\nRequired parts:\n- Shovel Head\n- Handle", "The Hatchet is a basic chopping tool. It is effective on wood and leaves.\n\nRequired parts:\n- Axe Head\n- Handle",
            // "The Lumber Axe is a broad chopping tool. It harvests wood in a wide range and can fell entire trees.\n\nRequired parts:\n- Broad Axe Head\n- Handle",
            // "The Ice Axe is a tool for harvesting ice, mining, and attacking foes.\n\nSpecial Ability:\n- Wall Climb\nNatural Ability:\n- Ice Harvest\nDamage: Moderate\n\nRequired parts:\n- Pickaxe Head\n- Spike\n- Handle",
            "The Cutter Mattock is a versatile farming tool. It is effective on wood, dirt, and plants.\n\nSpecial Ability: Hoe\n\nRequired parts:\n- Axe Head\n- Shovel Head\n- Handle", "The Broadsword is a defensive weapon. Blocking cuts damage in half.\n\nSpecial Ability: Block\nDamage: Moderate\nDurability: High\n\nRequired parts:\n- Sword Blade\n- Wide Guard\n- Handle", "The Longsword is an offensive weapon. It is often used for charging into battle at full speed.\n\nNatural Ability:\n- Charge Boost\nSpecial Ability: Lunge\n\nDamage: Moderate\nDurability: Moderate", "The Rapier is a special weapon that relies on quick strikes to defeat foes.\n\nNatural Abilities:\n- Armor Pierce\n- Quick Strike\n- Charge Boost\nSpecial Ability:\n- Backpedal\n\nDamage: Low\nDurability: Low", "The Dagger is a short blade that can be thrown.\n\nSpecial Ability:\n- Throw Item\n\nDamage: Low\nDurability: Moderate\n\nRequired parts:\n- Knife Blade\n- Crossbar\n- Handle", "The Frying is a heavy weapon that uses sheer weight to stun foes.\n\nSpecial Ability: Block\nNatural Ability: Heavy\nShift+rClick: Place Frying Pan\nDamage: Low\nDurability: High\n\nRequired parts:\n- Pan\n- Handle",
            // "The Battlesign is an advance in weapon technology worthy of Zombie Pigmen everywhere.\n\nSpecial Ability: Block\nShift-rClick: Place sign\nDamage: Low\nDurability: Average\n\nRequired parts:\n- Board\n- Handle"
            "The Battlesign is an advance in weapon technology worthy of Zombie Pigmen everywhere.\n\nSpecial Ability:\nDamage Reflector\n\nNatural Ability: Writable\n\nDamage: Low\nDurability: Average", "The Chisel is a utility tool that carves shapes into blocks.\n\nCrafting Grid:\n- Shape Items\nSpecial Ability: Chisel\nDurability: Average\n\nRequired parts:\n- Chisel Head\n- Handle" };

    void addToolButtons ()
    {
        for (int i = 0; i < toolNames.length; i++)
        {
            addToolButton(itemIcons[i][0], itemIcons[i][1], itemIcons[i][2], iconCoords[i * 2], iconCoords[i * 2 + 1], toolNames[i], toolDescriptions[i]);
        }

        addToolButton(3, 9, 1, new int[] { 0, 10, 0, 13 }, new int[] { 3, 3, 3, 13 }, "Shortbow", "The Shortbow is a ranged weapon. It fires arrows quickly and precisely at its foes.\n\nDraw Speed: Quick\n\nRequired parts:\n- Tool Rod\n- Bowstring\n- Tool Rod");
        addToolButton(7, 10, 1, new int[] { 11, 0, 12, 13 }, new int[] { 3, 3, 3, 13 }, "Arrow", "Arrows are projectiles usually fired from bows.\n\nRequired parts:\n- Arrowhead\n- Tool Rod\n- Fletching");
        addTierTwoButton(6, 13, 0, new int[] { 11, 8, 9, 9 }, new int[] { 2, 3, 2, 2 }, "Hammer", "The Hammer is a broad mining tool. It harvests blocks in a wide range and is effective against undead.\n\nNatural Abilities:\nArea of Effect\n- (3x3)\n- Smite\n\nDurability: High");
        addTierTwoButton(5, 11, 0, new int[] { 6, 8, 9, 9 }, new int[] { 2, 3, 2, 3 }, "Lumber Axe", "The Lumber Axe is a broad chopping tool. It can fell entire trees or gather wood in a wide range.\n\nNatural Abilities:\nArea of Effect\n- Fell Trees\n- (3x3x3)\n\nDurability: Average");
        addTierTwoButton(5, 12, 0, new int[] { 10, 8, 9, 9 }, new int[] { 2, 3, 2, 3 }, "Excavator", "The Excavator is a broad digging tool. It harvests soil and snow in a wide range.\n\nNatural Ability:\n- Area of Effect\n- (3x3)\n\nDurability: Average");
        addTierTwoButton(4, 10, 0, new int[] { 8, 8, 9, 8 }, new int[] { 2, 3, 3, 3 }, "Scythe", "The Scythe is a broad reaping tool. It is effective on plants and attacks enemies in a wide range.\n\nNatural Ability:\nArea of Effect\n- (3x3x3)\n\nDurability: Average\nDamage: Low, AoE");
        addTierTwoButton(5, 7, 1, new int[] { 6, 8, 9, 8 }, new int[] { 3, 3, 2, 3 }, "Cleaver", "The Cleaver is a heavy defensive weapon. It has powerful strikes, but is difficult to wield.\n\nSpecial Ability: Block\nNatural Ability:\n- Beheading\n\nDamage: High\nDurability: Average");
        addTierTwoButton(5, 8, 1, new int[] { 6, 8, 6, 9 }, new int[] { 2, 3, 2, 3 }, "Battleaxe", "The Battleaxe is a heavy offensive weapon. It is capable of bringing down small trees and can send foes flying.\n\nSpecial Ability: Block\nNatural Abilities:" + "\n- Knockback\n- Area of Effect\n- (1x9)\n\nDamage: Average\nDurability: Average");
    }

    void addToolButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body)
    {
        TConstructClientRegistry.addToolButton(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body, "tinker", "textures/gui/icons.png"));
    }

    void addTierTwoButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body)
    {
        TConstructClientRegistry.addTierTwoButton(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body, "tinker", "textures/gui/icons.png"));
    }

    void addToolRenderMappings ()
    {
        String[] partTypes = { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel", "blueslime", "pigiron" };
        String[] effectTypes = { "diamond", "emerald", "redstone", "piston", "moss", "ice", "lava", "blaze", "necrotic", "flux", "lapis", "quartz", "silk", "beheading", "smite", "spider", "reinforced", "flux" };
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
            TConstructClientRegistry.addAlternateMaterialRenderMapping(TinkerTools.shortbow, bowIter, "tinker", bowstringTypes[bowIter], true);
        }

        String[] fletching = { "feather", "leaf", "slime", "blueslime" };
        for (int arrowIter = 0; arrowIter < fletching.length; arrowIter++)
        {
            TConstructClientRegistry.addAlternateMaterialRenderMapping(TinkerTools.arrow, arrowIter, "tinker", fletching[arrowIter], true);
        }
    }
}
