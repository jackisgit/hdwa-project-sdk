package com.hdwa.sdk.entity.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abao
 * @since 2023/8/2
 * 路径数据容器
 */
public class PathDataContainer {
    /**
     * <p>数据容器</p>
     * <p>projectId-----数据</p>
     */
    public static Map<String, RepositoryImpl> projectMap = new ConcurrentHashMap<>(16);
}
