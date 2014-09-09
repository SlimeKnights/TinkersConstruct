package tconstruct.world.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tconstruct.library.TConstructRegistry;

public class OreBerries extends CraftingItem
{
    static String[] names = new String[] { "iron", "gold", "copper", "tin", "aluminum", "essence" };
    static String[] tex = new String[] { "oreberry_iron", "oreberry_gold", "oreberry_copper", "oreberry_tin", "oreberry_aluminum", "oreberry_essence" };

    public OreBerries()
    {
        super(names, tex, "oreberries/", "tinker", TConstructRegistry.materialTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add(StatCollector.translateToLocal("oreberries1.tooltip"));
            break;
        case 1:
            list.add(StatCollector.translateToLocal("oreberries2.tooltip"));
            break;
        case 2:
            list.add(StatCollector.translateToLocal("oreberries3.tooltip"));
            break;
        case 3:
            list.add(StatCollector.translateToLocal("oreberries4.tooltip"));
            break;
        case 4:
            list.add(StatCollector.translateToLocal("oreberries5.tooltip"));
            break;
        case 5:
            list.add(StatCollector.translateToLocal("oreberries6.tooltip"));
            break;
        }
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        if (stack.getItemDamage() == 5)
        {
            EntityXPOrb entity = new EntityXPOrb(world, player.posX, player.posY + 1, player.posZ, itemRand.nextInt(14) + 6);
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

}
