package tinker.tconstruct.crafting;

/** Once upon a time, too many tools to count. Let's put them together automatically */
import java.util.*;

import tinker.tconstruct.EnumMaterial;
import tinker.tconstruct.items.ToolPart;
import tinker.tconstruct.modifiers.ToolMod;
import tinker.tconstruct.tools.ToolCore;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ToolBuilder
{
	public static ToolBuilder instance = new ToolBuilder();
	
	List<ToolRecipe> combos = new ArrayList<ToolRecipe>();
	HashMap<String, String> modifiers = new HashMap<String, String>();
	List<ToolMod> toolMods = new ArrayList<ToolMod>();

	/* Build tools */
	public static void addToolRecipe (ToolCore output, Item head)
	{
		addToolRecipe(output, head, null);
	}

	public static void addToolRecipe (ToolCore output, Item head, Item accessory)
	{
		instance.combos.add(new ToolRecipe(head, accessory, output));
	}

	public ToolCore getMatchingRecipe (Item head, Item handle, Item accessory)
	{
		for (ToolRecipe recipe : combos)
		{
			if (recipe.validHead(head) && recipe.validHandle(handle) && recipe.validAccessory(accessory))
				return recipe.getType();
		}
		return null;
	}

	//Builds a tool from the parts given
	public ItemStack buildTool (ItemStack headStack, ItemStack handleStack, ItemStack accessoryStack, String name)
	{		
		if (headStack != null && headStack.getItem() instanceof ToolCore)
			return modifyTool(headStack, handleStack, accessoryStack);
		
		if (headStack == null || handleStack == null) //Nothing to build without these. All tools need at least two parts!
			return null;
		
		ToolCore item;
		if (accessoryStack == null)
			item = getMatchingRecipe(headStack.getItem(), handleStack.getItem(), null);
		else
			item = getMatchingRecipe(headStack.getItem(), handleStack.getItem(), accessoryStack.getItem());
		if (item == null)
			return null;
		
		//System.out.println("Tool name: "+item.getToolName());
		int head = headStack.getItemDamage();
		int handle = handleStack.getItemDamage();
		int accessory = -1;
		if (accessoryStack != null)
			accessory = accessoryStack.getItemDamage();
		if (handleStack.getItem() == Item.bone) //Don't worry about stick, it should be metadata 0.
			handle = 5;

		int durability = (int) (EnumMaterial.durability(head) * EnumMaterial.handleDurability(handle) * item.getDurabilityModifier());
		if (accessoryStack != null && (item.getHeadType() == 2 || item.getHeadType() == 3) )
			durability = (int) ((EnumMaterial.durability(head) + EnumMaterial.durability(accessory))/2 * EnumMaterial.handleDurability(handle) * item.getDurabilityModifier());
		{
			/*Item accessoryItem = accessoryStack.getItem();
			if (accessoryItem instanceof ToolPart && ((ToolPart)accessoryItem).isHead) //Two heads
				durability = (int) ((EnumMaterial.durability(head) + EnumMaterial.durability(accessory))/2 * EnumMaterial.handleDurability(handle) * item.getDurabilityModifier());*/
		}

		ItemStack tool = new ItemStack(item);
		//System.out.println("Stack name: "+tool);
		NBTTagCompound compound = new NBTTagCompound();
		compound.setCompoundTag("InfiTool", new NBTTagCompound());
		compound.getCompoundTag("InfiTool").setInteger("Head", head);
		compound.getCompoundTag("InfiTool").setInteger("Handle", handle);
		compound.getCompoundTag("InfiTool").setInteger("Accessory", accessory);
		compound.getCompoundTag("InfiTool").setInteger("RenderHead", head);
		compound.getCompoundTag("InfiTool").setInteger("RenderHandle", handle);
		compound.getCompoundTag("InfiTool").setInteger("RenderAccessory", accessory);

		compound.getCompoundTag("InfiTool").setInteger("Damage", 0); //Damage is damage to the tool
		compound.getCompoundTag("InfiTool").setInteger("TotalDurability", durability);
		compound.getCompoundTag("InfiTool").setInteger("BaseDurability", durability);
		compound.getCompoundTag("InfiTool").setInteger("BonusDurability", 0); //Modifier
		compound.getCompoundTag("InfiTool").setFloat("ModDurability", 0f); //Modifier
		compound.getCompoundTag("InfiTool").setBoolean("Broken", false);
		compound.getCompoundTag("InfiTool").setInteger("Attack", EnumMaterial.attack(head) + item.getDamageVsEntity(null));
		
		compound.getCompoundTag("InfiTool").setInteger("MiningSpeed", EnumMaterial.toolSpeed(head));
		if (item.getHeadType() == 2)
		{
			int hLvl = EnumMaterial.harvestLevel(head);
			int shLvl = EnumMaterial.harvestLevel(accessory);
		}
		else
			compound.getCompoundTag("InfiTool").setInteger("HarvestLevel", EnumMaterial.harvestLevel(head));
		
		if (item.getHeadType() == 3)
		{
			compound.getCompoundTag("InfiTool").setInteger("MiningSpeed2", EnumMaterial.toolSpeed(accessory));
			compound.getCompoundTag("InfiTool").setInteger("HarvestLevel2", EnumMaterial.harvestLevel(accessory));
		}

		compound.getCompoundTag("InfiTool").setInteger("Unbreaking", buildUnbreaking(head, handle, accessory));
		compound.getCompoundTag("InfiTool").setFloat("Shoddy", buildShoddy(head, handle, accessory));

		int modifiers = 3;
		if (accessory == -1)
			modifiers += (head == 9 ? 2 : 0) + (handle == 9 ? 1 : 0);
		else
			modifiers += (head == 9 ? 1 : 0) + (handle == 9 ? 1 : 0) + (accessory == 9 ? 1 : 0);
		compound.getCompoundTag("InfiTool").setInteger("Modifiers", modifiers);

		if (name != "" && name != null)
		{
			compound.setCompoundTag("display", new NBTTagCompound());
			compound.getCompoundTag("display").setString("Name", "\u00A7f" + name);
		}

		tool.setTagCompound(compound);

		return tool;
	}

	/*public ItemStack modifyTool (ItemStack input, ItemStack topSlot, ItemStack bottomSlot)
	{
		ItemStack tool = input.copy();
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		tags.removeTag("Built");
		
		for (ToolMod mod : toolMods)
		{
			ItemStack[] slots = new ItemStack[] {topSlot, bottomSlot};
			if (mod.matches(slots, tool))
			{
				mod.modify(slots, tool);
				mod.addMatchingEffect(tool);
			}
		}
		
		return tool;
	}*/
	
	public ItemStack modifyTool (ItemStack input, ItemStack topSlot, ItemStack bottomSlot)
	{
		ItemStack tool = input.copy();
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		tags.removeTag("Built");
		
		boolean built = false;
		for (ToolMod mod : toolMods)
		{
			ItemStack[] slots = new ItemStack[] {topSlot, bottomSlot};
			if (mod.matches(slots, tool))
			{
				built = true;
				mod.addMatchingEffect(tool);
				mod.modify(slots, tool);
			}
		}
		
		if (built)
			return tool;
		else
			return null;
	}

	int buildUnbreaking (int head, int handle, int accessory)
	{
		int durability = 0;

		int dHead = EnumMaterial.unbreaking(head);
		int dHandle = EnumMaterial.unbreaking(handle);
		int dAccessory = 0;
		if (accessory != -1)
			dAccessory = EnumMaterial.unbreaking(accessory);

		if (dHead > durability)
			durability = dHead;
		if (dHandle > durability)
			durability = dHandle;
		if (dAccessory > durability)
			durability = dAccessory;

		return durability;
	}

	float buildShoddy (int head, int handle, int accessory)
	{
		float sHead = EnumMaterial.shoddy(head);
		float sHandle = EnumMaterial.shoddy(handle);
		if (accessory != -1)
		{
			float sAccessory = EnumMaterial.shoddy(accessory);
			return (sHead + sHandle + sAccessory) / 3f;
		}
		return (sHead + sHandle) / 2f;
	}
	
	public static void registerToolMod(ToolMod mod)
	{
		instance.toolMods.add(mod);
	}
}
