package the.wind.library.utils;

public final class CWColorUtils {

    // beautiful color
    // Thanks http://everyknightshoulddesign.blogspot.com/2013/08/beautiful-color-palettes-their-hex-codes.html
    public static final String[] POKE_COLORS = {
            "#e3d4d3", "#5d9f69", "#c990a5", "#4976a0", "#b25542", "#90b0a3", "#9da9a7", "#495663", "#b09c82", "#f0e0d7",
            "#a19f5a", "#c7ab92", "#e4bb97", "#a67e5f", "#c39889", "#aec2d3", "#dddbcf", "#c7c5c3", "#ac6c94", "#d5e1de",
            "#8a8586", "#ac869c", "#85c476", "#66758c", "#b18eaa", "#bbb4a7", "#a49485", "#9cbfd6", "#97c03b", "#ccda85",
            "#bd4934", "#216f91", "#98acbc", "#2d87c2", "#83a3b7", "#ece4c4", "#d6c68c", "#ecdedb", "#4a6caa", "#9dbea5",
            "#d36754", "#7a5c94", "#879285", "#647483", "#ddb7c9", "#9284bd", "#afc3c7", "#549e7a", "#d45270", "#969daa",
            "#d6a17b", "#f4e483", "#e5d947", "#9cadb4", "#f3db8b", "#df88a0", "#56795d", "#cdc7c7", "#d8e2da", "#8397ba",
            "#a4cadc", "#8ca2b4", "#8b9657", "#74a6ac", "#e8eaef", "#d0d8cc", "#cc5a4d", "#b0a9b5", "#d4e0cb", "#ae7e4a",
            "#b99084", "#bad097", "#bfd7c8", "#ccebd3", "#f2eae4", "#76b075", "#496686", "#a49fb5", "#b08a87", "#c9b55a",
            "#cdb6b5", "#8bb7cc", "#919889", "#4f6d86", "#a2dce1", "#b58d81", "#e1ded7", "#538b97", "#51a4a5", "#d9d6d9",
            "#d6c5c5", "#dbdbd4", "#869e62", "#af5345", "#c6ac8c", "#e2e3e2", "#ae8497", "#64acc4", "#cbcacb", "#3f6990",
            "#c2a5a7", "#d2856b", "#b6aebb", "#647378", "#a1d562", "#afc673", "#9cdcdc", "#d7e2e8", "#71b457", "#99c4d0",
            "#e1dbe4", "#919faa", "#dce8d8", "#c1a8b2", "#93c39c", "#d483a1", "#7299be", "#bfcabb", "#6eb2cb", "#b3bda6",
            "#78aacf", "#d2ead5", "#d2e0de", "#c8acc0", "#baa392", "#cba96f", "#d1c7a7", "#d5b5c3", "#e2c671", "#bea392",
            "#b5a9be", "#e0dcbb", "#a2bdc8", "#f8e9b3", "#b6bfc0", "#a98877", "#63733a", "#787b75", "#ebd5d3", "#c3bcb9",
            "#dbe7e4", "#badcd9", "#dcc3a7", "#d9e0dd", "#84c185", "#adbeb1", "#d9c9a5", "#dd8794", "#d86d74", "#967f97",
            "#b08659", "#e6653d", "#f1f3e1", "#d0c2c8", "#c1e1f5", "#cdb9c5", "#bcd4db", "#877484", "#bcc78e", "#838156",
            "#94b4b9", "#74a4c4", "#cbb2d1", "#f9d477", "#8bad68", "#a44c6c", "#34acd4", "#e9d582", "#ce744c", "#e5dce0",
            "#b2b585", "#a3a1a9", "#d6775b", "#b9a8a3", "#d3dad7", "#bcb6c6", "#e95241", "#d4c5aa", "#ede8de", "#d3c7d3",
            "#4393aa", "#cdcabe", "#d7dedc", "#6b9dab", "#d4c166", "#548570", "#2c8bb5", "#b8887e", "#c2aca5", "#f2e5da",
            "#d4a6a7", "#a78a8b", "#a5b4c4", "#bac5cb", "#5c9f87", "#98d6e2", "#c8d9e6", "#96cecf", "#889cb0", "#e3c6d4",
            "#9b8fb4", "#d4bd9c", "#b49250", "#8e7d54", "#ae8c57", "#7a707c", "#e6e184", "#bad3e0", "#cd9894", "#c3a6b2",
            "#afb2bd", "#a45251", "#987b6d", "#7bbedf", "#b0a2b6", "#9e6069", "#deded7", "#e0d7da", "#ad5168", "#a2bac6",
            "#4f706c", "#5cbc7c", "#d8dfe2", "#d0bb71", "#2e9751", "#a5adab", "#4f717b", "#a27f74", "#a06474", "#ecc6b5",
            "#d9c9b4", "#82bf52", "#dec054", "#ba95ae", "#e4dccb", "#b7dce9", "#d4c7c2", "#abb6c2", "#cbd0bf", "#7a9ab3",
            "#d5dcd9", "#227ac1", "#587dac", "#efdcb2", "#bb455e", "#b7c7c2", "#bcdc74", "#d7e0e0", "#caa493", "#c9e7b5",
            "#dedfe3", "#937953", "#cbc5d4", "#e6c58d", "#a98e85", "#d6ad8b", "#ca4b4b", "#95aaba", "#5c84bc", "#a59c57",
            "#7f6d91", "#a9acbb", "#bedc82", "#d5dde6", "#dfd9de", "#9e90a8", "#df7f5a", "#99d4c9", "#bccc75", "#dcd6e3",
            "#79b8af", "#bbc8a8", "#efdcb0", "#c8bcac", "#e66e82", "#51627c", "#eeaf69", "#b3d4a3", "#b65164", "#57678c",
            "#948f8c", "#a29bb9", "#dfe1ea", "#cde48f", "#738e54", "#bba380", "#df839f", "#c59579", "#e5a56c", "#8d9751",
            "#e0dee2", "#7c7d7c", "#4d6b86", "#c9733b", "#ddd7e0", "#56bc7c", "#65aad0", "#746e7c", "#c49450", "#d3d3e4",
            "#819353", "#cebacf", "#d76272", "#87c5e8", "#717481", "#a395c2", "#d7daa4", "#c8deea", "#f39454", "#abaa56",
            "#d1a5af", "#f3eabc", "#accc71", "#c3b7a7", "#546f8f", "#c49059", "#cd9e39", "#f4e197", "#dbe1e7", "#aeb8b9",
            "#99a1b1", "#ebd8c8", "#5a8cba", "#e68544", "#72ae9f", "#3985a4", "#d5e4d2", "#559155", "#d8dbdf", "#829042",
            "#64849c", "#61ae58", "#576885", "#bb666a", "#acd6e5", "#d0bb8b", "#a39082", "#d5d8da", "#e9d47d", "#e7885d",
            "#9aaba0", "#ccc3b0", "#ccbcdc", "#a49491", "#aa6c50", "#c5c1c1", "#88b7d9", "#87878b", "#d9bab1", "#647c94",
            "#a5af84", "#b1bcca", "#bec8db", "#5c808d", "#d7dadb", "#707c59", "#9dbccb", "#cba4a3", "#c2b69a", "#566784",
            "#bcccdc", "#e7bb54", "#c3d9da", "#8578b3", "#517c90", "#b394b4", "#dcdeda", "#a4b898", "#bcd4c0", "#ac9eaf",
            "#eadae2", "#b25528", "#f5d85a", "#eee9ed", "#c8beb5", "#d3563c", "#8eba35", "#e19ab2", "#d0e2e1", "#e1c15d",
            "#e5dfe6", "#bbd6db", "#a5cf9a", "#caa7cc", "#f3f3b3", "#8b8f95", "#a6d6e7", "#ad9e8c", "#d3dcdd", "#88c65c",
            "#ed9f42", "#aa5f6f", "#89a9b7", "#b3aa8d", "#5c5474", "#b79660", "#a19792", "#bac2b9", "#4c7470", "#957755",
            "#d3cf99", "#e5ded3", "#ae784a", "#968493", "#c3a5ae", "#e7ca86", "#6a959d", "#afc579", "#bcbcdc", "#74afb0",
            "#e3dedd", "#c29237", "#5b93c8", "#d89b96", "#917788", "#baaccc", "#d0d8db", "#e0a6be", "#71aa78", "#dbd0bc",
            "#f2f294", "#b7d6cf", "#9f6e62", "#7a9e9f", "#8a70a0", "#74b4b3", "#e7e7e5", "#d2594e", "#a5b0b1", "#60b949",
            "#eddee0", "#bcdcdf", "#bf8d9a", "#d7dfe6", "#b294bf", "#8f715b", "#c1a27f", "#d6856c", "#e18b6b", "#e1dfd4",
            "#d1ebf6", "#cadfb3", "#d3d5da", "#e4d34e", "#d66a56", "#b380b2", "#e5754b", "#d1c7ca", "#c5cdd2", "#a7ce86",
            "#ce8a4c", "#90959f", "#b782a0", "#bbc7c0", "#bcd2e0", "#8299ae", "#79b7b8", "#cb5949", "#71b7b2", "#b6d5a7",
            "#dfdbd5", "#877e56", "#b76f34", "#b8b9bb", "#97959b", "#acaaaf", "#3f596b", "#549cb4", "#e9c967", "#c2bba7",
            "#a8a88a", "#cbe29c", "#929395", "#dfdce7", "#75a8d3", "#d7daab", "#cba55e", "#ded651", "#b3a7b2", "#52acfb",
            "#ecc1a8", "#81a384", "#ac5981", "#c2d6bb", "#d9e7e4", "#9d708c", "#a0c6d9", "#75c0e3", "#edd37a", "#7fa7b1",
            "#acb0ca", "#ad8ea7", "#b9d2da", "#77a97c", "#818d93", "#4f6d72", "#a77f5d", "#747170", "#98c561", "#e3d8e1",
            "#b3723d", "#326491", "#4490a7", "#d78b49", "#95ba6c", "#b1bebf", "#64abc1", "#729872", "#778ea7", "#63747d",
            "#4e6b90", "#aedad3", "#71a246", "#cbe1ac", "#f2da98", "#eac959", "#4b7da6", "#e89262", "#e1d6ca", "#df6582",
            "#cfe0ea", "#746e86", "#a6896e", "#d1bece", "#54b4dc", "#7b8bae", "#a05562", "#8c9cd0", "#567188", "#abc3e1",
            "#c5ab97", "#96ccd0", "#a9b7bd", "#3e6b94", "#6182ba", "#948f86", "#7aa0b7", "#8b8795", "#f2d9a7", "#997c66",
            "#e0d4d5", "#b4be8a", "#a1b6b6", "#c9a849", "#afbdb2", "#6eb1d7", "#e1e9c9", "#dbf3eb", "#7ab5c8", "#b58b79",
            "#e3dfae", "#bbc8bc", "#819cc5", "#b2a396", "#ea8363", "#a5b5c0", "#a79f95", "#e9827b", "#c25258", "#80b6a9",
            "#979fa5", "#d1c2be", "#b44e56", "#b7aea5", "#516987", "#5c99c3", "#d66e57", "#d6bb9c", "#ba9a85", "#b4aba6",
            "#845b57", "#958359", "#697b6f", "#d5dad3", "#2e74a5", "#beb2a8", "#f9dd6e", "#b75a84", "#79aad6", "#bcd8d8",
            "#a37e55", "#74a5c4", "#bdb97d", "#8d7273", "#de8e59", "#93af4b", "#9ca772", "#b3bb96", "#99bfa1", "#d6d7d8",
            "#5fa754", "#a2514b", "#f3b383", "#a1b881", "#71b09c", "#dadadc", "#e3e9d8", "#bfb1a2", "#a7b492", "#f2d0a2",
            "#e9ebd2", "#ecefe6", "#c4be9c", "#9b8f4e", "#abb965", "#767778", "#d2d7dd", "#70b8c7", "#e18a62", "#eff5f6",
            "#c6e0e7", "#ae7bb0", "#b6d4d4", "#9b6049", "#e19d70", "#acaba6", "#b3c6d3", "#9c759f", "#456079", "#b0ac90",
            "#baa4a5", "#70a45d", "#bdba8c", "#839971", "#eae49a", "#ea9c63", "#eacfd9", "#a6c4cc", "#cd98ad", "#cec4a6",
            "#b87e65", "#8fbb7d", "#aaacbe", "#456276", "#d0dde4", "#44657f", "#9cd2dd", "#cfdf73", "#c7dfcb", "#a397b2",
            "#6e8163", "#b0899a", "#bea6b3", "#b37d84", "#c0a296", "#9c8172", "#b894aa", "#adb4c0", "#afa68f", "#c2a63a",
            "#7f94a8", "#868ca5", "#a38467", "#5e758d", "#acd0d6", "#c6b3a4", "#b5a24d", "#e4b2ba", "#ae9989", "#e57b72",
            "#80bcda", "#a382a1", "#aeba9e", "#edefee", "#8c9daf", "#c0914c", "#d2785b", "#d9e2c0", "#ece0d6", "#d7d5d8",
            "#9cc4a5", "#64acbc", "#c5bec8", "#96bacf", "#6c94a4", "#d4d0d0", "#9ca8ac", "#547d93", "#94748e", "#e8e9e4",
            "#d29eb3", "#829ba7", "#c0ba8d", "#367a93", "#71776d", "#62b258", "#bcb0b3", "#977760", "#94a4cc", "#cde6e9",
            "#b2aa9e", "#79a7b8", "#639cbf", "#cc9881", "#e2d2ac", "#779c9f", "#e5908e", "#38719c", "#8b7094", "#8390a1",
            "#5e6276", "#89c389", "#ceb5bc", "#df8777", "#c2b5a8", "#94d0c8", "#7192b3", "#caccdd", "#a5c9a0", "#ccd2d5",
            "#cdcebe", "#d47282", "#8d95c3", "#e2beaf", "#e5e4e1", "#9fafc1", "#4c718f", "#c6a278", "#d69974", "#9fbe8c",
            "#dcbb5b", "#97c03f", "#cbc9c5", "#e5d2c1", "#b2a6b3", "#93b67c", "#3689bc", "#eec09d", "#b9ab82", "#84ccdc",
            "#dfbda9", "#73c5cc", "#7d7fa8", "#979b8d", "#846583", "#cae1ee", "#75b46a", "#ac858a", "#bbb6b5", "#5990b0",
            "#6d857b", "#85c853", "#d5d1d4", "#73962e", "#99d478", "#d7cab8", "#95b1a7", "#85a7ae", "#938265", "#ccba6e",
            "#73c6da", "#d0dbdb", "#e2a24a", "#f4d974", "#e1e886", "#678283", "#dca9b5", "#bdb58b", "#8891a2", "#94a4aa",
            "#e5ddd9", "#d5dd8f", "#c4b5d4", "#436ca4", "#e0a888", "#875b74", "#c7ccb3"
    };

    /**
     * Generate a random hex color
     *
     * @return hex color
     */
    public static String randomColor() {
        String letters = "0123456789ABCDEF";
        StringBuilder color = new StringBuilder("#");
        for (int i = 0; i < 6; i++) {
            color.append(letters.charAt((int) Math.floor(Math.random() * 16)));
        }
        return color.toString();
    }

    /**
     * Generate a random beautiful color
     *
     * @return hex color
     */
    public static String randomPokeColor() {
        // Random beautiful color only
        return POKE_COLORS[CWMathUtils.random(0, POKE_COLORS.length - 1)];
    }

    /**
     * Convert RGB to HEX string
     * https://stackoverflow.com/questions/5623838/rgb-to-hex-and-hex-to-rgb
     *
     * @param r red
     * @param g green
     * @param b blue
     * @return hex string
     */
    public static String rgbToHex(int r, int g, int b) {
        return "#" + Integer.toString((1 << 24) + (r << 16) + (g << 8) + b, 16).substring(1);
    }
}
