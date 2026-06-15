package com.app.noobshop;

import com.app.noobshop.aop.annotation.common.ParamCheckAnnotation;
import com.app.noobshop.common.generator.NicknameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NoobshopApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(NicknameGenerator.generateDefaultNickname());
    }


    @ParamCheckAnnotation
    public void testParamCheck(Object object,String string,Integer integer){

    }
}
