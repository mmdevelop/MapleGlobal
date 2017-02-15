package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.life.AbstractLoadedMapleLife;
import net.sf.odinms.server.maps.AnimatedMapleMapObject;
import net.sf.odinms.server.movement.AbsoluteLifeMovement;
import net.sf.odinms.server.movement.ChangeEquipSpecialAwesome;
import net.sf.odinms.server.movement.LifeMovement;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.server.movement.RelativeLifeMovement;
import net.sf.odinms.server.movement.TeleportMovement;
import net.sf.odinms.tools.data.input.LittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 *
 * @Author - Ginseng
 */
public abstract class MovePathHandler extends AbstractMaplePacketHandler {

    //private static Logger log = LoggerFactory.getLogger(AbstractMovementPacketHandler.class);
    protected List<LifeMovementFragment> parseMovement(LittleEndianAccessor lea) {
        List<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
        
        
        int numCommands = lea.readByte();
        for (int i = 0; i < numCommands; i++) {
            int command = lea.readByte();
            switch (command) {
                case 0: // Normal move
                case 5: 
                {
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short xwobble = lea.readShort();
                    short ywobble = lea.readShort();
                    short foothold = lea.readShort();
                    byte newstate = lea.readByte();
                    AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), foothold, newstate);
                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    lea.skip(2);
                    res.add(alm);
//                    int xpos = lea.readShort();
//                    int ypos = lea.readShort();
//                    int xwobble = lea.readShort();
//                    int ywobble = lea.readShort();
//                    int unk = lea.readShort();
//                    int newstate = lea.readByte();
//                    int duration = lea.readShort();
//                    AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate);
//                    alm.setUnk(unk);
//                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));
//                    res.add(alm);
                    break;
                }
                case 1:
                case 2:
                case 6: // FJ
                {
                    int xmod = lea.readShort();
                    int ymod = lea.readShort();
                    int newstate = lea.readByte();
                    int foothold = lea.readShort();
                    RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), foothold, newstate);
                    res.add(rlm);
                    break;
                }
                case 3:
                case 4: // Teleport
                case 7: // Assaulter
                case 9: // Rush
                {
                    int xpos = lea.readShort();
                    int ypos = lea.readShort();
                    int xwobble = lea.readShort();
                    int ywobble = lea.readShort();
                    int newstate = lea.readByte();
                    TeleportMovement tm = new TeleportMovement(command, new Point(xpos, ypos), newstate);
                    tm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    res.add(tm);
                    break;
                }
                case 8: // Change equip
                {
                    res.add(new ChangeEquipSpecialAwesome(lea.readByte()));
                    break;
                }
                default: {
                    return null;
                }
            }
        }
        if (numCommands != res.size()) {
            //log.warn("numCommands ({}) does not match the number of deserialized movement commands ({})", numCommands, res.size());
        }
        return res;
    }

    protected void updatePosition(List<LifeMovementFragment> movement, AnimatedMapleMapObject target, int yoffset) {
        for (LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    Point position = ((LifeMovement) move).getPosition();
                    //position.y += yoffset;
                    target.setPosition(position);
                    if (target instanceof AbstractLoadedMapleLife)
                    {
                        ((AbstractLoadedMapleLife)target).setFh(((AbsoluteLifeMovement) move).getFoothold());
                    }
                }
                target.setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}
