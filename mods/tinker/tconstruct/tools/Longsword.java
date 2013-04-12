package mods.tinker.tconstruct.tools;

import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.library.AbilityHelper;
import mods.tinker.tconstruct.library.Weapon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Longsword extends Weapon
{
    public Longsword(int itemID)
    {
        super(itemID, 4);
        this.setUnlocalizedName("InfiTool.Longsword");
    }

    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        if (player.onGround)
        {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            NBTTagCompound tags = stack.getTagCompound();
            tags.getCompoundTag("InfiTool").setBoolean("InUse", true);
        }
        return stack;
    }

    public float chargeAttack ()
    {
        return 1.5f;
    }

    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int time)
    {
        if (time > 10)
        {
            player.setSprinting(true);
            player.motionY += 0.62;

            float f = 1.0F;
            player.motionX = (double) (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
            player.motionZ = (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
        }
    }

    @Override
    protected Item getHeadItem ()
    {
        return TContent.swordBlade;
    }

    @Override
    protected Item getAccessoryItem ()
    {
        return TContent.handGuard;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_longsword_blade";
        case 1:
            return "_longsword_blade_broken";
        case 2:
            return "_longsword_handle";
        case 3:
            return "_longsword_accessory";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_longsword_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "longsword";
    }
}
