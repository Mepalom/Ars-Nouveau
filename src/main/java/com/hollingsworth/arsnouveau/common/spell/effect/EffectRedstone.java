package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.RedstoneAir;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectRedstone extends AbstractEffect {
    public EffectRedstone() {
        super(ModConfig.EffectRedstoneID, "Redstone Signal");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockState state = BlockRegistry.REDSTONE_AIR.getDefaultState();
            int signalModifier = getAmplificationBonus(augments) + 10;
            if(signalModifier < 1)
                signalModifier = 1;
            if(signalModifier > 15)
                signalModifier = 15;
            state = state.with(RedstoneAir.POWER, signalModifier);
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos().offset(((BlockRayTraceResult) rayTraceResult).getFace());
            if(!(world.getBlockState(pos).getMaterial() == Material.AIR && world.getBlockState(pos).getBlock() != BlockRegistry.REDSTONE_AIR)){
                return;
            }
            int timeBonus = getBuffCount(augments, AugmentExtendTime.class);
            world.setBlockState(pos, state);
            world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 5 + timeBonus * 10);

            BlockPos hitPos = pos.offset(((BlockRayTraceResult) rayTraceResult).getFace().getOpposite());

            BlockUtil.safelyUpdateState(world, pos);
            world.notifyNeighborsOfStateChange(pos, state.getBlock());
            world.notifyNeighborsOfStateChange(hitPos, state.getBlock());
        }
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.REDSTONE_BLOCK;
    }

    @Override
    public String getBookDescription() {
        return "Creates a brief redstone signal on a block, like a button. The signal starts at strength 10, and may be increased with Amplify, or decreased with Dampen. The duration may be extended with Extend Time.";
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
