package utils;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class Graphics {

    private XYChart chart;

    public Graphics(String title, String xAxis, String yAxis) {
        chart = new XYChartBuilder().width(800).height(600).title(title).xAxisTitle(xAxis).yAxisTitle(yAxis).build();
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setChartTitleFont(new Font("TimesRoman", Font.BOLD, 8));
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(5);
    }

    public Graphics addGraphic(List<Point> points, String graphicName, Color color, XYSeries.XYSeriesRenderStyle style) {
        List<Double> xData = new LinkedList<>();
        List<Double> yData = new LinkedList<>();
        for (Point point : points) {
            xData.add(point.x);
            yData.add(point.y);
        }
        XYSeries series = chart.addSeries(graphicName, xData, yData);
        series.setMarker(SeriesMarkers.SQUARE);
        series.setMarkerColor(color);
        series.setXYSeriesRenderStyle(style);
        return this;
    }

    public Graphics show() {
        new SwingWrapper<>(chart).displayChart();
        return this;
    }

    public void saveBitmap(String filename) {
        try {
            BitmapEncoder.saveBitmap(chart, filename, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            System.err.println("Unable to save " + filename + ": " + e.getMessage());
        }
    }

    public void saveBitmap(String filename, BitmapEncoder.BitmapFormat format) {
        try {
            BitmapEncoder.saveBitmap(chart, filename, format);
        } catch (IOException e) {
            System.err.println("Unable to save " + filename + ": " + e.getMessage());
        }
    }
}
