package pdf;


import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.Math.*;

/**
 * Representation of graphics stream object
 */
public class GraphicStreamObject extends StreamObject {
    private static final String MOVE_POINTER = "m";
    private static final String LINE = "l";
    private static final String LINE_WIDTH = "w";
    private static final String RECTANGLE = "re";
    private static final String FILL = "f";
    private static final String BEZIER_CURVE = "c";
    private static final String BORDER_COLOR = "rg";
    private static final String FILL_COLOR = "RG";
    private static final String STROKE = "S";
    private static final String CLOSE_FILL_STROKE = "b";
    private static final String SAVE_STATE = "q";

    private static final float CIRCLE_MAGIC = 0.551915024494f;
    private static final float M_PI = 3.14159265358979323846f;

    //=======================

    private float k = (float) (72 / 25.4); // A4
    private int state = 0;
    private List<String> fonts = new ArrayList<String>();
    private List<String> CoreFonts = new ArrayList<String>(Arrays.asList("courier", "helvetica", "times", "symbol", "zapfdingbats"));
    private boolean InHeader = false;
    private boolean InFooter = false;
    private float lasth = 0;
    private String FontFamily = "";
    private String FontStyle = "";
    private float FontSizePt = 12;
    private float LineWidth = 0.567f / k;
    private float FontSize = FontSizePt;
    private boolean underline = false;
    private String DrawColor = "0 G";
    private String FillColor = "0 g";
    private String TextColor = "0 g";
    private boolean ColorFlag = false;
    private int ws = 0;
    private float x, y;
    private float lMargin;
    private float rMargin;
    // A4 size
    private float w = 595.28f;
    private float h = 841.89f;

    private float PageBreakTrigger = (float) (h - (28.35 * 2) / k);
    private List<String> graphics = new ArrayList<>();
    private String CurrentFont;
    private int cMargin;

    public void addLine(int xFrom, int yFrom, int xTo, int yTo) {
        this.graphics.add(
                " " + xFrom + " " + yFrom + " " + MOVE_POINTER + " " + xTo + " " + yTo + " " + LINE + " " + STROKE);
    }

    public void addRectangle(int x, int y, int width, int high) {
        this.graphics.add(" " + x + " " + y + " " + width + " " + high + " " + RECTANGLE + " " + STROKE);
    }

    public void addFilledRectangle(int x, int y, int width, int high, String color) {
        this.graphics.add("" + color);
        this.graphics.add(" " + x + " " + y + " " + width + " " + high + " " + RECTANGLE + " " + FILL + " " + STROKE);
    }

    public void addRectangle(int x, int y, int width, int high, String border, String fill) {

    }

    public void addBezierCurve(int movex, int movey, int a, int b, int c, int d, int e, int f, String borderColor,
                               int borderWidth, String fillColor) {
        this.graphics.add(borderWidth + " " + LINE_WIDTH);
        this.graphics.add(fillColor + " " + FILL_COLOR);
        this.graphics.add(borderColor + " " + BORDER_COLOR);
        this.graphics.add(movex + " " + movey + " " + MOVE_POINTER);
        this.graphics.add(a + " " + b + " " + c + " " + d + " " + e + " " + f + " " + BEZIER_CURVE);
        this.graphics.add(" " + CLOSE_FILL_STROKE);
    }

    private void addBezierCurve(float movex, float movey, float a, float b, float c, float d, float e, float f) {
        this.graphics.add(movex + " " + movey + " " + MOVE_POINTER);
        this.graphics.add(a + " " + b + " " + c + " " + d + " " + e + " " + f + " " + BEZIER_CURVE);
        this.graphics.add(" " + CLOSE_FILL_STROKE);
    }

    private void drawBezierOvalQuarter(int centerX, int centerY, int sizeX, int sizeY) {
        this.addBezierCurve(centerX - (sizeX), centerY - (0),
                centerX - (sizeX), centerY - (CIRCLE_MAGIC * sizeY),
                centerX - (CIRCLE_MAGIC * sizeX), centerY - (sizeY),
                centerX - (0), centerY - (sizeY));

    }

