package powercrystals.minefactoryreloaded.api;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

/**
 * NO-OP
 * @deprecated for FactoryRegistry
 */
@Deprecated
public class FarmingRegistry
{
	private static void alert()
	{
		FMLLog.severe("%s is using a removed API and may crash the game.", 
				Loader.instance().activeModContainer().getName());
		new Throwable().printStackTrace();
	}
	public static void registerPlantable(IFactoryPlantable plantable) { alert(); }
	public static void registerHarvestable(IFactoryHarvestable harvestable) { alert(); }
	public static void registerFertilizable(IFactoryFertilizable fertilizable) { alert(); }
	public static void registerFertilizer(IFactoryFertilizer fertilizer) { alert(); }
	public static void registerRanchable(IFactoryRanchable ranchable) { alert(); }
	public static void registerGrindable(IFactoryGrindable grindable) { alert(); }
	public static void registerSludgeDrop(int weight, ItemStack drop) { alert(); }
	public static void registerBreederFood(Class<?> entityToBreed, ItemStack food) { alert(); }
	public static void registerSafariNetHandler(ISafariNetHandler handler) { alert(); }
	public static void registerMobEggHandler(IMobEggHandler handler) { alert(); }
	public static void registerRubberTreeBiome(String biome) { alert(); }
	public static void registerSafariNetBlacklist(Class<?> blacklistedEntity) { alert(); }
	public static void registerVillagerTradeMob(IRandomMobProvider mobProvider) { alert(); }
	public static void registerLiquidDrinkHandler(int liquidId, ILiquidDrinkHandler liquidDrinkHandler) { alert(); }
	public static void registerLaserOre(int weight, ItemStack drop) { alert(); }
	public static void setLaserPreferredOre(int color, ItemStack ore) { alert(); }
	public static void registerFruitLogBlockId(Integer fruitLogBlockId) { alert(); }
	public static void registerFruit(IFactoryFruit fruit) { alert(); }
	public static void registerAutoSpawnerBlacklist(String entityString) { alert(); }
	public static void registerRedNetLogicCircuit(IRedNetLogicCircuit circuit) { alert(); }
}
