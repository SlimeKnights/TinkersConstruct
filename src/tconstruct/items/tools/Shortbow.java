package tconstruct.items.tools;

import java.util.List;

import tconstruct.common.TContent;
import tconstruct.library.tools.BowBase;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Shortbow extends BowBase
{
    public Shortbow(int itemID)
    {
        super(itemID);
        this.setUnlocalizedName("InfiTool.Shortbow");
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_bow_top";
        case 1:
            return "_bowstring_broken";
        case 2:
            return "_bowstring";
        case 3:
            return "_bow_bottom";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_bow_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "shortbow";
    }

    @Override
    public Item getHeadItem ()
    {
        return TContent.toolRod;
    }

    @Override
    public Item getHandleItem ()
    {
        return TContent.bowstring;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.toolRod;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "ranged", "bow" };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) entity;
            ItemStack usingItem = player.getItemInUse();
            if (usingItem != null && usingItem.getItem() == this)
            {
                player.movementInput.moveForward *= 2.0F;
                player.movementInput.moveStrafe *= 2.0F;
            }
        }
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
                int binding = tags.getCompoundTag("InfiTool").getInteger("Accessory");
                int extra = tags.getCompoundTag("InfiTool").getInteger("Extra");

                String headName = getAbilityNameForType(head);
                if (!headName.equals(""))
                    list.add(getStyleForType(head) + headName);

                String handleName = getBowstringName(handle);
                if (!handleName.equals("") && handle != head)
                    list.add(handleName);

                if (getPartAmount() >= 3)
                {
                    String bindingName = getAbilityNameForType(binding);
                    if (!bindingName.equals("") && binding != head && binding != handle)
                        list.add(getStyleForType(binding) + bindingName);
                }

                if (getPartAmount() >= 4)
                {
                    String extraName = getAbilityNameForType(extra);
                    if (!extraName.equals("") && extra != head && extra != handle && extra != binding)
                        list.add(getStyleForType(extra) + extraName);
                }

                int unbreaking = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");
                String reinforced = getReinforcedName(head, handle, binding, extra, unbreaking);
                if (!reinforced.equals(""))
                    list.add(reinforced);

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
    }

    public String getBowstringName (int type)
    {
        switch (type)
        {
        case 0:
            return "";
        case 1:
            return "\u00A7bEnchanted";
        default:
            return "";
        }
        //return TConstructRegistry.getMaterial(type).ability();
    }
}
