package tconstruct.compat;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.Configuration;

public class Tforest {
	public static void initProps (File location)
    {
        /* Here we will set up the config file for the mod 
         * First: Create a folder inside the config folder
         * Second: Create the actual config file
         * Note: Configs are a pain, but absolutely necessary for every mod.
         */
        //File file = new File(TConstruct.proxy.getLocation() + "/config");
        //file.mkdir();
        //File newFile = new File(TConstruct.proxy.getLocation() + "/config/TinkersWorkshop.txt");
        
		File newFile = new File(location + "/TwilightForest.cfg");
		if( newFile.exists()){
        
        /* [Forge] Configuration class, used as config method */
        Configuration config = new Configuration(newFile);

        /* Load the configuration file */
        config.load();

        tfdimid = config.get("Dimension", "DimensionID", true).getInt();
    }
	else {
		tfdimid = -100;
	}
    }
	public static int tfdimid;
}
