package tconstruct.blocks.logic;

import net.minecraft.inventory.InventoryBasic;

import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.*;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.inventory.AdvancedDrawbridgeContainer;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.*;
import tconstruct.library.util.*;
import tconstruct.util.player.FakePlayerLogic;

public class AdvancedDrawbridgeLogic extends InventoryLogic implements IFacingLogic, IActiveLogic, IDrawbridgeLogicBase {
	boolean active;
	boolean working;
	int ticks;
	public int selSlot = 0;
	byte extension;
	byte direction;
	byte placementDirection = 4;
	FakePlayerLogic fakePlayer;

	ItemStack[] bufferStacks = new ItemStack[getSizeInventory()];

	public InvCamo camoInventory = new InvCamo();

	public AdvancedDrawbridgeLogic() {
		super(16);
	}
	
	@Override
	public void setWorldObj(World par1World) {
		this.worldObj = par1World;
		fakePlayer = new FakePlayerLogic(worldObj, "Player.Drawbridge", this);
	}

	@Override
	public boolean getActive() {
		return active;
	}

	@Override
	public void setActive(boolean flag) {
		active = flag;
		working = true;
	}

	@Override
	public byte getRenderDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getForgeDirection() {
		return ForgeDirection.VALID_DIRECTIONS[direction];
	}

	@Override
	public void setDirection(int side) {
	}

	public boolean canDropInventorySlot(int slot) {
		return false;
	}

	@Override
	public void setDirection(float yaw, float pitch, EntityLivingBase player) {
		if (pitch > 45) {
			direction = 1;
		} else if (pitch < -45) {
			direction = 0;
		} else {
			int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
			switch (facing) {
			case 0:
				direction = 2;
				break;

			case 1:
				direction = 5;
				break;

			case 2:
				direction = 3;
				break;

			case 3:
				direction = 4;
				break;
			}
		}
	}

	/*
	 * 0 = Up 1 = Right 2 = Down 3 = Left 4 = Center, neutral
	 */
	public void setPlacementDirection(byte keycode) {
		if (keycode == 4) {
			fakePlayer.rotationYaw = 0;
			fakePlayer.rotationPitch = 0;
		} else if (this.direction == 0 || this.direction == 1) {
			switch (keycode) {
			case 0:
				fakePlayer.rotationYaw = 0;
				break;
			case 1:
				fakePlayer.rotationYaw = 90;
				break;
			case 2:
				fakePlayer.rotationYaw = 180;
				break;
			case 3:
				fakePlayer.rotationYaw = 270;
				break;
			}

			if (this.direction == 0)
				fakePlayer.rotationPitch = -90;
			else
				fakePlayer.rotationPitch = 90;
		} else {
			if (keycode == 0) // Forward
			{
				fakePlayer.rotationYaw = mapDirection() * 90;

				if (keycode == 0)
					fakePlayer.rotationPitch = 90;
				else
					fakePlayer.rotationPitch = -90;
			} else if (keycode == 2) // Backward
			{
				int face = mapDirection() + 2;
				if (face > 3)
					face -= 4;
				fakePlayer.rotationYaw = face * 90;

				if (keycode == 0)
					fakePlayer.rotationPitch = 90;
				else
					fakePlayer.rotationPitch = -90;
			} else {
				fakePlayer.rotationPitch = 0;

				int facing = mapDirection();
				if (keycode == 1)
					facing += 1;
				else
					facing -= 1;

				if (facing >= 4)
					facing = 0;
				if (facing < 0)
					facing = 3;

				fakePlayer.rotationYaw = facing * 90;
			}
		}
		placementDirection = keycode;
	}

	int mapDirection() {
		if (this.direction == 2) // North
			return 0;
		if (this.direction == 5) // East
			return 1;
		if (this.direction == 3) // South
			return 2;

		return 3; // West
	}

	public byte getPlacementDirection() {
		return placementDirection;
	}

    @Override
    public ItemStack getStackInSlot (int slot){
        return slot < inventory.length ? inventory[slot] : null;
    }
	
	public ItemStack getStackInBufferSlot(int slot) {
		return slot < bufferStacks.length ? bufferStacks[slot] : null;
	}

	public void setBufferSlotContents(int slot, ItemStack itemstack) {
		if (slot < bufferStacks.length) {
			bufferStacks[slot] = itemstack;
		} else {
			return;
		}
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z) {
		return new AdvancedDrawbridgeContainer(inventoryplayer, this);
	}

