package tconstruct.mechworks.landmine.behavior;

import mantle.blocks.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * @author fuj1n
 * 
 */
public class BehaviorBlockThrow extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        for (int i = 0; i < par5ItemStack.stackSize; i++)
        {
            int direction = par1World.rand.nextInt(8);
            double speed = 2D * Math.random() + 0.01D;
            double upVecReduction = 0.25D;
            double randomnessFactor = 0.5D;
            double vec0 = 0, vec1 = par1World.rand.nextDouble() * upVecReduction, vec2 = 0;
            switch (direction)
            {
            case 0:
                vec0 = speed + par1World.rand.nextDouble() * randomnessFactor;
                // vec2 = par1World.rand.nextDouble() * randomnessFactor;
                break;
            case 1:
                vec0 = -speed + par1World.rand.nextDouble() * randomnessFactor;
                // vec2 = par1World.rand.nextDouble() * randomnessFactor;
                break;
            case 2:
                vec2 = speed + par1World.rand.nextDouble() * randomnessFactor;
                // vec0 = par1World.rand.nextDouble() * randomnessFactor;
                break;
            case 3:
                vec2 = -speed + par1World.rand.nextDouble() * randomnessFactor;
                // vec0 = par1World.rand.nextDouble() * randomnessFactor;
                break;
            case 4:
                vec0 = speed + par1World.rand.nextDouble() * randomnessFactor;
                vec2 = speed + par1World.rand.nextDouble() * randomnessFactor;
                break;
            case 5:
                vec0 = speed + par1World.rand.nextDouble() * randomnessFactor;
                vec2 = -speed + par1World.rand.nextDouble() * randomnessFactor;
                break;
            case 6:
                vec0 = -speed + par1World.rand.nextDouble() * randomnessFactor;
                vec2 = speed + par1World.rand.nextDouble() * randomnessFactor;
                break;
            case 7:
                vec0 = -speed;
                vec2 = -speed;
                break;
            default:
                vec0 = speed;
                break;
            }

            EntityFallingBlock entityfallingsand = new EntityFallingBlock(par1World, (double) ((float) par2 + 0.5F), (double) ((float) par3 + 2F), (double) ((float) par4 + 0.5F), BlockUtils.getBlockFromItemStack(par5ItemStack), par5ItemStack.getItemDamage());
            entityfallingsand.preventEntitySpawning = false;
            entityfallingsand.ticksExisted = 2;
            entityfallingsand.setVelocity(vec0, vec1, vec2);
            par1World.spawnEntityInWorld(entityfallingsand);
        }
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return true;
    }

    @Override
    public int getStackLimit (ItemStack par1ItemStack)
    {
        return 64;
    }
}
