package com.lingyun.common.base;

import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
public abstract class BaseEntityManager<E> implements IBaseEntityManager<E> {

//	private Log log = LogFactory.getLog(getClass());

    protected abstract EntityDao<E> getEntityDao();

    public void insert(E e) {
        getEntityDao().insert(e);
    }

    @Override
    public String saveFile(String fileName, byte[] file) {
        return getEntityDao().saveFile(fileName, file);
    }
    @Override
    public String saveFile(String fileName, File file) throws IOException {
        return getEntityDao().saveFile(fileName, file);
    }
    @Override
    public void deleteFile(String id){
        getEntityDao().deleteFile(id);
    }
    @Override
    public GridFSDBFile findFileById(String id) {
        return getEntityDao().findFileById(id);
    }

    @Override
    public List<E> findEquals(E e) {
        return getEntityDao().findEquals(e);
    }
    @Override
    public List<E> findAll(DBObject condition){
        return getEntityDao().findAll(condition);
    }
    @Override
    public E findById(String id) {
        return getEntityDao().findById(id);
    }
    @Override
    public void updateByIds(String[] ids,String field,Object value) {
        getEntityDao().updateByIds(ids,field,value);
    }
    @Override
    public void updateByIds(List<String> ids,String field,Object value) {
        getEntityDao().updateByIds(ids,field,value);
    }
    @Override
    public List<E> findAll() {
        return getEntityDao().findAll();
    }
    @Override
    public List<E> findAll(Integer limit){
        return getEntityDao().findAll(limit);
    }

//    @Override
//    public int upsert(E queryEntity, E updateEntity) {
//        return getEntityDao().upsert(queryEntity, updateEntity);
//    }

    @Override
    public E findById(ObjectId id) {
        return getEntityDao().findById(id);
    }
    @Override
    public E findOne(E condition){ return getEntityDao().findOne(condition);}
    @Override
    public List<E> findRange(String key, Object min, Object max) {
        return getEntityDao().findRange(key, min, max);
    }

    @Override
    public List<E> findNotEquals(E e) {
        return getEntityDao().findNotEquals(e);
    }
    @Override
    public  List<E> textQuery(String  keyWord){
        return getEntityDao().textQuery(keyWord);
    }
    @Override
    public List<E> findKeyIn(String key, Collection collection) {
        return getEntityDao().findKeyIn(key, collection);
    }

    @Override
    public List<E> findKeyNotIn(String key, Collection collection) {
        return getEntityDao().findKeyNotIn(key, collection);
    }

    @Override
    public List<E> findKeyIsNull(String key) {
        return getEntityDao().findKeyIsNull(key);
    }

    @Override
    public List<E> findByRegex(String key, String regex) {
        return getEntityDao().findByRegex(key, regex);
    }

    @Override
    public List<E> findInArray(String key, Object[] values) {
        return getEntityDao().findInArray(key, values);
    }

    @Override
    public List<E> findRef(String refKey, Object value) {
        return getEntityDao().findRef(refKey, value);
    }

    @Override
    public Page<E> findPage(int pageIndex) {
        return getEntityDao().findPage(pageIndex);
    }

    @Override
    public void update(E e) {
        getEntityDao().upsert(e);
    }

    @Override
    public CommandResult runCommand(String s) {

        return getEntityDao().runCommand(s);
    }
    @Override
    public E findOne(DBObject condition){
        return getEntityDao().findOne(condition);
    }

    @Override
    public void  removeById(String id){
        getEntityDao().removeById(id);
    }
    @Override
    public List<E> findAll(Query query){
        return getEntityDao().findAll(query);
    }
    @Override
    public void removeAll(){
        getEntityDao().removeAll();
    }
    @Override
    public void removeAll(List<E> list){
        getEntityDao().removeAll(list);
    }
    public void insertAll(List<E> list){
        getEntityDao().insertAll(list);
    }
    public long count(DBObject dbObject){
        return getEntityDao().count(dbObject);
    }

    @Override
    public Page<E> findPage(DBObject dbObject, Integer page) {
        return getEntityDao().findPage(dbObject, page);
    }
    @Override
    public Page<E> findPage(DBObject condition,int currentPage,int pageSize){
        return getEntityDao().findPage(condition,currentPage,pageSize);
    }
    public Page<E> findPage(DBObject dbObject, Integer currentPage, int pageSize, String sortField, boolean asc){
        return getEntityDao().findPage(dbObject,currentPage,pageSize,sortField,asc);
    }
    public E getMax(String field, String fieldQuery, Object fieldQueryValue){
        return getEntityDao().getMax(field, fieldQuery, fieldQueryValue);
    }
}
