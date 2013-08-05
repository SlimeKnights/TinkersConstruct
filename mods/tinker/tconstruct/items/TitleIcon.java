package mods.tinker.tconstruct.items;

import java.util.List;

import mods.natura.entity.HeatscarSpider;
import mods.natura.entity.BabyHeatscarSpider;
import mods.natura.entity.ImpEntity;
import mods.natura.entity.NitroCreeper;
import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.entity.BlueSlime;
import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TitleIcon extends Item
{
    int[] primaryColor = { 0x66BBE8, 0x66BBE8 };
    int[] secondaryColor = { 0x1567BF, 0xFFEC6E };
    String[] mobNames = { "TConstruct.EdibleSlime", "TConstruct.KingSlime" };

    public TitleIcon(int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        ToolCore.blankSprite = iconRegister.registerIcon("tinker:blanksprite");
        TProxyClient.metalBall = iconRegister.registerIcon("tinker:metalball");
        itemIcon = iconRegister.registerIcon("tinker:tparts");
    }

    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamageForRenderPass (int par1, int par2)
    {
        if (par1 == 255)
            return itemIcon;
        return Item.monsterPlacer.getIconFromDamageForRenderPass(par1, par2);
    }

    @Override
    public String getItemDisplayName (ItemStack par1ItemStack)
    {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String s1 = mobNames[par1ItemStack.getItemDamage()];

        if (s1 != null)
        {
            s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        }

        return s;
    }

    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < mobNames.length; i++)
            list.add(new ItemStack(id, 1, i));
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack (ItemStack stack, int pass)
    {
        int damage = stack.getItemDamage();
        if (damage == 255)
            return 0xffffff;
        return pass == 0 ? primaryColor[damage] : secondaryColor[damage];
    }

    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int posX, int posY, int posZ, int par7, float par8, float par9, float par10)
    {
        if (!world.isRemote)
        {
            int i1 = world.getBlockId(posX, posY, posZ);
            posX += Facing.offsetsXForSide[par7];
            posY += Facing.offsetsYForSide[par7];
            posZ += Facing.offsetsZForSide[par7];
            double d0 = 0.0D;

            if (par7 == 1 && Block.blocksList[i1] != null && Block.blocksList[i1].getRenderType() == 11)
            {
                d0 = 0.5D;
            }

            int damage = stack.getItemDamage();
            switch (damage)
            {
            case 0:
                spawnEntity(posX, posY, posZ, new BlueSlime(world), world, player);
                break;
            case 1:
                spawnBossSlime(posX, posY, posZ, new BlueSlime(world), world, player);
                break;
            }
            if (!player.capabilities.isCreativeMode)
            {
                --stack.stackSize;
            }
        }
        return true;
    }

    public static EntityLiving activateSpawnEgg (ItemStack stack, World world, double posX, double posY, double posZ, int par7)
    {
        int i1 = world.getBlockId((int) posX, (int) posY, (int) posZ);
        posX += Facing.offsetsXForSide[par7];
        posY += Facing.offsetsYForSide[par7];
        posZ += Facing.offsetsZForSide[par7];
        double d0 = 0.0D;

        if (par7 == 1 && Block.blocksList[i1] != null && Block.blocksList[i1].getRenderType() == 11)
        {
            d0 = 0.5D;
        }

        int damage = stack.getItemDamage();
        EntityLiving entity = null;
        switch (damage)
        {
        case 0:
            entity = new BlueSlime(world);
            spawnEntity(posX, posY, posZ, entity, world);
            break;
        case 1:
            entity = new BlueSlime(world);
            spawnBossSlime(posX, posY, posZ, new BlueSlime(world), world);
            break;
        }
        return entity;
    }

    public static void spawnEntity (double x, double y, double z, Entity entity, World world)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            ((EntityLiving) entity).initCreature();
            world.spawnEntityInWorld(entity);
        }
    }

    public static void spawnEntity (double x, double y, double z, Entity entity, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.setAngles(player.cameraYaw, player.cameraYaw);
            ((EntityLiving) entity).initCreature();
            world.spawnEntityInWorld(entity);
        }
    }

    public static void spawnBossSlime (double x, double y, double z, BlueSlime entity, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.setAngles(player.cameraYaw, player.cameraYaw);
            entity.setSlimeSize(8);
            entity.initCreature();
            world.spawnEntityInWorld(entity);
        }
    }

    public static void spawnBossSlime (double x, double y, double z, BlueSlime entity, World world)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.setSlimeSize(8);
            entity.initCreature();
            world.spawnEntityInWorld(entity);
        }
    }
}
