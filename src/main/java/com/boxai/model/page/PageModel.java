package com.boxai.model.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxai.common.constants.CommonConstant;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;

/**
 * 分页查询模型类
 *
 */
@Data
public class PageModel implements Serializable {

    /**
     * 页数
     */
    private Integer page;

    /**
     * 项数
     */
    private Integer size;

    /**
     * 是否允许深分页
     */
    private Boolean allowDeep;

    /**
     * 默认页数 第1页
     */
    private static final int DEFAULT_PAGE = 1;

    /**
     * 不允许深分页情况下默认项数（最大项数） 查询500条
     */
    private static final int DEFAULT_SIZE_NO_DEEP = 500;

    /**
     * 允许深分页情况下默认项数（最大项数） 查询全部
     */
    private static final int DEFAULT_SIZE_IN_DEEP = Integer.MAX_VALUE;

    /**
     * 默认不允许深分页
     */
    private static final boolean DEFAULT_ALLOW_DEEP = false;

    /**
     * 构建一个分页对象。
     * <p>此方法根据当前设置的允许深度（allowDeep）、页码（page）和大小（size）来构建一个分页对象。
     * 如果没有明确设置allowDeep，则会使用默认值来决定size的默认值。
     * 如果page的值小于等于0，则会自动调整为默认页码。
     * </p>
     *
     * @param <T> 分页对象的数据类型。
     * @return 返回一个配置好的Page对象，包含了页码和大小信息。
     */
    public <T> Page<T> build() {
        // 根据是否允许深挖（allowDeep），来确定size的默认值。
        Boolean allowDeep = ObjectUtils.defaultIfNull(getAllowDeep(), DEFAULT_ALLOW_DEEP);
        Integer page = ObjectUtils.defaultIfNull(getPage(), DEFAULT_PAGE);
        Integer size = allowDeep ?
                ObjectUtils.defaultIfNull(getSize(), DEFAULT_SIZE_IN_DEEP) : ObjectUtils.defaultIfNull(getSize(), DEFAULT_SIZE_NO_DEEP);

        // 确保页码大于0，否则使用默认页码
        if (page <= 0) {
            page = DEFAULT_PAGE;
        }

        // 创建并返回一个配置好的Page对象
        return new Page<T>(page, size);
    }
}
