package com.corona.corona_visualizer.controller;

import com.corona.corona_visualizer.model.RegionCovidStats;
import com.corona.corona_visualizer.service.CovidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.annotation.PostConstruct; // ✅ 꼭 import 필요

import java.util.*;

@Controller
public class CovidController {

    @Autowired
    private CovidService covidService;

    @PostConstruct
    public void init() {
        System.out.println("✅ CovidController가 정상 등록되었습니다!");
    }

    @GetMapping("/corona")
    public String showCoronaStats(Model model) {
        try {
            // 서비스 호출로 데이터 받아오기
            Map<String, String> total = covidService.getTotalStats();
            List<RegionCovidStats> regionStats = covidService.getRegionStats();
            Map<String, Long> ageStats = covidService.getAgeStats();  // 나이대별 확진자 수
            Map<String, Long> genderStats = covidService.getGenderStats();  // 성별 확진자 수

            // 지역 이름과 확진자 수 리스트 생성
            List<String> regionNames = new ArrayList<>();
            List<Integer> confirmedCounts = new ArrayList<>();
            for (RegionCovidStats stat : regionStats) {
                regionNames.add(stat.getRegion());
                confirmedCounts.add((int) stat.getConfirmed());
            }

            // 나이대별 확진자 리스트
            List<String> ageLabels = new ArrayList<>(ageStats.keySet());
            List<Long> ageCounts = new ArrayList<>(ageStats.values());

            // 성별 확진자 리스트
            List<String> genderLabels = new ArrayList<>(genderStats.keySet());
            List<Long> genderCounts = new ArrayList<>(genderStats.values());

            // Thymeleaf로 데이터 전달
            model.addAttribute("regionStats", regionStats);  // 모든 지역과 확진자 수 데이터를 전달
            model.addAttribute("total", total);  // 총 확진자, 사망자 정보도 전달

            model.addAttribute("regionNames", regionNames);
            model.addAttribute("confirmedCounts", confirmedCounts);
            model.addAttribute("total", total);

            model.addAttribute("ageLabels", ageLabels);
            model.addAttribute("ageCounts", ageCounts);

            model.addAttribute("genderLabels", genderLabels);
            model.addAttribute("genderCounts", genderCounts);

            // 지역별 확진자 수 비율 전달
            model.addAttribute("regionStats", regionStats);  // 원형 차트를 위한 데이터 추가

        } catch (Exception e) {
            model.addAttribute("error", "데이터를 불러오지 못했습니다: " + e.getMessage());
        }

        return "index"; // "index.html" 템플릿으로 반환
    }
}
