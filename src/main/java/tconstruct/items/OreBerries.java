package tconstruct.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OreBerries extends CraftingItem
{
    static String[] names = new String[] { "essence" };
    static String[] tex = new String[] { "oreberry_essence" };

    public OreBerries(int id)
    {
        super(id, names, tex, "oreberries/");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add(StatCollector.translateToLocal("oreberries6.tooltip"));
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        EntityXPOrb entity = new EntityXPOrb(world, player.posX, player.posY + 1, player.posZ, itemRand.nextInt(14) + 6);
        spawnEntity(player.posX, player.posY + 1, player.posZ, entity, world, player);
        if (!player.capabilities.isCreativeMode)
            stack.stackSize--;

        return stack;
    }

    public static void spawnEntity (double x, double y, double z, Entity entity, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            world.spawnEntityInWorld(entity);
        }
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        return icons[0];
    }
}
