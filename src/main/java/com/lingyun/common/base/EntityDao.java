package com.lingyun.common.base;

import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface EntityDao <E>{
    public String saveFile(String fileName,byte[] file);
    public String saveFile(String fileName,File file) throws IOException;
    public GridFSDBFile findFileById(String id);
    void deleteFile(String id);
    void insert(E e);
    List<E> findEquals(E e);
    List<E> findAll();
    List<E> findAll(Integer limit);
    List<E> findAll(DBObject condition);
    E findOne(DBObject queryCondition);
    E findOne(E condition);
    public List<E> textQuery(String  keyWord);
    List<E> findRange(String key, Object min, Object max);
    E findById(ObjectId id);
    List<E> findNotEquals(E e);
    List<E> findKeyIn(String key, Collection collection);

    List<E> findKeyNotIn(String key, Collection collection);

    List<E> findKeyIsNull(String key);

    List<E> findByRegex(String key, String regex);

    List<E> findInArray(String key, Object[] values);

    List<E> findRef(String refKey, Object value);

    Page<E> findPage(int pageIndex);

    void upsert(E e);

    CommandResult runCommand(String s);

    E findById(String id);

    void removeById(String id);

    List<E> findAll(Query query);

    void removeAll(List<E> list);

    long count(DBObject dbObject);

    Page<E> findPage(DBObject dbObject, Integer page);
    Page<E> findPage(DBObject condition,int currentPage,int pageSize);
    Page<E> findPage(DBObject dbObject, Integer currentPage, Integer pageSize, String sortField, Boolean asc);
}
