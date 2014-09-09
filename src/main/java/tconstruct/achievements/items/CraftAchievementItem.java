package tconstruct.achievements.items;

import mantle.items.abstracts.CraftingItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.achievements.TAchievements;

public class CraftAchievementItem extends CraftingItem
{
    public String grantedAchievement = "";

    public CraftAchievementItem(String[] names, String[] tex, String folder, String modTexturePrefix, CreativeTabs tab, String tachievement)
    {
        super(names, tex, folder, modTexturePrefix, tab);

        grantedAchievement = tachievement;
    }

    @Override
    public void onCreated (ItemStack item, World world, EntityPlayer player)
    {
        if (grantedAchievement != null && !grantedAchievement.equals(""))
        {
            TAchievements.triggerAchievement(player, grantedAchievement);
        }
    }
}
