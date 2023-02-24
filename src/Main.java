
import pdf.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Create PDF file without any library from scratch.
 * <p>
 * Include multiple pages, text and graphics
 *
 * @author itsallbinary
 */
public class Main {

    public static void main(String[] args) throws IOException {

        /*
         * Create text stream with few lines
         */
        TextStreamObject textStreamObject = new TextStreamObject("F1", 18, 30, 100, "Hello World");

        /*
         * First page with above text stream
         */
        PageObject page1 = new PageObject();
        page1.addAttribute("Resources", new FontObject("F1", "Naxe"));
        page1.addContent(textStreamObject);
        page1.addAttribute("MediaBox", "[0 0 300 200]");

        /*
         * Create graphic stream with few graphics.
         */
        GraphicStreamObject graphicStreamObject = new GraphicStreamObject();
        graphicStreamObject.addLine(100, 100, 400, 500);
        graphicStreamObject.addRectangle(2, 3, 4, 5);

        /*
         * Second page with above graphics
         */
        PageObject page2 = new PageObject();
        page2.addContent(graphicStreamObject);

        /*
         * Create graphic stream circle and star.
         */
        GraphicStreamObject graphicStreamObject1 = new GraphicStreamObject();
        graphicStreamObject1.addCircle2(400, 600, 30);
        graphicStreamObject1.addStar(200, 350, 100);

        /*
         * Second page with above graphics
         */
        PageObject page3 = new PageObject();
        page3.addContent(graphicStreamObject1);

        /*
         * Prepare pages & catalog objects.
         */
        PageCollectionObject pageCollectionObject = new PageCollectionObject();
        pageCollectionObject.addPages(drawLineChart(), drawBarChart(), drawPieChart());
//		pageCollectionObject.addPages(page1, page2, page3);
        CatalogObject catalogObject = new CatalogObject(pageCollectionObject);

        /*
         * Build final PDF.
         */
        PDF pdf = new PDF(catalogObject);
        /*
         * Write PDF to a file.
         */
        FileWriter fileWriter = new FileWriter("C://Users//Ray//Desktop//Test5!.pdf");
        fileWriter.write(pdf.build());
        fileWriter.close();

        System.out.println("success");
    }

    private static final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(Arrays.asList(
            new HashMap<String, Object>() {
                {
                    put("name", "dat1");
                    put("color", new ArrayList<>(Arrays.asList(255, 0, 0)));
                    put("value", 100);
                }
            },
            new HashMap<String, Object>() {
                {
                    put("name", "dat2");
                    put("color", new ArrayList<>(Arrays.asList(255, 222, 0)));
                    put("value", 300);
                }
            },
            new HashMap<String, Object>() {
                {
                    put("name", "dat3");
                    put("color", new ArrayList<>(Arrays.asList(255, 0, 222)));
                    put("value", 150);
                }
            },
            new HashMap<String, Object>() {
                {
                    put("name", "dat4");
                    put("color", new ArrayList<>(Arrays.asList(255, 0, 111)));
                    put("value", 50);
                }
            },
            new HashMap<String, Object>() {
                {
                    put("name", "dat5");
                    put("color", new ArrayList<>(Arrays.asList(255, 111, 111)));
                    put("value", 240);
                }
            }));