    private void drawBezierOval(int centerX, int centerY, int sizeX, int sizeY) {
        drawBezierOvalQuarter(centerX, centerY, -sizeX, sizeY);
        drawBezierOvalQuarter(centerX, centerY, sizeX, sizeY);
        drawBezierOvalQuarter(centerX, centerY, sizeX, -sizeY);
        drawBezierOvalQuarter(centerX, centerY, -sizeX, -sizeY);
    }

    public void addCircle(int centerX, int centerY, int size) {
        drawBezierOval(centerX, centerY, size, size);
    }

    public void addCircle2(int centerX, int centerY, int r) {
        this.graphics.add(" q");
        this.graphics.add(" " + centerX + " " + (centerY + r) + " m");
        this.graphics.add(" " + (centerX + r * CIRCLE_MAGIC) + " " + (centerY + r) + " " + (centerX + r) + " " + (centerY + r * CIRCLE_MAGIC) + " " + (centerX + r) + " " + (centerY) + " c");
        this.graphics.add(" " + (centerX + r) + " " + (centerY + r * CIRCLE_MAGIC * -1f) + " " + (centerX + r * CIRCLE_MAGIC) + " " + (centerY - r) + " " + (centerX) + " " + (centerY - r) + " c");
        this.graphics.add(" " + (centerX + r * CIRCLE_MAGIC * -1) + " " + (centerY - r) + " " + (centerX - r) + " " + (centerY + r * CIRCLE_MAGIC * -1) + " " + (centerX - r) + " " + (centerY) + " c");
        this.graphics.add(" " + (centerX + r * -1) + " " + (centerY + r * CIRCLE_MAGIC) + " " + (centerX - r * CIRCLE_MAGIC) + " " + (centerY + r) + " " + (centerX) + " " + (centerY + r) + " c");
        this.graphics.add(" W");
        this.graphics.add(" s");
        this.graphics.add(" Q");
    }

    public void addStar(int x, int y, int d) {
        this.graphics.add(String.format("%d %d m", x + d, y));
        for (int i = 1; i < 5; ++i) {
            int xto = (int) (x + d * Math.cos(0.8f * i * M_PI));
            int yto = (int) (y + d * Math.sin(0.8f * i * M_PI));
            this.graphics.add(String.format(" %d %d l", xto, yto));
        }
        this.graphics.add(" W\ns");
    }

    private void Arc(double x1, double y1, double x2, double y2, double x3, double y3) {
        out("%.2f %.2f %.2f %.2f %.2f %.2f c",
                x1 * this.k,
                (this.h - y1) * this.k,
                x2 * this.k,
                (this.h - y2) * this.k,
                x3 * this.k,
                (this.h - y3) * this.k);
    }

    protected void out(String format, Object... args) {
        String s = new Formatter().format(format, args).toString();
        this.graphics.add(s);
    }

    public void SetFont(String family) {
        this.SetFont(family, "", 0);
    }

    public void SetFont(String family, String style, float size) {
        // Select a font; size given in points
        if (family == null || family.isEmpty())
            family = FontFamily;
        else
            family = family.toLowerCase();
        style = style.toUpperCase();
        if (style.indexOf("U") >= 0) {
            underline = true;
            style = style.replace("U", "");
        } else
            underline = false;
        if (style == "IB")
            style = "BI";
        if (size == 0)
            size = FontSizePt;
        // Test if font is already selected
        if (FontFamily == family && Objects.equals(FontStyle, style) && FontSizePt == size)
            return;
        // Test if font is already loaded
        String fontkey = family + style;
        if (this.fonts.contains(fontkey)) {
            // Test if one of the core fonts
            if (family == "arial")
                family = "helvetica";
            if (CoreFonts.contains(family)) {
                if (family == "symbol" || family == "zapfdingbats")
                    style = "";
                fontkey = family + style;
                if (this.fonts.contains(fontkey))
                    ;// todo AddFont(family,style);
            } else
                throw new RuntimeException("Undefined font: " + family + " " + style);
        }
        // Select it
        FontFamily = family;
        FontStyle = style;
        FontSizePt = size;
        FontSize = size / this.k;
        CurrentFont = fontkey;
        out("BT /F%d %.2f Tf ET", 1,// CurrentFont["i"],
                FontSizePt);
    }


