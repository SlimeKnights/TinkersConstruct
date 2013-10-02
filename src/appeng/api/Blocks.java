package appeng.api;

import net.minecraft.item.ItemStack;

/**
 * Blocks, all of the Blocks in AE.
 * DO NOT USE THESE WITHOUT COPYING THEM : ItemStack.copy() - is your friend.
 */

public class Blocks
{
	// World Gen
    public static ItemStack blkQuartzOre;
    public static ItemStack blkQuartz;
    
    // Tech 1 ( non-ME )
    public static ItemStack blkGrinder;
    
    // Tech 4? ( ME Storage )
    public static ItemStack blkCable_Colored[];
    
    public static ItemStack blkAssembler;
    public static ItemStack blkController;
    public static ItemStack blkDrive;
    public static ItemStack blkPatternEncoder;
    public static ItemStack blkWireless;
    public static ItemStack blkTerminal;
    public static ItemStack blkChest;
    public static ItemStack blkInterface;
    public static ItemStack blkPartitioner;
	public static ItemStack blkCraftingTerminal;
	public static ItemStack blkStorageBus;
	
	public static ItemStack blkAssemblerFieldWall;
	public static ItemStack blkHeatVent;
	public static ItemStack blkCraftingAccelerator;
	
    public static ItemStack blkInputCablePrecision;
    public static ItemStack blkInputCableFuzzy;
    public static ItemStack blkInputCableBasic;
    
    public static ItemStack blkOutputCablePrecision;
	public static ItemStack blkOutputCableFuzzy;
	public static ItemStack blkOutputCableBasic;
	public static ItemStack blkStorageBusFuzzy;
	
	public static ItemStack blkLevelEmitter;
	public static ItemStack blkDarkCable;
	public static ItemStack blkIOPort;
	public static ItemStack blkCraftingMonitor;
	public static ItemStack blkStorageMonitor;
	public static ItemStack blkColorlessCable;
	public static ItemStack blkColorlessCableCovered;

	public static ItemStack blkTransitionPlane;
	public static ItemStack blkCondenser;
	public static ItemStack blkEnergyCell;
	public static ItemStack blkPowerRelay;
	public static ItemStack blkQuantumRing;
	public static ItemStack blkQuantumLink;
	public static ItemStack blkQuartzGlass;
	public static ItemStack blkQuartzLamp;
    
    // Used internally, best not to mess with this one...
    public static ItemStack blkPhantom;

}
