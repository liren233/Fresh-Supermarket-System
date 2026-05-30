package com.xhm.main.dao;  // 修正为你的实际包路径

import com.xhm.main.model.BGood;  // 修正为你的实际包路径
import com.xhm.main.model.Page;   // 修正为你的实际包路径

import java.util.List;

public interface BGoodDao {
    BGood getGoodById(Integer id);
    Integer insertGood(BGood bGood);
    int updateGood(BGood bGood);
    List<BGood> list(Page page);
    int total(Page page);
    int deleteGood(int id);
    List<BGood> getGoodByTypeId(int id);
}