    public static PageObject drawBarChart() {

        PageObject page = new PageObject();
        page.addAttribute("Resources", new FontObject("F1", "Naxe"));
        page.addAttribute("MediaBox", "[0 0 595.28 841.89]");

        /*
         * Create graphic stream with few graphics.
         */
        GraphicStreamObject graphicStreamObject = new GraphicStreamObject();

        int chartX = 10;
        int chartY = 60;

        //dimension
        int chartWidth = 150;
        int chartHeight = 100;

        //padding
        int chartTopPadding = 10;
        int chartLeftPadding = 20;
        int chartBottomPadding = 20;
        int chartRightPadding = 5;

        //chart box
        int chartBoxX = chartX + chartLeftPadding;
        int chartBoxY = chartY + chartTopPadding;
        int chartBoxWidth = chartWidth - chartLeftPadding - chartRightPadding;
        int chartBoxHeight = chartHeight - chartBottomPadding - chartTopPadding;

        //bar width
        int barWidth = 20;

        //chart data

        //dataMax
        int dataMax = 300;

        //data step
        int dataStep = 50;

        //set font, line width and color
        graphicStreamObject.SetFont("Arial", "B", 16);
        graphicStreamObject.SetLineWidth(0.2f);
        graphicStreamObject.SetDrawColor(0);

        //chart boundary
        graphicStreamObject.Rect(chartX, chartY, chartWidth, chartHeight);

        //vertical axis line
        graphicStreamObject.Line(
                chartBoxX,
                chartBoxY,
                chartBoxX,
                (chartBoxY + chartBoxHeight)
        );
        //horizontal axis line
        graphicStreamObject.Line(
                chartBoxX - 2,
                (chartBoxY + chartBoxHeight),
                chartBoxX + (chartBoxWidth),
                (chartBoxY + chartBoxHeight)
        );

        ///vertical axis
        //calculate chart"s y axis scale unit
        float yAxisUnits = chartBoxHeight * 1.0f / dataMax;

        //draw the vertical (y) axis labels
        for (int i = 0; i <= dataMax; i += dataStep) {
            //y position
            float yAxisPos = chartBoxY + (yAxisUnits * i);
            //draw y axis line
            graphicStreamObject.Line(
                    chartBoxX - 2,
                    yAxisPos,
                    chartBoxX,
                    yAxisPos
            );
            //set cell position for y axis labels
            graphicStreamObject.SetXY(chartBoxX - chartLeftPadding, yAxisPos - 2);
            //Cell(chartLeftPadding-4 , 5 , dataMax-i , 1);---------------
            graphicStreamObject.Cell(chartLeftPadding - 4, 5, (dataMax - i) + "", 0 + "", 0, "R", false, "");
        }

///horizontal axis
//set cells position
        graphicStreamObject.SetXY(chartBoxX, chartBoxY + chartBoxHeight);

//cell"s width
        float xLabelWidth = chartBoxWidth / data.size();

//Cell(xLabelWidth , 5 , itemName , 1 , 0 , "C");-------------
//loop horizontal axis and draw the bar
        float barXPos = 0;
        for (Map<String, Object> map : data) {
            String key = (String) map.get("name");
            List<Integer> color = (List<Integer>) map.get("color");
            Integer value = (Integer) map.get("value");
            //print the label
            //Cell(xLabelWidth , 5 , itemName , 1 , 0 , "C");--------------
            graphicStreamObject.Cell(xLabelWidth, 5, key, String.valueOf(0), 0, "C", false, "");

            ///drawing the bar
            //bar color
            graphicStreamObject.SetFillColor(color.get(0), color.get(1), color.get(2));
            //bar height
            float barHeight = yAxisUnits * value;
            //bar x position
            float barX = (xLabelWidth / 2) + (xLabelWidth * barXPos);
            barX = barX - (barWidth / 2);
            barX = barX + chartBoxX;
            //bar y position
            float barY = chartBoxHeight - barHeight;
            barY = barY + chartBoxY;
            //draw the bar
            graphicStreamObject.Rect(barX, barY, barWidth, barHeight, "DF");
            //increase x position (next series)
            barXPos++;
        }

        barXPos = 0;
        for (Map<String, Object> map : data) {
            Integer value = (Integer) map.get("value");
            //bar height
            float barHeight = yAxisUnits * value;
            //bar x position
            float barX = (xLabelWidth / 2) + (xLabelWidth * barXPos);
            barX = barX - (barWidth / 2) +5;
            barX = barX + chartBoxX;
            //bar y position
            float barY = chartBoxHeight - barHeight;
            barY = barY + chartBoxY -8;
            graphicStreamObject.SetXY(barX, barY);
            graphicStreamObject.Cell(800, 10, value+"", String.valueOf(0), 0, "", false, "");

            //increase x position (next series)
            barXPos++;
        }

        //axis labels
        graphicStreamObject.SetFont("Arial", "B", 12);
        graphicStreamObject.SetXY(chartX, chartY);
        graphicStreamObject.Cell(100, 10, "Amount", String.valueOf(0), 0, "", false, "");
        graphicStreamObject.SetXY((chartWidth / 2) - 50 + chartX, chartY + chartHeight - (chartBottomPadding / 2));
        graphicStreamObject.Cell(100, 5, "Series", String.valueOf(0), 0, "C", false, "");

        graphicStreamObject.SetFont("Arial", "B", 18);
        graphicStreamObject.SetXY(chartX+20, chartY-10);
        graphicStreamObject.Cell(800, 10, "World Population growth", String.valueOf(0), 0, "", false, "");

        page.addContent(graphicStreamObject);
        return page;
    }

