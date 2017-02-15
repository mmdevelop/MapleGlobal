package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.server.movement.AbsoluteLifeMovement;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MovePlayerHandler extends MovePathHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        slea.readByte();
        Point originPos = new Point(slea.readShort(), slea.readShort());
        final List<LifeMovementFragment> res = parseMovement(slea);
        if (res != null) {
            if (slea.available() != 10) {
                return;
            }
            updatePosition(res, player, 0);
            player.getMap().movePlayer(player, player.getPosition());

            slea.seek(2);


            if (!player.isHidden()) {
                player.getMap().broadcastMessage(player, MaplePacketCreator.movePlayer(player.getId(), slea), false);
            }

            if (CheatingOffense.FAST_MOVE.isEnabled() || CheatingOffense.HIGH_JUMP.isEnabled()) {
                //checkMovementSpeed(player, res);
            }
        }
    }

    private static void checkMovementSpeed(MapleCharacter chr, List<LifeMovementFragment> moves) {
        double playerSpeedMod = chr.getSpeedMod() + 0.005;
        boolean encounteredUnk0 = false;
        for (LifeMovementFragment lmf : moves) {
            if (lmf.getClass() == AbsoluteLifeMovement.class) {
                final AbsoluteLifeMovement alm = (AbsoluteLifeMovement) lmf;
                double speedMod = Math.abs(alm.getPixelsPerSecond().x) / 125.0;
                if (speedMod > playerSpeedMod) {
                    if (alm.getUnk() == 0) { // to prevent FJ messing up
                        encounteredUnk0 = true;
                    }
                    if (!encounteredUnk0) {
                        if (speedMod > playerSpeedMod) {
                            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_MOVE);
                        }
                    }
                }
            }
        }
    }
}