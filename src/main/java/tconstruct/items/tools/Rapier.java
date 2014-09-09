package tconstruct.items.tools;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

public class Rapier extends Weapon
{
    public Rapier()
    {
        super(2);
        this.setUnlocalizedName("InfiTool.Rapier");
    }

    @Override
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.none;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        if (player.onGround)
        {
            player.addExhaustion(0.1f);
            player.motionY += 0.32;
            float f = 0.5F;
            player.motionX = (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
            player.motionZ = (double) (-MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
        }
        return stack;
    }

    @Override
    public boolean hitEntity (ItemStack stack, EntityLivingBase mob, EntityLivingBase player)
    {
        // AbilityHelper.hitEntity(stack, mob, player, damageVsEntity);
        // AbilityHelper.knockbackEntity(mob, 0.8f);
        mob.motionY *= 0.8;
        if (mob.hurtResistantTime > 18)
            mob.hurtResistantTime -= 5;
        return true;
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 0.7f;
    }

    @Override
    public float getDamageModifier ()
    {
        return 0.8f;
    }

    /*
     * public float chargeAttack () { return 1.0f; }
     */

    @Override
    public boolean pierceArmor ()
    {
        return true;
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.swordBlade;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.crossbar;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_rapier_blade";
        case 1:
            return "_rapier_blade_broken";
        case 2:
            return "_rapier_handle";
        case 3:
            return "_rapier_accessory";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_rapier_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "rapier";
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return none;
    }
}
