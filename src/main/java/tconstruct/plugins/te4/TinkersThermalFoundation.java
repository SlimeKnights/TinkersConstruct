package tconstruct.plugins.te4;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.world.TinkerWorld;

import java.util.ArrayList;

@GameRegistry.ObjectHolder(TinkersThermalFoundation.TF_MOD_ID)
@Pulse(id = "Tinkers Thermal Foundation Compatibility", description = "Tinkers Construct compatibility for Thermal Foundation", modsRequired = TinkersThermalFoundation.TF_MOD_ID, forced = true)
public class TinkersThermalFoundation {
    static final String TF_MOD_ID = "ThermalFoundation";

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        registerFluidType("Nickel", 400, TinkerSmeltery.moltenNickelFluid);
        registerFluidType("Lead", 400, TinkerSmeltery.moltenLeadFluid);
        registerFluidType("Silver", 400, TinkerSmeltery.moltenSilverFluid);
        registerFluidType("Platinum", 400, TinkerSmeltery.moltenShinyFluid);
        registerFluidType("Invar", 400, TinkerSmeltery.moltenInvarFluid);
        registerFluidType("Electrum", 400, TinkerSmeltery.moltenElectrumFluid);
        registerFluidType("Lumium", 370, TinkerSmeltery.moltenLumiumFluid);
        registerFluidType("Signalum", 450, TinkerSmeltery.moltenSignalumFluid);
        registerFluidType("Mithril", 800, TinkerSmeltery.moltenMithrilFluid);
        registerFluidType("Enderium", 1000, TinkerSmeltery.moltenEnderiumFluid);
    }

    @Handler
    public void init(FMLInitializationEvent event) {
        // melt stuff in the smeltery
        Fluid pyrotheumFluid = FluidRegistry.getFluid("pyrotheum");
        Fluid cryotheumFluid = FluidRegistry.getFluid("cryotheum");
        Fluid redstoneFluid = FluidRegistry.getFluid("redstone");
        Fluid glowstoneFluid = FluidRegistry.getFluid("glowstone");
        Fluid coalFluid = FluidRegistry.getFluid("coal");

        //pyrotheum fuel
        // register pyrotheum if it's present
        Smeltery.addSmelteryFuel(pyrotheumFluid, 5000, 70); // pyrotheum lasts 3.5 seconds per 15 mb

        // liquid redstone
        for(ItemStack stack : OreDictionary.getOres("blockRedstone"))
            Smeltery.addMelting(stack, Block.getBlockFromItem(stack.getItem()), stack.getItemDamage(), 3000, new FluidStack(redstoneFluid, 900));
        for(ItemStack stack : OreDictionary.getOres("dustRedstone"))
            Smeltery.addMelting(stack, Blocks.redstone_block, stack.getItemDamage(), 2500, new FluidStack(redstoneFluid, 100));

        // liquid glowstone
        for(ItemStack stack : OreDictionary.getOres("glowstone"))
            Smeltery.addMelting(stack, Block.getBlockFromItem(stack.getItem()), stack.getItemDamage(), 3000, new FluidStack(glowstoneFluid, 1000));
        for(ItemStack stack : OreDictionary.getOres("dustGlowstone"))
            Smeltery.addMelting(stack, Blocks.glowstone, stack.getItemDamage(), 2500, new FluidStack(glowstoneFluid, 250));

        // liquid pyrotheum
        for(ItemStack stack : OreDictionary.getOres("dustPyrotheum"))
            Smeltery.addMelting(stack, Blocks.glowstone, stack.getItemDamage(), 4000, new FluidStack(pyrotheumFluid, 100));

        // liquid cryotheum
        for(ItemStack stack : OreDictionary.getOres("dustCryotheum"))
            Smeltery.addMelting(stack, Blocks.snow, stack.getItemDamage(), 4000, new FluidStack(cryotheumFluid, 100));

        // liquid coal
        for(ItemStack stack : OreDictionary.getOres("dustCoal"))
            Smeltery.addMelting(stack, Blocks.coal_block, stack.getItemDamage(), 4000, new FluidStack(coalFluid, 100));

        // Alloying
        int amount = TConstruct.ingotLiquidValue;
        FluidStack result, part1, part2, part3;

        // Invar
        result = new FluidStack(TinkerSmeltery.moltenInvarFluid, amount * 3);
        part1 = new FluidStack(TinkerSmeltery.moltenIronFluid, amount * 2);
        part2 = new FluidStack(TinkerSmeltery.moltenNickelFluid, amount);
        Smeltery.addAlloyMixing(result, part1, part2);

        // Electrum
        result = new FluidStack(TinkerSmeltery.moltenElectrumFluid, amount * 2);
        part1 = new FluidStack(TinkerSmeltery.moltenGoldFluid, amount);
        part2 = new FluidStack(TinkerSmeltery.moltenSilverFluid, amount);
        Smeltery.addAlloyMixing(result, part1, part2);

        // Lumium
        result = new FluidStack(TinkerSmeltery.moltenLumiumFluid, amount * 4);
        part1 = new FluidStack(TinkerSmeltery.moltenSilverFluid, amount);
        part2 = new FluidStack(TinkerSmeltery.moltenTinFluid, amount * 3);
        part3 = new FluidStack(glowstoneFluid, amount);
        Smeltery.addAlloyMixing(result, part1, part2, part3);

        // Signalum
        result = new FluidStack(TinkerSmeltery.moltenSignalumFluid, amount * 4);
        part1 = new FluidStack(TinkerSmeltery.moltenSilverFluid, amount);
        part2 = new FluidStack(TinkerSmeltery.moltenCopperFluid, amount * 3);
        part3 = new FluidStack(redstoneFluid, amount);
        Smeltery.addAlloyMixing(result, part1, part2, part3);

        // Enderium
        result = new FluidStack(TinkerSmeltery.moltenEnderiumFluid, amount * 4);
        part1 = new FluidStack(TinkerSmeltery.moltenSilverFluid, amount);
        part2 = new FluidStack(TinkerSmeltery.moltenTinFluid, amount * 2);
        part3 = new FluidStack(TinkerSmeltery.moltenShinyFluid, amount);
        Smeltery.addAlloyMixing(result, part1, part2, part3, new FluidStack(TinkerSmeltery.moltenEnderFluid, amount));
    }


    private void registerFluidType(String name, int temp, Fluid fluid) {
        ItemStack stack = GameRegistry.findItemStack(TF_MOD_ID, "block" + name, 1);
        if(stack == null || stack.getItem() == null)
            stack = new ItemStack(TinkerWorld.metalBlock);

        FluidType.registerFluidType(name, Block.getBlockFromItem(stack.getItem()), stack.getItemDamage(), temp, fluid, false);
    }
}
