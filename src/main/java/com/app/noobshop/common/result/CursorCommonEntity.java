package com.app.noobshop.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Schema(description = "通用游标查询封装类")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursorCommonEntity {

    @Schema(description = "查询种类")
    @NotNull
    private String sortType;

    @Schema(description = "末尾查询值")
    private String sortValue ;

    @Schema(description = "末尾查询 id")
    private Long sortId;

    @Schema(description = "查询数量")
    private Integer querySize = 20;
}
