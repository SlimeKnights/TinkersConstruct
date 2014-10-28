package tconstruct.library.weaponry;

import tconstruct.weaponry.client.CrosshairType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Throwing weapons that utilize the ammo system on themselves.
 * Throwing knifes etc.
 */
public abstract class AmmoWeapon extends AmmoItem implements IAccuracy, IWindup {
    public AmmoWeapon(int baseDamage, String name) {
        super(baseDamage, name);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    } // we use custom animation renderiiing!

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return false;
    }

    /**
     * How long it takes to "ready" the weapon. To reach the point, where holding the right mouse button any longer doesn't have an impact.
     */
    @Override
    public int getWindupTime(ItemStack itemStack) { return 0; }

    @Override
    public float getMinWindupProgress(ItemStack itemStack) {
        return 0;
    }

    public float getWindupProgress(ItemStack itemStack, int timeInUse)
    {
        float time = (float) timeInUse;
        float windup = getWindupTime(itemStack);
        if(time > windup)
            time = windup;

        return time/windup;
    }


    public float minAccuracy(ItemStack itemStack) { return 0.5f; }
    public float maxAccuracy(ItemStack itemStack) { return 0.5f; }

    public float getWindupProgress(ItemStack itemStack, EntityPlayer player)
    {
        // what are you doing!
        if(player.inventory.getCurrentItem() != itemStack)
            return 0f;

        // are we using it?
        if(player.getItemInUse() == null)
            return 0f;

        return getWindupProgress(itemStack, getMaxItemUseDuration(itemStack) -  player.getItemInUseCount());
    }

    public float getAccuracy(ItemStack itemStack, EntityPlayer player)
    {
        float dif = minAccuracy(itemStack) - maxAccuracy(itemStack);

        return minAccuracy(itemStack) - dif * getWindupProgress(itemStack, player);
    }

    @Override
    public String[] getTraits() {
        return new String[] {"weapon", "thrown", "ammo", "windup"};
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int durationLeft) {
        int time = this.getMaxItemUseDuration(stack) - durationLeft;
        if(getWindupProgress(stack, time) >= getMinWindupProgress(stack))
            launchProjectile(stack, world, player);
    }

    protected void launchProjectile(ItemStack stack, World world, EntityPlayer player) {
        // spawn projectile on server
        if(!world.isRemote) {
            ItemStack reference = stack.copy();
            reference.stackSize = 1;
            reference.getTagCompound().getCompoundTag("InfiTool").setInteger("Ammo", 1);
            Entity projectile = createProjectile(reference, world, player, getAccuracy(stack, player));
            world.spawnEntityInWorld(projectile);
        }

        // reduce ammo
        if(!player.capabilities.isCreativeMode)
            this.consumeAmmo(1, stack);
    }

    protected abstract Entity createProjectile(ItemStack reference, World world, EntityPlayer player, float accuracy);

    /** used for displaying the damage, return the value used for pseed in createProjectile/ProjectileBase constructor
     */
    public abstract float getProjectileSpeed();

    @SideOnly(Side.CLIENT)
    public CrosshairType getCrosshairType() { return CrosshairType.SQUARE; }

    @Override
    public boolean zoomOnWindup(ItemStack itemStack) {
        return false;
    }

    @Override
    public float getZoom(ItemStack itemStack) {
        return 1.0f;
    }
}