    public void SetLineWidth(float width) {
        LineWidth = width;
        out("%.2f w", width * k);
    }

    public void SetDrawColor(int r) {
        DrawColor = String.format("%.3f G", r * 1.0 / 255);
        out(DrawColor);
    }

    public void SetDrawColor(int r, int g, int b) {
        DrawColor = String.format("%.3f %.3f %.3f RG", r * 1.0 / 255, g * 1.0 / 255, b * 1.0 / 255);
        out(DrawColor);
    }

    public void Rect(float x, float y, float w, float h) {
        this.Rect(x, y, w, h, "");
    }

    public void Rect(float x, float y, float w, float h, String style) {
        String op = "S";
        if ("F".equals(style))
            op = "f";
        else if ("FD".equals(style) || "DF".equals(style))
            op = "B";

        out("%.2f %.2f %.2f %.2f re %s", x * this.k, this.h - y * this.k, w * this.k, -h * this.k, op);
    }

    public void Line(float x1, float y1, float x2, float y2) {
        out("%.2f %.2f m %.2f %.2f l S", x1 * k, h - y1 * k, x2 * k, h - y2 * k);
    }

    public void SetX(float xpos) {
        // Set x position
        if (xpos >= 0)
            this.x = xpos;
        else
            this.x = w + xpos;
    }

    public void SetY(float ypos) {
        this.SetY(ypos, true);
    }

    public void SetY(float ypos, boolean resetX) {
        if (ypos >= 0)
            this.y = ypos;
        else
            this.y = h + ypos;
        if (resetX)
            this.x = lMargin;
    }

    public void SetXY(float xpos, float ypos) {
        SetX(xpos);
        SetY(ypos, false);
    }

    public void SetFillColor(int r) {
        FillColor = String.format("%.3f g", r * 1.0 / 255);
        out(FillColor);
    }

    public void SetFillColor(int r, int g, int b) {
        FillColor = String.format("%.3f %.3f %.3f rg", r * 1.0 / 255, g * 1.0 / 255, b * 1.0 / 255);
        ColorFlag = FillColor != TextColor;
        out(FillColor);
    }

    protected String _escape(String s) {
        // Escape special characters
        if (s.indexOf('(') >= 0 || s.indexOf(')') >= 0 || s.indexOf('\\') >= 0 || s.indexOf('\r') >= 0) {
            s = s.replace("\\", "\\\\");
            s = s.replace("(", "\\(");
            s = s.replace(")", "\\)");
            s = s.replace("\r", "\\r");
        }
        return s;
    }

    public float GetStringWidth(String s) {
        w = s.length() * 600;
        return w * this.FontSize / 1000.0f;
    }


