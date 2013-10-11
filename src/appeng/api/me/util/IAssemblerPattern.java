package appeng.api.me.util;

import java.util.List;

import appeng.api.IItemList;
import appeng.api.me.tiles.IPushable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Interact with the internals of assembler patterns, get this via Util.getAssemblerPattern(...)
 */
public interface IAssemblerPattern
{
	
	IAssemblerCluster getCluster();
	
    /** Returns a 3x3 matrix of nulls or ItemStacks, or null if it is not included. */
    ItemStack[] getCraftingMatrix();
    
    /** 
     * Same as getCraftingMatrix(), but gets a crafting inventory for real crafting.
     * 
     * Item pool is optional, null will work, but it won't be able to edit items... 
     */
    InventoryCrafting getCraftingInv( World w, IMEInventory itemPool, List<ItemStack> missing, List<ItemStack> used, IItemList all );
    
    /** Returns a ItemStack, if the pattern is encoded, this will ALWAYS have a value. */
    ItemStack getOutput();
    
    /** Returns true if there is a pattern encoded. */
    boolean isEncoded();
    
    /** 
     * Encode a pattern.
     * craftingMatrix - accepts a 3x3 grid of ItemStacks and Nulls.
     * output - accepts a single ItemStack, NEVER SEND NULL 
     */
    void encodePattern(ItemStack[] craftingMatrix, ItemStack output);
    
    /**
     * I have no idea what the World is for, its just part of IRecipe...
     */
    boolean isCraftable(World w);
    
    /**
     * Returns a condensed list of requirements.
     * 
     * Example: sticks, will return a single stack of 2, rather then two stacks of 1.
     * The same Item will not show more than one stack.
     */
    public List<ItemStack> condensedRequirements();
    
    /** Returns the Tile Entity for the interface... */
	List<IPushable> getInterface();
	
	/** Sets the Tile Entity for the interface... */
	void setInterface(IPushable a);
	
	/** returns the output of the pattern */
	ItemStack getRecipeOutput( InventoryCrafting ic, World w );
	
	/** returns the recipe for the patterns */
	IRecipe getMatchingRecipe( InventoryCrafting ic, World w );
	
	/**
	 * Compare to Patterns
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj);
	
}

