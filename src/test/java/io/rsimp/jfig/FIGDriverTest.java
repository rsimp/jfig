package io.rsimp.jfig;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FIGDriverTest {
    @Test
    public void convert() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        String banner = driver.convert("Hello World", "");
        String helloWorld = "   __ __    ____       _      __         __   __\n" +
                "  / // /__ / / /__    | | /| / /__  ____/ /__/ /\n" +
                " / _  / -_) / / _ \\   | |/ |/ / _ \\/ __/ / _  / \n" +
                "/_//_/\\__/_/_/\\___/   |__/|__/\\___/_/ /_/\\_,_/  \n" +
                "                                                ";
        assertEquals(banner, helloWorld);
    }

}

