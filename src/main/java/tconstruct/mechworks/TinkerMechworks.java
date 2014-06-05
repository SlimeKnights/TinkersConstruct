package tconstruct.mechworks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import tconstruct.armor.TinkerArmor;
import tconstruct.blocks.SlabBase;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.blocks.slime.SlimeFluid;
import tconstruct.blocks.slime.SlimeGel;
import tconstruct.blocks.slime.SlimeGrass;
import tconstruct.blocks.slime.SlimeLeaves;
import tconstruct.blocks.slime.SlimeSapling;
import tconstruct.blocks.slime.SlimeTallGrass;
import tconstruct.blocks.traps.BarricadeBlock;
import tconstruct.blocks.traps.Punji;
import tconstruct.client.StepSoundSlime;
import tconstruct.common.itemblocks.MetadataItemBlock;
import tconstruct.library.TConstructRegistry;
import tconstruct.mechworks.blocks.BlockLandmine;
import tconstruct.mechworks.itemblocks.ItemBlockLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.blocks.MetalOre;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.blocks.MultiBrick;
import tconstruct.tools.blocks.MultiBrickFancy;
import tconstruct.tools.itemblocks.MultiBrickFancyItem;
import tconstruct.tools.itemblocks.MultiBrickItem;
import tconstruct.world.TinkerWorld;
import tconstruct.world.blocks.ConveyorBase;
import tconstruct.world.blocks.GravelOre;
import tconstruct.world.blocks.MeatBlock;
import tconstruct.world.blocks.OreberryBush;
import tconstruct.world.blocks.OreberryBushEssence;
import tconstruct.world.blocks.SlimeExplosive;
import tconstruct.world.blocks.SlimePad;
import tconstruct.world.blocks.StoneLadder;
import tconstruct.world.blocks.StoneTorch;
import tconstruct.world.blocks.WoodRail;
import tconstruct.world.itemblocks.BarricadeItem;
import tconstruct.world.itemblocks.GravelOreItem;
import tconstruct.world.itemblocks.HamboneItemBlock;
import tconstruct.world.itemblocks.MetalOreItemBlock;
import tconstruct.world.itemblocks.OreberryBushItem;
import tconstruct.world.itemblocks.OreberryBushSecondItem;
import tconstruct.world.itemblocks.SlimeGelItemBlock;
import tconstruct.world.itemblocks.SlimeGrassItemBlock;
import tconstruct.world.itemblocks.SlimeLeavesItemBlock;
import tconstruct.world.itemblocks.SlimeSaplingItemBlock;
import tconstruct.world.itemblocks.SlimeTallGrassItem;
import tconstruct.world.itemblocks.WoolSlab1Item;
import tconstruct.world.itemblocks.WoolSlab2Item;
import tconstruct.world.items.OreBerries;
import tconstruct.world.items.StrangeFood;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "TinkerMechworks", name = "TinkerMechworks", version = "${tinkermechworksversion}")
public class TinkerMechworks
{

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        // Traps
        GameRegistry.registerBlock(TinkerWorld.landmine, ItemBlockLandmine.class, "Redstone.Landmine");
        GameRegistry.registerTileEntity(TileEntityLandmine.class, "Landmine");
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
