package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectHeal extends AbstractEffect {
    public EffectHeal() {
        super(ModConfig.EffectHealID, "Heal");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            LivingEntity entity = ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity());
            if(entity.removed || entity.getHealth() <= 0)
                return;

            float maxHealth = entity.getMaxHealth();
            float healVal = 3.0f + 3 * getBuffCount(augments, AugmentAmplify.class);
            if(getBuffCount(augments, AugmentExtendTime.class) > 0){
                applyPotionWithCap(entity, Effects.REGENERATION, augments, 5, 5, 5);
            }else{
                entity.setHealth(entity.getHealth() + healVal > maxHealth ? entity.getMaxHealth() : entity.getHealth() + healVal);
            }
        }

    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public int getManaCost() {
        return 40;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.GLISTERING_MELON_SLICE;
    }

    @Override
    public String getBookDescription() {
        return "Heals a small amount of health for the target. When used with Extend Time, the Regeneration buff is applied instead, up to level 5.";
    }
}
