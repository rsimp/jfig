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
    public void convertToSmslantLoadAsFile() throws Exception {
        FIGDriver driver = new FIGDriver(new File(getClass().getResource("/smslant.flf").toURI()));
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_parsed.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToSmslantReverse() throws Exception {
        FIGDriver driver = new FIGDriver(new File(getClass().getResource("/smslant.flf").toURI()));
        driver.setReverseMode();
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_reverse.txt").getPath());
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
    public void convertToSmslantCustomLayoutFullSmushRules2() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant.flf"));
        driver.setOldLayout(63);
        String banner = driver.convert("  >< \"% #\"", "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("extra_smush_rules.txt").getPath());
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

    @Test
    public void convertToCustomFontWithOldLayout() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant_old_layout.flf"));
        String banner = driver.convert(testMessage, "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_old_layout.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }

    @Test
    public void convertToCustomFontWithOctalAndHexCustomTagsCodes() throws Exception {
        FIGDriver driver = new FIGDriver(getClass().getResource("/smslant_custom_codetags.flf"));
        String banner = driver.convert("¡¢£¤", "");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("smslant_codetags.txt").getPath());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertEquals(contents, banner);
    }
}

