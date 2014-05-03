package tconstruct.plugins.imc;

import mantle.module.ILoadableModule;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import cpw.mods.fml.common.event.FMLInterModComms;

public class BuildcraftTransport implements ILoadableModule
{

    @SuppressWarnings("unused")
    public static String modId = "BuildCraft|Transport";

    @Override
    public void preInit ()
    {

    }

    @Override
    public void init ()
    {
        TConstruct.logger.info("[BC|Transport] Registering facades.");
        // Smeltery Blocks
        addFacade(TRepo.smeltery, 2);
        for (int sc = 4; sc < 11; sc++)
        {
            addFacade(TRepo.smeltery, sc);
        }

        addFacade(TRepo.searedBlock, 0);
        addFacade(TRepo.searedBlockNether, 0);
    }

    @Override
    public void postInit ()
    {

    }

    private void addFacade (Block b, int meta)
    {
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", new ItemStack(b, 1, meta));
    }

}
