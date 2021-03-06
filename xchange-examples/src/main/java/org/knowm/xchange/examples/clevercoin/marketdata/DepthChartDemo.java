package org.knowm.xchange.examples.clevercoin.marketdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.clevercoin.CleverCoinExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.polling.marketdata.PollingMarketDataService;

/**
 * Demonstrate requesting OrderBook from Bitstamp and plotting it using XChart.
 */
public class DepthChartDemo {

  public static void main(String[] args) throws IOException {

    // Use the factory to get the version 1 Bitstamp exchange API using default settings
    Exchange bitstampExchange = ExchangeFactory.INSTANCE.createExchange(CleverCoinExchange.class.getName());

    // Interested in the public market data feed (no authentication)
    PollingMarketDataService marketDataService = bitstampExchange.getPollingMarketDataService();

    System.out.println("fetching data...");

    // Get the current orderbook
    OrderBook orderBook = marketDataService.getOrderBook(CurrencyPair.BTC_EUR);

    System.out.println("received data.");

    System.out.println("plotting...");

    // Create Chart
    XYChart chart = new XYChartBuilder().width(800).height(600).title("CleverCoin Order Book").xAxisTitle("BTC").yAxisTitle("EUR").build();

    // Customize Chart
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);

    // BIDS
    List<Number> xData = new ArrayList<Number>();
    List<Number> yData = new ArrayList<Number>();
    BigDecimal accumulatedBidUnits = new BigDecimal("0");
    for (LimitOrder limitOrder : orderBook.getBids()) {
      if (limitOrder.getLimitPrice().doubleValue() > 10) {
        xData.add(limitOrder.getLimitPrice());
        accumulatedBidUnits = accumulatedBidUnits.add(limitOrder.getTradableAmount());
        yData.add(accumulatedBidUnits);
      }
    }
    Collections.reverse(xData);
    Collections.reverse(yData);

    // Bids Series
    XYSeries series = chart.addSeries("bids", xData, yData);
    series.setMarker(SeriesMarkers.NONE);

    // ASKS
    xData = new ArrayList<Number>();
    yData = new ArrayList<Number>();
    BigDecimal accumulatedAskUnits = new BigDecimal("0");
    for (LimitOrder limitOrder : orderBook.getAsks()) {
      if (limitOrder.getLimitPrice().doubleValue() < 1000) {
        xData.add(limitOrder.getLimitPrice());
        accumulatedAskUnits = accumulatedAskUnits.add(limitOrder.getTradableAmount());
        yData.add(accumulatedAskUnits);
      }
    }

    // Asks Series
    series = chart.addSeries("asks", xData, yData);
    series.setMarker(SeriesMarkers.NONE);

    new SwingWrapper(chart).displayChart();

  }

}
