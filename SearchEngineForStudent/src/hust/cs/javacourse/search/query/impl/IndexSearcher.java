package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.*;

/**
 * AbstractIndexSearcher的具体实现类
 */
public class IndexSearcher extends AbstractIndexSearcher {

    @Override

    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     * @param indexFile ：指定索引文件
     */
    public void open(String indexFile) {
        this.index.load(new File(indexFile));
        this.index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     * @param queryTerm ：检索词
     * @param sorter ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        AbstractPostingList postingList = index.search(queryTerm);
        if(postingList == null)
            return new AbstractHit[0];
        List<AbstractHit> hitList = new ArrayList<>();
        for(int i = 0; i < postingList.size(); i++){
            AbstractPosting posting = postingList.get(i);
            Map<AbstractTerm,AbstractPosting> map = new TreeMap<>();
            map.put(queryTerm, posting);
            int docId = posting.getDocId();
            AbstractHit hit = new Hit(docId, index.getDocName(docId), map);
            hit.setScore(sorter.score(hit));
            hitList.add(hit);
        }
        sorter.sort(hitList);
        return hitList.toArray(new AbstractHit[0]);
    }

    /**
     *
     * 根据二个检索词进行搜索
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter ：    排序器
     * @param combine ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        AbstractPostingList postingList1 = index.search(queryTerm1);
        AbstractPostingList postingList2 = index.search(queryTerm2);
        List<AbstractHit> hitList = new ArrayList<>();

        if(combine == LogicalCombination.OR){
            int i = 0, j = 0;
            while(i < postingList1.size() && j < postingList2.size()){
                AbstractPosting posting1 = postingList1.get(i);
                AbstractPosting posting2 = postingList2.get(j);
                Map<AbstractTerm,AbstractPosting> map = new TreeMap<>();
                AbstractPosting posting;
                if(posting1.getDocId() == posting2.getDocId()){
                    posting = posting1;
                    map.put(queryTerm1, posting1);
                    map.put(queryTerm2, posting2);
                    i++;
                    j++;
                }
                else if(posting1.getDocId() < posting2.getDocId()){
                    posting = posting1;
                    map.put(queryTerm1, posting1);
                    i++;
                }
                else {
                    posting = posting2;
                    map.put(queryTerm2, posting2);
                    j++;
                }
                int docId = posting.getDocId();
                AbstractHit hit = new Hit(docId, index.getDocName(docId), map);
                hit.setScore(sorter.score(hit));
                hitList.add(hit);
            }
            while(i < postingList1.size()){
                AbstractPosting posting = postingList1.get(i);
                Map<AbstractTerm,AbstractPosting> map=new TreeMap<>();
                map.put(queryTerm1,posting);
                AbstractHit hit=new Hit(posting.getDocId(),index.getDocName(posting.getDocId()),map);
                hit.setScore(sorter.score(hit));
                hitList.add(hit);
                i++;
            }
            while(j < postingList2.size()){
                AbstractPosting posting =postingList2.get(j);
                Map<AbstractTerm,AbstractPosting> map=new TreeMap<>();
                map.put(queryTerm2,posting);
                AbstractHit hit=new Hit(posting.getDocId(),index.getDocName(posting.getDocId()),map);
                hit.setScore(sorter.score(hit));
                hitList.add(hit);
                j++;
            }

        }
        else { // AND
            int i = 0, j = 0;
            while(i < postingList1.size() && j < postingList2.size()){
                AbstractPosting posting1 = postingList1.get(i);
                AbstractPosting posting2 = postingList2.get(j);
                if(posting1.getDocId() == posting2.getDocId()){
                    Map<AbstractTerm,AbstractPosting> map = new TreeMap<>();
                    map.put(queryTerm1, posting1);
                    map.put(queryTerm2, posting2);
                    AbstractHit hit=new Hit(posting1.getDocId(),index.getDocName(posting1.getDocId()),map);
                    hit.setScore(sorter.score(hit));
                    hitList.add(hit);
                    i++;
                    j++;
                }
                else if(posting1.getDocId() < posting2.getDocId())
                    i++;
                else j++;
            }

        }
        sorter.sort(hitList);
        return hitList.toArray(new AbstractHit[0]);
    }

    public AbstractHit[] mysearch(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter){
        AbstractPostingList postingList1 = index.search(queryTerm1);
        AbstractPostingList postingList2 = index.search(queryTerm2);
        List<AbstractHit> hitList = new ArrayList<>();
        int i = 0, j = 0;
        while(i < postingList1.size() && j < postingList2.size()){
            AbstractPosting posting1 = postingList1.get(i);
            AbstractPosting posting2 = postingList2.get(j);
            if(posting1.getDocId() == posting2.getDocId()){
                Collections.sort(posting1.getPositions());
                Collections.sort(posting2.getPositions());
                int flag = 0;
                for(int p  : posting1.getPositions()){
                    for(int q : posting2.getPositions()){
                        if(p+1 == q){
                            Map<AbstractTerm,AbstractPosting> map = new TreeMap<>();
                            map.put(queryTerm1,posting1);
                            map.put(queryTerm2, posting2);
                            AbstractHit hit=new Hit(posting1.getDocId(),index.getDocName(posting1.getDocId()),map);
                            hit.setScore(sorter.score(hit));
                            hitList.add(hit);
                            i++;
                            j++;
                            flag = 1;
                            break;
                        }
                    }
                    if(flag == 1){
                        break;
                    }
                }
                if(flag == 0){
                    i++;
                    j++;
                }
            }
            else if(posting1.getDocId() < posting2.getDocId())
                i++;
            else j++;
        }


        sorter.sort(hitList);
        return hitList.toArray(new AbstractHit[0]);
    }

}
