package org.opengoofy.assault.framework.starter.database;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.opengoofy.assault.framework.starter.common.toolkit.BeanUtil;
import org.opengoofy.assault.framework.starter.convention.page.PageRequest;
import org.opengoofy.assault.framework.starter.convention.page.PageResponse;

/**
 * 分页工具类
 */
public class PageUtil {
    
    /**
     * {@link PageRequest} to {@link Page}
     *
     * @param pageRequest
     * @return
     */
    public static Page convert(PageRequest pageRequest) {
        return convert(pageRequest.getCurrent(), pageRequest.getSize());
    }
    
    /**
     * {@link PageRequest} to {@link Page}
     *
     * @param current
     * @param size
     * @return
     */
    public static Page convert(long current, long size) {
        return new Page(current, size);
    }
    
    /**
     * {@link IPage} to {@link PageRequest}
     *
     * @param iPage
     * @return
     */
    public static PageResponse convert(IPage iPage) {
        return buildConventionPage(iPage);
    }
    
    /**
     * {@link IPage} to {@link PageRequest}
     *
     * @param iPage
     * @param targetClass
     * @param <TARGET>
     * @param <ORIGINAL>
     * @return
     */
    public static <TARGET, ORIGINAL> PageResponse<TARGET> convert(IPage<ORIGINAL> iPage, Class<TARGET> targetClass) {
        iPage.convert(each -> BeanUtil.convert(each, targetClass));
        return buildConventionPage(iPage);
    }
    
    /**
     * {@link IPage} build to {@link PageRequest}
     *
     * @param iPage
     * @return
     */
    private static PageResponse buildConventionPage(IPage iPage) {
        return PageResponse.builder().current(iPage.getCurrent()).size(iPage.getSize()).records(iPage.getRecords()).total(iPage.getTotal()).build();
    }
}
