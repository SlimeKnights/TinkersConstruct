package tconstruct.plugins.imc;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.TConstruct;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers Mystcraft Compatibility", forced = true, modsRequired = "Mystcraft")
public class TinkerMystcraft
{
    private static String[] fluids = new String[] { "invar.molten", "electrum.molten", "bronze.molten", "aluminumbrass.molten", "manyullyn.molten", "alumite.molten", "cobalt.molten", "moltenArdite", "ender", "steel.molten", "platinum.molten" };

    @Handler
    public void init (FMLInitializationEvent event)
    {
        TConstruct.logger.info("Mystcraft detected. Blacklisting Mystcraft fluid symbols.");
        for (String nm : fluids)
            sendFluidBlacklist(nm);
    }

    private void sendFluidBlacklist (String FluidName)
    {
        NBTTagCompound NBTMsg = new NBTTagCompound();
        NBTMsg.setTag("fluidsymbol", new NBTTagCompound());
        NBTMsg.getCompoundTag("fluidsymbol").setFloat("rarity", 0.0F);
        NBTMsg.getCompoundTag("fluidsymbol").setFloat("grammarweight", 0.0F);
        NBTMsg.getCompoundTag("fluidsymbol").setFloat("instabilityPerBlock", 10000F); // renders creative symbol useless
        NBTMsg.getCompoundTag("fluidsymbol").setString("fluidname", FluidName);
        FMLInterModComms.sendMessage("Mystcraft", "fluidsymbol", NBTMsg);
    }

}
