package tconstruct.items.tools;

import java.util.List;

import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Arrow extends ToolCore
{

    public Arrow(int id)
    {
        super(id, 3);
        this.setUnlocalizedName("InfiTool.Arrow");
        this.setMaxStackSize(64);
        this.setMaxDamage(0);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_arrow_head";
        case 1:
            return ""; //Doesn't break
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
        return TContent.arrowhead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.fletching;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "ammo" };
    }

    @Override
    public float getDamageModifier ()
    {
        return 0.5f;
    }

    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        //Vanilla style arrow
        Item accessory = getAccessoryItem();
        ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, 0) : null;
        Item extra = getExtraItem();
        ItemStack extraStack = extra != null ? new ItemStack(extra, 1, 0) : null;
        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, 3), new ItemStack(getHandleItem(), 1, 0), accessoryStack, extraStack, "");
        if (tool == null)
        {
            if (!TContent.supressMissingToolLogs)
            {
                TConstruct.logger.warning("Creative builder failed tool for Vanilla style" + this.getToolName());
                TConstruct.logger.warning("Make sure you do not have item ID conflicts");
            }
        }
        else
        {
            tool.stackSize = 1;
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }

        //Random arrow
        accessory = getAccessoryItem();
        accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, random.nextInt(4)) : null;
        extra = getExtraItem();
        extraStack = extra != null ? new ItemStack(extra, 1, 0) : null;
        tool = ToolBuilder.instance
                .buildTool(new ItemStack(getHeadItem(), 1, random.nextInt(18)), new ItemStack(getHandleItem(), 1, random.nextInt(18)), accessoryStack, extraStack, "Random Streamer");

        if (tool == null)
        {
            if (!TContent.supressMissingToolLogs)
            {
                TConstruct.logger.warning("Creative builder failed tool for Vanilla style" + this.getToolName());
                TConstruct.logger.warning("Make sure you do not have item ID conflicts");
            }
        }
        else
        {
            tool.stackSize = 1;
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
        super.getSubItems(id, tab, list);
    }

    @Override
    public void buildTool (int id, String name, List list)
    {
        Item accessory = getAccessoryItem();
        ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, 0) : null;
        Item extra = getExtraItem();
        ItemStack extraStack = extra != null ? new ItemStack(getExtraItem(), 1, id) : null;
        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, id), new ItemStack(getHandleItem(), 1, id), accessoryStack, extraStack, name + getToolName());
        if (tool == null)
        {
            if (!TContent.supressMissingToolLogs)
            {
                TConstruct.logger.warning("Creative builder failed tool for " + name + this.getToolName());
                TConstruct.logger.warning("Make sure you do not have item ID conflicts");
            }
        }
        else
        {
            tool.stackSize = 1;
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;

        NBTTagCompound tags = stack.getTagCompound();
        if (tags.hasKey("charge"))
        {
            String color = "";
            //double joules = this.getJoules(stack);
            int power = tags.getInteger("charge");

            if (power != 0)
            {
                if (power <= this.getMaxCharge(stack) / 3)
                    color = "\u00a74";
                else if (power > this.getMaxCharge(stack) * 2 / 3)
                    color = "\u00a72";
                else
                    color = "\u00a76";
            }

            String charge = new StringBuilder().append(color).append(tags.getInteger("charge")).append("/").append(getMaxCharge(stack)).append(" EU").toString();
            list.add(charge);
        }
        if (tags.hasKey("InfiTool"))
        {
            boolean broken = tags.getCompoundTag("InfiTool").getBoolean("Broken");
            if (broken)
                list.add("\u00A7oBroken");
            else
            {
                int head = tags.getCompoundTag("InfiTool").getInteger("Head");
                int handle = tags.getCompoundTag("InfiTool").getInteger("Handle");

                String headName = getAbilityNameForType(head);
                if (!headName.equals(""))
                    list.add(getStyleForType(head) + headName);

                String handleName = getAbilityNameForType(handle);
                if (!handleName.equals("") && handle != head)
                    list.add(getStyleForType(handle) + handleName);

                boolean displayToolTips = true;
                int tipNum = 0;
                while (displayToolTips)
                {
                    tipNum++;
                    String tooltip = "Tooltip" + tipNum;
                    if (tags.getCompoundTag("InfiTool").hasKey(tooltip))
                    {
                        String tipName = tags.getCompoundTag("InfiTool").getString(tooltip);
                        if (!tipName.equals(""))
                            list.add(tipName);
                    }
                    else
                        displayToolTips = false;
                }
            }
        }
        int attack = (int) (tags.getCompoundTag("InfiTool").getInteger("Attack") * this.getDamageModifier());
        list.add("\u00A79+" + attack + " " + StatCollector.translateToLocalFormatted("attribute.name.generic.attackDamage"));
        list.add("\u00A79+" + tags.getCompoundTag("InfiTool").getInteger("Attack") + " " + StatCollector.translateToLocalFormatted("attribute.name.ammo.attackDamage"));
    }

}
