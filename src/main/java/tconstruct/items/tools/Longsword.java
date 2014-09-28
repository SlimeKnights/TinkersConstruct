package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

public class Longsword extends Weapon
{
    public Longsword()
    {
        super(4);
        this.setUnlocalizedName("InfiTool.Longsword");
    }

    @Override
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        if (player.onGround)
        {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public float chargeAttack ()
    {
        return 1.5f;
    }

    @Override
    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int useCount)
    {
        /*
         * if (player.onGround) { int time = this.getMaxItemUseDuration(stack) -
         * useCount; if (time > 5) { player.addExhaustion(0.2F);
         * player.setSprinting(true);
         * 
         * float speed = 0.05F * time; if (speed > 0.925f) speed = 0.925f;
         * 
         * float increase = (float) (0.02 * time + 0.2); if (increase > 0.56f)
         * increase = 0.56f; player.motionY += increase + speed/3;
         * 
         * player.motionX = (double) (-MathHelper.sin(player.rotationYaw /
         * 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch /
         * 180.0F * (float) Math.PI) * speed); player.motionZ = (double)
         * (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) *
         * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) *
         * speed); } }
         */
        int time = this.getMaxItemUseDuration(stack) - useCount;
        if (time > 5)
        {
            player.addExhaustion(0.2F);
            player.setSprinting(true);

            float increase = (float) (0.02 * time + 0.2);
            if (increase > 0.56f)
                increase = 0.56f;
            player.motionY += increase;

            float speed = 0.05F * time;
            if (speed > 0.925f)
                speed = 0.925f;
            player.motionX = (double) (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
            player.motionZ = (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
        }
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
                player.movementInput.moveForward *= 5.0F;
                player.movementInput.moveStrafe *= 5.0F;
            }
        }
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.swordBlade;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.handGuard;
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
