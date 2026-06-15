package com.app.noobshop.application.es.init;

import com.app.noobshop.application.common.EsClientUtil;
import com.app.noobshop.infrastructure.es.index.EsIndexInitializerService.EsIndexInitializerService;
import com.app.noobshop.infrastructure.es.index.EsIndexInitializerService.impl.EsIndexInitializerServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsIndexInitializer {

    public static void main(String[] args) {
        try (EsClientUtil.EsClientResource esClientResource = EsClientUtil.openEsClient()) {
            EsIndexInitializerService initializerService = new EsIndexInitializerServiceImpl(esClientResource.client());
            initializerService.initProductIndex();
        } catch (Exception e) {
            log.error("ES index initialization failed", e);
        }
    }
}
