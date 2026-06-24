package com.lab.robot.interpreter.runtime;

import com.lab.robot.interpreter.InterpreterException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class HairetsuValueTest {

    @Test
    public void allocatesAndAccessesThreeDimensions() {
        HairetsuValue cube = HairetsuValue.allocate(List.of(2, 3, 4));
        Assert.assertEquals(List.of(2, 3, 4), cube.dimensions);
        cube.setElement(List.of(1, 2, 3), new SeisuValue(42));
        Assert.assertEquals(42, cube.getElement(List.of(1, 2, 3)).asSeisu().value);
        Assert.assertEquals(0, cube.getElement(List.of(0, 0, 0)).asSeisu().value);
    }

    @Test
    public void flatAndTwoDimensionalStillWork() {
        HairetsuValue flat = HairetsuValue.allocate(List.of(3));
        flat.setElement(List.of(1), new SeisuValue(5));
        Assert.assertEquals(5, flat.flatElements().get(1).asSeisu().value);

        HairetsuValue grid = HairetsuValue.allocate(List.of(2, 2));
        grid.setElement(List.of(1, 0), new SeisuValue(9));
        Assert.assertEquals(9, grid.grid2d().get(1).get(0).asSeisu().value);
    }

    @Test(expected = InterpreterException.class)
    public void rejectsWrongIndexCount() {
        HairetsuValue arr = HairetsuValue.allocate(List.of(2, 2));
        arr.getElement(List.of(0));
    }

    @Test(expected = InterpreterException.class)
    public void rejectsOutOfBounds() {
        HairetsuValue arr = HairetsuValue.allocate(List.of(2));
        arr.getElement(List.of(2));
    }
}
