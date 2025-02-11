package com.dataMall.searchCenter.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dataMall.common.entity.Goods;
import com.dataMall.common.vo.BlogVO;
import com.dataMall.searchCenter.dto.BlogEsDTO;
import com.dataMall.searchCenter.dto.GoodsEsDTO;
import com.dataMall.searchCenter.dto.UserEsDTO;
import com.dataMall.searchCenter.esDao.BlogEsDao;
import com.dataMall.searchCenter.esDao.GoodsEsDao;
import com.dataMall.searchCenter.esDao.UserEsDao;
import com.dataMall.searchCenter.feign.BlogService;
import com.dataMall.searchCenter.feign.GoodsService;
import com.dataMall.searchCenter.service.SearchService;
import com.dataMall.searchCenter.vo.SearchResponseVo;
import com.dataMall.searchCenter.vo.SearchVo;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private GoodsEsDao goodsEsDao;
    @Resource
    private BlogEsDao blogEsDao;
    @Resource
    private UserEsDao userEsDao;
    @Resource
    private GoodsService goodsService;
    @Resource
    private BlogService blogService;

    @Override
    public SearchResponseVo searchGoods(String keyword, Integer uid, Integer pageNum, Integer pageSize) {
        // es初始页码为0，pageNum传入null或负值时统一设为0，否则减1转换为0-based页码
        if (pageNum == null || pageNum < 0) {
            pageNum = 0;
        } else {
            pageNum -= 1;
        }
        //处理搜索情况
        Page<GoodsEsDTO> goodsEsDTOList = null;
        long total = 0;
        //1.只有关键词
        if (uid == null && StringUtils.isNotBlank(keyword)) {
            goodsEsDTOList = goodsEsDao.findByName(keyword, PageRequest.of(pageNum, pageSize));
            total = goodsEsDao.countByName(keyword);
        }
        //2.关键词+用户id
        else if (uid != null && StringUtils.isNotBlank(keyword)) {
            goodsEsDTOList = goodsEsDao.findByNameAndUid(keyword, uid, PageRequest.of(pageNum, pageSize));
            total = goodsEsDao.countByNameAndUid(keyword, uid);
        }
        //3.用户id
        else if (uid != null && StringUtils.isBlank(keyword)) {
            goodsEsDTOList = goodsEsDao.findByUid(uid, PageRequest.of(pageNum, pageSize));
            total = goodsEsDao.countByUid(uid);
        }
        if (goodsEsDTOList == null || goodsEsDTOList.isEmpty()) {
            return null;
        }
        // 取出商品id
        List<Integer> goodsIdList = goodsEsDTOList.stream().map(GoodsEsDTO::getId).toList();
        // 根据id查询商品
        List<Goods> goodsList = goodsService.getGoodsListByIds(goodsIdList);
        // 脱敏处理：过滤state不为0的，并去掉fileMd5字段
        goodsList = goodsList.stream().filter(goods -> goods.getState() == 0)
                .peek(goods -> goods.setFileMd5(null)).toList();
        // 构造返回结果
        List<SearchVo> searchVoList = goodsList.stream().map(goods -> {
            SearchVo searchVo = new SearchVo();
            searchVo.setGoods(goods);
            // 如果更新时间为空，则使用创建时间补充
            if (goods.getUpdateTime() == null) {
                goods.setUpdateTime(goods.getCreateTime());
            }
            searchVo.setUpdateTime(goods.getUpdateTime());
            searchVo.setType("goods");
            return searchVo;
        }).toList();
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        searchResponseVo.setTotal(total);
        searchResponseVo.setPageNum(pageNum);
        searchResponseVo.setPageSize(pageSize);
        searchResponseVo.setRecords(searchVoList);
        return searchResponseVo;
    }

    @Override
    public SearchResponseVo searchBlog(String keyword, Integer uid, Integer pageNum, Integer pageSize) {
        // es初始页码为0，pageNum传入null或负值时统一设为0，否则减1转换为0-based页码
        if (pageNum == null || pageNum < 0) {
            pageNum = 0;
        } else {
            pageNum -= 1;
        }
        // 处理搜索情况
        Page<BlogEsDTO> blogEsDTOList = null;
        long total = 0;
        // 1.只有关键词
        if (uid == null && StringUtils.isNotBlank(keyword)) {
            blogEsDTOList = blogEsDao.findBlogByTitle(keyword, PageRequest.of(pageNum, pageSize));
            total = blogEsDao.countBlogByTitle(keyword);
        }
        // 2.关键词+用户id
        else if (uid != null && StringUtils.isNotBlank(keyword)) {
            blogEsDTOList = blogEsDao.findBlogByTitleAndUid(keyword, uid, PageRequest.of(pageNum, pageSize));
            total = blogEsDao.countBlogByTitleAndUid(keyword, uid);
        }
        // 3.用户id
        else if (uid != null && StringUtils.isBlank(keyword)) {
            blogEsDTOList = blogEsDao.findByUid(uid, PageRequest.of(pageNum, pageSize));
            total = blogEsDao.countByUid(uid);
        }
        if (blogEsDTOList == null || blogEsDTOList.isEmpty()) {
            return null;
        }
        // 取出id
        List<Integer> blogIdList = blogEsDTOList.stream().map(BlogEsDTO::getId).toList();
        // 根据id查询
        List<BlogVO> blogVOList = blogService.getBlogListByIds(blogIdList);
        // 构造返回结果
        List<SearchVo> searchVoList = blogVOList.stream().map(blogVO -> {
            SearchVo searchVo = new SearchVo();
            searchVo.setBlogVO(blogVO);
            searchVo.setUpdateTime(blogVO.getUpdateTime());
            searchVo.setType("blog");
            return searchVo;
        }).toList();
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        searchResponseVo.setTotal(total);
        searchResponseVo.setPageNum(pageNum);
        searchResponseVo.setPageSize(pageSize);
        searchResponseVo.setRecords(searchVoList);
        return searchResponseVo;
    }

    @Override
    public SearchResponseVo searchUser(String keyword, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        // es初始页码为0，pageNum传入null或负值时统一设为0，否则减1转换为0-based页码
        if (pageNum == null || pageNum < 0) {
            pageNum = 0;
        } else {
            pageNum -= 1;
        }
        // 处理搜索情况
        Page<UserEsDTO> userEsDTOList = userEsDao.findByUsername(keyword, PageRequest.of(pageNum, pageSize));
        long total = userEsDao.countByUsername(keyword);
        if (userEsDTOList == null || userEsDTOList.isEmpty()) {
            return null;
        }
        // 构造返回结果
        List<SearchVo> searchVoList = userEsDTOList.stream().map(userEsDTO -> {
            SearchVo searchVo = new SearchVo();
            searchVo.setUserEsDTO(userEsDTO);
            searchVo.setType("user");
            return searchVo;
        }).toList();

        SearchResponseVo searchResponseVo = new SearchResponseVo();
        searchResponseVo.setTotal(total);
        searchResponseVo.setPageNum(pageNum);
        searchResponseVo.setPageSize(pageSize);
        searchResponseVo.setRecords(searchVoList);
        return searchResponseVo;
    }

    @Override
    public SearchResponseVo searchAll(String keyword, Integer uid, int page, int size) {
        // 1. 商品搜索，查询2倍size数据，以便后续过滤后仍能满足最终分页要求
        SearchResponseVo goodsSearchResponse = searchGoods(keyword, uid, page, size * 2);
        // 对goodsSearchResponse进行null判断，防止后续调用getRecords()时报空指针异常
        List<SearchVo> goodsList = goodsSearchResponse != null ? goodsSearchResponse.getRecords() : new ArrayList<>();
        long goodsTotal = goodsSearchResponse != null ? goodsSearchResponse.getTotal() : 0;
        // 2. 帖子搜索
        SearchResponseVo blogSearchResponse = searchBlog(keyword, uid, page, size * 2);
        List<SearchVo> blogList = blogSearchResponse != null ? blogSearchResponse.getRecords() : new ArrayList<>();
        long blogTotal = blogSearchResponse != null ? blogSearchResponse.getTotal() : 0;
        // 3. excelApp搜索
        List<SearchVo> excelAppList = new ArrayList<>();
        long excelAppTotal = 0;
        //用户搜索
        SearchResponseVo userSearchResponse = searchUser(keyword, page, size * 2);
        List<SearchVo> userList = userSearchResponse != null ? userSearchResponse.getRecords() : new ArrayList<>();
        long userTotal = userSearchResponse != null ? userSearchResponse.getTotal() : 0;
        // 4. 聚合所有入口搜索结果
        List<SearchVo> mergedList = new ArrayList<>();
        mergedList.addAll(goodsList);
        mergedList.addAll(blogList);
        mergedList.addAll(excelAppList);
        mergedList.addAll(userList);
        long total = goodsTotal + blogTotal + excelAppTotal + userTotal;
        // 遍历处理更新时间为空的情况，避免排序时出现空指针异常
        mergedList.forEach(vo -> {
            if (vo.getUpdateTime() == null && vo.getGoods() != null) {
                vo.setUpdateTime(vo.getGoods().getCreateTime());
            }
        });
        // 按更新时间降序排序，使用空值安全比较
        mergedList.sort((o1, o2) -> {
            if (o1.getUpdateTime() == null && o2.getUpdateTime() == null) return 0;
            if (o1.getUpdateTime() == null) return 1;
            if (o2.getUpdateTime() == null) return -1;
            return o2.getUpdateTime().compareTo(o1.getUpdateTime());
        });
        // 5. 对合并后的结果做全局分页，确保最终返回的数据条数为size条（例如10条）
        int totalRecords = mergedList.size();
        // 假定page参数从1开始
        int fromIndex = (page - 1) * size;
        if (fromIndex >= totalRecords) {
            // 如果页码超出数据总数，返回空记录
            SearchResponseVo response = new SearchResponseVo();
            response.setTotal(totalRecords);
            response.setPageNum(page);
            response.setPageSize(size);
            response.setRecords(new ArrayList<>());
            return response;
        }
        int toIndex = Math.min(fromIndex + size, totalRecords);
        List<SearchVo> pageList = mergedList.subList(fromIndex, toIndex);
        // 构造最终返回结果
        SearchResponseVo response = new SearchResponseVo();
        response.setTotal(totalRecords);
        response.setPageNum(page);
        response.setPageSize(size);
        response.setRecords(pageList);
        return response;
    }


}
