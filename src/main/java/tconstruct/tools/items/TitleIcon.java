package tconstruct.tools.items;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.client.TProxyClient;
import tconstruct.library.tools.ToolCore;
import tconstruct.world.entity.BlueSlime;

public class TitleIcon extends Item
{
    int[] primaryColor = { 0x66BBE8, 0x66BBE8 };
    int[] secondaryColor = { 0x1567BF, 0xFFEC6E };
    String[] mobNames = { "TConstruct.EdibleSlime", "TConstruct.KingSlime" };

    String[] achievementIconNames = new String[] { "tinkerer", "preparedFight", "proTinkerer", "enemySlayer", "dualConvenience" };
    IIcon[] achievementIcons = new IIcon[achievementIconNames.length];

    public TitleIcon()
    {
        super();
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        ToolCore.blankSprite = iconRegister.registerIcon("tinker:blanksprite");
        TProxyClient.metalBall = iconRegister.registerIcon("tinker:metalball");
        itemIcon = iconRegister.registerIcon("tinker:tparts");
        for (int i = 0; i < achievementIcons.length; i++)
        {
            achievementIcons[i] = iconRegister.registerIcon("tinker:achievementIcons/" + (i < achievementIconNames.length ? achievementIconNames[i] : ""));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass (int par1, int par2)
    {
        if (par1 == 255)
            return itemIcon;
        if (par1 >= 4096)
        {
            return getIconFromDamage(par1);
        }
        return new ItemStack(Items.spawn_egg).getItem().getIconFromDamageForRenderPass(par1, par2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int par1)
    {
        if (par1 >= 4096)
        {
            int index = par1 - 4096;
            if (index < achievementIcons.length)
            {
                return achievementIcons[index];
            }
        }
        // Not returning null to prevent crashes
        return itemIcon;
    }

    @Override
    public String getItemStackDisplayName (ItemStack par1ItemStack)
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
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 0; i < mobNames.length; i++)
            list.add(new ItemStack(b, 1, i));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack (ItemStack stack, int pass)
    {
        int damage = stack.getItemDamage();
        if (damage == 255)
            return 0xffffff;
        if (damage >= 4096)
            return 0xffffff;
        return pass == 0 ? primaryColor[damage] : secondaryColor[damage];
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int posX, int posY, int posZ, int par7, float par8, float par9, float par10)
    {
        if (!world.isRemote)
        {
            Block b = world.getBlock(posX, posY, posZ);
            posX += Facing.offsetsXForSide[par7];
            posY += Facing.offsetsYForSide[par7];
            posZ += Facing.offsetsZForSide[par7];
            double d0 = 0.0D;

            if (par7 == 1 && b != null && b.getRenderType() == 11)
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
        Block b = world.getBlock((int) posX, (int) posY, (int) posZ);
        posX += Facing.offsetsXForSide[par7];
        posY += Facing.offsetsYForSide[par7];
        posZ += Facing.offsetsZForSide[par7];
        double d0 = 0.0D;

        if (par7 == 1 && b != null && b.getRenderType() == 11)
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
            ((EntityLiving) entity).onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(entity);
        }
    }

    public static void spawnEntity (double x, double y, double z, Entity entity, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
                entity.setAngles(player.cameraYaw, player.cameraYaw);
            ((EntityLiving) entity).onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(entity);
        }
    }

    public static void spawnBossSlime (double x, double y, double z, BlueSlime entity, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.setSlimeSize(8);
            entity.onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(entity);
        }
    }

    public static void spawnBossSlime (double x, double y, double z, BlueSlime entity, World world)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.setSlimeSize(8);
            entity.onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(entity);
        }
    }
}
