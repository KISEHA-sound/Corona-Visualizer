package com.corona.corona_visualizer.service;

import com.corona.corona_visualizer.crawler.CovidCrawler;
import com.corona.corona_visualizer.model.RegionCovidStats;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CovidService {
    public Map<String, String> getTotalStats() throws Exception {
        return CovidCrawler.getTotalStats();
    }

    public List<RegionCovidStats> getRegionStats() throws Exception {
        return CovidCrawler.getRegionStats(); // 모든 지역의 확진자 수 반환
    }

    public Map<String, Long> getAgeStats() throws Exception {
        return CovidCrawler.getAgeStats();
    }

    public Map<String, Long> getGenderStats() throws Exception {
        return CovidCrawler.getGenderStats();
    }
}
