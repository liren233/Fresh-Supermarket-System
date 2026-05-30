package com.xhm.main.dao;

import com.xhm.main.model.FAddress;
import com.xhm.main.model.Page;
import java.util.List;

public interface FAddressDao {
    FAddress getAddressById(int id);
    FAddress getAddressByUserId(int id);
    List<FAddress> list(Page page);
    int total(Page page);
    int insertAddress(FAddress fAddress);
    int updateAddress(FAddress fAddress);
    int deleteAddress(int id);
    List<FAddress> getAddressListByUserId(Integer userId);

    void setAllNotDefault(Integer userId);
    void setOneDefault(Integer id);
}