    public void Cell(float w, float h, String txt, String border, int ln, String align, boolean fill, String link) {
        // Output a cell
        if (y + h > PageBreakTrigger && !this.InHeader && !this.InFooter) {
            // Automatic page break
            x = this.x;
            ws = this.ws;
            if (ws > 0) {
                this.ws = 0;
                this.out("0 Tw");
            }
            this.x = x;
            if (ws > 0) {
                this.ws = ws;
                this.out("%.3F Tw", ws * k);
            }
        }
        if (w == 0)
            w = (int) (this.w - this.rMargin - this.x);
        StringBuilder s = new StringBuilder();
        if (fill || border.equals("1")) {
            String op = "S";
            if (fill)
                op = border.equals("1") ? "B" : "f";
            s.append(String.format("%.2f %.2f %.2f %.2f re %s ", this.x * k, (this.h - this.y) * k, w * k, -h * k, op));
        }

        x = this.x;
        y = this.y;
        if (border.contains("L")) {
            s.append(String.format("%.2f %.2f m %.2f %.2f l S ", x * k, (this.h - y) * k, x * k, (this.h - (y + h)) * k));
        }
        if (border.contains("T"))
            s.append(String.format("%.2f %.2f m %.2f %.2f l S ", x * k, (this.h - y) * k, (x + w) * k, (this.h - y) * k));
        if (border.contains("R"))
            s.append(String.format("%.2f %.2f m %.2f %.2f l S ", (x + w) * k, (this.h - y) * k, (x + w) * k, (this.h - (y + h)) * k));
        if (border.contains("B"))
            s.append(String.format("%.2f %.2f m %.2f %.2f l S ", x * k, (this.h - (y + h)) * k, (x + w) * k, (this.h - (y + h)) * k));

        if (txt != null && !txt.isEmpty()) {
            if (this.CurrentFont == null || this.CurrentFont.isEmpty())
                throw new RuntimeException("No font has been set");
            float dx = this.cMargin;
            if ("R".equals(align)) {
                dx = w - this.cMargin - GetStringWidth(txt);
            } else if ("C".equals(align)) {
                dx = (w - GetStringWidth(txt)) / 2;
            }
            if (this.ColorFlag)
                s.append("q " + this.TextColor + " ");
            s.append(String.format("BT %.2f %.2f Td (%s) Tj ET", (this.x + dx) * k,
                    (this.h - (this.y + .5 * h + .3 * this.FontSize) * k), this._escape(txt)));

            if (this.ColorFlag)
                s.append(" Q");
        }
        if (s.length() > 0)
            this.out(s.toString());
        this.lasth = h;
        if (ln > 0) {
            // Go to next line
            this.y += h;
            if (ln == 1)
                this.x = this.lMargin;
        } else
            this.x += w;
    }

    public void Sector2(float xc, float yc, float r, float startAngle, float endAngle, String txt, boolean cw, int o) {

        float d0 = startAngle - endAngle;
        if (cw) {
            float d = endAngle;
            endAngle = o - startAngle;
            startAngle = o - d;
        } else {
            endAngle += o;
            startAngle += o;
        }
        while (startAngle < 0)
            startAngle += 360;
        while (startAngle > 360)
            startAngle -= 360;
        while (endAngle < 0)
            endAngle += 360;
        while (endAngle > 360)
            endAngle -= 360;
        startAngle = startAngle / 360 * 2 * M_PI;
       //put the first point
        float firstx = (float) ((xc + r * Math.cos(startAngle)) * this.k);
        float firsty = (float) (this.h - (yc - r * Math.sin(startAngle)) * k);

        this.out("q 0 g BT %.2f %.2f Td (%s) Tj ET Q", firstx, firsty, txt);
    }

