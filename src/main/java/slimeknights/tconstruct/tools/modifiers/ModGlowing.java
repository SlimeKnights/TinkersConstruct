package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.shared.TinkerCommons;

public class ModGlowing extends ModifierTrait {

  public ModGlowing() {
    super("glowing", 0xffffaa);
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    if(isSelected && !world.isRemote && !ToolHelper.isBroken(tool)) {
      BlockPos pos = entity.getPosition();
      // check light level at entity
      if(world.getLight(pos) < 8) {
        for(BlockPos candidate : new BlockPos[]{pos, pos.down(), pos.up(), pos.north(), pos.east(), pos.south(), pos.west()}) {
          // try placing a glow block. Values are defined as vertical first
          for(EnumFacing dir : EnumFacing.values()) {
            if(TinkerCommons.blockGlow.addGlow(world, candidate, dir)) {
              EntityLivingBase entityLiving = null;
              if(entity instanceof EntityLivingBase) {
                entityLiving = (EntityLivingBase) entity;
              }
              ToolHelper.damageTool(tool, 1, entityLiving);
              return;
            }
          }
        }
      }
    }
  }
}
