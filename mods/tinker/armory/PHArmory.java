package mods.tinker.armory;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.Configuration;

public class PHArmory 
{

	public static void initProps()
    {
        /* Here we will set up the config file for the mod 
         * First: Create a folder inside the config folder
         * Second: Create the actual config file
         * Note: Configs are a pain, but absolutely necessary for every mod.
         */
		
		File file = new File(Armory.proxy.getMinecraftDir() + "/config/InfiCraft");
        file.mkdir();
        File newFile = new File(Armory.proxy.getMinecraftDir() + "/config/InfiCraft/Armory.txt");
        
        /* Some basic debugging will go a long way */
        try
        {
            newFile.createNewFile();
            System.out.println("Successfully read configuration file for mod_FloraBerries");
        }
        catch (IOException e)
        {
            System.out.println("Could not read configuration file for mod_FloraBerries. Reason:");
            System.out.println(e);
        }

        /* [Forge] Configuration class, used as config method */
        Configuration config = new Configuration(newFile);

        /* Load the configuration file */
        config.load();

        /* Define the mod's IDs. 
         * Avoid values below 4096 for items and in the 250-450 and 2216-2226 range for blocks
         */
        
        //armorBlock = config.getBlock("Armor Stand", 3257).getInt(3257);
        toolrack = config.getBlock("Toolrack", 3201).getInt(3201);
        shieldrack = config.getBlock("Shield Rack", 3202).getInt(3202);
        armorItem = config.getItem("Armor Stand Item", 4570).getInt(4570);
        /*berryBlockID = config.getBlock("Berry_Bush", 3257).getInt(3257);
        berryItemID = config.getItem("Berry_Food", 12402).getInt(12402);
        
        raspSpawnDensity = config.get("general", "Raspberry_Spawn_Density", 14).getInt(14);
        raspSpawnHeight = config.get("general", "Raspberry_Spawn_Height", 64).getInt(64);
        raspSpawnRange = config.get("general", "Raspberry_Spawn_Range", 128).getInt(128);
        blueSpawnDensity = config.get("general", "Blueberry_Spawn_Density", 12).getInt(12);
        blueSpawnHeight = config.get("general", "Blueberry_Spawn_Height", 64).getInt(64);
        blueSpawnRange = config.get("general", "Blueberry_Spawn_Range", 128).getInt(128);
        blackSpawnDensity = config.get("general", "Blackberry_Spawn_Density", 10).getInt(10);
        blackSpawnHeight = config.get("general", "Blackberry_Spawn_Height", 64).getInt(64);
        blackSpawnRange = config.get("general", "Blackberry_Spawn_Range", 128).getInt(128);
        geoSpawnDensity = config.get("general", "Geoberry_Spawn_Density", 14).getInt(14);
        geoSpawnHeight = config.get("general", "Geoberry_Spawn_Height", 64).getInt(64);
        geoSpawnRange = config.get("general", "Geoberry_Spawn_Range", 128).getInt(128);*/
        
        /* Save the configuration file */
        config.save();
    }
	
	/* Prototype fields, used elsewhere */
	public static int armorBlock;
	public static int armorItem;
	public static int toolrack;
	public static int shieldrack;
	/*public static int raspSpawnDensity;
	public static int raspSpawnHeight;
	public static int raspSpawnRange;
	public static int blueSpawnDensity;
	public static int blueSpawnHeight;
	public static int blueSpawnRange;
	public static int blackSpawnDensity;
	public static int blackSpawnHeight;
	public static int blackSpawnRange;
	public static int geoSpawnDensity;
	public static int geoSpawnHeight;
	public static int geoSpawnRange;
	
	public static int berryItemID;
	public static int berryBlockID;*/
	
}
