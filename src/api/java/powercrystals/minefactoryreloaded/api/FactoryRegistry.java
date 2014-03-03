package powercrystals.minefactoryreloaded.api;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * @author PowerCrystals
 * 
 * Class used to register plants and other farming-related things with MFR. Will do nothing if MFR does not exist, but your mod should be set to load
 * after MFR or things may not work properly.
 * 
 * To avoid breaking the API, additional FactoryRegistry##s will appear on major MFR versions that contain API additions. On a Minecraft version change, 
 * these will be rolled back into this class.
 * 
 */
public class FactoryRegistry
{
	/**
	 * Registers a plantable object with the Planter.
	 * 
	 * @param plantable The thing to plant.
	 */
	public static void registerPlantable(IFactoryPlantable plantable)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerPlantable", IFactoryPlantable.class);
				reg.invoke(registry, plantable);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a harvestable block with the Harvester.
	 * 
	 * @param harvestable The thing to harvest.
	 */
	public static void registerHarvestable(IFactoryHarvestable harvestable)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
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
	 * Registers a fertilizable block with the Fertilizer.
	 * 
	 * @param fertilizable The thing to fertilize.
	 */
	public static void registerFertilizable(IFactoryFertilizable fertilizable)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerFertilizable", IFactoryFertilizable.class);
				reg.invoke(registry, fertilizable);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a fertilizer item Fertilizer.
	 * 
	 * @param fertilizable The thing to fertilize with.
	 */
	public static void registerFertilizer(IFactoryFertilizer fertilizer)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerFertilizer", IFactoryFertilizer.class);
				reg.invoke(registry, fertilizer);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a ranchable entity with the Rancher.
	 * 
	 * @param ranchable The entity to ranch.
	 */
	public static void registerRanchable(IFactoryRanchable ranchable)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerRanchable", IFactoryRanchable.class);
				reg.invoke(registry, ranchable);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Registers a grindable entity with the Grinder using the new grinder interface. This method will be renamed to the standard "registerGrindable"
	 * on MC 1.6.
	 * 
	 * @param grindable The entity to grind.
	 */
	public static void registerGrindable(IFactoryGrindable grindable)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
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
	
	/**
	 * Bans an entity class from being automatically ground by the Grinder
	 * 
	 * @param blacklistedEntity Class to blacklist
	 */
	public static void registerGrinderBlacklist(Class<?> ...ungrindables)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerGrinderBlacklist", Class[].class);
				reg.invoke(registry, (Object[])ungrindables);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a possible output with the sludge boiler.
	 * 
	 * @param weight Likelihood that this item will be produced. Lower means rarer.
	 * @param drop The thing being produced by the sludge boiler.
	 */
	public static void registerSludgeDrop(int weight, ItemStack drop)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerSludgeDrop", int.class, ItemStack.class);
				reg.invoke(registry, weight, drop);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers specific food to use in the Breeder (instead of wheat) for a given mob.
	 * 
	 * @param entityToBreed Entity this food will be used with.
	 * @param food The item to use when breeding this entity.
	 */
	public static void registerBreederFood(Class<?> entityToBreed, ItemStack food)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerBreederFood", Class.class, ItemStack.class);
				reg.invoke(registry, entityToBreed, food);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Bans an entity class from being collected by Safari Nets
	 * 
	 * @param blacklistedEntity Class to blacklist
	 */
	public static void registerSafariNetBlacklist(Class<?> blacklistedEntity)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerSafariNetBlacklist", Class.class);
				reg.invoke(registry, blacklistedEntity);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a Safari Net handler to properly serialize a type of mob.
	 * 
	 * @param handler The Safari Net handler.
	 */
	public static void registerSafariNetHandler(ISafariNetHandler handler)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerSafariNetHandler", ISafariNetHandler.class);
				reg.invoke(registry, handler);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Registers a mob egg handler, which allows the Safari Net to properly change colors.
	 * 
	 * @param handler The mob egg handler
	 */
	public static void registerMobEggHandler(IMobEggHandler handler)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerMobEggHandler", IMobEggHandler.class);
				reg.invoke(registry, handler);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Allows Rubber Trees to spawn in the specified biome.
	 * 
	 * @param biome The biome name.
	 */
	public static void registerRubberTreeBiome(String biome)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerRubberTreeBiome", String.class);
				reg.invoke(registry, biome);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers an entity as a possible output from villager random safari nets. Note that the "id" field must be initialized
	 * (i.e. with Entity.addEntityID()) for it to work correctly.
	 * 
	 * @param savedMob A serialized mob that will be unloaded by the safari net
	 * @param weight The weight of this mob in the random selection
	 */
	public static void registerVillagerTradeMob(IRandomMobProvider mobProvider)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerVillagerTradeMob", IRandomMobProvider.class);
				reg.invoke(registry, mobProvider);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a handler for drinking liquids with the straw.
	 * 
	 * @param liquidId The liquid id the handler handles.
	 * @param liquidDrinkHandler The drink handler instance.
	 */
	@Deprecated
	public static void registerLiquidDrinkHandler(int liquidId, ILiquidDrinkHandler liquidDrinkHandler)
	{
		Fluid fluid = FluidRegistry.getFluid(liquidId);
		registerLiquidDrinkHandler(fluid.getName(), liquidDrinkHandler);
	}

