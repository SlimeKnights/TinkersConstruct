package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Set;

public class AxeTool extends HarvestTool {
  public static final AOEToolHarvestLogic HARVEST_LOGIC = new HarvestLogic(1, 1, 1);
  public AxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return HARVEST_LOGIC;
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return this.getToolHarvestLogic().transformBlocks(context, ToolType.AXE, SoundEvents.ITEM_AXE_STRIP, false);
  }

  @Override
  public boolean dealDamage(ToolStack tool, LivingEntity player, Entity entity, float damage, boolean isCriticalHit, boolean fullyCharged) {
    boolean hit = super.dealDamage(tool, player, entity, damage, isCriticalHit, fullyCharged);
    if (hit && fullyCharged) {
      ToolAttackUtil.spawnAttachParticle(TinkerTools.axeAttackParticle.get(), player, 0.8d);
    }
    return hit;
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return true;
  }

  /** Logic extension to include leaves and plants */
  public static class HarvestLogic extends AOEToolHarvestLogic {
    private static final Set<Material> EXTRA_MATERIALS = Sets.newHashSet(Material.PLANTS, Material.TALL_PLANTS);
    public HarvestLogic(int width, int height, int depth) {
      super(width, height, depth);
    }

    @Override
    public boolean isEffectiveAgainst(ToolStack tool, ItemStack stack, BlockState state) {
      return EXTRA_MATERIALS.contains(state.getMaterial()) || super.isEffectiveAgainst(tool, stack, state);
    }
  }
}