    public void Sector(float xc, float yc, float r, float startAngle, float endAngle, String style, boolean cw, int o) {
        float d0 = startAngle - endAngle;
        if (cw) {
            float d = endAngle;
            endAngle = o - startAngle;
            startAngle = o - d;
        } else {
            endAngle += o;
            startAngle += o;
        }
        while (startAngle < 0)
            startAngle += 360;
        while (startAngle > 360)
            startAngle -= 360;
        while (endAngle < 0)
            endAngle += 360;
        while (endAngle > 360)
            endAngle -= 360;
        if (startAngle > endAngle)
            endAngle += 360;
        endAngle = endAngle / 360 * 2 * M_PI;
        startAngle = startAngle / 360 * 2 * M_PI;
        float d = endAngle - startAngle;
        if (d == 0 && d0 != 0)
            d = 2 * M_PI;
        float hp = this.h;
        float MyArc = 0;
        if (Math.sin(d / 2.0) != 0)
            MyArc = (float) (4.0f / 3.0f * (1.0 - Math.cos(d / 2.0f)) / Math.sin(d / 2.0f) * r);

        //first put the center
        this.out("%.2f %.2f m", (xc) * this.k, hp - yc * this.k);
        //put the first point
        float firstx = (float) ((xc + r * Math.cos(startAngle)) * this.k);
        float firsty = (float) (hp - (yc - r * Math.sin(startAngle)) * k);
        this.out("%.2f %.2f l", firstx, firsty);
        //draw the arc
        if (d < M_PI / 2) {
            this.arc(xc + r * Math.cos(startAngle) + MyArc * Math.cos(M_PI / 2 + startAngle),
                    yc - r * Math.sin(startAngle) - MyArc * Math.sin(M_PI / 2 + startAngle),
                    xc + r * Math.cos(endAngle) + MyArc * Math.cos(endAngle - M_PI / 2),
                    yc - r * Math.sin(endAngle) - MyArc * Math.sin(endAngle - M_PI / 2),
                    xc + r * Math.cos(endAngle),
                    yc - r * Math.sin(endAngle)
            );
        } else {
            endAngle = startAngle + d / 4.0f;
            MyArc = (float) (4.0f / 3.0f * (1.0f - Math.cos(d / 8.0f)) / Math.sin(d / 8.0f) * r);
            this.arc(xc + r * Math.cos(startAngle) + MyArc * Math.cos(M_PI / 2.0f + startAngle),
                    yc - r * Math.sin(startAngle) - MyArc * Math.sin(M_PI / 2f + startAngle),
                    xc + r * Math.cos(endAngle) + MyArc * Math.cos(endAngle - M_PI / 2f),
                    yc - r * Math.sin(endAngle) - MyArc * Math.sin(endAngle - M_PI / 2f),
                    xc + r * Math.cos(endAngle),
                    yc - r * Math.sin(endAngle)
            );
            startAngle = endAngle;
            endAngle = startAngle + d / 4f;
            this.arc(xc + r * Math.cos(startAngle) + MyArc * Math.cos(M_PI / 2f + startAngle),
                    yc - r * Math.sin(startAngle) - MyArc * Math.sin(M_PI / 2f + startAngle),
                    xc + r * Math.cos(endAngle) + MyArc * Math.cos(endAngle - M_PI / 2f),
                    yc - r * Math.sin(endAngle) - MyArc * Math.sin(endAngle - M_PI / 2f),
                    xc + r * Math.cos(endAngle),
                    yc - r * Math.sin(endAngle)
            );
            startAngle = endAngle;
            endAngle = startAngle + d / 4f;
            this.arc(xc + r * Math.cos(startAngle) + MyArc * Math.cos(M_PI / 2f + startAngle),
                    yc - r * Math.sin(startAngle) - MyArc * Math.sin(M_PI / 2f + startAngle),
                    xc + r * Math.cos(endAngle) + MyArc * Math.cos(endAngle - M_PI / 2f),
                    yc - r * Math.sin(endAngle) - MyArc * Math.sin(endAngle - M_PI / 2f),
                    xc + r * Math.cos(endAngle),
                    yc - r * Math.sin(endAngle)
            );
            startAngle = endAngle;
            endAngle = startAngle + d / 4f;
            this.arc(xc + r * Math.cos(startAngle) + MyArc * Math.cos(M_PI / 2f + startAngle),
                    yc - r * Math.sin(startAngle) - MyArc * Math.sin(M_PI / 2f + startAngle),
                    xc + r * Math.cos(endAngle) + MyArc * Math.cos(endAngle - M_PI / 2f),
                    yc - r * Math.sin(endAngle) - MyArc * Math.sin(endAngle - M_PI / 2f),
                    xc + r * Math.cos(endAngle),
                    yc - r * Math.sin(endAngle)
            );
        }
        //terminate drawing
        String op = "s";
        if ("F".equals(style))
            op = "f";
        else if ("FD".equals(style) || "DF".equals(style))
            op = "b";
        this.out(op);
    }

    private void arc(double x1, double y1, double x2, double y2, double x3, double y3) {
        out("%.2f %.2f %.2f %.2f %.2f %.2f c",
                x1 * this.k,
                this.h - y1 * this.k,
                x2 * this.k,
                this.h - y2 * this.k,
                x3 * this.k,
                this.h - y3 * this.k);
    }


    @Override
    public String buildStream() {
        return graphics.stream().collect(Collectors.joining("\n")) + "\n";
    }


}
