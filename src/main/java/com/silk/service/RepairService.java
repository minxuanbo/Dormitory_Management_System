package com.silk.service;

import com.silk.mapper.RepairMapper;
import com.silk.entity.Repair;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LindaSilk
 * @date 2021年3月13日, 周六
 */
@Service
public class RepairService {

    @Autowired
    private RepairMapper repairMapper;

    public int create(Repair repair) {
        return repairMapper.create(repair);
    }

    public int delete(String ids) {
        String[] arr = ids.split(",");
        int row = 0;
        for (String s : arr) {
            if(!StringUtils.isEmpty(s)){
                repairMapper.delete(Integer.parseInt(s));
            row++;
            }
        }
        return row;
    }

    public int delete(Integer id) {
        return repairMapper.delete(id);
    }

    public int update(Repair repair) {
        return repairMapper.update(repair);
    }

    public int updateSelective(Repair repair) {
        return repairMapper.updateSelective(repair);
    }

    public PageInfo<Repair> query(Repair repair) {
        if(repair != null && repair.getPage() != null){
            PageHelper.startPage(repair.getPage(),repair.getLimit());
        }
        return new PageInfo<Repair>(repairMapper.query(repair));
    }

    public Repair detail(Integer id) {
        return repairMapper.detail(id);
    }

    public int count(Repair repair) {
        return repairMapper.count(repair);
    }

    public int countByStatus(Integer repStatus) {
        return repairMapper.countByStatus(repStatus);
    }

    public int countByUrgency(Integer urgency) {
        return repairMapper.countByUrgency(urgency);
    }

    public int countByRepairer(Integer repairerId) {
        return repairMapper.countByRepairer(repairerId);
    }

    public double avgRating() {
        return repairMapper.avgRating();
    }

    public int countByRating(Integer rating) {
        return repairMapper.countByRating(rating);
    }

    public Map<String, Object> getStatistics() {
        int s0 = countByStatus(0);
        int s1 = countByStatus(1);
        int s2 = countByStatus(2);
        int s3 = countByStatus(3);
        int s4 = countByStatus(4);
        int u0 = countByUrgency(0);
        int u1 = countByUrgency(1);
        int u2 = countByUrgency(2);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", count(new Repair()));
        stats.put("status0", s0);
        stats.put("status1", s1);
        stats.put("status2", s2);
        stats.put("status3", s3);
        stats.put("status4", s4);
        stats.put("urgency0", u0);
        stats.put("urgency1", u1);
        stats.put("urgency2", u2);

        // 兼容前端图表的字段
        stats.put("pendingCount", s0);
        stats.put("processingCount", s1 + s2);
        stats.put("completedCount", s3);
        stats.put("reviewedCount", s4);
        stats.put("normalCount", u0);
        stats.put("urgentCount", u1);
        stats.put("veryUrgentCount", u2);

        List<Map<String, Object>> statusPieData = new ArrayList<>();
        statusPieData.add(pieItem("待指派", s0, "#5b8ff9"));
        statusPieData.add(pieItem("处理中", s1 + s2, "#f6bd16"));
        statusPieData.add(pieItem("已完成", s3, "#5ad8a6"));
        statusPieData.add(pieItem("已评价", s4, "#9270ca"));
        stats.put("statusPieData", statusPieData);

        List<Integer> statusBarData = new ArrayList<>();
        statusBarData.add(s0);
        statusBarData.add(s1 + s2);
        statusBarData.add(s3);
        statusBarData.add(s4);
        stats.put("statusBarData", statusBarData);

        List<Map<String, Object>> urgencyPieData = new ArrayList<>();
        urgencyPieData.add(pieItem("普通", u0, "#5ad8a6"));
        urgencyPieData.add(pieItem("紧急", u1, "#f6bd16"));
        urgencyPieData.add(pieItem("非常紧急", u2, "#f5222d"));
        stats.put("urgencyPieData", urgencyPieData);

        // 近7日趋势
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        List<String> trendDates = new ArrayList<>();
        List<Integer> trendNewData = new ArrayList<>();
        List<Integer> trendDoneData = new ArrayList<>();
        Map<String, Object> newMap = new HashMap<>();
        for (Map<String, Object> m : repairMapper.countTrendNew()) {
            newMap.put(String.valueOf(m.get("stat_date")), m.get("cnt"));
        }
        Map<String, Object> doneMap = new HashMap<>();
        for (Map<String, Object> m : repairMapper.countTrendDone()) {
            doneMap.put(String.valueOf(m.get("stat_date")), m.get("cnt"));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        for (int i = 6; i >= 0; i--) {
            Calendar d = (Calendar) cal.clone();
            d.add(Calendar.DAY_OF_MONTH, -i);
            String key = new SimpleDateFormat("yyyy-MM-dd").format(d.getTime());
            trendDates.add(sdf.format(d.getTime()));
            trendNewData.add(toInt(newMap.get(key)));
            trendDoneData.add(toInt(doneMap.get(key)));
        }
        stats.put("trendDates", trendDates);
        stats.put("trendNewData", trendNewData);
        stats.put("trendDoneData", trendDoneData);
        stats.put("last7DaysNew", trendNewData);
        stats.put("last7DaysDone", trendDoneData);
        return stats;
    }

    public Map<String, Object> getSatisfaction() {
        double avg = avgRating();
        int c1 = countByRating(1);
        int c2 = countByRating(2);
        int c3 = countByRating(3);
        int c4 = countByRating(4);
        int c5 = countByRating(5);

        Map<String, Object> sat = new HashMap<>();
        sat.put("avgRating", avg);
        sat.put("avgScore", avg);
        sat.put("avgSatisfaction", avg);
        sat.put("rating1", c1);
        sat.put("rating2", c2);
        sat.put("rating3", c3);
        sat.put("rating4", c4);
        sat.put("rating5", c5);
        sat.put("totalEvaluated", c1 + c2 + c3 + c4 + c5);
        sat.put("totalCount", c1 + c2 + c3 + c4 + c5);
        sat.put("count", c1 + c2 + c3 + c4 + c5);
        sat.put("star1", c1);
        sat.put("star2", c2);
        sat.put("star3", c3);
        sat.put("star4", c4);
        sat.put("star5", c5);
        sat.put("count1", c1);
        sat.put("count2", c2);
        sat.put("count3", c3);
        sat.put("count4", c4);
        sat.put("count5", c5);
        sat.put("score1Count", c1);
        sat.put("score2Count", c2);
        sat.put("score3Count", c3);
        sat.put("score4Count", c4);
        sat.put("score5Count", c5);

        List<Map<String, Object>> starDistribution = new ArrayList<>();
        starDistribution.add(pieItem("1星", c1, "#f5222d"));
        starDistribution.add(pieItem("2星", c2, "#fa8c16"));
        starDistribution.add(pieItem("3星", c3, "#f6bd16"));
        starDistribution.add(pieItem("4星", c4, "#52c41a"));
        starDistribution.add(pieItem("5星", c5, "#5ad8a6"));
        sat.put("starDistribution", starDistribution);
        return sat;
    }

    private Map<String, Object> pieItem(String name, int value, String color) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("value", value);
        Map<String, Object> style = new LinkedHashMap<>();
        style.put("color", color);
        item.put("itemStyle", style);
        return item;
    }

    private int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