    public static PageObject drawPieChart() {
        PageObject page = new PageObject();
        page.addAttribute("Resources", new FontObject("F1", "Naxe"));
        page.addAttribute("MediaBox", "[0 0 595.28 841.89]");

        GraphicStreamObject graphicStreamObject = new GraphicStreamObject();

//pie and legend properties
        int pieX = 95;
        int pieY = 60;
        int r = 50;//radius
        int legendX = 150;
        int legendY = 70;

//get total data summary
        int dataSum = 0;
        for (Map<String, Object> map : data) {
            dataSum += (int) map.get("value");
        }

//get scale unit for each degree
        float degUnit = 360.0f / dataSum;

//variable to store current angle
        float currentAngle = 0f;
//store current legend Y position
        float currentLegendY = legendY;

        graphicStreamObject.SetFont("Arial", "", 9);

//simplify the code by drawing both pie and legend in one loop
        for (Map<String, Object> map : data) {
            List<Integer> color = (List<Integer>) map.get("color");
            Integer value = (Integer) map.get("value");
            //draw the pie
            //slice size
            float deg = degUnit * value;
            //set color
            graphicStreamObject.SetFillColor(color.get(0), color.get(1), color.get(2));
            //remove border
            graphicStreamObject.SetDrawColor(color.get(0), color.get(1), color.get(2));
            //draw the slice
            graphicStreamObject.Sector(pieX, pieY, r, currentAngle, currentAngle + deg, "FD", true, 90);
            //add slice angle to currentAngle var
            currentAngle += deg;

            //draw the legend
            graphicStreamObject.Rect(legendX, currentLegendY, 5, 5, "DF");
            graphicStreamObject.SetXY(legendX + 6, currentLegendY);
            graphicStreamObject.Cell(50, 5, (String) map.get("name")+"("+value+")", 0 + "", 0, "", false, "");
            currentLegendY += 5;
        }

        graphicStreamObject.SetFont("Arial", "", 9);
        currentAngle = 0f;
        for (Map<String, Object> map : data) {
            Integer value = (Integer) map.get("value");
            //draw the pie
            //slice size
            float deg = degUnit * value;
            //draw the slice
            graphicStreamObject.Sector2(pieX, pieY, r-10, currentAngle+deg/2, currentAngle + deg/2, value+"", true,90);
            //add slice angle to currentAngle var
            currentAngle += deg;
        }

        graphicStreamObject.SetFont("Arial", "B", 18);
        graphicStreamObject.SetXY(pieX-r+20, pieY-r-10);
        graphicStreamObject.Cell(800, 10, "World Population by countries", String.valueOf(0), 0, "", false, "");

        page.addContent(graphicStreamObject);
        return page;
    }

