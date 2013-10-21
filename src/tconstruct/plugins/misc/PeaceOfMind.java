package tconstruct.plugins.misc;

import java.lang.reflect.Field;

import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;

@Mod(modid = "TConstruct|PeaceOfMind", name = "TConstruct|PeaceOfMind", version = "1.0")
public class PeaceOfMind
{
    public PeaceOfMind()
    {
        System.out.println("Debug mod IDs go!");
        for (ModContainer o : Loader.instance().getModList())
        {
            System.out.println("Mod ID: "+o.getModId());
            if (o.getModId().toLowerCase().contains("gregtech"))
            {
                System.out.println("GregTech detected, attempting to unregister");
                try
                {
                    ModContainer mod = o;
                    Field ann = FMLModContainer.class.getDeclaredField("eventBus");
                    ann.setAccessible(true);
                    com.google.common.eventbus.EventBus bus = (com.google.common.eventbus.EventBus) ann.get(mod);
                    bus.unregister(mod);
                }
                catch (Exception e)
                {
                    System.err.println("Cannot unregister GregTech");
                    e.printStackTrace();
                }
            }
        }

    }
}
