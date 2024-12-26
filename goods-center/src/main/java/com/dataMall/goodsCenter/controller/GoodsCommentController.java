package com.dataMall.goodsCenter.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataMall.goodsCenter.common.BaseResponse;
import com.dataMall.goodsCenter.common.ErrorCode;
import com.dataMall.goodsCenter.entity.GoodsComment;
import com.dataMall.goodsCenter.exception.BusinessException;
import com.dataMall.goodsCenter.feign.AccountService;
import com.dataMall.goodsCenter.service.GoodsCommentService;
import com.dataMall.goodsCenter.service.GoodsService;
import com.dataMall.goodsCenter.common.ResultUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 商品评论表 前端控制器
 * </p>
 *
 * @author woov
 * @since 2023-08-29
 */
@RestController
@RequestMapping("/goodsComment")
public class GoodsCommentController {
    private final String authPath = "comment";
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsCommentService goodsCommentService;
    @Resource
    private GoodsService goodsService;

    //发起或回复评论
    @PostMapping("/send")
    public BaseResponse<Object> send(@RequestHeader("token") String token, @RequestBody GoodsComment goodsComment) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        goodsComment.setUid(uid);
        //评论逻辑：最多允许二级评论，如出现参数parentId则查其父节点是否存在父节点，如存在则为非法请求
        if (goodsComment.getParentId() != null) {
            Integer grandparentId = goodsCommentService.getById(goodsComment.getParentId()).getParentId();
            if (grandparentId != null) {
                throw new BusinessException(ErrorCode.FAIL);
            }
        }
        boolean state = goodsCommentService.save(goodsComment);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    // 删除评论：发评论er，商品所有者，管理员
    @DeleteMapping("/del")
    public BaseResponse<Object> del(@RequestHeader("token") String token, @RequestParam("commentId") Integer commentId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Integer goodsId = goodsCommentService.commentIdToGoodsId(commentId);
        if (goodsId == -1) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        //商品所有者删除评论逻辑：拿到uid，拿到评论所属商品id，判断是不是商品owner，是的话就可以删除，否则不行
        boolean owner = goodsService.isOwner(uid, goodsId);
        //评论er删除评论逻辑
        boolean sender = goodsCommentService.isSender(uid, commentId);
        boolean state = goodsCommentService.removeById(commentId);
        if (!state) {
            throw new BusinessException(ErrorCode.FAIL);
         }
        return ResultUtils.success();
    }

    //查看商品的评论
    @GetMapping("/")
    public BaseResponse<IPage<GoodsComment>> getList(@RequestParam("goodsId") Integer goodsId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        IPage<GoodsComment> page = goodsCommentService.query(goodsId, pageNum, pageSize);
        return ResultUtils.success(page);
    }

}

