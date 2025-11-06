package com.project.demo.service;

import com.project.demo.model.Coin;

import java.util.List;

public interface CoinService {
    List<Coin> getCoinList(int page) throws Exception;

    String getMarketChart(String coinId, int days) throws Exception;

    String getCoinDetails(String coinId) throws Exception; //fetch coin using api

    Coin findById(String coinId) throws Exception; //fetch coin using db

    String searchCoin(String keyword) throws Exception;

    String getTop50CoinsByMarketCapRank() throws Exception;

    String getTrendingCoins() throws Exception;

}
