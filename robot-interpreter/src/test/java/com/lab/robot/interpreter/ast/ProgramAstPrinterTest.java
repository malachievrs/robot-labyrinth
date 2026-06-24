package com.lab.robot.interpreter.ast;

import com.lab.robot.interpreter.ParseTestUtil;
import org.junit.Assert;
import org.junit.Test;

public class ProgramAstPrinterTest {

    @Test
    public void printsProgramTree() throws Exception {
        Program program = ParseTestUtil.parse(
                "seisu kansu f(seisu x) kido return x; shushi;\n" +
                "seisu r = f(1);\n"
        );
        String tree = ProgramAstPrinter.format(program);
        Assert.assertTrue(tree.contains("Program"));
        Assert.assertTrue(tree.contains("FunctionDecl name=f"));
        Assert.assertTrue(tree.contains("FunctionCall name=f"));
        Assert.assertTrue(tree.contains("ReturnStatement"));
    }
}
