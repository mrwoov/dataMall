package com.example.datamall.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.datamall.entity.GoodsComment;
import com.example.datamall.service.AccountService;
import com.example.datamall.service.GoodsCommentService;
import com.example.datamall.service.GoodsService;
import com.example.datamall.vo.ResultData;
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
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsCommentService goodsCommentService;
    @Resource
    private GoodsService goodsService;

    //发起或回复评论
    @PostMapping("/send")
    public ResultData send(@RequestHeader("token") String token, @RequestBody GoodsComment goodsComment) {
        Integer uid = accountService.tokenToUid(token);
        if (uid == -1) {
            return ResultData.fail("登陆过期");
        }
        goodsComment.setUid(uid);
        //评论逻辑：最多允许二级评论，如出现参数parentId则查其父节点是否存在父节点，如存在则为非法请求
        if (goodsComment.getParentId() != null) {
            Integer grandparentId = goodsCommentService.getById(goodsComment.getParentId()).getParentId();
            if (grandparentId != null) {
                return ResultData.fail("非法请求");
            }
        }
        boolean state = goodsCommentService.save(goodsComment);
        return ResultData.state(state);
    }

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
        boolean isAdmin = accountService.checkAdminHavaAuth("/admin", token);
        if (!(owner || sender || isAdmin)) {
            return ResultData.fail();
        }
        boolean state = goodsCommentService.removeById(commentId);
        return ResultData.state(state);
    }

    //查看商品的评论
    @GetMapping("/")
    public ResultData getList(@RequestParam("goodsId") Integer goodsId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        IPage<GoodsComment> page = goodsCommentService.query(goodsId, pageNum, pageSize);
        return ResultData.success(page);
    }

}

