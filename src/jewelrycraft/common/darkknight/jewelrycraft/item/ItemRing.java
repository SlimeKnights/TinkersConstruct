package common.darkknight.jewelrycraft.item;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import tconstruct.TConstruct;
import tconstruct.library.IHealthAccessory;
import tconstruct.util.player.ArmorExtended;
import tconstruct.util.player.TPlayerStats;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import common.darkknight.jewelrycraft.JewelrycraftMod;
import common.darkknight.jewelrycraft.block.BlockList;
import common.darkknight.jewelrycraft.util.JewelryNBT;
import common.darkknight.jewelrycraft.util.JewelrycraftUtil;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRing extends Item
{
	public Icon jewel;
	private int amplifier, cooldown = 0;
	int index = 0;

	public ItemRing(int par1)
	{
		super(par1);
		this.setMaxStackSize(1);
	}

	public void registerIcons(IconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon("jewelrycraft:ring");
		jewel = iconRegister.registerIcon("jewelrycraft:jewel");
	}

	@Override
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int pass)
	{
		try
		{
			return color(par1ItemStack, pass);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 16777215;
	}

	public Icon getIcon(ItemStack stack, int pass)
	{
		if (JewelryNBT.jewel(stack) != null) return pass == 0 ? itemIcon : jewel;
		return itemIcon;
	}

	public static int color(ItemStack stack, int pass) throws IOException
	{
		String domain = "", texture;
		ResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		if (pass == 1 && JewelryNBT.ingot(stack) != null && JewelryNBT.jewel(stack) == null)
		{
			if (JewelryNBT.ingot(stack).getIconIndex().getIconName().substring(0, JewelryNBT.ingot(stack).getIconIndex().getIconName().indexOf(":") + 1) != "") domain = JewelryNBT.ingot(stack).getIconIndex().getIconName().substring(0, JewelryNBT.ingot(stack).getIconIndex().getIconName().indexOf(":") + 1).replace(":", " ").trim();
			else domain = "minecraft";
			texture = JewelryNBT.ingot(stack).getIconIndex().getIconName().substring(JewelryNBT.ingot(stack).getIconIndex().getIconName().lastIndexOf(":") + 1) + ".png";
			ResourceLocation ingot = null;
			if (JewelryNBT.ingot(stack).getUnlocalizedName().contains("item")) ingot = new ResourceLocation(domain, "textures/items/" + texture);
			else ingot = new ResourceLocation(domain, "textures/blocks/" + texture);
			BufferedImage bufferedimage = ImageIO.read(rm.getResource(ingot).getInputStream());
			return bufferedimage.getRGB(9, 9);
		}
		else if (JewelryNBT.ingot(stack) != null && JewelryNBT.jewel(stack) != null)
		{
			if (pass == 1)
			{
				if (JewelryNBT.jewel(stack).getIconIndex().getIconName().substring(0, JewelryNBT.jewel(stack).getIconIndex().getIconName().indexOf(":") + 1) != "") domain = JewelryNBT.jewel(stack).getIconIndex().getIconName().substring(0, JewelryNBT.jewel(stack).getIconIndex().getIconName().indexOf(":") + 1).replace(":", " ").trim();
				else domain = "minecraft";
				texture = JewelryNBT.jewel(stack).getIconIndex().getIconName().substring(JewelryNBT.jewel(stack).getIconIndex().getIconName().lastIndexOf(":") + 1) + ".png";
				ResourceLocation jewelLoc = null;
				if (JewelryNBT.jewel(stack).getUnlocalizedName().contains("item")) jewelLoc = new ResourceLocation(domain, "textures/items/" + texture);
				else jewelLoc = new ResourceLocation(domain, "textures/blocks/" + texture);
				BufferedImage bufferedimage = ImageIO.read(rm.getResource(jewelLoc).getInputStream());
				return bufferedimage.getRGB(9, 4);
			}
			if (JewelryNBT.ingot(stack).getIconIndex().getIconName().substring(0, JewelryNBT.ingot(stack).getIconIndex().getIconName().indexOf(":") + 1) != "") domain = JewelryNBT.ingot(stack).getIconIndex().getIconName().substring(0, JewelryNBT.ingot(stack).getIconIndex().getIconName().indexOf(":") + 1).replace(":", " ").trim();
			else domain = "minecraft";
			texture = JewelryNBT.ingot(stack).getIconIndex().getIconName().substring(JewelryNBT.ingot(stack).getIconIndex().getIconName().lastIndexOf(":") + 1) + ".png";
			ResourceLocation ingot = null;
			if (JewelryNBT.ingot(stack).getUnlocalizedName().contains("item")) ingot = new ResourceLocation(domain, "textures/items/" + texture);
			else ingot = new ResourceLocation(domain, "textures/blocks/" + texture);
			BufferedImage bufferedimage = ImageIO.read(rm.getResource(ingot).getInputStream());
			return bufferedimage.getRGB(9, 9);
		}
		return 16777215;
	}

	public String getItemDisplayName(ItemStack stack)
	{
		if (JewelryNBT.ingot(stack) != null && JewelryNBT.jewel(stack) != null && JewelryNBT.isJewelX(stack, new ItemStack(Item.diamond)) && JewelryNBT.isIngotX(stack, new ItemStack(Item.ingotGold))) return "Wedding Ring";
		else if(JewelryNBT.ingot(stack) != null) return JewelryNBT.ingot(stack).getDisplayName().replace("Ingot", " ").trim() + " " + ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
		return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote){
			if (JewelryNBT.playerPosX(stack) != -1 && JewelryNBT.playerPosY(stack) != -1 && JewelryNBT.playerPosZ(stack) != -1){
				double posX = JewelryNBT.playerPosX(stack), posY = JewelryNBT.playerPosY(stack), posZ = JewelryNBT.playerPosZ(stack);
				if (JewelryNBT.isJewelX(stack, new ItemStack(Item.enderPearl)) && JewelryNBT.isModifierX(stack, new ItemStack(Item.bed)) && JewelryNBT.dimension(stack) != -2 && JewelryNBT.dimName(stack) != null)
				{
					int dimension = JewelryNBT.dimension(stack);
					for (int i = 1; i <= 20; i++) world.spawnParticle("largesmoke", player.posX - 0.5D + Math.random(), player.posY - 1.5D + Math.random(), player.posZ - 0.5D + Math.random(), 0.0D, 0.0D, 0.0D);
					if (!JewelryNBT.isDimensionX(stack, player.dimension)) player.travelToDimension(dimension);
					player.setPositionAndUpdate(posX, posY, posZ);
					for (int i = 1; i <= 300; i++) world.spawnParticle("portal", posX - 0.5D + Math.random(), posY + Math.random(), posZ - 0.5D + Math.random(), 0.0D, 0.0D, 0.0D);
				}
				else if(JewelryNBT.isDimensionX(stack, player.dimension))
				{
					for (int i = 1; i <= 20; i++) world.spawnParticle("largesmoke", player.posX - 0.5D + Math.random(), player.posY - 1.5D + Math.random(), player.posZ - 0.5D + Math.random(), 0.0D, 0.0D, 0.0D);
					player.setPositionAndUpdate(posX, posY, posZ);
					for (int i = 1; i <= 300; i++) world.spawnParticle("portal", posX - 0.5D + Math.random(), posY + Math.random(), posZ - 0.5D + Math.random(), 0.0D, 0.0D, 0.0D);
				}
				else player.addChatMessage("You can't teleport to these coordonates! You need to be in the same dimension they were set!");
			}
			else if(JewelryNBT.isJewelX(stack, new ItemStack(Item.enderPearl)) && JewelryNBT.isModifierX(stack, new ItemStack(Item.bed)) && JewelryNBT.dimension(stack) == -2 && JewelryNBT.playerPosX(stack) == -1 && JewelryNBT.playerPosY(stack) == -1 && JewelryNBT.playerPosZ(stack) == -1){
				JewelryNBT.addCoordonatesAndDimension(stack, player.posX, player.posY, player.posZ, world.provider.dimensionId, world.provider.getDimensionName());
				JewelryNBT.addFakeEnchantment(stack);
			}
			else if (JewelryNBT.isJewelX(stack, new ItemStack(Block.obsidian)) && JewelryNBT.isModifierX(stack, new ItemStack(Item.eyeOfEnder)))
			{
				InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
				player.displayGUIChest(inventoryenderchest);
			}
			else if (JewelryNBT.isJewelX(stack, new ItemStack(Item.enderPearl)) && JewelryNBT.isModifierX(stack, new ItemStack(Block.chest))){
				int i = JewelryNBT.blockCoordX(stack), j = JewelryNBT.blockCoordY(stack), k = JewelryNBT.blockCoordZ(stack);
				if (player.getDistance(i + 0.5F, j + 0.5F, k + 0.5F) <= 128 && i != -1 && j != -1 && k != -1){
					int id = world.getBlockId(i, j, k);
					if (id != 0 && Block.blocksList[id] != null && Block.blocksList[id].blockID == Block.chest.blockID){
						TileEntity tile = world.getBlockTileEntity(i, j, k);
						if (tile != null && tile instanceof TileEntityChest) FMLNetworkHandler.openGui(player, JewelrycraftMod.instance, 0, world, i, j, k);
					}
				}
				else if(i != -1 && j != -1 && k != -1) player.addChatMessage("Chest out of range! You need to be " + ((int)player.getDistance(i + 0.5F, j + 0.5F, k + 0.5F) - 127) + " blocks closer.");
				else player.addChatMessage("You need to link the ring with a chest first, before using it!");
			}
			else if (JewelryNBT.isJewelX(stack, new ItemStack(Item.enderPearl)) && JewelryNBT.playerPosX(stack) == -1 && JewelryNBT.playerPosY(stack) == -1 && JewelryNBT.playerPosZ(stack) == -1){
				JewelryNBT.addCoordonatesAndDimension(stack, player.posX, player.posY, player.posZ, world.provider.dimensionId, world.provider.getDimensionName());
				JewelryNBT.addFakeEnchantment(stack);
			}

			if(JewelryNBT.hasTag(stack, "mode"))
			{
				String mode = "";
				if(JewelryNBT.isModeX(stack, "Disenchant")) mode = "Transfer";
				else if(JewelryNBT.isModeX(stack, "Transfer")) mode = "Enchant";
				else if(JewelryNBT.isModeX(stack, "Enchant")) mode = "Disenchant";
				if(mode != "")
				{
					player.addChatMessage("Switched to " + mode + " mode");
					JewelryNBT.addMode(stack, mode);
				}
				if(JewelryNBT.isModeX(stack, "Activated")) mode = "Deactivated";
				else if(JewelryNBT.isModeX(stack, "Deactivated")) mode = "Activated";
				if(mode != "")
				{
					player.addChatMessage("The Ring has been " + mode);
					JewelryNBT.addMode(stack, mode);
				}
			}
			else if(JewelryNBT.hasTag(stack, "modifier") && JewelryNBT.isModifierEffectType(stack)) JewelryNBT.addMode(stack, "Activated");
		}
		return stack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity)
	{
		if (!player.worldObj.isRemote && JewelryNBT.isJewelX(stack, new ItemStack(Item.netherStar)) && JewelryNBT.isModifierX(stack, new ItemStack(Block.chest)) && JewelryNBT.entity(stack, player) == null){
			JewelryNBT.addEntity(stack, entity);
			JewelryNBT.addEntityID(stack, entity);
			entity.setDead();
			JewelryNBT.addFakeEnchantment(stack);
		}
		return true;
	}

	/**
	 * allows items to add custom lines of information to the mouseover
	 * description
	 */
	@Override
	@SuppressWarnings(
			{ "rawtypes", "unchecked" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		if (stack.hasTagCompound() && stack.getDisplayName() != "Wedding Ring")
		{
			ItemStack ingot = JewelryNBT.ingot(stack);
			if (ingot != null) list.add("Ingot: " + EnumChatFormatting.YELLOW + ingot.getDisplayName());

			ItemStack jewel = JewelryNBT.jewel(stack);
			if (jewel != null) list.add("Jewel: " + EnumChatFormatting.BLUE + jewel.getDisplayName());

			ItemStack modifier = JewelryNBT.modifier(stack);
			if(modifier != null) list.add("Modifier: " + EnumChatFormatting.DARK_PURPLE + modifier.getDisplayName());

			double playerPosX = JewelryNBT.playerPosX(stack), playerPosY = JewelryNBT.playerPosY(stack), playerPosZ = JewelryNBT.playerPosZ(stack);
			if(playerPosX != -1 && playerPosY != -1 && playerPosZ != -1) list.add(EnumChatFormatting.YELLOW + "X: " + EnumChatFormatting.GRAY + (int) playerPosX + EnumChatFormatting.YELLOW + " Y: " + EnumChatFormatting.GRAY + (int) playerPosY + EnumChatFormatting.YELLOW + " Z: " + EnumChatFormatting.GRAY + (int) playerPosZ);

			int posX = JewelryNBT.blockCoordX(stack), posY = JewelryNBT.blockCoordY(stack), posZ = JewelryNBT.blockCoordZ(stack);            
			if(posX != -1 && posY != -1 && posZ != -1) list.add(EnumChatFormatting.YELLOW + "X: " + EnumChatFormatting.GRAY + (int) posX + EnumChatFormatting.YELLOW + " Y: " + EnumChatFormatting.GRAY + (int) posY + EnumChatFormatting.YELLOW + " Z: " + EnumChatFormatting.GRAY + (int) posZ);

			String name = JewelryNBT.dimName(stack);
			if(name != null) list.add("Dimension: " + EnumChatFormatting.DARK_GREEN + name);

			EntityLivingBase entity = JewelryNBT.entity(stack, player);
			if (entity != null) list.add("Entity: " + EnumChatFormatting.GOLD + entity.getEntityName());

			String modeN = JewelryNBT.modeName(stack);
			if(modeN != null) list.add("Mode: " + modeN);
		}
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int side, float par8, float par9, float par10)
	{
		if (!world.isRemote)
		{
			EntityLivingBase entity = JewelryNBT.entity(stack, player);
			if(entity != null){
				entity.setLocationAndAngles(i + 0.5D, j + 1D, k + 0.5D, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
				world.spawnEntityInWorld(entity);
				JewelryNBT.removeEntity(stack);
			}
			if (JewelryNBT.isJewelX(stack, new ItemStack(Item.enderPearl)) && JewelryNBT.isModifierX(stack, new ItemStack(Block.chest)) && world.getBlockId(i, j, k) == Block.chest.blockID) JewelryNBT.addBlockCoordonates(stack, i, j, k);
			onItemRightClick(stack, world, player);
		}
		return true;
	}

	public boolean canDisenchant(EntityPlayer player)
	{
		if(player.capabilities.isCreativeMode) return true;
		else if(player.experienceLevel >= 2) return true;
		return false;
	}

	public void dynamicLight(World world, EntityPlayer player)
	{
		world.setBlock((int)player.prevPosX, (int)player.prevPosY, (int)player.prevPosZ, 0);
		world.setBlock((int)player.posX, (int)player.posY, (int)player.posZ, BlockList.glow.blockID);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5)
	{
		amplifier = 0;
		if(cooldown > 0) cooldown--;
		if (!world.isRemote)
		{
			EntityPlayer entityplayer = (EntityPlayer) entity;
			if (JewelryNBT.isJewelX(stack, new ItemStack(Item.diamond))) amplifier = 1;
			else if (JewelryNBT.isJewelX(stack, new ItemStack(Item.emerald))) amplifier = 2;
			else if (JewelryNBT.isJewelX(stack, new ItemStack(Item.netherStar))) amplifier = 7;

			if(JewelryNBT.isModeX(stack, "Activated"))
			{
				if (JewelryNBT.isModifierX(stack, new ItemStack(Item.blazePowder)) && entityplayer != null)
				{
					entityplayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 4, amplifier, true));
					entityplayer.addPotionEffect(new PotionEffect(Potion.weakness.id, 4, amplifier, true));
				}
				else if (JewelryNBT.isModifierX(stack, new ItemStack(Item.sugar)) && entityplayer != null)
				{
					entityplayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 4, amplifier, true));
					entityplayer.addExhaustion(0.05f*amplifier);
				}
				else if (JewelryNBT.isModifierX(stack, new ItemStack(Item.pickaxeIron)) && entityplayer != null)
				{
					entityplayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 4, amplifier, true));
					entityplayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 4, -2*amplifier, true));
				}
				else if (JewelryNBT.isModifierX(stack, new ItemStack(Item.feather)) && entityplayer != null)
				{
					entityplayer.addPotionEffect(new PotionEffect(Potion.jump.id, 4, amplifier, true));
					if(entityplayer.inventory.armorInventory[0] != null)
					{
						int damage = entityplayer.inventory.armorInventory[0].getMaxDamage() - entityplayer.inventory.armorInventory[0].getItemDamage();
						if(damage - entityplayer.fallDistance > 0){
							entityplayer.inventory.armorInventory[0].damageItem((int)entityplayer.fallDistance, entityplayer);
							entityplayer.fallDistance = 0;
						}
						else
						{
							--entityplayer.inventory.armorInventory[0].stackSize;
							entityplayer.fallDistance -= damage;
						}
					}
				}
				else if (JewelryNBT.isModifierX(stack, new ItemStack(Item.potion, 1, 8270)) && entityplayer != null) entityplayer.addPotionEffect(new PotionEffect(Potion.invisibility.id, 4, amplifier, true));
			}
			if(entityplayer.inventory.getCurrentItem() != null && JewelryNBT.isJewelX(stack, new ItemStack(Item.netherStar)) && JewelryNBT.isModifierX(stack, new ItemStack(Item.book)) && entityplayer.inventory.getCurrentItem().equals(stack))
			{
				ItemStack item = null;
				if(entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1) != null && entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1).isItemEnchanted()) item = entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1);
				if(entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1) != null && entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1).isItemEnchanted()) item = entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1);
				if(item != null && JewelryNBT.isModeX(stack, "Disenchant"))
				{
					ItemStack enchBook = new ItemStack(Item.enchantedBook);
					Map enchItem = EnchantmentHelper.getEnchantments(item);
					Map book = EnchantmentHelper.getEnchantments(enchBook);
					Iterator iterator = enchItem.keySet().iterator();
					int e;

					if (iterator.hasNext() && canDisenchant(entityplayer))
					{                
						e = ((Integer)iterator.next()).intValue();
						book.put(Integer.valueOf(e), Integer.valueOf(((Integer)enchItem.get(Integer.valueOf(e))).intValue()));
						EnchantmentHelper.setEnchantments(book, enchBook);
						if(entityplayer.inventory.addItemStackToInventory(enchBook))
						{
							if(!entityplayer.capabilities.isCreativeMode)
							{
								entityplayer.addExperienceLevel(-2);
								entityplayer.heal(-1f);
							}
							enchItem.remove(Integer.valueOf(e));
							if(item.isItemStackDamageable() && (item.getMaxDamage() - item.getItemDamage())/3 > 0) item.damageItem((item.getMaxDamage() - item.getItemDamage())/3, entityplayer);
							EnchantmentHelper.setEnchantments(enchItem, item);
						}
					}
				}
				if(entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1) != null && entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1) != null && JewelryNBT.isModeX(stack, "Transfer"))
				{
					if(cooldown > 0) entityplayer.addChatMessage("Ring is currently cooling down (" + cooldown + ")");
					ItemStack enchantedItem = null, enchantableItem = null;
					if(entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1).isItemEnchanted() && entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1) != null)
					{
						enchantedItem = entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1);
						enchantableItem = entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1);

					}
					if(enchantedItem != null && enchantableItem != null)
					{
						Map enchItem = EnchantmentHelper.getEnchantments(enchantedItem);
						Map resultItem = EnchantmentHelper.getEnchantments(enchantableItem);
						Iterator iterator = enchItem.keySet().iterator();
						int e;

						if (iterator.hasNext() && cooldown == 0)
						{                
							e = ((Integer)iterator.next()).intValue();
							if(!EnchantmentHelper.getEnchantments(enchantableItem).containsKey(Integer.valueOf(e)))
							{
								resultItem.put(Integer.valueOf(e), Integer.valueOf(((Integer)enchItem.get(Integer.valueOf(e))).intValue()));
								EnchantmentHelper.setEnchantments(resultItem, enchantableItem);
								enchItem.remove(Integer.valueOf(e));
								EnchantmentHelper.setEnchantments(enchItem, enchantedItem);
								cooldown = 1000;
							}
						}
					}
				}
				if(entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1) != null) item = entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem + 1);
				else if(entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1) != null) item = entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem - 1);
				if(item != null && !item.isItemEnchanted() && item.isItemEnchantable() && entityplayer.experienceLevel > 0 && JewelryNBT.isModeX(stack, "Enchant"))
				{
					Map enchItem = EnchantmentHelper.getEnchantments(item);
					int level = entityplayer.experienceLevel;
					if(entityplayer.experienceLevel > 6) level = 6;
					if(!entityplayer.capabilities.isCreativeMode) entityplayer.addExperienceLevel(-level);
					enchItem.put(Enchantment.enchantmentsBookList[new Random().nextInt(Enchantment.enchantmentsBookList.length)].effectId, level);
					EnchantmentHelper.setEnchantments(enchItem, item);
				}
			}
		}
	}
	
    public ItemStack getModifiedItemStack(ItemStack ingot, ItemStack modifier, ItemStack jewel)
    {
        ItemStack itemstack = new ItemStack(this);
		JewelryNBT.addMetal(itemstack, ingot);
		JewelryNBT.addModifier(itemstack, modifier);
		JewelryNBT.addJewel(itemstack, jewel);
		if(JewelryNBT.isModifierEffectType(itemstack)) JewelryNBT.addMode(itemstack, "Activated");
		if(JewelryNBT.isJewelX(itemstack, new ItemStack(Item.netherStar)) && JewelryNBT.isModifierX(itemstack, new ItemStack(Item.book))) 
			JewelryNBT.addMode(itemstack, "Disenchant");
        return itemstack;
    }
}
