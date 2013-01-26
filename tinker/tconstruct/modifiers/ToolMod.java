package tinker.tconstruct.modifiers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ToolMod
{
	// The ItemStacks used to compare against
	//ItemStack[] stacks;
	public final String key;
	public final List stacks;
	public final int effectIndex; //255 is always blank

	public ToolMod(ItemStack[] items, int effect, String dataKey)
	{
		//recipeItems = items;
		List<ItemStack> itemstacks = new ArrayList<ItemStack>();
		for (int iter = 0; iter < items.length; iter++)
			itemstacks.add(items[iter]);
		stacks = itemstacks;
		effectIndex = effect;
		key = dataKey;
	}

	/** Checks to see if the inputs match the stored items
	 * Note: Works like ShapelessRecipes
	 * 
	 * @param input The ItemStacks to compare against
	 * @param tool Item to modify, used for restrictions
	 * @return Whether the recipe matches the input
	 */
	public boolean matches (ItemStack[] input, ItemStack tool)
	{
		if (!canModify(tool, input))
			return false;

		ArrayList list = new ArrayList(this.stacks);

		for (int iter = 0; iter < input.length; ++iter)
		{
			ItemStack craftingStack = input[iter];

			if (craftingStack != null)
			{
				boolean canCraft = false;
				Iterator iterate = list.iterator();

				while (iterate.hasNext())
				{
					ItemStack removeStack = (ItemStack) iterate.next();

					if (craftingStack.itemID == removeStack.itemID && (removeStack.getItemDamage() == -1 || craftingStack.getItemDamage() == removeStack.getItemDamage()))
					{
						canCraft = true;
						list.remove(removeStack);
						break;
					}
				}

				if (!canCraft)
				{
					return false;
				}
			}
		}

		return list.isEmpty();
	}

	/**
	 * 
	 * @param tool Tool to compare against
	 * @return Whether the tool can be modified
	 */

	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		return tags.getInteger("Modifiers") > 0;
	}

	/** Modifies the tool. Adds nbttags, changes existing ones, ticks down modification counter, etc
	 * 
	 * @param input ItemStacks to pull info from
	 * @param tool The tool to modify
	 */
	public abstract void modify (ItemStack[] input, ItemStack tool);

	public void addMatchingEffect (ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (tags.hasKey("Effect3") || tags.hasKey(key))
			return;

		else if (tags.hasKey("Effect2"))
		{
			//System.out.println("Adding Effect 3");
			tags.setInteger("Effect3", effectIndex);
		}
		else if (tags.hasKey("Effect1"))
		{
			//System.out.println("Adding Effect 2");
			tags.setInteger("Effect2", effectIndex);
		}
		else
		{
			//System.out.println("Adding Effect 1");
			tags.setInteger("Effect1", effectIndex);
		}
	}

	protected int addModifierTip (ItemStack tool, String modifierTip)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int tipNum = 0;
		while (true)
		{
			tipNum++;
			String tip = "Tooltip" + tipNum;
			if (!tags.hasKey(tip))
			{
				tags.setString(tip, "");
				String modTip = "ModifierTip" + tipNum;
				tags.setString(modTip, modifierTip);
				return tipNum;
			}
		}
	}
	
	protected int addToolTip (ItemStack tool, String tooltip, String modifierTip)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int tipNum = 0;
		while (true)
		{
			tipNum++;
			String tip = "Tooltip" + tipNum;
			if (!tags.hasKey(tip))
			{
				tags.setString(tip, tooltip);
				String modTip = "ModifierTip" + tipNum;
				tags.setString(modTip, modifierTip);
				return tipNum;
			}
		}
	}
}
