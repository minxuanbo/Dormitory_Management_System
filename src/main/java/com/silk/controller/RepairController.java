package com.silk.controller;

import com.github.pagehelper.PageInfo;
import com.silk.entity.Building;
import com.silk.entity.Repair;
import com.silk.entity.Room;
import com.silk.entity.User;
import com.silk.service.BuildingService;
import com.silk.service.RepairService;
import com.silk.service.RoomService;
import com.silk.service.UserService;
import com.silk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private BuildingService buildingService;

    /**
     * 安全转换 Map 参数为 Integer：兼容前端传入的 Number 或字符串数字（如 form 表单提交的 "1"）。
     * 转换失败或为 null 时返回 null，交由调用方做参数校验。
     */
    private Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            String s = String.valueOf(v).trim();
            return s.isEmpty() ? null : Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("create")
    public Result create(@RequestBody Repair repair){
        int flag = repairService.create(repair);
        if(flag>0){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @GetMapping("delete")
    public Result delete(String ids){
        int flag = repairService.delete(ids);
        if(flag>0){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @PostMapping("update")
    public Result update(@RequestBody Repair repair){
        int flag = repairService.updateSelective(repair);
        if(flag>0){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @GetMapping("detail")
    public Repair detail(Integer id){
        Repair repair = repairService.detail(id);
        if (repair != null) {
            if (repair.getStuId() != null) {
                repair.setUser(userService.detail(repair.getStuId()));
            }
            if (repair.getRepairerId() != null) {
                repair.setRepairer(userService.detail(repair.getRepairerId()));
            }
        }
        return repair;
    }

    @PostMapping("query")
    public Map<String,Object> query(Repair repair, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        Integer userType = param.getUserType();

        if (userType == 0) {
            repair.setStuId(param.getId());
        } else if (userType == 1) {
            repair.setRepairerId(param.getId());
        }

        PageInfo<Repair> pageInfo = repairService.query(repair);
        fillRepairInfo(pageInfo.getList());
        return Result.ok(pageInfo);
    }

    private void fillRepairInfo(List<Repair> list) {
        list.forEach(entity->{
            if (entity.getStuId() != null) {
                entity.setUser(userService.detail(entity.getStuId()));
            }
            if (entity.getRepairerId() != null) {
                entity.setRepairer(userService.detail(entity.getRepairerId()));
            }
        });
    }

    @PostMapping("my_list")
    public Map<String,Object> myList(Repair repair, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        repair.setStuId(param.getId());
        PageInfo<Repair> pageInfo = repairService.query(repair);
        fillRepairInfo(pageInfo.getList());
        return Result.ok(pageInfo);
    }

    @PostMapping("my_orders")
    public Map<String,Object> myOrders(Repair repair, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        repair.setRepairerId(param.getId());
        PageInfo<Repair> pageInfo = repairService.query(repair);
        fillRepairInfo(pageInfo.getList());
        return Result.ok(pageInfo);
    }

    @PostMapping("my_history")
    public Map<String,Object> myHistory(Repair repair, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        repair.setRepairerId(param.getId());
        PageInfo<Repair> pageInfo = repairService.query(repair);
        fillRepairInfo(pageInfo.getList());
        return Result.ok(pageInfo);
    }

    @PostMapping("stu_create")
    public Result stuCreate(@RequestBody Repair repair, HttpServletRequest request){
        User param = (User)request.getAttribute("user");

        User student = userService.detail(param.getId());
        if (student != null && student.getRoomId() != null) {
            repair.setRoomId(student.getRoomId());
            // 楼栋ID优先从房间表关联查询（兼容任意楼栋编号，如13栋130701）
            Room room = roomService.detail(student.getRoomId());
            if (room != null && room.getBuildingId() != null) {
                repair.setBuildingId(room.getBuildingId());
            } else {
                // 兜底兼容旧数据：房间号如 100101 → 1号楼
                repair.setBuildingId(student.getRoomId() / 100000);
            }
            repair.setStuId(param.getId());
            repair.setRepStatus(0);
            repair.setRepDate(new Date());
            if (repair.getUrgency() == null) {
                repair.setUrgency(0);
            }

            // 自动指派：根据报修楼栋的负责维修人员（楼栋管理中设置 manager_id）直接指派
            if (repair.getBuildingId() != null) {
                Building building = buildingService.detail(repair.getBuildingId());
                if (building != null && building.getManagerId() != null) {
                    User repairer = userService.detail(building.getManagerId());
                    if (repairer != null) {
                        repair.setRepairerId(repairer.getId());
                        repair.setRepMan(repairer.getUserName());
                        repair.setRepStatus(1);   // 待接单
                        repair.setAssignedTime(new Date());
                    }
                }
            }

            int flag = repairService.create(repair);
            if(flag>0){
                return Result.ok();
            }else{
                return Result.fail();
            }
        }else {
            return Result.fail("操作失败，没有该学生的相关宿舍信息");
        }
    }

    @PostMapping("assign")
    public Result assign(@RequestBody Map<String, Object> params, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        if (param.getUserType() != 2) {
            return Result.fail("无权限操作");
        }

        Integer id = toInt(params.get("id"));
        Integer repairerId = toInt(params.get("repairerId"));
        if (id == null || repairerId == null) {
            return Result.fail("参数不完整");
        }

        Repair repair = new Repair();
        repair.setId(id);
        repair.setRepairerId(repairerId);
        repair.setRepStatus(1);
        repair.setAssignedTime(new Date());

        User repairer = userService.detail(repairerId);
        if (repairer != null) {
            repair.setRepMan(repairer.getUserName());
        }

        int flag = repairService.updateSelective(repair);
        if(flag>0){
            return Result.ok("指派成功");
        }else{
            return Result.fail("指派失败");
        }
    }

    @PostMapping("set_urgency")
    public Result setUrgency(@RequestBody Map<String, Object> params, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        if (param.getUserType() != 2) {
            return Result.fail("无权限操作");
        }

        Integer id = toInt(params.get("id"));
        Integer urgency = toInt(params.get("urgency"));
        if (id == null || urgency == null) {
            return Result.fail("参数不完整");
        }

        Repair repair = new Repair();
        repair.setId(id);
        repair.setUrgency(urgency);

        int flag = repairService.updateSelective(repair);
        if(flag>0){
            return Result.ok("设置成功");
        }else{
            return Result.fail("设置失败");
        }
    }

    @PostMapping("reject")
    public Result reject(@RequestBody Map<String, Object> params, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        if (param.getUserType() != 1) {
            return Result.fail("无权限操作");
        }

        Integer id = toInt(params.get("id"));
        if (id == null) {
            return Result.fail("参数不完整");
        }

        // 校验工单存在、处于待接单状态且当前指派给该维修人员
        Repair old = repairService.detail(id);
        if (old == null) {
            return Result.fail("工单不存在");
        }
        if (old.getRepStatus() == null || old.getRepStatus() != 1) {
            return Result.fail("当前状态不可拒绝");
        }
        if (old.getRepairerId() == null || !old.getRepairerId().equals(param.getId())) {
            return Result.fail("该工单未指派给您");
        }

        int flag = repairService.reject(id);
        if(flag>0){
            return Result.ok("已拒绝，工单返回待指派");
        }else{
            return Result.fail("操作失败");
        }
    }

    @PostMapping("accept")
    public Result accept(@RequestBody Map<String, Object> params, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        if (param.getUserType() != 1) {
            return Result.fail("无权限操作");
        }

        Integer id = toInt(params.get("id"));
        if (id == null) {
            return Result.fail("参数不完整");
        }

        Repair repair = new Repair();
        repair.setId(id);
        repair.setRepStatus(2);
        repair.setAcceptedTime(new Date());

        int flag = repairService.updateSelective(repair);
        if(flag>0){
            return Result.ok("接单成功");
        }else{
            return Result.fail("接单失败");
        }
    }

    @PostMapping("complete")
    public Result complete(@RequestBody Repair repairParam, HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        if (param.getUserType() != 1) {
            return Result.fail("无权限操作");
        }

        if (repairParam.getId() == null) {
            return Result.fail("参数不完整");
        }

        Repair repair = new Repair();
        repair.setId(repairParam.getId());
        repair.setRepStatus(3);
        repair.setRepairRecord(repairParam.getRepairRecord());
        repair.setCompletedTime(new Date());

        int flag = repairService.updateSelective(repair);
        if(flag>0){
            return Result.ok("完成维修成功");
        }else{
            return Result.fail("完成维修失败");
        }
    }

    @PostMapping("rate")
    public Result rate(@RequestBody Map<String, Object> params, HttpServletRequest request){
        User param = (User)request.getAttribute("user");

        Integer id = toInt(params.get("id"));
        Integer rating = toInt(params.get("rating"));
        String feedback = (String) params.get("feedback");

        if (id == null || rating == null) {
            return Result.fail("参数不完整");
        }

        Repair repair = repairService.detail(id);
        if (repair == null) {
            return Result.fail("工单不存在");
        }
        if (!repair.getStuId().equals(param.getId())) {
            return Result.fail("无权限评价");
        }
        if (repair.getRepStatus() < 3) {
            return Result.fail("工单未完成，无法评价");
        }

        Repair updateRepair = new Repair();
        updateRepair.setId(id);
        updateRepair.setRating(rating);
        updateRepair.setFeedback(feedback);
        updateRepair.setRepStatus(4);

        int flag = repairService.updateSelective(updateRepair);
        if(flag>0){
            return Result.ok("评价成功");
        }else{
            return Result.fail("评价失败");
        }
    }

    @PostMapping("statistics")
    public Result statistics(HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        if (param.getUserType() != 2) {
            return Result.fail("无权限操作");
        }
        Map<String, Object> stats = repairService.getStatistics();
        // 管理员首页「数据预览」卡片所需字段（后端返回字段与前端读取字段对齐）
        stats.put("totalCount", stats.get("total"));
        stats.put("avgSatisfaction", repairService.avgRating());
        User repairerQuery = new User();
        repairerQuery.setUserType(1);
        stats.put("repairerCount", userService.count(repairerQuery));
        return Result.ok(stats);
    }

    @PostMapping("satisfaction")
    public Result satisfaction(HttpServletRequest request){
        User param = (User)request.getAttribute("user");
        if (param.getUserType() != 2) {
            return Result.fail("无权限操作");
        }
        return Result.ok(repairService.getSatisfaction());
    }

    @GetMapping("repairer_list")
    public Result repairerList(){
        User param = new User();
        param.setUserType(1);
        param.setPage(1);
        param.setLimit(100);
        PageInfo<User> pageInfo = userService.query(param);
        return Result.ok(pageInfo.getList());
    }

}
