package tconstruct.items;

public class MaterialItem extends CraftingItem
{

    public MaterialItem(int id)
    {
        super(id, materialNames, getTextures(), "materials/");
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


    static String[] materialNames = new String[] { "PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone",
            "CopperIngot", "TinIngot", "AluminumIngot", "RawAluminum", "BronzeIngot", "AluBrassIngot", "AlumiteIngot", "SteelIngot", "BlueSlimeCrystal", "ObsidianIngot", "IronNugget", "CopperNugget", "TinNugget",
            "AluminumNugget", "EssenceCrystal", "AluBrassNugget", "SilkyCloth", "SilkyJewel", "ObsidianNugget", "CobaltNugget", "ArditeNugget", "ManyullynNugget", "BronzeNugget", "AlumiteNugget",
            "SteelNugget", "PigIronIngot", "PigIronNugget", "GlueBall", "SearedBrick" };

    static String[] craftingTextures = new String[] { "paperstack", "slimecrystal", "searedbrick", "cobaltingot", "arditeingot", "manyullyningot", "mossball", "lavacrystal", "necroticbone",
            "copperingot", "tiningot", "aluminumingot", "aluminumraw", "bronzeingot", "alubrassingot", "alumiteingot", "steelingot", "blueslimecrystal", "obsidianingot", "nugget_iron",
            "nugget_copper", "nugget_tin", "nugget_aluminum", "", "nugget_alubrass", "silkycloth", "silkyjewel", "nugget_obsidian", "nugget_cobalt", "nugget_ardite", "nugget_manyullyn",
            "nugget_bronze", "nugget_alumite", "nugget_steel", "pigironingot", "nugget_pigiron", "glueball", "searedbrick_nether" };
}
