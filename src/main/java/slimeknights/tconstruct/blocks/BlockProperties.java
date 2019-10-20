package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public final class BlockProperties {

  static Block.Properties FIREWOOD = Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).harvestLevel(-1).hardnessAndResistance(2.0F, 7.0F).sound(SoundType.WOOD).lightValue(7);
  static Block.Properties LAVAWOOD = FIREWOOD;

  static Block.Properties MUD_BRICKS = Block.Properties.create(Material.EARTH).harvestTool(ToolType.SHOVEL).harvestLevel(-1).hardnessAndResistance(2.0F).sound(SoundType.GROUND);
  static Block.Properties DRIED_CLAY = Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(-1).hardnessAndResistance(1.5F, 20.0F).sound(SoundType.STONE);
  static Block.Properties DRIED_CLAY_BRICKS = DRIED_CLAY;
}
