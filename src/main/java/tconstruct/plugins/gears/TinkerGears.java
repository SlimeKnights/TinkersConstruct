package tconstruct.plugins.gears;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.items.Pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Pulse(id = "Tinkers Gears", description = "Adds a gear cast if other mods provide gears", pulsesRequired = "Tinkers' Smeltery")
public class TinkerGears {
    public static Item gearCast;

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        TConstruct.logger.info("Gear module active. Adding gear cast.");
        gearCast = new GearCast();

        GameRegistry.registerItem(gearCast, "gearCast");
    }

    @Handler
    public void postInit(FMLPostInitializationEvent event) {
        ItemStack cast = new ItemStack(gearCast);
        FluidStack aluCastLiquid = new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue);
        FluidStack goldCastLiquid = new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue*2);

        // find all gears in the registry
        for(String oreName : OreDictionary.getOreNames()) {
            if(!oreName.startsWith("gear"))
                continue;

            List<ItemStack> gears = OreDictionary.getOres(oreName);

            // register every gear besides wooden gear for creating a gear cast
            if(!oreName.equals("gearWood")) {
                for(ItemStack g : gears) {
                    TConstructRegistry.getTableCasting().addCastingRecipe(cast, aluCastLiquid, g, false, 50);
                    TConstructRegistry.getTableCasting().addCastingRecipe(cast, goldCastLiquid, g, false, 50);
                }
            }

            // find a fluid that fits the gear
            String material = oreName.substring(4);
            // try the oredict name directly
            Fluid fluid = FluidRegistry.getFluid(material);
            // or lowercased
            if(fluid == null)
                fluid = FluidRegistry.getFluid(material.toLowerCase());
            // or in the tinkers liquid format
            if(fluid == null)
                fluid = FluidRegistry.getFluid(material.toLowerCase() + ".molten");

            // found one?
            if(fluid != null) {
                ItemStack gear = gears.get(0);
                FluidStack liquid = new FluidStack(fluid.getID(), TConstruct.ingotLiquidValue*4);
                TConstructRegistry.getTableCasting().addCastingRecipe(gear, liquid, cast, 55);
            }
        }
    }
}
