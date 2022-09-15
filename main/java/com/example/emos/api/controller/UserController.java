package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbUser;
import com.example.emos.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 生成登陆二维码的字符串
     */
    @GetMapping("/createQrCode")
    @Operation(summary = "生成二维码Base64格式的字符串")
    public R createQrCode() {
        HashMap map = userService.createQrCode();
        return R.ok(map);
    }

    /**
     * 检测登陆验证码
     *
     * @param form
     * @return
     */
    @PostMapping("/checkQrCode")
    @Operation(summary = "检测登陆验证码")
    public R checkQrCode(@Valid @RequestBody CheckQrCodeForm form) {
        boolean bool = userService.checkQrCode(form.getCode(), form.getUuid());
        return R.ok().put("result", bool);
    }

    @PostMapping("/wechatLogin")
    @Operation(summary = "微信小程序登陆")
    public R wechatLogin(@Valid @RequestBody WechatLoginForm form) {
        HashMap map = userService.wechatLogin(form.getUuid());
        boolean result = (boolean) map.get("result");
        if (result) {
            int userId = (int) map.get("userId");
            StpUtil.setLoginId(userId);
            Set<String> permissions = userService.searchUserPermissions(userId);
            map.remove("userId");
            map.put("permissions", permissions);
        }
        return R.ok(map);
    }

    /**
     * 登陆成功后加载用户的基本信息
     */
    @GetMapping("/loadUserInfo")
    @Operation(summary = "登陆成功后加载用户的基本信息")
    @SaCheckLogin
    public R loadUserInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap summary = userService.searchUserSummary(userId);
        return R.ok(summary);
    }

    @PostMapping("/searchById")
    @Operation(summary = "根据ID查找用户")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchUserByIdForm form) {
        HashMap map = userService.searchById(form.getUserId());
        return R.ok(map);
    }

    @GetMapping("/searchAllUser")
    @Operation(summary = "查询所有用户")
    @SaCheckLogin
    public R searchAllUser() {
        ArrayList<HashMap> list = userService.searchAllUser();
        return R.ok().put("list", list);
    }
    @PostMapping("/login")
    @Operation(summary = "登录系统")
    public R login(@Valid @RequestBody LoginFrom from){
        HashMap map = JSONUtil.parse(from).toBean(HashMap.class);//将form转换成带有用户名和密码的hashmap
        Integer userId = userService.login(map);
        R r = R.ok().put("result", userId != null ? true : false);
        if(userId!=null){
            StpUtil.setLoginId(userId);//其实就是session
            Set<String> permissions = userService.searchUserPermissions(userId);
            r.put("permissions",permissions);
        }
        return r;
    }
    @PostMapping("/updatePassword")
    @SaCheckLogin//确保用户登录后才能调用此方法
    @Operation(summary = "修改密码")
    public R updatePassword(@Valid @RequestBody UpdatePasswordFrom from){
        int userId = StpUtil.getLoginIdAsInt();//可以把cookie的令牌转换成userId
        HashMap param = new HashMap(){{
            put("userId",userId);
            put("password",from.getPassword());
        }};
        int rows = userService.updatePassword(param);//影响行数
        return R.ok().put("rows",rows);
    }
    @GetMapping("/logout")
    @Operation(summary = "退出系统")
    public R logout(){
        StpUtil.logout();//1.把Redis缓存的令牌信息抹除掉 2.让浏览器保存的cookie过期
        return R.ok();
    }
    @PostMapping("/searchUserByPage")
    @Operation(summary = "查询用户分页记录")
    @SaCheckPermission(value = {"ROOT","USER:SELECT"},mode = SaMode.OR)//只有ROOT,和USER:SELECT的权限才能进行查询
    public R searchUserByPage(@Valid @RequestBody SearchUserByPageFrom from){
        int page = from.getPage();
        int length = from.getLength();
        int start = (page-1)*length;
        HashMap map =JSONUtil.parse(from).toBean(HashMap.class);
        map.put("start",start);
        PageUtils pageUtils = userService.searchUserByPage(map);
        return R.ok().put("page",pageUtils);
    }
    @PostMapping("/insert")
    @Operation(summary = "添加用户记录")
    @SaCheckPermission(value = {"ROOT","USER:INSERT"},mode = SaMode.OR)
    public R insertUser(@Valid @RequestBody InsertUserFrom from){
        TbUser user = JSONUtil.parse(from).toBean(TbUser.class);
        user.setStatus((byte)1);
        user.setRole(JSONUtil.parseArray(from.getRole()).toString());
        user.setCreateTime(new Date());
        int rows = userService.insert(user);
        return R.ok().put("rows",rows);
    }
    @PostMapping("/update")
    @Operation(summary = "修改用户记录")
    @SaCheckPermission(value = {"ROOT","USER:UPDATE"},mode = SaMode.OR)
    public R updateUser(@Valid @RequestBody InsertUserFrom from){
        HashMap map = JSONUtil.parse(from).toBean(HashMap.class);
        map.replace("role",JSONUtil.parseArray(from.getRole()).toString());
        int rows = userService.update(map);
        if(rows==1){
            StpUtil.logoutByLoginId(from.getUsername());
        }
        return R.ok().put("rows",rows);
    }
    @PostMapping("/deleteUserByIds")
    @Operation(summary = "删除非管理员的用户记录")
    @SaCheckPermission(value = {"ROOT","USER:DELETE"},mode = SaMode.OR)
    public R deleteUserByIds(@Valid @RequestBody DeleteUserByIdsForm form){
        String userId = StpUtil.getLoginIdAsString();
        if(ArrayUtil.contains(form.getIds(),userId)){
            return R.error("您不能删除您自己的用户记录");
        }
        int rows = userService.deleteUserByIds(form.getIds());
        if(rows>0){
            for (int i = 0; i < form.getIds().length; i++) {
                StpUtil.logoutByLoginId(form.getIds()[i]);//将用户t下线
            }
        }
        return R.ok().put("rows",rows);
    }
}
