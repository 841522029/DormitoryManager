package com.bjpowernode.buss.service;

import org.hibernate.criterion.DetachedCriteria;

import com.bjpowernode.buss.entity.base.NoticeEntity;
import com.bjpowernode.common.util.Pagination;
import com.bjpowernode.system.service.SystemService;

public interface NoticeService extends SystemService {
	
	/**
	 * 根据传入数据进行查询
	 * @param condition
	 * @param ce
	 * @param page
	 * @param rows
	 * @return
	 */
	public Pagination<NoticeEntity> findPageData(DetachedCriteria condition,
			NoticeEntity ne, int page, int rows);
}
