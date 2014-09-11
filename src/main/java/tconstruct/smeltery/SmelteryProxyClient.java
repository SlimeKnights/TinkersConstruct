package tconstruct.smeltery;

import cpw.mods.fml.client.registry.*;
import mantle.client.MProxyClient;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.RenderBlockFluid;
import tconstruct.armor.TinkerArmor;
import tconstruct.armor.modelblock.*;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.client.pages.BlockCastPage;
import tconstruct.common.TProxyCommon;
import tconstruct.smeltery.gui.*;
import tconstruct.smeltery.logic.*;
import tconstruct.smeltery.model.*;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

public class SmelteryProxyClient extends SmelteryProxyCommon
{
    @Override
    public void initialize ()
    {
        registerRenderer();
        registerGuiHandler();
        registerManualIcons();
        registerManualRecipes();
        MinecraftForge.EVENT_BUS.register(this);
    }

    void registerRenderer ()
    {
        RenderingRegistry.registerBlockHandler(new TankRender());
        RenderingRegistry.registerBlockHandler(new TankAirRender());
        RenderingRegistry.registerBlockHandler(new CastingBlockRender());
        RenderingRegistry.registerBlockHandler(new DryingRackRender());
        RenderingRegistry.registerBlockHandler(new PaneRender());
        RenderingRegistry.registerBlockHandler(new PaneConnectedRender());
        RenderingRegistry.registerBlockHandler(new RenderBlockFluid());
        RenderingRegistry.registerBlockHandler(new BlockRenderCastingChannel());

        if (!PHConstruct.newSmeltery) {
            RenderingRegistry.registerBlockHandler(new SmelteryRender());
        }

        ClientRegistry.bindTileEntitySpecialRenderer(CastingTableLogic.class, new CastingTableSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CastingBasinLogic.class, new CastingBasinSpecialRender());
        ClientRegistry.bindTileEntitySpecialRenderer(DryingRackLogic.class, new DryingRackSpecialRender());
    }

    void registerManualIcons ()
    {
        MantleClientRegistry.registerManualIcon("smelterybook", new ItemStack(TinkerTools.manualBook, 1, 2));
        MantleClientRegistry.registerManualIcon("smeltery", new ItemStack(TinkerSmeltery.smeltery));
        MantleClientRegistry.registerManualIcon("blankcast", new ItemStack(TinkerTools.blankPattern, 1, 1));
        MantleClientRegistry.registerManualIcon("castingtable", new ItemStack(TinkerSmeltery.searedBlock));
        // MantleClientRegistry.registerManualIcon("liquidiron", new
        // ItemStack(TRepo.liquidMetalStill));
        MantleClientRegistry.registerManualIcon("lavatank", new ItemStack(TinkerSmeltery.lavaTank));
        MantleClientRegistry.registerManualIcon("searedbrick", new ItemStack(TinkerSmeltery.smeltery, 1, 2));
        MantleClientRegistry.registerManualIcon("drain", new ItemStack(TinkerSmeltery.smeltery, 1, 1));
        MantleClientRegistry.registerManualIcon("faucet", new ItemStack(TinkerSmeltery.searedBlock, 1, 1));

        MantleClientRegistry.registerManualIcon("bloodbucket", new ItemStack(TinkerSmeltery.buckets, 1, 16));
        MantleClientRegistry.registerManualIcon("emeraldbucket", new ItemStack(TinkerSmeltery.buckets, 1, 15));
        MantleClientRegistry.registerManualIcon("gluebucket", new ItemStack(TinkerSmeltery.buckets, 1, 25));
        MantleClientRegistry.registerManualIcon("slimebucket", new ItemStack(TinkerSmeltery.buckets, 1, 24));
        MantleClientRegistry.registerManualIcon("enderbucket", new ItemStack(TinkerSmeltery.buckets, 1, 23));

        MProxyClient.registerManualPage("blockcast", BlockCastPage.class);
    }

    void registerManualRecipes ()
    {
        ItemStack sand = new ItemStack(Blocks.sand, 1, 0);
        ItemStack gravel = new ItemStack(Blocks.gravel, 1, 0);
        ItemStack clay = new ItemStack(Items.clay_ball, 1, 0);
        ItemStack glass = new ItemStack(Blocks.glass, 1, 0);

        ItemStack grout = new ItemStack(TinkerTools.craftedSoil, 2, 1);
        ItemStack searedbrick = new ItemStack(TinkerTools.materials, 1, 2);
        ItemStack searedbrickBlock = new ItemStack(TinkerSmeltery.smeltery, 1, 2);
        ItemStack plankSlab = new ItemStack(Blocks.wooden_slab, 1, 0);

        MantleClientRegistry.registerManualSmallRecipe("grout", grout, sand, gravel, null, clay);
        MantleClientRegistry.registerManualFurnaceRecipe("searedbrick", searedbrick, grout);
        MantleClientRegistry.registerManualSmallRecipe("searedbricks", new ItemStack(TinkerSmeltery.smeltery, 1, 2), searedbrick, searedbrick, searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterycontroller", new ItemStack(TinkerSmeltery.smeltery, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, null, searedbrick, searedbrick, searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("dryingrack", new ItemStack(TinkerArmor.dryingRack, 1, 0), null, null, null, plankSlab, plankSlab, plankSlab, null, null, null);

        MantleClientRegistry.registerManualLargeRecipe("smelterydrain", new ItemStack(TinkerSmeltery.smeltery, 1, 1), searedbrick, null, searedbrick, searedbrick, null, searedbrick, searedbrick, null, searedbrick);

        MantleClientRegistry.registerManualLargeRecipe("smelterytank1", new ItemStack(TinkerSmeltery.lavaTank, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, glass, searedbrick, searedbrick, searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterytank2", new ItemStack(TinkerSmeltery.lavaTank, 1, 1), searedbrick, glass, searedbrick, glass, glass, glass, searedbrick, glass, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterytank3", new ItemStack(TinkerSmeltery.lavaTank, 1, 2), searedbrick, glass, searedbrick, searedbrick, glass, searedbrick, searedbrick, glass, searedbrick);

        MantleClientRegistry.registerManualLargeRecipe("smelterytable", new ItemStack(TinkerSmeltery.searedBlock, 1, 0), searedbrick, searedbrick, searedbrick, searedbrick, null, searedbrick, searedbrick, null, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelteryfaucet", new ItemStack(TinkerSmeltery.searedBlock, 1, 1), searedbrick, null, searedbrick, null, searedbrick, null, null, null, null);
        MantleClientRegistry.registerManualLargeRecipe("castingchannel", new ItemStack(TinkerSmeltery.castingChannel), null, null, null, searedbrick, null, searedbrick, searedbrick, searedbrick, searedbrick);
        MantleClientRegistry.registerManualLargeRecipe("smelterybasin", new ItemStack(TinkerSmeltery.searedBlock, 1, 2), searedbrick, null, searedbrick, searedbrick, null, searedbrick, searedbrick, searedbrick, searedbrick);
    }

    @Override
    protected void registerGuiHandler ()
    {
        TProxyCommon.registerClientGuiHandler(smelteryGuiID, this);
        TProxyCommon.registerServerGuiHandler(smelteryGuiID, this);
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == SmelteryProxyCommon.smelteryGuiID)
        {
            if (PHConstruct.newSmeltery)
                return new AdaptiveSmelteryGui(player.inventory, (AdaptiveSmelteryLogic) world.getTileEntity(x, y, z), world, x, y, z);
            else
                return new SmelteryGui(player.inventory, (SmelteryLogic) world.getTileEntity(x, y, z), world, x, y, z);
        }
        return null;
    }
}
