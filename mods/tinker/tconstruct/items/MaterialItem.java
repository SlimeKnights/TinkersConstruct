package mods.tinker.tconstruct.items;

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
            names[i] = "material_" + craftingTextures[i];
        return names;
    }

    static String[] materialNames = new String[] { "PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone",
            "CopperIngot", "TinIngot", "AluminumIngot", "RawAluminum", "BronzeIngot", "AluBrassIngot", "AlumiteIngot", "SteelIngot", "BlueSlimeCrystal", "ObsidianIngot", "IronNugget", "CopperNugget",
            "TinNugget", "AluminumNugget", "EssenceCrystal", "AluBrassNugget", "SilkyCloth", "SilkyJewel" };

    static String[] craftingTextures = new String[] { "paperstack", "slimecrystal", "searedbrick", "cobaltingot", "arditeingot", "manyullyningot", "mossball", "lavacrystal", "necroticbone",
            "copperingot", "tiningot", "aluminumingot", "aluminumraw", "bronzeingot", "alubrassingot", "alumiteingot", "steelingot", "blueslimecrystal", "obsidianingot", "nugget_iron",
            "nugget_copper", "nugget_tin", "nugget_aluminum", "essencecrystal", "nugget_alubrass", "silkycloth", "silkyjewel" };

}
