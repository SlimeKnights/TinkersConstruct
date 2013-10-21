package tconstruct.plugins.misc;

import java.lang.reflect.Field;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;

@Mod(modid = "TConstruct|PeaceOfMind", name = "TConstruct|PeaceOfMind", version = "1.0")
public class PeaceOfMind
{
    public PeaceOfMind()
    {
        for (ModContainer o : Loader.instance().getModList())
        {
            if (o.getModId().toLowerCase().contains("gregtech"))
            {
                try
                {
                    /** Explicit permission: http://forum.industrial-craft.net/index.php?page=Thread&postID=121457#post121457 */
                    ModContainer mod = o;
                    Field ann = FMLModContainer.class.getDeclaredField("eventBus");
                    ann.setAccessible(true);
                    com.google.common.eventbus.EventBus googlebus = (com.google.common.eventbus.EventBus) ann.get(mod);
                    googlebus.unregister(mod);
                    MinecraftForge.EVENT_BUS.unregister(mod);
                    
                    Class clazz = Class.forName("gregtechmod.common.GT_OreDictHandler");
                    MinecraftForge.EVENT_BUS.unregister(clazz);
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
