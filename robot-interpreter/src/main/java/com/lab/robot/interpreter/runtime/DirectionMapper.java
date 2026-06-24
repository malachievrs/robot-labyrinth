package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.ast.Direction;

public final class DirectionMapper {

    private DirectionMapper() {
    }

    public static String toProtocolName(Direction direction) {
        return direction.name();
    }
}
