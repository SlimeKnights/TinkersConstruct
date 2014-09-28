package tconstruct.mechworks;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.mechworks.blocks.BlockLandmine;
import tconstruct.mechworks.entity.item.*;
import tconstruct.mechworks.itemblocks.ItemBlockLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.tools.TinkerTools;

@ObjectHolder(TConstruct.modID)
//TODO handle migration of all items/blocks that were owned by the previously seperate mod
@Pulse(id = "Tinkers' Mechworks", description = "Mechanical machinations and steampunk inspired shenanigans.")
public class TinkerMechworks
{
    @SidedProxy(clientSide = "tconstruct.mechworks.MechworksProxyClient", serverSide = "tconstruct.mechworks.MechworksProxyCommon")
    public static MechworksProxyCommon proxy;
    // Traps
    public static Block landmine;

    @Handler
    public void preInit (FMLPreInitializationEvent event)
    {
        //NetworkRegistry.INSTANCE.registerGuiHandler(TConstruct.instance, proxy);
        // Traps
        TinkerMechworks.landmine = new BlockLandmine().setHardness(0.5F).setResistance(0F).setStepSound(Block.soundTypeMetal).setCreativeTab(CreativeTabs.tabRedstone).setBlockName("landmine");
        GameRegistry.registerBlock(TinkerMechworks.landmine, ItemBlockLandmine.class, "Redstone.Landmine");
        GameRegistry.registerTileEntity(TileEntityLandmine.class, "Landmine");

        // Landmine Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 0), "mcm", "rpr", 'm', "plankWood", 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r', Items.redstone, 'p', Blocks.stone_pressure_plate));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 1), "mcm", "rpr", 'm', Blocks.stone, 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r', Items.redstone, 'p', Blocks.stone_pressure_plate));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 2), "mcm", "rpr", 'm', Blocks.obsidian, 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r', Items.redstone, 'p', Blocks.stone_pressure_plate));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 3), "mcm", "rpr", 'm', Items.repeater, 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r', Items.redstone, 'p', Blocks.stone_pressure_plate));

        EntityRegistry.registerModEntity(EntityLandmineFirework.class, "LandmineFirework", 5, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ExplosivePrimed.class, "SlimeExplosive", 6, TConstruct.instance, 32, 5, true);
    }

    @Handler
    public void init (FMLInitializationEvent event)
    {
        proxy.initialize();
    }

    @Handler
    public void postInit (FMLPostInitializationEvent evt)
    {

    }
}
