package slimeknights.tconstruct.library.modifiers.modules.armor;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.MutableUseOnContext;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.Objects;

/**
 * Boot module that transforms walked on blocks using a tool action
 * @param action     Transforming action
 * @param sound      Sound to play when transforming
 * @param radius     Radius to cover
 * @param condition  Standard module condition
 */
public record ToolActionWalkerTransformModule(ToolAction action, SoundEvent sound, LevelingValue radius, ModifierModuleCondition condition) implements ModifierModule, ArmorWalkRadiusModule<MutableUseOnContext>, ToolActionModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.BOOT_WALK, TinkerHooks.TOOL_ACTION);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return condition.matches(tool, modifier) && toolAction == this.action;
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (condition.matches(tool, modifier)) {
      ArmorWalkRadiusModule.super.onWalk(tool, modifier, living, prevPos, newPos);
    }
  }

  @Override
  public MutableUseOnContext getContext(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    return new MutableUseOnContext(living.getLevel(), living instanceof Player p ? p : null, InteractionHand.MAIN_HAND, living.getItemBySlot(EquipmentSlot.FEET), Util.createTraceResult(newPos, Direction.UP, false));
  }

  @Override
  public void walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, MutableUseOnContext context) {
    Material material = world.getBlockState(target).getMaterial();
    if (material.isReplaceable() || material == Material.PLANT) {
      mutable.set(target.getX(), target.getY() - 1, target.getZ());
      context.setOffsetPos(mutable);
      // transform the block
      BlockState original = world.getBlockState(mutable);
      BlockState transformed = original.getToolModifiedState(context, action, false);
      if (transformed != null) {
        world.setBlock(mutable, transformed, Block.UPDATE_ALL_IMMEDIATE);
        world.destroyBlock(target, true);
        world.playSound(null, mutable, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
      }
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ToolActionWalkerTransformModule> LOADER = new IGenericLoader<>() {
    @Override
    public ToolActionWalkerTransformModule deserialize(JsonObject json) {
      return new ToolActionWalkerTransformModule(
        ToolAction.get(GsonHelper.getAsString(json, "tool_action")),
        JsonHelper.getAsEntry(ForgeRegistries.SOUND_EVENTS, json, "sound"),
        LevelingValue.deserialize(GsonHelper.getAsJsonObject(json, "radius")),
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(ToolActionWalkerTransformModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.addProperty("tool_action", object.action.name());
      json.addProperty("sound", Objects.requireNonNull(object.sound.getRegistryName()).toString());
      json.add("radius", object.radius.serialize(new JsonObject()));
    }

    @Override
    public ToolActionWalkerTransformModule fromNetwork(FriendlyByteBuf buffer) {
      return new ToolActionWalkerTransformModule(
        ToolAction.get(buffer.readUtf(Short.MAX_VALUE)),
        buffer.readRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS),
        LevelingValue.fromNetwork(buffer),
        ModifierModuleCondition.fromNetwork(buffer)
      );
    }

    @Override
    public void toNetwork(ToolActionWalkerTransformModule object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.action.name());
      buffer.writeRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS, object.sound);
      object.radius.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };
  

  /* Builder */

  /** Creates a builder instance */
  public static Builder builder(ToolAction action, SoundEvent sound) {
    return new Builder(action, sound);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModifierModuleCondition.Builder<Builder> implements LevelingValue.Builder<ToolActionWalkerTransformModule> {
    private final ToolAction action;
    private final SoundEvent sound;

    @Override
    public ToolActionWalkerTransformModule amount(float flat, float eachLevel) {
      return new ToolActionWalkerTransformModule(action, sound, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
