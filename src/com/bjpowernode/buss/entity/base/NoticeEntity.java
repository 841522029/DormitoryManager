package com.bjpowernode.buss.entity.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.bjpowernode.common.entity.base.BaseEntity;

/**
 * 
 * @desc 通知
 *
 * @author bjpowernode
 */
@Entity
@Table(name="T_B_NOTICE")
public class NoticeEntity extends BaseEntity{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 420646872754846899L;

	/**
	 * 标题
	 */
	@Column(length = 45)
	private String title;
	
	/**
	 * 内容
	 */
	@Column(length = 1024)
	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	
}