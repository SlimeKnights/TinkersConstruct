package boni.tinkersweaponry.weapons;

import boni.tinkersweaponry.entity.ArrowEntity;
import boni.tinkersweaponry.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

import java.util.List;

public class ThrowArrowItem extends Weapon {
    public ThrowArrowItem() {
        super(1);
        this.setUnlocalizedName(Reference.prefix("ThrowArrow"));

        headIcons = TinkerTools.arrow.headIcons;
        handleIcons = TinkerTools.arrow.handleIcons;
        accessoryIcons = TinkerTools.arrow.accessoryIcons;
        extraIcons = TinkerTools.arrow.extraIcons;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack itemstack, World world, EntityPlayer player)
    {
        ItemStack stack = itemstack.copy();
        if (!world.isRemote)
        {
            EntityArrow arrow = new ArrowEntity(world, player, 1.0f, 1.0f, stack);
            world.spawnEntityInWorld(arrow);
        }

        return itemstack;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
            case 0:
                return "_arrow_head";
            case 1:
                return ""; // Doesn't break
            case 2:
                return "_arrow_shaft";
            case 3:
                return "_arrow_fletching";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_arrow_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "arrow";
    }

    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public void registerAlternatePartPaths (int index, String[] location)
    {
        accessoryStrings.put(index, location[3]);
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.arrowhead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.fletching;
    }

    @Override
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public String[] getTraits ()
    {
        return new String[] { "weapon", "throwing" };
    }

    @Override
    public void getSubItems (Item id, CreativeTabs tab, List list)
    {
        ItemStack toolMine = new ItemStack(this);

        Item accessory = getAccessoryItem();
        ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, 0) : null;
        Item extra = getExtraItem();
        ItemStack extraStack = extra != null ? new ItemStack(extra, 1, 0) : null;
        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, 3), new ItemStack(getHandleItem(), 1, 0), accessoryStack, extraStack, "");
        if (tool != null)
        {
            tool.stackSize = 1;
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);

            toolMine.setTagCompound(tool.getTagCompound());
            list.add(toolMine);
        }
    }
}
