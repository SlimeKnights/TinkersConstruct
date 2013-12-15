package tconstruct.items;

import tconstruct.common.TRepo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import tconstruct.achievements.TAchievements;

import java.util.List;

import tconstruct.library.TConstructRegistry;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CraftingItem extends mantle.items.abstracts.CraftingItem
{

    public CraftingItem(int id, String[] names, String[] tex, String folder)
    {
        super(id, names, tex, folder, "tinker", TConstructRegistry.materialTab);
    }

    @Override
    public void onCreated (ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par1ItemStack.itemID == TRepo.blankPattern.itemID)
        {
            par3EntityPlayer.addStat(TAchievements.achievements.get("tconstruct.pattern"), 1);
        }
    }
}
