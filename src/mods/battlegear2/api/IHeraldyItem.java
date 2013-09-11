package mods.battlegear2.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

@Deprecated
public interface IHeraldyItem {
	
	public enum HeraldyRenderPassess{
		/**
		 * The first render pass. This will use the items base icon coloured the primary colour
		 */
		PrimaryColourBase,
		/**
		 * The second render pass. This will use the selected pattern overlayed on top of the base icon with the apropriate colour
		 */
		SecondaryColourPattern,
		/**
		 * The third render pass. The sigil(s) will be rendered on top of the pattern in the apropriate positions
		 */
		Sigil,
		/**
		 * Fourth render pass. The items trim icon will be rendered in the secondady colour
		 */
		SecondaryColourTrim,
		/**
		 * Fifth Render pass, the Items post render icon will be rendered in the default colour
		 */
		PostRenderIcon
	}
	
	/**
	 * Returns the "base" icon. This icon will be coloured the primary colour
	 */
	public Icon getBaseIcon(ItemStack stack);
	/**
	 * Returns the trim icon, This will be coloured the secondary colour
	 */
	public Icon getTrimIcon(ItemStack stack);
	/**
	 * Returns the post render icon, this Icon will render after all other rendering passess in it's default colour
	 */
	public Icon getPostRenderIcon(ItemStack stack);
	
	
	/**
	 * Returns true if the given itemstack has heraldy attached
	 */
	public boolean hasHeraldry(ItemStack stack);
	/**
	 * Returns the current heraldy code, this will only be called on ItemStacks that have been found to have heraldrybackup using the hasHeraldryMethod
	 */
	public byte[] getHeraldryCode(ItemStack stack);
	
	/**
	 * Saves the given heraldy code in the given stack. It is recommended to use the stacks NBT Tag compound for this
	 */
	public void setHeraldryCode(ItemStack stack, byte[] code);
	
	/**
	 * Removes the heraldy code from the item
	 */
	public void removeHeraldry(ItemStack item);
	
	/**
	 * Returns true if the default renderer should perform the given render pass
	 */
	public boolean shouldDoPass(HeraldyRenderPassess pass);
	
	/**
	 * Returns true if the default renderer should be used. 
	 * If the method returns true, the default renderer will be attached. 
	 * If this method returns false it is the modders responsibility to attach an appropriate renderer
	 */
	public boolean useDefaultRenderer();

}
