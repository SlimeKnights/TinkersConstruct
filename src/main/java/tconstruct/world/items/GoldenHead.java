package tconstruct.world.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class GoldenHead extends ItemFood
{
    public GoldenHead(int par2, float par3, boolean par4)
    {
        super(par2, par3, par4);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect (ItemStack par1ItemStack)
    {
        return par1ItemStack.getItemDamage() > 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity (ItemStack par1ItemStack)
    {
        return par1ItemStack.getItemDamage() == 0 ? EnumRarity.rare : EnumRarity.epic;
    }

    @Override
    protected void onFoodEaten (ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par1ItemStack.getItemDamage() > 0)
        {
            if (!par2World.isRemote)
            {
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 600, 3));
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 6000, 0));
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 6000, 0));
            }
        }
        else
        {
            super.onFoodEaten(par1ItemStack, par2World, par3EntityPlayer);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems (Item b, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(b, 1, 0));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:skull_char_gold");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("\u00a75\u00a7o" + StatCollector.translateToLocal("goldenhead1.tooltip"));
        list.add("\u00a75\u00a7o" + StatCollector.translateToLocal("goldenhead2.tooltip"));
    }
}
