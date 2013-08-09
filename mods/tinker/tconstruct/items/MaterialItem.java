package mods.tinker.tconstruct.items;

import java.util.List;

import mods.tinker.tconstruct.blocks.logic.EssenceExtractorLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
            "TinNugget", "AluminumNugget", "EssenceCrystal", "AluBrassNugget", "SilkyCloth", "SilkyJewel", "ObsidianNugget", "CobaltNugget", "ArditeNugget", "ManyullynNugget", "BronzeNugget",
            "AlumiteNugget", "SteelNugget" };

    static String[] craftingTextures = new String[] { "paperstack", "slimecrystal", "searedbrick", "cobaltingot", "arditeingot", "manyullyningot", "mossball", "lavacrystal", "necroticbone",
            "copperingot", "tiningot", "aluminumingot", "aluminumraw", "bronzeingot", "alubrassingot", "alumiteingot", "steelingot", "blueslimecrystal", "obsidianingot", "nugget_iron",
            "nugget_copper", "nugget_tin", "nugget_aluminum", "essencecrystal", "nugget_alubrass", "silkycloth", "silkyjewel", "nugget_obsidian", "nugget_cobalt", "nugget_ardite", "nugget_manyullyn",
            "nugget_bronze", "nugget_alumite", "nugget_steel" };

    /*@Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (stack.getItemDamage() == 23)
        {
            if (stack.hasTagCompound())
            {
                int amount = stack.getTagCompound().getInteger("Essence");
                list.add("Stored Levels: " + EssenceExtractorLogic.getEssencelevels(amount));
            }
            else
            {
                list.add("Stored Levels: 0");
            }
        }
    }*/
}
