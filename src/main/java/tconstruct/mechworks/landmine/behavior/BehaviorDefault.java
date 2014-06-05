package tconstruct.mechworks.landmine.behavior;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.util.DamageSourceLandmine;

public class BehaviorDefault extends Behavior
{

    @Override
    public void executeLogic (World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, Entity triggerer, boolean willBlockBeRemoved)
    {
        if (triggerer != null)
        {
            triggerer.attackEntityFrom(new DamageSourceLandmine("Landmine"), 5F);
        }
    }

}
