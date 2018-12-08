package com.tyxxp.mytcc.common.util;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author tangyu
 */
public class CollectionUtil {

    /**
     * 转数组
     */
    @SuppressWarnings("unchecked")
    public static<T> T[] toArray(Collection<T> coll) {
        if(isNotEmpty(coll)) {
            return coll.toArray((T[]) new Object[coll.size()]);
        }
        return (T[])new Object[0];
    }

    /**
     * 从c1中移除c2中包含的元素
     */
    public static void substract(Collection c1, Collection c2) {
        if (isNotEmpty(c1) && isNotEmpty(c2)) {
            for (Object o : c2) {
                c1.remove(o);
            }
        }
    }

    /**
     * 是否为空集合
     */
    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 是否为非空集合
     */
    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

}
