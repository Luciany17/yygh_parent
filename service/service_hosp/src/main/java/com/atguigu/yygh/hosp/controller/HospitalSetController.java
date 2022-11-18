package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@CrossOrigin
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Api(tags = "医院设置")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "查询所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospSet() {
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        return flag ? Result.ok() : Result.fail();
    }

    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable Integer current, @PathVariable Integer limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> page = new Page<>(current, limit);
        QueryWrapper<HospitalSet> warpper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())) {
            warpper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())) {
            warpper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }

        Page<HospitalSet> setPage = hospitalSetService.page(page, warpper);
        return Result.ok(setPage);
    }

    @ApiOperation(value = "增加医院设置")
    @PostMapping("saveHospSet")
    public Result saveHospSet(@RequestBody HospitalSet hospitalSet) {
        hospitalSet.setStatus(1);
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        return save ? Result.ok() : Result.fail();

    }

    @ApiOperation(value = "根据id查询医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id) {
        //模拟异常

//        try {
//            int i = 5/0;
//        } catch (Exception e) {
//            throw new YyghException("失败",201);
//        }
        HospitalSet byId = hospitalSetService.getById(id);
        return Result.ok(byId);
    }

    @ApiOperation(value = "更新医院设置")
    @PutMapping("updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        return flag ? Result.ok() : Result.fail();
    }

    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("removeHospSets")
    public Result removeHospSets(@RequestBody List<Long> list) {
        boolean flag = hospitalSetService.removeByIds(list);
        return flag ? Result.ok() : Result.fail();
    }

    @ApiOperation(value = "锁定与解锁")
    @PutMapping("lockHospSet/{id}/{status}")
    public Result lockHospSet(@PathVariable Long id, @PathVariable Integer status) {
        //获取医院设置信息并设置状态
        HospitalSet byId = hospitalSetService.getById(id);
        byId.setStatus(status);
        //更新信息
        boolean flag = hospitalSetService.updateById(byId);
        return flag ? Result.ok() : Result.fail();

    }

    @ApiOperation(value = "发送签名秘钥")
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id) {
        //获取医院设置信息
        HospitalSet byId = hospitalSetService.getById(id);
        String hoscode = byId.getHoscode();
        String signKey = byId.getSignKey();
        //TODO 发送短信

        return Result.ok();

    }
}
