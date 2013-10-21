package tconstruct.plugins.misc;

import java.lang.reflect.Field;

import net.minecraftforge.event.EventBus;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;

@Mod(modid = "TConstruct|PeaceOfMind", name = "TConstruct|PeaceOfMind", version = "1.0", dependencies = "after:GregTech-Addon")
public class PeaceOfMind
{
    public PeaceOfMind()
    {
        for (ModContainer o : Loader.instance().getModList())
        {
            if (o.getModId().equals("GregTech-Addon"))
            {
                try
                {
                    ModContainer mod = o;
                    Field ann = FMLModContainer.class.getDeclaredField("eventBus");
                    ann.setAccessible(true);
                    EventBus bus = (EventBus) ann.get(mod);
                    bus.unregister(mod);
                }
                catch (Exception e)
                {
                    System.err.println("Cannot unregister GregTech");
                }
            }
        }

    }
}
