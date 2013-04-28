package mods.tinker.tconstruct.library.crafting;

/** Once upon a time, too many tools to count. Let's put them together automatically */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.tools.ToolMaterial;
import mods.tinker.tconstruct.library.tools.ToolMod;
import mods.tinker.tconstruct.library.util.IToolPart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ToolBuilder
{
	public static ToolBuilder instance = new ToolBuilder();
	
	public List<ToolRecipe> combos = new ArrayList<ToolRecipe>();
	public HashMap<String, String> modifiers = new HashMap<String, String>();
	public List<ToolMod> toolMods = new ArrayList<ToolMod>();

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
		boolean validMaterials = true;
		int head = -1, handle = -1, accessory = -1;
		if (headStack.getItem() instanceof IToolPart)
		{
			head = ((IToolPart) headStack.getItem()).getMaterialID(headStack);
			//handle = ((IToolPart) handleStack.getItem()).getMaterialID(handleStack);
		}
		else
			validMaterials = false;
		
		Item handleItem = handleStack.getItem();
		
		if (handleItem == Item.stick)
			handle = 0;		
		else if (handleItem == Item.bone)
			handle = 5;
		else if (handleItem instanceof IToolPart)
			handle = ((IToolPart)handleItem).getMaterialID(handleStack);
		else
			validMaterials = false;
		
		if (!validMaterials)
			return null;
		
		if (accessoryStack == null)
		{
			item = getMatchingRecipe(headStack.getItem(), handleStack.getItem(), null);
		}
		else
		{
			if (accessoryStack.getItem() instanceof IToolPart)
				accessory = ((IToolPart) accessoryStack.getItem()).getMaterialID(accessoryStack);
			else
				return null;
			
			item = getMatchingRecipe(headStack.getItem(), handleStack.getItem(), accessoryStack.getItem());
		}
		
		if (item == null)
			return null;
		
		
		
		ToolMaterial headMat = null, handleMat = null, accessoryMat = null;
		headMat = TConstructRegistry.getMaterial(head);
		handleMat = TConstructRegistry.getMaterial(handle);
		if (accessory != -1)
			accessoryMat = TConstructRegistry.getMaterial(accessory);		

		int durability = (int) (headMat.durability() * handleMat.handleDurability() * item.getDurabilityModifier());
		if (accessoryStack != null && (item.getHeadType() == 2 || item.getHeadType() == 3) )
			durability = (int) ((headMat.durability() + accessoryMat.durability())/2 * handleMat.handleDurability() * item.getDurabilityModifier());

		ItemStack tool = new ItemStack(item);
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
		compound.getCompoundTag("InfiTool").setInteger("Attack", headMat.attack() + item.getDamageVsEntity(null));
		
		compound.getCompoundTag("InfiTool").setInteger("MiningSpeed", headMat.toolSpeed());
		if (item.getHeadType() == 2)
		{
			int hLvl = headMat.harvestLevel();
			int shLvl = accessoryMat.harvestLevel();
		}
		else
			compound.getCompoundTag("InfiTool").setInteger("HarvestLevel", headMat.harvestLevel());
		
		if (item.getHeadType() == 3)
		{
			compound.getCompoundTag("InfiTool").setInteger("MiningSpeed2", accessoryMat.toolSpeed());
			compound.getCompoundTag("InfiTool").setInteger("HarvestLevel2", accessoryMat.harvestLevel());
		}

		compound.getCompoundTag("InfiTool").setInteger("Unbreaking", buildReinforced(headMat, handleMat, accessoryMat));
		compound.getCompoundTag("InfiTool").setFloat("Shoddy", buildShoddy(headMat, handleMat, accessoryMat));

		int modifiers = 3;
		if (accessory == -1)
			modifiers += (head == 9 ? 2 : 0) + (handle == 9 ? 1 : 0);
		else
			modifiers += (head == 9 ? 1 : 0) + (handle == 9 ? 1 : 0) + (accessory == 9 ? 1 : 0);
		compound.getCompoundTag("InfiTool").setInteger("Modifiers", modifiers);

		if (name != null && !name.equals(""))
		{
			compound.setCompoundTag("display", new NBTTagCompound());
			compound.getCompoundTag("display").setString("Name", "\u00A7f" + name);
		}

		tool.setTagCompound(compound);

		return tool;
	}
	
	public ItemStack modifyTool (ItemStack input, ItemStack topSlot, ItemStack bottomSlot)
	{
	    //if (topSlot == null && bottomSlot == null)
	        //return input;
	    
		ItemStack tool = input.copy();
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		tags.removeTag("Built");
		
		if (topSlot == null && bottomSlot == null)
		    return tool;
		
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

	int buildReinforced (ToolMaterial headMat, ToolMaterial handleMat, ToolMaterial accessoryMat)
	{
		int durability = 0;

		int dHead = headMat.reinforced();
		int dHandle = handleMat.reinforced();
		int dAccessory = 0;
		if (accessoryMat != null)
			dAccessory = accessoryMat.reinforced();

		if (dHead > durability)
			durability = dHead;
		if (dHandle > durability)
			durability = dHandle;
		if (dAccessory > durability)
			durability = dAccessory;

		return durability;
	}

	float buildShoddy (ToolMaterial headMat, ToolMaterial handleMat, ToolMaterial accessoryMat)
	{
		float sHead = headMat.shoddy();
		float sHandle = handleMat.shoddy();
		if (accessoryMat != null)
		{
			float sAccessory = accessoryMat.shoddy();
			return (sHead + sHandle + sAccessory) / 3f;
		}
		return (sHead + sHandle) / 2f;
	}
	
	public static void registerToolMod(ToolMod mod)
	{
		instance.toolMods.add(mod);
	}
}
