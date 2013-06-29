package mods.tinker.tconstruct.util.player;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.skill.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;

public class ArmorExtendedClient extends ArmorExtended
{
	/*public void recalculateSkills(EntityPlayer player, TPlayerStats stats)
    {
		System.out.println("Client skills");
    	if (inventory[1] != null && inventory[1].getItem() == TContent.glove)
    	{
    		if (TProxyClient.skillList.size() < 1)
    		{
    			try
    			{
    				TProxyClient.skillList.add(SkillRegistry.skills.get("Wall Building").copy());
    			}
    			catch (Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	else
    	{
    		if (TProxyClient.skillList.size() > 0)
    		{
    			TProxyClient.skillList.remove(0);
    		}
    	}
    }*/
}
