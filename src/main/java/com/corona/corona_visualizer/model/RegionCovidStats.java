package com.corona.corona_visualizer.model;

// 한 지역의 코로나 정보를 담는 데이터 클래스 (Model 역할)
public class RegionCovidStats {
    // 지역명
    private String region;
    // 누적 확진자 수
    private long confirmed;
    // 누적 사망자 수
    private int deaths;
    // 누적 완치자 수
    private int recovered;
    

    // 생성자: 객체 생성 시 필드 초기화
    public RegionCovidStats(String region, long confirmed, long death) {
        this.region = region;
        this.confirmed = confirmed;
    }

    // 각 필드에 접근할 수 있는 getter 메서드들
    public String getRegion() { return region; }
    public long getConfirmed() { return confirmed; }
    public int getDeaths() { return deaths; }
    public int getRecovered() { return recovered; }
}
