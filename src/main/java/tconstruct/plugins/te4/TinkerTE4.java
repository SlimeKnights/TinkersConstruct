package tconstruct.plugins.te4;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.*;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers TE4 Compatibility", description = "Tinkers Construct compatibility for Thermal Expansion", modsRequired = "ThermalExpansion", forced = true)
public class TinkerTE4
{
    @Handler
    public void init ()
    {
        TConstruct.logger.info("Thermal Expansion detected. Adding Pulverizer & Induction Smelter recipes");
        ItemStack crystalCinnabar = OreDictionary.getOres("crystalCinnabar").get(0);

        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, 11), new ItemStack(TinkerTools.materials, 1, 40), null, 0);
        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, 3), new ItemStack(TinkerTools.materials, 1, 39), null, 0);
        TE4Helper.addPulverizerRecipe(1000, new ItemStack(TinkerTools.materials, 1, 4), new ItemStack(TinkerTools.materials, 1, 38), null, 0);

        TE4Helper.addPulverizerRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 1), new ItemStack(TinkerTools.materials, 2, 39), GameRegistry.findItemStack("ThermalExpansion", "dustIron", 1), 10);
        TE4Helper.addSmelterRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 1), crystalCinnabar.copy(), new ItemStack(TinkerTools.materials, 3, 3), new ItemStack(Items.iron_ingot), 100);
        TE4Helper.addPulverizerRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 2), new ItemStack(TinkerTools.materials, 2, 38), GameRegistry.findItemStack("ThermalExpansion", "dustGold", 1), 10);
        TE4Helper.addSmelterRecipe(12000, new ItemStack(TinkerWorld.oreSlag, 1, 2), crystalCinnabar.copy(), new ItemStack(TinkerTools.materials, 3, 4), new ItemStack(Items.gold_ingot), 100);

        TE4Helper.addSmelterRecipe(4000, new ItemStack(TinkerTools.materials, 1, 4), new ItemStack(TinkerTools.materials, 1, 3), new ItemStack(TinkerTools.materials, 1, 5), null, 0);
        TE4Helper.addSmelterRecipe(4000, new ItemStack(TinkerTools.materials, 1, 9), new ItemStack(TinkerTools.materials, 3, 11), new ItemStack(TinkerTools.materials, 4, 14), null, 0);

        // Dust Recipes
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 1, 41), "dustArdite", "dustCobalt"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TinkerTools.materials, 4, 42), "dustAluminum", "dustAluminum", "dustAluminum", "dustCopper"));
    }

    @Handler
    public void postInit(FMLPostInitializationEvent event) {
        Fluid redstoneFluid = FluidRegistry.getFluid("redstone");
        Fluid glowstoneFluid = FluidRegistry.getFluid("glowstone");
        Fluid enderFluid = FluidRegistry.getFluid("ender");

        // selected fluid transposer recipies
        // tesseract
        ItemStack tesseractEmpty = GameRegistry.findItemStack("ThermalExpansion", "frameTesseractEmpty" ,1);
        ItemStack tesseractFull = GameRegistry.findItemStack("ThermalExpansion", "frameTesseractFull" ,1);
        TConstructRegistry.getBasinCasting().addCastingRecipe(tesseractFull, new FluidStack(enderFluid, 1000), tesseractEmpty, true, 300);

        // redstone energy cell
        ItemStack cellFrameEmpty = GameRegistry.findItemStack("ThermalExpansion", "frameCellReinforcedEmpty", 1);
        ItemStack cellFrameFull = GameRegistry.findItemStack("ThermalExpansion", "frameCellReinforcedFull", 1);
        TConstructRegistry.getBasinCasting().addCastingRecipe(cellFrameFull, new FluidStack(redstoneFluid, 4000), cellFrameEmpty, true, 100);

        // glowstone illuminator
        ItemStack illuminatorEmpty = GameRegistry.findItemStack("ThermalExpansion", "frameIlluminator" ,1);
        ItemStack illuminator = GameRegistry.findItemStack("ThermalExpansion", "illuminator" ,1);
        TConstructRegistry.getBasinCasting().addCastingRecipe(illuminator, new FluidStack(glowstoneFluid, 1000), illuminatorEmpty, true, 300);

        // plates
        ItemStack plate = GameRegistry.findItemStack("ThermalExpansion", "plateFrame" ,1);
        ItemStack plateRedstone = GameRegistry.findItemStack("ThermalExpansion", "plateSignal" ,1);
        ItemStack plateGlowstone = GameRegistry.findItemStack("ThermalExpansion", "plateImpulse" ,1);
        ItemStack plateEnder = GameRegistry.findItemStack("ThermalExpansion", "plateTranslocate" ,1);

        TConstructRegistry.getTableCasting().addCastingRecipe(plateRedstone, new FluidStack(redstoneFluid, 1000), plate, true, 100);
        TConstructRegistry.getTableCasting().addCastingRecipe(plateGlowstone, new FluidStack(glowstoneFluid, 1000), plate, true, 100);
        TConstructRegistry.getTableCasting().addCastingRecipe(plateEnder, new FluidStack(enderFluid, 1000), plate, true, 100);
    }
}