    public static PageObject drawLineChart() {
        PageObject page = new PageObject();
        page.addAttribute("Resources", new FontObject("F1", "Naxe"));
        page.addAttribute("MediaBox", "[0 0 595.28 841.89]");

        /*
         * Create graphic stream with few graphics.
         */
        GraphicStreamObject graphicStreamObject = new GraphicStreamObject();

        int chartX = 10;
        int chartY = 10;

        //dimension
        int chartWidth = 150;
        int chartHeight = 100;

        //padding
        int chartTopPadding = 10;
        int chartLeftPadding = 20;
        int chartBottomPadding = 20;
        int chartRightPadding = 5;

        //chart box
        int chartBoxX = chartX + chartLeftPadding;
        int chartBoxY = chartY + chartTopPadding;
        int chartBoxWidth = chartWidth - chartLeftPadding - chartRightPadding;
        int chartBoxHeight = chartHeight - chartBottomPadding - chartTopPadding;

        //bar width
        int barWidth = 20;

        //chart data

        //dataMax
        int dataMax = 300;

        //data step
        int dataStep = 50;

        //set font, line width and color
        graphicStreamObject.SetFont("Arial", "B", 16);
        graphicStreamObject.SetLineWidth(0.2f);
        graphicStreamObject.SetDrawColor(0);

        //chart boundary
        graphicStreamObject.Rect(chartX, chartY, chartWidth, chartHeight);

        //vertical axis line
        graphicStreamObject.Line(
                chartBoxX,
                chartBoxY,
                chartBoxX,
                (chartBoxY + chartBoxHeight)
        );
        //horizontal axis line
        graphicStreamObject.Line(
                chartBoxX - 2,
                (chartBoxY + chartBoxHeight),
                chartBoxX + (chartBoxWidth),
                (chartBoxY + chartBoxHeight)
        );

        ///vertical axis
        //calculate chart"s y axis scale unit
        float yAxisUnits = chartBoxHeight * 1.0f / dataMax;

        //draw the vertical (y) axis labels
        for (int i = 0; i <= dataMax; i += dataStep) {
            //y position
            float yAxisPos = chartBoxY + (yAxisUnits * i);
            //draw y axis line
            graphicStreamObject.Line(
                    chartBoxX - 2,
                    yAxisPos,
                    chartBoxX,
                    yAxisPos
            );
            //set cell position for y axis labels
            graphicStreamObject.SetXY(chartBoxX - chartLeftPadding, yAxisPos - 2);
            //Cell(chartLeftPadding-4 , 5 , dataMax-i , 1);---------------
            graphicStreamObject.Cell(chartLeftPadding - 4, 5, (dataMax - i) + "", 0 + "", 0, "R", false, "");
        }

///horizontal axis
//set cells position
        graphicStreamObject.SetXY(chartBoxX, chartBoxY + chartBoxHeight);

//cell"s width
        float xLabelWidth = chartBoxWidth / data.size();

//loop horizontal axis and draw the bar
        float barXPos = 0;
        Float preX = null;
        Float preY = null;
        for (Map<String, Object> map : data) {
            String key = (String) map.get("name");
            List<Integer> color = (List<Integer>) map.get("color");
            Integer value = (Integer) map.get("value");
            //print the label
            //Cell(xLabelWidth , 5 , itemName , 1 , 0 , "C");--------------
            graphicStreamObject.Cell(xLabelWidth, 5, key, String.valueOf(0), 0, "C", false, "");

            ///drawing the bar
            //bar color
            graphicStreamObject.SetFillColor(color.get(0), color.get(1), color.get(2));
            //bar height
            float barHeight = yAxisUnits * value;
            //bar x position
            float barX = (xLabelWidth / 2) + (xLabelWidth * barXPos);
            barX = barX + chartBoxX;
            //bar y position
            float barY = chartBoxHeight - barHeight;
            barY = barY + chartBoxY;

            if (preX == null || preY == null) {
                preX = barX;
                preY = barY;
            } else {
                //draw the line
                graphicStreamObject.Line(preX, preY, barX, barY);
                preX = barX;
                preY = barY;
            }
            //increase x position (next series)
            barXPos++;
        }

        barXPos = 0;
        for (Map<String, Object> map : data) {
            Integer value = (Integer) map.get("value");
            //bar height
            float barHeight = yAxisUnits * value;
            //bar x position
            float barX = (xLabelWidth / 2) + (xLabelWidth * barXPos);
            barX = barX + chartBoxX;
            //bar y position
            float barY = chartBoxHeight - barHeight;
            barY = barY + chartBoxY;

            graphicStreamObject.SetXY(barX-0.65f, barY-3);
            graphicStreamObject.Cell(0, 3, ".", String.valueOf(0), 0, "", false, "");

            graphicStreamObject.SetXY(barX-5, barY-8);
            graphicStreamObject.Cell(800, 10, value+"", String.valueOf(0), 0, "", false, "");
            //increase x position (next series)
            barXPos++;
        }

        //axis labels
        graphicStreamObject.SetFont("Arial", "B", 12);
        graphicStreamObject.SetXY(chartX, chartY);
        graphicStreamObject.Cell(100, 10, "Amount", String.valueOf(0), 0, "", false, "");
        graphicStreamObject.SetXY((chartWidth / 2) - 50 + chartX, chartY + chartHeight - (chartBottomPadding / 2));
        graphicStreamObject.Cell(100, 5, "Series", String.valueOf(0), 0, "C", false, "");

        graphicStreamObject.SetFont("Arial", "B", 18);
        graphicStreamObject.SetXY(chartX+20, chartY-10);
        graphicStreamObject.Cell(800, 10, "Schools Vs Years", String.valueOf(0), 0, "", false, "");


        page.addContent(graphicStreamObject);
        return page;
    }
}
