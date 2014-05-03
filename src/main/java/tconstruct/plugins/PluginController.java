package tconstruct.plugins;

import static tconstruct.TConstruct.moduleLoader;
import tconstruct.plugins.fmp.ForgeMultiPart;
import tconstruct.plugins.ic2.IC2;
import tconstruct.plugins.imc.AppEng;
import tconstruct.plugins.imc.BuildcraftTransport;
import tconstruct.plugins.imc.Mystcraft;
import tconstruct.plugins.imc.Thaumcraft;
import tconstruct.plugins.nei.NotEnoughItems;

public class PluginController
{

    private PluginController() {} // Don't need to instantiate this.

    public static void registerModules()
    {
        // Mystcraft is pushed in through the backdoor so it can't be disabled.
        moduleLoader.registerUncheckedModule(Mystcraft.class);

        // Register the remaining plugin classes normally
        moduleLoader.registerModule(AppEng.class);
        moduleLoader.registerModule(BuildcraftTransport.class);
        moduleLoader.registerModule(ForgeMultiPart.class);
        moduleLoader.registerModule(IC2.class);
        moduleLoader.registerModule(Thaumcraft.class);
        moduleLoader.registerModule(NotEnoughItems.class);
        
    }   

}
