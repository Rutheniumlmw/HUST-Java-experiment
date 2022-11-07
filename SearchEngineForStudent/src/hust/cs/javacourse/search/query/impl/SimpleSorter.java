package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Sort的具体实现类
 */
public class SimpleSorter implements Sort {

    /**
     * 对命中结果集合根据文档得分排序
     * @param hits ：命中结果集合
     */
    @Override
    public void sort(List<AbstractHit> hits) {
        Collections.sort(hits);
    }

    /**
     * <pre>
     * 计算命中文档的得分, 作为命中结果排序的依据.
     * @param hit ：命中文档
     * @return ：命中文档的得分
     * </pre>
     */
    @Override
    public double score(AbstractHit hit) {
        double ansScore = 0;
        Map<AbstractTerm, AbstractPosting> mp = hit.getTermPostingMapping();
        for(AbstractPosting posting:mp.values()){
            ansScore -= posting.getFreq();
        }
        return ansScore;
    }
}
