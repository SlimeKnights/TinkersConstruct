package tconstruct.tools.items;

import mantle.items.abstracts.CraftingItem;
import tconstruct.library.TConstructRegistry;

public class MaterialItem extends CraftingItem
{

    public MaterialItem()
    {
        super(materialNames, getTextures(), "materials/", "tinker", TConstructRegistry.materialTab);
    }

    private static String[] getTextures ()
    {
        String[] names = new String[craftingTextures.length];
        for (int i = 0; i < craftingTextures.length; i++)
        {
            if (craftingTextures[i].equals(""))
                names[i] = "";
            else
                names[i] = "material_" + craftingTextures[i];
        }
        return names;
    }

    static String[] materialNames = new String[] { "PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone", "CopperIngot", "TinIngot", "ZincIngot", "RawZinc", "BronzeIngot", "BrassIngot", "ZarconIngot", "SteelIngot", "BlueSlimeCrystal", "ObsidianIngot", "IronNugget", "CopperNugget", "TinNugget", "ZincNugget", "EssenceCrystal", "BrassNugget", "SilkyCloth", "SilkyJewel", "ObsidianNugget", "CobaltNugget", "ArditeNugget", "ManyullynNugget", "BronzeNugget", "ZarconNugget", "SteelNugget", "PigIronIngot", "PigIronNugget", "GlueBall", "SearedBrick", "ArditeDust", "CobaltDust", "ZincDust", "ManyullynDust", "BrassDust" };

    static String[] craftingTextures = new String[] { "paperstack", "slimecrystal", "searedbrick", "cobaltingot", "arditeingot", "manyullyningot", "mossball", "lavacrystal", "necroticbone", "copperingot", "tiningot", "zincingot", "zincraw", "bronzeingot", "brassingot", "zarconingot", "steelingot", "blueslimecrystal", "obsidianingot", "nugget_iron", "nugget_copper", "nugget_tin", "nugget_zinc", "", "nugget_brass", "silkycloth", "silkyjewel", "nugget_obsidian", "nugget_cobalt", "nugget_ardite", "nugget_manyullyn", "nugget_bronze", "nugget_zarcon", "nugget_steel", "pigironingot", "nugget_pigiron", "glueball", "searedbrick_nether", "ardite_dust", "cobalt_dust", "zinc_dust", "manyullyn_dust", "brass_dust" };
}
