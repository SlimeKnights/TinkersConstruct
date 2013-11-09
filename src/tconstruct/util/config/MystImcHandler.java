package tconstruct.util.config;

import java.util.ArrayList;

import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.common.TContent;

public class MystImcHandler
{
    private static String[] FluidBlackList = new String[] { "invar.molten", "electrum.molten", "bronze.molten", "aluminumbrass.molten", "manyullyn.molten", "alumite.molten", "cobalt.molten",
            "moltenArdite", "ender", "steel.molten" };// = new String[]();
    private static ArrayList<String> additionalFluids = new ArrayList<String>();

    public static void addFluidToBlacklist (String fluidName)
    {
        additionalFluids.add(fluidName);
    }

    public static void blacklistFluids ()
    {
        for (String nm :additionalFluids){
         // check if exists??
            SendFluidIMCBLMsg(nm);
        }
        for (String nm : FluidBlackList)
        {
            // check if exists??
            SendFluidIMCBLMsg(nm);
        }
    }

    public static void SendFluidIMCBLMsg (String FluidName)
    {
        NBTTagCompound NBTMsg = new NBTTagCompound();
        NBTMsg.setCompoundTag("fluidsymbol", new NBTTagCompound());
        NBTMsg.getCompoundTag("fluidsymbol").setFloat("rarity", 0.0F);
        NBTMsg.getCompoundTag("fluidsymbol").setFloat("grammarweight", 0.0F);
        NBTMsg.getCompoundTag("fluidsymbol").setFloat("instabilityPerBlock", 10000F);// renders creative symbol useless
        NBTMsg.getCompoundTag("fluidsymbol").setString("fluidname", FluidName);
        FMLInterModComms.sendMessage("Mystcraft", "fluidsymbol", NBTMsg);
    }

}