	/**
	 * Registers a handler for drinking liquids with the straw.
	 * 
	 * @param liquidId The liquid string name the handler handles.
	 * @param liquidDrinkHandler The drink handler instance.
	 */
	public static void registerLiquidDrinkHandler(String liquidId, ILiquidDrinkHandler liquidDrinkHandler)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerLiquidDrinkHandler", String.class, ILiquidDrinkHandler.class);
				reg.invoke(registry, liquidId, liquidDrinkHandler);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a possible output with the laser drill.
	 * 
	 * @param weight Likelihood that this item will be produced. Lower means rarer.
	 * @param drop The thing being produced by the laser drill.
	 */
	public static void registerLaserOre(int weight, ItemStack drop)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerLaserOre", int.class, ItemStack.class);
				reg.invoke(registry, weight, drop);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Registers a preferred ore with the laser drill. Focuses with the specified color will make the specified ore more likely.
	 * Used by MFR itself for vanilla: Black (Coal), Light Blue (Diamond), Lime (Emerald), Yellow (Gold), Brown (Iron), Blue (Lapis),
	 * Red (Redstone), and White (nether quartz).
	 * 
	 * This will replace setLaserPreferredOre on MC 1.6.
	 * 
	 * @param color The color that the preferred ore is being set for. White is 0.
	 * @param ore The ore that will be preferred by the drill when a focus with the specified color is present.
	 */
	public static void addLaserPreferredOre(int color, ItemStack ore)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("addLaserPreferredOre", int.class, ItemStack.class);
				reg.invoke(registry, color, ore);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Registers a block ID as a fruit tree log. When the Fruit Picker sees this block on the ground, it will
	 * begin a search in tree mode for any fruit nearby.
	 * 
	 * @param fruitLogBlockId The block ID to mark as a fruit tree log.
	 */
	public static void registerFruitLogBlockId(Integer fruitLogBlockId)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerFruitLogBlockId", Integer.class);
				reg.invoke(registry, fruitLogBlockId);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers a fruit for the Fruit Picker.
	 * 
	 * @param fruit The fruit to be picked.
	 */
	public static void registerFruit(IFactoryFruit fruit)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerFruit", IFactoryFruit.class);
				reg.invoke(registry, fruit);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers an entity string as an invalid entry for the autospawner.
	 * See also: {@link net.minecraft.entity.EntityList}'s classToStringMapping and stringToClassMapping.
	 * 
	 * @param entityString The entity string to blacklist.
	 */
	public static void registerAutoSpawnerBlacklist(String entityString)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerAutoSpawnerBlacklist", String.class);
				reg.invoke(registry, entityString);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Registers logic circuit to be usable in the Programmable RedNet Controller.
	 * 
	 * @param circuit The circuit to be registered.
	 */
	public static void registerRedNetLogicCircuit(IRedNetLogicCircuit circuit)
	{
		try
		{
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
				Method reg = registry.getMethod("registerRedNetLogicCircuit", IRedNetLogicCircuit.class);
				reg.invoke(registry, circuit);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
