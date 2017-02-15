package net.sf.odinms.server.movement;

import java.awt.Point;

public abstract class AbstractLifeMovement implements LifeMovement {
    private Point position;
    private int foothold;
    private int newstate;
    private int type;

    public AbstractLifeMovement(int type, Point position, int foothold, int newstate) {
        super();
        this.type = type;
        this.position = position;
        this.foothold = foothold;
        this.newstate = newstate;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getFoothold() {
        return foothold;
    }

    @Override
    public int getNewstate() {
        return newstate;
    }

    @Override
    public Point getPosition() {
        return position;
    }
}