	@Override
	protected String getDefaultName() {
		return "tinker.drawbridge";
	}

	@Override
	public ItemStack decrStackSize(int slot, int quantity) {
		ItemStack stack = super.decrStackSize(slot, quantity);
		if (slot == 1)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return stack;
	}

	public void updateEntity() {
		if (working) {
			ticks++;
			if (ticks == 5) {
				ticks = 0;
				if (active) // Placement
				{
					if (getStackInSlot(extension) != null && getStackInSlot(extension).stackSize > 0 && extension < 15) {
						extension++;
						int xPos = xCoord;
						int yPos = yCoord;
						int zPos = zCoord;

						switch (direction) {
						case 0:
							yPos -= extension;
							break;
						case 1:
							yPos += extension;
							break;
						case 2:
							zPos -= extension;
							break;
						case 3:
							zPos += extension;
							break;
						case 4:
							xPos -= extension;
							break;
						case 5:
							xPos += extension;
							break;
						}

						Block block = Block.blocksList[worldObj.getBlockId(xPos, yPos, zPos)];
						if (block == null || block.isAirBlock(worldObj, xPos, yPos, zPos) || block.isBlockReplaceable(worldObj, xPos, yPos, zPos)) {
							// tryExtend(worldObj, xPos, yPos, zPos, direction);
							int blockToItem = getStackInBufferSlot(extension) != null ? TConstructRegistry.blockToItemMapping[getStackInBufferSlot(extension).itemID] : 0;
							if (blockToItem == 0) {
								if (getStackInSlot(extension) == null || getStackInSlot(extension).itemID >= 4096 || Block.blocksList[getStackInSlot(extension).itemID] == null)
									return;
								Block placeBlock = Block.blocksList[getStackInBufferSlot(extension).itemID];
								placeBlockAt(getStackInBufferSlot(extension), fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, getStackInBufferSlot(extension).getItemDamage(), placeBlock);
							} else {
								Block placeBlock = Block.blocksList[blockToItem];
								placeBlockAt(getStackInBufferSlot(extension), fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, getStackInBufferSlot(extension).getItemDamage(), placeBlock);
							}
							worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.out", 0.25F, worldObj.rand.nextFloat() * 0.25F + 0.6F);
							decrStackSize(0, 1);
						} else {
							extension--;
							working = false;
						}
					} else {
						working = false;
					}
				} else
				// Retraction
				{
					if ((getStackInSlot(extension) == null || getStackInSlot(extension).stackSize < getStackInSlot(extension).getMaxStackSize()) && extension > 0) {
						int xPos = xCoord;
						int yPos = yCoord;
						int zPos = zCoord;

						switch (direction) {
						case 0:
							yPos -= extension;
							break;
						case 1:
							yPos += extension;
							break;
						case 2:
							zPos -= extension;
							break;
						case 3:
							zPos += extension;
							break;
						case 4:
							xPos -= extension;
							break;
						case 5:
							xPos += extension;
							break;
						}

						Block block = Block.blocksList[worldObj.getBlockId(xPos, yPos, zPos)];
						if (block != null) {
							int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
							if (getStackInBufferSlot(extension) != null && validBlock(extension, block) && validMetadata(extension, block.blockID, meta)) {
								worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.in", 0.25F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
								if (worldObj.setBlock(xPos, yPos, zPos, 0))
									if (getStackInSlot(extension) == null) {
										setInventorySlotContents(extension, getStackInBufferSlot(extension).copy());
									} else {
										getStackInSlot(extension).stackSize++;
									}
							} else {
								working = false;
							}
						}
						extension--;
					} else {
						working = false;
					}
				}
			}
		}
	}

