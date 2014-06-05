package tconstruct.mechworks.landmine.behavior;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BehaviorPreventExplode extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
    }

    @Override
    public boolean isBehaviorExchangableWithOffensive (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    public boolean doesBehaviorPreventRemovalOfBlock (ItemStack par1ItemStack)
    {
        return true;
    }

    @Override
    public boolean shouldItemBeRemoved (ItemStack par1ItemStack, boolean willBlockBeRemoved)
    {
        return false;
    }

    @Override
    public boolean isOffensive (ItemStack par1ItemStack)
    {
        return false;
    }

}
