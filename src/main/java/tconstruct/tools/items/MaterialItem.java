package tconstruct.tools.items;

import org.apache.commons.lang3.ArrayUtils;

import mantle.items.abstracts.CraftingItem;
import tconstruct.library.TConstructRegistry;

public class MaterialItem extends CraftingItem
{
    static String[] materialNames = new String[] {
        "PaperStack", "SlimeCrystal", "SearedBrick", "CobaltIngot", "ArditeIngot",
        "ManyullynIngot", "Mossball", "LavaCrystal", "NecroticBone", "CopperIngot",
        "TinIngot", "AluminumIngot", "RawAluminum", "BronzeIngot", "AluBrassIngot",
        "AlumiteIngot", "SteelIngot", "BlueSlimeCrystal", "ObsidianIngot", "IronNugget",
        "CopperNugget", "TinNugget", "AluminumNugget", "EssenceCrystal", "AluBrassNugget",
        "SilkyCloth", "SilkyJewel", "ObsidianNugget", "CobaltNugget", "ArditeNugget",
        "ManyullynNugget", "BronzeNugget", "AlumiteNugget", "SteelNugget", "PigIronIngot",
        "PigIronNugget", "GlueBall", "SearedBrick", "ArditeDust", "CobaltDust",
        "AluminumDust", "ManyullynDust", "AluBrassDust" };

    static String[] craftingTextures = new String[] {
        "paperstack", "slimecrystal", "searedbrick", "cobaltingot", "arditeingot", "manyullyningot", "mossball", "lavacrystal", "necroticbone", "copperingot",
        "tiningot", "aluminumingot", "aluminumraw", "bronzeingot", "alubrassingot", "alumiteingot", "steelingot", "blueslimecrystal", "obsidianingot", "nugget_iron",
        "nugget_copper", "nugget_tin", "nugget_aluminum", "", "nugget_alubrass", "silkycloth", "silkyjewel", "nugget_obsidian", "nugget_cobalt", "nugget_ardite", "nugget_manyullyn",
        "nugget_bronze", "nugget_alumite", "nugget_steel", "pigironingot", "nugget_pigiron", "glueball", "searedbrick_nether", "ardite_dust", "cobalt_dust",
        "aluminum_dust", "manyullyn_dust", "alubrass_dust" };

    public static final int PAPER_STACK = ArrayUtils.indexOf(materialNames, "PaperStack");
    public static final int SLIME_CRYSTAL = ArrayUtils.indexOf(materialNames, "SlimeCrystal");
    public static final int SEARED_BRICK = ArrayUtils.indexOf(materialNames, "SearedBrick");
    public static final int COBALT_INGOT = ArrayUtils.indexOf(materialNames, "CobaltIngot");
    public static final int ARDITE_INGOT = ArrayUtils.indexOf(materialNames, "ArditeIngot");
    public static final int MANYULLYN_INGOT = ArrayUtils.indexOf(materialNames, "ManyullynIngot");
    public static final int MOSS_BALL = ArrayUtils.indexOf(materialNames, "Mossball");
    public static final int LAVA_CRYSTAL = ArrayUtils.indexOf(materialNames, "LavaCrystal");
    public static final int NECROTIC_BONE = ArrayUtils.indexOf(materialNames, "NecroticBone");
    public static final int COPPER_INGOT = ArrayUtils.indexOf(materialNames, "CopperIngot");
    public static final int TIN_INGOT = ArrayUtils.indexOf(materialNames, "TinIngot");
    public static final int ALUMINUM_INGOT = ArrayUtils.indexOf(materialNames, "AluminumIngot");
    public static final int RAW_ALUMINUM = ArrayUtils.indexOf(materialNames, "RawAluminum");
    public static final int BRONZE_INGOT = ArrayUtils.indexOf(materialNames, "BronzeIngot");
    public static final int ALUMINUM_BRASS_INGOT = ArrayUtils.indexOf(materialNames, "AluBrassIngot");
    public static final int ALUMITE_INGOT = ArrayUtils.indexOf(materialNames, "AlumiteIngot");
    public static final int STEEL_INGOT = ArrayUtils.indexOf(materialNames, "SteelIngot");
    public static final int BLUE_SLIME_CRYSTAL = ArrayUtils.indexOf(materialNames, "BlueSlimeCrystal");
    public static final int OBSIDIAN_INGOT = ArrayUtils.indexOf(materialNames, "ObsidianIngot");
    public static final int IRON_NUGGET = ArrayUtils.indexOf(materialNames, "IronNugget");
    public static final int COPPER_NUGGET = ArrayUtils.indexOf(materialNames, "CopperNugget");
    public static final int TIN_NUGGET = ArrayUtils.indexOf(materialNames, "TinNugget");
    public static final int ALUMINUM_NUGGET = ArrayUtils.indexOf(materialNames, "AluminumNugget");
    public static final int ESSENCE_CRYSTAL = ArrayUtils.indexOf(materialNames, "EssenceCrystal");
    public static final int ALUMINUM_BRASS_NUGGET = ArrayUtils.indexOf(materialNames, "AluBrassNugget");
    public static final int SILKY_CLOTH = ArrayUtils.indexOf(materialNames, "SilkyCloth");
    public static final int SILKY_JEWEL = ArrayUtils.indexOf(materialNames, "SilkyJewel");
    public static final int OBSIDIAN_NUGGET = ArrayUtils.indexOf(materialNames, "ObsidianNugget");
    public static final int COBALT_NUGGET = ArrayUtils.indexOf(materialNames, "CobaltNugget");
    public static final int ARDITE_NUGGET = ArrayUtils.indexOf(materialNames, "ArditeNugget");
    public static final int MANYULLYN_NUGGET = ArrayUtils.indexOf(materialNames, "ManyullynNugget");
    public static final int BRONZE_NUGGET = ArrayUtils.indexOf(materialNames, "BronzeNugget");
    public static final int ALUMITE_NUGGET = ArrayUtils.indexOf(materialNames, "AlumiteNugget");
    public static final int STEEL_NUGGET = ArrayUtils.indexOf(materialNames, "SteelNugget");
    public static final int PIG_IRON_INGOT = ArrayUtils.indexOf(materialNames, "PigIronIngot");
    public static final int PIG_IRON_NUGGET = ArrayUtils.indexOf(materialNames, "PigIronNugget");
    public static final int GLUE_BALL = ArrayUtils.indexOf(materialNames, "GlueBall");
    public static final int SEARED_BRICK_NETHER = ArrayUtils.indexOf(materialNames, "SearedBrick");
    public static final int ARDITE_DUST = ArrayUtils.indexOf(materialNames, "ArditeDust");
    public static final int COBALT_DUST = ArrayUtils.indexOf(materialNames, "CobaltDust");
    public static final int ALUMINUM_DUST = ArrayUtils.indexOf(materialNames, "AluminumDust");
    public static final int MANYULLYN_DUST = ArrayUtils.indexOf(materialNames, "ManyullynDust");
    public static final int ALUMINUM_BRASS_DUST = ArrayUtils.indexOf(materialNames, "AluBrassDust");

    public MaterialItem()
    {
        super(materialNames, getTextures(), "materials/", "tinker", TConstructRegistry.materialTab);
    }

    private static String[] getTextures()
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

}
