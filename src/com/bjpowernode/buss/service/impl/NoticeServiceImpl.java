package com.bjpowernode.buss.service.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjpowernode.buss.entity.base.NoticeEntity;
import com.bjpowernode.buss.service.NoticeService;
import com.bjpowernode.common.dao.BaseDao;
import com.bjpowernode.common.util.Pagination;
import com.bjpowernode.system.service.impl.SystemServiceImpl;

@Service("noticeService")
public class NoticeServiceImpl extends SystemServiceImpl implements NoticeService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public Pagination<NoticeEntity> findPageData(DetachedCriteria condition,
			NoticeEntity ne, int page, int rows) {
		Pagination<NoticeEntity> pagination = new Pagination<NoticeEntity>(page, rows);
		
		if(ne.getTitle() != null && !"".equals(ne.getTitle())){
			condition.add(Restrictions.like("title", "%"+ne.getTitle()+"%"));
		}
		if(ne.getContent() != null && !"".equals(ne.getContent())){
			condition.add(Restrictions.like("content", "%"+ne.getContent()+"%"));
		}
		condition.addOrder(Order.desc("createTime"));
		int total = this.baseDao.getRowCountByDetachedCriteria(condition);
		pagination.setTotalCount(total);
		condition.setProjection(null);
		if (total != 0) {
			List<NoticeEntity> datas = baseDao.findByDetachedCriteria(condition, page, rows);
			pagination.setDatas(datas);
		}
		return pagination;
	}

}
