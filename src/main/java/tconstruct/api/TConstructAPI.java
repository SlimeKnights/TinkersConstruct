package tconstruct.api;

import net.minecraft.entity.player.EntityPlayer;

public class TConstructAPI
{
    public static String PROP_NAME;

    public static IPlayerExtendedInventoryWrapper getInventoryWrapper (EntityPlayer player)
    {
        return (IPlayerExtendedInventoryWrapper) player.getExtendedProperties(PROP_NAME);
    }
}
