package com.boxai.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
 *
 */
public class SqlUtils {


    /**
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
