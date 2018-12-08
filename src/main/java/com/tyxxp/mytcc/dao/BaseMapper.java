package com.tyxxp.mytcc.dao;

import com.tyxxp.mytcc.annotation.AutoIncrementId;
import com.tyxxp.mytcc.annotation.Serial;
import com.tyxxp.mytcc.common.Constant;
import com.tyxxp.mytcc.common.exception.MapperException;
import com.tyxxp.mytcc.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

/**
 * Mapper基类
 *
 * @author tangyu
 */
public abstract class BaseMapper {

    /**
     * 表字段数据类型与对象属性类型映射处理器
     */
    private static final Map<Class, TypeHandler> typeHandlers;

    static {
        Map<Class, TypeHandler> map = new HashMap<>();
        map.put(BigInteger.class, (TypeHandler<BigInteger, Long>) (p, rCls) -> p.longValue());
        map.put(Byte.class, (TypeHandler<Byte, Integer>) (p, rCls) -> (int) p);
        typeHandlers = Collections.unmodifiableMap(map);
    }

    interface TypeHandler<P, R> {
        R convert(P p, Class<R> rCls) throws MapperException;
    }

    /**
     * 数据源
     */
    protected DataSource tccDataSource;

    /**
     * 注入tcc数据源
     */
    @Autowired
    @Qualifier(Constant.DATA_SOURCE_BEAN_ID)
    public void setTccDataSource(DataSource tccDataSource) {
        this.tccDataSource = tccDataSource;
    }

    /**
     * sql执行
     */
    protected void execute(String sql, Object... params) throws MapperException {
        try (Connection connection = tccDataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            setParams(pstmt, params);
            pstmt.execute();
        } catch (SQLException e) {
            throw new MapperException(e);
        }
    }

    /**
     * 执行更新或插入操作
     */
    protected int executeUpdate(String sql, Object... params) throws MapperException {
        try (Connection connection = tccDataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            setParams(pstmt, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new MapperException(e);
        }
    }

    /**
     * 将对象属性插入到数据库中
     */
    protected int insert(boolean selective, boolean ignore, Object obj, String tableName) throws MapperException {
        try (Connection connection = tccDataSource.getConnection()) {
            // 将对象属性解析为insert sql
            StringBuilder cs = new StringBuilder();
            StringBuilder hs = new StringBuilder();
            List<Field> fields = new ArrayList<>();
            List<Object> colVals = new ArrayList<>();
            Field autoIncrementField = null;
            for (Field objField : obj.getClass().getDeclaredFields()) {
                if (objField.getAnnotation(AutoIncrementId.class) == null) {
                    boolean oriAccessible = objField.isAccessible();
                    objField.setAccessible(true);
                    Object val = objField.get(obj);
                    objField.setAccessible(oriAccessible);
                    if (!selective || val != null) {
                        fields.add(objField);
                        cs.append(",").append(StringUtil.humpToLine(objField.getName()));
                        hs.append(",").append("?");
                        colVals.add(val);
                    }
                } else {
                    autoIncrementField = objField;
                }
            }

            String sql = "insert" + (ignore ? " ignore" : "") + " into " + tableName + "(" + cs.toString().replaceFirst(",", "") + ") " +
                    "values (" + hs.toString().replaceFirst(",", "") + ")";
            PreparedStatement pstmt;
            if (autoIncrementField != null) {
                pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                pstmt = connection.prepareStatement(sql);
            }
            for (int i = 0; i < colVals.size(); i++) {
                Object val = colVals.get(i);
                Field field = fields.get(i);
                if (field.getAnnotation(Serial.class) == null) {
                    pstmt.setObject(i + 1, val);
                } else {
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                        oos.writeObject(val);
                        pstmt.setObject(i + 1, baos.toByteArray());
                    }
                }
            }
            int count = pstmt.executeUpdate();
            if (count == 1 && autoIncrementField != null) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    setField(obj, autoIncrementField, rs.getObject(1));
                }
            }
            return count;
        } catch (Exception e) {
            throw new MapperException(e);
        }
    }

    /**
     * 查询sql并将返回结果映射成指定对象
     */
    protected <T> List<T> select(Class<T> clazz, String selectSql, Object... params) throws MapperException {
        try (Connection connection = tccDataSource.getConnection()) {
            // 查询
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            setParams(pstmt, params);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            // 映射
            List<T> list = new ArrayList<>();
            if (clazz == String.class) {
                while (rs.next()) {
                    for (int i = 1; i <= colCount; i++) {
                        list.add(clazz.cast(rs.getString(i)));
                    }
                }
            } else {
                Field[] fields = clazz.getDeclaredFields();
                while (rs.next()) {
                    T obj = clazz.newInstance();
                    for (int i = 1; i <= colCount; i++) {
                        String colName = rsmd.getColumnLabel(i);
                        for (Field field : fields) {
                            if (Objects.equals(StringUtil.lineToHump(colName), field.getName())) {
                                setField(obj, field, rs.getObject(i));
                                break;
                            }
                        }
                    }
                    list.add(obj);
                }
            }

            return list;
        } catch (Exception e) {
            throw new MapperException(e);
        }
    }

    /**
     * 设置某个对象的属性值
     */
    @SuppressWarnings("unchecked")
    private static void setField(Object obj, Field field, Object val) throws MapperException {
        try {
            if (obj != null && field != null) {
                boolean oriAccessible = field.isAccessible();
                field.setAccessible(true);
                if (val != null) {
                    if (field.getAnnotation(Serial.class) == null) {
                        TypeHandler typeHandler = typeHandlers.get(val.getClass());
                        Class<?> fieldType = field.getType();
                        field.set(obj, typeHandler != null ? typeHandler.convert(val, fieldType) : val);
                    } else {
                        if (val instanceof byte[]) {
                            try (ObjectInputStream in = new ObjectInputStream(
                                    new ByteArrayInputStream((byte[]) val))) {
                                val = in.readObject();
                            }
                            field.set(obj, val);
                        } else {
                            throw new MapperException("无法反序列化");
                        }
                    }
                } else {
                    field.set(obj, null);
                }
                field.setAccessible(oriAccessible);
            }
        } catch (MapperException e) {
            throw e;
        } catch (Exception e) {
            throw new MapperException(e);
        }
    }

    /**
     * 设置占位符实际值
     */
    private void setParams(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }
}
