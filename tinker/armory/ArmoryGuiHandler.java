package tinker.armory;

import java.util.List;

import tinker.armory.client.ArmorStandGui;
import tinker.armory.content.EntityEquipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class ArmoryGuiHandler implements IGuiHandler
{

	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		List<Entity> list = world.getLoadedEntityList();
		for (Entity entity : list)
		{
			if (ID == entity.entityId)
			{
				return ((EntityEquipment)entity).getContainer(player);
			}
		}
		System.out.println("Returning a null server entity");
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		List<Entity> list = world.getLoadedEntityList();
		for (Entity entity : list)
		{
			if (ID == entity.entityId)
			{
				return new ArmorStandGui(player, (EntityEquipment)entity);
			}
		}
		System.out.println("Returning a null client entity");
		return null;
	}
}
