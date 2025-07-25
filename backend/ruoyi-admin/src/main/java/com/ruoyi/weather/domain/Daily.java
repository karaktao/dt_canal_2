package com.ruoyi.weather.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class Daily {





    private List<Date> time;

    private List<String> temperature2mMax;

    private List<String> temperature2mMin;

    private List<Integer> windDirection10mDominant;

    private List<String> windSpeed10mMax;


    private List<String> precipitationSum;

    private List<Integer> precipitationProbabilityMax;



}
