
package me.heldplayer.util.HeldCore.sync.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import me.heldplayer.util.HeldCore.event.SyncEvent;
import me.heldplayer.util.HeldCore.packet.HeldCorePacket;
import me.heldplayer.util.HeldCore.sync.ISyncableObjectOwner;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;

public class Packet1TrackingStatus extends HeldCorePacket {

    public boolean isWordly;
    public String identifier;
    public int posX;
    public int posY;
    public int posZ;
    public boolean track;

    public Packet1TrackingStatus() {
        super(null);
    }

    public Packet1TrackingStatus(ISyncableObjectOwner object, boolean track) {
        super(object != null ? object.getWorld() : null);

        this.track = track;
        if (object.isWorldBound()) {
            this.isWordly = true;

            this.posX = object.getPosX();
            this.posY = object.getPosY();
            this.posZ = object.getPosZ();
        }
        else {
            this.isWordly = false;

            this.identifier = object.getIdentifier();
        }
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
    }

    @Override
    public void read(ChannelHandlerContext context, ByteBuf in) throws IOException {
        this.isWordly = in.readBoolean();

        if (this.isWordly) {
            this.posX = in.readInt();
            this.posY = in.readInt();
            this.posZ = in.readInt();
        }
        else {
            byte[] data = new byte[in.readInt()];
            in.readBytes(data);
            this.identifier = new String(data);
        }

        this.track = in.readBoolean();
    }

    @Override
    public void write(ChannelHandlerContext context, ByteBuf out) throws IOException {
        out.writeBoolean(this.isWordly);

        if (this.isWordly) {
            out.writeInt(this.posX);
            out.writeInt(this.posY);
            out.writeInt(this.posZ);
        }
        else {
            byte[] data = this.identifier.getBytes();
            out.writeInt(data.length);
            out.writeBytes(data);
        }

        out.writeBoolean(this.track);
    }

    @Override
    public void onData(ChannelHandlerContext context, EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }

        if (this.isWordly) {
            TileEntity tile = player.worldObj.getTileEntity(this.posX, this.posY, this.posZ);
            if (tile != null) {
                if (tile instanceof ISyncableObjectOwner) {
                    if (this.track) {
                        SyncHandler.startTracking((ISyncableObjectOwner) tile, (EntityPlayerMP) player);
                    }
                    else {
                        SyncHandler.stopTracking((ISyncableObjectOwner) tile, (EntityPlayerMP) player);
                    }
                }
            }
        }
        else {
            SyncEvent.RequestObject event = new SyncEvent.RequestObject(this.identifier);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.result != null) {
                if (this.track) {
                    SyncHandler.startTracking(event.result, (EntityPlayerMP) player);
                }
                else {
                    SyncHandler.stopTracking(event.result, (EntityPlayerMP) player);
                }
            }
        }
    }

}
