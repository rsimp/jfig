package io.rsimp.jfig;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class FIGDriverTest {
    static String testMessage = "The quick brown fox jumps over the lazy dog";
    
    @Test
    public void convertToSmslant() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_parsed.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantUniversalSmushing() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setUniversalSmushing();
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_overlapping.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantKerning() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setKerningMode();
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_kerning.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantFullWidth() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setFullWidthMode();
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_full_width.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantCustomLayoutFullSmushRules() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setOldLayout(63);
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_full_smush.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantCustomLayoutKerning() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setOldLayout(0);
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_kerning.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantCustomLayoutFullWidth() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setOldLayout(-1);
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_full_width.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantCustomLayoutUniversalSmushing() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setLayout(128);
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_overlapping.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }
    
    //TODO Doctor smslant.flf to use old layout and print right to left
    // - make fixture and test for reverse banner
    
    //TODO make tests that covers more smushing rule logic
    
    //TODO test code tag characters
    // - have basis test for a bunch of them
    // - doctor smslant.flf to have same code tags use octal and hex
    //   - find a font file that uses octal or hex as an example
}

