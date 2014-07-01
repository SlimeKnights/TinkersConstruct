package tconstruct.mechworks;

import mantle.pulsar.pulse.IPulse;
import mantle.pulsar.pulse.Pulse;
import mantle.pulsar.pulse.PulseProxy;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.armor.ArmorProxyCommon;
import tconstruct.mechworks.blocks.BlockLandmine;
import tconstruct.mechworks.entity.item.EntityLandmineFirework;
import tconstruct.mechworks.entity.item.ExplosivePrimed;
import tconstruct.mechworks.itemblocks.ItemBlockLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.tools.TinkerTools;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(TConstruct.modID)
//TODO handle migration of all items/blocks that were owned by the previously seperate mod
@Pulse(id = "TinkerMechworks", description = "Mechanical machinations and steampunk inspired shenanigans.")
public class TinkerMechworks implements IPulse //TODO: Remove IPulse implementation, keep annotation
{
    @PulseProxy(client = "tconstruct.mechworks.MechworksProxyClient", server = "tconstruct.mechworks.MechworksProxyCommon")
    public static MechworksProxyCommon proxy;
    public TinkerMechworks()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(TConstruct.instance, proxy);
    }
    // Traps
    public static Block landmine;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        // Traps
        TinkerMechworks.landmine = new BlockLandmine().setHardness(0.5F).setResistance(0F).setStepSound(Block.soundTypeMetal).setCreativeTab(CreativeTabs.tabRedstone).setBlockName("landmine");
        GameRegistry.registerBlock(TinkerMechworks.landmine, ItemBlockLandmine.class, "Redstone.Landmine");
        GameRegistry.registerTileEntity(TileEntityLandmine.class, "Landmine");

        // Landmine Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 0), "mcm", "rpr", 'm', "plankWood", 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r',
                Items.redstone, 'p', Blocks.stone_pressure_plate));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 1), "mcm", "rpr", 'm', Blocks.stone, 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r',
                Items.redstone, 'p', Blocks.stone_pressure_plate));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 2), "mcm", "rpr", 'm', Blocks.obsidian, 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r',
                Items.redstone, 'p', Blocks.stone_pressure_plate));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerMechworks.landmine, 1, 3), "mcm", "rpr", 'm', Items.repeater, 'c', new ItemStack(TinkerTools.blankPattern, 1, 1), 'r',
                Items.redstone, 'p', Blocks.stone_pressure_plate));

        EntityRegistry.registerModEntity(EntityLandmineFirework.class, "LandmineFirework", 5, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ExplosivePrimed.class, "SlimeExplosive", 6, TConstruct.instance, 32, 5, true);
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent evt)
    {

    }
}
