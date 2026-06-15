package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.AddressDTO;
import com.app.noobshop.pojo.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;

public interface AddressService extends IService<Address> {
    Result getAddressList();

    Result updateAddress(@Valid AddressDTO addressDTO);

    Result insertAddress(@Valid AddressDTO addressDTO);

    Result deleteAddress(String id);
}
