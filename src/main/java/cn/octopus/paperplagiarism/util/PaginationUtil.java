package cn.octopus.paperplagiarism.util;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtil {

    /**
     * 从列表中根据页码和每页大小获取数据。
     *
     * @param <T> 列表中元素的类型
     * @param list 待分页的列表
     * @param pageNumber 页码，从1开始
     * @param pageSize 每页显示的数据条数
     * @return 当前页的数据列表，如果页码无效则返回空列表
     */
    public static <T> List<T> getPageData(List<T> list, int pageNumber, int pageSize) {
        // 验证页码和页大小的有效性
        if (pageNumber <= 0 || pageSize <= 0 || list == null || list.isEmpty()) {
            return new ArrayList<>(); // 返回空列表以表示无效请求
        }

        // 计算当前页的起始索引（注意：Java中的List索引从0开始）
        int startIndex = (pageNumber - 1) * pageSize;

        // 计算当前页的结束索引，确保不会越界
        int endIndex = Math.min(startIndex + pageSize, list.size());

        // 检查起始索引是否已超出列表范围，如果是，则直接返回空列表
        if (startIndex >= list.size()) {
            return new ArrayList<>();
        }

        // 使用subList方法获取当前页的数据
        return list.subList(startIndex, endIndex);
    }

    /**
     * 计算给定列表大小和每页大小所需的总页数。
     *
     * @param totalSize 列表的总大小
     * @param pageSize  每页显示的数据条数
     * @return 所需的总页数
     */
    public static int calculateTotalPages(int totalSize, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        // 使用整数除法向上取整来计算总页数
        return (totalSize + pageSize - 1) / pageSize;
    }
}