	/**
	 * Called to actually place the block, after the location is determined and
	 * all permission checks have been made. Copied from ItemBlock
	 * 
	 * @param stack
	 *            The item stack that was used to place the block. This can be
	 *            changed inside the method.
	 * @param player
	 *            The player who is placing the block. Can be null if the block
	 *            is not being placed by a player.
	 * @param side
	 *            The side the player (or machine) right-clicked on.
	 */
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata, Block block) {
		if (!world.setBlock(x, y, z, block.blockID, metadata, 3)) {
			return false;
		}

		if (world.getBlockId(x, y, z) == block.blockID) {
			block.onBlockPlacedBy(world, x, y, z, player, stack);
			block.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}

	boolean validBlock(int slot, Block block) {
		int type = TConstructRegistry.interchangableBlockMapping[block.blockID];
		if (type != 0) {
			if (type == getStackInBufferSlot(slot).itemID)
				return true;
		}
		int blockToItem = TConstructRegistry.blockToItemMapping[block.blockID];
		if (blockToItem != 0) {
			if (blockToItem == getStackInBufferSlot(slot).itemID)
				return true;
		}
		return block.blockID == getStackInBufferSlot(slot).itemID;
	}

	boolean validMetadata(int slot, int blockID, int metadata) {
		int type = TConstructRegistry.drawbridgeState[blockID];
		if (type == 0) {
			return metadata == getStackInBufferSlot(slot).getItemDamage();
		}
		if (type == 1) {
			return true;
		}
		if (type == 2) {
			return false;
		}
		if (type == 3) {
			return true; // TODO: rotational metadata, probably not needed
							// anymore
		}
		if (type == 4) {
			return true;
		}
		if (type == 5) {
			return metadata == getStackInBufferSlot(slot).getItemDamage();
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);
		active = tags.getBoolean("Active");
		working = tags.getBoolean("Working");
		extension = tags.getByte("Extension");

		NBTTagCompound camoTag = (NBTTagCompound) tags.getTag("Camo");
		if (camoTag != null) {
			camoInventory.setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(camoTag));
		}

		readBufferFromNBT(tags);
		readCustomNBT(tags);
	}

	@Override
	public void writeToNBT(NBTTagCompound tags) {
		super.writeToNBT(tags);
		tags.setBoolean("Active", active);
		tags.setBoolean("Working", working);
		tags.setByte("Extension", extension);

		if (camoInventory.getStackInSlot(0) != null) {
			NBTTagCompound camoTag = new NBTTagCompound();
			camoInventory.getStackInSlot(0).writeToNBT(camoTag);
			tags.setTag("Camo", camoTag);
		}

		writeBufferToNBT(tags);
		writeCustomNBT(tags);
	}

	public void readBufferFromNBT(NBTTagCompound tags) {
		NBTTagList nbttaglist = tags.getTagList("Buffer");
		bufferStacks = new ItemStack[getSizeInventory()];
//		bufferStacks.ensureCapacity(nbttaglist.tagCount() > getSizeInventory() ? getSizeInventory() : nbttaglist.tagCount());
		for (int iter = 0; iter < nbttaglist.tagCount(); iter++) {
			NBTTagCompound tagList = (NBTTagCompound) nbttaglist.tagAt(iter);
			byte slotID = tagList.getByte("Slot");
			if (slotID >= 0 && slotID < bufferStacks.length) {
				setBufferSlotContents(slotID, ItemStack.loadItemStackFromNBT(tagList));
			}
		}
	}

	public void writeBufferToNBT(NBTTagCompound tags) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int iter = 0; iter < bufferStacks.length; iter++) {
			if (getStackInBufferSlot(iter) != null) {
				NBTTagCompound tagList = new NBTTagCompound();
				tagList.setByte("Slot", (byte) iter);
				getStackInBufferSlot(iter).writeToNBT(tagList);
				nbttaglist.appendTag(tagList);
			}
		}

		tags.setTag("Buffer", nbttaglist);
	}

	public void readCustomNBT(NBTTagCompound tags) {
		direction = tags.getByte("Direction");
		placementDirection = tags.getByte("Placement");
	}

	public void writeCustomNBT(NBTTagCompound tags) {
		tags.setByte("Direction", direction);
		tags.setByte("Placement", placementDirection);
	}

	/* Packets */
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		readFromNBT(packet.data);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	public boolean hasExtended() {
		return extension != 0;
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null) {
				setBufferSlotContents(i, getStackInSlot(i).copy());
				getStackInBufferSlot(i).stackSize = 1;
			}
		}
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

//	@Override
//	public int getMaxSize() {
//		return 16;
//	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public class InvCamo extends InventoryBasic {

		private InvCamo() {
			super("camoSlot", false, 1);
		}

		public ItemStack getCamoStack() {
			return this.getStackInSlot(0);
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack content) {
			super.setInventorySlotContents(slot, content);
			if (slot == 0)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void onInventoryChanged() {
			super.onInventoryChanged();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
}
