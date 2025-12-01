package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ArmorWalkRadiusModule;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Module that places glow blocks at low light levels */
public record GlowWalkerModule(LevelingValue radius, int minLight, int damage) implements ModifierModule, ArmorWalkRadiusModule<Void> {
  public static final RecordLoadable<GlowWalkerModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("radius", GlowWalkerModule::radius),
    IntLoadable.range(0, 15).requiredField("min_light", GlowWalkerModule::minLight),
    IntLoadable.FROM_ZERO.requiredField("tool_damage", GlowWalkerModule::damage),
    GlowWalkerModule::new);

  @Override
  public RecordLoadable<GlowWalkerModule> getLoader() {
    return LOADER;
  }

  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public boolean walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, Void context) {
    if (world.isEmptyBlock(target) && world.getBrightness(LightLayer.BLOCK, target) < minLight) {
      if (TinkerCommons.glow.get().addGlow(world, target, Direction.DOWN)) {
        world.playSound(null, target, world.getBlockState(target).getSoundType(world, target, living).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        ToolDamageUtil.damageAnimated(tool, damage, living, EquipmentSlot.FEET);
        // only run a single success, gives the lighting engine time to update before we place a ton of unneeded glows
        return true;
      }
    }
    return false;
  }
}
