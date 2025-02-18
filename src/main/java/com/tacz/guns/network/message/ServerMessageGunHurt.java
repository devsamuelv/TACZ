package com.tacz.guns.network.message;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ServerMessageGunHurt {
    private final int hurtEntityId;
    private final int attackerId;
    private final ResourceLocation gunId;
    private final float amount;
    private final boolean isHeadShot;

    public ServerMessageGunHurt(int hurtEntityId, int attackerId, ResourceLocation gunId, float amount, boolean isHeadShot) {
        this.hurtEntityId = hurtEntityId;
        this.attackerId = attackerId;
        this.gunId = gunId;
        this.amount = amount;
        this.isHeadShot = isHeadShot;
    }

    public static void encode(ServerMessageGunHurt message, FriendlyByteBuf buf) {
        buf.writeInt(message.hurtEntityId);
        buf.writeInt(message.attackerId);
        buf.writeResourceLocation(message.gunId);
        buf.writeFloat(message.amount);
        buf.writeBoolean(message.isHeadShot);
    }

    public static ServerMessageGunHurt decode(FriendlyByteBuf buf) {
        int hurtEntityId = buf.readInt();
        int attackerId = buf.readInt();
        ResourceLocation gunId = buf.readResourceLocation();
        float amount = buf.readFloat();
        boolean isHeadShot = buf.readBoolean();
        return new ServerMessageGunHurt(hurtEntityId, attackerId, gunId, amount, isHeadShot);
    }

    public static void handle(ServerMessageGunHurt message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> onHurt(message));
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void onHurt(ServerMessageGunHurt message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        @Nullable Entity hurtEntity = level.getEntity(message.hurtEntityId);
        @Nullable LivingEntity attacker = level.getEntity(message.attackerId) instanceof LivingEntity livingEntity ? livingEntity : null;
        MinecraftForge.EVENT_BUS.post(new EntityHurtByGunEvent(hurtEntity, attacker, message.gunId, message.amount, message.isHeadShot, LogicalSide.CLIENT));
    }
}
