package app;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;

public class TestClass {

    @Test
    public void test() throws IOException {
        File file = new File(TestClass.class.getClassLoader().getResource("test.txt").getFile());
        try(BufferedReader buffIn = new BufferedReader(new FileReader(file))){
            System.out.println(buffIn.read());
            System.out.println(buffIn.read());
            System.out.println(buffIn.read());
            System.out.println(buffIn.read());
            System.out.println(buffIn.read());
        }



    }
}
