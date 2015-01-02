package tconstruct.weaponry;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.DualMaterialToolPart;
import tconstruct.library.util.IPattern;
import tconstruct.library.util.IToolPart;
import tconstruct.tools.TinkerTools;
import tconstruct.util.Reference;

public class AlternateBoltRecipe implements IRecipe {
    static {
        // register the recipe with the recipesorter
        RecipeSorter.register(Reference.MOD_ID + ":part", AlternateBoltRecipe.class, RecipeSorter.Category.SHAPELESS, "");
    }

    private ItemStack outputPart;

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        outputPart = null;
        ItemStack rod = null;
        ItemStack head = null;

        for(int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack slot = inventoryCrafting.getStackInSlot(i);
            // empty slot
            if(slot == null)
                continue;

            // is it the tool?
            if(slot.getItem() == TinkerWeaponry.arrowhead) {
                // only one arrowhead
                if(head != null)
                    return false;

                head = slot;
            }
            else if(slot.getItem() == TinkerTools.toolRod) {
                // only one rod
                if(rod != null)
                    return false;

                rod = slot;
            }
            else {
                // unknown object
                return false;
            }
        }

        if(rod == null || head == null)
            return false;

        // craft the bolt
        int mat1 = ((IToolPart)rod.getItem()).getMaterialID(rod);
        int mat2 = ((IToolPart)head.getItem()).getMaterialID(head);

        outputPart = DualMaterialToolPart.createDualMaterial(TinkerWeaponry.partBolt, mat1, mat2);

        return outputPart != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return outputPart;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return outputPart;
    }
}
