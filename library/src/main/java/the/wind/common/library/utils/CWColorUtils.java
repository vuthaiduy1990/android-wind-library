package the.wind.common.library.utils;

public final class CWColorUtils {

    // beautiful color
    // Thanks http://everyknightshoulddesign.blogspot.com/2013/08/beautiful-color-palettes-their-hex-codes.html
    public static String[] BEAUTIFUL_COLORS = {
            "#69D2E7", "#A7DBDB", "#F38630", "#FA6900", "#E94C6F", "#FDF200", "#DB3340",
            "#E8B71A", "#1FDA9A", "#28ABE3", "#588C73", "#F2E394", "#F2AE72", "#D96459",
            "#8C4646", "#D0C91F", "#85C4B9", "#008BBA", "#E9514C", "#DC403B", "#00C8F8",
            "#59C4C5", "#FFC33C", "#FBE2B4", "#FF4C65", "#5E412F", "#FCEBB6", "#78C0A8",
            "#F07818", "#F0A830", "#DE4D4E", "#DA4624", "#DE593A", "#E1D041", "#6E9ECF",
            "#75EB00", "#53BBF4", "#FF85CB", "#FF432E", "#FFAC00", "#354458", "#3A9AD9",
            "#29ABA4", "#EB7260", "#4298B5", "#ADC4CC", "#92B06A", "#E19D29", "#DD5F32",
            "#BCCF02", "#5BB12F", "#73C5FF", "#9B539C", "#EB65A0", "#FFA200", "#00A03E",
            "#24A8AC", "#0087CB", "#982395", "#BFF073", "#0DC9F7", "#7F7F7F", "#F05B47",
            "#ED1C24", "#20457C", "#5E3448", "#FB6648", "#E45F56", "#A3D39C", "#7ACCC8",
            "#4AAAA5", "#DC2742", "#AFA577", "#ABA918", "#8BAD39", "#14B8B1", "#F2671F",
            "#C91B26", "#9C0F5F", "#60047A", "#0F5959", "#17A697", "#638CA6", "#8FD4D9",
            "#D93240", "#83AA30", "#1499D3", "#4D6684", "#E74700", "#CCC51C", "#FFE600",
            "#F05A28", "#B9006E", "#F17D80", "#737495", "#68A8AD", "#0B99BC", "#5C2D50",
            "#D40E52", "#CD1719", "#FCE014", "#6C8672", "#293E6A", "#3B5998", "#74AAF7",
            "#77BA9B", "#B6A754", "#B0A472", "#F5DF65", "#2B9464", "#59C8DF", "#D14D28",
            "#F15D58", "#83BF17", "#A68F58", "#753A48", "#954F47", "#C05949", "#9AADBD",
            "#CBBB58", "#1352A2", "#FFD464", "#FB6964", "#D94E67", "#F2D8A7", "#A68572",
            "#28BE9B", "#92DCE0", "#609194", "#EF9950", "#D79C8C", "#FFF568", "#0C98CF",
            "#0AA0D9", "#6C6E70", "#25AAA0", "#66C3BC", "#41D4CF", "#10206B", "#442D65",
            "#775BA3", "#91C5A9", "#F8E1B4", "#F98A5F", "#493621", "#82683B", "#F76835",
            "#B8BE1C", "#AA2159", "#009D97", "#7EC2AA", "#BCC747", "#5DB89D", "#007034",
            "#8C8535", "#FFCA00", "#F26547", "#E8A0B8", "#FFC300", "#BCCF3D", "#02C9C9",
            "#FAC8BF", "#94FFFC", "#466675", "#6A8D9D", "#D75C37", "#67727A", "#6991AC",
            "#FE9601", "#CC0063", "#86269B", "#00D2F1", "#00B796", "#A79E65", "#73AFB6",
            "#5F9DA1"
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
    public static String randomBeautifulColor() {
        // Random beautiful color only
        return BEAUTIFUL_COLORS[CWMathUtils.random(0, BEAUTIFUL_COLORS.length - 1)];
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
