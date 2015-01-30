package tconstruct.weaponry.weapons;

import net.minecraft.client.entity.EntityPlayerSP;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.weaponry.client.CrosshairType;
import tconstruct.weaponry.entity.ThrowingKnifeEntity;
import tconstruct.library.weaponry.AmmoWeapon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.tools.TinkerTools;

public class ThrowingKnife extends AmmoWeapon {
    public ThrowingKnife() {
        super(1, "throwingknife");
    }

    @Override
    public int getPartAmount() {
        return 2;
    }

    @Override
    public String getIconSuffix(int partType) {
        switch (partType)
        {
            case 0:
                return "_knife_blade";
            case 1:
                return ""; // no broken, since it runs out of ammo
            case 2:
                return "_knife_handle";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix() {
        return "_knife_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "throwingknife";
    }

    @Override
    public Item getHeadItem() {
        return TinkerTools.knifeBlade;
    }

    @Override
    public Item getAccessoryItem() {
        return null;
    }

    @Override
    public int getWindupTime(ItemStack itemStack) { return 15; } // 1 1/2 seconds

    @Override
    public float getMinWindupProgress(ItemStack itemStack) {
        return 0.6f;
    }

    @Override
    public float maxAccuracy(ItemStack itemStack) {
        return 1f;
    }

    @Override
    public float minAccuracy(ItemStack itemStack) {
        return 10f;
    }

    @Override
    protected Entity createProjectile(ItemStack reference, World world, EntityPlayer player, float accuracy, int time) {
        ProjectileBase knife = new ThrowingKnifeEntity(world, player, getProjectileSpeed(), accuracy, reference);
        // if you aim long enough, it's a crit!
        if(time >= this.getWindupTime(reference)*1.5f)
            knife.setIsCritical(true);

        return knife;
    }

    @Override
    public float getProjectileSpeed() {
        return 2.2f;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrosshairType getCrosshairType() {
        return CrosshairType.SPIKE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        // aiming with throwing knives slows down much less than with a bow
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) entity;
            ItemStack usingItem = player.getItemInUse();
            if (usingItem != null && usingItem.getItem() == this)
            {
                player.movementInput.moveForward *= 3.0F;
                player.movementInput.moveStrafe *= 3.0F;
            }
        }
    }
}
