package tinker.armory.content;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/*
 * Dummy class, used for rendering equipment and nothing else!
 */

public abstract class EntityEquipment extends EntityLiving
	implements IInventory
{
	public EntityEquipment(World par1World) 
	{
		super(par1World);
		equipmentDropChances = new float[] { 1f, 1f, 1f, 1f, 1f };
	}
	
	public abstract Container getContainer(EntityPlayer player);
	
	public String getTexture()
	{
		return this.texture;
	}
	
	protected void kill()
    {
        this.attackEntityFrom(DamageSource.outOfWorld, 1000);
    }
	
	protected void dropFewItems(boolean par1, int par2)
    {
        this.dropItem(this.getDropItemId(), 1);
    }
	
	protected void dropEquipment(boolean par1, int par2)
    {
        for (int var3 = 0; var3 < this.getLastActiveItems().length; ++var3)
        {
            ItemStack var4 = this.getCurrentItemOrArmor(var3);
            boolean var5 = this.equipmentDropChances[var3] > 1.0F;

            if (var4 != null && (par1 || var5) && this.rand.nextFloat() - (float)par2 * 0.01F < this.equipmentDropChances[var3])
            {
                if (!var5 && var4.isItemStackDamageable())
                {
                    int var6 = Math.max(var4.getMaxDamage() - 25, 1);
                    int var7 = var4.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(var6) + 1);

                    if (var7 > var6)
                    {
                        var7 = var6;
                    }

                    if (var7 < 1)
                    {
                        var7 = 1;
                    }

                    //var4.setItemDamage(var7);
                }

                this.entityDropItem(var4, 0.0F);
            }
        }
    }
	
	public int getMaxHealth() { return 5; }	
	public boolean canBePushed() { return false; }	
	protected int decreaseAirSupply(int par1) { return 0; }	
	//public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) { return false; }	
	//protected void damageEntity(DamageSource par1DamageSource, int par2) { super.damageEntity(par1DamageSource, par2); }
	protected void updateFallState(double par1, boolean par3) {}
	public void knockBack(Entity par1Entity, int par2, double par3, double par5) {}
	protected void fall(float par1) {}
	public void moveEntityWithHeading(float par1, float par2) {}
	public boolean canBreatheUnderwater() { return true; }
	public void onLivingUpdate() { }
	public boolean isBlocking() { return true; }
	protected void jump() {}
	protected boolean canDespawn() { return true; }
	//protected void despawnEntity() { super.despawnEntity(); }
	protected void updateAITasks() {}
	protected void updateEntityActionState() {}
	protected void updateArmSwingProgress() {}
	public boolean getCanSpawnHere() { return false; }
	protected void updatePotionEffects() {}
	public void addPotionEffect(PotionEffect par1PotionEffect) {}
	public boolean isPotionApplicable(PotionEffect par1PotionEffect) { return false; }
}
