package mods.tinker.tconstruct.items;

import java.util.List;

import mods.tinker.tconstruct.blocks.logic.EssenceExtractorLogic;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EssenceCrystal extends Item
{
    public EssenceCrystal(int id)
    {
        super(id);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public int getMaxItemUseDuration (ItemStack par1ItemStack)
    {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.eat;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }

    @Override
    public ItemStack onEaten (ItemStack stack, World world, EntityPlayer player)
    {
        if (stack.hasTagCompound())
        {
            EntityXPOrb entity = new EntityXPOrb(world, player.posX, player.posY + 1, player.posZ, stack.getTagCompound().getInteger("Essence"));
            spawnEntity(player.posX, player.posY + 1, player.posZ, entity, world, player);
            if (!player.capabilities.isCreativeMode)
                stack.stackSize--;
        }
        return stack;
    }

    public static void spawnEntity (double x, double y, double z, Entity entity, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            world.spawnEntityInWorld(entity);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:materials/material_essencecrystal");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
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

    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        int[] amount = new int[] { 17, 85, 170, 255, 385, 590, 825 };
        for (int i = 0; i < amount.length; i++)
        {
            ItemStack crystal = new ItemStack(id, 1, 0);
            NBTTagCompound tags = new NBTTagCompound();
            tags.setInteger("Essence", amount[i]);
            crystal.setTagCompound(tags);
            list.add(crystal);
        }
    }
}
