package tconstruct.tools;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mantle.client.MProxyClient;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
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
import tconstruct.tools.client.DaggerEntityRenderer;
import tconstruct.tools.entity.*;
import tconstruct.tools.gui.*;
import tconstruct.tools.logic.*;
import tconstruct.tools.model.*;
import tconstruct.weaponry.TinkerWeaponry;

import static tconstruct.tools.TinkerTools.*;

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
        RenderingRegistry.registerEntityRenderingHandler(DaggerEntity.class, new DaggerEntityRenderer());
        //RenderingRegistry.registerEntityRenderingHandler(DaggerEntity.class, new DaggerRenderCustom());
        //RenderingRegistry.registerEntityRenderingHandler(ArrowEntity.class, new ArrowRenderCustom());
        RenderingRegistry.registerEntityRenderingHandler(FancyEntityItem.class, new FancyItemRender());

        //MinecraftForgeClient.registerItemRenderer(TinkerTools.shortbow, new CustomBowRenderer());
        ToolCoreRenderer renderer = new ToolCoreRenderer(false);
        /*
        MinecraftForgeClient.registerItemRenderer(TinkerTools.pickaxe, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.shovel, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.hatchet, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.broadsword, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.battleaxe, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.battlesign, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.chisel, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.cleaver, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.cutlass, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.excavator, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.hammer, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.lumberaxe, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.frypan, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.longsword, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.mattock, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.rapier, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.scythe, renderer);
        */
        renderer = new ToolCoreRenderer(true);
        MinecraftForgeClient.registerItemRenderer(TinkerTools.dagger, renderer); // todo proper renderer

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
        MantleClientRegistry.registerManualIcon("shortbowIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.toolRod, 1, 10), new ItemStack(TinkerWeaponry.bowstring, 1, 0), new ItemStack(TinkerTools.toolRod, 1, 12), ""));
        MantleClientRegistry.registerManualIcon("arrowIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerWeaponry.arrowhead, 1, 10), new ItemStack(TinkerTools.toolRod, 1, 11), new ItemStack(TinkerWeaponry.fletching, 1, 0), ""));

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

        MantleClientRegistry.registerManualIcon("bowstring", new ItemStack(TinkerWeaponry.bowstring, 1, 0));
        MantleClientRegistry.registerManualIcon("arrowhead", new ItemStack(TinkerWeaponry.arrowhead, 1, 2));
        MantleClientRegistry.registerManualIcon("fletching", new ItemStack(TinkerWeaponry.fletching, 1, 0));

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
        TProxyCommon.registerClientGuiHandler(battlesignTextID, this);
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
        if (ID == ToolProxyCommon.battlesignTextID)
            return new BattlesignGui((BattlesignLogic) world.getTileEntity(x, y, z));

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
        for (int[] icon : icons)
        {
            // spacer
            if(icon.length == 0)
            {
                addStencilButton(0, 0, -1);
            }
            else
                addStencilButton(icon[0], icon[1], i++);
        }
    }

    void addStencilButton (int xButton, int yButton, int index)
    {
        TConstructClientRegistry.addStencilButton(xButton, yButton, index, "tinker", "textures/gui/icons.png");
    }

    static int[][] itemIconsT1 = {
            new int[] { 1, 4, 0 }, // Pickaxe
            new int[] { 2, 5, 0 }, // Shovel
            new int[] { 2, 6, 0 }, // Hatchet
            new int[] { 3, 8, 0 }, // Mattock
            new int[] { 1, 0, 1 }, // Broadsword
            new int[] { 1, 1, 1 }, // Longsword
            new int[] { 1, 2, 1 }, // Rapier
            new int[] { 1, 5, 1 }, // Dagger
            new int[] { 2, 3, 1 }, // Frying pan
            new int[] { 2, 4, 1 }, // Battlesign
            new int[] { 2, 6, 1 }  // Chisel
    };

    static int[][] iconCoordsT1 = {
            new int[] { 0, 0, 1, 13 }, new int[] { 2, 3, 3, 13 }, // Pickaxe
            new int[] { 3, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Shovel
            new int[] { 2, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Hatchet
            new int[] { 2, 0, 3, 13 }, new int[] { 2, 3, 2, 13 }, // Mattock
            new int[] { 1, 0, 2, 13 }, new int[] { 2, 3, 3, 13 }, // Broadsword
            new int[] { 1, 0, 3, 13 }, new int[] { 2, 3, 3, 13 }, // Longsword
            new int[] { 1, 0, 4, 13 }, new int[] { 2, 3, 3, 13 }, // Rapier
            new int[] { 7, 0, 4, 13 }, new int[] { 2, 3, 3, 13 }, // Dagger
            new int[] { 4, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Frying Pan
            new int[] { 5, 0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // Battlesign
            new int[] { 7, 0, 13, 13 }, new int[] { 3, 3, 13, 13 } // Chisel
    };

    static int[][] itemIconsT2 = {
            new int[] { 6, 13, 0, }, // Hammer
            new int[] { 5, 11, 0, }, // Lumberaxe
            new int[] { 5, 12, 0, }, // Excavator
            new int[] { 4, 10, 0, }, // Scythe
            new int[] { 5,  7, 1, }, // Cleaver
            new int[] { 5,  8, 1, }, // Battleaxe
    };

    static int[][] iconCoordsT2 = {
            new int[] { 11, 8, 9, 9 }, new int[] { 2, 3, 2, 2 }, // Hammer
            new int[] {  6, 8, 9, 9 }, new int[] { 2, 3, 2, 3 }, // Lumberaxe
            new int[] { 10, 8, 9, 9 }, new int[] { 2, 3, 2, 3 }, // Excavator
            new int[] {  8, 8, 9, 8 }, new int[] { 2, 3, 3, 3 }, // Scythe
            new int[] {  6, 8, 9, 8 }, new int[] { 3, 3, 2, 3 }, // Cleaver
            new int[] {  6, 8, 6, 9 }, new int[] { 2, 3, 2, 3 }, // Battleaxe
    };

    void addToolButtons ()
    {
        final ToolCore[] tier1Tools = {pickaxe, shovel, hatchet, mattock, broadsword, longsword, rapier, dagger, frypan, battlesign, chisel};
        final ToolCore[] tier2Tools = {hammer, lumberaxe, excavator, scythe, cleaver, battleaxe};

        // repair
        addToolButton(0, 3, 0, new int[] { 0, 1, 2, 13 }, new int[] { 13, 13, 13, 13 }, "gui.toolforge1", "gui.toolforge2");

        // tier 1 tools
        for (int i = 0; i < tier1Tools.length; i++)
        {
            String locString = String.format("gui.toolstation.%s.desc", tier1Tools[i].getToolName().toLowerCase());
            addToolButton(itemIconsT1[i][0], itemIconsT1[i][1], itemIconsT1[i][2], iconCoordsT1[i * 2], iconCoordsT1[i * 2 + 1], tier1Tools[i].getLocalizedToolName(), locString);
        }

        // tier 2 tools
        for (int i = 0; i < tier2Tools.length; i++)
        {
            String locString = String.format("gui.toolstation.%s.desc", tier2Tools[i].getToolName().toLowerCase());
            addTierTwoButton(itemIconsT2[i][0], itemIconsT2[i][1], itemIconsT2[i][2], iconCoordsT2[i * 2], iconCoordsT2[i * 2 + 1], tier2Tools[i].getLocalizedToolName(), locString);
        }
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

    }
}
