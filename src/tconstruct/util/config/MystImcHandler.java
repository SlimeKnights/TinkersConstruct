package tconstruct.util.config;

import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.common.TContent;
public class MystImcHandler {
public static String[] FluidBlackList = new String[]{"moltenInvar", "moltenElectrum", "moltenBronze","moltenAluminumBrass","moltenManyullyn","MoltenAlumite", "moltenCobalt","moltenArdite"};// = new String[]();

	public static void blacklistFluids(){
		for(String nm: FluidBlackList){
			// check if exists??
			SendFluidIMCBLMsg(nm);
		}
	}
	public static void SendFluidIMCBLMsg(String FluidName){
		NBTTagCompound NBTMsg = new NBTTagCompound();
		NBTMsg.setCompoundTag("fluidsymbol",new NBTTagCompound());
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("rarity", 0.0F);
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("grammarweight", 0.0F);
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("instabilityPerBlock ", 10000F);// renders creative symbol useless
		NBTMsg.getCompoundTag("fluidsymbol").setString("fluidname", FluidName);
		FMLInterModComms.sendMessage("Mystcraft", "fluidsymbol", NBTMsg);
	}
	
}