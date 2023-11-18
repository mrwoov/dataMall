package com.dataMall.adminCenter.controller;


import com.dataMall.adminCenter.service.AccountService;
import com.dataMall.adminCenter.service.GoodsCommentService;
import com.dataMall.adminCenter.service.GoodsService;
import com.dataMall.adminCenter.vo.ResultData;
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
@SuppressWarnings("ALL")
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

    // 删除评论：发评论er，商品所有者，管理员
    @DeleteMapping("/del")
    public ResultData del(@RequestHeader("token") String token, @RequestParam("commentId") Integer commentId) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登陆过期");
        }
        Integer goodsId = goodsCommentService.commentIdToGoodsId(commentId);
        if (goodsId == -1) {
            return ResultData.fail();
        }
        //商品所有者删除评论逻辑：拿到uid，拿到评论所属商品id，判断是不是商品owner，是的话就可以删除，否则不行
        boolean owner = goodsService.isOwner(uid, goodsId);
        //评论er删除评论逻辑
        boolean sender = goodsCommentService.isSender(uid, commentId);
        //管理员删除逻辑
        boolean isAdmin = accountService.checkAdminHavaAuth(authPath, token);
        if (!(owner || sender || isAdmin)) {
            return ResultData.fail();
        }
        boolean state = goodsCommentService.removeById(commentId);
        return ResultData.state(state);
    }

}

