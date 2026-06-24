package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ast.Type;

public final class RipotaiValue extends Value {
    public final int x;
    public final int y;
    public final int z;
    public final boolean obstacle;
    public final boolean exit;

    public RipotaiValue(int x, int y, int z, boolean obstacle) {
        this(x, y, z, obstacle, false);
    }

    public RipotaiValue(int x, int y, int z, boolean obstacle, boolean exit) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.obstacle = obstacle;
        this.exit = exit;
    }

    @Override
    public Type getType() {
        return Type.RIPPTAI;
    }

    @Override
    public RipotaiValue asRipotai() {
        return this;
    }
}
