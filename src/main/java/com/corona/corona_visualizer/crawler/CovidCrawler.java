package com.corona.corona_visualizer.crawler;

import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.corona.corona_visualizer.model.RegionCovidStats;

public class CovidCrawler {

    // 나무위키 URL
    private static final String URL = "https://namu.wiki/w/코로나바이러스감염증-19/현황/국가별/대한민국#s-3";

    // 대한민국 17개 시도 + 검역
    private static final Set<String> validRegions = Set.of(
        "서울특별시", "부산광역시", "대구광역시", "인천광역시",
        "광주광역시", "대전광역시", "울산광역시", "세종특별자치시",
        "경기도", "강원특별자치도", "충청북도", "충청남도",
        "전라북도", "전라남도", "경상북도", "경상남도",
        "제주특별자치도", "검역"
    );

    // 누적 확진자 수 / 사망자 수만 추출
    public static Map<String, String> getTotalStats() throws Exception {
        Map<String, String> result = new HashMap<>();

        // Jsoup으로 페이지 가져오기
        Document doc = Jsoup.connect(URL).get();

         // 1. 오른쪽 정렬된 셀 가져오기
        Elements tdList = doc.select("td[style*=text-align:right]");

        // 2. 확진자/사망자 수를 담을 리스트
        List<String> numbers = new ArrayList<>();

        for (Element td : tdList) {
            Elements span = td.select("strong > span");
            if (!span.isEmpty()) {
                String text = span.text();
                if (text.contains("명")) {
                    numbers.add(text);
                }
            }
        }

        // 3. 첫 번째는 확진자, 두 번째는 사망자
        if (numbers.size() >= 2) {
            result.put("확진자", numbers.get(0));
            result.put("사망자", numbers.get(1));
        } else {
            result.put("확진자", "데이터 없음");
            result.put("사망자", "데이터 없음");
        }

        return result;
    }

    // 시도별 확진자 수만 추출 (증가량 제외)
    public static List<RegionCovidStats> getRegionStats() throws Exception {
        List<RegionCovidStats> result = new ArrayList<>();

        Document doc = Jsoup.connect(URL).get();
        Elements rows = doc.select("tr.dyonRYro");

        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() >= 2) {
                String region = tds.get(0).text().trim();
                String confirmedText = tds.get(1).text().trim();

                // 괄호 앞까지만 사용하여 퍼센트 제거
                if (confirmedText.contains("(")) {
                    confirmedText = confirmedText.substring(0, confirmedText.indexOf("(")).trim();
                }

                // 숫자만 추출
                String confirmedStr = confirmedText.replaceAll("[^0-9]", "");

                // 시도 필터링
                if (validRegions.contains(region) && !confirmedStr.isEmpty()) {
                    long confirmed = Long.parseLong(confirmedStr);
                    result.add(new RegionCovidStats(region, confirmed, 0)); // 사망자 수는 0으로 설정
                }
            }
        }

        return result;
    }
    
    // 나이대별 확진자 수
    public static Map<String, Long> getAgeStats() throws Exception {
        Map<String, Long> stats = new LinkedHashMap<>();
        Document doc = Jsoup.connect(URL).get();
        Elements rows = doc.select("tr.dyonRYro");
    
        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() >= 2) {
                String label = tds.get(0).text().trim();
                String valueText = tds.get(1).text().trim();
    
                // 괄호 제거
                if (valueText.contains("(")) {
                    valueText = valueText.substring(0, valueText.indexOf("(")).trim();
                }
    
                String numberStr = valueText.replaceAll("[^0-9]", "");
    
                // ✅ 나이대만 추출: "숫자~숫자세" 형식
                if (label.matches("\\d{1,2}~\\d{1,2}세") && !numberStr.isEmpty()) {
                    long number = Long.parseLong(numberStr);
                    stats.put(label, number);
                }
            }
        }
    
        return stats;
    }

    // 성별 확진자 수
    public static Map<String, Long> getGenderStats() throws Exception {
        Map<String, Long> stats = new LinkedHashMap<>();
        Document doc = Jsoup.connect(URL).get();
        Elements rows = doc.select("tr.dyonRYro");
    
        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() >= 2) {
                String label = tds.get(0).text().trim();
                String valueText = tds.get(1).text().trim();
    
                // 괄호 제거
                if (valueText.contains("(")) {
                    valueText = valueText.substring(0, valueText.indexOf("(")).trim();
                }
    
                String numberStr = valueText.replaceAll("[^0-9]", "");
    
                // ✅ 성별만 추출: 남성 / 여성
                if ((label.equals("남성") || label.equals("여성")) && !numberStr.isEmpty()) {
                    long number = Long.parseLong(numberStr);
                    stats.put(label, number);
                }
            }
        }
    
        return stats;
    }

    // 테스트용 main() (단독 실행 시 사용)
    public static void main(String[] args) throws Exception {
        // 총 확진자, 총 사망자
        Map<String, String> total = getTotalStats();
        System.out.println("누적 확진자: " + total.get("확진자"));
        System.out.println("누적 사망자: " + total.get("사망자"));
        
        // 지역별 수
        System.out.println("\n지역별 확진자 수:");
        List<RegionCovidStats> stats = getRegionStats();
        for (RegionCovidStats stat : stats) {
            System.out.printf("- %s(확진자 수): %,d명\n", stat.getRegion(), stat.getConfirmed());
        }

        // 나이대별 수
        System.out.println("\n나이대별 확진자 수:");
        Map<String, Long> ageStats = getAgeStats();
        for (Map.Entry<String, Long> entry : ageStats.entrySet()) {
            System.out.printf("- %-8s: %,d명\n", entry.getKey(), entry.getValue());
        }

        // 성별 수
        System.out.println("\n성별 확진자 수:");
        Map<String, Long> genderStats = getGenderStats();
        for (Map.Entry<String, Long> entry : genderStats.entrySet()) {
            System.out.printf("- %-4s: %,d명\n", entry.getKey(), entry.getValue());
        }
    }
}
