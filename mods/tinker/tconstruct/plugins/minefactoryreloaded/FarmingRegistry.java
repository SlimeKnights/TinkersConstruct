package mods.tinker.tconstruct.plugins.minefactoryreloaded;

import java.lang.reflect.Method;

import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

/**
 * @author Emys
 * 
 * A stripped-down version of the FarmingRegistry from MFR, registers Harvestables and Grindables with the API.
 * 
 */
public class FarmingRegistry
{	
	/**
	 * Registers a harvestable block with the Harvester.
	 * 
	 * @param harvestable The thing to harvest.
	 */
	public static void registerHarvestable(IFactoryHarvestable harvestable)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.api.FarmingRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerHarvestable", IFactoryHarvestable.class);
				reg.invoke(registry, harvestable);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Registers a grindable entity with the Grinder.
	 * 
	 * @param grindable The entity to grind.
	 */
	public static void registerGrindable(IFactoryGrindable grindable)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.api.FarmingRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerGrindable", IFactoryGrindable.class);
				reg.invoke(registry, grindable);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
