package io.rsimp.jfig;

class Layout {
    final HorizontalLayout horizontal;
    final VerticalLayout vertical; //only in FullLayout, not in OldLayout

    Layout(int fullLayoutMask){
        this.horizontal = new HorizontalLayout(fullLayoutMask);
        this.vertical = new VerticalLayout(fullLayoutMask);
    }

    Layout(int oldLayoutMask, Option<Integer> fullLayoutMask){
        this(mergeMasks(oldLayoutMask, fullLayoutMask));
    }

    private static int mergeMasks(int oldLayoutMask, Option<Integer> fullLayoutMask){
        if (fullLayoutMask.isPresent()) {
            return fullLayoutMask.get();
        } else {
            int convertedLayout = oldLayoutMask;
            if (convertedLayout > 0)
                convertedLayout += 128; //controlled smushing (old layout doesn't have universal)
            else if (convertedLayout == 0)
                convertedLayout = 64; //kerning
            else if (convertedLayout < 0)
                convertedLayout = 0; //full size
            return convertedLayout;
        }
    }

    class HorizontalLayout{
        final LayoutMode mode;

        final boolean applyEqualCharSmushing;
        final boolean applyUnderscoreSmushing;
        final boolean applyHeirarchySmushing;
        final boolean applyOppositePairSmushing;
        final boolean applyBigXSmushing;
        final boolean applyHardBlankSmushing;
        final boolean applyUniversalSmushing;

        private HorizontalLayout(int mask) {
            if ((mask & 128) == 128) {
                this.mode = LayoutMode.SMUSHED;
            } else if ((mask & 64) == 64) {
                this.mode = LayoutMode.FITTED;
            } else {
                this.mode = LayoutMode.FULL_SIZE;
            }

            this.applyEqualCharSmushing = (mask & 1) == 1;
            this.applyUnderscoreSmushing = (mask & 2) == 2;
            this.applyHeirarchySmushing = (mask & 4) == 4;
            this.applyOppositePairSmushing = (mask & 8) == 8;
            this.applyBigXSmushing = (mask & 16) == 16;
            this.applyHardBlankSmushing = (mask & 32) == 32;
            this.applyUniversalSmushing = (mask & (1+2+4+8+16+32)) == 0;
        }
    }

    class VerticalLayout{
        final LayoutMode mode;

        final boolean applyEqualCharSmushing;
        final boolean applyUnderscoreSmushing;
        final boolean applyHeirarchySmushing;
        final boolean applyHorizontalLineSmushing;
        final boolean applyVerticalLineSuperSmushing;
        final boolean applyUniversalSmushing;

        private VerticalLayout(int mask) {
            if ((mask & 16384) == 16384) {
                this.mode = LayoutMode.SMUSHED;
            } else if ((mask & 8192) == 8192) {
                this.mode = LayoutMode.FITTED;
            } else {
                this.mode = LayoutMode.FULL_SIZE;
            }

            this.applyEqualCharSmushing = (mask & 256) == 256;
            this.applyUnderscoreSmushing = (mask & 512) == 512;
            this.applyHeirarchySmushing = (mask & 1024) == 1024;
            this.applyHorizontalLineSmushing = (mask & 2048) == 2048;
            this.applyVerticalLineSuperSmushing = (mask & 4096) == 4096;
            this.applyUniversalSmushing = (mask & (256+512+1024+2048+4096)) == 0;
        }
    }
}