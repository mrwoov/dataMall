package com.dataMall.goodsCenter.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataMall.goodsCenter.entity.GoodsFile;
import com.dataMall.goodsCenter.feign.AccountService;
import com.dataMall.goodsCenter.service.GoodsFileService;
import com.dataMall.goodsCenter.utils.OssUtils;
import com.dataMall.goodsCenter.vo.ResultData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dataMall.goodsCenter.controller.GoodsController.generateFileName;

/**
 * <p>
 * 商品数据文件 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-10-26
 */
@RestController
@RequestMapping("/goodsFiles")
public class GoodsFileController {
    @Resource
    private GoodsFileService goodsFileService;
    @Resource
    private AccountService accountService;
    @Resource
    private OssUtils ossUtils;

    public GoodsFileController(OssUtils ossUtils) {
        this.ossUtils = ossUtils;
    }

    //用户上传图片
    @PostMapping("/user/upload_pic")
    public ResultData userUploadPic(@RequestHeader("token") String token, @RequestPart("file") MultipartFile file) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }
        //处理图片
        String originalName = file.getOriginalFilename();
        String processedFileName = generateFileName(Objects.requireNonNull(originalName));
        //处理上传的数据文件
        //计算文件md5
        String fileMd5 = calculateFileMD5(file);
        //生成文件链接
        String dataFileUrl = ossUtils.getPicUrlUser(accountId, processedFileName);
        //保存至商品数据文件表
        GoodsFile goodsFile = new GoodsFile(accountId, processedFileName, dataFileUrl, fileMd5);
        boolean state = goodsFileService.save(goodsFile);
        if (!state) {
            return ResultData.fail();
        }
        //开始上传
        boolean uploadStatus = ossUtils.uploadPicUser(accountId, processedFileName, file);
        //上传失败操作
        if (!uploadStatus) {
            goodsFileService.removeById(goodsFile.getId());
            return ResultData.fail("文件上传失败");
        }
        //上传成功
        Map<String, String> map = new HashMap<>();
        map.put("md5", goodsFile.getMd5());
        map.put("name", originalName);
        return ResultData.success(map);
    }

    //用户上传文件
    @PostMapping("/user/upload_file")
    public ResultData userUploadFile(@RequestHeader("token") String token, @RequestPart("file") MultipartFile file) {
        Integer accountId = accountService.tokenToUid(token);
        if (accountId == -1) {
            return ResultData.fail("登录过期");
        }
        String originalName = file.getOriginalFilename();
        String processedFileName = generateFileName(Objects.requireNonNull(originalName));
        //处理上传的数据文件
        //计算md5
        String fileMd5 = calculateFileMD5(file);
        //生成文件链接
        String dataFileUrl = ossUtils.getGoodsDataUrlUser(accountId, processedFileName);
        //保存至商品数据文件表
        GoodsFile goodsFile = new GoodsFile(accountId, originalName, dataFileUrl, fileMd5);
        boolean state = goodsFileService.save(goodsFile);
        if (!state) {
            return ResultData.fail();
        }
        //开始上传
        boolean uploadStatus = ossUtils.uploadGoodsData(accountId, processedFileName, file);
        //上传失败操作
        if (!uploadStatus) {
            goodsFileService.removeById(goodsFile.getId());
            return ResultData.fail("文件上传失败");
        }
        //上传成功
        Map<String, String> map = new HashMap<>();
        map.put("md5", goodsFile.getMd5());
        map.put("name", originalName);
        return ResultData.success(map);
    }

    //计算文件md5值
    private String calculateFileMD5(MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            return "";
        }
        // 使用MD5算法计算数据的MD5值
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        md.update(data);
        byte[] digest = md.digest();

        // 将字节数组转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    //新增或修改
    @PatchMapping("/")
    public ResultData saveOrUpdate(@RequestBody GoodsFile goodsFile) {
        return ResultData.state(goodsFileService.saveOrUpdate(goodsFile));
    }

    //删除by id
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return goodsFileService.removeById(id);
    }

    //批量删除
    @PostMapping("/del_batch")
    public Boolean deleteBatch(@RequestBody List<Integer> ids) {
        return goodsFileService.removeByIds(ids);
    }

    //查找全部
    @GetMapping
    public List<GoodsFile> findAll() {
        return goodsFileService.list();
    }

    //查找单个
    @GetMapping("/{id}")
    public GoodsFile findOne(@PathVariable Integer id) {
        return goodsFileService.getById(id);
    }

    //分页查询
    @GetMapping("/page")
    public Page<GoodsFile> findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize) {
        return goodsFileService.page(new Page<>(pageNum, pageSize));
    }
}

