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
    public static boolean completeShutdown = false;

    public PeaceOfMind()
    {
        /** Explicit permission: http://forum.industrial-craft.net/index.php?page=Thread&postID=121457#post121457 
         * Fixes Ore Dictionary debug spam
         */

        try
        {
            Class ores = Class.forName("gregtechmod.common.GT_OreDictHandler");

            try
            {
                Field ice = ores.getDeclaredField("instance");
                Object o = ice.get(this);
                MinecraftForge.EVENT_BUS.unregister(o);
            }
            catch (Exception e)
            {
                System.err.println("Cannot unregister GregTech Ore handler");
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            //GT not here
        }
    }
}
